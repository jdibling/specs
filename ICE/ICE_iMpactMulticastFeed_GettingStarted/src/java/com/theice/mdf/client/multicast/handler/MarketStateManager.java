package com.theice.mdf.client.multicast.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.state.MarketStreamState;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.message.notification.MarketStateChangeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * A global ready queue is used, independent of the market. As markets move to the READY state,
 * the priority and wait queues are drained by the Snapshot Dispatcher
 * onto this application wide ready queue. This queue is held in the market state manager
 * 
 * This application-wide ready queue is processed by the Multicast Processor thread associated with the
 * Live Dispatcher
 * 
 * While handling options markets, all the underlying markets are set to ready state at initialization
 * 
 * @author Adam Athimuthu
 */
public class MarketStateManager 
{
    protected static Map<String, MarketStateManager> _instances = new Hashtable<String, MarketStateManager>();
    
    private String _groupName;

    final Logger logger=Logger.getLogger(MarketStateManager.class.getName());

    /**
     * Map of sequencers keyed by the market id
     * Within the market id the messages are sequenced prior to draining them for processing
     * If an out of sequence is detected, that market processor has to wait prior to sequence resolution
     * Key: MarketKey
     */
    protected Map<MarketKey,MarketMessageSequencer> _sequencers=new HashMap<MarketKey,MarketMessageSequencer>();
    
    /**
     * Application-wide ready queue (per multicast group)
     */
    protected BlockingQueue<PriceFeedMessage> _readyQueue=new LinkedBlockingQueue<PriceFeedMessage>();

    /**
     * Map of market states keyed by the underlying market
     * Key: MarketKey
     * Value: MarketStateChangeMessage
     */
    protected Map<MarketKey,MarketStateChangeMessage> _stateChanges=new HashMap<MarketKey,MarketStateChangeMessage>();
    
	/**
	 * Sequence number of the recent live channel message that was just missed
	 * independent of any market.
	 * This information is used to validate if our snapshot is valid
	 */
	private int _recentLiveMessageMissed=-1;

    /**
     * Snapshot counter set is used for knowing when the snapshots have been loaded for all the markets
     * We put the market id into this set each time a product definition is received. As the market moves
     * to the ready state, we remove the entry. Once the set reaches zero size, we can safely unsubscribe from
     * the SNAPSHOT multicast channel
     */
    protected Set<MarketKey> _snapshotPending=new HashSet<MarketKey>();
    
    protected boolean _snapshotShutdown=false;
    
    /*
     * returns MarketStateManager instance for a specific multicast group if exists.
     * otherwise, add it in the instances map
     */
    public synchronized static MarketStateManager addInstanceIfNotExists(String multicastGroupName)
    {
       MarketStateManager manager = null;
       if (_instances.containsKey(multicastGroupName))
       {
          manager = _instances.get(multicastGroupName);
       }
       else
       {
          manager = new MarketStateManager(multicastGroupName);
          _instances.put(multicastGroupName, manager);
       }
       
       return manager;
    }
       
    public synchronized static MarketStateManager getInstance(String groupName)
    {
        return _instances.get(groupName);
    }

    private MarketStateManager(String groupName)
    {
       this._groupName = groupName;
    }
    
    public String getGroupName()
    {
       return this._groupName;
    }
    
    /**
     * initialize
     */
    public synchronized void initialize()
    {
    	System.out.println("Market State Manager - initializing...");
    	
    	_recentLiveMessageMissed=-1;
    	_snapshotShutdown=false;
    	
		MarketKey[] keys=_sequencers.keySet().toArray(new MarketKey[0]);
		_sequencers.clear();
		
    	System.out.println("Re-creating the market sequencers");
	
		for(int index=0;index<keys.length;index++)
		{
			initSequencer(keys[index]);
		}
    	
    	_stateChanges.clear();
    	
    	System.out.println("Market State Manager is clearing the application-wide ready queue...");
    	
    	_readyQueue.clear();
    	
    	System.out.println("TODO : Pending initializing market state changes (To do for Options)...");
    	
    	return;
    }

    /**
     * get the market sequencer for the given market, return null if it is not found
     * @param market key
     * @return
     */
    public MarketMessageSequencer lookupSequencer(MarketKey key)
    {
    	return(_sequencers.get(key));
    }
    
    /**
     * get from the internal map.
     * if the sequencer doesn't exist create it
     * @param MarketKey
     * @return
     */
    public synchronized MarketMessageSequencer initSequencer(MarketKey key)
    {
    	MarketMessageSequencer sequencer=null;
    	
    	sequencer=lookupSequencer(key);
    	
    	if(sequencer==null)
    	{
    		sequencer=new MarketMessageSequencer(key);
    		_sequencers.put(key, sequencer);
    		
    		if(logger.isTraceEnabled())
    		{
        		logger.trace("Sequencer Created for : "+key.toString());
    		}
    		
        	_snapshotPending.add(key);
    	}
    	
    	/**
    	 * Futures and Options market contexts are mutually exclusive
    	 * If the current group is for options, then we won't get any snapshots/live messages for 
    	 * the underlying markets. So, we immediately move the underlying market to the ready queue 
    	 */
		if(!key.isOptions())
		{
	    	MDFClientConfiguration currentConfig=MDFClientConfigurator.getInstance().getCurrentConfiguration();
	    	
	    	if(currentConfig.getMulticastGroupDefinition(this._groupName).isOptions())
	    	{
        		if(logger.isTraceEnabled())
        		{
            		logger.trace("Moving the Underlying non-OPTIONS market to the READY queue : "+key.toString());
        		}
        		
    			markReady(key);
	    	}
		}
		else
		{
    		if(logger.isTraceEnabled())
    		{
        		logger.trace("Sequencer created for options market : "+key.toString());
    		}
		}
		
    	return(sequencer);
    }

    /**
     * Used when the markets are dynamically created (as in the options markets situation)
     * 
     * @param key
     * @deprecated unused. With pre-defined options, we now have a uniform handling of all markets
     * @return
     */
    public synchronized MarketMessageSequencer assignSequencer(MarketKey key)
    {
    	MarketMessageSequencer sequencer=null;
    	
    	MarketStreamState streamState=null;
    	
    	sequencer=lookupSequencer(key);
    	
    	if(sequencer==null)
    	{
        	if(isSnapshotShutdown())
        	{
        		streamState=MarketStreamState.READY;
        		
        		if(logger.isTraceEnabled())
        		{
    				logger.trace("StateManager - Market is in ready state : "+key.toString());
        		}
        	}
        	else
        	{
        		streamState=MarketStreamState.NOTREADY;
        		
        		if(logger.isTraceEnabled())
        		{
            		logger.trace("StateManager - Market is NOT in ready state : "+key.toString());
        		}
        	}
        	
    		sequencer=new MarketMessageSequencer(key,streamState);
    		
    		_sequencers.put(key, sequencer);

    		if(streamState!=MarketStreamState.READY)
    		{
            	_snapshotPending.add(key);
    		}
    	}
    	
    	return(sequencer);
    }

    /**
     * when a market becomes ready, remove it from the snapshot pending set
     * @param market key
     * @return number of entries still pending. Zero indicates that all the markets have become ready
     */
    public int markReady(MarketKey key)
    {
    	if(!_snapshotPending.remove(key))
		{
    		StringBuffer buf=new StringBuffer();
    		buf.append("Pending Queue remove FAILED!!!...: "+_snapshotPending.toString());
    		System.err.println(buf.toString());
    		logger.error(buf.toString());
		}
    	
    	return(_snapshotPending.size());
    }
    
    public MarketKey[] getSnapshotPendingMarkets()
    {
    	return((MarketKey[]) _snapshotPending.toArray(new MarketKey[0]));
    }
    
    public int getNumberOfMarketsPendingSnapshot()
    {
    	return(_snapshotPending.size());
    }
    
    public void setSnapshotShutdown()
    {
    	_snapshotShutdown=true;
    }
    
    public boolean isSnapshotShutdown()
    {
    	return(this._snapshotShutdown);
    }
    
    public void updateStateChange(MarketKey key, MarketStateChangeMessage stateChangeMessage)
    {
    	_stateChanges.put(key, stateChangeMessage);
    }
    
    public MarketStateChangeMessage lookupStateChange(MarketKey key)
    {
    	return(_stateChanges.get(key));
    }

	public int getRecentLiveMessageMissed()
	{
		return(_recentLiveMessageMissed);
	}

	public void setRecentLiveMessageMissed(int recentLiveMessageMissed)
	{
		_recentLiveMessageMissed=recentLiveMessageMissed;
	}
	
	public BlockingQueue<PriceFeedMessage> getReadyQueue()
	{
		return(_readyQueue);
	}

    public String toString()
    {
    	StringBuffer buf=new StringBuffer("MarketStateManager = ");
    	buf.append("SnapshotShutdown? : ").append(this.isSnapshotShutdown()).append(". ");
		buf.append("[RecentLiveMessageMissed=" + _recentLiveMessageMissed + "]");
    	buf.append("<<<").append(_sequencers.toString()).append(">>>");
    	buf.append("<<<").append(this._snapshotPending.toString()).append(">>>");
    	
    	return(buf.toString());
    }
}

