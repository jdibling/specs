/**
 * Created by IntelliJ IDEA.
 * User: Adam Athimuthu
 * Date: Aug 3, 2007
 * Time: 11:23:40 AM
 * To change this template use File | Settings | File Templates.
 */
package com.theice.mdf.client.process.handlers;

import java.util.List;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketStateManager;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.OptionMarket;
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
 * @author Adam Athimuthu
 */
public class OptionsProductDefinitionHandler extends AbstractMarketMessageHandler
{
    private static OptionsProductDefinitionHandler _instance=new OptionsProductDefinitionHandler();

    private static final Logger logger=Logger.getLogger(OptionsProductDefinitionHandler.class.getName());
    
    public static OptionsProductDefinitionHandler getInstance()
    {
        return _instance;
    }

    private OptionsProductDefinitionHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
        Market underlyingMarket=null;
        OptionMarket optionsMarket=null;
        
        if(logger.isTraceEnabled())
        {
        	logger.trace("Received Options Product Definition : "+message.toString());
        }
        
        OptionsProductDefinitionResponse theMessage=(OptionsProductDefinitionResponse) message;

        /**
         * Locate the underlying market
         * Create the market in the appropriate hash table belonging to the market type
		 * an option market is always initialized with a PriceLevelBook in the OptionMarket constructor
		 */
        underlyingMarket=(Market) MarketsHolder.getInstance().findMarket(theMessage.UnderlyingMarketID);
        
        if(underlyingMarket==null)
        {
        	logger.error("Underlying market not found while trying to create OptionsMarket : "+theMessage.toString());
        	return;
        }

        optionsMarket=new OptionMarket(theMessage,underlyingMarket);
        
        MarketsHolder.getInstance().storeMarket(optionsMarket);
        
        /**
         * Update the Models inside the context
         */
        AppManager.getAppContext().cacheMarket(optionsMarket);
        
        /**
         * Init/create the market sequencer with a NOT READY state
         * if the channel context is for options, we don't need to track the underlying markets
         * If we have the options product definition handler registered, we are certainly operating in the options mode
         */
        
        OptionsProductDefinitionResponse response = (OptionsProductDefinitionResponse)message;
        short marketTypeID = response.RequestMarketType;
        List<String> multicastGroupNamesList = ProductDefinitionHandler.getMulticastGroupNameByMarketTypeID(marketTypeID);
        
        search:
        for(String multicastGroupName:multicastGroupNamesList)
        {
           if (multicastGroupName.contains("Options") && MarketStateManager.getInstance(multicastGroupName)!=null)
           {
              MarketStateManager.getInstance(multicastGroupName).initSequencer(optionsMarket.getMarketKey());
              break search;
           }
        }
        
        return;
    }

}

