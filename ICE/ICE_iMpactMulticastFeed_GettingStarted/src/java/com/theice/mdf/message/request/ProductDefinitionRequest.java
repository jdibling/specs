package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

import com.theice.mdf.message.RawMessageFactory;

/**
 * ProductDefinitionRequest.java
 * @author David Chen
 */

public class ProductDefinitionRequest extends Request
{
	private static final short MESSAGE_LENGTH = 10;
   public static final char SECURITY_TYPE_OPTION = 'O';
   public static final char SECURITY_TYPE_FUTRES_OTC = 'F';
   public static final char SECURITY_TYPE_UDS_OPTIONS = 'U';
   public static final char SECURITY_TYPE_UDS_FUTURES = 'D';

	public short MarketType;
   public char SecurityType = SECURITY_TYPE_FUTRES_OTC;

   public ProductDefinitionRequest()
   {
      MessageType = RawMessageFactory.ProductDefinitionRequestType;
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
         SerializedContent.putShort(MarketType );
         SerializedContent.put((byte)SecurityType);

			SerializedContent.rewind();
		}

		return SerializedContent.array();
	}

	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
      MarketType = inboundcontent.getShort();

      // to make it backward compatible, check hasRemaining first
      // before we read security type, old client would not send us the flag
      if (inboundcontent.hasRemaining())
      {
         SecurityType = (char) inboundcontent.get();
      }
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("MarketType=");
		str.append(MarketType);
		str.append( "|");
		str.append("SecurityType=");
		str.append(SecurityType);
		str.append( "|");

		return str.toString();
	}
   
}

	