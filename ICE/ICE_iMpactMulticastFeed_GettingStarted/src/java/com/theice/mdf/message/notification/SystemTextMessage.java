package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

/**
 * SystemTextMessage.java
 * @author David Chen
 */

public class SystemTextMessage extends MDSequencedMessage
{
	private static final short MESSAGE_LENGTH = 1011;

	public char Text[] = new char[200];
	public long DateTime;
   public char TextExtraFld[] = new char[800];

   public SystemTextMessage()
   {
      MessageType = RawMessageFactory.SystemTextMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public int getMarketID()
   {
      return -1;
   }

   public void setFullTextMessage(String fullText)
   {
      // convert full contract symbol to bytes, extra characters would be truncated
      Text = MessageUtil.toRawChars(fullText, Text.length);

      if ((fullText!=null) &&(fullText.length()>Text.length))
      {
         // ContractSymbol is not enough to hold the full symbol, extra field is needed
         TextExtraFld = MessageUtil.toRawChars(fullText.substring(Text.length), TextExtraFld.length);
      }
      else
      {
         // no extra field needed
         TextExtraFld = MessageUtil.toRawChars("", TextExtraFld.length);
      }
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

			SerializedContent.putLong( DateTime );

			for( int i=0; i<TextExtraFld.length  ; i++ )
			{
				SerializedContent.put( (byte)TextExtraFld[i] );
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
			strBuf.append( DateTime );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( MessageUtil.toString(TextExtraFld) );
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
		DateTime = inboundcontent.getLong();

      if (inboundcontent.hasRemaining())
      {
         for( int i=0; i<TextExtraFld.length  ; i++ )
         {
            TextExtraFld[i] = (char)inboundcontent.get();
         }
      }
   }

   public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("Text=");
		str.append(MessageUtil.toString(Text));
		str.append( "|");
		str.append("DateTime=");
		str.append(DateTime);
		str.append( "|");
		str.append("TextExtraFld=");
		str.append(MessageUtil.toString(TextExtraFld));
		str.append( "|");
		return str.toString();
	}

   public void setMarketID(int MarketID)
   {
   }

}

	