package com.theice.mdf.client.multicast.process;

import com.theice.mdf.client.multicast.handler.AddPriceLevelHandler;
import com.theice.mdf.client.multicast.handler.ChangePriceLevelHandler;
import com.theice.mdf.client.multicast.handler.DeletePriceLevelHandler;
import com.theice.mdf.client.multicast.handler.MarketSnapshotPriceLevelHandler;
import com.theice.mdf.client.multicast.handler.NewOptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.AbstractMarketHandlerFactory;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.client.process.handlers.SimpleMessageHandler;
import com.theice.mdf.message.RawMessageFactory;

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
public class PriceLevelMarketHandlerFactory extends AbstractMarketHandlerFactory
{
    protected static PriceLevelMarketHandlerFactory _instance = new PriceLevelMarketHandlerFactory();

    final Logger logger=Logger.getLogger(PriceLevelMarketHandlerFactory.class.getName());

    public static PriceLevelMarketHandlerFactory getInstance()
    {
        return _instance;
    }

    /**
     * Market Handler Factory
     * Initialize the factory with handlers
     */
    protected PriceLevelMarketHandlerFactory()
    {
    	super();
    }

    /**
     * Register Handlers
     */
    protected void registerHandlers()
    {
        MarketMessageHandler handler=null;

        registerBasicHandlers();

        /**
         * Snapshot Channel handler(s)
         */
        handler=MarketSnapshotPriceLevelHandler.getInstance();
        _handlers.put(RawMessageFactory.MarketSnapshotPriceLevelMessageType,handler);
        
        /**
         * Live Channel handlers
         */
        handler=AddPriceLevelHandler.getInstance();
        _handlers.put(RawMessageFactory.AddPriceLevelMessageType,handler);

        handler=ChangePriceLevelHandler.getInstance();
        _handlers.put(RawMessageFactory.ChangePriceLevelMessageType,handler);
        
        handler=DeletePriceLevelHandler.getInstance();
        _handlers.put(RawMessageFactory.DeletePriceLevelMessageType,handler);
        
        handler=NewOptionStrategyDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.NewOptionStrategyDefinitionMessageType,handler);
        
        /**
         * TODO unregister the unneeded handlers
        _handlers.remove(RawMessageFactory.AddModifyOrderMessageType);
        _handlers.remove(RawMessageFactory.AddModifyOptionOrderMessageType);
        _handlers.remove(RawMessageFactory.DeleteOrderMessageType);
         */
        
    }

    protected MarketMessageHandler getDefaultHandler()
    {
    	return(SimpleMessageHandler.getInstance());
    }
}

