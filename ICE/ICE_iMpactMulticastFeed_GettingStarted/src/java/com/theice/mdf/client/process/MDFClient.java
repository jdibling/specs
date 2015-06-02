package com.theice.mdf.client.process;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;

import com.theice.mdf.client.exception.ProcessingException;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.request.LoginRequest;
import com.theice.mdf.message.request.HistoricalMarketDataRequest;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The main application client that uses the socket reader for receiving market data feed. The messages
 * are then consumed by the MDF client consumer and distributed to individual handlers for processing. 
 *
 * @author Adam Athimuthu
 * Date: Aug 1, 2007
 * Time: 10:57:50 AM
 */
public class MDFClient extends AbstractMDFClient implements Runnable
{
    static Logger logger=Logger.getLogger(MDFClient.class.getName());
    protected static int connectCount=0;

    /**
     * Constructor
     */
    public MDFClient()
    {
    	super();
    	
    	_factory=AppManager.getCoreMessageHandlerFactory();
    }

    public void registerExtraMessageHandler(char messageType, MarketMessageHandler handler)
    {
       _factory.registerHandler(messageType, handler);
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

    /**
     * send HistoricalMarketDataRequest - on demand
     * @param session
     * @param group
     * @param port
     * @param startSeq
     * @param endSeq
     * @return
     * @throws ProcessingException
     */
    public int sendHistoricalMarketDataRequest(short session, String group, short port, int startSeq, int endSeq)
    	throws ProcessingException
    {
    	int requestNumber=(-1);
    	
    	if(session<=0)
    	{
    		String msg="Invalid session";
    		logger.warn(msg);
    		throw(new ProcessingException(msg));
    	}
    	
    	if(group==null || group.trim().equals(""))
    	{
    		String msg="Invalid group";
    		logger.warn(msg);
    		throw(new ProcessingException(msg));
    	}

    	if(port<=0)
    	{
    		String msg="Invalid port";
    		logger.warn(msg);
    		throw(new ProcessingException(msg));
    	}

    	try
    	{
			requestNumber=getRequestSeqID();
			
			if (clientSoc.isClosed()) {
				System.out.println("MDFClient: socket connection is closed. Re-establish the connection...");
				if (connect()) 
				{
					login(clientSoc.getOutputStream());
					InputStream inStream = clientSoc.getInputStream();
					MDFClient.connectCount++;
					
					// Start socket reader thread for processing response/streamed data from server
					MDFClientSocketReader reader = new MDFClientSocketReader(new DataInputStream(inStream));
					Thread readerThread = new Thread(reader, "ReaderThread-" + MDFClient.connectCount);
					readerThread.start();
					
					// Start message consumer thread for processing messages
					Thread consumerThread = new Thread(createMessageConsumer(reader), "ConsumerThread-" + MDFClient.connectCount);
					consumerThread.start();
					AppManager.startInactivityTimer();
				}
				else
				{
					throw new ProcessingException("Client failed to establish TCP connection to server.");
				}
			}				

			OutputStream outStream=(OutputStream) clientSoc.getOutputStream();

			HistoricalMarketDataRequest req=new HistoricalMarketDataRequest();
			req.RequestSeqID=requestNumber;
			req.setSessionId(session);
			req.setGroupAddress(MessageUtil.toRawChars(group,req.getGroupAddress().length));
			req.setPort(port);
			req.setStartSequenceNumber(startSeq);
			req.setEndSequenceNumber(endSeq);
			
			System.out.println("*** Historical Data Request : "+req.toString());
			
			byte[] bytes=req.serialize();
			outStream.write(bytes);
    	}
    	catch(IOException ioe)
    	{
    		logger.error("Exception while trying to send the Historical Market Data request : "+ioe.toString());
    		ioe.printStackTrace();
    	}
    	
    	return(requestNumber);
    }
    
}

