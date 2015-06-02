package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

public class AuctionNotificationMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 40;
   public static final char AUCTION_STATUS_NEW = 'N';
   public static final char AUCTION_STATUS_ACTIVE = 'A';
   public static final char AUCTION_STATUS_END = 'E';
   public static final char AUCTION_STATUS_TERMINATED = 'T';

   public long SystemID;
   public long DateTime; 
   public char AuctionStatus;
   public int AuctionDuration;//in seconds
   public long Price;
   public int Quantity;
   
   public AuctionNotificationMessage()
   {
      MessageType = RawMessageFactory.AuctionNotificationMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.putLong( SystemID );
         SerializedContent.putLong( DateTime );
         SerializedContent.put( (byte)AuctionStatus );
         SerializedContent.putInt( AuctionDuration );
         SerializedContent.putLong( Price );
         SerializedContent.putInt( Quantity );
         
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
        
         strBuf.append( SystemID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( AuctionStatus );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( AuctionDuration );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Price);
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Quantity );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }


   protected void deserializeContent( ByteBuffer inboundcontent )
   {
      SystemID = inboundcontent.getLong();
      DateTime = inboundcontent.getLong();
      AuctionStatus = (char)inboundcontent.get();
      AuctionDuration = inboundcontent.getInt();
      Price = inboundcontent.getLong();
      Quantity = inboundcontent.getInt();
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());    
      str.append("SystemID=");
      str.append(SystemID);
      str.append( "|");
      str.append("DateTime=");
      str.append(DateTime);
      str.append( "|");
      str.append("AuctionStatus=");
      str.append(AuctionStatus);
      str.append("|");
      str.append("AuctionDuration=");
      str.append(AuctionDuration);
      str.append("|");
      str.append("Price=");
      str.append(Price);
      str.append("|");
      str.append("Quantity=");
      str.append(Quantity);
      str.append("|");
      
      return str.toString();
   }
}

   
