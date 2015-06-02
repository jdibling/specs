package com.theice.mdf.message;

import com.theice.mdf.message.response.FuturesStrategyLegDefinition;

public class FuturesStrategyDefinition
{
   public static final short CONTRACT_SYMBOL_LENGTH = 70;
   private int _marketID;
   private char[] _contractSymbol = new char[CONTRACT_SYMBOL_LENGTH];
   private char _tradingStatus;
   private char _orderPriceDenominator;
   private int _incrementPrice; 
   private int _incrementQty;
   private int _minQty;
   private FuturesStrategyLegDefinition[] _legDefinitions;
   
   public int getMarketID()
   {
      return _marketID;
   }
   public void setMarketID(int marketID)
   {
      _marketID = marketID;
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
   public int getIncrementPrice()
   {
      return _incrementPrice;
   }
   public void setIncrementPrice(int incrementPrice)
   {
      _incrementPrice = incrementPrice;
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
   public FuturesStrategyLegDefinition[] getLegDefinitions()
   {
      return _legDefinitions;
   }
   public void setLegDefinitions(FuturesStrategyLegDefinition[] legDefinitions)
   {
      _legDefinitions = legDefinitions;
   }
   
   public int getNumberOfLegDefinitions()
   {
      return _legDefinitions==null? 0 : _legDefinitions.length;
   }
}
