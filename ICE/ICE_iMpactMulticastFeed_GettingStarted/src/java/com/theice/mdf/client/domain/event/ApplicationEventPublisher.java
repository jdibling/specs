package com.theice.mdf.client.domain.event;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Application Event Publisher
 * 
 * @author Adam Athimuthu
 */
public interface ApplicationEventPublisher 
{
	public void addSubscriber(ApplicationEventSubscriber subscriber);
	public void removeSubscriber(ApplicationEventSubscriber subscriber);
}

