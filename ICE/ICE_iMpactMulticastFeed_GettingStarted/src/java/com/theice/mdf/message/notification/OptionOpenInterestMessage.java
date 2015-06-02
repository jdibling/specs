package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * OptionOpenInterestMessage.java
 * @author David Chen
 */

public class OptionOpenInterestMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 29;
	private static final byte OPEN_INTEREST_DATE_LENGTH = MarketSnapshotMessage.OPEN_INTEREST_DATE_LENGTH;

	public int OpenInterest;
	public long DateTime;
   public char[] OpenInterestDate = new char[OPEN_INTEREST_DATE_LENGTH];
	public String PublishedDate; //NOT included in the message spec

   public OptionOpenInterestMessage()
   {
      MessageType = RawMessageFactory.OptionOpenInterestMessageType;
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
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		str.append("OpenInterestDate=");
		str.append(OpenInterestDate);
		str.append("|");
		str.append("PublishedDate=");
      str.append(PublishedDate);
      str.append("|");
      
		return str.toString();
	}
	
	public boolean equals(Object other)
	{
	   boolean result=false;
	   if (other != null && other instanceof OptionOpenInterestMessage)
	   {
	      OptionOpenInterestMessage that = (OptionOpenInterestMessage)other;
	      if (this.getMarketID() == that.getMarketID() &&
	          this.OpenInterest == that.OpenInterest   && 
	          ( (this.OpenInterestDate==null && that.OpenInterestDate==null) || 
	            (this.OpenInterestDate!=null && that.OpenInterestDate!=null && openInterestDateEqual(this.OpenInterestDate,that.OpenInterestDate))
	          )
	         ) 
	      {
	         result = true;
	      }
	   }
	      
	   return result;
	}
	
	private static boolean openInterestDateEqual(char[] date1, char[] date2)
	{
	   boolean result = true;
	   for (int i=0; i<OPEN_INTEREST_DATE_LENGTH; i++)
	   {
	      if (date1[i] != date2[i])
	      {
	         result = false;
	         break;
	      }
	   }
	   return result;
	}
}

