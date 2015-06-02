package com.theice.mdf.client.process.handlers;

import com.theice.mdf.client.message.PriceFeedMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Market message handler serves as an interface from which all the concrete handlers are derived.
 *
 * @author Adam Athimuthu
 * Date: Aug 2, 2007
 * Time: 4:59:53 PM
 */
public interface MarketMessageHandler
{
    /**
     * Handle a message and notify the subscribers
     * 
     * @param priceFeedMessage
     */
    public void handle(PriceFeedMessage priceFeedMessage);

}
