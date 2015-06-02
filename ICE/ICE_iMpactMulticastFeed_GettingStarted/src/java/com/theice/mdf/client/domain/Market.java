package com.theice.mdf.client.domain;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.domain.book.Book;
import com.theice.mdf.client.domain.book.BookContext;
import com.theice.mdf.client.domain.book.FullOrderDepthBookKeeper;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.domain.transaction.TradeTransaction;
import com.theice.mdf.client.domain.transaction.Transaction;
import com.theice.mdf.client.exception.InvalidStateException;
import com.theice.mdf.message.response.FuturesStrategyDefinitionResponse;
import com.theice.mdf.message.response.ProductDefinitionResponse;
import com.theice.mdf.message.MessageUtil;
import java.lang.InstantiationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Represents the Market Information. The market objects are built with the product definitions and later
 * updated using other messages for state changes etc., In addition, every market contains collections for holding
 * the book for bids and offers. The aggregate information is held in the corresponding price level collections.
 *
 * Information at the book/price levels for bid/offers are updated using messages such as AddModifyOrder,
 * DeleteOrder and Trade.
 * 
 * Market statistics information is captured using the market snapshot and updated through message such as
 * market statistics, open interest, open price and settlement price
 * 
 * The recent 50 trades are kept in an internal linked list
 * 
 * A market can hold a collection of OptionsMarkets depending on whether it has options markets
 * 
 * @author : Adam Athimuthu
 * @author Qian Wang
 * Date: Aug 2, 2007
 * Time: 4:23:59 PM
 */
public class Market extends AbstractMarketBase implements FullOrderDepthBookKeeper
{
    private static final Logger logger=Logger.getLogger(Market.class.getName());
    
	/**
	 * The key of the market
	 */
    protected MarketKey _key=null;

    /**
     * The source of the market information
     * Source: Product Definition (B)
     * Updates: Market State Change (K) 
     */
    protected ProductDefinitionResponse _source=null;
    
    protected FuturesStrategyDefinitionResponse _futuresStrategyDefinition =null;
    
    /**
     * Map of all orders (irrespective of bids/offers)
     * This collection is populated *only* if the underlying book is of type "full order depth"
     * For Price Level book implementations, the global orders collection is not used
     * Key: OrderId
     * Value: MarketOrder
     */
    protected Map<Long,MarketOrder> _orders=new HashMap<Long,MarketOrder>();

    /**
     * The Option markets associated with this market
     */
    protected Map<OptionMarketKey,OptionMarket> _optionMarkets=new HashMap<OptionMarketKey,OptionMarket>();
    
    protected AtomicBoolean _dependentMarketsUpdated=new AtomicBoolean(false);

    private Market()
    {
    	super();
    }

    /**
     * Constructor
     * @param productDefinition
     * @param book, the concrete book (full order vs. price level)
     * @throws Exception
     */
    public Market(ProductDefinitionResponse productDefinition,Book book) throws Exception
    {
    	super(book);
    	
        if(productDefinition==null)
        {
            throw(new InstantiationException("Exception Creating Market"));
        }
        
        _key=new MainMarketKey(productDefinition.getMarketID());
        
        this._source=productDefinition;
    }
    
    public Market(FuturesStrategyDefinitionResponse udsFuturesProductDefinition,Book book) throws Exception
    {
    	super(book);
    	
        if(udsFuturesProductDefinition==null)
        {
            throw(new InstantiationException("Exception Creating Market"));
        }
        
        _key=new MainMarketKey(udsFuturesProductDefinition.getMarketID());
        
        this._futuresStrategyDefinition=udsFuturesProductDefinition;
    }


    /**
     * initialize internal data structures
     */
    public synchronized void initialize()
    {
    	logger.info("Initializing (orders/states/book/remove option markets)...: "+getMarketID());
    	    	
    	updateGUIComponentsText("SEQUENCE GAP DETECTED, BOOK IS BEING REINITIALIZED ...");

    	_orders.clear();
    	_state.initialize();
    	_book.initialize();
        
      /**
       * Remove all option markets (if any)
       */
      _optionMarkets.clear();
      _dependentMarketsUpdated.compareAndSet(false, true);
    }

    /**
     * get market id
     * @return
     */
    public int getMarketID()
    {
        return(_source.getMarketID());
    }

    /**
     * get market key
     * @return
     */
    public MarketKey getMarketKey()
    {
        return(_key);
    }

    /**
     * get market type
     * @return
     */
    public short getMarketType()
    {
        return(_source.RequestMarketType);
    }

    /**
     * get market description
     * @return
     */
    public String getMarketDesc()
    {
        return(MessageUtil.toString(this._source.MarketDesc));
    }

    /**
     * get contract symbol
     * @return
     */
    public String getContractSymbol()
    {
        return(MessageUtil.toString(this._source.ContractSymbol));
    }

    /**
     * is this an option market?
     * @return
     */
    public boolean isOptionMarket()
    {
        return(false);
    }
    
    public boolean isUDSMarket()
    {
       return false;
    }

    /**
     * Get the source product definition message
     * @return
     */
    public ProductDefinitionResponse getSource()
    {
        return(_source);
    }

    /**
     * Get order price denom
     * @return order price denom
     */
	public char getOrderPriceDenominator()
	{
		return(_source.OrderPriceDenominator);
	}
	
    /**
     * Get deal price denom
     * @return deal price denom
     */
	public char getDealPriceDenominator()
	{
		return(_source.DealPriceDenominator);
	}

    /**
     * Get number of decimals for options price (used for options markets)
     * @return options price denom
     */
	public char getNumDecimalsOptionsPrice()
	{
		return(_source.NumDecimalsOptionsPrice);
	}
	
    /**
     * Get the number of decmials for strike price (used for options markets)
     * @return strike price denom
     */
	public char getNumDecimalsStrikePrice()
	{
		return(_source.NumDecimalsStrikePrice);
	}

	/**
     * get underlying market. For regular markets, just return 'this'
     * @return Market
     */
	public MarketInterface getUnderlyingMarket()
	{
		return(this);
	}

	/**
	 * Process market state change
	 * TODO do a validity check. Blank is used by the multicast sync channel to avoid
	 * TODO options status changes overwriting the underlying market state
	 * TODO update a flag so we can signal the consumers of status (e.g. GUI)
	 * 
	 * @param tradingStatus
	 */
     public synchronized void handleStateChange(char tradingStatus)
     {
    	 if(tradingStatus!=' ')
    	 {
    		 _source.TradingStatus=tradingStatus;
    	 }
     }

    /**
     * Adding an order involves the following steps
     *
     * 1. First we try and remove the order if it already exists
     * 2. Based on the Side (bid/offer) of the incoming order, we update the bids or offers collection
     * 3. Finally the corresponding price level (the price level that matches the price)
     *      is incremented by the quantity of the incoming order. If a price level doesn't already exist
     *      a new price level is created and inserted into the map
     *      
     * @param AddModifyTransaction containing the market order
     */
    public synchronized void addOrder(AddModifyTransaction transaction) throws UnsupportedOperationException
    {
    	MarketOrder marketOrder=transaction.getOrder();
    	
        if(_book.getContext()!=BookContext.FULLORDERDEPTH)
        {
        	logger.warn("Invalid Operation when the Book is not FullOrderDepth");
        	return;
        }
        
        MarketOrder removedOrder=removeOrder(marketOrder.getOrderID(),transaction);
        MarketOrder order = transaction.getOrder();
        
        if (MDFClientConfigurator.isVerifyingModifyOrderFlag())
        {
           if ((order.isModifyOrder() && removedOrder==null) || (!order.isModifyOrder() && removedOrder!=null))
           {
              logger.fatal("Incorrect flag: "+order.toString());
           }
        }
        
        /**
         * Add the new order
         */
        
        marketOrder.setDateTimeAddedToBook(System.currentTimeMillis());
        
        _orders.put(new Long(marketOrder.getOrderID()), marketOrder);
        
        _book.processAddOrder(transaction);
        
        return;
    }
    
    /**
     *
     * Removing an order involves updating the book and adjusting the price levels
     *
     * 1. Try to remove an order. If the order id doesn't exist in the internal map it is still okay.
     * 2. From the removed order, we determine the Side (bid/offer). Based on the Side, we choose
     * the collection(s) to operate on
     * 3. The order gets removed from the bids or offers collection.
     * 4. Finally we update or delete the price level depending on the Quantity of the removed order 
     *
     * removeOrder is common for regular orders and option orders. Check the isOptionOrder flag
     * and process accordingly
     * 
     * @param orderId
     * @param triggeringTransaction, the transaction that triggered the removal (such as a modify order that will replace this one).
     * @return removed order
     * @throws InvalidStateException
     * 
     * @see AddModifyOrderHandler, DeleteOrderHandler, TradeMessageHandler
     */
    public synchronized MarketOrder removeOrder(long orderId,Transaction triggeringTransaction) throws UnsupportedOperationException
    {
        MarketOrder removedOrder=null;
        
        if(_book.getContext()!=BookContext.FULLORDERDEPTH)
        {
        	logger.warn("Invalid Operation when the Book is not FullOrderDepth");
        	return(removedOrder);
        }

        removedOrder=_orders.remove(new Long(orderId));

        if(removedOrder!=null)
        {
    		_book.processRemoveOrder(removedOrder,triggeringTransaction);
    		removedOrder.setDateTimeRemovedFromBook(System.currentTimeMillis());
        }

        return(removedOrder);
    }
    
    /**
     * Processing a trade message involves the following: 
     *
     * 1. Try removing an order
     * 2. Update the internal list of recent trades (irrespective of the flags)
     * 3. If trade quantity>0 and isSystemPricedLeg=N and BlockTrade is blank,
     * 		then update last trade price/quantity/TrasactDateTime as part of the statistics
     *
     * @param TradeTransaction containing the trade
     * @return removed order as a result of this trade, if any
     */
     public synchronized MarketOrder handleTrade(TradeTransaction transaction)
     {
     	Trade trade=transaction.getTrade();
     	
     	MarketOrder removedOrder=null;
     	
		if(_book.getContext()==BookContext.FULLORDERDEPTH)
		{
			removedOrder=removeOrder(trade.getTradeMessage().OrderID,transaction);
		}

		if(trade.getTradeMessage().Quantity>0 || trade.getTradeMessage().Price>0)
     	{
     		_state.processTrade(trade);
     	}
     	
         return(removedOrder);
     }
     
    /**
     * Reset the option order updated flag to false, if the current value is true
     * @return true if the operation was successful
     */
    public synchronized boolean resetDependentMarketsUpdatedIfTrue()
    {
        return(_dependentMarketsUpdated.compareAndSet(true, false));
    }

    /**
     * get the option markets associated with this market
     * @return
     */
    public Map<OptionMarketKey,OptionMarket> getOptionMarkets()
    {
    	return(_optionMarkets);
    }
    
    /**
     * get the option market
     * @return
     */
    public OptionMarket getOptionMarket(OptionMarketKey key)
    {
    	return(_optionMarkets.get(key));
    }

    public synchronized void addOptionsMarket(OptionMarket optionMarket)
    {
    	this._optionMarkets.put((OptionMarketKey) optionMarket.getMarketKey(), optionMarket);
    	return;
    }
    
    /**
     * toString
     * @return
     */
    public synchronized String toString()
    {
        StringBuffer buf=new StringBuffer("Market Details : ");
        //buf.append(this._source.toString());
        buf.append("["+this.getMarketID()+"]");
        buf.append("["+ MessageUtil.toString(this._source.MarketDesc)+"]");
        buf.append("\n");
        buf.append(_state.toString());
        buf.append("[OptionMarkets]");
        buf.append("["+_optionMarkets.toString()+"]");
        buf.append("\n");
        
        if(_endOfDayMarketSummary!=null)
        {
        	buf.append("[EndOfDayMarketSummary="+_endOfDayMarketSummary.toString()+"]");
        }

        return(buf.toString());
    }
}

