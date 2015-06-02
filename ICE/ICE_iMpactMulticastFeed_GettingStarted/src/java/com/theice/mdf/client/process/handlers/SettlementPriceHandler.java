package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.SettlementPriceMessage;
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
public class SettlementPriceHandler extends AbstractMarketMessageHandler
{
    private static SettlementPriceHandler _instance = new SettlementPriceHandler();

    private static final Logger logger=Logger.getLogger(SettlementPriceHandler.class.getName());

    public static SettlementPriceHandler getInstance()
    {
        return _instance;
    }

    private SettlementPriceHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	SettlementPriceMessage theMessage=null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("SettlementPriceHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(SettlementPriceMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market==null)
            {
            	logger.error("Market Not Found while processing settlement price message : "+theMessage.toString());
            }
            else
            {
            	MarketStatistics statistics=market.getStatistics();
            	
            	if(statistics!=null)
            	{
            		synchronized(statistics)
            		{
                		statistics.setSettlementPrice(theMessage.SettlementPrice);
                		statistics.setSettlePriceDateTime(theMessage.EvaluationDateTime);
                		statistics.setSettlementOfficial(theMessage.IsOfficial);
            		}
                	
            		market.marketStatsUpdated();
            		
            		if(logger.isTraceEnabled())
                    {
                    	logger.trace("SettlementPrice Processed : "+statistics.toString());
                    }
            	}
            	else
            	{
                	logger.error("Market Not Found while processing settlement price message : "+theMessage.toString());
            	}
            	
            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing SettlementPrice message: "+e.getMessage());
        }
    }
}

