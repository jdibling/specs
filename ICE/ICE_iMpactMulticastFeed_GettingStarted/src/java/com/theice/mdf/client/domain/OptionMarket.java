package com.theice.mdf.client.domain;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.book.PriceLevelBook;
import com.theice.mdf.client.domain.transaction.TradeTransaction;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.notification.NewOptionStrategyDefinitionMessage;
import com.theice.mdf.message.notification.NewOptionsMarketDefinitionMessage;
import com.theice.mdf.message.response.OptionStrategyDefinitionResponse;
import com.theice.mdf.message.response.OptionStrategyHedgeDefinition;
import com.theice.mdf.message.response.OptionStrategyLegDefinition;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Represents the Option Market
 * For Option Markets, we keep only the Top Of Book (i.e. Price Level Only Book)
 * 
 * @author : Adam Athimuthu
 */
public class OptionMarket extends AbstractMarketBase
{
    private static final Logger logger=Logger.getLogger(OptionMarket.class.getName());
    
    /**
     * The source of the market information
     * Source: Options Product Definition (p)
     * Updates: Market State Change (K) 
     */
    protected OptionsProductDefinitionResponse _source=null;
    protected OptionStrategyDefinitionResponse _udsSourceFromTcp=null;
    protected NewOptionStrategyDefinitionMessage _udsSourceFromMC=null;
    protected NewOptionsMarketDefinitionMessage _flexOptionsSource=null;
    
    /**
     * Option type and strike price are set only if this is an option order.
     */
    private OptionMarketKey _key;
    private MarketInterface _underlyingMarket=null;
    private boolean _isUDSMarket=false;
    
    private OptionMarket()
    {
    }

    /**
     * Option Market constructor
     * @param optionsProductDefinition
     * @param underlyingMarket
     */
    public OptionMarket(OptionsProductDefinitionResponse optionsProductDefinition, MarketInterface underlyingMarket)
    {
    	this._source=optionsProductDefinition;
    	this._key=new OptionMarketKey(optionsProductDefinition.MarketID);
    	this._underlyingMarket=underlyingMarket;
    	
    	this._book=new PriceLevelBook(MarketInterface.NUMBER_OF_PRICELEVELS_OPTIONS);
    }
    
    public OptionMarket(OptionStrategyDefinitionResponse udsProductDefinition, MarketInterface underlyingMarket)
    {
      this._udsSourceFromTcp=udsProductDefinition;
      this._isUDSMarket=true;
      this._key=new OptionMarketKey(udsProductDefinition.MarketID);
      this._underlyingMarket=underlyingMarket;
      
      this._book=new PriceLevelBook(MarketInterface.NUMBER_OF_PRICELEVELS_OPTIONS);
    }
    
    public OptionMarket(NewOptionStrategyDefinitionMessage newUDSMarketDefinition, MarketInterface underlyingMarket)
    {
      this._udsSourceFromMC=newUDSMarketDefinition;
      this._isUDSMarket=true;
      this._key=new OptionMarketKey(newUDSMarketDefinition.getMarketID());
      this._underlyingMarket=underlyingMarket;
      
      this._book=new PriceLevelBook(MarketInterface.NUMBER_OF_PRICELEVELS_OPTIONS);
    }
    
    public OptionMarket(NewOptionsMarketDefinitionMessage flexOptionsMarket, MarketInterface underlyingMarket)
    {
      this._flexOptionsSource=flexOptionsMarket;
      this._key=new OptionMarketKey(flexOptionsMarket.getMarketID());
      this._underlyingMarket=underlyingMarket;
      
      this._book=new PriceLevelBook(MarketInterface.NUMBER_OF_PRICELEVELS_OPTIONS);
    }


    public synchronized void initialize()
    {
    	StringBuffer message=new StringBuffer("Initializing OptionsMarket (orders/states/book/remove option markets)...: ").append(getMarketID());
    	logger.info(message.toString());
    	
        _state.initialize();
        _book.initialize();
    }
    
    public MarketKey getMarketKey()
    {
    	return(this._key);
    }

    public int getMarketID() 
	{
		return(_key.getMarketID());
	}
	
	public char getOptionType() 
	{
		char optionType=' ';
	   if (_source!=null)
		{
		   optionType = _source.OptionType;
		}
		else if (_udsSourceFromTcp!=null || _udsSourceFromMC!=null)
		{
		   optionType =  'U';
		}
		else if (_flexOptionsSource!=null)
		{
		   optionType = _flexOptionsSource.OptionType;
		}
		
	   return optionType;
	}
	
	public String getOptionTypeString() 
	{
		String optionTypeString = "";
		if (getOptionType()==OptionsProductDefinitionResponse.OPTION_TYPE_CALL)
		{
		   optionTypeString = "Call";
		}
		else if (getOptionType()==OptionsProductDefinitionResponse.OPTION_TYPE_PUT)
		{
		   optionTypeString = "Put";
		}
		else if (getOptionType() == 'U')
		{
		   optionTypeString = "UDS";
		}
		
		return optionTypeString;
	}

	public String getMarketDesc() 
	{
	   String marketDesc="";
	   if (_source!=null)
	   {
	      marketDesc = MessageUtil.toString(this._source.MarketDesc);
	   }
	   else if (_udsSourceFromTcp!=null)
	   {
	      marketDesc = "Production Definition - User Definition Strategy)";
	   }
	   else if (_udsSourceFromMC!=null)
	   {
	      marketDesc = "New Market - User Definition Strategy";
	   }
	   else if (_flexOptionsSource!=null)
	   {
	      marketDesc = MessageUtil.toString(this._flexOptionsSource.MarketDesc);
	   }
	   
	   return marketDesc;
	}

	public boolean isOptionMarket() 
	{
		return(true);
	}
	
	public boolean isUDSMarket()
	{
	   return _isUDSMarket;
	}

	public long getStrikePrice() 
	{
		long strikePrice=0;
		if (_source!=null)
		{
		   strikePrice=_source.StrikePrice;
		}
		else if (_flexOptionsSource!=null)
		{
		   strikePrice=_flexOptionsSource.StrikePrice;
		}
	   
		return strikePrice;
	}
	
	public String getContractSymbol() 
	{
		String contractSymbol="";
		if (_source!=null)
		{
		   contractSymbol = MessageUtil.toString(_source.ContractSymbol) + MessageUtil.toString(_source.ContractSymbolExtra);
		}
		else if (_flexOptionsSource!=null)
		{
		   contractSymbol = MessageUtil.toString(_flexOptionsSource.ContractSymbol);
		}
	   
		return contractSymbol;
	}

    public short getMarketType()
    {
       short marketType = 0;
       if (_source!=null)
       {
          marketType = _source.RequestMarketType;
       }
       else if (_udsSourceFromTcp!=null)
       {
          marketType = _udsSourceFromTcp.RequestMarketType;
       }
       else if (_udsSourceFromMC!=null)
       {
          marketType = _underlyingMarket.getMarketType();
       }
       else if (_flexOptionsSource!=null)
       {
          marketType = _underlyingMarket.getMarketType();
       }
       
       return marketType;
    }
    
    /**
     * Get order price denom
     * @return order price denom
     */
	public char getOrderPriceDenominator()
	{
		char d = ' ';
		if (_source!=null)
		{
		   d = _source.OrderPriceDenominator;
		}
		else if (_flexOptionsSource!=null)
      {
         d = _flexOptionsSource.OrderPriceDenominator;
      }
		else if (_udsSourceFromTcp!=null)
		{
		   d = _udsSourceFromTcp.OrderPriceDenominator;
		}
		else if (_udsSourceFromMC!=null)
		{
		   d = _udsSourceFromMC.OrderPriceDenominator;
		}
		
		return d;
	}
	
    /**
     * Get deal price denom
     * @return deal price denom
     */
	public char getDealPriceDenominator()
	{
		char d = ' ';
		if (_source!=null)
		{
		   d = _source.DealPriceDenominator;
		}
	   else if (_flexOptionsSource!=null)
	   {
	      d = _flexOptionsSource.DealPriceDenominator;
	   }
	   else if (_udsSourceFromTcp!=null)
		{
		   d = _udsSourceFromTcp.OrderPriceDenominator;
		}
		else if (_udsSourceFromMC!=null)
		{
		   d = _udsSourceFromMC.OrderPriceDenominator;
		}

	   return d;
	}
	
	public char getSettlePriceDenominator()
	{
	   char d = ' ';
	   if (_source!=null)
	   {
	      d = _source.SettlePriceDenominator;
	   }
	   else if (_flexOptionsSource!=null)
	   {
	      d = _flexOptionsSource.DealPriceDenominator;
	   }
	   else if (_udsSourceFromTcp!=null)
	   {
	      d = _udsSourceFromTcp.OrderPriceDenominator;
	   }
	   else if (_udsSourceFromMC!=null)
	   {
	      d = _udsSourceFromMC.OrderPriceDenominator;
	   }

	   return d;
	}

    /**
     * Get number of decimals for options price (used for options markets)
     * @return options price denom
     */
	public char getNumDecimalsOptionsPrice()
	{
		return(_underlyingMarket.getNumDecimalsOptionsPrice());
	}
	
    /**
     * Get the number of decmials for strike price (used for options markets)
     * @return strike price denom
     */
	public char getNumDecimalsStrikePrice()
	{
		char d = ' ';
		if (_source!=null)
		{
		   d = _source.NumDecimalsStrikePrice;
		}
      else if (_flexOptionsSource!=null)
      {
         d = _flexOptionsSource.NumDecimalsStrikePrice;
      }
		else if (_udsSourceFromTcp!=null)
		{
		   d = _udsSourceFromTcp.OrderPriceDenominator;
		}
		else if (_udsSourceFromMC!=null)
		{
		   d = _udsSourceFromMC.OrderPriceDenominator;
		}
	   
		return d;
	}
	
	public long getMinOptionsPrice()
	{
	   long value=0;
	   if (_source!=null)
      {
         value=_source.MinOptionsPrice;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.MinOptionsPrice;
      }
     
	   return value;
	}
	
	public long getMaxOptionsPrice()
	{
	   long value=0;
	   if (_source!=null)
      {
         value=_source.MaxOptionsPrice;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.MaxOptionsPrice;
      }
      
	   return value;
	}
	
	public int getIncrementPremiumPrice()
	{
	   int value=0;
	   if (_source!=null)
      {
         value=_source.IncrementPremiumPrice;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.IncrementPremiumPrice;
      }
      else if (_udsSourceFromTcp!=null)
      {
         value=_udsSourceFromTcp.IncrementPremiumPrice;
      }
      else if (_udsSourceFromMC!=null)
      {
         value=_udsSourceFromMC.IncrementPremiumPrice;
      }
	   
	   return value;
	}
	
	public short getOptionsExpYear()
	{
	   short value=0;
	   if (_source!=null)
      {
         value=_source.OptionsExpirationYear;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.OptionsExpirationYear;
      }
     
	   return value;
	}
	
	public short getOptionsExpMonth()
	{
	   short value=0;
	   if (_source!=null)
      {
         value=_source.OptionsExpirationMonth;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.OptionsExpirationMonth;
      }
      
	   return value;
	}
	
	public short getOptionsExpDay()
	{
	   short value=0;
	   if (_source!=null)
      {
         value=_source.OptionsExpirationDay;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.OptionsExpirationDay;
      }
      
	   return value;
	}
	
	public char getOptionsSettlementType()
	{
	   char value=' ';
	   if (_source!=null)
      {
         value=_source.OptionsSettlementType;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.OptionsSettlementType;
      }
      
	   return value;
	}

	public char getOptionsExpirationType()
	{
	   char value=' ';
	   if (_source!=null)
      {
         value=_source.OptionsExpirationType;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.OptionsExpirationType;
      }
      
	   return value;
	}
	
	public int getSerialUnderlyingMarketID()
	{
	   int value=-1;
	   if (_source!=null)
      {
         value=_source.SerialUnderlyingMarketID;
      }
      else if (_flexOptionsSource!=null)
      {
         value=_flexOptionsSource.SerialUnderlyingMarketID;
      }
      
	   return value;
	}
	 /**
	  * get underlying market. For option markets, return the regular market that holds the option market
	  * @return Market
	  */
	public MarketInterface getUnderlyingMarket()
	{
		return(_underlyingMarket);
	}

    /**
     * handle option trade
     * @param optionTradeMessage
     * @return removed order. Always null for options markets
     * This is because, options markets don't have the full order depth book. So there is nothing to remove
     */
    public MarketOrder handleTrade(TradeTransaction transaction)
    {
    	Trade trade=transaction.getTrade();
    	
		if(trade.getTradeMessage().Quantity>0 || trade.getTradeMessage().Price>0)
    	{
    		_state.processTrade(trade);
    	}

    	return(null);
    }
    
    /**
     * Process market state change
     * @param tradingStatus
     */
    public synchronized void handleStateChange(char tradingStatus)
    {
    	if(tradingStatus!=' ')
    	{
    		if (_source!=null)
    		{
    		   _source.TradingStatus=tradingStatus;
    		}
    		else if (_udsSourceFromTcp!=null)
         {
    		   _udsSourceFromTcp.TradingStatus=tradingStatus;
         }
    		else if (_udsSourceFromMC!=null)
    		{
    		   _udsSourceFromMC.TradingStatus=tradingStatus;
    		}
    	}
    }

    /**
	 * No dependent markets for an option market
	 * @return always return false (not applicable)
	 */
	public synchronized boolean resetDependentMarketsUpdatedIfTrue()
	{
	    return(false);
	}

    /**
     * Get the source options product definition message
     * @return
     */
    public OptionsProductDefinitionResponse getOptionsSource()
    {
       return _source;
    }

    public OptionStrategyDefinitionResponse getUDSSourceFromTcp()
    {
       return _udsSourceFromTcp;
    }
    
    public NewOptionStrategyDefinitionMessage getUDSSourceFromMC()
    {
       return _udsSourceFromMC;
    }
    
    public NewOptionsMarketDefinitionMessage getFlexOptionsSource()
    {
       return _flexOptionsSource;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
       StringBuffer buf = new StringBuffer();
       if (_isUDSMarket)
       {
          buf.append("UDSMarket=").append(_key.toString()).append("]");
          buf.append("[UnderlyingMarket=").append(this.getUnderlyingMarket()).append("]");
          if (_udsSourceFromTcp!=null)
          {
             buf.append("[UDSProductDefinition=" +this._udsSourceFromTcp.toString()+ "]");
          }
          else if (_udsSourceFromMC!=null)
          {
             buf.append("[NewUDSMarket="+this._udsSourceFromMC.toString() + "]");
          }
          buf.append("\n");
          buf.append(_state.toString());
          buf.append("\n");
       }
       else
       {
          if (this._source!=null)
          {
             buf.append("OptionMarket=").append(_key.toString()).append("]");
             buf.append("[UnderlyingMarket=").append(this.getUnderlyingMarket()).append("]");
             buf.append("[OptionsProductDefinition=" +this._source.toString()+ "]");
             buf.append("\n");
             buf.append(_state.toString());
             buf.append("\n");
          }
          else if (this._flexOptionsSource!=null)
          {
             buf.append("OptionMarket=").append(_key.toString()).append("]");
             buf.append("[UnderlyingMarket=").append(this.getUnderlyingMarket()).append("]");
             buf.append("[FlexOptionsMarketDefinition=" +this._flexOptionsSource.toString()+ "]");
             buf.append("\n");
             buf.append(_state.toString());
             buf.append("\n");             
          }
       }
       
       return (buf.toString());
    }
    
    public String getShortUDSDesc()
    {
       StringBuffer buf = new StringBuffer();
       if (_isUDSMarket)
       {
          OptionStrategyLegDefinition[] legs = null;
          OptionStrategyHedgeDefinition[] hedges = null;
          
          buf.append("<html>MarketID=");
          buf.append(_key._marketID);
          buf.append("|UnderlyingMarket=").append(this.getUnderlyingMarket().getMarketID()).append("<br/>");
          
          if (_udsSourceFromTcp!=null)
          {
             
             legs = this._udsSourceFromTcp.LegDefinitions;
             hedges = this._udsSourceFromTcp.HedgeDefinitions;
          }
          else if (_udsSourceFromMC!=null)
          {
             legs = this._udsSourceFromMC.LegDefinitions;
             hedges = this._udsSourceFromMC.HedgeDefinitions;
          }
       
          if (legs!=null && legs.length>0)
          {
             for(int i=0;i<legs.length;i++)
             {
                OptionStrategyLegDefinition leg = legs[i];
                buf.append("Leg-"+i+"="+leg.toString()+"<br/>");
             }
          }
          if (hedges!=null && hedges.length>0)
          {
             for(int i=0;i<hedges.length;i++)
             {
                OptionStrategyHedgeDefinition hedge = hedges[i];
                buf.append("Hedge-"+i+"="+hedge.toString()+"<br/>");
             }
          }
          buf.append("</html>");
       }
       return buf.toString();
    }

    public char getTradingStatus()
    {
       char value=' ';
       if (_source!=null)
       {
          value=_source.TradingStatus;
       }
       else if (_flexOptionsSource!=null)
       {
          value=_flexOptionsSource.TradingStatus;
       }
       else if (_udsSourceFromTcp!=null)
       {
          value=_udsSourceFromTcp.TradingStatus;
       }
       else if (_udsSourceFromMC!=null)
       {
          value=_udsSourceFromMC.TradingStatus;
       }
       return value;
 
    }
    
    public int getIncrementQty()
    {
       int value = 0;
       if (_source!=null)
       {
          value=_source.IncrementQty;
       }
       else if (_flexOptionsSource!=null)
       {
          value=_flexOptionsSource.IncrementQty;
       }
       else if (_udsSourceFromTcp!=null)
       {
          value=_udsSourceFromTcp.IncrementQty;
       }
       else if (_udsSourceFromMC!=null)
       {
          value=_udsSourceFromMC.IncrementQty;
       }
       
       return value;
    }
    
    public int getLotSize()
    {
       int value=0;
       if (_source!=null)
       {
          value=_source.LotSize;
       }
       else if (_flexOptionsSource!=null)
       {
          value=_flexOptionsSource.LotSize;
       }
       
       return value;
    }
    
    public int getMinQty()
    {
       int value=0;
       if (_source!=null)
       {
          value=_source.MinQty;
       }
       else if (_flexOptionsSource!=null)
       {
          value=_flexOptionsSource.MinQty;
       }
       else if (_udsSourceFromTcp!=null)
       {
          value=_udsSourceFromTcp.MinQty;
       }
       else if (_udsSourceFromMC!=null)
       {
          value=_udsSourceFromMC.MinQty;
       }
       
       return value;
    }
    
    public String getCurrency()
    {
       String value="USD";
       if (_source!=null)
       {
          value=MessageUtil.toString(_source.Currency);
       }
       else if (_flexOptionsSource!=null)
       {
          value=MessageUtil.toString(_flexOptionsSource.Currency);
       }
       
       return value;
    }
    
    public char getNumDecimalStrikePrice()
    {
       char value=' ';
       if (_source!=null)
       {
          value=_source.NumDecimalsStrikePrice;
       }
       else if (_flexOptionsSource!=null)
       {
          value=_flexOptionsSource.NumDecimalsStrikePrice;
       }
       else if (_udsSourceFromTcp!=null)
       {
          value=_udsSourceFromTcp.OrderPriceDenominator;
       }
       else if (_udsSourceFromMC!=null)
       {
          value=_udsSourceFromMC.OrderPriceDenominator;
       }
       
       return value;
    }
    
    
	/**
	 * Checks if this option market is empty
	 * @return flag indicating if this option market is empty
	 */
	public boolean isEmpty()
	{
		boolean flag=(_book.getBids().isEmpty() && _book.getOffers().isEmpty());
		return(flag);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((_key == null) ? 0 : _key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OptionMarket other = (OptionMarket) obj;
		if (_key == null) {
			if (other._key != null)
				return false;
		} else if (!_key.equals(other._key))
			return false;
		return true;
	}

}

