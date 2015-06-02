package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.MarketStateChangeMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MarketStateChangeHandler extends AbstractMarketMessageHandler
{
    private static MarketStateChangeHandler _instance = new MarketStateChangeHandler();

    private static final Logger logger = Logger.getLogger(MarketStateChangeHandler.class.getName());

    public static MarketStateChangeHandler getInstance()
    {
        return _instance;
    }

    private MarketStateChangeHandler()
    {
    }

    /**
     * handle the message
     *
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	MarketStateChangeMessage theMessage = null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("MarketStateChangeHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(MarketStateChangeMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());

            if(market!=null)
            {
                market.handleStateChange(theMessage.TradingStatus);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Failure processing MarketStateChange: " + e.toString());
        }

    }

}
