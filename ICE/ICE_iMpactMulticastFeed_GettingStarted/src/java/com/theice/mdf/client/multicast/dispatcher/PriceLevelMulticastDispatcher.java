package com.theice.mdf.client.multicast.dispatcher;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.OptionMarket;
import com.theice.mdf.client.domain.state.MarketStreamState;
import com.theice.mdf.client.gui.AbstractMDFDialog;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketMessageSequencer;
import com.theice.mdf.client.multicast.handler.NewFlexOptionDefinitionHandler;
import com.theice.mdf.client.multicast.handler.NewOptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.NewOptionStrategyDefinitionMessage;
import com.theice.mdf.message.notification.NewOptionsMarketDefinitionMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class PriceLevelMulticastDispatcher extends BasicMulticastDispatcher 
{
    static final Logger logger=Logger.getLogger(PriceLevelMulticastDispatcher.class.getName());

    public PriceLevelMulticastDispatcher(String multicastGroupName, MarketHandlerFactoryInterface factory)
    {
    	super(multicastGroupName, factory);
    }

    protected String getName()
    {
    	return("PriceLevelMulticastDispatcher");
    }
    
    /**
     * check validity of message
     * Create the appropriate key based on whether this market is options or not
     * 
     * @param message
     * @return market key
     */
    protected MarketKey checkValidityOfMessage(MDSequencedMessage message)
    {
    	MarketInterface market=MarketsHolder.getInstance().findMarket(message.getMarketID());
    	MarketKey key=null;
    	
    	if (market==null)
    	{
    	   if (logger.isDebugEnabled())
    	   {
    	      logger.debug("Market Not found in the cache (PLMulticastDispatcher): "+message.getMarketID());
    	   }
    	   
    	   //UDS or Flex Options that are created dynamically
    	   if (message.getMessageType()==RawMessageFactory.NewOptionStrategyDefinitionMessageType ||
    	       message.getMessageType()==RawMessageFactory.NewOptionsMarketDefinitionMessageType )
    	   {
    	      OptionMarket newOptionsMarket = null;
    	      if (message.getMessageType()==RawMessageFactory.NewOptionStrategyDefinitionMessageType)
    	      {
    	         newOptionsMarket = NewOptionStrategyDefinitionHandler.getInstance().processNewUDSMarketDefinition((NewOptionStrategyDefinitionMessage)message);
    	      }
    	      else
    	      {
    	         newOptionsMarket = NewFlexOptionDefinitionHandler.getInstance().processNewFlexOptionMarketDefinition((NewOptionsMarketDefinitionMessage)message);
    	      }

    	      if (newOptionsMarket!=null)
    	      {
    	         MarketMessageSequencer sequencer = _stateManager.initSequencer(newOptionsMarket.getMarketKey());
    	         sequencer.setState(MarketStreamState.READY);
    	         _stateManager.markReady(newOptionsMarket.getMarketKey());
    	           	            	         
    	         return newOptionsMarket.getMarketKey();
    	      }
    	   }
    	}
    	else
    	{
    	   key=market.getMarketKey();
    	   if(logger.isTraceEnabled())
    	  	{
    	  	   StringBuffer buf=new StringBuffer("");
    	  	   buf.append("Key : ").append(key.toString());
    	  	   buf.append(" ... for message : ").append(message.toString());
    	  	   logger.trace(buf.toString());
    	  	}
    	}
    	
    	return(key);
    }
    
}

