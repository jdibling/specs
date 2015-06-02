package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * MarketStateChangeMessage.java
 * @author David Chen
 */

public class MarketStateChangeMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 16;

	public char TradingStatus;
	public long DateTime;

   public MarketStateChangeMessage()
   {
      MessageType = RawMessageFactory.MarketStateChangeMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.put( (byte)TradingStatus );
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
        
			strBuf.append( TradingStatus );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }


	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		TradingStatus = (char)inboundcontent.get();
		DateTime = inboundcontent.getLong();

	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());		
		str.append("TradingStatus=");
		str.append(TradingStatus);
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		return str.toString();
	}
}

	