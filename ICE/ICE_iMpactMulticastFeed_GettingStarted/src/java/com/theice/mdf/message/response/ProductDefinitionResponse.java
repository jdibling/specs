package com.theice.mdf.message.response;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

/**
 * ProductDefinitionResponse.java
 * @author David Chen
 */

public class ProductDefinitionResponse  extends Response implements Serializable
{
	private static final short MESSAGE_LENGTH = 532;

	public short RequestMarketType;
	public short NumOfMarkets;
	public int MarketID;		
	public char ContractSymbol[] = new char[35];
	public char TradingStatus;
	public char OrderPriceDenominator;
	public int IncrementPrice;
	public int IncrementQty;
	public int LotSize;		
	public char MarketDesc[] = new char[120];
	public short MaturityYear;
	public short MaturityMonth;
	public short MaturityDay;
	public char IsSpread;
	public char IsCrackSpread;
	public int PrimaryMarketID;
	public int SecondaryMarketID;
	public char IsOptions;
	public char OptionType;
	public long StrikePrice;
	public long SecondStrike;
   public char DealPriceDenominator;
   public int MinQty;
   public int UnitQuantity;
   public char Currency[] = new char[20];
   //added to support option
   public long MinStrikePrice;
   public long MaxStrikePrice;
   public int IncrementStrikePrice;  
   public char NumDecimalsStrikePrice;
   public long MinOptionsPrice;
   public long MaxOptionsPrice; 
   public int IncrementOptionsPrice;  
   public char NumDecimalsOptionsPrice;
   public long TickValue;
   public char AllowOptions;
   public char ClearedAlias[] = new char[15];
   public char AllowImplied;
   public short OptionsExpirationYear = -1;
   public short OptionsExpirationMonth = -1;
   public short OptionsExpirationDay = -1;
   public long MinPrice;
   public long MaxPrice;
   public short ProductID;
   public char ProductName[] = new char[62];
   public short HubID;
   public char HubAlias[] = new char[80];
   public short StripID;
   public char StripName[] = new char[39];
   public char ReservedField1;
   public char IsSerialOptionsSupported;
   public char IsTradable;
   public char SettlementPriceDenominator;
   public char MicCode[] = new char[4];
   public char UnitQtyDenominator = '0';


   public ProductDefinitionResponse()
   {
      MessageType = RawMessageFactory.ProductDefinitionResponseType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public int getMarketID()
   {
      return MarketID;
   }

   public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

			serializeHeader();
			SerializedContent.putInt( RequestSeqID );
			SerializedContent.putShort( RequestMarketType );
			SerializedContent.putShort( NumOfMarkets );
			SerializedContent.putInt( MarketID );
			for( int i=0; i<ContractSymbol.length  ; i++ )
			{
				SerializedContent.put( (byte)ContractSymbol[i] );
			}

			SerializedContent.put( (byte)TradingStatus );
			SerializedContent.put( (byte)OrderPriceDenominator );
			SerializedContent.putInt( IncrementPrice );
			SerializedContent.putInt( IncrementQty );
			SerializedContent.putInt( LotSize );
			for( int i=0; i<MarketDesc.length  ; i++ )
			{
				SerializedContent.put( (byte)MarketDesc[i] );
			}

			SerializedContent.putShort( MaturityYear );
			SerializedContent.putShort( MaturityMonth );
			SerializedContent.putShort( MaturityDay );
			SerializedContent.put( (byte)IsSpread );
			SerializedContent.put( (byte)IsCrackSpread );
			SerializedContent.putInt( PrimaryMarketID );
			SerializedContent.putInt( SecondaryMarketID );
			SerializedContent.put( (byte)IsOptions );
			SerializedContent.put( (byte)OptionType );
			SerializedContent.putLong( StrikePrice );
			SerializedContent.putLong( SecondStrike );

			SerializedContent.put( (byte)DealPriceDenominator );
	        SerializedContent.putInt( MinQty );
	        SerializedContent.putInt ( UnitQuantity );

			for( int i=0; i<Currency.length  ; i++ )
			{
				SerializedContent.put( (byte)Currency[i] );
			}
         
         SerializedContent.putLong( MinStrikePrice );
         SerializedContent.putLong( MaxStrikePrice );
         SerializedContent.putInt( IncrementStrikePrice );
         SerializedContent.put( (byte)NumDecimalsStrikePrice );
         SerializedContent.putLong( MinOptionsPrice );
         SerializedContent.putLong( MaxOptionsPrice );
         SerializedContent.putInt( IncrementOptionsPrice );
         SerializedContent.put( (byte)NumDecimalsOptionsPrice );
         SerializedContent.putLong( TickValue );
         SerializedContent.put( (byte)AllowOptions );
         for( int i=0; i<ClearedAlias.length  ; i++ )
         {
            SerializedContent.put( (byte)ClearedAlias[i] );
         }
         
         SerializedContent.put( (byte)AllowImplied );
         SerializedContent.putShort( OptionsExpirationYear );
         SerializedContent.putShort( OptionsExpirationMonth );
         SerializedContent.putShort( OptionsExpirationDay );

         SerializedContent.putLong( MinPrice );
         SerializedContent.putLong( MaxPrice );

         SerializedContent.putShort( ProductID );
			for( int i=0; i<ProductName.length  ; i++ )
			{
				SerializedContent.put( (byte)ProductName[i] );
			}

         SerializedContent.putShort( HubID );
			for( int i=0; i<HubAlias.length  ; i++ )
			{
				SerializedContent.put( (byte)HubAlias[i] );
			}

         SerializedContent.putShort( StripID );
			for( int i=0; i<StripName.length  ; i++ )
			{
				SerializedContent.put( (byte)StripName[i] );
			}

			SerializedContent.put((byte)ReservedField1);
			SerializedContent.put((byte)IsSerialOptionsSupported);
			SerializedContent.put((byte)IsTradable);
			SerializedContent.put((byte)SettlementPriceDenominator);
			
			for( int i=0; i<MicCode.length  ; i++ )
			{
				SerializedContent.put( (byte)MicCode[i] );
			}
			
			SerializedContent.put((byte)UnitQtyDenominator);
			
			SerializedContent.rewind();

         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
		}

		return SerializedContent.array();
	}

   public String getShortLogStr()
   {
      // too much for logging, and it is pretty static, just
      // return null
      return null;
   }

	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
		RequestMarketType = inboundcontent.getShort();
		NumOfMarkets = inboundcontent.getShort();
		MarketID = inboundcontent.getInt();

		for( int i=0; i<ContractSymbol.length  ; i++ )
		{
			ContractSymbol[i] = (char)inboundcontent.get();
		}
		TradingStatus = (char)inboundcontent.get();
		OrderPriceDenominator = (char)inboundcontent.get();
		IncrementPrice = inboundcontent.getInt();
		IncrementQty = inboundcontent.getInt();
		LotSize = inboundcontent.getInt();

		for( int i=0; i<MarketDesc.length  ; i++ )
		{
			MarketDesc[i] = (char)inboundcontent.get();
		}
		MaturityYear = inboundcontent.getShort();
		MaturityMonth = inboundcontent.getShort();
		MaturityDay = inboundcontent.getShort();
		IsSpread = (char)inboundcontent.get();
		IsCrackSpread = (char)inboundcontent.get();
		PrimaryMarketID = inboundcontent.getInt();
		SecondaryMarketID = inboundcontent.getInt();
		IsOptions = (char)inboundcontent.get();
		OptionType = (char)inboundcontent.get();
		StrikePrice = inboundcontent.getLong();
		SecondStrike = inboundcontent.getLong();

      DealPriceDenominator = (char)inboundcontent.get();
      MinQty = inboundcontent.getInt();
      UnitQuantity = inboundcontent.getInt();
		for( int i=0; i<Currency.length  ; i++ )
		{
			Currency[i] = (char)inboundcontent.get();
		}    
      MinStrikePrice = inboundcontent.getLong();
      MaxStrikePrice = inboundcontent.getLong();
      IncrementStrikePrice = inboundcontent.getInt();
      NumDecimalsStrikePrice = (char)inboundcontent.get();
      MinOptionsPrice = inboundcontent.getLong();
      MaxOptionsPrice = inboundcontent.getLong();
      IncrementOptionsPrice = inboundcontent.getInt();
      NumDecimalsOptionsPrice = (char)inboundcontent.get();
      TickValue = inboundcontent.getLong();
      AllowOptions = (char)inboundcontent.get();      
      for( int i=0; i<ClearedAlias.length  ; i++ )
      {
         ClearedAlias[i] = (char)inboundcontent.get();
      }
      AllowImplied = (char)inboundcontent.get();
      OptionsExpirationYear = inboundcontent.getShort();
      OptionsExpirationMonth = inboundcontent.getShort();
      OptionsExpirationDay = inboundcontent.getShort();

      MinPrice = inboundcontent.getLong();
      MaxPrice = inboundcontent.getLong();
        
      ProductID = inboundcontent.getShort();
      for( int i=0; i<ProductName.length  ; i++ )
      {
         ProductName[i] = (char)inboundcontent.get();
      }

      HubID = inboundcontent.getShort();
      for( int i=0; i<HubAlias.length  ; i++ )
      {
         HubAlias[i] = (char)inboundcontent.get();
      }

      StripID = inboundcontent.getShort();
      for( int i=0; i<StripName.length  ; i++ )
      {
         StripName[i] = (char)inboundcontent.get();
      }
            
      ReservedField1 = (char)inboundcontent.get();
      IsSerialOptionsSupported = (char)inboundcontent.get();
      IsTradable = (char)inboundcontent.get();
      
      SettlementPriceDenominator = (char)inboundcontent.get();
         
      if (inboundcontent.remaining()>0)
      {
          for( int i=0; i<MicCode.length  ; i++ )
          {
        	  MicCode[i] = (char)inboundcontent.get();
          }
      }
      
      if(inboundcontent.remaining()>0)
      {
         UnitQtyDenominator = (char)inboundcontent.get();
      }
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
        str.append(super.toString());
		str.append("RequestMarketType=");
		str.append(RequestMarketType);
		str.append( "|");
		str.append("NumOfMarkets=");
		str.append(NumOfMarkets);
		str.append( "|");
		str.append("MarketID=");
		str.append(MarketID);
		str.append( "|");
		str.append("ContractSymbol=");
		str.append(MessageUtil.toString(ContractSymbol));
		str.append( "|");
		str.append("TradingStatus=");
		str.append(TradingStatus);
		str.append( "|");
		str.append("OrderPriceDenominator=");
		str.append(OrderPriceDenominator);
		str.append( "|");
		str.append("IncrementPrice=");
		str.append(IncrementPrice);
		str.append( "|");
		str.append("IncrementQty=");
		str.append(IncrementQty);
		str.append( "|");
		str.append("LotSize=");
		str.append(LotSize);
		str.append( "|");
		str.append("MarketDesc=");
		str.append(MessageUtil.toString(MarketDesc));
		str.append( "|");
		str.append("MaturityYear=");
		str.append(MaturityYear);
		str.append( "|");
		str.append("MaturityMonth=");
		str.append(MaturityMonth);
		str.append( "|");
		str.append("MaturityDay=");
		str.append(MaturityDay);
		str.append( "|");
		str.append("IsSpread=");
		str.append(IsSpread);
		str.append( "|");
		str.append("IsCrackSpread=");
		str.append(IsCrackSpread);
		str.append( "|");
		str.append("PrimaryMarketID=");
		str.append(PrimaryMarketID);
		str.append( "|");
		str.append("SecondaryMarketID=");
		str.append(SecondaryMarketID);
		str.append( "|");
		str.append("IsOptions=");
		str.append(IsOptions);
		str.append( "|");
		str.append("OptionType=");
		str.append(OptionType);
		str.append( "|");
		str.append("StrikePrice=");
		str.append(StrikePrice);
		str.append( "|");
		str.append("DealPriceDenominator=");
		str.append(DealPriceDenominator);
		str.append( "|");
		str.append("MinQty=");
		str.append(MinQty);
		str.append( "|");
		str.append("UnitQuantity=");
		str.append(UnitQuantity);
		str.append( "|");
		str.append("Currency=");
		str.append(MessageUtil.toString(Currency));
		str.append( "|");
      str.append("MinStrikePrice=");
      str.append(MinStrikePrice);
      str.append( "|");
      str.append("MaxStrikePrice=");
      str.append(MaxStrikePrice);
      str.append( "|");
      str.append("IncrementStrikePrice=");
      str.append(IncrementStrikePrice);
      str.append( "|");
      str.append("NumDecimalsStrikePrice=");
      str.append(NumDecimalsStrikePrice);
      str.append( "|");
      str.append("MinOptionsPrice=");
      str.append(MinOptionsPrice);
      str.append( "|");
      str.append("MaxOptionsPrice=");
      str.append(MaxOptionsPrice);
      str.append( "|");
      str.append("IncrementOptionsPrice=");
      str.append(IncrementOptionsPrice);
      str.append( "|");
      str.append("NumDecimalsOptionsPrice=");
      str.append(NumDecimalsOptionsPrice);
      str.append( "|");
      str.append("TickValue=");
      str.append(TickValue);
      str.append( "|");    
      str.append("AllowOptions=");
      str.append(AllowOptions=='Y'?'Y':'N');
      str.append( "|");
      str.append("ClearedAlias=");
      str.append(MessageUtil.toString(ClearedAlias));
      str.append( "|");
      str.append("AllowImplied=");
      str.append(AllowImplied=='Y'?'Y':'N');
      str.append( "|");
      str.append("OptionsExpirationYear=");
      str.append(OptionsExpirationYear);
      str.append( "|");
      str.append("OptionsExpirationMonth=");
      str.append(OptionsExpirationMonth);
      str.append( "|");
      str.append("OptionsExpirationDay=");
      str.append(OptionsExpirationDay);
      str.append( "|");
      str.append("MinPrice=");
      str.append(MinPrice);
      str.append( "|");
      str.append("MaxPrice=");
      str.append(MaxPrice);
      str.append( "|");
      str.append("ProductID=");
      str.append(ProductID);
      str.append( "|");
      str.append("ProductName=");
      str.append(MessageUtil.toString(ProductName));
      str.append( "|");
      str.append("HubID=");
      str.append(HubID);
      str.append( "|");
      str.append("HubAlias=");
      str.append(MessageUtil.toString(HubAlias));
      str.append( "|");
      str.append("StripID=");
      str.append(StripID);
      str.append( "|");
      str.append("StripName=");
      str.append(MessageUtil.toString(StripName));
      str.append( "|");
      str.append("ReservedField1=");
      str.append(ReservedField1);
      str.append( "|");
      str.append("IsForSerialOptions=");
      str.append(IsSerialOptionsSupported);
      str.append("|");
      str.append("IsTradable=");
      str.append(IsTradable);
      str.append("|");
      str.append("SettlementPriceDenominator=");
      str.append(SettlementPriceDenominator);
      str.append("|");
      str.append("MicCode=");
      str.append(MicCode);
      str.append("|");
      str.append("UnitQtyDenominator=");
      str.append(UnitQtyDenominator);
      str.append("|");
      
	  return str.toString();
	}

   public void setMarketID(int MarketID)
   {
      this.MarketID = MarketID;
   }

}

	