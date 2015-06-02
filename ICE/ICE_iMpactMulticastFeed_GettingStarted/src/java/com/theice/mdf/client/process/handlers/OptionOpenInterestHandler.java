package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.OptionOpenInterestMessage;
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
public class OptionOpenInterestHandler extends AbstractMarketMessageHandler
{
    private static OptionOpenInterestHandler _instance = new OptionOpenInterestHandler();

    private static final Logger logger=Logger.getLogger(OptionOpenInterestHandler.class.getName());

    public static OptionOpenInterestHandler getInstance()
    {
        return _instance;
    }

    private OptionOpenInterestHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
      MDMessage message=priceFeedMessage.getMessage();
      
      OptionOpenInterestMessage theMessage=null;

      if(logger.isTraceEnabled())
        {
            logger.trace("OptionOpenInterestHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(OptionOpenInterestMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market==null)
            {
               logger.error("Market Not Found while processing option open interest message : "+theMessage.toString());
            }
            else
            {
               MarketStatistics statistics=market.getStatistics();
               
               if(statistics!=null)
               {
                  synchronized(statistics)
                  {
                     statistics.setOpenInterest(theMessage.OpenInterest);
                     char[] oiDate = theMessage.OpenInterestDate;
                     if (oiDate!=null && oiDate.length>0 && oiDate[0]!='\0')
                     {
                        statistics.setOpenInterestDate(new String(oiDate));
                        market.marketStatsUpdated();
                     }
                  }
                  
                  if(logger.isTraceEnabled())
                    {
                     logger.trace("OptionOpenInterest Processed : "+statistics.toString());
                    }
               }
               else
               {
                  logger.error("Market Not Found while processing option open interest message : "+theMessage.toString());
               }
               
            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing OptionOpenInterest message: "+e.getMessage());
        }
    }
}

