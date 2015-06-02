package com.theice.mdf.client.qv.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVDefaultMessageHandler extends AbstractMarketMessageHandler
{
    private static QVDefaultMessageHandler _instance=new QVDefaultMessageHandler();

    private static Logger logger=Logger.getLogger(QVDefaultMessageHandler.class.getName());

    private QVDefaultMessageHandler()
    {
    }

    public static QVDefaultMessageHandler getInstance()
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

    }
}
