package com.theice.mdf.client.process.handlers;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Simple message handler, just logs the incoming message. This serves are a placeholder handler until
 * we implement specific handler for a given message type.
 *
 * @author Adam Athimuthu
 * Date: Aug 2, 2007
 * Time: 4:38:56 PM
 */
public class SimpleMessageHandler extends AbstractMarketMessageHandler
{
    private static SimpleMessageHandler _instance=new SimpleMessageHandler();

    private static Logger logger=Logger.getLogger(SimpleMessageHandler.class.getName());

    private SimpleMessageHandler()
    {
    }

    /**
     * Singleton method
     * @return
     */
    public static SimpleMessageHandler getInstance()
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
         logger.trace("SimpleMessageHandler : "+message.toString());
      }
    }
}
