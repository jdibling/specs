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
public class QVOptionOpenInterestHandler extends AbstractMarketMessageHandler
{
    private static QVOptionOpenInterestHandler _instance=new QVOptionOpenInterestHandler();

    private static Logger logger=Logger.getLogger(QVOptionOpenInterestHandler.class.getName());

    private QVOptionOpenInterestHandler()
    {
    }

    public static QVOptionOpenInterestHandler getInstance()
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
