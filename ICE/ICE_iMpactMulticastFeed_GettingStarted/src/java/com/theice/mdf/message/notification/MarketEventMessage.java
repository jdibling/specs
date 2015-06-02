package com.theice.mdf.message.notification;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

import java.nio.ByteBuffer;

/**
 * MarketEventMessage.java
 * @author David Chen
 */

public class MarketEventMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 16;

   public final static char EVENT_TYPE_IMPLICATION_DISABLED = 'A';

	public char EventType;
	public long DateTime;

   public MarketEventMessage()
   {
      MessageType = RawMessageFactory.MarketEventMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.put( (byte)EventType );
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

			strBuf.append( EventType );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }


	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		EventType = (char)inboundcontent.get();
		DateTime = inboundcontent.getLong();
	}

   public String toString()
	{
		StringBuffer str = new StringBuffer();

      str.append(super.toString());
		str.append("EventType=");
		str.append(EventType);
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		return str.toString();
	}
}
