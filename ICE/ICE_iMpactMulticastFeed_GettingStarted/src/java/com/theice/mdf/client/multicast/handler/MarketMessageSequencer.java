package com.theice.mdf.client.multicast.handler;

import java.util.LinkedList;
import java.util.List;

import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.domain.state.MarketStreamState;
import com.theice.mdf.client.message.PriceFeedMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MarketMessageSequencer 
{
	private MarketKey _key;

	/**
	 * Wait Queue for this market
	 */
	private List<PriceFeedMessage> _waitQueue = new LinkedList<PriceFeedMessage>();

	/**
	 * Priority Queue for this market (holds messages that have to be processed
	 * first, e.g. Snapshot) Typically this queue will have a MarketSnapshot
	 * followed by the Pending Orders for this market
	 */
	private List<PriceFeedMessage> _priorityQueue = new LinkedList<PriceFeedMessage>();

	/**
	 * Market state
	 */
	private MarketStreamState _state = MarketStreamState.NOTREADY;

	/**
	 * Sequence numbers to detect out-of-sequence conditions
	 * 
	 * Keep track of the sequence number at which the snapshot was taken All the
	 * messages that has sequence numbers less than the snapshot sequence will
	 * be dropped from the live/incremental channel
	 */
	private int _snapshotsLiveChannelLink = -1;

	/**
	 * Number of pending orders
	 */
	private int _numberOfOrdersPending = -1;

	public MarketMessageSequencer(MarketKey key) {
		_key = key;
	}

	public MarketMessageSequencer(MarketKey key, MarketStreamState state) {
		_key = key;
		_state = state;
	}

	public List<PriceFeedMessage> getWaitQueue()
	{
		return (_waitQueue);
	}

	public List<PriceFeedMessage> getPriorityQueue()
	{
		return (_priorityQueue);
	}

	public void enqueueWaitMessage(PriceFeedMessage message) 
	{
		_waitQueue.add(message);
	}

	public void enqueuePriorityMessage(PriceFeedMessage message) 
	{
		_priorityQueue.add(message);
	}

	public synchronized void setState(MarketStreamState state) 
	{
		_state = state;
	}

	public void setSnapshotsLiveChannelLink(int snapshotSequence) 
	{
		_snapshotsLiveChannelLink = snapshotSequence;
	}

	public void setNumberOfOrdersPending(int numberOfOrdersPending) 
	{
		_numberOfOrdersPending = numberOfOrdersPending;
	}

	public int getNumberOfOrdersPending() 
	{
		return (_numberOfOrdersPending);
	}

	public int getSnapshotsLiveChannelLink() 
	{
		return(_snapshotsLiveChannelLink);
	}
	
	/**
	 * decrement number of orders pending
	 * 
	 * @return the orders pending after decrementing
	 */
	public int decrementNumberOfOrdersPending() 
	{
		return (--_numberOfOrdersPending);
	}

	public MarketStreamState getState() 
	{
		return (_state);
	}

	public MarketKey getMarketKey() 
	{
		return (_key);
	}

	public synchronized String toString() 
	{
		StringBuffer buf = new StringBuffer("Sequencer=");
		buf.append("[MarketKey=" + _key + "]");
		buf.append("[State=" + _state + "]");
		buf.append("[SnapshotsLiveChannelLink=" + _snapshotsLiveChannelLink+ "]");
		buf.append("[NumberofOrdersPending=" + _numberOfOrdersPending + "]");
		buf.append("[PriorityQueue=" + _priorityQueue.toString() + "]");
		buf.append("[WaitQueue=" + _waitQueue.toString() + "]");
		return (buf.toString());
	}

}
