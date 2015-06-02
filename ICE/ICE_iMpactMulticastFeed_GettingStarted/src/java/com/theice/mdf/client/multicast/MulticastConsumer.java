package com.theice.mdf.client.multicast;

import org.apache.log4j.Logger;

import com.theice.mdf.client.exception.SequenceException;
import com.theice.mdf.client.multicast.dispatcher.MDFMulticastDispatcher;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.util.MailThrottler;
import com.theice.mdf.message.MulticastMessageBlock;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastConsumer implements Runnable 
{
    static Logger logger=Logger.getLogger(MulticastConsumer.class.getName());

    private MulticastReceiver _receiver=null;
    private MDFMulticastDispatcher _dispatcher=null;
    private boolean keepRunning=true;
    private long lastStatusReportTimestamp=0;
    
	public MulticastConsumer(MulticastReceiver receiver,MDFMulticastDispatcher dispatcher)
	{
		_receiver=receiver;
		_dispatcher=dispatcher;
	}
	
	/**
	 * stop the consumer
	 */
	public void stop()
	{
		System.out.println("Consumer is Stopping the Dispatcher.");
		_dispatcher.stop();
		
		System.out.println("MulticastConsumer is stopping.");
		this.keepRunning=false;
	}

	/**
	 * The thread's run method
	 * Retrieve from the multicast queue and start processing
	 */
	public void run()
	{
		while(keepRunning)
		{
			try
			{
				MulticastMessageBlock messageBlock=_receiver.getNextMessage(MDFMulticastClient.POLLING_TIMEOUT_MILLISECONDS);
				
				if(messageBlock==null)
				{
					if(logger.isTraceEnabled())
					{
						logger.trace("No messages received for : "+MDFMulticastClient.POLLING_TIMEOUT_MILLISECONDS);
					}
					
					continue;
				}

				if(logger.isTraceEnabled())
				{
					logger.trace("*** MulticastConsumer Dispatching : "+messageBlock.SessionNumber+"/"+messageBlock.SequenceNumber+
							" ["+messageBlock.toString()+"]");
				}

				_dispatcher.dispatch(messageBlock);
				
				statusReport();
			}
			catch(InterruptedException e)
			{
				System.out.println("MulticastConsumer Interrupted.");
				e.printStackTrace();
				break;
			}
			catch(SequenceException ose)
			{
				StringBuffer buf=new StringBuffer();
				buf.append("Sequence Gap Detected. ChannelInfo: ").append(_receiver.getMulticastEndPoint());
				buf.append(" : Details : ").append(ose.getMessage());
				logger.error(buf.toString(),ose);
				MailThrottler.getInstance().enqueueError(buf.toString());
				_receiver.sendAlert("SequenceGap", "Sequence Gap Detected", AppManager.SEQUENCE_GAP_ERROR_CODE);
				
				keepRunning=false;
				
				break;
			}
			catch(Throwable ex)
			{
			   logger.error("MulticastConsumer: Throwable caught:"+ex.getMessage(),ex);
			}
			finally
			{
			}
		}
		
		logger.info("MulticastConsumer ["+Thread.currentThread().getName()+"] Exiting...");
		
	}
	
	private void statusReport()
	{
	   long currentTime = System.currentTimeMillis();
	   if (currentTime-lastStatusReportTimestamp>600000)
	   {
	      _receiver.sendHealthyStatus();
	      lastStatusReportTimestamp = currentTime;
	   }
	}
}

