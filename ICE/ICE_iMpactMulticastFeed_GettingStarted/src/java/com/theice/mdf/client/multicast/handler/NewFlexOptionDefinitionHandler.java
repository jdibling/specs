package com.theice.mdf.client.multicast.handler;

import java.util.List;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.NewOptionStrategyDefinitionMessage;
import com.theice.mdf.message.notification.NewOptionsMarketDefinitionMessage;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.handlers.AbstractMarketMessageHandler;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.OptionMarket;
import com.theice.mdf.client.domain.state.MarketStreamState;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 */
public class NewFlexOptionDefinitionHandler extends AbstractMarketMessageHandler
{
   private static NewFlexOptionDefinitionHandler _instance=new NewFlexOptionDefinitionHandler();

   private static final Logger logger=Logger.getLogger(NewFlexOptionDefinitionHandler.class.getName());

   public static NewFlexOptionDefinitionHandler getInstance()
   {
      return _instance;
   }

   private NewFlexOptionDefinitionHandler()
   {
   }

   /**
    * handle the message
    * @param message
    */
   protected void handleMessage(PriceFeedMessage priceFeedMessage)
   {
      //special case
      //new Flex Options markets are handled in PriceLevelMulticastDispatcher (message consuming thread), which then
      //calls the processNewFlexOptionMarketDefinition(NewOptionStrategyDefinitionMessage) method in this class.
      System.out.println("NewFlexOptionDefinitionHandler: "+priceFeedMessage);
   }
   
   public OptionMarket processNewFlexOptionMarketDefinition(NewOptionsMarketDefinitionMessage message)
   {
      MarketInterface underlyingMarket = MarketsHolder.getInstance().findMarket(message.UnderlyingMarketID);
      if (underlyingMarket==null)
      {
         logger.error("Underlying market not found for Flex Options. Options marketID="+message.getMarketID()+", underlying marketID="+message.UnderlyingMarketID);
         return null;
      }

      OptionMarket flexOptionsMarket=new OptionMarket(message,underlyingMarket);
      MarketsHolder.getInstance().storeMarket(flexOptionsMarket);

      /**
       * Update the Models inside the context
       */
      AppManager.getAppContext().cacheMarket(flexOptionsMarket);

      return flexOptionsMarket;
   }

}


