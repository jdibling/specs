package com.theice.mdf.client.process;

import org.apache.log4j.Logger;

public abstract class AbstractMDFMessageConsumer
{
    private static Logger logger = Logger.getLogger(AbstractMDFMessageConsumer.class.getName());

    MDFClientSocketReader _socketReader;

    protected MarketHandlerFactoryInterface _handlerFactory=null;

    /**
     * Constructor
     * @param socketReader
     */
    public AbstractMDFMessageConsumer(MDFClientSocketReader socketReader)
    {
        _socketReader=socketReader;
    }

}

