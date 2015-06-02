package com.theice.mdf.client.domain;

import com.theice.mdf.client.process.AbstractMarketHandlerFactory;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.client.process.handlers.SimpleMessageHandler;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Market Handler Factory keeps a map of handlers for each market type
 * The handlers are initialized and configured prior to registration with the factory.
 * 
 * @author Adam Athimuthu
 * Date: Aug 15, 2007
 * Time: 12:58:58 PM
 *
 */
public class MarketHandlerFactory extends AbstractMarketHandlerFactory
{
    protected static MarketHandlerFactory _instance = new MarketHandlerFactory();

    final Logger logger=Logger.getLogger(MarketHandlerFactory.class.getName());

    public static MarketHandlerFactory getInstance()
    {
        return _instance;
    }

    /**
     * Market Handler Factory
     * Initialize the factory with handlers
     */
    protected MarketHandlerFactory()
    {
    	super();
    }

    /**
     * Register Handlers
     */
    protected void registerHandlers()
    {
    	registerBasicHandlers();
    }

    protected MarketMessageHandler getDefaultHandler()
    {
    	return(SimpleMessageHandler.getInstance());
    }
}

