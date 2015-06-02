package com.theice.mdf.client.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.domain.book.BookContext;
import com.theice.mdf.client.domain.transaction.TradeTransaction;
import com.theice.mdf.message.notification.EndOfDayMarketSummaryMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * An interface implemented by the regular markets and the option markets
 * 
 * TODO Trace all the isOptionMarket references to make sure the logic is okay for predefined options
 * 
 * @author : Adam Athimuthu
 */
public interface MarketInterface extends Serializable
{
   public final int _maxRecentTrades=50;

   public final int NUMBER_OF_PRICELEVELS=5;
   public final int NUMBER_OF_PRICELEVELS_OPTIONS=MDFClientConfigurator.getNumberOfPriceLevelsOptions();

   public void initialize();

   public int getMarketID();
   public String getMarketDesc();
   public short getMarketType();
   public String getContractSymbol();
   public boolean isOptionMarket();
   public boolean isUDSMarket();
   public char getOrderPriceDenominator();
   public char getDealPriceDenominator();

   public char getNumDecimalsOptionsPrice();
   public char getNumDecimalsStrikePrice();

   public MarketInterface getUnderlyingMarket();

   public Collection<MarketOrder> getBids();
   public Collection<MarketOrder> getOffers();

   /**
    * Common trade messages
    * Trade, EndOfDayMarketSummary
    */
   public MarketOrder handleTrade(TradeTransaction transaction);
   public void handleCancelTrade(long orderId);
   public void handleInvestigatedTrade(long orderId, char investigationStatus);
   public EndOfDayMarketSummaryMessage getEndOfDayMarketSummary();
   public void setEndOfDayMarketSummary(EndOfDayMarketSummaryMessage endOfDayMarketSummary);
   public void handleStateChange(char tradingStatus);

   /**
    * return the price level in the "natural" order based on the side
    * The price level array should be ready to be consumed
    */
   public PriceLevel[] getPriceLevels(char side);

   public MarketStatistics getStatistics();
   public void setStatistics(MarketStatistics statistics);
   public List<Trade> getRecentTrades();

   public MarketKey getMarketKey();
   public BookContext getBookContext();

   public boolean resetBookUpdatedIfTrue();
   public boolean resetDependentMarketsUpdatedIfTrue();
   public boolean resetOrderUpdatedIfTrue();
   public boolean resetTradeHistoryUpdatedIfTrue();
   public boolean resetMarketStatsUpdatedIfTrue();
   public void marketStatsUpdated();
   public void setGUIComponentsContext(GUIComponentsContext context);
   public GUIComponentsContext getGUIComponentsContext();
   public void updateGUIComponentsText(String str);
   public void resetGUIComponentsText();
   public List<MessageBucketInterface> getMessageCountList();
   public void setMessageCountList(List<MessageBucketInterface> list);
   public void dumpBookToLogger(long timestamp, short numOfPriceLevelLogged, Logger logger);
}

