package com.theice.mdf.client.process.handlers;

import java.util.HashMap;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.TradeMessage;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.Trade;
import com.theice.mdf.client.domain.transaction.TradeTransaction;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

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
public class TradeMessageHandler extends AbstractMarketMessageHandler
{
    private static TradeMessageHandler _instance = new TradeMessageHandler();
    
    private static final HashMap<Character,String> OffExchangeDealTradeTypeMap = new HashMap<Character,String>();

    private static final Logger logger = Logger.getLogger(TradeMessageHandler.class.getName());

    static
    {
       OffExchangeDealTradeTypeMap.put('K', "Block");
       OffExchangeDealTradeTypeMap.put('E', "EFP");
       OffExchangeDealTradeTypeMap.put('S', "EFS");
       OffExchangeDealTradeTypeMap.put('V', "Bilateral Off-Exchange");
       OffExchangeDealTradeTypeMap.put('O', "NG EFP/EFS");
       OffExchangeDealTradeTypeMap.put('9', "CCX EFP");
       OffExchangeDealTradeTypeMap.put('J', "EFR");
       OffExchangeDealTradeTypeMap.put('Q', "EOO");
       OffExchangeDealTradeTypeMap.put('I', "EFM");
    }
    
    public static TradeMessageHandler getInstance()
    {
        return _instance;
    }

    private TradeMessageHandler()
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
    	
        TradeMessage theMessage = null;

        if(logger.isTraceEnabled())
        {
            logger.trace("TradeMessageHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(TradeMessage) message;

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
                
                if(market.isOptionMarket())
                {
                	trade=new Trade(theMessage,true);
                }
                else
                {
                	trade=new Trade(theMessage);
                }
                	
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
            logger.error("Failure processing TradeMessage: " + e.toString());
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

