package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

import com.theice.mdf.message.RawMessageFactory;

/**
 * DebugRequest.java
 * @author David Chen
 */

public class DebugRequest extends Request
{
	private static final short MESSAGE_LENGTH = 7;

   public DebugRequest()
   {
      MessageType = RawMessageFactory.DebugRequestType;
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

			SerializedContent.rewind();
		}

		return SerializedContent.array();
	}

	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
	}

}

	