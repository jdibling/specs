package com.theice.mdf.client.multicast.handler;

import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.PriceLevel;
import com.theice.mdf.client.domain.book.PriceLevelBookKeeper;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;
import com.theice.mdf.message.MDMessage;
import org.apache.log4j.Logger;
import com.theice.mdf.message.notification.MarketSnapshotPriceLevelMessage;

/**
 * 
 * Context: PriceLevel Multicaster path
 * Channels: Snapshot
 * ------------------------------------
 * 
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MarketSnapshotPriceLevelHandler extends AbstractMarketMessageHandler
{
    private static MarketSnapshotPriceLevelHandler _instance=new MarketSnapshotPriceLevelHandler();

    private static Logger logger=Logger.getLogger(MarketSnapshotPriceLevelHandler.class.getName());

    private MarketSnapshotPriceLevelHandler()
    {
    }

    /**
     * Singleton method
     * @return
     */
    public static MarketSnapshotPriceLevelHandler getInstance()
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
            logger.trace("MarketSnapshotPriceLevelHandler : "+message.toString());
        }

        MarketInterface market=MarketsHolder.getInstance().findMarket(message.getMarketID());
        
        if(market==null)
        {
        	logger.error("Market not found while processing MarketSnapshotPriceLevelHandler : "+message.toString());
        	return;
        }
        
        MarketSnapshotPriceLevelMessage theMessage=(MarketSnapshotPriceLevelMessage) message;

        PriceLevel priceLevel=new PriceLevel(theMessage.getPrice(),theMessage.getQuantity(),
        		theMessage.getSide(),theMessage.getPriceLevelPosition(),theMessage.getOrderCount(),
        		theMessage.getImpliedQuantity(),theMessage.getImpliedOrderCount());
        ((PriceLevelBookKeeper) market).addPriceLevel(priceLevel);
        
        return;

    }
}
