package com.theice.mdf.client.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Interface contact that all the market data publishers will need to implement 
 * 
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 5:18:17 PM
 */
public interface MDPublisher
{
    public void addSubscriber(MDSubscriber subscriber);
    public void removeSubscriber(MDSubscriber subscriber);
    
    public void addEventSubscriber(MDSubscriber subscriber,Integer marketId);
    public void removeEventSubscriber(MDSubscriber subscriber,Integer marketId);
}

