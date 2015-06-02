package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.OpenInterestMessage;
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
public class OpenInterestHandler extends AbstractMarketMessageHandler
{
    private static OpenInterestHandler _instance = new OpenInterestHandler();

    private static final Logger logger=Logger.getLogger(OpenInterestHandler.class.getName());

    public static OpenInterestHandler getInstance()
    {
        return _instance;
    }

    private OpenInterestHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	OpenInterestMessage theMessage=null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("OpenInterestHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(OpenInterestMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market==null)
            {
            	logger.error("Market Not Found while processing open interest message : "+theMessage.toString());
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
                    	logger.trace("OpenInterest Processed : "+statistics.toString());
                    }
            	}
            	else
            	{
                	logger.error("Market Not Found while processing open interest message : "+theMessage.toString());
            	}
            	
            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing OpenInterest message: "+e.getMessage());
        }
    }
}

