package com.theice.mdf.client.domain.book;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.PriceLevel;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.domain.transaction.Transaction;
import com.theice.mdf.client.exception.InconsistentStateException;
import com.theice.mdf.client.exception.ProcessingException;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.CrossedBookMonitor;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract Book implementation
 *
 * Detecting Crossed Book Scenarios - While processing orders, existence of a crossed book can be detected
 * by a simple check to see if bid>offer in the top of book. When transactions are processed in isolation, this can
 * occur momentarily. But if this condition lasts longer than a threshold (such as 1 second), an alert can be
 * generated
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractBook implements Book
{
    private static final Logger logger=Logger.getLogger(AbstractBook.class.getName());

    /**
     * This flag indicates whether the market's book has changed
     * We update this flag when an order gets added/modified or deleted. Consumers of the market order
     * information (typically the GUI models) will use this flag to determine if a refresh is needed for the
     * display. If this flag is true, a refresh is performed followed by a reset of this flag back to false.
     */
    protected AtomicBoolean _bookUpdated=new AtomicBoolean(false);

    /**
     * Boolean indicating if the book is currently crossed
     * Start/End timestamps of the crossed book condition
     */
    protected AtomicBoolean isCrossedBook=new AtomicBoolean(false);
    protected CrossedBookInfo crossedBook=null;
    protected int _marketID=0; //only needed in Top5PL crossed book detection reporting
    
    protected AbstractBook()
    {
    }
    
    /**
     * Reset the book updated flag to false, if the current value is true
     * @return true if the operation was successful
     */
    public boolean resetBookUpdatedIfTrue()
    {
        return(_bookUpdated.compareAndSet(true, false));
    }

    /**
     * Base implementations that throw UnsupportedOperationException
     */
    public void processAddOrder(AddModifyTransaction transaction) throws UnsupportedOperationException
    {
    	throw(new UnsupportedOperationException());
    }
    
    /**
     * process remove order
     * @param marketOrder, the order that has to be removed from the book
     * @param triggeringTransaction, the transaction that triggered this removal, such as a Modify Order that might replace this
     * 			order after removal
     * @return
     * @throws UnsupportedOperationException
     */
    public MarketOrder processRemoveOrder(MarketOrder removedOrder,Transaction triggeringTransaction) throws UnsupportedOperationException
    {
    	throw(new UnsupportedOperationException());
    }

    public abstract Collection getBids();
    
    public abstract Collection getOffers();

    /**
     * Base implementations for Price Level Operations
     */
    public void addPriceLevel(PriceLevel priceLevel) throws UnsupportedOperationException, ProcessingException
    {
    	throw(new UnsupportedOperationException());
    }

    public void changePriceLevel(PriceLevel priceLevel) throws UnsupportedOperationException, ProcessingException
    {
    	throw(new UnsupportedOperationException());
    }
    
    public PriceLevel removePriceLevel(byte priceLevelPosition, char side)
    	throws UnsupportedOperationException, ProcessingException
    {
    	throw(new UnsupportedOperationException());
    }
    
    public boolean isCrossed()
    {
    	return(isCrossedBook.get());
    }
    
    /**
     * set the book as crossed, if not already crossed
     * bid>offer
     * @param CrossedBookInfo
     * @return true if the crossed condition started. false if the condition already exists
     */
    protected boolean setCrossed(CrossedBookInfo crossedBookInfo)
    {
    	boolean crossed=isCrossedBook.compareAndSet(false, true);
    	
    	if(crossed)
    	{
        	if(logger.isTraceEnabled())
        	{
            	logger.trace("Setting the book as CROSSED : "+crossedBookInfo.toString());
        	}
        	
        	this.crossedBook=crossedBookInfo;
    	}
    	
    	return(crossed);
    }
    
    /**
     * if the book is currently crossed, reset the flag
     * @param clearedByOrder
     * @return true if a condition just got cleared, false if the condition never existed
     */
    protected CrossedBookInfo clearCrossed(Transaction clearedByTransaction)
    {
    	CrossedBookInfo clearedCrossedBookInfo=null;
    	boolean cleared=isCrossedBook.compareAndSet(true, false);
    	
    	if(cleared)
    	{
        	if(logger.isTraceEnabled())
        	{
            	logger.trace("Clearing the book from Crossed status for Market="+clearedByTransaction.getMarketId());
        	}

        	if(this.crossedBook!=null)
        	{
            	this.crossedBook.setEndTime(System.currentTimeMillis());
            	this.crossedBook.setClearedBy(clearedByTransaction);
            	
            	clearedCrossedBookInfo=this.crossedBook;
            	
            	/**
            	 * Clear the internal crossed book info
            	 */
            	this.crossedBook=null;
        	}

    	}
    	
    	return(clearedCrossedBookInfo);
    }
    
    public CrossedBookInfo getCrossedBookInfo()
    {
    	return(this.crossedBook);
    }
    
    /**
     * check crossed status and set/reset the flag accordingly
     * @param marketId
     * @param LiteOrderInfo, the triggeringOrderInfo
     * @param topBid
     * @param topOffer
     */
    protected void checkCrossedStatus(int marketId, Transaction triggeringTransaction, Long topBid, Long topOffer)
    {
    	if(topBid.longValue()<topOffer.longValue())
    	{
    		CrossedBookInfo clearedCrossedBookInfo=clearCrossed(triggeringTransaction);
    		
    		if(clearedCrossedBookInfo!=null)
    		{
            	if(logger.isTraceEnabled())
            	{
            		StringBuffer buf=new StringBuffer("### Crossed Book Cleared : ").append(marketId);
            		buf.append(" - ").append(clearedCrossedBookInfo.toString());
            		buf.append(" ### Top Bid: [").append(topBid).append("] ");
            		buf.append(" ### Top Offer : [").append(topOffer).append("]");
            		logger.trace(buf.toString());
            	}

            	raiseCrossBookAlerts(clearedCrossedBookInfo);
    		}
    	}
    	else
    	{
    		MarketInterface market=MarketsHolder.getInstance().findMarket(marketId);
    		
    		CrossedBookInfo crossedBookInfo=new CrossedBookInfo(triggeringTransaction,market.getMarketDesc(),topBid,topOffer);

    		if(setCrossed(crossedBookInfo))
        	{
        		StringBuffer buf=new StringBuffer();
        		
        		String allBids="";
        		String allOffers="";
        		
        		allBids=this.getBids().toString();
        		allOffers=this.getOffers().toString();
        		
        		crossedBookInfo.setAllBidDetailsAtOccurrence(allBids);
        		crossedBookInfo.setAllOfferDetailsAtOccurrence(allOffers);
        		
        		buf.append("### ").append(marketId);
        		buf.append(" # ").append(crossedBookInfo.toString());
        		buf.append(" ### Current Top Bid: [").append(topBid).append("] ");
        		buf.append(" ### Current Top Offer : [").append(topOffer).append("]");
        		buf.append(MDFUtil.linefeed);
        		buf.append(" ### Bids : [").append(crossedBookInfo.getAllBidDetailsAtOccurrence()).append("]");
        		buf.append(MDFUtil.linefeed);
        		buf.append(" ### Offers : [").append(crossedBookInfo.getAllOfferDetailsAtOccurrence()).append("]");
        		
        		if(crossedBookInfo.causedByABundleMessage())
        		{
        			StringBuffer message=new StringBuffer("### Crossed Book Found Inside a Bundle: ").append(buf.toString());
           		logger.warn(message.toString());
        		}
        		else
        		{
        			StringBuffer message=new StringBuffer("### Crossed Book Found Outside a Bundle: ").append(buf.toString());
            	logger.error(message.toString());
        			AppManager.writeAlert(Level.ERROR,message.toString());
        		}

        		/**
        		 * If this market has been marked for delayed monitoring (such as OTC), then register this
        		 * crossed book information with the CrossedBookAlertManager
        		 */
        		short marketType=market.getMarketType();
        		        		
        		if(CrossedBookMonitor.getInstance().isRegisteredForDelayedMonitoring(marketType))
        		{
        			CrossedBookMonitor.getInstance().registerCrossedBookOccurrence(crossedBookInfo);
        		}
        	}
        	else
        	{
        		StringBuffer buf=new StringBuffer("### Crossed Book condition already exists : ").append(marketId);
        		buf.append(" # ").append(getCrossedBookInfo().toString());
        		buf.append(" ## New Triggering Condition: [").append(crossedBookInfo.toString()).append("] ");
        		buf.append(" ### Current Top Bid: [").append(topBid).append("] ");
        		buf.append(" ### Current Top Offer : [").append(topOffer).append("]");
        		logger.warn(buf.toString());
        	}
    	}
    	
    	return;
    }

    /**
     * Check how the crossed book was cleared and raise appropriate alerts
     * If the market type has been registered for delayed alerts, then report to the monitor
     * @param clearedCrossedBookInfo
     */
    protected void raiseCrossBookAlerts(CrossedBookInfo clearedCrossedBookInfo)
    {
    	try
    	{
    		if(clearedCrossedBookInfo.hasClearedInsideABundle())
    		{
    			StringBuffer buf=new StringBuffer("### CrossedBook Cleared Inside Bundles (NORMAL) ### ");
    			buf.append(clearedCrossedBookInfo.toString());
    			logger.warn(buf.toString());
    		}
    		else
    		{
    			StringBuffer buf=new StringBuffer("### CrossedBook Cleared Outside Bundles ### ");
    			buf.append(clearedCrossedBookInfo.toString());
    			logger.error(buf.toString());
    			AppManager.writeAlert(Level.ERROR,buf.toString());
    		}
    		
    		/**
    		 * If this market has been marked for delayed monitoring (such as OTC), then clear the crossed book
    		 * inside the CrossedBookAlertManager
    		 */
    		MarketInterface market=MarketsHolder.getInstance().findMarket(clearedCrossedBookInfo.getMarketId());
    		short marketType=market.getMarketType();
    		    		
    		if(CrossedBookMonitor.getInstance().isRegisteredForDelayedMonitoring(marketType))
    		{
    			System.out.println("Market type registered for crossed book monitoring : "+marketType);
    			CrossedBookMonitor.getInstance().clearCrossedBook(clearedCrossedBookInfo.getMarketId());
    		}
    	}
    	catch(InconsistentStateException e)
    	{
    	}
    	
    	return;
    }
    
    public int getMarketID()
    {
       return this._marketID;
    }
    
    public void setMarketID(int marketId)
    {
       this._marketID=marketId;
    }

}

