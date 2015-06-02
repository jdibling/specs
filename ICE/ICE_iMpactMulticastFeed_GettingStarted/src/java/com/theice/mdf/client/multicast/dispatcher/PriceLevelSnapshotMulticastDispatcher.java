package com.theice.mdf.client.multicast.dispatcher;

import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.state.MarketStreamState;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.MarketSnapshotMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TODO register the message types at startup so we can apply the filters easily
 * 
 * @author Adam Athimuthu
 */
public class PriceLevelSnapshotMulticastDispatcher extends AbstractSnapshotMulticastDispatcher 
{
    static final Logger logger=Logger.getLogger(PriceLevelSnapshotMulticastDispatcher.class.getName());
    
    public PriceLevelSnapshotMulticastDispatcher(String multicastGroupName)
    {
    	super(multicastGroupName);
    }

    public PriceLevelSnapshotMulticastDispatcher(String multicastGroupName, CountDownLatch shutdownLatch)
    {
    	super(multicastGroupName, shutdownLatch);
    }

    protected String getName()
    {
    	return("PriceLevelSnapshotMulticastDispatcher");
    }
    
    /**
     * Check the validity of message types for this channel
     * @param messageType
     * @return true if the message type is valid for this multicast channel, otherwise return false
     */
    protected MarketKey checkValidityOfMessage(MDSequencedMessage message)
    {
    	MarketKey key=null;
    	
		switch(message.getMessageType())
		{
			case RawMessageFactory.MarketSnapshotMessageType:
			case RawMessageFactory.MarketSnapshotPriceLevelMessageType:

		    	MarketInterface market=MarketsHolder.getInstance().findMarket(message.getMarketID());
		    	
		    	if(market!=null)
		    	{
			    	key=market.getMarketKey();
		    	}
		    	else
		    	{
		    	  if (logger.isDebugEnabled())
		         {
		    	     logger.debug("Market Not found in the cache : "+message.getMarketID());
		         }
		    	}
		    	
				break;
				
			default:
				logger.error(getName()+" - INVALID message type for this channel: "+message.toString());
				break;
		}
		
    	return(key);
    }
    
    /**
     * Check if the message is valid for the given MarketStreamState
     * 
     * Possible state parameters
     * 	NOTREADY
     * 	SNAPSHOTLOADING
     * 
     * @param MarketStreamState
     * @return true if valid, false if we get an unexpected message in this state
     */
    protected boolean isMessageValidInState(MDSequencedMessage message,MarketStreamState state)
    {
    	boolean valid=true;
    	StringBuffer buf=null;
    	
    	if(logger.isTraceEnabled())
    	{
        	buf=new StringBuffer("Message Validity check for market state : ");
        	buf.append(state.toString()).append(" Message = ").append(message.toString());
        	logger.trace(buf.toString());
    	}

    	switch(state.getId())
    	{
			case MarketStreamState.STATEID_NOTREADY:
				if(!(message instanceof MarketSnapshotMessage))
				{
					logger.error("Fatal!! Expected a MarketSnapshot message. Got : "+message.toString());
					valid=false;
				}
				break;
				
			case MarketStreamState.STATEID_SNAPSHOTLOADING:
				
				switch(message.getMessageType())
				{
					case RawMessageFactory.MarketSnapshotPriceLevelMessageType:
						break;
					default:
						buf=new StringBuffer("Unexcpted Message while SnapShotLoading. Got : ");
						buf.append(message.toString());
						logger.error(buf.toString());
						valid=false;
						break;
				}
				
				break;
				
			default:
				logger.warn("Unchecked market state : "+state.toString());
				break;
    	}
    	
    	return(valid);
    }
    
}

