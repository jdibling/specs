package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

/**
 * RequestFeedByMarketType.java
 * @author David Chen
 */

public class RequestFeedByMarketType extends Request
{
	private static final short MESSAGE_LENGTH = 12;

	public short MarketType;
	public short MarketDepth;
   public char GetOptionsMessages = 'N';

   public RequestFeedByMarketType()
   {
      MessageType = '3';
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
         SerializedContent.putShort( MarketType );
			SerializedContent.putShort( MarketDepth );
         SerializedContent.put( (byte)GetOptionsMessages );

			SerializedContent.rewind();
		}

		return SerializedContent.array();
	}

	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
		MarketType = inboundcontent.getShort();
		MarketDepth = inboundcontent.getShort();
      if (inboundcontent.remaining()>0)
      {
         GetOptionsMessages = (char)inboundcontent.get();
      }

	}

	public String toString()
	{
		StringBuffer str = new StringBuffer();
		
      str.append(super.toString());
		str.append("MarketType=");
		str.append(MarketType);
		str.append( "|");
		str.append("MarketDepth=");
		str.append(MarketDepth);
		str.append( "|");
      str.append("GetOptionsMessages=");
      str.append(GetOptionsMessages);
      str.append( "|");
		return str.toString();
	}

   
}

	