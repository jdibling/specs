package com.theice.mdf.client.multicast.factory;

import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.multicast.MDFMulticastClient;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * <p/>
 *
 * Provides the incremental and snapshot multicast configurations for a given Channel Context
 * 
 * The channel contexts are as follows:
 * 
 * FullOrderDepth
 * PriceLevel
 * OptionsTopOfBook
 * 
 * @see MulticastChannelContext
 * @author Adam Athimuthu
 */
public interface MulticastClientFactory 
{
	public MDFMulticastClient createIncrementalMulticastClient(String groupName) throws InitializationException;
	public MDFMulticastClient createSnapshotMulticastClient(String groupName) throws InitializationException;
}
