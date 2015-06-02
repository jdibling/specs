package com.theice.mdf.client.domain;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.book.Book;
import com.theice.mdf.client.domain.book.BookContext;
import com.theice.mdf.client.domain.book.PriceLevelBookKeeper;
import com.theice.mdf.client.exception.ProcessingException;
import com.theice.mdf.message.notification.EndOfDayMarketSummaryMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract Market base class
 * 
 * @author : Adam Athimuthu
 */
public abstract class AbstractMarketBase implements MarketInterface, PriceLevelBookKeeper
{
    private static final Logger logger=Logger.getLogger(AbstractMarketBase.class.getName());
    
    private List<MessageBucketInterface> _messageCountList = null;
    
    protected MarketState _state=new MarketState();

    protected Book _book=null;
    
    protected GUIComponentsContext _guiContext = null;
    
        
    /**
     * End of Day Market Summary
     */
    protected EndOfDayMarketSummaryMessage _endOfDayMarketSummary=null;
    
    protected AbstractMarketBase()
    {	
    }
    
    public AbstractMarketBase(Book book) throws Exception
    {
        if(book==null)
        {
            throw(new InstantiationException("Concrete Book must be supplied"));
        }
    	_book=book;
    }

    /**
     * Get all the bids
     * @return
     */
    public Collection<MarketOrder> getBids()
    {
        return(_book.getBids());
    }

    /**
     * Get all the offers
     * @return
     */
    public Collection<MarketOrder> getOffers()
    {
        return(_book.getOffers());
    }

    /**
     * get the price level array in the "natural" order sorted based on the side
     * @param side
     */
    public PriceLevel[] getPriceLevels(char side)
    {
    	return(_book.getPriceLevels(side));
    }
    
    /**
     * set the market statistics
     * @param statistics
     */
    public synchronized void setStatistics(MarketStatistics statistics)
    {
    	_state.setStatistics(statistics);
    }

    /**
     * get the market statistics
     * @return market statistics
     */
    public MarketStatistics getStatistics()
    {
    	return(_state.getStatistics());
    }

    /**
     * Process cancel trade 
     *
     * @param orderId
     */
     public synchronized void handleCancelTrade(long orderId)
     {
 		_state.processCancelTrade(orderId);
     }

     /**
     * Process investigated trade 
     *
     * @param orderId
     * @param investigation status
     */
     public synchronized void handleInvestigatedTrade(long orderId, char investigationStatus)
     {
 		_state.processInvestigatedTrade(orderId, investigationStatus);
     }
     
    /**
     * get the list of recent trades
     * @return List of trade messages
     */
    public List<Trade> getRecentTrades()
    {
    	return(_state.getRecentTrades());
    }

	/**
     * get EndOfDayMarketSummaryMessage
     * @return EndOfDayMarketSummary
     */
	public EndOfDayMarketSummaryMessage getEndOfDayMarketSummary()
	{
		return(_endOfDayMarketSummary);
	}

	/**
     * set EndOfDayMarketSummaryMessage
     * @param EndOfDayMarketSummary
     */
	public synchronized void setEndOfDayMarketSummary(EndOfDayMarketSummaryMessage endOfDayMarketSummary)
	{
		_endOfDayMarketSummary=endOfDayMarketSummary;
	}

    /**
     * Reset the order updated flag to false, if the current value is true
     * @return true if the operation was successful
     */
    public synchronized boolean resetOrderUpdatedIfTrue()
    {
        return(_state.resetOrderUpdatedIfTrue());
    }
    
    public synchronized boolean resetMarketStatsUpdatedIfTrue()
    {
       return (_state.resetMarketStatsUpdatedIfTrue());
    }
    
    public synchronized void marketStatsUpdated()
    {
       _state.marketStatsUpdated();
    }
    
    /**
     * Reset the book updated flag to false, if the current value is true
     * @return true if the operation was successful
     */
    public synchronized boolean resetBookUpdatedIfTrue()
    {
    	return(_book.resetBookUpdatedIfTrue());
    }

    /**
     * @return true if the operation was successful
     */
    public synchronized boolean resetTradeHistoryUpdatedIfTrue()
    {
        return(_state.resetTradeHistoryUpdatedIfTrue());
    }

    /**
     * get book context
     * @BookContext
     */
    public BookContext getBookContext()
    {
    	return(_book.getContext());
    }
    
    /**
     * Add the price level messages that are sent by the multicast feed
     * Not used when we have the full order book, where the price levels are calculated
     * @param priceLevel
     * @see PriceLevelBook, AddPriceLevelHandler
     */
    public synchronized void addPriceLevel(PriceLevel priceLevel)
    {
        if(_book.getContext()!=BookContext.PRICELEVEL)
        {
        	logger.warn("Invalid Operation when the Book is not PriceLevel");
        	return;
        }

        try
        {
        	_book.addPriceLevel(priceLevel);
        }
    	catch(ProcessingException e)
    	{
    		StringBuffer msg=new StringBuffer();
    		msg.append("ProcessingException adding price level. Market : ").append(getMarketID());
    		logger.error(msg.toString());
    	}
    	
    	return;
    }
    
    /**
     * Add the price level messages that are sent by the multicast feed
     * Not used when we have the full order book, where the price levels are calculated
     * @param priceLevel
     * @see PriceLevelBook, ChangePriceLevelHandler
     */
    public synchronized void changePriceLevel(PriceLevel priceLevel)
    {
        if(_book.getContext()!=BookContext.PRICELEVEL)
        {
        	logger.warn("Invalid Operation when the Book is not PriceLevel");
        	return;
        }

        try
        {
        	_book.changePriceLevel(priceLevel);
        }
    	catch(ProcessingException e)
    	{
    		StringBuffer msg=new StringBuffer();
    		msg.append("ProcessingException changing price level. Market : ").append(getMarketID());
    		logger.error(msg.toString());
    	}
    	
    	return;
    }
    
    /**
     * remove the price level at the specified position
     * @param priceLevelPosition
     * @param side
     */
    public synchronized void removePriceLevel(byte priceLevelPosition, char side)
    {
        if(_book.getContext()!=BookContext.PRICELEVEL)
        {
        	logger.warn("Invalid Operation when the Book is not PriceLevel");
        	return;
        }

        try
        {
        	_book.removePriceLevel(priceLevelPosition, side);
        }
    	catch(ProcessingException e)
    	{
    		StringBuffer msg=new StringBuffer();
    		msg.append("ProcessingException removing price level. Market : ").append(getMarketID());
    		logger.error(msg.toString());
    	}
    	
    	return;
    }
    
    public synchronized void dumpBookToLogger(long timestamp, short numOfPriceLevelLogged, Logger snapshotLogger)
    {
       try
       {
          PriceLevel[] buySidePriceLevels = _book.getPriceLevels(MDFConstants.BID);
          PriceLevel[] sellSidePriceLevels = _book.getPriceLevels(MDFConstants.OFFER);
          boolean buySideOrderExists = buySidePriceLevels != null && buySidePriceLevels.length>0;
          boolean sellSideOrderExists = sellSidePriceLevels != null && sellSidePriceLevels.length>0;
          if (snapshotLogger.isInfoEnabled() && (buySideOrderExists || sellSideOrderExists))
          {
             if (buySideOrderExists)
             {
                String buySide = PriceLevelSnapshotLogGenerator.buildLogString(timestamp, MDFConstants.BID, buySidePriceLevels, numOfPriceLevelLogged, this);
                snapshotLogger.info(buySide);
             }
             if (sellSideOrderExists)
             {
                String sellSide =  PriceLevelSnapshotLogGenerator.buildLogString(timestamp, MDFConstants.OFFER, sellSidePriceLevels, numOfPriceLevelLogged, this);
                snapshotLogger.info(sellSide);
             }
          }
       }
       catch(Throwable t)
       {
          logger.error("Error when taking snapshots at :"+timestamp+" MarketID="+this.getMarketID()+". Error:"+t, t);
       }
    }
    
    public synchronized void setGUIComponentsContext(GUIComponentsContext context)
    {
       _guiContext = context;
    }
    
    public synchronized GUIComponentsContext getGUIComponentsContext()
    {
       return _guiContext;
    }
    
    public synchronized void updateGUIComponentsText(String str)
    {
       if (_guiContext != null)
       {
          _guiContext.displayPanelInitializingText(str);
       }
    }
    
    public synchronized void resetGUIComponentsText()
    {
       if (_guiContext != null)
       {
          _guiContext.clearPanelInitializingText();
       }
    }    
    
    public List<MessageBucketInterface> getMessageCountList()
    {
       return _messageCountList;
    }
    
    public void setMessageCountList(List<MessageBucketInterface> list)
    {
       _messageCountList=list;
    }
    
}

