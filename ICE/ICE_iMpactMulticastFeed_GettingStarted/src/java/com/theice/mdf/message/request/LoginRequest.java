package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

/**
 * LoginRequest.java
 * @author David Chen
 */

public class LoginRequest extends Request
{
	private static final short MESSAGE_LENGTH = 70;

	public char UserName[] = new char[30];
	public char Password[] = new char[30];
   public char GetStripInfoMessages = 'N';
   public short ReservedField1 = 0;

   public LoginRequest()
   {
      MessageType = RawMessageFactory.LoginRequestType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
			SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
			SerializedContent.putInt( RequestSeqID );
			for( int i=0; i<UserName.length  ; i++ )
			{
				SerializedContent.put( (byte)UserName[i] );
			}

			for( int i=0; i<Password.length  ; i++ )
			{
				SerializedContent.put( (byte)Password[i] );
			}
			
			SerializedContent.put((byte)GetStripInfoMessages);
			SerializedContent.putShort(ReservedField1);

			SerializedContent.rewind();
		}

		return SerializedContent.array();
	}

	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();

		for( int i=0; i<UserName.length  ; i++ )
		{
			UserName[i] = (char)inboundcontent.get();
		}

		for( int i=0; i<Password.length  ; i++ )
		{
			Password[i] = (char)inboundcontent.get();
		}

		if (inboundcontent.remaining() > 0)
		{
		   GetStripInfoMessages = (char)inboundcontent.get();
		}
		
		if (inboundcontent.remaining() > 0)
		{
		   ReservedField1 = inboundcontent.getShort();
		}
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("UserName=");
		str.append(MessageUtil.toString(UserName));
		str.append( "|");
		str.append("Password=");
		str.append("*****");
		str.append( "|");
		str.append("GetStripInfoMessages=");
		str.append(GetStripInfoMessages);
		str.append("|");
		str.append("ReservedField1=");
		str.append(ReservedField1);
		str.append("|");
		
		return str.toString();
	}

}

	