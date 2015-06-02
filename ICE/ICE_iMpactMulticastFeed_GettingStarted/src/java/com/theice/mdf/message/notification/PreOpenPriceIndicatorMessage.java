package com.theice.mdf.message.notification;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

import java.nio.ByteBuffer;

/**
 * PreOpenPriceIndicatorMessage.java
 * @author David Chen
 */

public class PreOpenPriceIndicatorMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 23;

	public long PreOpenPrice;
	public long DateTime;

   public PreOpenPriceIndicatorMessage()
   {
      MessageType = RawMessageFactory.PreOpenPriceIndicatorMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.putLong( PreOpenPrice );
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

			strBuf.append( PreOpenPrice );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		PreOpenPrice = inboundcontent.getLong();
		DateTime = inboundcontent.getLong();
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
      str.append(super.toString());
		str.append("PreOpenPrice=");
		str.append(PreOpenPrice);
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		return str.toString();
	}

}

