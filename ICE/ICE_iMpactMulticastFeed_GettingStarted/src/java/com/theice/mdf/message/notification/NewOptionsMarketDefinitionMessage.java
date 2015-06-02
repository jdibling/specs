package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;

/**
 * NewOptionsMarketDefinitionMessage
 * 
 * This message is generated dynamically when new flex options markets are created
 * 
 */
public class NewOptionsMarketDefinitionMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 278;
   
   public int UnderlyingMarketID;
   public char ContractSymbol[] = new char[70];
   public char TradingStatus;
   public char OrderPriceDenominator;
   public int IncrementQty;
   public int LotSize;     
   public char MarketDesc[] = new char[120];
   public char OptionType; //Call or Put (C or P)
   public long StrikePrice;
   public char DealPriceDenominator;
   public int MinQty;
   public char Currency[] = new char[20];
   public char NumDecimalsStrikePrice;
   public long MinOptionsPrice;
   public long MaxOptionsPrice; 
   public int IncrementPremiumPrice;  
   public short OptionsExpirationYear = -1;
   public short OptionsExpirationMonth = -1;
   public short OptionsExpirationDay = -1;
   public char OptionsSettlementType; //'A'-American or 'E'-European
   public char OptionsExpirationType=OptionsProductDefinitionResponse.OPTIONS_EXPIRATION_TYPE_MONTHLY; //'M'-Monthly or 'D'-Daily
   public int SerialUnderlyingMarketID = -1;

   public NewOptionsMarketDefinitionMessage()
   {
      MessageType = RawMessageFactory.NewOptionsMarketDefinitionMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );
      
         serializeHeader();
         
         SerializedContent.putInt(UnderlyingMarketID);
         
         for( int i=0; i<ContractSymbol.length  ; i++ )
         {
            SerializedContent.put( (byte)ContractSymbol[i] );
         }
      
         SerializedContent.put( (byte)TradingStatus );
         SerializedContent.put( (byte)OrderPriceDenominator );
         SerializedContent.putInt( IncrementQty );
         SerializedContent.putInt( LotSize );
         
         for( int i=0; i<MarketDesc.length  ; i++ )
         {
            SerializedContent.put( (byte)MarketDesc[i] );
         }
         
         SerializedContent.put( (byte)OptionType );
         SerializedContent.putLong( StrikePrice );
         SerializedContent.put( (byte)DealPriceDenominator );
         SerializedContent.putInt( MinQty );
         
         for( int i=0; i<Currency.length  ; i++ )
         {
            SerializedContent.put( (byte)Currency[i] );
         }
          
         SerializedContent.put( (byte)NumDecimalsStrikePrice );
         SerializedContent.putLong( MinOptionsPrice );
         SerializedContent.putLong( MaxOptionsPrice );
         SerializedContent.putInt( IncrementPremiumPrice );
         
         SerializedContent.putShort( OptionsExpirationYear );
         SerializedContent.putShort( OptionsExpirationMonth );
         SerializedContent.putShort( OptionsExpirationDay );
      
         SerializedContent.put( (byte)OptionsSettlementType );
         SerializedContent.put( (byte)OptionsExpirationType );

         SerializedContent.putInt( SerialUnderlyingMarketID );
      
         SerializedContent.rewind();
      }
      
      return SerializedContent.array();
   }
   
   public String getShortLogStr()
   {
      // too much for logging, and it is pretty static, just
      // return null
      return null;
   }
   
   public void deserializeContent( ByteBuffer inboundcontent )
   {
      UnderlyingMarketID = inboundcontent.getInt();
   
      for( int i=0; i<ContractSymbol.length  ; i++ )
      {
         ContractSymbol[i] = (char)inboundcontent.get();
      }
      
      TradingStatus = (char)inboundcontent.get();
      OrderPriceDenominator = (char)inboundcontent.get();
      IncrementQty = inboundcontent.getInt();
      LotSize = inboundcontent.getInt();
   
      for( int i=0; i<MarketDesc.length  ; i++ )
      {
         MarketDesc[i] = (char)inboundcontent.get();
      }
      
      OptionType = (char)inboundcontent.get();
      StrikePrice = inboundcontent.getLong();
      
      DealPriceDenominator = (char)inboundcontent.get();
      MinQty = inboundcontent.getInt();

      for( int i=0; i<Currency.length  ; i++ )
      {
         Currency[i] = (char)inboundcontent.get();
      }    

      NumDecimalsStrikePrice = (char)inboundcontent.get();
      MinOptionsPrice = inboundcontent.getLong();
      MaxOptionsPrice = inboundcontent.getLong();
      IncrementPremiumPrice = inboundcontent.getInt();
      OptionsExpirationYear = inboundcontent.getShort();
      OptionsExpirationMonth = inboundcontent.getShort();
      OptionsExpirationDay = inboundcontent.getShort();
      OptionsSettlementType = (char)inboundcontent.get();
      OptionsExpirationType = (char)inboundcontent.get();
      SerialUnderlyingMarketID = inboundcontent.getInt();
   }
   
   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("UnderlyingMarketID=");
      str.append(UnderlyingMarketID);
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
      str.append("IncrementQty=");
      str.append(IncrementQty);
      str.append( "|");
      str.append("LotSize=");
      str.append(LotSize);
      str.append( "|");
      str.append("MarketDesc=");
      str.append(MessageUtil.toString(MarketDesc));
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
      str.append("Currency=");
      str.append(MessageUtil.toString(Currency));
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
      str.append("IncrementPremiumPrice=");
      str.append(IncrementPremiumPrice);
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
      str.append("OptionsSettlementType=");
      str.append(OptionsSettlementType);
      str.append( "|");
      str.append("OptionsExpirationType=");
      str.append(OptionsExpirationType);
      str.append( "|");
      str.append("SerialUnderlyingMarketID=");
      str.append(SerialUnderlyingMarketID);
      str.append("|");
      
      return str.toString();
   }
   
}