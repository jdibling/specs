package com.theice.mdf.client.multicast.handler;

import java.util.List;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.NewOptionStrategyDefinitionMessage;
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
 * 
 * This handler processes the options product defintion messages and helps building the data
 * structures for representing options markets.
 * 
 * This is the primary place where an Options Market object gets created first. The market is created with
 * a Price Level context. The Underlying market has to exist first.
 * 
 * TODO Make sure the options markets/sequencer creation is proper
 * TODO check the options snapshot channel
 * TODO check whether subscription to futures vs. options market is mutually exclusive 
 * 
 */
public class NewOptionStrategyDefinitionHandler extends AbstractMarketMessageHandler
{
   private static NewOptionStrategyDefinitionHandler _instance=new NewOptionStrategyDefinitionHandler();

   private static final Logger logger=Logger.getLogger(NewOptionStrategyDefinitionHandler.class.getName());

   public static NewOptionStrategyDefinitionHandler getInstance()
   {
      return _instance;
   }

   private NewOptionStrategyDefinitionHandler()
   {
   }

   /**
    * handle the message
    * @param message
    */
   protected void handleMessage(PriceFeedMessage priceFeedMessage)
   {
      //special case
      //new UDS markets are handled in PriceLevelMulticastDispatcher (message consuming thread), which then
      //calls the processNewUDSMarketDefinition(NewOptionStrategyDefinitionMessage) method in this class.
   }
   
   public OptionMarket processNewUDSMarketDefinition(NewOptionStrategyDefinitionMessage message)
   {
      MarketInterface underlyingMarket = MarketsHolder.getInstance().findMarket(message.UnderlyingMarketID);
      if (underlyingMarket==null)
      {
         logger.error("Underlying market not found for UDS. UDS marketID="+message.getMarketID()+", underlying marketID="+message.UnderlyingMarketID);
         return null;
      }

      OptionMarket udsMarket=new OptionMarket(message,underlyingMarket);
      MarketsHolder.getInstance().storeMarket(udsMarket);

      /**
       * Update the Models inside the context
       */
      AppManager.getAppContext().cacheMarket(udsMarket);

      return udsMarket;
   }

}

