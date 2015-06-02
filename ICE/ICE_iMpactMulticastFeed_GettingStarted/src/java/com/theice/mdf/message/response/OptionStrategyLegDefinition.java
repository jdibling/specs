package com.theice.mdf.message.response;

import java.nio.ByteBuffer;

public class OptionStrategyLegDefinition
{
   public static final byte MESSAGE_LENGTH = 12;
  
   public int LegMarketID;
   public int LegUnderlyingMarketID;
   public short LegRatio;
   public char LegSide;
   private byte _actualMessageLength = MESSAGE_LENGTH;
   
   public void serialize(ByteBuffer serializedContent)
   {
      if (serializedContent != null)
      {
         serializedContent.put(MESSAGE_LENGTH);
         serializedContent.putInt(LegMarketID);
         serializedContent.putInt(LegUnderlyingMarketID);
         serializedContent.putShort(LegRatio);
         serializedContent.put((byte)LegSide);
      }
   }
   
   public void deserialize( ByteBuffer inboundcontent )
   {
      _actualMessageLength = inboundcontent.get();
      LegMarketID = inboundcontent.getInt();
      LegUnderlyingMarketID = inboundcontent.getInt();
      LegRatio = inboundcontent.getShort();
      LegSide = (char)inboundcontent.get();
   }
   
   public byte getActualMessageLength()
   {
      return _actualMessageLength;
   }
   
   public String toString()
   {
      StringBuilder strBuilder = new StringBuilder("OptionStrategyLegDefinition|MessageLength=");
      strBuilder.append(_actualMessageLength);
      strBuilder.append("|LegMarketID=");
      strBuilder.append(LegMarketID);
      strBuilder.append("|LegUnderlyingMarketID=");
      strBuilder.append(LegUnderlyingMarketID);
      strBuilder.append("|LegRatio=");
      strBuilder.append(LegRatio);
      strBuilder.append("|LegSide=");
      strBuilder.append(LegSide);
      strBuilder.append("|");
      
      return strBuilder.toString();
   }
}
