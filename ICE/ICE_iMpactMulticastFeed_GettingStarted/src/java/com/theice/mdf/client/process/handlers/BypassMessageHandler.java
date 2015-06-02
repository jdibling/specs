package com.theice.mdf.client.process.handlers;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * BypassMessageHandler is used by the core message handler factory for handling messages other
 * than the core messages. This is useful while handling historical data for all the notification messages
 * as we don't want these messages affecting the book
 *
 * @author Adam Athimuthu
 */
public class BypassMessageHandler extends AbstractMarketMessageHandler
{
    private static BypassMessageHandler _instance=new BypassMessageHandler();

    private static Logger logger=Logger.getLogger(BypassMessageHandler.class.getName());

    private BypassMessageHandler()
    {
    }

    /**
     * Singleton method
     * @return
     */
    public static BypassMessageHandler getInstance()
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
            logger.trace("BypassMessageHandler : "+message.toString());
        }
    }
}
