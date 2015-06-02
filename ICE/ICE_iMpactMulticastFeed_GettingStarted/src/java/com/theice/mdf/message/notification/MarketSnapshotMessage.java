package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.SnapshotMessageIface;

/**
 * MarketSnapshotMessage.java
 * @author David Chen
 */

public class MarketSnapshotMessage extends MDSequencedMessageWithMarketID implements SnapshotMessageIface
{
	private static final short MESSAGE_LENGTH = 127;
	public static final byte OPEN_INTEREST_DATE_LENGTH = 10;

	public short MarketType;
	public char TradingStatus;
	public int TotalVolume;
	public int BlockVolume;
	public int EFSVolume;
	public int EFPVolume;
	public int OpenInterest;
	public long OpeningPrice;
	public long SettlementPriceWithDealPricePrecision;
	public long High;
	public long Low;
	public long VWAP;
	public int NumOfBookEntries;
   public long LastTradePrice;
   public int LastTradeQuantity;
   public long LastTradeDateTime;
   public long SettlementPriceDateTime = -1;
   public int LastMessageSequenceNumber;
   public short ReservedField1;
   public char[] OpenInterestDate = new char[OPEN_INTEREST_DATE_LENGTH];
   public char IsSettlementPriceOfficial = 'N';
   public long SettlementPrice;

   public MarketSnapshotMessage()
   {
      MessageType = RawMessageFactory.MarketSnapshotMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.putShort( MarketType );
			SerializedContent.put( (byte)TradingStatus );
			SerializedContent.putInt( TotalVolume );
			SerializedContent.putInt( BlockVolume );
			SerializedContent.putInt( EFSVolume );
			SerializedContent.putInt( EFPVolume );
			SerializedContent.putInt( OpenInterest );
			SerializedContent.putLong( OpeningPrice );
			SerializedContent.putLong( SettlementPriceWithDealPricePrecision );
			SerializedContent.putLong( High );
			SerializedContent.putLong( Low );
			SerializedContent.putLong( VWAP );
			SerializedContent.putInt( NumOfBookEntries );
         SerializedContent.putLong(LastTradePrice);
         SerializedContent.putInt(LastTradeQuantity);
         SerializedContent.putLong(LastTradeDateTime);
         SerializedContent.putLong(SettlementPriceDateTime);
         SerializedContent.putInt(LastMessageSequenceNumber);
         SerializedContent.putShort(ReservedField1);
         for( int i=0; i<OPEN_INTEREST_DATE_LENGTH; i++ )
         {
            SerializedContent.put( (byte)OpenInterestDate[i] );
         }
         SerializedContent.put( (byte)IsSettlementPriceOfficial );
         SerializedContent.putLong( SettlementPrice );

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

         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( MarketType );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( (byte)TradingStatus );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  TotalVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  BlockVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  EFSVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  EFPVolume );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  OpenInterest );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  OpeningPrice );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( SettlementPriceWithDealPricePrecision );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( High );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Low );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( VWAP );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append(  NumOfBookEntries );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append(  LastTradePrice );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append(  LastTradeQuantity );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append(  LastTradeDateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append(  SettlementPriceDateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append(  LastMessageSequenceNumber );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append(  ReservedField1 );
         strBuf.append( LOG_FLD_DELIMITER);
         strBuf.append( OpenInterestDate );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsSettlementPriceOfficial );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( SettlementPrice );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }


	public void deserializeContent( ByteBuffer inboundcontent )
	{
		MarketType = inboundcontent.getShort();
		TradingStatus = (char)inboundcontent.get();
		TotalVolume = inboundcontent.getInt();
		BlockVolume = inboundcontent.getInt();
		EFSVolume = inboundcontent.getInt();
		EFPVolume = inboundcontent.getInt();
		OpenInterest = inboundcontent.getInt();
		OpeningPrice = inboundcontent.getLong();
		SettlementPriceWithDealPricePrecision = inboundcontent.getLong();
		High = inboundcontent.getLong();
		Low = inboundcontent.getLong();
		VWAP = inboundcontent.getLong();
		NumOfBookEntries = inboundcontent.getInt();
      LastTradePrice = inboundcontent.getLong();
      LastTradeQuantity = inboundcontent.getInt();
      LastTradeDateTime = inboundcontent.getLong();
      SettlementPriceDateTime = inboundcontent.getLong();
      LastMessageSequenceNumber = inboundcontent.getInt();
      ReservedField1 = inboundcontent.getShort();
      
      for( int i=0; i<OPEN_INTEREST_DATE_LENGTH  ; i++ )
      {
         OpenInterestDate[i] = (char)inboundcontent.get();
      }
      
      IsSettlementPriceOfficial = (char)inboundcontent.get();
            
      if (inboundcontent.hasRemaining())
      {
         SettlementPrice = inboundcontent.getLong();
      }
	}

   public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("MarketType=");
		str.append(MarketType);
		str.append( "|");
		str.append("TradingStatus=");
		str.append(TradingStatus);
		str.append( "|");
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
		str.append("OpenInterest=");
		str.append(OpenInterest);
		str.append( "|");
		str.append("OpeningPrice=");
		str.append(OpeningPrice);
		str.append( "|");
		str.append("SettlePriceOld=");
		str.append(SettlementPriceWithDealPricePrecision);
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
		str.append("NumOfBookEntries=");
		str.append(NumOfBookEntries);
		str.append( "|");
      str.append("LastTradePrice=");
      str.append(LastTradePrice);
      str.append( "|");
      str.append("LastTradeQuantity=");
      str.append(LastTradeQuantity);
      str.append( "|");
      str.append("LastTradeDateTime=");
      str.append(LastTradeDateTime);
      str.append( "|");
      str.append("SettlePriceDateTime=");
      str.append(SettlementPriceDateTime);
      str.append( "|");
      str.append("LastMessageSequenceNumber=");
      str.append(LastMessageSequenceNumber);
      str.append( "|");
      str.append("ReservedField1=");
      str.append(ReservedField1);
      str.append( "|");
      str.append("OpenInterestDate=");
      str.append(OpenInterestDate);
      str.append( "|");
      str.append("IsSettlePriceOfficial=");
      str.append(IsSettlementPriceOfficial);
      str.append( "|");
      str.append("SettlePrice=");
      str.append(SettlementPrice);
      str.append( "|");
      
		return str.toString();
	}
   
   /**
    * Methods to support SnapshotMessageIface interface contracts
    */

	public int getNumOfBookEntries()
	{
		return(this.NumOfBookEntries);
	}
	
	public int getLastMessageSequenceNumber()
	{
		return(this.LastMessageSequenceNumber);
	}
	
}

