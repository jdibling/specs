package com.theice.mdf.client.multicast.handler;

import java.util.Random;

import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.PriceLevel;
import com.theice.mdf.client.domain.book.PriceLevelBookKeeper;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.MDMessage;
import org.apache.log4j.Logger;
import com.theice.mdf.message.pricelevel.AddPriceLevelMessage;

/**
 * 
 * Context: PriceLevel Multicaster path
 * Channels: Incremental
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
 * The book is updaetd for both the bid/offer sides. The collections within the corresponding 
 * markets are updated.
 * 
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class AddPriceLevelHandler extends AbstractMarketMessageHandler
{
    private static AddPriceLevelHandler _instance=new AddPriceLevelHandler();

    private static Logger logger=Logger.getLogger(AddPriceLevelHandler.class.getName());

    private AddPriceLevelHandler()
    {
    }

    /**
     * Singleton method
     * @return
     */
    public static AddPriceLevelHandler getInstance()
    {
        return(_instance);
    }
    
    /**
     * handle the message
     * @param priceFeedMessage
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
        if(logger.isTraceEnabled())
        {
            logger.trace("AddPriceLevelHandler : "+message.toString());
        }

        MarketInterface market=MarketsHolder.getInstance().findMarket(message.getMarketID());
        
        if(market==null)
        {
        	logger.error("Market not found while processing AddPriceLevel : "+message.toString());
        	return;
        }
        
        AddPriceLevelMessage theMessage=(AddPriceLevelMessage) message;
        
        /*
        if (SIMULATECROSSEDBOOK)
        {
           simulateCrossedBook(theMessage);
        }
        */
        
        PriceLevel priceLevel=new PriceLevel(theMessage.getPrice(),theMessage.getQuantity(),
        		theMessage.getSide(),theMessage.getPriceLevelPosition(),theMessage.getOrderCount(),
        		theMessage.getImpliedQuantity(),theMessage.getImpliedOrderCount());
        ((PriceLevelBookKeeper) market).addPriceLevel(priceLevel);
        
        return;

    }
    
    protected AddPriceLevelMessage simulateCrossedBook(AddPriceLevelMessage theMessage)
    {
       Random random=new Random();
       int randomNumber=random.nextInt(10000);
    
       if(randomNumber==7)
       {
          if(MDFUtil.isBuy(theMessage.getSide()))
          {
             long price=theMessage.getPrice();
             price+=100;
             theMessage.setPrice(price);
          }
       }
       
       return theMessage;
    }
}

