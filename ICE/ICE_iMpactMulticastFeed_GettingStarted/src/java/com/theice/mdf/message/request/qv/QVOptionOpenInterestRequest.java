package com.theice.mdf.message.request.qv;

import java.nio.ByteBuffer;

import com.theice.mdf.message.request.Request;


/**
 * Domain class for QV Option Open Interest Request
 * 
 * @author qwang
 * @version     %I%, %G%
 * Created: Apr 17, 2007 1:36:57 PM
 *
 *
 */
public class QVOptionOpenInterestRequest extends Request
{
	private static final short MESSAGE_LENGTH = 7;

   public QVOptionOpenInterestRequest()
   {
      MessageType = 'e';
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

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		return str.toString();
	}
   
}

	