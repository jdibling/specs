package com.theice.mdf.message;

import java.nio.ByteBuffer;

/**
 * <code>MDMessage</code> is the abstract class that should be inherited by all
 * message class. It contains the message type and message body length field which
 * are the first 2 fields in every message..
 *
 * @author David Chen
 * @version %I%, %G%
 * @since 12/12/2006
 */

public abstract class MDMessage implements Cloneable
{
   public static final short HEADER_LENGTH = 3;
   protected static final String LOG_FLD_DELIMITER = "|";

	protected ByteBuffer SerializedContent;
	protected char MessageType = ' ';
   protected short MessageBodyLength;
   public static boolean SHORT_LOG_STR_PRE_ALLOCATED = false;
   protected String ShortLogStr = null;

   public MDMessage()
   {
   }
   
   public MDMessage(MDMessage mdMsg)
   {
      MessageType = mdMsg.MessageType;
      MessageBodyLength = mdMsg.MessageBodyLength;
   }

	public abstract byte[] serialize();

	public abstract void deserialize(ByteBuffer content);

   public abstract int getMarketID();

   public abstract void setMarketID(int MarketID);

   public abstract String getShortLogStr();

   public short getMessageBodyLength()
   {
      return MessageBodyLength;
   }

   public short getMessageLength()
   {
      return (short)(MessageBodyLength + HEADER_LENGTH);
   }

   public char getMessageType()
   {
      return MessageType;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
		str.append("MessageType=");
		str.append(MessageType);
		str.append( LOG_FLD_DELIMITER);
		str.append("MessageBodyLength=");
		str.append(MessageBodyLength);
		str.append(LOG_FLD_DELIMITER);

      return str.toString();
   }

   protected void serializeHeader()
   {
      SerializedContent.put( (byte)MessageType );
      SerializedContent.putShort(MessageBodyLength );
   }

   public String getLogHeaderShortStr()
   {
      StringBuffer str = new StringBuffer();
      str.append(MessageType);
      str.append(LOG_FLD_DELIMITER);
      str.append(MessageBodyLength);
      str.append(LOG_FLD_DELIMITER);
      return str.toString();
   }

   public boolean isImpliedOrderMsg()
   {
      return false;
   }

   /**
    * @param messageBodyLength the messageBodyLength to set
    */
   public void setMessageBodyLength(short messageBodyLength)
   {
      MessageBodyLength = messageBodyLength;
   }

   // most of the attributes for message classes are primitive
   // other than the serialized content, set it to null
   // so that the cloned object has no chance to use it or change it
   public Object clone() throws CloneNotSupportedException
   {
      MDMessage msg = (MDMessage) super.clone();
      msg.SerializedContent = null;
      
      return msg;
   }

}
