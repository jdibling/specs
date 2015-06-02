package com.theice.mdf.client.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Contains book/price levels for bid/offers are updated using messages such as AddModifyOrder,
 * DeleteOrder and Trade.
 * 
 * Market statistics information is captured using the market snapshot and updated through message such as
 * market statistics, open interest, open price and settlement price
 * 
 * The recent 50 trades are kept in an internal linked list
 * 
 * Used by any market (regular or option market)
 *
 * @author : Adam Athimuthu
 * @author Qian Wang
 */
public class MarketState implements Serializable
{
    private static final Logger logger=Logger.getLogger(MarketState.class.getName());
    
    /**
     * Market Statistics
     * Source: Market Snapshot (C)
     * Updates : Market Statistics (J), Open Interest (M), Open Price (N), Settlement Price (O)
     * Updates (option markets): OptionMarketStatistics (Z)
     */
    protected MarketStatistics _statistics=null;

    /**
     * Recent 50 Trades
     * Source: Trade (G)
     * OptionTrade (W) for option markets
     */
    protected List<Trade> _recentTrades=new LinkedList<Trade>();
    
    /**
     * This flag indicates whether the market's order information has changed
     * We update this flag when an order gets added/modified or deleted. Consumers of the market order
     * information (typically the GUI models) will use this flag to determine if a refresh is needed for the
     * display. If this flag is true, a refresh is performed followed by a reset of this flag back to false.
     */
    protected AtomicBoolean _orderUpdated=new AtomicBoolean(false);

    protected AtomicBoolean _tradeHistoryUpdated=new AtomicBoolean(false);
    
    protected AtomicBoolean _marketStatsUpdated=new AtomicBoolean(false);

    /**
     * process trade message
     * @param tradeMessage
     */
    public void processTrade(Trade trade)
    {
    	if(logger.isTraceEnabled())
        {
            logger.trace("processTrade:"+trade);
        }
        
        if(_recentTrades.size()>=MarketInterface._maxRecentTrades)
        {
            ((LinkedList<Trade>)_recentTrades).removeFirst();
        }

        ((LinkedList<Trade>)_recentTrades).addLast(trade);

        if(MDFUtil.canProcessTrade(trade))
        {
           if(_statistics==null)
           {
              _statistics=new MarketStatistics();
           }

           if (!trade.isAdjusted())
           {   
              _statistics.setLastTradeQuantity(trade.getTradeMessage().Quantity);
              _statistics.setLastTradePrice(trade.getTradeMessage().Price);
              _statistics.setLastTradeDateTime(trade.getTradeMessage().DateTime);
           }
           else
           {
              ListIterator<Trade> list=((LinkedList<Trade>)_recentTrades).listIterator(_recentTrades.size());
              Trade lastTradeEligibleForLastDeal = null;
              while(list.hasPrevious())
              {
                 lastTradeEligibleForLastDeal = (Trade)list.previous();
                 if (!lastTradeEligibleForLastDeal.isCancelled() && 
                       (lastTradeEligibleForLastDeal.getInvestigationStatus()==null || lastTradeEligibleForLastDeal.getInvestigationStatus().getStatus()!=InvestigationStatus.STATUS_UNDER_INVESTIGATION))
                 {
                    break;
                 }
              }
              int qty = trade.getTradeMessage().Quantity;
              long price = trade.getTradeMessage().Price;
              long dateTime = trade.getTradeMessage().DateTime;
              
              if (lastTradeEligibleForLastDeal!=null && lastTradeEligibleForLastDeal.getTradeMessage().DateTime>trade.getTradeMessage().DateTime)
              {
                 qty = lastTradeEligibleForLastDeal.getTradeMessage().Quantity;
                 price = lastTradeEligibleForLastDeal.getTradeMessage().Price;
                 dateTime = lastTradeEligibleForLastDeal.getTradeMessage().DateTime;
              }
              _statistics.setLastTradeQuantity(qty);
              _statistics.setLastTradePrice(price);
              _statistics.setLastTradeDateTime(dateTime);
           }
           
           //((LinkedList<Trade>)_recentTrades).addLast(trade);
        }
        
        _orderUpdated.compareAndSet(false, true);
		
        _tradeHistoryUpdated.compareAndSet(false, true);

        return;
    }

    /**
     * Process cancel trade
     * @param orderId
     */
    public void processCancelTrade(long orderId)
    {
    	if(logger.isTraceEnabled())
        {
            logger.trace("processCancelTrade:"+orderId);
        }

        Trade trade=findTradeInHistory(orderId);
        
        if(trade==null)
        {
        	if(logger.isTraceEnabled())
	        {
	        	logger.trace("Trade not found while trying to cancel:"+orderId);
	        }
	        
	        return;
        }

		trade.setCancelled(true);

		/**
         * Update the statistics with the last good trade
         */
    	if(logger.isTraceEnabled())
        {
            logger.trace("processCancelTrade: Updating the statistics");
        }

        ListIterator<Trade> list=((LinkedList<Trade>)_recentTrades).listIterator(_recentTrades.size());
        
        Trade lastGoodTrade=null;
        
        while(list.hasPrevious())
        {
           lastGoodTrade=(Trade) list.previous();
        	
           if(lastGoodTrade.isCancelled())
           {
              lastGoodTrade=null;
              continue;
           }
        	
           if(MDFUtil.canProcessTrade(lastGoodTrade))
           {
              break;
           }
           else
           {
              //set lastGoodTrade to null, otherwise it might be pointing to the previous trade that is not eligible for being "last trade"
              lastGoodTrade = null;
           }
        }

        if(lastGoodTrade!=null)
        {
        	if(_statistics==null)
        	{
        		_statistics=new MarketStatistics();
        	}

        	_statistics.setLastTradeQuantity(lastGoodTrade.getTradeMessage().Quantity);
        	_statistics.setLastTradePrice(lastGoodTrade.getTradeMessage().Price);
        	_statistics.setLastTradeDateTime(lastGoodTrade.getTradeMessage().DateTime);
        }
        else
        {
        	if(_statistics!=null)
        	{
	        	_statistics.setLastTradeQuantity(0);
	        	_statistics.setLastTradePrice(0);
	        	_statistics.setLastTradeDateTime(0L);
        	}
        }
    	
 		_orderUpdated.compareAndSet(false, true);
 		_tradeHistoryUpdated.compareAndSet(false, true);
     	
        return;
    }

    /**
     * process trade to mark as being investigated
     * @param orderId
     * @param investigationStatus (valid values: 1 or 2)
     */
    public void processInvestigatedTrade(long orderId, char investigationStatus)
    {
    	if(logger.isTraceEnabled())
        {
            logger.trace("processInvestigatedTrade:"+orderId);
        }

        Trade trade=findTradeInHistory(orderId);
        
        if(trade==null)
        {
        	if(logger.isTraceEnabled())
	        {
	        	logger.trace("Trade not found while trying to mark for investigation:"+orderId);
	        }
	        
	        return;
        }
        
        trade.setInvestigationStatus(InvestigationStatus.getInvestigationStatus(investigationStatus));
        
		_orderUpdated.compareAndSet(false, true);

		_tradeHistoryUpdated.compareAndSet(false, true);
      	
		return;
    }


    /**
     * find trade in history using the orderId
     * @param orderId
     * @return
     */
    protected Trade findTradeInHistory(long orderId)
    {
    	Trade trade=null;
        ListIterator<Trade> list=((LinkedList<Trade>)_recentTrades).listIterator(_recentTrades.size());
    	
        while(list.hasPrevious())
        {
        	trade=(Trade) list.previous();
        	
        	if(trade.getTradeMessage().OrderID==orderId)
        	{
        		break;
        	}
        }
        
        return(trade);
    }
    
    /**
     * set the market statistics
     * @param statistics
     */
    public void setStatistics(MarketStatistics statistics)
    {
    	_statistics=statistics;
 		_orderUpdated.compareAndSet(false, true);
    }

    /**
     * get the market statistics
     * @return market statistics
     */
    public MarketStatistics getStatistics()
    {
    	return(_statistics);
    }

    /**
     * get the list of recent trades
     * @return List of trade messages
     */
    public List<Trade> getRecentTrades()
    {
    	return(_recentTrades);
    }

    /**
     * Reset the order updated flag to false, if the current value is true
     * @return true if the operation was successful
     */
    public boolean resetOrderUpdatedIfTrue()
    {
        return(_orderUpdated.compareAndSet(true, false));
    }

    /**
     * @return true if the operation was successful
     */
    public boolean resetTradeHistoryUpdatedIfTrue()
    {
        return(_tradeHistoryUpdated.compareAndSet(true, false));
    }
    
    public boolean resetMarketStatsUpdatedIfTrue()
    {
       return (_marketStatsUpdated.compareAndSet(true, false));
    }
    
    public void marketStatsUpdated()
    {
       _marketStatsUpdated.compareAndSet(false, true);
    }

    public void initialize()
    {
    	logger.info("Initialize...");
    	
    	if(_statistics!=null)
    	{
            _statistics=null;
    	}
    	
        _recentTrades.clear();
        
        _orderUpdated.compareAndSet(false, true);
        _tradeHistoryUpdated.compareAndSet(false, true);
    }
    
    /**
     * toString
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("State : ");
        buf.append("[Bids]\n");
        buf.append("[Statistics]");
        if(_statistics!=null)
        {
            buf.append("["+_statistics.toString()+"]");
        }
        buf.append("\n");

        buf.append("[Recent Trades]");
        if(_recentTrades!=null)
        {
            buf.append("["+_recentTrades.toString()+"]");
        }
        buf.append("\n");

        return(buf.toString());
    }
}

