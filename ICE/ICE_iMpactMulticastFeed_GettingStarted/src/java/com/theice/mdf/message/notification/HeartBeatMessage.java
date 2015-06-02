package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.RawMessageFactory;

/**
 * HeartBeatMessage.java
 * @author David Chen
 */

public class HeartBeatMessage extends MDMessage
{
	private static final short MESSAGE_LENGTH = 11;
	public long DateTime;

   public HeartBeatMessage()
   {
      MessageType = RawMessageFactory.HeartBeatMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public int getMarketID()
   {
      return -1;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.putLong( DateTime );

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
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	public void deserialize( ByteBuffer inboundcontent )
	{
		DateTime = inboundcontent.getLong();
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		return str.toString();
	}

   public void setMarketID(int MarketID)
   {
   }
}

	