package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * RequestForQuoteMessage.java
 */

public class RequestForQuoteMessage extends MDSequencedMessageWithMarketID
{
   public static final short MESSAGE_LENGTH = 34;
   public long MessageTimestamp;
   public long RFQSystemID;
   public short MarketTypeID;
   public int  UnderlyingMarketID;
   public int  Quantity;
   public char Side;
   
   public RequestForQuoteMessage()
   {
      MessageType = RawMessageFactory.RequestForQuoteMessageType;
      MessageBodyLength = (short) (MESSAGE_LENGTH - HEADER_LENGTH);
   }

   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.putLong( MessageTimestamp );
         SerializedContent.putLong( RFQSystemID );
         SerializedContent.putShort( MarketTypeID );
         SerializedContent.putInt( UnderlyingMarketID );
         SerializedContent.putInt( Quantity );
       	 SerializedContent.put( (byte)Side );	 
         
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

         strBuf.append( MessageTimestamp );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( RFQSystemID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MarketTypeID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( UnderlyingMarketID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Quantity );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Side );
         
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   protected void deserializeContent( ByteBuffer inboundcontent )
   {
      MessageTimestamp = inboundcontent.getLong();
      RFQSystemID = inboundcontent.getLong();
      MarketTypeID = inboundcontent.getShort();
      UnderlyingMarketID = inboundcontent.getInt();
      Quantity = inboundcontent.getInt();
  	  if (inboundcontent.hasRemaining())
	  {
		Side =  (char)inboundcontent.get();
	  }      
   }
   
   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("MessageTimestamp=");
      str.append(MessageTimestamp);
      str.append( "|");
      str.append("RFQSystemID=");
      str.append(RFQSystemID);
      str.append( "|");
      str.append("MarketType=");
      str.append(MarketTypeID);
      str.append( "|");
      str.append("UnderlyingMarketID=");
      str.append(UnderlyingMarketID);
      str.append( "|");
      str.append("Quantity=");
      str.append(Quantity);
      str.append( "|");
      str.append("Side=");
      str.append(Side);
      str.append( "|");
      
      return str.toString();
   }

}

   