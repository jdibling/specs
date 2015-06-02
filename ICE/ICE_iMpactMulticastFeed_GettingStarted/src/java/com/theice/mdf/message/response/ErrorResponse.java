package com.theice.mdf.message.response;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

/**
 * ErrorResponse.java
 * @author David Chen
 */

public class ErrorResponse extends Response
{
	private static final short MESSAGE_LENGTH = 108;

   public static final char ERROR_UNKNOWN_REQUEST_TYPE               = '1';
   public static final char ERROR_INVALID_MARKET_TYPE                = '2';
   public static final char ERROR_MARKET_TYPE_ACCESS_DENIED          = '3';
   public static final char ERROR_REQUEST_NOT_ALLOWED_UNTIL_LOGGEDIN = '4';
   public static final char ERROR_OTHER                              = 'X';

	public char Code;		
	public char Text[] = new char[100];

   public ErrorResponse()
   {
      MessageType = RawMessageFactory.ErrorResponseType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
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
			SerializedContent.putInt( RequestSeqID );
			SerializedContent.put( (byte)Code );
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

			strBuf.append( RequestSeqID );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( Code );
         strBuf.append( LOG_FLD_DELIMITER );
			strBuf.append( MessageUtil.toString(Text) );
         strBuf.append( LOG_FLD_DELIMITER );

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }


	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
		Code = (char)inboundcontent.get();

		for( int i=0; i<Text.length  ; i++ )
		{
			Text[i] = (char)inboundcontent.get();
		}

	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("Code=");
		str.append(Code);
		str.append( "|");
		str.append("Text=");
		str.append(MessageUtil.toString(Text));
		str.append( "|");
		return str.toString();
	}

   public void setMarketID(int MarketID)
   {      
   }

}

	