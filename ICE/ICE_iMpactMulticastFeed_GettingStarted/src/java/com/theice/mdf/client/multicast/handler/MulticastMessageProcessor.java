package com.theice.mdf.client.multicast.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.MDFMulticastClient;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.client.process.handlers.MarketMessageHandler;
import com.theice.mdf.message.MDSequencedMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The queue contains the decorated PriceFeedMessage wrapping the MDSequencedMessage
 * The PriceFeedMessage identifies if a message is bundled or not
 * 
 * @author Adam Athimuthu
 */
public class MulticastMessageProcessor implements Runnable 
{
    static final Logger logger=Logger.getLogger(MulticastMessageProcessor.class.getName());
    
	private BlockingQueue<PriceFeedMessage> _queue=null;
	private MarketHandlerFactoryInterface _factory=null;
	
    private boolean keepRunning=true;

    private MulticastMessageProcessor()
	{
	}

	public MulticastMessageProcessor(BlockingQueue<PriceFeedMessage> queue,MarketHandlerFactoryInterface factory)
	{
		_queue=queue;
		_factory=factory;
	}
	
	public void run()
	{
		MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
		int queueSizeMonitoringThreshold=configuration.getMDFClientRuntimeParameters().getQueueSizeMonitoringThreshold();
			
		while(keepRunning)
		{
			try
			{
				PriceFeedMessage message=_queue.poll(MDFMulticastClient.POLLING_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
				
				if(message==null)
				{
					continue;
				}
				
				/**
				 * Monitor the queue size and report excessive backlogs
				 */
				int queueSize=_queue.size();
				
				if(queueSize>queueSizeMonitoringThreshold)
				{
					StringBuffer buf=new StringBuffer();
					buf.append("### Message Queue Size : [").append(queueSize);
					buf.append("]. Exceeds the threshold of : ").append(queueSizeMonitoringThreshold);
					logger.warn(buf.toString());
					System.err.println(buf.toString());
				}

				if(logger.isTraceEnabled())
				{
					logger.trace("MulticastMessageProcessor: >>> Processing "+message.toString());
				}
				
				MarketMessageHandler handler=_factory.getHandler(message.getMessage().getMessageType());
				
	            if(handler!=null)
	            {
	                handler.handle(message);
	            }
			}
			catch(InterruptedException e)
			{
				logger.error("MulticastMessageProcessor. take() from queue interrupted : "+e.toString());
			}
		}
		
		System.out.println("MulticastMessageProcessor Exiting...");
	}
	
    /**
     * stop
     */
	public void stop()
	{
		System.out.println("Stopping the MulticastMessageProcessor");
		keepRunning=false;
	}

}
