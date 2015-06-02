package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.InvestigatedTradeMessage;
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
public class InvestigatedTradeHandler extends AbstractMarketMessageHandler
{
    private static InvestigatedTradeHandler _instance = new InvestigatedTradeHandler();

    private static final Logger logger = Logger.getLogger(InvestigatedTradeHandler.class.getName());

    public static InvestigatedTradeHandler getInstance()
    {
        return _instance;
    }

    private InvestigatedTradeHandler()
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
    	
    	InvestigatedTradeMessage theMessage = null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("InvestigatedTradeHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(InvestigatedTradeMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());

            if(market!=null)
            {
                market.handleInvestigatedTrade(theMessage.OrderID,theMessage.Status);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Failure processing InvestigatedTrade: ",e);
        }

        return;
        
    }

}
