package com.theice.mdf.client.examples;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.client.domain.MDSubscriber;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * <code>SimpleClientMessageConsumer</code> gets messages from the reader and
 * print them to console.
 *
 * @author David Chen
 * @since 12/28/2006
 */

public class SimpleClientMessageConsumer implements Runnable
{
    SimpleClientSocketReader _socketReader;

    private static Logger logger = Logger.getLogger(SimpleClientMessageConsumer.class.getName());

    /**
     * MD Subscriber
     */
    MDSubscriber _subscriber = null;

    /**
     * Constructor
     *
     * @param socketReader
     */
    public SimpleClientMessageConsumer(SimpleClientSocketReader socketReader)
    {
        _socketReader = socketReader;
    }

    /**
     * Constructor
     *
     * @param socketReader
     */
    public SimpleClientMessageConsumer(SimpleClientSocketReader socketReader, MDSubscriber subscriber)
    {
        _socketReader = socketReader;
        _subscriber = subscriber;
    }

    public void run()
    {
        while (true)
        {
            MDMessage msg = _socketReader.getNextMessage();

            if (_subscriber != null)
            {
    	    	if(logger.isTraceEnabled())
                {
                    logger.trace("Inbound Msg: " + msg.toString());
                }

                _subscriber.notifyWithMDMessage(msg);
            }
            else
            {
                // Print out every message
                System.out.println("Inbound Msg: " + msg.toString());
            }
        }
    }
}
