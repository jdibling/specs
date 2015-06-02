package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;

/**
 * BundleMarkerMessage.java
 * @author David Chen
 */

public class BundleMarkerMessage extends MDSequencedMessage
{
   public static final char MARKER_TYPE_START = 'S';
   public static final char MARKER_TYPE_END = 'E';
   
   public static final short MESSAGE_LENGTH = 4;
   public char MarkerType;

   public BundleMarkerMessage()
   {
      MessageType = RawMessageFactory.BundleMarkerMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public int getMarketID()
   {
      return -1;
   }

   public synchronized byte[] serialize()
   {
      if (SerializedContent==null)
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.put( (byte) MarkerType );

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
         strBuf.append( MarkerType );
         strBuf.append( LOG_FLD_DELIMITER );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	public void deserializeContent( ByteBuffer inboundcontent )
	{
      MarkerType = (char) inboundcontent.get();
	}

   public String toString()
   {
      StringBuffer str = new StringBuffer();

      str.append(super.toString());
      str.append("MarkerType=");
      str.append(MarkerType);
      str.append( "|");
      return str.toString();
   }
   
   public void fromString(String rawString)
   {
      String[] fields = rawString.split("\\||=");
      MessageBodyLength = Short.valueOf(fields[3]);
      MarkerType = (fields[5]).charAt(0);
   }

   public void setMarketID(int MarketID)
   {
   }
}
