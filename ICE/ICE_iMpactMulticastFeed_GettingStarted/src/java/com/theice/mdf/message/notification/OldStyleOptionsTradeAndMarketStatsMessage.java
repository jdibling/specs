package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;

public class OldStyleOptionsTradeAndMarketStatsMessage extends MDSequencedMessage
{
   public static final short MESSAGE_LENGTH = 86;
   public static final char NORMAL_TRADE = '0';
   public static final char CANCELLED_TRADE = '1';
   public static final char ADJUSTED_TRADE = '2';

   public int UnderlyingMarketID;
   public long OrderID;
   public long Price;
   public int Quantity;
   public char BlockTradeType;
   public long DateTime;
   public char OptionType;
   public long StrikePrice;
   public char EventCode = NORMAL_TRADE;
   public int TotalVolume = -1;
   public int BlockVolume = -1;
   public int EFSVolume = -1;
   public int EFPVolume = -1;
   public long High = -1;
   public long Low = -1;
   public long VWAP = -1;

   public OldStyleOptionsTradeAndMarketStatsMessage()
   {
      MessageType = RawMessageFactory.OldStyleOptionsTradeAndMarketStatsMessageType;
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
         SerializedContent.putLong( OrderID );
         SerializedContent.putLong( Price );
         SerializedContent.putInt( Quantity );
         SerializedContent.put( (byte)BlockTradeType );
         SerializedContent.putLong( DateTime );
         SerializedContent.put( (byte)OptionType );
         SerializedContent.putLong( StrikePrice );
         SerializedContent.put( (byte)EventCode );
         SerializedContent.putInt( TotalVolume );
         SerializedContent.putInt( BlockVolume );
         SerializedContent.putInt( EFSVolume );
         SerializedContent.putInt( EFPVolume );
         SerializedContent.putLong( High );
         SerializedContent.putLong( Low );
         SerializedContent.putLong( VWAP );
         
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
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());
         strBuf.append( UnderlyingMarketID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( OrderID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Price );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Quantity );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( BlockTradeType );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( OptionType);
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( StrikePrice );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EventCode );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( TotalVolume );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( BlockVolume );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EFSVolume );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EFPVolume );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( High );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Low );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( VWAP );
         strBuf.append( LOG_FLD_DELIMITER );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   protected void deserializeContent( ByteBuffer inboundcontent )
   {
      UnderlyingMarketID = inboundcontent.getInt();
      OrderID = inboundcontent.getLong();
      Price = inboundcontent.getLong();
      Quantity = inboundcontent.getInt();
      BlockTradeType = (char)inboundcontent.get();
      DateTime = inboundcontent.getLong();
      OptionType = (char)inboundcontent.get();
      StrikePrice = inboundcontent.getLong();
      EventCode = (char)inboundcontent.get();
      TotalVolume = inboundcontent.getInt();
      BlockVolume = inboundcontent.getInt();
      EFSVolume = inboundcontent.getInt();
      EFPVolume = inboundcontent.getInt();
      High = inboundcontent.getLong();
      Low = inboundcontent.getLong();
      VWAP = inboundcontent.getLong();
   }
    
   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("UnderlyingMarketID=");
      str.append(UnderlyingMarketID);
      str.append("|");
      str.append("OrderID=");
      str.append(OrderID);
      str.append( "|");
      str.append("Price=");
      str.append(Price);
      str.append( "|");
      str.append("Quantity=");
      str.append(Quantity);
      str.append( "|");
      str.append("BlockTradeType=");
      str.append(BlockTradeType);
      str.append( "|");
      str.append("DateTime=");
      str.append(DateTime);
      str.append( "|");
      str.append("OptionType=");
      str.append(OptionType);
      str.append( "|");
      str.append("StrikePrice=");
      str.append(StrikePrice);
      str.append( "|");
      str.append("EventCode=");
      str.append(EventCode);
      str.append("|");
      str.append("TotalVolume=");
      str.append(TotalVolume);
      str.append( "|" );
      str.append("BlockVolume=");
      str.append(BlockVolume);
      str.append("|");
      str.append("EFSVolume=");
      str.append(EFSVolume);
      str.append("|");
      str.append("EFPVolume=");
      str.append(EFPVolume);
      str.append("|");
      str.append("High=");
      str.append(High);
      str.append("|");
      str.append("Low=");
      str.append(Low);
      str.append("|");
      str.append("VWAP=");
      str.append(VWAP);
      str.append("|");
            
      return str.toString();
   }

   @Override
   public void setMarketID(int MarketID)
   {  
   }

   @Override
   public int getMarketID()
   {
      return -1;
   }

}
