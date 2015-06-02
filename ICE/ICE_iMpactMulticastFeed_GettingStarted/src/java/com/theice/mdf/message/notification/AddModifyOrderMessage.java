package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * AddModifyOrderMessage.java
 * @author David Chen
 */

public class AddModifyOrderMessage extends MDSequencedMessageWithMarketID
{
   public static final short MESSAGE_LENGTH = 45;
   
	public long OrderID;
	public int OrderSeqID; //2 bytes in message spec. No longer used on client side
	public char Side;
	public long Price;
	public int Quantity;
	public char Implied;
	public char IsRFQ;
	public long DateTime;
	public boolean IsModifyOrder = false;
	public int SequenceWithinMillis;
	
	private byte Flags = 0;

   public AddModifyOrderMessage()
   {
      MessageType = RawMessageFactory.AddModifyOrderMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public AddModifyOrderMessage(AddModifyOrderMessage order)
   {
      super(order);
      OrderID = order.OrderID;
      OrderSeqID = order.OrderSeqID;
      Side = order.Side;
      Price = order.Price;
      Quantity = order.Quantity;
      Implied = order.Implied;
      IsRFQ = order.IsRFQ;
      DateTime = order.DateTime;
      IsModifyOrder = order.IsModifyOrder;
      SequenceWithinMillis = order.SequenceWithinMillis;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         serializeContent(SerializedContent);

			SerializedContent.rewind();

         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
		}

		return SerializedContent.array();
	}

   protected void serializeContent(ByteBuffer byteBuffer)
   {
      byteBuffer.putLong( OrderID );
      //always serialize OrderSeqID to 0
      byteBuffer.putShort((short)0);
      byteBuffer.put( (byte)Side );
      byteBuffer.putLong( Price );
      byteBuffer.putInt( Quantity );
      byteBuffer.put( (byte)Implied );
      byteBuffer.put( (byte)IsRFQ );
      byteBuffer.putLong( DateTime );
      byteBuffer.put( writeFlags() );
      byteBuffer.putInt(SequenceWithinMillis);
   }

   public String getShortLogStr()
   {
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());
         strBuf.append( OrderID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( OrderSeqID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Side );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Price );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Quantity );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( Implied );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsRFQ );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( IsModifyOrder );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( SequenceWithinMillis );
         strBuf.append( LOG_FLD_DELIMITER );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		OrderID = inboundcontent.getLong();
		OrderSeqID = inboundcontent.getShort();
		Side = (char)inboundcontent.get();
		Price = inboundcontent.getLong();
		Quantity = inboundcontent.getInt();
		Implied = (char)inboundcontent.get();
		IsRFQ = (char)inboundcontent.get();
		DateTime = inboundcontent.getLong();
		readFlags(inboundcontent);

		if (inboundcontent.hasRemaining())
		{
			SequenceWithinMillis = inboundcontent.getInt();
		}
	}
	
	private byte writeFlags()
	{
	   byte value = 0;
	   if (IsModifyOrder)
	   {
	      value |= ((byte)0x01);
	   }
	   
	   return value;
	}
	
	private void readFlags(ByteBuffer inboundcontent)
	{
	   Flags = inboundcontent.get();
	   IsModifyOrder = ((Flags & 0x01) != 0)? true : false;
	}

   public boolean isImpliedOrderMsg()
   {
      return Implied=='Y';
   }

   public AddModifyOrderMessage fromString(String rawString)
	{
		String[] fields = getFieldTokens(rawString);
      populateHeaderFromString(fields);
      int index = getLastHeaderFieldIndex();
      OrderID = Long.valueOf(fields[index+=2]);
      OrderSeqID = Integer.valueOf(fields[index+=2]);
      Side = fields[index+=2].charAt(0);
      Price = Long.valueOf(fields[index+=2]);
      Quantity = Integer.valueOf(fields[index+=2]);
      Implied = fields[index+=2].length()>0?fields[index].charAt(0):'N';
      IsRFQ = fields[index+=2].length()>0?fields[index].charAt(0):'N';
      DateTime = Long.valueOf(fields[index+=2]);
      //TODO: check if Sequence within Millis needed here .. most likely no..
      return this;
	}

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("OrderID=");
      str.append(OrderID);
      str.append( "|");
      str.append("OrderSeqID=");
      str.append(OrderSeqID);
      str.append( "|");
      str.append("Side=");
      str.append(Side);
      str.append( "|");
      str.append("Price=");
      str.append(Price);
      str.append( "|");
      str.append("Quantity=");
      str.append(Quantity);
      str.append( "|");
      str.append("Implied=");
      str.append(Implied=='Y'?'Y':'N');
      str.append( "|");
      str.append("IsRFQ=");
      str.append(IsRFQ);
      str.append( "|");
      str.append("DateTime=");
      str.append(DateTime);
      str.append( "|");
      str.append("Flags=");
      str.append(Integer.toBinaryString(Flags));
      str.append("|");
      str.append("IsModifyOrder=");
      str.append(IsModifyOrder);
      str.append("|");
      str.append("SequenceWithinMillis=");
      str.append(SequenceWithinMillis);
      str.append("|");
      return str.toString();
   }

}

	