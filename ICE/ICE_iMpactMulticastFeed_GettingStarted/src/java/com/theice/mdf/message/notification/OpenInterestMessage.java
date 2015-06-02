package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * OpenInterestMessage.java
 * @author David Chen
 */

public class OpenInterestMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 33;
	private static final byte OPEN_INTEREST_DATE_LENGTH = MarketSnapshotMessage.OPEN_INTEREST_DATE_LENGTH;

	public int OpenInterest;
	public int OpenInterestChange;
	public long DateTime;
	public char[] OpenInterestDate = new char[OPEN_INTEREST_DATE_LENGTH];

   public OpenInterestMessage()
   {
      MessageType = RawMessageFactory.OpenInterestMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();			
			SerializedContent.putInt( OpenInterest );
			SerializedContent.putInt( OpenInterestChange );
			SerializedContent.putLong( DateTime );
			for( int i=0; i<OPEN_INTEREST_DATE_LENGTH; i++ )
         {
            SerializedContent.put( (byte)OpenInterestDate[i] );
         }

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
			
			strBuf.append( OpenInterest );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( OpenInterestChange );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( OpenInterestDate );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{		
		OpenInterest = inboundcontent.getInt();
		OpenInterestChange = inboundcontent.getInt();
		DateTime = inboundcontent.getLong();
		if (inboundcontent.hasRemaining())
		{
		   for( int i=0; i<OPEN_INTEREST_DATE_LENGTH; i++ )
		   {
		      OpenInterestDate[i] = (char)inboundcontent.get();
		   }
		}
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());		
		str.append("OpenInterest=");
		str.append(OpenInterest);
		str.append( "|");
		str.append("OpenInterestChange=");
		str.append(OpenInterestChange);
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		str.append("OpenInterestDate=");
		str.append(OpenInterestDate);
		str.append("|");
		return str.toString();
	}
}

	