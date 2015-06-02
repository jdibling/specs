package com.theice.mdf.client.domain;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 */
public class PriceLevelSnapshotLogGenerator
{
   //all instance variables are only accessed by one thread
   static Logger logger=Logger.getLogger(PriceLevelSnapshotLogGenerator.class);
   //static Logger snapshotLogger=Logger.getLogger("com.theice.mdf.client.multicast.SnapshotPriceLevelLogger");
   private static final SimpleDateFormat DateTimeFormatterGMT=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
   //private static final SimpleDateFormat DateTimeFormatter=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
   private static final int DIVIDERARRAYSIZE=10;
   private static final double[] DIVIDERARRAY = new double[DIVIDERARRAYSIZE];
   private static final DecimalFormat[] FORMATTERARRAY = new DecimalFormat[DIVIDERARRAYSIZE];
   private static final String LINEBREAK = System.getProperty("line.separator");
   //private Set<Integer> _missingPackets=new HashSet<Integer>();
   //private MulticastReceiver _receiver=null;
   //private StringBuilder _logBuffer = new StringBuilder();
   //private boolean _keepRunning=true;
   //private int _marketIDLastProcessed=0;
   //private int _marketOrdPriceDenominator=-1;
   //private int _remainingNumOfOrderEntries=0;
   //private final Calendar _MSGDATETIME=Calendar.getInstance(); //reused for every single message
   //private long[] _startTimesMillis=null;
   //private long[] _endTimesMillis=null;
   //private MarketInterface _market=null;
   //private OptionMarket _optionMarket=null;

   static
   {
      for (int i=0; i<DIVIDERARRAYSIZE; i++)
      {
         DIVIDERARRAY[i] = Math.pow(10, i);
         DecimalFormat f = new DecimalFormat("0.0");
         f.setMinimumFractionDigits(i);
         f.setMaximumFractionDigits(i);
         FORMATTERARRAY[i] = f;
      }

      DateTimeFormatterGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
   }

   public static String buildLogString(long timestamp, char side, PriceLevel[] priceLevels, short numOfPriceLevelLogged, MarketInterface market)
   {
      if (priceLevels == null || priceLevels.length == 0 || market == null)
      {
         logger.error("No order exists: MarketID="+market.getMarketID()+" side="+side);
         return null;
      }

      int orderPriceDenominator = 1;
      char securityType = 'F';
      int optionUnderlyingMarketID = -1;
      long optionStrikePrice = 0;
      char optionType = ' ';
      int optionStrikePriceDenominator=1;

      try
      {   
         orderPriceDenominator=Character.digit(market.getOrderPriceDenominator(),10);
      }
      catch(Exception ex)
      {
         logger.error("Error getting OrderPriceDenominator for marketID="+market.getMarketID()+", orderPriceDenominator="+market.getOrderPriceDenominator());
      }

      if (market.isOptionMarket())
      {
         OptionMarket optionMarket=(OptionMarket)market;
         try
         {
            optionStrikePriceDenominator=Character.digit(optionMarket.getNumDecimalsStrikePrice(),10);
         }
         catch(Exception ex)
         {
            logger.error("Error getting NumDecimalStrikePrice for marketID="+optionMarket.getMarketID()+", numDecimalStrikePrice="+optionMarket.getNumDecimalsStrikePrice());
         }

         securityType = 'O';
         optionUnderlyingMarketID=optionMarket.getUnderlyingMarket().getMarketID();
         optionStrikePrice=optionMarket.getStrikePrice();
         optionType=optionMarket.getOptionType();
      }

      StringBuilder logBuffer = new StringBuilder();
      for (int i=0; i<priceLevels.length; i++)
      {
         //do not rely on PriceLevel.getPosition() because when we add or remove price level from the book,
         //we do not update the 'position' of every single PriceLevel in the _bids or _offers list.
         //the Pirce Level list itself it correct. But the 'position' within each PriceLevel might not be accurate
         int priceLevelPosition = i+1;
         
         if (priceLevelPosition > numOfPriceLevelLogged)
         {
            break;
         }
         
         if (i > 0)
         {
            logBuffer.append(LINEBREAK);
         }
         
         PriceLevel pl = priceLevels[i];
         
         logBuffer.append(DateTimeFormatterGMT.format(timestamp));
         logBuffer.append("|"+market.getMarketID());
         logBuffer.append("|"+side);
         logBuffer.append("|"+priceLevelPosition);
         logBuffer.append("|"+FORMATTERARRAY[orderPriceDenominator].format(pl.getPrice()/DIVIDERARRAY[orderPriceDenominator]));//use lookup table for better performance
         logBuffer.append("|"+pl.getQuantity());
         logBuffer.append("|"+pl.getOrderCount());
         logBuffer.append("|"+pl.getImpliedQuantity());
         logBuffer.append("|"+pl.getImpliedOrderCount());
         logBuffer.append("|"+securityType);
         logBuffer.append("|");

         if (optionUnderlyingMarketID != -1)
         {
            logBuffer.append(optionUnderlyingMarketID);
         }
         logBuffer.append("|");

         if (securityType=='O')
         {
            logBuffer.append(FORMATTERARRAY[optionStrikePriceDenominator].format(optionStrikePrice/DIVIDERARRAY[optionStrikePriceDenominator]));
         }

         logBuffer.append("|");
         if (optionType != ' ')
         {
            logBuffer.append(optionType);
         }
      }
      
      return logBuffer.toString();

   }

}

