package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.MarketSnapshotMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketStatistics;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * This handler processes the market snapshort messages. These messages are used to build
 * the statistics for the market.
 * 
 * Context: FullOrderDepth, PriceLevel
 * Channel: Multicast Snapshot
 * 
 * This message is expected during both fullorder depth and pricelevel contexts
 * Followed by a snapshot message, we can expect one of the following messages depending on the context:
 * 
 * - AddModifyOrder (FullOrderDepth)
 * - AddPriceLevel (PriceLevel)
 * 
 * @author Adam Athimuthu
 */
public class MarketSnapshotHandler extends AbstractMarketMessageHandler
{
    private static MarketSnapshotHandler _instance = new MarketSnapshotHandler();

    private static final Logger logger=Logger.getLogger(MarketSnapshotHandler.class.getName());

    public static MarketSnapshotHandler getInstance()
    {
        return(_instance);
    }

    private MarketSnapshotHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
        MarketSnapshotMessage theMessage=null;

        if(logger.isTraceEnabled())
        {
            logger.trace("MarketSnapshot.handleMessage() : Entering ["+message.toString()+"]");
        }

        try
        {
            theMessage=(MarketSnapshotMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market==null)
            {
            	logger.error("Market Not Found while processing statistics message : "+theMessage.toString());
            }
            else
            {
            	MarketStatistics statistics=new MarketStatistics(theMessage);
            	
            	market.setStatistics(statistics);
            	
            	if(logger.isTraceEnabled())
                {
                	logger.trace("Statistics Processed : "+statistics.toString());
                }
                
                market.handleStateChange(statistics.getTradingStatus());

            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing MarketSnapshot message: "+e.getMessage(), e);
        }
    }
}

