package com.theice.mdf.client.domain.book;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.PriceLevel;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.domain.transaction.Transaction;
import com.theice.mdf.client.domain.transaction.TransactionType;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * FullOrderBook that holds all the individual orders
 * 
 * In the Full Order Depth implementation, the price levels are calculated using the orders as they
 * are added/modified or deleted
 * 
 * While processing add/remove orders, check to see if a crossed book condition exists. If it does,
 * set the flag and update the time stamp so that the situation can be monitored for alerts
 * 
 * @see Market and the FullOrderDepth Multicaster and related processing
 * 
 * @author Adam Athimuthu
 */
public class FullOrderBook extends AbstractBook 
{
    private static final Logger logger=Logger.getLogger(FullOrderBook.class.getName());
    
    /**
     * The Book
     * bids and offers - Collection of MarketOrder objects
     * 
     * Source for regular markets: Market Snapshot Order (D)
     * Updates: Add/Modify (E), Delete (F), Trade (G)
     * Updates (option markets): Add/Modify Option Order (V), Delete (F), Option Trade (W)
     */
    protected SortedSet<MarketOrder> _bids=new TreeSet<MarketOrder>();
    protected SortedSet<MarketOrder> _offers=new TreeSet<MarketOrder>();

    /**
     * Map of price levels for bids and offers
     */
    protected TreeMap<Long, PriceLevel> _bidPriceLevels=new TreeMap<Long, PriceLevel>();
    protected TreeMap<Long, PriceLevel> _offerPriceLevels=new TreeMap<Long, PriceLevel>();

    /**
     * Cross book detection, if active, can only be applicable for FullOrderBooks.
     */
    protected boolean detectCrossedBook=false;

	public FullOrderBook()
	{
		super();
		
	    detectCrossedBook=AppManager.getAppContext().isCrossBookDetectionEnabled();
	}

	/**
	 * Get Context
	 * @return
	 */
	public BookContext getContext()
	{
		return(BookContext.FULLORDERDEPTH);
	}
	
    /**
     * override base
     * Update book and adjust price levels
     * Check for crossed status
     * @param AddModify transaction containing the marketOrder
     */
    public void processAddOrder(AddModifyTransaction transaction)
    {
    	SortedSet<MarketOrder> book=null;
        Map<Long,PriceLevel> priceLevels=null;
        PriceLevel priceLevel=null;
        
        MarketOrder marketOrder=transaction.getOrder();
        
        /**
         * Choose the collections to operate on, based on the Side
         */
        if(marketOrder.isBuy())
        {
            book=_bids;
            priceLevels=_bidPriceLevels;
        }
        else
        {
            book=_offers;
            priceLevels=_offerPriceLevels;
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("Adding bid/offer : "+marketOrder.getPrice()+"/"+marketOrder.getQuantity());
        }

        /**
         * Add the order and update the price level
         * Update the number of orders that make up this price level
         * Also, if this is an implied order, increment the implied count/qty
         */
        book.add(marketOrder);

        priceLevel=(PriceLevel) priceLevels.get(marketOrder.getPrice());

        if(priceLevel!=null)
        {
            priceLevel.increase(marketOrder.getQuantity());
            priceLevel.incrementOrderCount();
            
            if(marketOrder.isImplied())
            {
            	priceLevel.incrementImpliedOrderCount();
            	priceLevel.increaseImpliedQuantity(marketOrder.getQuantity());
            }
        }
        else
        {
            priceLevel=new PriceLevel(marketOrder);
            priceLevels.put(marketOrder.getPrice(), priceLevel);
        }
        
        _bookUpdated.compareAndSet(false, true);
        
        /**
         * Check for crossed status
         */
        if(detectCrossedBook)
        {
            determineCrossedStatus(marketOrder,transaction);
        }

        return;
    }
    
    /**
     * process remove order
     * Update book and adjust price levels
     * 
     * Check for crossed status and update crossed book status, ONLY if the proposedNewOrder is known
     * If the proposed new order is null
     * 
     * @param marketOrder, the order that has to be removed from the book
     * @param triggeringOrder, the order that triggered this removal, such as a Modify Order that will replace this
     * 			order after removal. If the triggering order is Delete/Trade, it indicates that the removal was standalone.
     * @return
     * @throws UnsupportedOperationException
     */
    public MarketOrder processRemoveOrder(MarketOrder removedOrder,Transaction triggeringTransaction) throws UnsupportedOperationException
    {
        PriceLevel priceLevel=null;

        Long price=removedOrder.getPrice();

        SortedSet<MarketOrder> book=null;
        Map<Long,PriceLevel> priceLevels=null;
        
        /**
         * Choose the collections to operate on, based on the Side
         */
        if(removedOrder.isBuy())
        {
            book=_bids;
            priceLevels=_bidPriceLevels;
        }
        else
        {
            book=_offers;
            priceLevels=_offerPriceLevels;
        }

        book.remove(removedOrder);

        priceLevel=(PriceLevel) priceLevels.get(price);

        /**
         * Process remove order and update the price level accordingly
         * Update the number of orders
         * Also, if this is an implied order, decrement the implied count/qty
         */
        if(priceLevel.getQuantity()<=removedOrder.getQuantity())
        {
            priceLevels.remove(price);
        }
        else
        {
            priceLevel.decrease(removedOrder.getQuantity());
            priceLevel.decrementOrderCount();
            
            if(removedOrder.isImplied())
            {
            	priceLevel.decrementImpliedOrderCount();
            	priceLevel.decreaseImpliedQuantity(removedOrder.getQuantity());
            }
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("Removed Order : "+removedOrder.getPrice()+"/"+removedOrder.getQuantity());
        }

        _bookUpdated.compareAndSet(false, true);
        
        /**
         * Check for crossed status during the following scenarios
         * 1. Pure Delete or triggered by a Trade (within the same bundle)
         * 2. Pure Delete or triggered by a Trade (outside the bundle)
         * 3. delete triggered by a Modify Order (same bundle)
         * 4. delete triggered by a Modify Order (outside the bundle)
         * 
         * For scenarios #3 and #4, no need to check the crossed status check, as we know that it will be
         * done by the triggering order (such as a pending order that is waiting to trigger a Modify)
         */
        if(detectCrossedBook)
        {
            if(triggeringTransaction!=null)
            {
            	if(triggeringTransaction.getTransactionType()!=TransactionType.ADDMODIFY)
            	{
                    determineCrossedStatus(removedOrder,triggeringTransaction);
            	}
            }
        }

        return(removedOrder);
        
    }
    
    /**
     * Get all the bids (override)
     * @return
     */
    public Collection<MarketOrder> getBids() throws UnsupportedOperationException
    {
        return(_bids);
    }

    /**
     * Get all the offers (override)
     * @return
     */
    public Collection<MarketOrder> getOffers() throws UnsupportedOperationException
    {
        return(_offers);
    }

    /**
     * get the price level array in the "natural" order sorted based on the side
     * @param side
     */
    public PriceLevel[] getPriceLevels(char side)
    {
    	int length=0;
    	
    	PriceLevel[] priceLevels=new PriceLevel[0];
    	
    	if(MDFUtil.isBuy(side))
    	{
            length=_bidPriceLevels.size();

            if(length>0)
            {
                priceLevels=new PriceLevel[length];

                int index=length;

                for(Iterator<PriceLevel> it=_bidPriceLevels.values().iterator();it.hasNext();)
                {
                    priceLevels[--index]=new PriceLevel((PriceLevel) it.next());
                }
            }
            else
            {
                priceLevels=new PriceLevel[0];
            }
    	}
    	else
    	{
            length=_offerPriceLevels.size();
            
            if(length>0)
            {
                priceLevels=new PriceLevel[length];

                int index=0;

                for(Iterator<PriceLevel> it=_offerPriceLevels.values().iterator();it.hasNext();)
                {
                    priceLevels[index++]=new PriceLevel((PriceLevel) it.next());
                }
            	
            }
            else
            {
                priceLevels=new PriceLevel[0];
            }
    	}
    	
    	return(priceLevels);
    }
    
    /**
     * initialize
     */
    public void initialize()
    {
    	logger.info("Initialize...");
        _bids.clear();
        _offers.clear();
        _bidPriceLevels.clear();
        _offerPriceLevels.clear();
        _bookUpdated.compareAndSet(false, true);
        this.clearCrossed(null);
    }
    
    /**
     * determine crossed status (bid>=offer) and set or reset
     * the flag accordingly
     * 
     * Check if this order was part of the bundle and mark accordingly
     * TODO check if we need to sync with a mutex for crossed book updates
     * @param MarketOrder
     * @param the triggering transaction information
     */
    private void determineCrossedStatus(MarketOrder marketOrder,Transaction triggeringTransaction)
    {
    	int numberOfBids=_bidPriceLevels.size();
    	int numberOfOffers=_offerPriceLevels.size();
    	
    	if(numberOfBids==0 || numberOfOffers==0)
    	{
    		/**
    		 * Clear the crossed status if one side of the book is empty (can happen due to removal)
    		 */
    		CrossedBookInfo clearedCrossedBookInfo=clearCrossed(triggeringTransaction);
    		
    		if(clearedCrossedBookInfo!=null)
    		{
            	if(logger.isTraceEnabled())
            	{
            		StringBuffer buf=new StringBuffer("### Crossed Book Cleared due to bid or offer side is empty: ").append(marketOrder.getMarketID());
            		buf.append(" - ").append(clearedCrossedBookInfo.toString());
            		buf.append(" ### Number of Bids: [").append(numberOfBids).append("] ");
            		buf.append(" ### Number of Offers: [").append(numberOfOffers).append("]");
            		logger.trace(buf.toString());
            	}
            	
            	raiseCrossBookAlerts(clearedCrossedBookInfo);
    		}
        	
    		return;
    	}
    	
    	Long topBid=null;
    	Long topOffer=null;
    	
    	try
    	{
    		topBid=_bidPriceLevels.lastKey();
    	}
    	catch(NoSuchElementException e)
    	{
    		logger.error("Unable to find the top (last) element of the bid : "+marketOrder.toString());
    		return;
    	}
		
    	try
    	{
    		topOffer=_offerPriceLevels.firstKey();
    	}
    	catch(NoSuchElementException e)
    	{
    		logger.error("Unable to find the top (first) element of the offer : "+marketOrder.toString());
    		return;
    	}
    	
    	checkCrossedStatus(marketOrder.getMarketID(), triggeringTransaction, topBid, topOffer);
    	
    	return;
    }

    /**
     * toString
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("FullOrderBook");
        buf.append("[Bids]\n");
        if(_bids!=null && _bids.size()>0)
        {
            for(Iterator<MarketOrder> it=_bids.iterator();it.hasNext();)
            {
                MarketOrder order=(MarketOrder) it.next();
                buf.append("["+order.toString()+"]\n");
            }
        }
        else
        {
            buf.append("None\n");
        }

        buf.append("[Bid PriceLevels]");
        buf.append(_bidPriceLevels.toString());
        buf.append("\n");

        buf.append("[Offers]\n");
        if(_offers!=null && _offers.size()>0)
        {
            for(Iterator<MarketOrder> it=_offers.iterator();it.hasNext();)
            {
                MarketOrder order=(MarketOrder) it.next();
                buf.append("["+order.toString()+"]\n");
            }
        }
        else
        {
            buf.append("None\n");
        }

        buf.append("[Offer PriceLevels]");
        buf.append(_offerPriceLevels.toString());
        buf.append("\n");

        return(buf.toString());
    }
}

