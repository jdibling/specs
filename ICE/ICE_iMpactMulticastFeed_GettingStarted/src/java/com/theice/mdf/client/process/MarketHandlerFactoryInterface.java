package com.theice.mdf.client.process;

import com.theice.mdf.client.process.handlers.MarketMessageHandler;

public interface MarketHandlerFactoryInterface
{
    public MarketMessageHandler getHandler(char messageType);
    public void registerHandler(char messageType, MarketMessageHandler handler);
}
