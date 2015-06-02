package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.AddModifyOrderMessage;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientRuntimeParameters;
import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.util.MDFUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * This handler is used for processing the Add/Modify Order messages.
 * These messages are used to build the book for both the bid/offer sides. The
 * collections within the corresponding markets are updated.
 * 
 * Context: FullOrderDepth
 * Channels: Snapshot and Incremental
 * Markets: Futures/OTC only (No Option Markets, as they don't support FullOrderDepth)
 * ----------------------------------
 * 
 * This is the primary place where an Order gets created within a given market.
 * Applicable only during FullOrderDepth processing. If we are just interested in price levels,
 * then this handler is not used. Instead the AddPriceLevel message will be used. In those cases,
 * the market's book will have a model whereby it keeps just the price level information and not
 * all the individual orders.
 * 
 * If this order was part of a bundle, then it is marked with the sequence number
 * This is useful when we detect crossed book conditions and whether it was triggered
 * and cleared by a bundle (or) outside of a bundle
 * 
 * only applicable for Futures/OTC) markets (not for options)
 * 
 * <p/> THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES
 * ONLY. THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * @see FullOrderDepthMulticasterDispatcher and associated factories
 * 
 * Date: Aug 10, 2007
 * Time: 2:55:59 PM
 */
public class AddModifyOrderHandler extends AbstractMarketMessageHandler 
{
   private static final Logger LOGGER = Logger.getLogger(AddModifyOrderHandler.class.getName());
   private static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
   private static AddModifyOrderHandler _instance = new AddModifyOrderHandler();
   private static final char SEPARATOR = ',';
   //private static final Logger defaultBestPriceLogger = Logger.getLogger("com.theice.mdf.client.domain.marketstate.priceLogger");
   private static final boolean logBestPrice = MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters().isBestPriceLogEnabled();
   private static HashMap<Short, Logger> bestPriceLoggers = null;
   private static DecimalFormat[] numberFormatters = null;
   
   static
   {
      if (logBestPrice)
      {
         MDFClientRuntimeParameters parameters = MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters();
         String[] _interestedMarkets = parameters.getBestPriceLogFiles();
         String _maxFileSize = parameters.getBestPriceLogMaxSize();
         bestPriceLoggers = getBestPriceLoggers(_interestedMarkets, _maxFileSize);
         dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
         numberFormatters = new DecimalFormat[11];
         String pattern = "0";
         for (int i=0;i<=10;i++)
         {
            if (i==1)
            {
               pattern += ".0";
            }
            else if (i>1)
            {
               pattern += "0";
            }
            numberFormatters[i] = new DecimalFormat(pattern);
         }
      }
   }
   
   /**
    * get the singleton instance
    * 
    * @return
    */
   public static AddModifyOrderHandler getInstance() 
   {
      return _instance;
   }

   private AddModifyOrderHandler() 
   {
   }

   /**
    * handle the Add/Modify order message An order is added/modified within the
    * book. Based on the 'Side', the order ends up in the bid/offer side within
    * the book.
    * 
    * @param message
    */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
      MDMessage message=priceFeedMessage.getMessage();
      
        AddModifyOrderMessage theMessage=null;

        if(LOGGER.isTraceEnabled())
        {
            LOGGER.trace("AddModifyOrderHandler.handleMessage() : Entering ["+message.toString()+"]");
        }

        /**
       * Get the correct market and store the bid/offer in the map
       */
        try
        {
            theMessage=(AddModifyOrderMessage) message;
            
            /*
            if (SIMULATECROSSEDBOOK)
            {
               simulateCrossedBook(theMessage);
            }
            */
            
            /**
             * Filter out RFQ orders
             */
            if(theMessage.IsRFQ=='Y')
            {
                if(LOGGER.isDebugEnabled())
                {
                  LOGGER.debug("Filtering out RFQ Order.");
                }
                return;
            }

            /**
             * This can't be an options market as they don't support FullOrderDepth
             * So, this message has to be for an underlying Futures/OTC market
             */
            Market market=(Market) MarketsHolder.getInstance().findMarket(theMessage.getMarketID());
            
            if(market!=null)
            {
                MarketOrder marketOrder=new MarketOrder(
                     theMessage.getMarketID(),
                     theMessage.OrderID,
                     theMessage.OrderSeqID,
                     theMessage.Side,
                     theMessage.Price,
                     theMessage.Quantity,
                     theMessage.Implied,
                     theMessage.IsRFQ,
                     theMessage.DateTime,
                     theMessage.getMessageType()==MDFClientConfigurator.getMsgTypeForAMOrder(),
                     theMessage.SequenceWithinMillis
                     );
                
                marketOrder.setIsModifyOrder(theMessage.IsModifyOrder);
                                
                if (theMessage.Implied != 'Y' && logBestPrice) //do not log if it is a implied order
                {
                   Logger logger = bestPriceLoggers.get(market.getSource().RequestMarketType);
                   if (logger != null)
                   {
                      logBestPrice(market, marketOrder, logger);
                   }
                }
                /**
                 * If bundled, set the non-zero bundle sequence number
                 */
                if(priceFeedMessage.isBundled())
                {
                    marketOrder.setBundleSequenceNumber(priceFeedMessage.getBundleSequenceNumber());
                }
                
                market.addOrder(new AddModifyTransaction(marketOrder,priceFeedMessage.getBundleSequenceNumber()));
            }
            else
            {
                LOGGER.error("FATAL ERROR! Market not found while processing add/modify orders : "+message.toString());
            }
        }
        catch(Exception e)
        {
            LOGGER.error("Failed processing AddModifyOrder: "+e.getMessage());
        }
    }
    
    private static Logger getLogger(String desc, String maxFileSize)
    {
       final String FILENAME = "_BestPrice.log";
       Logger logger = Logger.getLogger(desc);
       try
       {
          PatternLayout layout = new PatternLayout("%m%n");
          RollingFileAppender appender = new RollingFileAppender(layout, "./logs/"+desc+FILENAME);
          appender.setMaxFileSize(maxFileSize);
          appender.setMaxBackupIndex(10);
          logger.addAppender(appender);
          logger.setLevel(Level.toLevel("INFO"));
       }
       catch(Exception ex)
       {
          logger.error("Error generating logger: desc="+desc+" fileName="+FILENAME+" maxFileSize="+maxFileSize, ex);
          System.out.println("Error generating logger..");
       }
       return logger;
    }
    
    private static HashMap<Short, Logger> getBestPriceLoggers(String[] interestedMarkets, String maxFileSize)
    {
       HashMap<Short, Logger> loggers = new HashMap<Short, Logger>();
       if (interestedMarkets != null && interestedMarkets.length>0)
       {
          for(String market : interestedMarkets)
          {
             String[] _marketDesc = market.split(",");
             Short _mType = Short.parseShort(_marketDesc[0].trim());
             Logger _logger = getLogger(_marketDesc[1], maxFileSize);
             loggers.put(_mType, _logger);
          }
       }

       return loggers;
    }
    
    /**
     * Log current best bid/offer prices and related information when message is being handled.
     * Information is logged before current order is added to the book.
     *
     * @author scheng
     *
     * @param market
     * @param marketOrder
     */
    private void logBestPrice(Market market, MarketOrder marketOrder, Logger logger)
    {
       if (logger == null)
       {
          return;
       }
       
       MarketOrder order = null;
       boolean topPriceFound = false;
       long topBidPrice = 0L;
       long topOfferPrice = 0L;
       int topBidQty = 0; //aggregate bid qty if there are more than 1 bids with same top bid price
       int topOfferQty = 0; //aggregate offer qty if there are more than 1 offers with same top offer price
       int ticksBetterThanBestRestPrice = 0;
       int ticksAwayFromBestOpposePrice = 0;
       
       //bid prices that are n tick away from best bid. bidPriceTicksAway[0] will be = best bid. 
       long[] bidPriceTicksAway = new long[6]; //best bid price is kept in [0], prices at n tick away are in [n]
       long[] offerPriceTicksAway = new long[6];
       int[] qtyAtTicksAwayFromBestBid = new int[6];
       int[] qtyAtTicksAwayFromBestOffer = new int[6];
              
       java.util.Iterator<MarketOrder> orders = null; //sorted orders

       try
       {
          com.theice.mdf.message.response.ProductDefinitionResponse source = market.getSource();
          int marketIncrementPrice = source.IncrementPrice;
          int orderPriceDenominator = Character.digit(source.OrderPriceDenominator,10);
          int orderPriceBase = (int)Math.pow(10, orderPriceDenominator);
          DecimalFormat numberFormat = numberFormatters[orderPriceDenominator];
          
          if (marketIncrementPrice==0)
          {
             LOGGER.error("MarketIncrementPrice is 0. MarketID="+marketOrder.getMarketID());
             marketIncrementPrice=1; //unlikely
          }
          
          orders = market.getBids().iterator();
          while (orders.hasNext())
          {
             order = orders.next();
             long orderPrice = order.getPrice();
             int orderQty = order.getQuantity();
             if (orderQty==0) //for BRIX, order qty could be in fractions. e.g, 0.6. And it could be rounded down to 0.
             {
                orderQty=1;
             }
             if (!topPriceFound)
             {
                topBidPrice = orderPrice;
                topPriceFound = true;
                for (int i=0;i<6;i++)
                {
                   bidPriceTicksAway[i] = topBidPrice - (marketIncrementPrice*i);
                }
             }
             
             for (int i=0;i<6;i++)
             {
                if (orderPrice == bidPriceTicksAway[i])
                {
                   qtyAtTicksAwayFromBestBid[i] += orderQty;
                }
             }
          }
          
          topBidQty = qtyAtTicksAwayFromBestBid[0];
          
          orders = market.getOffers().iterator();
          topPriceFound = false;
          while (orders.hasNext())
          {
             order = orders.next();
             long orderPrice = order.getPrice();
             int orderQty = order.getQuantity();
             if (orderQty==0) //for BRIX, order qty could be in fractions. e.g, 0.6. And it could be rounded down to 0.
             {
                orderQty=1;
             }
             if (!topPriceFound)
             {
                topOfferPrice = orderPrice;
                topPriceFound = true;
                for (int i=0;i<6;i++)
                {
                   offerPriceTicksAway[i] = topOfferPrice + (marketIncrementPrice*i);
                }
             }
             
             for (int i=0;i<6;i++)
             {
                if (orderPrice == offerPriceTicksAway[i])
                {
                   qtyAtTicksAwayFromBestOffer[i] += orderQty;
                }
             }
          }
          
          topOfferQty = qtyAtTicksAwayFromBestOffer[0];

          LOGGER.debug("BestPriceLogger: marketID="+marketOrder.getMarketID()+", orderID="+marketOrder.getOrderID()+", marketIncrementPrice="+marketIncrementPrice+", orderPriceDenominator="+orderPriceDenominator+", orderPriceBase="+orderPriceBase);

          if (marketOrder.isBuy())
          {
             ticksBetterThanBestRestPrice = (int)(marketOrder.getPrice()-(topBidQty==0? marketOrder.getPrice():topBidPrice))/marketIncrementPrice;
             ticksAwayFromBestOpposePrice = (int)(marketOrder.getPrice()-(topOfferQty==0? marketOrder.getPrice():topOfferPrice))/marketIncrementPrice;
          }
          else
          {
             ticksBetterThanBestRestPrice = (int)((topOfferQty==0? marketOrder.getPrice():topOfferPrice)-marketOrder.getPrice())/marketIncrementPrice;
             ticksAwayFromBestOpposePrice = (int)((topBidQty==0? marketOrder.getPrice():topBidPrice)-marketOrder.getPrice())/marketIncrementPrice;
          }
                    
          //accumulated qty
          int qtyOneTickAwayFromBestBid = 0;
          int qtyTwoTickAwayFromBestBid = 0;
          int qtyFiveTickAwayFromBestBid = 0;
          int qtyOneTickAwayFromBestOffer = 0;
          int qtyTwoTickAwayFromBestOffer = 0;
          int qtyFiveTickAwayFromBestOffer = 0;
          
          int bidTempAccumulatedQty = 0;
          int offerTempAccumulatedQty = 0;
          
          //loop thru best price and prices that are within 5 ticks away
          for (int i=0; i<6; i++)
          {
             bidTempAccumulatedQty += qtyAtTicksAwayFromBestBid[i];
             offerTempAccumulatedQty += qtyAtTicksAwayFromBestOffer[i];
             if (i==1)
             {
                qtyOneTickAwayFromBestBid = bidTempAccumulatedQty;
                qtyOneTickAwayFromBestOffer = offerTempAccumulatedQty;
             }
             else if (i==2)
             {
                qtyTwoTickAwayFromBestBid = bidTempAccumulatedQty;
                qtyTwoTickAwayFromBestOffer = offerTempAccumulatedQty;
             }
             else if (i==5)
             {
                qtyFiveTickAwayFromBestBid = bidTempAccumulatedQty;
                qtyFiveTickAwayFromBestOffer = offerTempAccumulatedQty;
             }
          }
          
          java.util.Calendar now = java.util.Calendar.getInstance();
          StringBuffer msg = new StringBuffer(dateTimeFormatter.format(now.getTime())).
          append(SEPARATOR).append(dateTimeFormatter.format(new java.util.Date(marketOrder.getDateTime()))).
          append(SEPARATOR).append(marketOrder.getMarketID()).
          append(SEPARATOR).append(marketOrder.getOrderID()).
          append(SEPARATOR).append(marketOrder.getOrderSeqID()).
          append(SEPARATOR).append(marketOrder.getSide()).
          append(SEPARATOR).append(numberFormat.format((double)marketOrder.getPrice()/orderPriceBase)).
          append(SEPARATOR).append(marketOrder.getQuantity()).
          append(SEPARATOR).append(marketOrder.getImplied()).
          append(SEPARATOR).append(numberFormat.format((double)topBidPrice/orderPriceBase)).
          append(SEPARATOR).append(topBidQty).
          append(SEPARATOR).append(numberFormat.format((double)topOfferPrice/orderPriceBase)).
          append(SEPARATOR).append(topOfferQty).
          append(SEPARATOR).append(ticksBetterThanBestRestPrice).
          append(SEPARATOR).append(ticksAwayFromBestOpposePrice).
          append(SEPARATOR).append((topBidQty==0 || topOfferQty==0)? "" : numberFormat.format((double)(topOfferPrice-topBidPrice)/orderPriceBase)).
          append(SEPARATOR).append(qtyOneTickAwayFromBestBid).
          append(SEPARATOR).append(qtyTwoTickAwayFromBestBid).
          append(SEPARATOR).append(qtyFiveTickAwayFromBestBid).
          append(SEPARATOR).append(qtyOneTickAwayFromBestOffer).
          append(SEPARATOR).append(qtyTwoTickAwayFromBestOffer).
          append(SEPARATOR).append(qtyFiveTickAwayFromBestOffer);

          logger.log(Level.INFO, msg.toString());
          
       }
       catch (Throwable e)
       {
          LOGGER.error("Failed logging best price: ", e);
       }
    }    
    
    /**
     * Simulate crossed book randomly 
     * @param marketOrder
     * @return
     */
    protected AddModifyOrderMessage simulateCrossedBook(AddModifyOrderMessage order)
    {
       Random random=new Random();
       
       if(!MDFUtil.isBuy(order.Side))
       {
          return(order);
       }
       
       int randomNumber=random.nextInt(10000);
       
       if(randomNumber>=3 && randomNumber<=7)
       {
          order.Price+=100;
          
          LOGGER.info("### Crossed Book Simulation. About to increment the bid price : "+order.toString());
          
          try
          {
             Thread.sleep(1000);
          }
          catch(InterruptedException e) {}

          LOGGER.info("### Crossed Book Simulation. Incremented the bid price : "+order.toString());
       }
       else
       {
          LOGGER.info("### Crossed Book Simulation. Bid price unchanged : "+order.toString());
       }
       
       return(order);
    }    
   
}

