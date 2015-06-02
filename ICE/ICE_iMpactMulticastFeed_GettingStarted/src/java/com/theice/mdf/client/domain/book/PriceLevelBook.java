package com.theice.mdf.client.domain.book;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.PriceLevel;
import com.theice.mdf.client.domain.transaction.Transaction;
import com.theice.mdf.client.domain.transaction.PriceLevelDummyTransaction;
import com.theice.mdf.client.exception.ProcessingException;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * PriceLevelBook holds only the price levels as sent by the feed server
 * No calculations are done
 * 
 * @see Market and the PriceLevel Multicaster and related processing
 * 
 * @author Adam Athimuthu
 */
public class PriceLevelBook extends AbstractBook 
{
    private static final Logger logger=Logger.getLogger(PriceLevelBook.class.getName());

    /**
     * Map of price levels for bids and offers
     * Key: position (supplied by the message)
     */
    protected List<PriceLevel> _bidPriceLevels=new LinkedList<PriceLevel>();
    protected List<PriceLevel> _offerPriceLevels=new LinkedList<PriceLevel>();
    
    protected int _numberOfLevels=5;
    /**
     * Cross book detection, if active, can only be applicable for FullOrderBooks.
     */
    protected boolean detectCrossedBook=AppManager.getAppContext().isCrossBookDetectionEnabled();

	private PriceLevelBook()
	{
	}

	/**
     * Constructor init with the number of levels
     * - for options markets, the level is set to 1
     * - for regular markets, the level is typically set to 5
     * @param numberOfLevels
     */
	public PriceLevelBook(int numberOfLevels)
	{
		_numberOfLevels=numberOfLevels;
	}

	public BookContext getContext()
	{
		return(BookContext.PRICELEVEL);
	}
	
    /**
     * get the price level array in the "natural" order sorted based on the side
     * Since the PriceLevelBook always keeps the price levels in their "natural" sorted order
     * as specified by the server's messages, we just return them after making a new array
     * @param side
     */
    public PriceLevel[] getPriceLevels(char side)
    {
    	PriceLevel[] priceLevels=new PriceLevel[0];
    	
    	if(MDFUtil.isBuy(side))
    	{
    		priceLevels=_bidPriceLevels.toArray(new PriceLevel[0]);
    	}
    	else
    	{
    		priceLevels=_offerPriceLevels.toArray(new PriceLevel[0]);
    	}
    	
    	return(priceLevels);
    }
    
    /**
     * add price level
     * @param price level
     */
    public void addPriceLevel(PriceLevel priceLevel) throws UnsupportedOperationException, ProcessingException
    {
    	LinkedList<PriceLevel> priceLevels=null;

    	if(priceLevel.isBuy())
    	{
    		priceLevels=(LinkedList<PriceLevel>)_bidPriceLevels;
    	}
    	else
    	{
    		priceLevels=(LinkedList<PriceLevel>)_offerPriceLevels;
    	}
    	
    	int position=((int) priceLevel.getPosition()-1);
    	
    	if(position<0)
    	{
    		logger.error("Invalid PriceLevel Position : "+priceLevel.toString());
    		return;
    	}

    	if(logger.isTraceEnabled())
    	{
        	logger.trace("Inserting PriceLevel at position : "+position);
    	}
    	
    	/**
    	 * Add the price level at the given position
    	 * If the book already has the MAX number of levels, then drop the last one prior to inserting
    	 */
    	try
    	{
    		if(priceLevels.size()==_numberOfLevels)
    		{
    			priceLevels.removeLast();
    		}
    		
        	priceLevels.add(position, priceLevel);
    	}
    	catch(IndexOutOfBoundsException e)
    	{
    		StringBuffer msg=new StringBuffer();
    		msg.append("addPriceLevel failed. Position : ").append(position);
    		msg.append(e.getMessage());
    		msg.append("[Current price level List : ").append(priceLevels.toString()).append("]");
    		logger.error(msg.toString());
    		
    		e.printStackTrace();
    		
    		throw(new ProcessingException(msg.toString()));
    	}
    	
        _bookUpdated.compareAndSet(false, true);
        
       /**
        * Check for crossed status
        */
       if(detectCrossedBook)
       {
          PriceLevelDummyTransaction dummyTransaction = new PriceLevelDummyTransaction(getMarketID(),"AddPriceLevel-"+priceLevel.toString());
          determineCrossedStatus(dummyTransaction);
       }

    	return;
    }

    /**
     * replace the price level at the given position
     * @param price level
     */
    public void changePriceLevel(PriceLevel priceLevel) throws UnsupportedOperationException,ProcessingException
    {
    	LinkedList<PriceLevel> priceLevels=null;

    	if(priceLevel.isBuy())
    	{
    		priceLevels=(LinkedList<PriceLevel>)_bidPriceLevels;
    	}
    	else
    	{
    		priceLevels=(LinkedList<PriceLevel>)_offerPriceLevels;
    	}
    	
    	int position=((int) priceLevel.getPosition()-1);
    	
    	if(position<0)
    	{
    		logger.error("Invalid PriceLevel Position : "+priceLevel.toString());
    		return;
    	}
    	
    	if(logger.isTraceEnabled())
    	{
        	logger.trace("Changing PriceLevel at position : "+position);
    	}
    	
    	try
    	{
        	PriceLevel oldPriceLevel=priceLevels.remove(position);
        	
        	if(oldPriceLevel==null)
        	{
        		logger.warn("PriceLevel not found while processing change : " +position);
        	}

        	priceLevels.add(position, priceLevel);
    	}
    	catch(IndexOutOfBoundsException e)
    	{
    		StringBuffer msg=new StringBuffer();
    		msg.append("changePriceLevel failed. Position : ").append(position);
    		msg.append(e.getMessage());
    		msg.append("[Current price level List : ").append(priceLevels.toString()).append("]");
    		logger.error(msg.toString());
    		
    		e.printStackTrace();
    		
    		throw(new ProcessingException(msg.toString()));
    	}
    	
        _bookUpdated.compareAndSet(false, true);
        
      /**
       * Check for crossed status
       */
      if(detectCrossedBook)
      {
         PriceLevelDummyTransaction dummyTransaction = new PriceLevelDummyTransaction(getMarketID(),"ChangePriceLevel-"+priceLevel.toString());
         determineCrossedStatus(dummyTransaction);
      }

    	return;
    }

    /**
     * remove price level at the given position
     * @param position
     * @param side
     * @throws UnsupportedOperationException
     */
    public PriceLevel removePriceLevel(byte priceLevelPosition, char side)
    	throws UnsupportedOperationException,ProcessingException
    {
    	LinkedList<PriceLevel> priceLevels=null;
    	PriceLevel oldPriceLevel=null;    	

    	if(MDFUtil.isBuy(side))
    	{
    		priceLevels=(LinkedList<PriceLevel>)_bidPriceLevels;
    	}
    	else
    	{
    		priceLevels=(LinkedList<PriceLevel>)_offerPriceLevels;
    	}
    	
    	int position=((int) priceLevelPosition-1);
    	
    	if(position<0)
    	{
    		logger.error("Invalid PriceLevel Position : "+position);
    		return(oldPriceLevel);
    	}
    	
    	if(logger.isTraceEnabled())
    	{
        	logger.trace("Removing PriceLevel at position : "+position);
    	}

    	try
    	{
        	oldPriceLevel=priceLevels.remove(position);
    	}
    	catch(IndexOutOfBoundsException e)
    	{
    		StringBuffer msg=new StringBuffer();
    		msg.append("removePriceLevel failed. Position : ").append(position);
    		msg.append(e.getMessage());
    		msg.append("[Current price level List : ").append(priceLevels.toString()).append("]");
    		logger.error(msg.toString());
    		
    		e.printStackTrace();
    		
    		throw(new ProcessingException(msg.toString()));
    	}
    	
    	if(oldPriceLevel!=null)
    	{
            _bookUpdated.compareAndSet(false, true);
    	}
    	else
    	{
    		logger.warn("PriceLevel not found while processing removal at position : " +position);
    	}
    	
    	/**
       * Check for crossed status
       */
      if(detectCrossedBook)
      {
         String desc="RemovePriceLevel|Position="+priceLevelPosition;
         if(MDFUtil.isBuy(side))
         {
            desc+="|Buy";
         }
         else
         {
            desc+="|Sell";
         }
         
         PriceLevelDummyTransaction dummyTransaction = new PriceLevelDummyTransaction(getMarketID(),desc);
         determineCrossedStatus(dummyTransaction);
      }
      
    	return(oldPriceLevel);
    }
    
    public void initialize()
    {
    	logger.info("Initialize...");
        _bidPriceLevels.clear();
        _offerPriceLevels.clear();
        _bookUpdated.compareAndSet(false, true);
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
    private void determineCrossedStatus(Transaction triggeringTransaction)
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
                  StringBuffer buf=new StringBuffer("### Crossed Book Cleared due to bid or offer side is empty: ").append(triggeringTransaction.getMarketId());
                  buf.append(" - ").append(clearedCrossedBookInfo.toString());
                  buf.append(" ### Number of Bids: [").append(numberOfBids).append("] ");
                  buf.append(" ### Number of Offers: [").append(numberOfOffers).append("]");
                  logger.trace(buf.toString());
               }
               
               raiseCrossBookAlerts(clearedCrossedBookInfo);
         }
         
         return;
      }
      
      PriceLevel topBid=null;
      PriceLevel topOffer=null;
      
      if(numberOfBids>0)
      {
         topBid=_bidPriceLevels.get(0);
      }
      
      if(numberOfOffers>0)
      {
         topOffer=_offerPriceLevels.get(0);
      }
            
      if (topBid==null || topOffer==null)
      {
         return;
      }
      
      checkCrossedStatus(triggeringTransaction.getMarketId(), triggeringTransaction, topBid.getPrice(), topOffer.getPrice());
      
      return;
    }    
    
    public Collection getBids() 
    {
        return(_bidPriceLevels);
    }
    
    public Collection getOffers() 
    {
        return(_offerPriceLevels);
    }
        
    public String toString()
    {
        StringBuffer buf=new StringBuffer("PriceLevelBook");
        buf.append("[BidPriceLevels]");
        buf.append(_bidPriceLevels.toString());
        buf.append("\n");
        buf.append("[OfferPriceLevels]");
        buf.append(_offerPriceLevels.toString());
        buf.append("\n");
        return(buf.toString());
    }
}

