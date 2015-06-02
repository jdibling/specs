package com.theice.mdf.message.response;

import java.nio.ByteBuffer;

public class OptionStrategyHedgeDefinition
{
   public static final byte MESSAGE_LENGTH = 18;
   public int HedgeMarketID;
   public char HedgeSecurityType;
   public char HedgeSide;
   public long HedgePrice;
   public char HedgePriceDenominator;
   public short HedgeDelta;
   private byte _actualMessageLength=MESSAGE_LENGTH;

   public byte[] serialize(ByteBuffer serializedContent)
   {
      if (serializedContent == null)
      {
         return null;
      }
      
      serializedContent.put(MESSAGE_LENGTH);
      serializedContent.putInt(HedgeMarketID);
      serializedContent.put((byte)HedgeSecurityType);
      serializedContent.put((byte)HedgeSide);
      serializedContent.putLong(HedgePrice);
      serializedContent.put((byte)HedgePriceDenominator);
      serializedContent.putShort(HedgeDelta);
      
      return serializedContent.array();
   }
   
   public void deserialize( ByteBuffer inboundcontent )
   {
      _actualMessageLength = inboundcontent.get();
      HedgeMarketID = inboundcontent.getInt();
      HedgeSecurityType = (char)inboundcontent.get();
      HedgeSide = (char)inboundcontent.get();
      HedgePrice = inboundcontent.getLong();
      HedgePriceDenominator = (char)inboundcontent.get();
      HedgeDelta = inboundcontent.getShort();
   }
   
   public byte getActualMessageLength()
   {
      return _actualMessageLength;
   }
   
   public String toString()
   {
      StringBuilder strBuilder = new StringBuilder("OptionStrategyHedgeDefinition|MessageLength=");
      strBuilder.append(_actualMessageLength);
      strBuilder.append("|HedgeMarketID=");
      strBuilder.append(HedgeMarketID);
      strBuilder.append("|HedgeSecurityType=");
      strBuilder.append(HedgeSecurityType);
      strBuilder.append("|HedgeSide=");
      strBuilder.append(HedgeSide);
      strBuilder.append("|HedgePrice=");
      strBuilder.append(HedgePrice);
      strBuilder.append("|HedgePriceDenominator=");
      strBuilder.append(HedgePriceDenominator);
      strBuilder.append("|HedgeDelta=");
      strBuilder.append(HedgeDelta);
      strBuilder.append("|");
      
      return strBuilder.toString();
   }
}
