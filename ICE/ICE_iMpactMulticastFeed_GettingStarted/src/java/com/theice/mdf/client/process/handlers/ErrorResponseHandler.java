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
public class ErrorResponseHandler extends AbstractMarketMessageHandler
{
    private static ErrorResponseHandler _instance=new ErrorResponseHandler();

    private static Logger logger=Logger.getLogger(ErrorResponseHandler.class.getName());

    private ErrorResponseHandler()
    {
    }

    public static ErrorResponseHandler getInstance()
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
    	
    	logger.error("Error Response: "+message.toString());
    }
}
