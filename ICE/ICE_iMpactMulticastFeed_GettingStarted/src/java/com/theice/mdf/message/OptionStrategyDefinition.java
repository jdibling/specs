package com.theice.mdf.message;

import com.theice.mdf.message.response.OptionStrategyHedgeDefinition;
import com.theice.mdf.message.response.OptionStrategyLegDefinition;

public class OptionStrategyDefinition
{
   public static final short CONTRACT_SYMBOL_LENGTH = 35;
   private int _marketID;
   private int _underlyingMarketID;
   private char[] _contractSymbol = new char[CONTRACT_SYMBOL_LENGTH];
   private char _tradingStatus;
   private char _orderPriceDenominator;
   private int _incrementPremiumPrice; 
   private int _incrementQty;
   private int _minQty;
   private OptionStrategyLegDefinition[] _legDefinitions;
   private OptionStrategyHedgeDefinition[] _hedgeDefinitions;
   
   public int getMarketID()
   {
      return _marketID;
   }
   public void setMarketID(int marketID)
   {
      _marketID = marketID;
   }
   public int getUnderlyingMarketID()
   {
      return _underlyingMarketID;
   }
   public void setUnderlyingMarketID(int underlyingMarketID)
   {
      _underlyingMarketID = underlyingMarketID;
   }
   public char[] getContractSymbol()
   {
      return _contractSymbol;
   }
   public void setContractSymbol(char[] contractSymbol)
   {
      _contractSymbol = contractSymbol;
   }
   public char getTradingStatus()
   {
      return _tradingStatus;
   }
   public void setTradingStatus(char tradingStatus)
   {
      _tradingStatus = tradingStatus;
   }
   public char getOrderPriceDenominator()
   {
      return _orderPriceDenominator;
   }
   public void setOrderPriceDenominator(char orderPriceDenominator)
   {
      _orderPriceDenominator = orderPriceDenominator;
   }
   public int getIncrementPremiumPrice()
   {
      return _incrementPremiumPrice;
   }
   public void setIncrementPremiumPrice(int incrementPremiumPrice)
   {
      _incrementPremiumPrice = incrementPremiumPrice;
   }
   public int getIncrementQty()
   {
      return _incrementQty;
   }
   public void setIncrementQty(int incrementQty)
   {
      _incrementQty = incrementQty;
   }
   public int getMinQty()
   {
      return _minQty;
   }
   public void setMinQty(int minQty)
   {
      _minQty = minQty;
   }
   public OptionStrategyLegDefinition[] getLegDefinitions()
   {
      return _legDefinitions;
   }
   public void setLegDefinitions(OptionStrategyLegDefinition[] legDefinitions)
   {
      _legDefinitions = legDefinitions;
   }
   public OptionStrategyHedgeDefinition[] getHedgeDefinitions()
   {
      return _hedgeDefinitions;
   }
   public void setHedgeDefinitions(OptionStrategyHedgeDefinition[] hedgeDefinitions)
   {
      _hedgeDefinitions = hedgeDefinitions;
   }
   
   public int getNumberOfLegDefinitions()
   {
      return _legDefinitions==null? 0 : _legDefinitions.length;
   }
     
   public int getNumberOfHedgeDefinitions()
   {
      return _hedgeDefinitions==null? 0 : _hedgeDefinitions.length;
   }
   
}
