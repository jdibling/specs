/*
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights Reserved.
 */
package com.theice.mdf.message.pricelevel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.theice.mdf.message.RawMessageFactory;


/**
 * @author qwang
 * @version     %I%, %G%
 * Created: Sep 26, 2007 11:04:10 AM
 *
 *
 */
public class ChangePriceLevelMessage extends AddPriceLevelMessage
{
   
   public ChangePriceLevelMessage()
   {
      MessageType = RawMessageFactory.ChangePriceLevelMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }
   
   public static final class Pool
   {
      private static final BlockingQueue<ChangePriceLevelMessage> PRICE_LEVEL_CACHE = new LinkedBlockingQueue<ChangePriceLevelMessage>();
      private static final int MAX_POOL_SIZE = 1000;
      public static void checkIn(ChangePriceLevelMessage priceLevel)
      {
         //if the cache size is more that 1000, discard the object. This can be fine tuned
         if (PRICE_LEVEL_CACHE.size()<MAX_POOL_SIZE)
         {
            PRICE_LEVEL_CACHE.offer(priceLevel);
         }        
      }
      public static ChangePriceLevelMessage checkOut()
      {
         ChangePriceLevelMessage priceLevel = PRICE_LEVEL_CACHE.poll();
         if (priceLevel == null)
         {
            priceLevel = new ChangePriceLevelMessage();
         }
         return priceLevel;
      }
   }   
}
