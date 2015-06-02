/*
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights Reserved.
 */
package com.theice.mdf.message.pricelevel;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.RawMessageFactory;


/**
 * @author qwang
 * @version     %I%, %G%
 * Created: Sep 26, 2007 11:03:36 AM
 *
 *
 */
public class AddPriceLevelMessage extends MDSequencedMessageWithMarketID
{
   protected static final short MESSAGE_LENGTH = 29;

   protected char _side;
   protected byte _priceLevelPosition;
   protected long _price;
   protected int _quantity;
   protected short _orderCount;
   protected int _impliedQuantity;
   protected short _impliedOrderCount;

   public AddPriceLevelMessage()
   {
      MessageType = RawMessageFactory.AddPriceLevelMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         serializeContent(SerializedContent);

         SerializedContent.rewind();

         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
      }

      return SerializedContent.array();
   }

   protected void serializeContent(ByteBuffer byteBuffer)
   {
      byteBuffer.put( (byte)_side );
      byteBuffer.put( _priceLevelPosition );
      byteBuffer.putLong( _price );
      byteBuffer.putInt( _quantity );
      byteBuffer.putShort( _orderCount );
      byteBuffer.putInt( _impliedQuantity );
      byteBuffer.putShort( _impliedOrderCount );
   }

   public String getShortLogStr()
   {
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());
         strBuf.append( _side );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _priceLevelPosition );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _price );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _quantity );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _orderCount );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _impliedQuantity );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _impliedOrderCount );
         strBuf.append( LOG_FLD_DELIMITER );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   protected void deserializeContent( ByteBuffer inboundcontent )
   {      
      _side = (char)inboundcontent.get();
      _priceLevelPosition = inboundcontent.get();
      _price = inboundcontent.getLong();
      _quantity = inboundcontent.getInt();
      _orderCount = inboundcontent.getShort();
      _impliedQuantity = inboundcontent.getInt();
      _impliedOrderCount = inboundcontent.getShort(); 
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("Side=");
      str.append(_side);
      str.append( "|");
      str.append("PriceLevelPosition=");
      str.append(_priceLevelPosition);
      str.append( "|");     
      str.append("Price=");
      str.append(_price);
      str.append( "|");
      str.append("Quantity=");
      str.append(_quantity);
      str.append( "|");     
      str.append("OrderCount=");
      str.append(_orderCount);
      str.append( "|");
      str.append("ImpliedQuantity=");
      str.append(_impliedQuantity);
      str.append( "|");
      str.append("ImpliedOrderCount=");
      str.append(_impliedOrderCount);
      str.append( "|");
      return str.toString();
   }
   
   public void fromString(String rawString)
   {
      String[] fields = getFieldTokens(rawString);
      populateHeaderFromString(fields);
      int index = getLastHeaderFieldIndex();
      _side = fields[index+=2].charAt(0);
      _priceLevelPosition=Byte.valueOf(fields[index+=2]);
      _price = Long.valueOf(fields[index+=2]);
      _quantity = Integer.valueOf(fields[index+=2]);
      _orderCount=Short.valueOf(fields[index+=2]);    
      _impliedQuantity=Integer.valueOf(fields[index+=2]);
      _impliedOrderCount=Short.valueOf(fields[index+=2]);
   }

   
   public static final class Pool
   {
      private static final BlockingQueue<AddPriceLevelMessage> PRICE_LEVEL_CACHE = new LinkedBlockingQueue<AddPriceLevelMessage>();
      private static final int MAX_POOL_SIZE = 1000;
      public static void checkIn(AddPriceLevelMessage priceLevel)
      {
         //if the cache size is more that 1000, discard the object. This can be fine tuned
         if (PRICE_LEVEL_CACHE.size()<MAX_POOL_SIZE)
         {
            PRICE_LEVEL_CACHE.offer(priceLevel);
         }        
      }
      public static AddPriceLevelMessage checkOut()
      {
         AddPriceLevelMessage priceLevel = PRICE_LEVEL_CACHE.poll();
         if (priceLevel == null)
         {
            priceLevel = new AddPriceLevelMessage();
         }
         return priceLevel;
      }
   }


   /**
    * @return the impliedOrderCount
    */
   public short getImpliedOrderCount()
   {
      return _impliedOrderCount;
   }

   /**
    * @param impliedOrderCount the impliedOrderCount to set
    */
   public void setImpliedOrderCount(short impliedOrderCount)
   {
      _impliedOrderCount = impliedOrderCount;
   }

   /**
    * @return the impliedQuantity
    */
   public int getImpliedQuantity()
   {
      return _impliedQuantity;
   }

   /**
    * @param impliedQuantity the impliedQuantity to set
    */
   public void setImpliedQuantity(int impliedQuantity)
   {
      _impliedQuantity = impliedQuantity;
   }

   /**
    * @return the orderCount
    */
   public short getOrderCount()
   {
      return _orderCount;
   }

   /**
    * @param orderCount the orderCount to set
    */
   public void setOrderCount(short orderCount)
   {
      _orderCount = orderCount;
   }

   /**
    * @return the price
    */
   public long getPrice()
   {
      return _price;
   }

   /**
    * @param price the price to set
    */
   public void setPrice(long price)
   {
      _price = price;
   }

   /**
    * @return the priceLevelPosition
    */
   public byte getPriceLevelPosition()
   {
      return _priceLevelPosition;
   }

   /**
    * @param priceLevelPosition the priceLevelPosition to set
    */
   public void setPriceLevelPosition(byte priceLevelPosition)
   {
      _priceLevelPosition = priceLevelPosition;
   }

   /**
    * @return the quantity
    */
   public int getQuantity()
   {
      return _quantity;
   }

   /**
    * @param quantity the quantity to set
    */
   public void setQuantity(int quantity)
   {
      _quantity = quantity;
   }

   /**
    * @return the side
    */
   public char getSide()
   {
      return _side;
   }

   /**
    * @param side the side to set
    */
   public void setSide(char side)
   {
      _side = side;
   }
   
}
