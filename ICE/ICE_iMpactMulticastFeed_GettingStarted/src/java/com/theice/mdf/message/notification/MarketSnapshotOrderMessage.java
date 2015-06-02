package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;


/**
 * MarketSnapshotOrderMessage.java
 * @author David Chen
 */

public class MarketSnapshotOrderMessage extends MDSequencedMessageWithMarketID
{
   public static final short MESSAGE_LENGTH = 44;
   
   public long OrderID;
   public int OrderSeqID; //2 bytes in message spec. No longer used on client side
   public char Side;
   public long Price;
   public int Quantity;
   public char Implied;
   public char IsRFQ;
   public long DateTime;
   public int SequenceWithinMillis;
   
   public MarketSnapshotOrderMessage()
   {
      MessageType = RawMessageFactory.MarketSnapshotOrderMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public MarketSnapshotOrderMessage(AddModifyOrderMessage order)
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
      MessageType = 'D';
      SequenceWithinMillis = order.SequenceWithinMillis;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH; //AddModifyOrderMessage has 41 bytes including header
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
      if (inboundcontent.hasRemaining())
      {
    	  SequenceWithinMillis = inboundcontent.getInt();
      }
   }
   
   public boolean isImpliedOrderMsg()
   {
      return Implied=='Y';
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
      str.append("|");
      str.append("SequenceWithinMillis=");
      str.append(SequenceWithinMillis);
      str.append("|");
      
      return str.toString();
   }
}

	