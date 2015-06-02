package com.theice.mdf.client.domain.event;

import com.theice.mdf.client.domain.state.ApplicationStatus;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Application Event interface, for communicating important events to interested subscribers
 * Usually the application monitor is responsible for keeping track of application status changes
 * and notifies them to other components
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractApplicationEvent implements ApplicationEvent
{
	protected ApplicationStatus status=ApplicationStatus.NORMAL;
	
	public AbstractApplicationEvent()
	{
	}
	
	public ApplicationStatus getStatus()
	{
		return(status);
	}
	
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append(status.toString());
		return(buf.toString());
	}
}
