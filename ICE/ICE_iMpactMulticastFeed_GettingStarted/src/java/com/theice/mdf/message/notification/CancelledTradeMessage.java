package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * CancelledTradeMessage.java
 * @author David Chen
 */

public class CancelledTradeMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 36;

	public long OrderID;
	public long Price;
	public int Quantity;
	public char BlockTradeType;
	public long DateTime;

   public CancelledTradeMessage()
   {
      MessageType = RawMessageFactory.CancelledTradeMessageType;
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
			SerializedContent.putLong( Price );
			SerializedContent.putInt( Quantity );
			SerializedContent.put( (byte)BlockTradeType );
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
			strBuf.append( OrderID );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Price );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Quantity );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( BlockTradeType );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( DateTime );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		OrderID = inboundcontent.getLong();
		Price = inboundcontent.getLong();
		Quantity = inboundcontent.getInt();
		BlockTradeType = (char)inboundcontent.get();
		DateTime = inboundcontent.getLong();
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("OrderID=");
		str.append(OrderID);
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
		return str.toString();
	} 

}

	