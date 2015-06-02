package com.theice.mdf.client.qv.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.EndOfDayMarketSummaryMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketMessageHandler extends AbstractMarketMessageHandler
{
    private static QVEndOfDayMarketMessageHandler _instance=new QVEndOfDayMarketMessageHandler();

    private static Logger logger=Logger.getLogger(QVEndOfDayMarketMessageHandler.class.getName());

    private QVEndOfDayMarketMessageHandler()
    {
    }

    public static QVEndOfDayMarketMessageHandler getInstance()
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
    	
    	int marketId=message.getMarketID();
    	
    	if(logger.isTraceEnabled())
        {
            logger.trace("Processing : "+message.toString());
        }
        
        try
        {
            MarketInterface market=MarketsHolder.getInstance().findMarket(marketId);
            
            if(market!=null)
            {
            	market.setEndOfDayMarketSummary((EndOfDayMarketSummaryMessage) message);
            }
            else
            {
            	logger.warn("Market not found while processing the EOD message : "+marketId);
            }
        }
        catch(Exception e)
        {
            logger.error("Error processing the while processing the EOD message : "+marketId);
        }
    }
}
