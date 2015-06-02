package com.theice.mdf.client.process;

import com.theice.mdf.client.process.handlers.BypassMessageHandler;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The Core Message Handler Factory keeps a map of handlers for the core messages
 * such as login and static content
 * 
 * Typically, for a client using the multicast channel for notification messages, 
 * this factory is used for handling the messages received by the TCP/IP socket connection
 * 
 * This factory uses active handlers for only the login and static messages
 * 	Login
 * 	ProductDefinition
 * 	Error
 * 	SystemText
 * 	HeartBeat
 * 
 * A Bypass Handler is used for other notification (order/trade) messages. While receiving historical
 * data feed, the bypass handlers can be "subscribed-to" for further processing, without affecting
 * the book
 * 
 * @author Adam Athimuthu
 */
public class CoreMessageHandlerFactory extends AbstractMarketHandlerFactory
{
    protected static CoreMessageHandlerFactory _instance = new CoreMessageHandlerFactory();

    final Logger logger=Logger.getLogger(CoreMessageHandlerFactory.class.getName());

    public static CoreMessageHandlerFactory getInstance()
    {
        return _instance;
    }

    /**
     * Market Handler Factory
     * Initialize the factory with handlers
     */
    protected CoreMessageHandlerFactory()
    {
    	super();
    }

    /**
     * Register Handlers
     */
    protected void registerHandlers()
    {
    	registerCoreMessageHandlers();
    }

    protected MarketMessageHandler getDefaultHandler()
    {
    	return(BypassMessageHandler.getInstance());
    }
}

