package com.theice.mdf.client.multicast.handler;

import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.book.PriceLevelBookKeeper;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.pricelevel.DeletePriceLevelMessage;

import org.apache.log4j.Logger;

/**
 * 
 * Context: PriceLevel Multicaster path
 * Channels: Snapshot and Incremental
 * Markets: Futures/OTC, Options
 * ------------------------------------
 * 
 * This is the primary place where the book gets created within a given market.
 * For price level only clients, the individual orders are not kept in the market.
 * We keep just the top 5 levels as indicated in the message.
 * 
 * Applicable only during PriceLevel processing. During Full Order Depth, this handler is not used.
 * Instead the AddModifyOrder message will be used. In those cases,
 * the market's book will have a model whereby it keeps all the individual orders.
 * 
 * The book is updated for both the bid/offer sides. The collections within the corresponding 
 * markets are updated.
 * 
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class DeletePriceLevelHandler extends AbstractMarketMessageHandler
{
    private static DeletePriceLevelHandler _instance=new DeletePriceLevelHandler();

    private static Logger logger=Logger.getLogger(DeletePriceLevelHandler.class.getName());

    private DeletePriceLevelHandler()
    {
    }

    /**
     * Singleton method
     * @return
     */
    public static DeletePriceLevelHandler getInstance()
    {
        return(_instance);
    }
    
    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();

    	if(logger.isTraceEnabled())
        {
            logger.trace("DeletePriceLevelHandler : "+message.toString());
        }
        
        MarketInterface market=MarketsHolder.getInstance().findMarket(message.getMarketID());
        
        if(market==null)
        {
        	logger.error("Market not found while processing DeletePriceLevel : "+message.toString());
        	return;
        }
        
        DeletePriceLevelMessage theMessage=(DeletePriceLevelMessage) message;

        ((PriceLevelBookKeeper) market).removePriceLevel(theMessage.getPriceLevelPosition(), theMessage.getSide());
        
        return;

    }
}

