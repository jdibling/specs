package com.theice.mdf.client.multicast.dispatcher;

import java.util.concurrent.CountDownLatch;

import com.theice.mdf.client.exception.SequenceException;
import com.theice.mdf.message.MulticastMessageBlock;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Interface for all the multicast dispatchers. The dispatchers are used by the corresponding multicast
 * clients for processing the message blocks as they are received off of the multicast channel.
 * In general, the dispatchers do the following:
 * 
 * 1. Unpack the multicast block
 * 2. Sequence the messages and handle missing packets
 * 3. Forward the MD messages onto a factory for further processing
 * 4. Keep track of market level sequencing issues (???) and initiate request for retransmission if needed
 * 
 * The following are some of the dispatchers:
 * 
 * FullOrderDepthMulticastDispatcher
 * PriceLevelMulticastDispatcher
 * MarketSnapshotMulticastDispatcher
 * 
 * @author Adam Athimuthu
 */
public interface MDFMulticastDispatcher 
{
	public void initialize();
	public void dispatch(MulticastMessageBlock block) throws SequenceException;
    public CountDownLatch getShutdownLatch();
	public void stop();
}

