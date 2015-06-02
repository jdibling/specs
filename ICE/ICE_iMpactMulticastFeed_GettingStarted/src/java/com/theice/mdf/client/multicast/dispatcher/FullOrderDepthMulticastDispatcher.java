package com.theice.mdf.client.multicast.dispatcher;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MainMarketKey;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.message.MDSequencedMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class FullOrderDepthMulticastDispatcher extends BasicMulticastDispatcher 
{
    static final Logger logger=Logger.getLogger(FullOrderDepthMulticastDispatcher.class.getName());

    public FullOrderDepthMulticastDispatcher(String multicastGroupName, MarketHandlerFactoryInterface factory)
    {
    	super(multicastGroupName, factory);
    }

    protected String getName()
    {
    	return("FullOrderDepthMulticastDispatcher");
    }
    
    /**
     * check validity of message
     * 
     * For full order context, the market can never be an OptionsMarket. So we just create a main market key
     * instead of looking up the market holder
     * 
     * @param message
     * @return market key
     */
    protected MarketKey checkValidityOfMessage(MDSequencedMessage  message)
    {
    	MarketKey key=null;
    	
    	key=new MainMarketKey(message.getMarketID());
    	
    	if(logger.isTraceEnabled())
    	{
    		StringBuffer buf=new StringBuffer("");
    		buf.append("Key : ").append(key.toString());
    		buf.append(" ... for message : ").append(message.toString());
        	logger.trace(buf.toString());
    	}
    	
    	return(key);
    }
    
}

