package com.theice.mdf.client.process.handlers;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataHandler extends AbstractMarketMessageHandler
{
    private static HistoricalMarketDataHandler _instance=new HistoricalMarketDataHandler();

    private static Logger logger=Logger.getLogger(HistoricalMarketDataHandler.class.getName());

    private HistoricalMarketDataHandler()
    {
    }

    public static HistoricalMarketDataHandler getInstance()
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
            logger.trace("Processing : "+message.toString());
        }

        System.out.println("************* Processing : "+message.toString());
    }
}
