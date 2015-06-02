package com.theice.mdf.client.multicast.process;

import org.apache.log4j.Logger;

import com.theice.mdf.client.process.AbstractMDFClient;
import com.theice.mdf.client.process.MDFClientMessageConsumer;
import com.theice.mdf.client.process.MDFClientSocketReader;
import com.theice.mdf.message.request.LoginRequest;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The main application client that uses the socket reader for receiving market data feed. The messages
 * are then consumed by the MDF client consumer and distributed to individual handlers for processing.
 * 
 * This is a version of MDF client exclusively to support the associated price level multicast messages
 * The factory that this client uses, will have handlers to process the price level top of book messages 
 *
 * @author Adam Athimuthu
 */
public class PriceLevelMDFClient extends AbstractMDFClient implements Runnable
{
    static Logger logger=Logger.getLogger(PriceLevelMDFClient.class.getName());

    /**
     * Constructor
     */
    public PriceLevelMDFClient()
    {
    	super();
    	
    	_factory=PriceLevelMarketHandlerFactory.getInstance();
    }

    /**
     * run
     */
    public void run()
    {
        process();
    }

    /**
     * Create Login Request
     * @return
     */
    protected LoginRequest createLoginRequest()
    {
        return(createBaseLoginRequest());
    }

    /**
     * Create the consumer 
     * init with reader and the proper factory to handle messages
     */
    protected Runnable createMessageConsumer(MDFClientSocketReader reader)
    {
    	return(new MDFClientMessageConsumer(reader,_factory));
    }
}

