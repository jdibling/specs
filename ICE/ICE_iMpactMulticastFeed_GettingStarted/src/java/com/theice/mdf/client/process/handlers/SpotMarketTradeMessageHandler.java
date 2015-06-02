package com.theice.mdf.client.process.handlers;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.Trade;
import com.theice.mdf.client.domain.transaction.TradeTransaction;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.SpotMarketTradeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Trade message handler is used to process the trade messages. When a trade message is processed, we effectively
 * remove an order (if the order id matches an order internally held within a market).
 * 
 * Also, we keep track of the last 50 trades
 *
 * @author Adam Athimuthu
 * Date: Aug 10, 2007
 * Time: 3:35:33 PM
 */
public class SpotMarketTradeMessageHandler extends AbstractMarketMessageHandler
{
    private static SpotMarketTradeMessageHandler _instance = new SpotMarketTradeMessageHandler();
    
    private static final HashMap<Character,String> OffExchangeDealTradeTypeMap = new HashMap<Character,String>();

    private static final Logger logger = Logger.getLogger(SpotMarketTradeMessageHandler.class.getName());

    public static SpotMarketTradeMessageHandler getInstance()
    {
        return _instance;
    }

    private SpotMarketTradeMessageHandler()
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
    	
        SpotMarketTradeMessage theMessage = null;

        if(logger.isTraceEnabled())
        {
            logger.trace("SpotMarketTradeMessageHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(SpotMarketTradeMessage) message;

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());

            /**
             * Create the trade. Identify if the message was part of a bundle
             */
            if(market!=null)
            {
            	/**
            	 * If this is an optionsMarket, call the Trade constructor with isOptions set to true
            	 */
                Trade trade=null;

                //no options market in Spot
               	trade=new Trade(theMessage);
                	
                if(priceFeedMessage.isBundled())
                {
                    trade.setBundleSequenceNumber(priceFeedMessage.getBundleSequenceNumber());
                }

                market.handleTrade(new TradeTransaction(trade,priceFeedMessage.getBundleSequenceNumber()));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Failure processing SpotMarketTradeMessage: " + e.toString());
        }

        return;
        
    }
    
    public static String getOffExchangeDealTradeType(char tradeType)
    {
       String tradeTypeDesc = OffExchangeDealTradeTypeMap.get(tradeType);
       if (tradeTypeDesc==null || tradeTypeDesc.length()==0)
       {
          tradeTypeDesc =  "Unknown block trade type - "+tradeType;
       }
       
       return tradeTypeDesc;
    }

}

