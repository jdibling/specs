package com.theice.mdf.client.qv.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.MarkerIndexPriceMessage;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;
import com.theice.mdf.client.qv.domain.QVMessageHolder;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarkerPriceIndexHandler extends AbstractMarketMessageHandler
{
    private static QVMarkerPriceIndexHandler _instance=new QVMarkerPriceIndexHandler();

    private static Logger logger=Logger.getLogger(QVMarkerPriceIndexHandler.class.getName());

    private QVMarkerPriceIndexHandler()
    {
    }

    public static QVMarkerPriceIndexHandler getInstance()
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
    	
    	MarkerIndexPriceMessage theMessage=(MarkerIndexPriceMessage) message;
    	
    	if(logger.isTraceEnabled())
        {
            logger.trace("Processing : "+message.toString());
        }
        
//        if(theMessage.RequestSeqID==(-1))
//        {
//            if(logger.isLoggable(Level.FINEST))
//            {
//                logger.finest("Handling broadcast message");
//            }
//            
//            QVMessageHolder.getInstance().storeQVMarkerPrice(theMessage);
//        }
    }
}

