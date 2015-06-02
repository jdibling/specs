package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.OptionSettlementPriceMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketStatistics;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Shawn Cheng
 */
public class OptionSettlementPriceHandler extends AbstractMarketMessageHandler
{
    private static OptionSettlementPriceHandler _instance = new OptionSettlementPriceHandler();

    private static final Logger logger=Logger.getLogger(OptionSettlementPriceHandler.class.getName());

    public static OptionSettlementPriceHandler getInstance()
    {
        return _instance;
    }

    private OptionSettlementPriceHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
      MDMessage message=priceFeedMessage.getMessage();
      
      OptionSettlementPriceMessage theMessage=null;

      if(logger.isTraceEnabled())
        {
            logger.trace("OptionSettlementPriceHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(OptionSettlementPriceMessage) message;

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
                  
                  if(logger.isTraceEnabled())
                    {
                     logger.trace("SettlementPrice Processed : "+statistics.toString());
                    }
                  
                  market.marketStatsUpdated();
               }
               else
               {
                  logger.error("Market Not Found while processing option settlement price message : "+theMessage.toString());
               }
               
            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing OptionSettlementPrice message: "+e.getMessage());
        }
    }
}

