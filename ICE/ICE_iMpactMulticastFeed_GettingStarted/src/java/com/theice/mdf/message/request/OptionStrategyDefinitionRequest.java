package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

/**
 * ProductDefinitionRequest.java
 * @author David Chen
 */

public class OptionStrategyDefinitionRequest extends Request
{
   private static final short MESSAGE_LENGTH = 9;
   
   public short MarketType;

   public OptionStrategyDefinitionRequest()
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
         SerializedContent.putShort(MarketType );
         
         SerializedContent.rewind();
      }

      return SerializedContent.array();
   }

   public void deserialize( ByteBuffer inboundcontent )
   {
      RequestSeqID = inboundcontent.getInt();
      MarketType = inboundcontent.getShort();
   }

   public String toString()
   {
      StringBuilder str = new StringBuilder(super.toString());
            
      str.append("MarketType=");
      str.append(MarketType);
      str.append( "|");

      return str.toString();
   }
}

	