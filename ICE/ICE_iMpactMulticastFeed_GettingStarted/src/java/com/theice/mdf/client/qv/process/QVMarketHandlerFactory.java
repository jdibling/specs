package com.theice.mdf.client.qv.process;

import com.theice.mdf.client.process.AbstractMarketHandlerFactory;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.client.process.handlers.SimpleMessageHandler;
import com.theice.mdf.client.qv.process.handlers.QVEndOfDayMarketMessageHandler;
import com.theice.mdf.client.qv.process.handlers.QVMarkerPriceIndexHandler;
import com.theice.mdf.message.RawMessageFactory;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Extended Market Handler Factory handles the QV messages in addition to the regular market messages
 * 
 * @author Adam Athimuthu
 */
public class QVMarketHandlerFactory extends AbstractMarketHandlerFactory
{
    private static Logger logger=Logger.getLogger(QVMarketHandlerFactory.class.getName());

    protected static QVMarketHandlerFactory _instance = new QVMarketHandlerFactory();

    public static QVMarketHandlerFactory getInstance()
    {
        return _instance;
    }

    protected QVMarketHandlerFactory()
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
        
        handler=QVMarkerPriceIndexHandler.getInstance();
        _handlers.put(RawMessageFactory.QVMarkerIndexPriceResponseType,handler);
        
        handler=QVEndOfDayMarketMessageHandler.getInstance();
        _handlers.put(RawMessageFactory.QVEndOfDayMarketSummaryMessageType,handler);
        
    	return;
    }
    
    protected MarketMessageHandler getDefaultHandler()
    {
    	return(SimpleMessageHandler.getInstance());
    }
}

