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
 * Created: Sep 26, 2007 11:03:55 AM
 *
 *
 */
public class DeletePriceLevelMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 9;

   protected char _side;
   protected byte _priceLevelPosition;

   public DeletePriceLevelMessage()
   {
      MessageType = RawMessageFactory.DeletePriceLevelMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

	public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.put( (byte)_side );
         SerializedContent.put( _priceLevelPosition );

         SerializedContent.rewind();

         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
      }

      return SerializedContent.array();
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
         strBuf.append( LOG_FLD_DELIMITER );
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   protected void deserializeContent( ByteBuffer inboundcontent )
   {    
      _side = (char)inboundcontent.get();
      _priceLevelPosition = inboundcontent.get();
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
      return str.toString();
   }
   
   public void fromString(String rawString)
   {
      String[] fields = getFieldTokens(rawString);
      populateHeaderFromString(fields);
      MessageBodyLength = Short.valueOf(fields[3]);
      int index = getLastHeaderFieldIndex();
      _side = fields[index+=2].charAt(0);
      _priceLevelPosition=Byte.valueOf(fields[index+=2]);
   }
   
   public static final class Pool
   {
      private static final BlockingQueue<DeletePriceLevelMessage> PRICE_LEVEL_CACHE = new LinkedBlockingQueue<DeletePriceLevelMessage>();
      private static final int MAX_POOL_SIZE = 1000;
      public static void checkIn(DeletePriceLevelMessage priceLevel)
      {
         //if the cache size is more that 1000, discard the object. This can be fine tuned
         if (PRICE_LEVEL_CACHE.size()<MAX_POOL_SIZE)
         {
            PRICE_LEVEL_CACHE.offer(priceLevel);
         }        
      }
      public static DeletePriceLevelMessage checkOut()
      {
         DeletePriceLevelMessage priceLevel = PRICE_LEVEL_CACHE.poll();
         if (priceLevel == null)
         {
            priceLevel = new DeletePriceLevelMessage();
         }
         return priceLevel;
      }
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
