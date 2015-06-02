package com.theice.mdf.client.domain;

import com.theice.mdf.message.MDMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Interface contract that need to be implemented by the market data subscribers
 *
 * @author Adam Athimuthu
 * Date: Aug 1, 2007
 * Time: 11:39:55 AM
 */
public interface MDSubscriber
{
    public void notifyWithMDMessage(MDMessage message);
}
