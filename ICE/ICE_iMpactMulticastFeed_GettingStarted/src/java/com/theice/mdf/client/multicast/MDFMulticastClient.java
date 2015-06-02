package com.theice.mdf.client.multicast;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;

import com.theice.mdf.client.multicast.dispatcher.MDFMulticastDispatcher;
import com.theice.mdf.client.multicast.handler.MarketLoadManager;
import com.theice.mdf.client.multicast.handler.MulticastClientConfig;
import com.theice.mdf.client.process.AppManager;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MDFMulticastClient implements Runnable
{
    static Logger logger=Logger.getLogger(MDFMulticastClient.class.getName());
        
    private MulticastClientConfig _config=null;
    private String _multicastGroupName=null;
    private boolean _forSnapshot=false;
    
    public static int POLLING_TIMEOUT_MILLISECONDS=100;
    
    private MDFMulticastClient()
    {
    }
    
    public MDFMulticastClient(MulticastClientConfig config, String groupName, boolean forSnapshot)
    {
    	this._config=config;
    	this._multicastGroupName=groupName;
    	this._forSnapshot=forSnapshot;
    }
    
    public MulticastClientConfig getConfig()
    {
    	return(_config);
    }

    /**
     * thread's run method
     * start the multicast receiver on the given ip/port
     * start the consumer to process the messages
     * Keep running and wait for the receiver to finish
     */
    public void run()
    {
        MulticastReceiver receiver=null;
        MulticastConsumer consumer=null;
        
        StringBuffer threadName=null;
        
    	try
    	{
    		//if(!AppManager.isMarketDataLoadCompleted())
    	   if(MarketLoadManager.getInstance().getLoadingStatus()!=MarketLoadManager.LOAD_COMPLETED)
    		{
    			waitForMarketDataLoad();
    		}
    		else
    		{
        		logger.info("Market static data has already been loaded");
    		}
    		
    		logger.info("Starting the multicast receiver");
    		
    		receiver=new MulticastReceiver(_config.getMulticastEndPoint(), _config.getNetworkInterface(), _multicastGroupName, _forSnapshot);
        	
        	try
        	{
        		receiver.openMulticastChannel();
        	}
        	catch(IOException e)
        	{
        		logger.error("Error opening multicast channel : "+_config.getIpAddress()+"/"+_config.getPort()+" : "+e.toString());
        		e.printStackTrace();
        		throw(e);
        	}
        	
        	threadName=new StringBuffer(_config.getName()).append(" - Multicast Receiver");
        	threadName.append("-"+_multicastGroupName);
        	
    		Thread multicastReceiver=new Thread(receiver,threadName.toString());
    		multicastReceiver.start();
    		
        	threadName=new StringBuffer(_config.getName()).append(" - Multicast Consumer");
         threadName.append("-"+_multicastGroupName);
         
        	MDFMulticastDispatcher dispatcher=_config.getDispatcher();
        	CountDownLatch shutdownLatch=dispatcher.getShutdownLatch();
        	dispatcher.initialize();
    		
        	consumer=new MulticastConsumer(receiver,dispatcher);
        	Thread multicastConsumer=new Thread(consumer,threadName.toString());
    		multicastConsumer.start();

    		/**
    		 * Shutdown latches are applicable for the snapshot channels.
    		 * These latches are activated either through a countdown or through a timer (see options snapshot)
    		 */
    		if(shutdownLatch!=null)
    		{
    			System.out.println(_config.getName()+" - Waiting for the ShutdownLatch to become zero.");
    			shutdownLatch.await();
    			
    			System.out.println(_config.getName()+" - ShutdownLatch activated. Stopping the Consumer.");
    			consumer.stop();
    			
    			System.out.println(_config.getName()+" - ShutdownLatch activated. Stopping the Receiver.");
    			receiver.stop();
    		}
    		else
    		{
    			System.out.println(_config.getName()+" - No ShutdownLatch : Join the receiver thread...");
        		multicastReceiver.join();
        		multicastConsumer.join();
    		}
    	}
    	catch(InterruptedException e)
    	{
    		e.printStackTrace();
    		logger.warn("Interrupted : "+e.toString());
    		
    		consumer.stop();
    		receiver.stop();
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		logger.error(e.toString());
    	}
    	finally
    	{
			System.out.println(_config.getName()+" - MDFMulticastClient wait for a clean shutdown of child threads");
			
			try
			{
				Thread.sleep(300);
			}
			catch(Exception e){}

			System.out.println(_config.getName()+" - Shutting down the multicast client");
    	}
    }
    
    /**
     * Wait for the market static data to be loaded
     */
    protected void waitForMarketDataLoad()
    {
		System.out.println(this._config.getName()+" - Waiting for the market static data to be loaded");
		
    	try
    	{
    		Object mutex=MarketLoadManager.getInstance().getMutex();
    		
    		synchronized(mutex)
    		{
        		logger.info(_config.getName()+" - Waiting for Market Load to complete + "+System.currentTimeMillis());
        		
        		mutex.wait();
        		
        		Integer status=MarketLoadManager.getInstance().getLoadingStatus();
        		
        		logger.info(_config.getName()+" - Waking up with status="+status+" at "+System.currentTimeMillis());
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		logger.error(e.toString());
    	}
    	
    	return;
    }
    
    public boolean isForSnapshot()
    {
       return this._forSnapshot;
    }

}

