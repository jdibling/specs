package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * TradeMessage.java
 * @author David Chen
 */

public class TradeMessage extends MDSequencedMessageWithMarketID
{
	public static final short MESSAGE_LENGTH = 42;

	public long OrderID;
	public char IsSystemPricedLeg;
	public long Price;
	public int Quantity;
	public char BlockTradeType;
	public long DateTime;
   public char SystemPricedLegType = ' ';
   public char IsImpliedSpreadAtMarketOpen = 'N';
   public char IsAdjustedTrade = 'N';
   public char AggressorSide = ' ';
   
   public boolean IsRFCCrossing = false;
   public boolean IsLegDealOutsideIPL = false;
   
   private byte Flags = 0;

   public TradeMessage()
   {
      MessageType = RawMessageFactory.TradeMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.putLong( OrderID );
			SerializedContent.put( (byte)IsSystemPricedLeg );
			SerializedContent.putLong( Price );
			SerializedContent.putInt( Quantity );
			SerializedContent.put( (byte)BlockTradeType );
			SerializedContent.putLong( DateTime );
         SerializedContent.put( (byte)SystemPricedLegType );
         SerializedContent.put( (byte)IsImpliedSpreadAtMarketOpen );
         SerializedContent.put( (byte)IsAdjustedTrade );
         SerializedContent.put( (byte)AggressorSide );
         SerializedContent.put( writeFlags() );

			SerializedContent.rewind();

         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
		}

		return SerializedContent.array();
	}
	
	private byte writeFlags()
	{
	   byte value = 0;
	   
	   if (IsRFCCrossing)
	   {
	      value |= ((byte) 0x01);
	   }
	   if (IsLegDealOutsideIPL)
	   {
	      value |= ((byte) 0x01 << 1);
	   }
	   
	   return value;
	}
	
	private void readFlags(ByteBuffer inboundcontent)
	{
	   Flags = inboundcontent.get();
	   IsRFCCrossing = ((Flags & 0x01) != 0)? true : false;
	   IsLegDealOutsideIPL = (((Flags >>> 1) & 0x01) != 0)? true : false;
	}

   public String getShortLogStr()
   {
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());

			strBuf.append( OrderID );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( IsSystemPricedLeg );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Price );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Quantity );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( BlockTradeType );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( SystemPricedLegType);
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsImpliedSpreadAtMarketOpen);
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsAdjustedTrade );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( AggressorSide );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsRFCCrossing );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		OrderID = inboundcontent.getLong();
		IsSystemPricedLeg = (char)inboundcontent.get();
		Price = inboundcontent.getLong();
		Quantity = inboundcontent.getInt();
		BlockTradeType = (char)inboundcontent.get();
		DateTime = inboundcontent.getLong();
      SystemPricedLegType = (char)inboundcontent.get();
      IsImpliedSpreadAtMarketOpen = (char)inboundcontent.get();
      IsAdjustedTrade = (char)inboundcontent.get();
      AggressorSide = (char)inboundcontent.get();
      
      if (inboundcontent.hasRemaining())
      {
         readFlags(inboundcontent);
      }
      
	}
   
   public TradeMessage fromString(String rawString)
   {
      String[] fields = getFieldTokens(rawString);
      populateHeaderFromString(fields);
      int index = getLastHeaderFieldIndex();
      OrderID = Long.valueOf(fields[index+=2]);
      IsSystemPricedLeg = fields[index+=2].length()>0?fields[index].charAt(0):'N';
      Price = Long.valueOf(fields[index+=2]);
      Quantity = Integer.valueOf(fields[index+=2]);
      BlockTradeType = fields[index+=2].length()>0?fields[index].charAt(0):'N';
      DateTime = Long.valueOf(fields[index+=2]);
      return this;
   }

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("OrderID=");
		str.append(OrderID);
		str.append( "|");
		str.append("IsSystemPricedLeg=");
		str.append(IsSystemPricedLeg=='Y'?'Y':'N');
		str.append( "|");
		str.append("Price=");
		str.append(Price);
		str.append( "|");
		str.append("Quantity=");
		str.append(Quantity);
		str.append( "|");
		str.append("BlockTradeType=");
		str.append(BlockTradeType);
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
      str.append( "|");
      str.append("SystemPricedLegType=");
      str.append(SystemPricedLegType);
      str.append( "|");
      str.append("IsImpliedSpreadAtMarketOpen=");
      str.append(IsImpliedSpreadAtMarketOpen);
      str.append( "|");
      str.append("IsAdjustedTrade=");
      str.append(IsAdjustedTrade);
      str.append( "|");
      str.append("AggressorSide=");
      str.append(AggressorSide);
      str.append( "|" );
      str.append("Flags=");
      str.append(Integer.toBinaryString(Flags));
      str.append("|");
      str.append("IsRFCCrossing=");
      str.append(IsRFCCrossing);
      str.append("|");
      str.append("IsLegDealOutsideIPL=");
      str.append(IsLegDealOutsideIPL);
      str.append("|");
      return str.toString();
	}

}

	