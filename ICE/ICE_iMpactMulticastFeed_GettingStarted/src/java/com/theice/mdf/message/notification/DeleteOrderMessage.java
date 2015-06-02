package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;

/**
 * DeleteOrderMessage.java
 * @author David Chen
 */

public class DeleteOrderMessage extends MDSequencedMessageWithMarketID
{
	public static final short MESSAGE_LENGTH = 15;
	public long OrderID;

   public DeleteOrderMessage()
   {
      MessageType = RawMessageFactory.DeleteOrderMessageType;
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
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

	protected void deserializeContent( ByteBuffer inboundcontent )
	{
		OrderID = inboundcontent.getLong();
	}
   
   public DeleteOrderMessage fromString(String rawString)
   {
      String[] fields = getFieldTokens(rawString);
      populateHeaderFromString(fields);
      int index = getLastHeaderFieldIndex();
      OrderID = Long.valueOf(fields[index+=2]);
      return this;
   }   

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());		
		str.append("OrderID=");
		str.append(OrderID);      
      str.append( "|");
		return str.toString();
	} 

}

	