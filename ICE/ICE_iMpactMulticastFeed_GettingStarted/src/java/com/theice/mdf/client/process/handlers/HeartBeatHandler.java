package com.theice.mdf.client.process.handlers;

import org.apache.log4j.Logger;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HeartBeatHandler extends AbstractMarketMessageHandler
{
    private static Logger logger=Logger.getLogger(HeartBeatHandler.class.getName());
    
    private static HeartBeatHandler _instance=new HeartBeatHandler();

    private HeartBeatHandler()
    {
    }

    public static HeartBeatHandler getInstance()
    {
        return(_instance);
    }

    /**
     * handle the message - just a pass through
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	if(logger.isTraceEnabled())
        {
            logger.trace("HeartBeatHandler : "+message.toString());
        }
    }
}
