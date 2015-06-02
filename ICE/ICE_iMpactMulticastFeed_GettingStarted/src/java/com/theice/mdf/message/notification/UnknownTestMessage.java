package com.theice.mdf.message.notification;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;

import java.nio.ByteBuffer;

/**
 * UnknownTestMessage.java
 *
 * This message is only sent out in test environment at random time. The format is not specified in the spec to make
 * sure that client can really handle it in the test env.
 *
 * @author David Chen
 */
public class UnknownTestMessage  extends MDSequencedMessage
{
   // this is a test message, the length is random at initializtion
   private static final short TEXT_FLD_LENGTH = 200;
	private static short MESSAGE_LENGTH = (short)(HEADER_LENGTH + TEXT_FLD_LENGTH);

	public char Text[] = new char[TEXT_FLD_LENGTH];

   public UnknownTestMessage()
   {
      MessageType = RawMessageFactory.UnknownTestMessageType;
      MessageBodyLength = (short) (MESSAGE_LENGTH - HEADER_LENGTH);
      Text = MessageUtil.toRawChars("This is an unknown test message that is only sent out in test env. Per iMpact spec, " +
               "client is required to handle it, by reading the right number of bytes based on the body length value.", Text.length);
   }

   public int getMarketID()
   {
      return -1;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			for( int i=0; i<Text.length  ; i++ )
			{
				SerializedContent.put( (byte)Text[i] );
			}

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

			strBuf.append( MessageUtil.toString(Text) );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   public void deserializeContent(ByteBuffer inboundcontent)
   {
		for( int i=0; i<Text.length  ; i++ )
		{
			Text[i] = (char)inboundcontent.get();
		}
   }

   public String toString()
	{
		StringBuffer str = new StringBuffer();

      str.append(super.toString());
		str.append("Text=");
		str.append(MessageUtil.toString(Text));
		str.append( "|");
		return str.toString();
	}

   public void setMarketID(int MarketID)
   {
   }
}
