package com.theice.mdf.client.domain.event;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Application Event Subscriber
 * 
 * @author Adam Athimuthu
 */
public interface ApplicationEventSubscriber 
{
	public void notifyEvent(ApplicationEvent event);
}

