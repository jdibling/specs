package com.theice.mdf.client.multicast.dispatcher;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MainMarketKey;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.state.MarketStreamState;
import com.theice.mdf.client.domain.state.SnapshotShutdownPolicy;
import com.theice.mdf.client.exception.InconsistentStateException;
import com.theice.mdf.client.exception.InvalidStateException;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketMessageSequencer;
import com.theice.mdf.client.multicast.handler.MarketStateManager;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.MulticastMessageBlock;
import com.theice.mdf.message.SnapshotMessageIface;
import com.theice.mdf.message.notification.MarketSnapshotMessage;
import com.theice.mdf.message.notification.MarketStateChangeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract base class for the snapshot multicaster dispatchers
 * 
 * The Snapshot dispatcher handles obtaining the snapshot message for each market
 * and keeping them in a priority queue. After obtaining the datagrams, the snapshot messages
 * are put in the priority queue until all the snapshot messages have been obtained. After that the
 * market is moved to a "Snapshot Loaded" state. From then on, it moves to a ready state.
 *
 * If, for some reason, we miss packets, an out of sequence exception is thrown and the application
 * re-initializes itself
 * 
 * Dependencies
 * 
 * Filters
 * 		What are the valid message types for this channel
 * Types
 * 		What are the message types valid a given state
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractSnapshotMulticastDispatcher extends AbstractMulticastDispatcher
{
    private static final Logger logger=Logger.getLogger(AbstractSnapshotMulticastDispatcher.class.getName());
    
    /**
     * if set, the shutdown latch is used to signal the multicast client to interrupt its threads
     * to stop. At this point the receiver should unjoin from the channel
     * Typically used by the snapshot channels to stop listening after the markets have been initialized
     * 
     * if the latch is null, then we assume that the client should continue running forever
     */
    protected CountDownLatch _shutdownLatch=null;

    /**
     * By default, the shutdown policy is set to COUNTER
     * For channels such as Options market we use the TIMER policy
     */
    protected SnapshotShutdownPolicy _shutdownPolicy=SnapshotShutdownPolicy.COUNTER;

    public AbstractSnapshotMulticastDispatcher(String multicastGroupName)
    {
    	super(multicastGroupName);
    }

    public AbstractSnapshotMulticastDispatcher(String multicastGroupName, CountDownLatch shutdownLatch)
    {
    	super(multicastGroupName);
    	_shutdownLatch=shutdownLatch;
    }

	/**
	 * process the message if the market has already been loaded
	 * 
	 * If we are not yet done loading all the product definitions, the messages are thrown away
	 * Also, if we receive a message for a market that we are not interested in, we throw them away as well
	 * 
	 * Concrete Dependencies
	 * 		Check if a message is valid for the given market state (NOTREADY, SNAPSHOT_LOADING)
	 * 		Get the market sequence information (market sequence number, expected number of messages) 
	 *
	 * @param market key
	 * @param priceFeedMessage
	 */
	protected void process(MarketKey key,PriceFeedMessage priceFeedMessage) throws InvalidStateException
	{
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

		/**
		 * Sequencer should always be there for pre-defined options
		 */
		if(sequencer==null)
		{
			sequencer=determineSequencerRequirement(key,message);
			
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
		
		/**
		 * Process the snapshot message based on the state
		 */
		synchronized(sequencer)
		{
			switch(sequencer.getState().getId())
			{
				case MarketStreamState.STATEID_NOTREADY:
					/**
					 * 1. Expect a MarketSnapshot message
					 * 2. Transitions:
					 * 	Snapshot Loading (if market orders pending)
					 * 	Ready (if no market orders pending)
					 * 
					 * Exceptions:
					 * 
					 * For a snapshot to be valid, it has to have a LastSequenceNumber greater than or
					 * equal to the recent Live Channel Message that was missed.
					 * 
					 */
					if(!(message instanceof SnapshotMessageIface))
					{
						StringBuffer buf=new StringBuffer("Unexpected message received in NOTREADY state : ").append(message.toString());
						throw(new InvalidStateException(buf.toString()));
					}
					
					SnapshotMessageIface snapshot=(SnapshotMessageIface) message;
					
					int recentLiveMessageMissed=_stateManager.getRecentLiveMessageMissed();
					
					if(recentLiveMessageMissed<0)
					{
						StringBuffer buf=new StringBuffer();
						buf.append("No live message information available. Dropping the snapshot : ");
						buf.append(message.toString());
						logger.warn(buf.toString());
						System.err.println(buf.toString());
						break;
					}
					
					if(snapshot.getLastMessageSequenceNumber()<recentLiveMessageMissed)
					{
						StringBuffer buf=new StringBuffer();
						buf.append("Bad snapshot. Snapshot too early for : ").append(message.getMarketID());
						buf.append(". Snapshot Seq# : ").append(snapshot.getLastMessageSequenceNumber());
						buf.append(". Recent Live Seq# missed : ").append(recentLiveMessageMissed);
						
						logger.warn(buf.toString());
						
						System.err.println(buf.toString());
						
						break;
					}
					else
					{
						StringBuffer buf=new StringBuffer();
						buf.append("Snapshot is valid. LastSeq# : ").append(snapshot.getLastMessageSequenceNumber());
						buf.append(". Recent Live Seq# missed : ").append(recentLiveMessageMissed);
						
						if(logger.isTraceEnabled())
						{
							logger.trace(buf.toString());
						}
					}
					
					if(logger.isDebugEnabled())
					{
						logger.debug("Not Ready - Enqueue priority message : "+priceFeedMessage.toString());
					}
					sequencer.enqueuePriorityMessage(priceFeedMessage);
					
					/**
					 * Get the last processed sequence number that links us to the live/incremental channel
					 */
					sequencer.setSnapshotsLiveChannelLink(snapshot.getLastMessageSequenceNumber());
					
					if(snapshot.getNumOfBookEntries()==0)
					{
						moveToReadyState(sequencer);
						
						int numRemainingMarkets=_stateManager.markReady(key);
						
						StringBuffer buf=new StringBuffer(getName());
						buf.append("-Synchronized [").append(key.getMarketID()).append("] [Flow=(a) Book Entries=None] ");
						
						if(numRemainingMarkets==0)
						{
							buf.append(" - Pending Markets : 0");
							System.out.println(buf.toString());
							
							if(logger.isDebugEnabled())
							{
								logger.debug(buf.toString());
							}

							if(_shutdownPolicy==SnapshotShutdownPolicy.COUNTER)
							{
								activateCounterLatch();
							}
						}
						else
						{
							buf.append(" - Pending Markets : ").append(numRemainingMarkets);
							System.out.println(buf.toString());
							
							if(logger.isDebugEnabled())
							{
								logger.debug(buf.toString());
							}
						}
						
						MarketInterface m = MarketsHolder.getInstance().findMarket(message.getMarketID());
                  if (m!=null)
                  {
                     m.resetGUIComponentsText();
                  }
					}
					else
					{
						if(logger.isDebugEnabled())
						{
							logger.debug("State Changed to SnapshotLoading : "+key.toString());
						}
						
						sequencer.setNumberOfOrdersPending(snapshot.getNumOfBookEntries());
						sequencer.setState(MarketStreamState.SNAPSHOTLOADING);
					}
					
					break;
					
				case MarketStreamState.STATEID_SNAPSHOTLOADING:

					if(!isMessageValidInState(message, sequencer.getState()))
					{
						StringBuffer buf=new StringBuffer("Unexpected message while STATEID_SNAPSHOTLOADING : ").append(message.toString());
						throw(new InvalidStateException(buf.toString()));
					}
					
					if(logger.isDebugEnabled())
					{
						logger.debug("SnapshotLoading - Enqueue priority message : "+message.toString());
					}
					
					sequencer.enqueuePriorityMessage(priceFeedMessage);
					
					if(sequencer.decrementNumberOfOrdersPending()==0)
					{
						moveToReadyState(sequencer);

						if(logger.isDebugEnabled())
						{
							logger.debug("State Changed from SnapShotLoading to Ready : "+key.toString());
						}
						
						int numRemainingMarkets=_stateManager.markReady(key);
						
						StringBuffer buf=new StringBuffer(getName());
						buf.append("-Synchronized [").append(key.getMarketID()).append("] [Flow=(b) With Book Entries] ");
						
						if(numRemainingMarkets==0)
						{
							buf.append(" - Pending Markets : 0");
							System.out.println(buf.toString());
							
							if(logger.isDebugEnabled())
							{
								logger.debug(buf.toString());
							}

							if(_shutdownPolicy==SnapshotShutdownPolicy.COUNTER)
							{
								activateCounterLatch();
							}
						}
						else
						{
							buf.append(" - Pending Markets : ").append(numRemainingMarkets);
							System.out.println(buf.toString());

							if(logger.isDebugEnabled())
							{
								logger.debug(buf.toString());
							}
						}
						
						MarketInterface m = MarketsHolder.getInstance().findMarket(message.getMarketID());
                  if (m!=null)
                  {
                     m.resetGUIComponentsText();
                  }
					}
					
					break;
					
				case MarketStreamState.STATEID_READY:
					/**
					 * If we come across a ready state, this indicates that we have gone full cycle
					 * of the snapshot window for a given market
					 */
					if(logger.isTraceEnabled())
					{
						logger.trace("Ready State - No Action for : "+key.toString());
					}
					
					break;
					
				default:
					logger.warn("Unknown MarketState while processing FullOrderDepth Snapshot : "+key.toString());
					break;
			}
		}
	}

	/**
	 * Route the snapshot messages to ready state
	 * Drain the wait queue and drop unneeded messages
	 * Process the waiting messages starting from the next seq# after snapshot 
	 * move to ready state
	 * @Market Message Sequencer
	 */
	protected void moveToReadyState(MarketMessageSequencer sequencer)
	{
		if(logger.isTraceEnabled())
		{
			logger.trace("Moving to the Ready Queue : "+sequencer.getMarketKey());
		}
		
		if(sequencer.getPriorityQueue().size()>0)
		{
			for(Iterator<PriceFeedMessage> it=sequencer.getPriorityQueue().iterator();it.hasNext();)
			{
				PriceFeedMessage priceFeedMessage=(PriceFeedMessage) it.next();
				
				try
				{
					_stateManager.getReadyQueue().put(priceFeedMessage);
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		else
		{
			if(logger.isTraceEnabled())
			{
				logger.trace("Nothing in the priority queue for : "+sequencer.getMarketKey());
			}
		}
		
		/**
		 * Now drain the wait queue after discarding outdated messages
		 */
		if(sequencer.getWaitQueue().size()>0)
		{
			for(Iterator<PriceFeedMessage> it=sequencer.getWaitQueue().iterator();it.hasNext();)
			{
				PriceFeedMessage priceFeedMessage=(PriceFeedMessage) it.next();
				
				MDSequencedMessage sequencedMessage=null;
				
				try
				{
					sequencedMessage=priceFeedMessage.getSequencedMessage();
				}
				catch(InconsistentStateException e)
				{
					logger.error("Expected a sequenced message. Got : "+priceFeedMessage.getMessage());
					continue;
				}
				
				int sequence=sequencedMessage.getSequenceNumber();
				
				if(sequence<=sequencer.getSnapshotsLiveChannelLink())
				{
					StringBuffer buf=new StringBuffer();
					buf.append("Discarding outdated message : [").append(sequencedMessage.getMarketID()).append("] ");
					buf.append("LastSeq from Snapshot=").append(sequencer.getSnapshotsLiveChannelLink());
					buf.append(" : ").append(sequencedMessage.toString());
					logger.warn(buf.toString());
					System.out.println(buf.toString());
					continue;
				}
				
				logger.info("Moving to the ready queue : "+priceFeedMessage.toString());
				
				try
				{
					_stateManager.getReadyQueue().put(priceFeedMessage);
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		else
		{
			logger.info("Nothing in the wait queue for : "+sequencer.getMarketKey());
		}
		
		sequencer.setState(MarketStreamState.READY);
		
		if(logger.isDebugEnabled())
		{
			logger.debug("State Changed to Ready : "+sequencer.getMarketKey().toString());
		}
		
		return;
	}
	
	/**
	 * Check the shutdown policy for this thread and activate the latch if necessary
	 * If the shutdown policy is not COUNTER (i.e. if it is TIMER), then the latch will not be
	 * activated here
	 * 
	 * When we activate the latch, set the market state to mark that we loaded all markets
	 * 
	 */
	protected void activateCounterLatch()
	{
		System.out.println("*** ALL Markets are ready. Executing Shutdown for Policy : "+_shutdownPolicy);
		
		preShutDown();

	    /**
		 * activate the latch for us to stop listening to the snapshot multicast channel
		 */
		if(_shutdownLatch!=null)
		{
			System.out.println("Activating the shutdown latch. StateManager [SnapshotShutdown=true]");
			
			_shutdownLatch.countDown();
			
        	MarketStateManager.getInstance(_multicastGroupName).setSnapshotShutdown();
		}
		else
		{
			System.err.println("Shutdown Latch is NULL !!!");
		}
		
		return;
	}

	/**
	 * The Snapshot channel will to make sure we don't overwrite state information
	 * received through the live channel
	 * 
	 * @param message
	 * @return snapshot will always return a false to indicate that the caller has to proceed processing
	 * This is because, the snapshot is slightly altered to avoid duplicate processing of state changes.
	 */
    protected boolean handleMarketStateChange(PriceFeedMessage priceFeedMessage)
    {
    	MDSequencedMessage message=null;
    	
    	try
    	{
    		message=priceFeedMessage.getSequencedMessage();
    	}
    	catch(InconsistentStateException e)
    	{
    		logger.warn("Expected a sequenced message while processing MarketStateChange. Got : "+priceFeedMessage.getMessage());
    		return(false);
    	}

    	if(message instanceof MarketSnapshotMessage)
    	{
        	MainMarketKey key=new MainMarketKey(message.getMarketID());
        	
    		MarketStateChangeMessage stateChangeMessage=_stateManager.lookupStateChange(key);
    		
    		if(stateChangeMessage!=null)
    		{
    		   if (logger.isDebugEnabled())
    		   {
    		      logger.debug("*** State Change already processed. Disable snapshot's trading status : "+
    					stateChangeMessage.toString());
    		   }
    			((MarketSnapshotMessage) message).TradingStatus=' ';  			
    		}
    	}
		
    	return(false);
    }

    /**
     * override method
     * 
     * pre shutdown callback will be called prior to shutting down the snapshot channel
     * this is applicable for both shutdown policies
     */
    protected void preShutDown()
    {
    	if(logger.isTraceEnabled())
    	{
    		logger.trace("*** Pre Shutdown (Default) : No action");
    	}
    }
    
    /**
     * preprocess block
     * @param message block
     */
	protected void preProcessBlock(MulticastMessageBlock block)
	{
	}

	/**
     * Get shutdown latch
     * @return shutdown latch
     */
    public CountDownLatch getShutdownLatch()
    {
    	return(_shutdownLatch);
    }
    
    /**
     * stop
     */
	public void stop()
	{
		System.out.println(getName()+" - Stop signal received (No Action).");
	}

    /**
     * Template methods
     * @param message
     * @param state
     * @return
     */
    protected abstract boolean isMessageValidInState(MDSequencedMessage message,MarketStreamState state);


}

