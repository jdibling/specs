package com.theice.mdf.client.multicast.dispatcher;

import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.state.ApplicationStatus;
import com.theice.mdf.client.exception.InvalidStateException;
import com.theice.mdf.client.exception.SequenceException;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketMessageSequencer;
import com.theice.mdf.client.multicast.handler.MarketStateManager;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.MulticastMessageBlock;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.BundleMarkerMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Multicast Dispatcher uses a global ready queue irrespective of the market.
 * As markets move to the READY state, the priority and wait queues are drained by the Snapshot Dispatcher
 * onto this application wide ready queue. This queue is held in the market state manager
 * 
 * This application-wide ready queue is processed by the Multicast Processor thread associated with the
 * Live Dispatcher
 * 
 * All the MDSequencedMessages are wrapped in a PriceFeedMessage, and flagged whether they are bundled or not
 * with a uniquely generated bundle id
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractMulticastDispatcher implements MDFMulticastDispatcher 
{
    static final Logger logger=Logger.getLogger(AbstractMulticastDispatcher.class.getName());

    /**
     * Member variables for handling sequence gaps
     */
    protected long _lastSequenceNumber=(-1L);
    protected int _lastNumberOfMessages=0;
    protected MulticastMessageBlock _lastBlock=null;
    
    /**
     * market state manager
     */
    protected MarketStateManager _stateManager=null;

    private int _currentSessionNumber=(-1);
    
    protected final String _multicastGroupName;
	
    /**
     * Bundle sequence number. Set to a uniquely generated long number when the Bundle Start
     * When we receive Bundle End, it is set back to zero
     */
	private long bundleSequenceNumber=0L;

	public AbstractMulticastDispatcher(String multicastGroupName)
   {
	   _multicastGroupName = multicastGroupName;
	   _stateManager = MarketStateManager.addInstanceIfNotExists(multicastGroupName);
   }

    /**
     * initialize
     */
	public void initialize()
	{
	}

	/**
     * dispatch the multicast message
     * 
     * 1. Check if the session is valid
     * 2. Check if we have out of sequence condition
     * 3. Dispatch the block for processing
     */
	public void dispatch(MulticastMessageBlock block) throws SequenceException
	{
		if(logger.isTraceEnabled())
		{
			logger.trace(block.toString());
		}

		try
		{
			if(!isValidSession(block))
			{
				handleInvalidSession(block);
			}
			
			if(isInSequence(block))
			{
				dispatchBlock(block);
			}
			else
			{
				System.err.println(">>> Not in sequence (Not severe - Duplicate?). Ignore the message and proceed");
			}
		}
		catch(SequenceException e)
		{
			System.err.println(">>> Dispatcher detected a Sequence Problem - Waking up the AppMonitor");
			AppManager manager = AppManager.getInstance(_multicastGroupName);
			manager.setApplicationStatus(ApplicationStatus.SEQUENCEPROBLEM);
			manager.getAppMonitor().wakeup();
			
			throw(e);
		}
	}
		
    /**
     * Sequence and dispatch the multicast block
	 * Unpacking and Dispatching
     * @param multicast message block
     */
    protected void dispatchBlock(MulticastMessageBlock block) throws SequenceException
	{
    	if(logger.isTraceEnabled())
		{
	    	logger.trace(getName()+" - dispatchBlock Entering.");
		}

		/**
		 * preproces the block
		 */
		preProcessBlock(block);

		/**
		 * Process the messages within the multicast block
		 */
		for(int index=0;index<block.getMdMessages().size();index++)
		{
			MDSequencedMessage message=(MDSequencedMessage) block.getMdMessages().get(index);
			
			if(logger.isTraceEnabled())
			{
				logger.trace(getName()+" dispatchBlock : "+message.toString());
			}
			
			char messageType=message.getMessageType();
			
			/**
			 * Bundled Message
			 * If we get Start bundle, then all the messages have to be flagged as bundled an End bundle marker
			 */
			if(messageType==RawMessageFactory.BundleMarkerMessageType)
			{
				handleBundleMarker(message);
				continue;
			}
			
			/**
			 * Before processing the message, the sequenced message is wrapped in a PriceFeedMessage decorator
			 * indicating whether this was part of the bundle etc.,
			 */
			PriceFeedMessage priceFeedMessage=new PriceFeedMessage(message,bundleSequenceNumber);

			/**
			 * Special handling of market state change messages
			 */
			if(handleMarketStateChange(priceFeedMessage))
			{
				continue;
			}

			/**
			 * Handle Messages with no market ids
			 * These are message such as SystemText etc.,
			 */
			if(message.getMarketID()==(-1))
			{
				logger.info("Handling message with market id = (-1): "+message.toString());
				handleGeneralMessage(priceFeedMessage);
				continue;
			}
			
			/**
			 * At this point, we are assured of all the messages having market ids
			 */
			MarketKey key=checkValidityOfMessage(message);
			
			if(key==null)
			{
				if (logger.isDebugEnabled())
				{
				   logger.debug(getName()+" - Market Key is null. Message Type Not Valid for this channel?? : "+message.toString());
				}
				   continue;
			}

			try
			{
				process(key,priceFeedMessage);
			}
			catch(InvalidStateException e)
			{
				logger.warn("Invalid State : "+e.toString());
			}
		}

		return;
	}

    /**
	 * Block Sequencing
     * Check for out of sequence conditions
     * 
	 * If detected, set the application's status as OUTOFSEQUENCE. This will lead to:
	 * 
	 * 	Init the market sequencers and reset all queues
	 * 	Init the market state manager
	 * 	Shutdown all multicast client threads and do a re-synchronization
	 * 
     * @param block
     * @return true if out of sequence, otherwise false
     * @throws SequenceException
     */
    protected boolean isInSequence(MulticastMessageBlock block) throws SequenceException
    {
    	boolean inSequence=true;
    	boolean severe=false;
    	
		long sequence=block.SequenceNumber;
		int numberOfMessages=block.NumOfMessages;

		if(logger.isTraceEnabled())
		{
			StringBuffer buf=new StringBuffer();
			buf.append(getName()).append(" - Sequence#: ").append(sequence);
			buf.append(" NumOfMessages#: ").append(numberOfMessages);
			logger.trace(buf.toString());
		}
		
		if(_lastSequenceNumber<0)
		{
			_lastSequenceNumber=sequence;
			_lastNumberOfMessages=numberOfMessages;
			_lastBlock=block;
		}
		else
		{
			long expectedSequence=(_lastSequenceNumber+_lastNumberOfMessages);
			
			if(expectedSequence!=sequence)
			{
				StringBuffer buf=new StringBuffer();
				buf.append("*** SequenceGap Detected. ExpectedSeq#=").append(expectedSequence);
				buf.append(". Got : ").append(sequence);
				buf.append(" [CurrentBlock=").append(block.toString()).append("]");
				buf.append(" [PreviousBlock=").append(_lastBlock.toString()).append("]");
				
				inSequence=false;
				
				if(sequence<expectedSequence)
				{
					buf.append(" *** Older packet (Duplicate?) : "+sequence);
					logger.warn(buf.toString());
				}
				else
				{
					/**
					 * Indicates a severe out of sequence problem
					 * Currently we are not caching the missing packets to resolve them
					 */
					severe=true;
					buf.append(" *** SEVERE ***");
					logger.error(buf.toString());
				}
				
				System.err.println(buf.toString());

				/**
				 * Raise an exception only if we encountered a gap
				 * Duplicate packets are simply discarded and not treated as severe
				 */
				if(severe)
				{
					throw(new SequenceException(buf.toString()));
				}
			}
			else
			{
				_lastSequenceNumber=sequence;
				_lastNumberOfMessages=numberOfMessages;
				_lastBlock=block;
			}
		}

		return(inSequence);
    }
    
    /**
     * Validate the session
     * The first time we receive a multicast packet, the session number is initialized
     * After that, if we receive a packet with a different session number, the client has to resync 
     * @param block
     * @return true if out of sequence, otherwise false
     */
    protected boolean isValidSession(MulticastMessageBlock block)
    {
    	boolean valid=true;
    	
    	int session=block.SessionNumber;
    	
    	if(_currentSessionNumber==(-1))
    	{
    		_currentSessionNumber=session;
    		AppManager.getInstance(_multicastGroupName).markSession(session);
    	}
    	else
    	{
    		if(_currentSessionNumber!=session)
    		{
    			if(logger.isTraceEnabled())
    			{
        			StringBuffer buf=new StringBuffer("*** Session number mismatch. ");
        			buf.append(getName()).append("Expected : ").append(_currentSessionNumber+". Got "+session);
        			
    				logger.trace(buf.toString());
    			}
				
    			valid=false;
    		}
    	}
    	
    	return(valid);
    }

    public CountDownLatch getShutdownLatch()
    {
    	return(null);
    }
    
	/**
	 * Handle out of sequence condition for the snapshot channel
	 * Action: shutdown and re-establish the connection
	 */
    protected void handleInvalidSession(MulticastMessageBlock block) throws SequenceException
    {
		System.out.println("TODO: Invalid Session detected");
				
		throw(new SequenceException("TODO: Invalid Session detected"));
    }
    
	/**
	 * Determine sequencer requirement and assign a sequencer if needed.
	 * 
	 * 1. If the market id is not in our product definitions repository, then we don't care about this market
	 * 		irrespective of the multicast channel
	 * 2. If this is one of the markets that we are currently subscribing to, then:
	 * 		- we shouldn't be running into this situation, as we should have had a sequencer assigned by
	 * 			the product definition handler
	 * 
	 * With the introduction of pre-defined options, we no longer need to run into this situation.
	 * This flow should never create a sequencer in any of the multicast contexts
	 * (Historically, if the multicast channel was OptionsTopOfBook, this used to be the primary place where we assign
	 *  the sequencer)
	 * 
	 * @param key
	 * @param message
	 * @return With the introduction of pre-defined options, this method should always return null
	 */
    protected MarketMessageSequencer determineSequencerRequirement(MarketKey key, MDSequencedMessage message)
    {
    	MarketMessageSequencer sequencer=null;
    	
    	int marketId=message.getMarketID();
    	
    	if(MarketsHolder.getInstance().findMarket(marketId)==null)
    	{
    		if(logger.isTraceEnabled())
    		{
    	    	StringBuffer buf=new StringBuffer("");
        		buf.append("Unwanted market. Sequencer not created : ").append(marketId);
    			logger.warn(buf.toString());
    		}
    		return(null);
    	}
    	
    	switch(AppManager.getMulticastChannelContext())
    	{
//	    	case OPTIONS_TOPOFBOOK:
//
//	    		if(logger.isTraceEnabled())
//	    		{
//	    	    	StringBuffer buf=new StringBuffer("");
//		    		buf.append("Creating Market Sequencer for : ").append(key.toString());
//					logger.warn(buf.toString());
//					System.out.println(buf.toString());
//	    		}
//				
//				sequencer=MarketStateManager.getInstance().assignSequencer(key);
//				break;
//				
			default:
				if(logger.isTraceEnabled())
				{
	    	    	StringBuffer buf=new StringBuffer("");
		    		buf.append("Market Sequencer must have been created: ").append(marketId);
					logger.warn(buf.toString());
				}
				break;
    	}
    	
    	return(sequencer);
    }
    
	/**
	 * Handle General message, that has no market id
	 * Messages such as system text, fall into this category
	 * These messages are immediately moved to the ready queue
	 * 
	 * @param priceFeedMessage
	 */
    protected void handleGeneralMessage(PriceFeedMessage priceFeedMessage)
    {
		logger.info("Handling General message : "+priceFeedMessage.toString());
		
		try
		{
			_stateManager.getReadyQueue().put(priceFeedMessage);
		}
		catch(InterruptedException e)
		{
			System.err.println("Dispatcher - readyqueue.put() Interrupted while handling general message.");
		}

    	return;
    }
    
    /**
     * If it is Bundle Start, get the next bundle sequence number
     * If it is Bundle End, set it back to zero
     * @param message
     */
    protected void handleBundleMarker(MDSequencedMessage message)
    {
    	BundleMarkerMessage bundleMarkerMessage=(BundleMarkerMessage) message;
    	
    	if(bundleMarkerMessage.MarkerType==BundleMarkerMessage.MARKER_TYPE_START)
    	{
    		bundleSequenceNumber=PriceFeedMessage.generateBundleSequenceNumber();
    		
    		if(logger.isTraceEnabled())
    		{
    			logger.trace("Bundle Marker Start ### "+this.bundleSequenceNumber);
    		}
    	}
    	else
    	{
    		if(logger.isTraceEnabled())
    		{
    			logger.trace("Bundle Marker End ### "+this.bundleSequenceNumber);
    		}
    		
    		bundleSequenceNumber=0;
    	}
    	
    	return;
    }

	/**
	 * Template methods to be implemented by the concrete dispatchers
	 */
    protected abstract String getName();
    
	/**
	 * Handle the market state change message
	 * Live Channel : Market state changes are immediately processed and moved to the ready queue
	 * 
	 * The Snapshot channel uses this information to make sure we don't overwrite state
	 * information. This is done by resetting the state change flag to blank
	 * 
	 * @param message
	 * @return true if the message has been processed. false, if the normal processing has to continue 
	 */
    protected abstract boolean handleMarketStateChange(PriceFeedMessage priceFeedMessage);

    /**
     * check validity of the message for this channel. if valid, return the market specific unique key
     * This key will be used for sequencing events
     * @param message
     * @return marketkey (specific to the channel)
     */
    protected abstract MarketKey checkValidityOfMessage(MDSequencedMessage message);
    
    /**
     * preprocess the block
     * @param block
     */
	protected abstract void preProcessBlock(MulticastMessageBlock block);

	/**
     * process the message
     * The key will be used to sequence the events. In the case of options markets
     * this key will be the OptionMarketKey
     * @param priceFeedMessage
     * @return
     */
	protected abstract void process(MarketKey key,PriceFeedMessage priceFeedMessage) throws InvalidStateException;


}

