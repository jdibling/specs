package com.theice.mdf.client.process;

import java.util.logging.Level;

import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.client.process.MDFClientInterface;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * <code>MDFClientMessageConsumer</code> gets messages from the reader
 * A version of the MDFClientSocketReader to support the GUI client
 * 
 * @author David Chen
 * @author Adam Athimuthu
 * @since 08/28/2007
 */
public class MDFClientMessageConsumer extends AbstractMDFMessageConsumer implements Runnable
{
    private static Logger logger = Logger.getLogger(MDFClientMessageConsumer.class.getName());
    private MDFClientInterface mdfClient = null;
    
    /**
     * Constructor
     * @param socketReader
     */
    public MDFClientMessageConsumer(MDFClientSocketReader socketReader, MarketHandlerFactoryInterface factory)
    {
    	this(socketReader, factory, null);
    }

    public MDFClientMessageConsumer(MDFClientSocketReader socketReader, MarketHandlerFactoryInterface factory, AbstractMDFClient client)
    {
     	super(socketReader);
        _handlerFactory=factory;
        mdfClient = client;
    }
    /**
     * Thread's run method that processes the messages using a handler
     */
    public void run()
    {
    	int messageCount = 0;
    	boolean receivingHistoryMessage = false;
    	
        while(_socketReader.isAlive())
        {
            MDMessage message=_socketReader.getNextMessage();
        	
            if(message==null)
            {
            	continue;
            }

            if(logger.isTraceEnabled())
            {
                logger.trace("Inbound Socket Message: " + message.toString());
            }

            char messageType=message.getMessageType();

            MarketMessageHandler handler=_handlerFactory.getHandler(messageType);

            /**
             * Wrap the message in PriceFeedMessage decorator with information on the bundle.
             * While using the multicast package, the simple TCP is only for doing login/product definitions
             * and NOT the market data requests. Technically, we shouldn't get any sequenced message here
             * through TCP. We will still check, just in case
             */
            PriceFeedMessage priceFeedMessage=null;
            
            if(message instanceof MDSequencedMessage)
            {
            	priceFeedMessage=new PriceFeedMessage((MDSequencedMessage) message,0L);
            	
            	logger.warn("Got a sequenced message while processing TCP feed in the multicast environment: "+message.toString());
            }
            else
            {
            	priceFeedMessage=new PriceFeedMessage(message);
            }
            
            if(handler!=null)
            {
                handler.handle(priceFeedMessage);
            }
            
            //special handling for history replay messages
            if (receivingHistoryMessage && mdfClient != null)
            {
            	if (--messageCount == 0 || messageType == RawMessageFactory.ErrorResponseType)
            	{
            		this.mdfClient.logoutAndCloseSocket();
            		String exitMessage = "Consumer thread exiting after closing socket connection...";
                    logger.info(exitMessage);
            		System.out.println(exitMessage);
            		return;
            	}
            }

            if (messageType == RawMessageFactory.HistoricalMarketDataResponseType) //got historical replay message
            {
            	com.theice.mdf.message.response.HistoricalMarketDataResponse response = (com.theice.mdf.message.response.HistoricalMarketDataResponse)message;
            	messageCount = response.getEndSequenceNumber() - response.getStartSequenceNumber() + 1;
            	receivingHistoryMessage = true;
                logger.info("Inbound Historical Replay Message. Number of messages expected: " + messageCount);
            }
        }
        
        String exitMsg = "Consumer thread is exiting...";
        logger.info(exitMsg);
        System.out.println(exitMsg);
        
        return;
        
    }
}

