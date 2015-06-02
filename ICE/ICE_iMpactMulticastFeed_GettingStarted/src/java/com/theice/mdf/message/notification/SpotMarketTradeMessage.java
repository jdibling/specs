package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * TradeMessage.java
 * 
 * @author David Chen
 */

public class SpotMarketTradeMessage extends MDSequencedMessageWithMarketID
{
	public static final short MESSAGE_LENGTH = 52;

	public long OrderID;
	public long Price;
	public int Quantity;
	public long DateTime;

	private byte Flags = 0;
	public long DeliveryBeginDateTime;
	public long DeliveryEndDateTime;

	public SpotMarketTradeMessage()
	{
		MessageType = RawMessageFactory.SpotMarketTradeMessageType;
		MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
	}

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if (SerializedContent == null)
		{
			SerializedContent = ByteBuffer.allocate(MESSAGE_LENGTH);

			serializeHeader();
			SerializedContent.putLong(OrderID);
			SerializedContent.putLong(Price);
			SerializedContent.putInt(Quantity);
			SerializedContent.putLong(DateTime);
			SerializedContent.put((byte) Flags);
			SerializedContent.putLong(DeliveryBeginDateTime);
			SerializedContent.putLong(DeliveryEndDateTime);
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
		if (ShortLogStr == null)
		{
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(getLogHeaderShortStr());

			strBuf.append(OrderID);
			strBuf.append(LOG_FLD_DELIMITER);
			strBuf.append(Price);
			strBuf.append(LOG_FLD_DELIMITER);
			strBuf.append(Quantity);
			strBuf.append(LOG_FLD_DELIMITER);
			strBuf.append(DateTime);
			strBuf.append(LOG_FLD_DELIMITER);
			strBuf.append(Flags);
			strBuf.append(LOG_FLD_DELIMITER);
			strBuf.append(DeliveryBeginDateTime);
			strBuf.append(LOG_FLD_DELIMITER);
			strBuf.append(DeliveryEndDateTime);
			ShortLogStr = strBuf.toString();
		}

		return ShortLogStr;
	}

	protected void deserializeContent(ByteBuffer inboundcontent)
	{
		OrderID = inboundcontent.getLong();
		Price = inboundcontent.getLong();
		Quantity = inboundcontent.getInt();
		DateTime = inboundcontent.getLong();
		Flags =  inboundcontent.get();
		DeliveryBeginDateTime = inboundcontent.getLong();
		DeliveryEndDateTime = inboundcontent.getLong();

	}

	
	public String toString()
	{
		StringBuffer str = new StringBuffer();

		str.append(super.toString());
		str.append("OrderID=");
		str.append(OrderID);
		str.append("|");
		str.append("Price=");
		str.append(Price);
		str.append("|");
		str.append("Quantity=");
		str.append(Quantity);
		str.append("|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append("|");
		str.append("Flags=");
		str.append(Integer.toBinaryString(Flags));
		str.append("|");
		str.append("DeliveryBeginTime=");
		str.append(DeliveryBeginDateTime);
		str.append("|");
		str.append("DeliveryEndTime=");
		str.append(DeliveryEndDateTime);
		str.append("|");
		
		return str.toString();
	}

}
