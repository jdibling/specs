package com.theice.mdf.client.qv.process;

import org.apache.log4j.Logger;

import com.theice.mdf.client.process.AbstractMDFClient;
import com.theice.mdf.client.process.AppManager;
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
 * @deprecated
 * @author Adam Athimuthu
 */
public class QVMDFClient extends AbstractMDFClient implements Runnable
{
    static Logger logger=Logger.getLogger(QVMDFClient.class.getName());

    public QVMDFClient()
    {
    	super();
    	
    	_factory=AppManager.getInstance(null).getMessageHandlerFactory();
    }

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
    	LoginRequest loginRequest=createBaseLoginRequest();
        return(loginRequest);
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
     * send QVMarkerIndexPrice Request
     * @param market type
     * @param request number that can be used by the calling client to match the response
     */
//    public int sendQVMarkerIndexPriceRequest(short marketType)
//    {
//    	int requestNumber=(-1);
//    	
//    	try
//    	{
//			OutputStream outStream=(OutputStream) clientSoc.getOutputStream();
//			
//			requestNumber=getRequestSeqID();
//
//			QVMarkerIndexPriceRequest req=new QVMarkerIndexPriceRequest();
//			req.RequestSeqID=requestNumber;
//			req.setMarketType(marketType);
//			
//			byte[] bytes=req.serialize();
//			outStream.write(bytes);
//    	}
//    	catch(IOException ioe)
//    	{
//    		logger.error("Exception while trying to send the QV request : "+ioe.toString());
//    		ioe.printStackTrace();
//    	}
//    	
//    	return(requestNumber);
//    }
//
//    /**
//     * send QVOptionSettlementPrice Request
//     * @param market type
//     * @param request number that can be used by the calling client to match the response
//     */
//    public int sendQVOptionSettlementPriceRequest(short marketType)
//    {
//    	int requestNumber=(-1);
//    	
//    	try
//    	{
//			OutputStream outStream=(OutputStream) clientSoc.getOutputStream();
//			
//			requestNumber=getRequestSeqID();
//
//			QVOptionSettlementPriceRequest req=new QVOptionSettlementPriceRequest();
//			req.RequestSeqID=requestNumber;
//			req.setMarketType(marketType);
//			
//			byte[] bytes=req.serialize();
//			outStream.write(bytes);
//    	}
//    	catch(IOException ioe)
//    	{
//    		logger.error("Exception while trying to send the QV request : "+ioe.toString());
//    		ioe.printStackTrace();
//    	}
//    	
//    	return(requestNumber);
//    }
    
}

