package com.theice.mdf.client.process.handlers;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.SystemTextMessage;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class SystemTextHandler extends AbstractMarketMessageHandler
{
    private static SystemTextHandler _instance=new SystemTextHandler();

    private static Logger logger=Logger.getLogger(SystemTextHandler.class.getName());

    private SystemTextHandler()
    {
    }

    public static SystemTextHandler getInstance()
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
    	
        char messageType=message.getMessageType();

        if(logger.isTraceEnabled())
        {
            logger.trace("Processing SystemText: "+message.toString());
        }
        
        SystemTextMessage theMessage=(SystemTextMessage) message;
        
        return;
    }
}

