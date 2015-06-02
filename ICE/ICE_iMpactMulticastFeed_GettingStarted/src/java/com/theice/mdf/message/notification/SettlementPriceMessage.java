package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * SettlementPriceMessage.java
 * @author David Chen
 */

public class SettlementPriceMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 40;
   
   public long SettlementPriceWithDealPricePrecision;
   public long DateTime;
   public char IsOfficial;
   public long EvaluationDateTime;
   public long SettlementPrice;

   public SettlementPriceMessage()
   {
      MessageType = RawMessageFactory.SettlementPriceMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.putLong( SettlementPriceWithDealPricePrecision );
         SerializedContent.putLong( DateTime );
         SerializedContent.put( (byte)IsOfficial );
         SerializedContent.putLong( EvaluationDateTime );
         SerializedContent.putLong( SettlementPrice );
         
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

         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( SettlementPriceWithDealPricePrecision );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsOfficial );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EvaluationDateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( SettlementPrice );
         strBuf.append( LOG_FLD_DELIMITER );
         
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   protected void deserializeContent( ByteBuffer inboundcontent )
   {
      SettlementPriceWithDealPricePrecision = inboundcontent.getLong();
      DateTime = inboundcontent.getLong();
      IsOfficial = (char)inboundcontent.get();
      EvaluationDateTime = inboundcontent.getLong();
      
      if (inboundcontent.hasRemaining())
      {
         SettlementPrice = inboundcontent.getLong();
      }
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("SettlementPriceOld=");
      str.append(SettlementPriceWithDealPricePrecision);
      str.append("|");
      str.append("DateTime=");
      str.append(DateTime);
      str.append("|");
      str.append("IsOfficial=");
      str.append(IsOfficial);
      str.append("|");    
      str.append("EvaluationDateTime=");
      str.append(EvaluationDateTime);
      str.append("|");
      str.append("SettlementPrice=");
      str.append(SettlementPrice);
      str.append("|");
      
      return str.toString();
   }

}

   