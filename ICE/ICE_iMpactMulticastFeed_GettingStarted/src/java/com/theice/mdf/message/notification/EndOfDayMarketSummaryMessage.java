package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * EndOfDayMarketSummaryMessage.java
 * @author Qian Wang
 */

public class EndOfDayMarketSummaryMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 75;

	public int TotalVolume;
	public int BlockVolume;
	public int EFSVolume;
	public int EFPVolume;
   public long OpeningPrice;
	public long High;
	public long Low;
	public long VWAP;
   public long SettlementPrice;
   public int OpenInterest;
	public long DateTime;

   public EndOfDayMarketSummaryMessage()
   {
      MessageType = RawMessageFactory.QVEndOfDayMarketSummaryMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.putInt( TotalVolume );
			SerializedContent.putInt( BlockVolume );
			SerializedContent.putInt( EFSVolume );
			SerializedContent.putInt( EFPVolume );
         SerializedContent.putLong( OpeningPrice );
			SerializedContent.putLong( High );
			SerializedContent.putLong( Low );
			SerializedContent.putLong( VWAP );
         SerializedContent.putLong( SettlementPrice );
         SerializedContent.putInt( OpenInterest );
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
         
			strBuf.append( TotalVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( BlockVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( EFSVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( EFPVolume );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( OpeningPrice );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( High );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Low );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( VWAP );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( SettlementPrice );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( OpenInterest );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		TotalVolume = inboundcontent.getInt();
		BlockVolume = inboundcontent.getInt();
		EFSVolume = inboundcontent.getInt();
		EFPVolume = inboundcontent.getInt();
      OpeningPrice = inboundcontent.getLong();
		High = inboundcontent.getLong();
		Low = inboundcontent.getLong();
		VWAP = inboundcontent.getLong();
      SettlementPrice = inboundcontent.getLong();
      OpenInterest = inboundcontent.getInt();
		DateTime = inboundcontent.getLong();
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("TotalVolume=");
		str.append(TotalVolume);
		str.append( "|");
		str.append("BlockVolume=");
		str.append(BlockVolume);
		str.append( "|");
		str.append("EFSVolume=");
		str.append(EFSVolume);
		str.append( "|");
		str.append("EFPVolume=");
		str.append(EFPVolume);
		str.append( "|");
      str.append("OpeningPrice=");
      str.append(OpeningPrice);
      str.append( "|");     
		str.append("High=");
		str.append(High);
		str.append( "|");
		str.append("Low=");
		str.append(Low);
		str.append( "|");
		str.append("VWAP=");
		str.append(VWAP);
		str.append( "|");
      str.append("SettlementPrice=");
      str.append(SettlementPrice);
      str.append( "|");  
      str.append("OpenInterest=");
      str.append(OpenInterest);
      str.append( "|");     
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		return str.toString();
	}

}

	