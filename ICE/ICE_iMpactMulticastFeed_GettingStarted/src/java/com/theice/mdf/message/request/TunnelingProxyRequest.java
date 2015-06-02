package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

import com.theice.mdf.message.RawMessageFactory;

/**
 * RequestFeedByMarketType.java
 * @author David Chen
 */

public class TunnelingProxyRequest extends Request
{
	private static final short MESSAGE_LENGTH = 15;

	public long TunnelingMagicNumber;

   public TunnelingProxyRequest()
   {
      MessageType = RawMessageFactory.TunnelingProxyRequestType;
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
         SerializedContent.putLong(TunnelingMagicNumber);

         SerializedContent.rewind();
		}

		return SerializedContent.array();
	}

	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
		TunnelingMagicNumber = inboundcontent.getLong();
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("TunnelingMagicNumber=");
		str.append(TunnelingMagicNumber);
		str.append( "|");
		return str.toString();
	}
}

