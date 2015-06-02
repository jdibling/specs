package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.MarketStatisticsMessage;
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
public class MarketStatisticsHandler extends AbstractMarketMessageHandler
{
    private static MarketStatisticsHandler _instance = new MarketStatisticsHandler();

    private static final Logger logger=Logger.getLogger(MarketStatisticsHandler.class.getName());

    public static MarketStatisticsHandler getInstance()
    {
        return _instance;
    }

    private MarketStatisticsHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
    	MarketStatisticsMessage theMessage=null;

    	if(logger.isTraceEnabled())
        {
            logger.trace("MarketStatisticsHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(MarketStatisticsMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market==null)
            {
            	logger.error("Market Not Found while processing MarketStatistics message : "+theMessage.toString());
            }
            else
            {
            	MarketStatistics statistics=market.getStatistics();
            	
            	if(statistics!=null)
            	{
            		synchronized(statistics)
            		{
            			setMarketStatistics(statistics, theMessage);
            		}
                	
            		if(logger.isTraceEnabled())
                    {
                    	logger.trace("MarketStatistics Processed : "+statistics.toString());
                    }
                    market.marketStatsUpdated();
            	}
            	else
            	{
                   if (market.isOptionMarket() || market.isUDSMarket())
                   {
                	   //for new UDS or flex options markets, there might not be market statistics that are obtained thru market snapshots.
                	   //create new market statistics in such case
                	   statistics = new MarketStatistics();
                	   setMarketStatistics(statistics, theMessage);
                	   market.setStatistics(statistics);
                	   market.marketStatsUpdated();
                   }
                   else
                   {
                	   logger.error("Statistics null while processing market statistics message : "+theMessage.toString());
                   }
            	}
            	
            }

        }
        catch(Exception e)
        {
            logger.error("Failure processing MarketStatistics message: "+e.getMessage());
        }
    }
    
    private void setMarketStatistics(MarketStatistics statistics, MarketStatisticsMessage theMessage)
    {
		statistics.setEfpVolume(theMessage.EFPVolume);
		statistics.setEfsVolume(theMessage.EFSVolume);
		statistics.setTotalVolume(theMessage.TotalVolume);
		statistics.setBlockVolume(theMessage.BlockVolume);
		statistics.setHigh(theMessage.High);
		statistics.setLow(theMessage.Low);
		statistics.setVwap(theMessage.VWAP);    	
    }
}

