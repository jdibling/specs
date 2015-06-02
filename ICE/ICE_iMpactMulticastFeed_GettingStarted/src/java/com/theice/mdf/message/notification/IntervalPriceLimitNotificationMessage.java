package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

public class IntervalPriceLimitNotificationMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 37;
   public static final char HOLD_START = 'S';
   public static final char HOLD_END = 'E';

   public char IPLNotificationType; //'S' or 'E'
   public long IPLNotificationDateTime;
   public char IsUp; //'Y' or 'N', NA when IPL end
   public int IPLHoldDuration;//how long, in milliseconds, the IPL Hold will last. NA when IPL end
   public long IPLUp; //NA when IPL end
   public long IPLDown; //NA when IPL end
   
   public IntervalPriceLimitNotificationMessage()
   {
      MessageType = RawMessageFactory.IntervalPriceLimitNotificationMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.put( (byte)IPLNotificationType );
         SerializedContent.putLong( IPLNotificationDateTime );
         SerializedContent.put( (byte)IsUp );
         SerializedContent.putInt( IPLHoldDuration );
         SerializedContent.putLong( IPLUp );
         SerializedContent.putLong( IPLDown );

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
        
         strBuf.append( IPLNotificationType );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IPLNotificationDateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsUp );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IPLHoldDuration );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IPLUp);
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IPLDown);

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }


   protected void deserializeContent( ByteBuffer inboundcontent )
   {
      IPLNotificationType = (char)inboundcontent.get();
      IPLNotificationDateTime = inboundcontent.getLong();
      IsUp = (char)inboundcontent.get();
      IPLHoldDuration = inboundcontent.getInt();
      IPLUp = inboundcontent.getLong();
      IPLDown = inboundcontent.getLong();

   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());    
      str.append("IPLNotificationType=");
      str.append(IPLNotificationType);
      str.append( "|");
      str.append("IPLNotificationDateTime=");
      str.append(IPLNotificationDateTime);
      str.append( "|");
      str.append("IsUp=");
      str.append(IsUp);
      str.append("|");
      str.append("IPLHoldDuration=");
      str.append(IPLHoldDuration);
      str.append("|");
      str.append("IPLUp=");
      str.append(IPLUp);
      str.append("|");
      str.append("IPLDown=");
      str.append(IPLDown);
      str.append("|");
      
      return str.toString();
   }
}

   
