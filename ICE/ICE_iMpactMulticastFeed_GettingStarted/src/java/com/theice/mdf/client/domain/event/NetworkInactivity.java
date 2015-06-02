package com.theice.mdf.client.domain.event;

import com.theice.mdf.client.domain.state.ApplicationStatus;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Network inactivity typically represents a socket timeout for TCP
 * or inactive message flow from a multicast channel
 * 
 * @author Adam Athimuthu
 */
public class NetworkInactivity extends AbstractApplicationEvent 
{
	public NetworkInactivity()
	{
		status=ApplicationStatus.NETWORKINACTIVITY;
	}
}

