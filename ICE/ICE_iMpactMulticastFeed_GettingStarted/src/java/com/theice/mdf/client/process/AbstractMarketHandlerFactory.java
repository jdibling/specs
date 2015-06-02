package com.theice.mdf.client.process;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MDFConstants;
import com.theice.mdf.client.multicast.handler.NewFlexOptionDefinitionHandler;
import com.theice.mdf.client.process.handlers.AddModifyOrderHandler;
import com.theice.mdf.client.process.handlers.CancelledTradeHandler;
import com.theice.mdf.client.process.handlers.DeleteOrderHandler;
import com.theice.mdf.client.process.handlers.ErrorResponseHandler;
import com.theice.mdf.client.process.handlers.FuturesStrategyDefinitionHandler;
import com.theice.mdf.client.process.handlers.HeartBeatHandler;
import com.theice.mdf.client.process.handlers.HistoricalMarketDataHandler;
import com.theice.mdf.client.process.handlers.InvestigatedTradeHandler;
import com.theice.mdf.client.process.handlers.LoginResponseHandler;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.client.process.handlers.MarketSnapshotHandler;
import com.theice.mdf.client.process.handlers.MarketSnapshotOrderHandler;
import com.theice.mdf.client.process.handlers.MarketStateChangeHandler;
import com.theice.mdf.client.process.handlers.MarketStatisticsHandler;
import com.theice.mdf.client.process.handlers.OpenInterestHandler;
import com.theice.mdf.client.process.handlers.OpenPriceHandler;
import com.theice.mdf.client.process.handlers.OptionOpenInterestHandler;
import com.theice.mdf.client.process.handlers.OptionSettlementPriceHandler;
import com.theice.mdf.client.process.handlers.OptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.handlers.OptionsProductDefinitionHandler;
import com.theice.mdf.client.process.handlers.ProductDefinitionHandler;
import com.theice.mdf.client.process.handlers.SettlementPriceHandler;
import com.theice.mdf.client.process.handlers.SimpleMessageHandler;
import com.theice.mdf.client.process.handlers.SpotMarketTradeMessageHandler;
import com.theice.mdf.client.process.handlers.StripInfoMessageHandler;
import com.theice.mdf.client.process.handlers.SystemTextHandler;
import com.theice.mdf.client.process.handlers.TradeMessageHandler;
import com.theice.mdf.client.qv.process.handlers.QVEndOfDayMarketMessageHandler;
import com.theice.mdf.client.qv.process.handlers.QVMarkerPriceIndexHandler;
import com.theice.mdf.message.RawMessageFactory;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract Market Handler Factory keeps a map of handlers for each market type
 * The handlers are initialized and configured prior to registration with the factory.
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractMarketHandlerFactory implements MarketHandlerFactoryInterface 
{
    private static final Logger logger=Logger.getLogger(AbstractMarketHandlerFactory.class.getName());
    
    /**
     * map of handlers, per market type
     */
    protected Map<Character, MarketMessageHandler> _handlers=new HashMap();
    
    protected MarketMessageHandler _defaultHandler=null;

    /**
     * Notification interval in milliseconds
     */
    protected int _notificationInterval=MDFConstants.BOOK_REFRESH_INTERVAL;

    protected AbstractMarketHandlerFactory()
    {
        registerHandlers();
        
        _defaultHandler=getDefaultHandler();
        
        if(_defaultHandler==null)
        {
        	_defaultHandler=SimpleMessageHandler.getInstance();
        }
    }

    /**
     * Get handler for the given message type
     * @param messageType
     * @return
     */
    public MarketMessageHandler getHandler(char messageType)
    {
        MarketMessageHandler handler=(MarketMessageHandler) _handlers.get(new Character(messageType));

        if(handler==null)
        {
            handler=_defaultHandler;
        }

        return(handler);
    }

    /**
     * Initialize/configure individual handlers for core messages
     * such as login/product definition, system text and error
     */
    protected void registerCoreMessageHandlers()
    {
        MarketMessageHandler handler=null;
        
        handler=ProductDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.ProductDefinitionResponseType,handler);
        
        handler=OptionsProductDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.OptionsProductDefinitionResponseType,handler);

        handler=OptionStrategyDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.OptionStrategyDefinitionResponseType,handler);

        handler=FuturesStrategyDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.FuturesStrategyDefinitionResponseType,handler);

        handler=LoginResponseHandler.getInstance();
        _handlers.put(RawMessageFactory.LoginResponseType,handler);

        handler=SystemTextHandler.getInstance();
        _handlers.put(RawMessageFactory.SystemTextMessageType,handler);
        
        handler=ErrorResponseHandler.getInstance();
        _handlers.put(RawMessageFactory.ErrorResponseType,handler);

        handler=HeartBeatHandler.getInstance();
        _handlers.put(RawMessageFactory.HeartBeatMessageType,handler);
        
    	return;
    }
    
    /**
     * Initialize/configure individual handlers and register them with the factory
     */
    protected void registerBasicHandlers()
    {
        MarketMessageHandler handler=null;

        logger.info("Initializing and Registering message handlers.");
        
        registerCoreMessageHandlers();
        
        handler=MarketSnapshotHandler.getInstance();
        _handlers.put(RawMessageFactory.MarketSnapshotMessageType,handler);

        handler=MarketSnapshotOrderHandler.getInstance();
        _handlers.put(RawMessageFactory.MarketSnapshotOrderMessageType,handler);

        handler=MarketStatisticsHandler.getInstance();
        _handlers.put(RawMessageFactory.MarketStatisticsMessageType,handler);

        handler=OpenInterestHandler.getInstance();
        _handlers.put(RawMessageFactory.OpenInterestMessageType,handler);

        handler=OptionOpenInterestHandler.getInstance();
        _handlers.put(RawMessageFactory.OptionOpenInterestMessageType,handler);
        
        handler=OpenPriceHandler.getInstance();
        _handlers.put(RawMessageFactory.OpenPriceMessageType,handler);

        handler=SettlementPriceHandler.getInstance();
        _handlers.put(RawMessageFactory.SettlementPriceMessageType,handler);
        
        handler=OptionSettlementPriceHandler.getInstance();
        _handlers.put(RawMessageFactory.OptionSettlementPriceMessageType,handler);

        handler=AddModifyOrderHandler.getInstance();
        _handlers.put(RawMessageFactory.AddModifyOrderMessageType,handler);

        handler=DeleteOrderHandler.getInstance();
        _handlers.put(RawMessageFactory.DeleteOrderMessageType,handler);

        handler=TradeMessageHandler.getInstance();
        _handlers.put(RawMessageFactory.TradeMessageType,handler);

        handler=SpotMarketTradeMessageHandler.getInstance();
        _handlers.put(RawMessageFactory.SpotMarketTradeMessageType,handler);

        handler=MarketStateChangeHandler.getInstance();
        _handlers.put(RawMessageFactory.MarketStateChangeMessageType,handler);

        handler=CancelledTradeHandler.getInstance();
        _handlers.put(RawMessageFactory.CancelledTradeMessageType,handler);
        
        handler=InvestigatedTradeHandler.getInstance();
        _handlers.put(RawMessageFactory.InvestigatedTradeMessageType,handler);
        
        handler=StripInfoMessageHandler.getInstance();
        _handlers.put(RawMessageFactory.StripInfoMessageType, handler);
        
        handler=OptionStrategyDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.NewOptionStrategyDefinitionMessageType, handler);

        handler=FuturesStrategyDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.NewFuturesStrategyDefinitionMessageType, handler);

        
        handler=NewFlexOptionDefinitionHandler.getInstance();
        _handlers.put(RawMessageFactory.NewOptionsMarketDefinitionMessageType, handler);

        handler=QVMarkerPriceIndexHandler.getInstance();
        _handlers.put(RawMessageFactory.QVMarkerIndexPriceResponseType,handler);
        
        handler=QVEndOfDayMarketMessageHandler.getInstance();
        _handlers.put(RawMessageFactory.QVEndOfDayMarketSummaryMessageType,handler);

        /**
         * Handlers associated with on-demand requests. Currently we support the following
         * on-demand requests for all the multicast contexts
         * 
         * 1. Historical Market Data
         * 2. Option Open Interest (previously associated with QV)
         */
        handler=HistoricalMarketDataHandler.getInstance();
        _handlers.put(RawMessageFactory.HistoricalMarketDataResponseType,handler);
        
        return;
    }
    
    public void registerHandler(char messageType, MarketMessageHandler handler)
    {
       _handlers.put(messageType, handler);
    }

    protected abstract void registerHandlers();
    
    protected abstract MarketMessageHandler getDefaultHandler();
}

