package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * InvestigatedTradeMessage.java
 * @author David Chen
 */

public class InvestigatedTradeMessage extends MDSequencedMessageWithMarketID
{
	private static final short MESSAGE_LENGTH = 37;
	public long OrderID;
	public long Price;
	public int Quantity;
	public char BlockTradeType;
	public long DateTime;
   public char Status;

   public InvestigatedTradeMessage()
   {
      MessageType = RawMessageFactory.InvestigatedTradeMessageType;
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
         SerializedContent.put( (byte)Status );

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
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Status );
         strBuf.append( LOG_FLD_DELIMITER );
         
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
      Status = (char)inboundcontent.get();         
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
      str.append("Status=");
      str.append(Status);
      str.append( "|");      
		return str.toString();
	}

}

	