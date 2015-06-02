package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.OpenPriceMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketStatistics;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class OpenPriceHandler extends AbstractMarketMessageHandler
{
    private static OpenPriceHandler _instance = new OpenPriceHandler();

    private static final Logger logger=Logger.getLogger(OpenPriceHandler.class.getName());

    public static OpenPriceHandler getInstance()
    {
        return _instance;
    }

    private OpenPriceHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	OpenPriceMessage theMessage=null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("OpenPriceHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(OpenPriceMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market==null)
            {
            	logger.error("Market Not Found while processing open price message : "+theMessage.toString());
            }
            else
            {
            	MarketStatistics statistics=market.getStatistics();
            	
            	if(statistics!=null)
            	{
            		synchronized(statistics)
            		{
                		statistics.setOpeningPrice(theMessage.OpenPrice);
            		}
                	
            		if(logger.isTraceEnabled())
                    {
                    	logger.trace("OpenPrice Processed : "+statistics.toString());
                    }
            	}
            	else
            	{
                	logger.error("Market Not Found while processing open price message : "+theMessage.toString());
            	}
            	
            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing OpenPrice message: "+e.getMessage());
        }
    }
}

