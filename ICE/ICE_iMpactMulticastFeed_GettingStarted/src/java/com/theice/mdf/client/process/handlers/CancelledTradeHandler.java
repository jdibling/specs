package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.CancelledTradeMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Cancelled Trade Message Handler.  We process this message by marking the specific trade as being
 * canceled. This is done only if we find this trade in our history list.
 * 
 * In addition, we update the market statistics with the last non-canceled trade information. Market statistics
 * should reflect the following:
 * 
 * - the trade that is not cancelled
 * - isSystemPricedLeg!=Y
 * - not a block trade
 * 
 * @author Adam Athimuthu
 */
public class CancelledTradeHandler extends AbstractMarketMessageHandler
{
    private static CancelledTradeHandler _instance = new CancelledTradeHandler();

    private static final Logger logger = Logger.getLogger(CancelledTradeHandler.class.getName());

    public static CancelledTradeHandler getInstance()
    {
        return _instance;
    }

    private CancelledTradeHandler()
    {
    }

    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	CancelledTradeMessage theMessage = null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("CancelledTradeMessageHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(CancelledTradeMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());

            if(market!=null)
            {
                market.handleCancelTrade(theMessage.OrderID);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Failure processing TradeMessage: " + e.toString());
        }

        return;
        
    }
}

