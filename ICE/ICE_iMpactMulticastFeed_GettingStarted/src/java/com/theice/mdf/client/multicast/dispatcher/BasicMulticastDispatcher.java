package com.theice.mdf.client.multicast.dispatcher;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MainMarketKey;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.state.MarketStreamState;
import com.theice.mdf.client.exception.InconsistentStateException;
import com.theice.mdf.client.exception.InvalidStateException;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketMessageSequencer;
import com.theice.mdf.client.multicast.handler.MulticastMessageProcessor;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.MulticastMessageBlock;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.MarketStateChangeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Default (but complete) implementation for the incremental multicast channel
 * 
 * - It provides the flow sequence algorithm that is common for all the incremental channels
 * - It provides no validation of message types
 * 
 * The Multicast Dispatcher queues the messages in a "per-market" wait queue until the
 * Snapshot messages have been obtained for that particular market. After that the messages are moved to ready state.
 * 
 * The dispatcher for the Snapshot multicast channel handles obtaining the snapshot messages
 * and determining whether a market is fully ready for processing.
 * 
 * In the case of FullOrderDepth, the Snapshot messages include:
 * 
 *   MarketSnapshot message (1)
 *   MarketSnapshotOrder messages (zero or more depending on the NumBookEntries field of MarketSnapshot)
 * 
 * Market level message sequencing not needed. There can be now gaps in sequence#'s within a given market
 * 
 * @author Adam Athimuthu
 */
public class BasicMulticastDispatcher extends AbstractMulticastDispatcher
{
    static final Logger logger=Logger.getLogger(BasicMulticastDispatcher.class.getName());
    
    /**
     * MulticastMessageProcessor and the Multicast Handler Thread
     */
    protected MulticastMessageProcessor _processor=null;
    protected Thread _readyQueueHandlerThread=null;

    /**
     * Live channel dispatchers can't be instantiated without a message handler factory
     */
    //private BasicMulticastDispatcher()
    //{
    //}

    /**
     * Constructor for the live channel dispatcher
     * A factory is needed that can process information from the application wide ready queue
     * A reader thread will work the ready queue held by the state manager and pass it to a suitable
     * message handler registered with the factory
     *  
     * @param factory
     */
    public BasicMulticastDispatcher(String multicastGroupName, MarketHandlerFactoryInterface factory)
    {
    	super(multicastGroupName);

    	logger.info("Live Dispatcher Initializing the ready queue handler thread.");
    	
    	_processor=new MulticastMessageProcessor(_stateManager.getReadyQueue(),factory);
    	
    	_readyQueueHandlerThread=new Thread(_processor,getName()+" - MulticastMessageProcessor-"+multicastGroupName);
    	_readyQueueHandlerThread.start();
    }

    protected String getName()
    {
    	return("BasicMulticastDispatcher");
    }
    
	/**
	 * Handle the market state change message
	 * Market state changes are immediately processed and moved to the ready queue
	 * 
	 * The Snapshot channel uses this information to make sure we don't overwrite state
	 * information
	 * 
	 * @param message
	 * @return true if the message has been handled. false, if the caller has to proceed normal processing
	 */
    protected boolean handleMarketStateChange(PriceFeedMessage priceFeedMessage)
    {
    	MDSequencedMessage sequencedMessage=null;
    	
    	try
    	{
    		sequencedMessage=priceFeedMessage.getSequencedMessage();
    	}
    	catch(InconsistentStateException e)
    	{
    		logger.error(e.getMessage());
    		return(false);
    	}
    	
    	if(sequencedMessage.getMessageType()!=RawMessageFactory.MarketStateChangeMessageType)
    	{
    		return(false);
    	}
    	
    	MainMarketKey key=new MainMarketKey(sequencedMessage.getMarketID());
    	
		try
		{
			_stateManager.getReadyQueue().put(priceFeedMessage);
		}
		catch(InterruptedException e)
		{
			System.out.println("Dispatcher - readyqueue.put() Interrupted");
		}

		_stateManager.updateStateChange(key, (MarketStateChangeMessage) sequencedMessage);
		
    	return(true);
    }


    /**
     * TODO Null implementation of check validity of message
     */
    protected MarketKey checkValidityOfMessage(MDSequencedMessage message)
    {
    	MarketKey key=null;
    	System.err.println("NULL Implementation of checkValidityOfMessage....Please OVERRIDE !!!!!!");
    	return(key);
    }
    
    /**
     * preprocess block
     * @param message block
     */
	protected void preProcessBlock(MulticastMessageBlock block)
	{
		if(_stateManager.getRecentLiveMessageMissed()<0)
		{
			_stateManager.setRecentLiveMessageMissed(block.SequenceNumber-1);
			
			System.out.println("Recent live message missed : "+(block.SequenceNumber-1));
		}
		
		return;
	}

	/**
	 * process the message
	 * @param Market key
	 * @param priceFeedMessage
	 */
	protected void process(MarketKey key,PriceFeedMessage priceFeedMessage) throws InvalidStateException
	{
		if(logger.isTraceEnabled())
		{
			logger.trace("Looking for key : "+key.toString());
		}
		
		MarketMessageSequencer sequencer=_stateManager.lookupSequencer(key);
		
		MDSequencedMessage message=null;
		
		try
		{
			message=priceFeedMessage.getSequencedMessage();
		}
		catch(InconsistentStateException e)
		{
			logger.error("Expected a sequenced message. "+e.getMessage());
			throw(new InvalidStateException(e.getMessage()));
		}
		
		if(sequencer==null)
		{
			sequencer=determineSequencerRequirement(key, message);
			
			if(sequencer==null)
			{
				if(logger.isTraceEnabled())
				{
					StringBuffer buf=new StringBuffer("");
					buf.append(getName()).append(" - Market not yet loaded (or) unwanted market : ");
					buf.append(key.toString()).append(" : ").append(message.toString());
					logger.trace(buf.toString());
				}
				
				return;
			}
			
		}

		if(logger.isDebugEnabled())
		{
			logger.debug(getName()+" - Processing : "+message.toString());
		}

		synchronized(sequencer)
		{
			switch(sequencer.getState().getId())
			{
				case MarketStreamState.STATEID_NOTREADY:
				case MarketStreamState.STATEID_SNAPSHOTLOADING:
					if(logger.isTraceEnabled())
					{
						logger.trace("NotReady/SnapshotLoading - Enqueue the message in wait queue : "+message.toString());
					}
					sequencer.enqueueWaitMessage(priceFeedMessage);
					break;
					
				case MarketStreamState.STATEID_READY:
					/**
					 * Once the market has moved to ready state, we process all the live messages
					 * after making sure they are not outdated.
					 */
					if(logger.isTraceEnabled())
					{
						logger.trace("Ready : "+message.toString());
					}
					
					int sequence=message.getSequenceNumber();
					
					if(sequence<=sequencer.getSnapshotsLiveChannelLink())
					{
						StringBuffer buf=new StringBuffer();
						buf.append("Discarding outdated message in READY state. [").append(message.getMarketID()).append("] ");
						buf.append("LastSeq from Snapshot=").append(sequencer.getSnapshotsLiveChannelLink());
						buf.append(" : ").append(message.toString());
						logger.warn(buf.toString());
					}
					else
					{
						try
						{
							_stateManager.getReadyQueue().put(priceFeedMessage);
						}
						catch(InterruptedException e)
						{
							System.out.println("Dispatcher - readyqueue.put() Interrupted");
						}
					}

					break;
					
				default:
					logger.warn(getName()+" - Unknown MarketState");
					break;
			}
		}
	}
	
    /**
     * stop
     */
	public void stop()
	{
		System.out.println(getName()+" - Stopping the dispatcher and the associated MulticastMessageProcessor");
		_processor.stop();
	}

}

