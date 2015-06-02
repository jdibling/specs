package com.theice.mdf.client.process.handlers;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.StripInfoMessage;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Shawn Cheng
 */
public class StripInfoMessageHandler extends AbstractMarketMessageHandler
{
   private static StripInfoMessageHandler _instance=new StripInfoMessageHandler();

   private static Logger logger=Logger.getLogger(StripInfoMessageHandler.class.getName());

   private StripInfoMessageHandler()
   {
   }

   public static StripInfoMessageHandler getInstance()
   {
      return(_instance);
   }

   /**
    * handle the message
    * @param message
    */
   protected void handleMessage(PriceFeedMessage priceFeedMessage)
   {
      MDMessage message=priceFeedMessage.getMessage();

      char messageType=message.getMessageType();

      if(logger.isTraceEnabled())
      {
         logger.trace("Processing StripInfoMessage: "+message.toString());
      }

      StripInfoMessage theMessage=(StripInfoMessage) message;
      System.out.println(theMessage);

      return;
   }
}


