package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * OpenPriceMessage.java
 * @author David Chen
 */

public class OpenPriceMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 23;

	public long OpenPrice;
	public long DateTime;

   public OpenPriceMessage()
   {
      MessageType = RawMessageFactory.OpenPriceMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();			
			SerializedContent.putLong( OpenPrice );
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
			
			strBuf.append( OpenPrice );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		OpenPrice = inboundcontent.getLong();
		DateTime = inboundcontent.getLong();
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();		
      str.append(super.toString());		
		str.append("OpenPrice=");
		str.append(OpenPrice);
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		return str.toString();
	}

}

	