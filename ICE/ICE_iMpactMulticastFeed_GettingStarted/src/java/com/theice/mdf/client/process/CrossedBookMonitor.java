package com.theice.mdf.client.process;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.CrossedBookDetectionInfo;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.config.domain.MDFClientRuntimeParameters;
import com.theice.mdf.client.domain.book.CrossedBookInfo;
import com.theice.mdf.client.domain.book.CrossedBookTracker;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.util.MailThrottler;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Maintains a registry of market types for which delayed crossed book monitoring has to be performed
 * Maintains a single CrossedBookInfo per market
 * Report on whether the crossed book clears at given intervals
 * 
 * Used for OTC markets, where crossed situation is more common, but we expect the situation
 * not to last too long.
 * 
 * Also, once we report on a crossed book situation exceeding a given threshold, we won't report on it
 * until it clears.
 * 
 * @author Adam Athimuthu
 */
public class CrossedBookMonitor implements Runnable
{
	private static final Logger logger=Logger.getLogger(CrossedBookMonitor.class.getName());

	private static CrossedBookMonitor instance=new CrossedBookMonitor();

	private static Set<Short> registeredMarketTypes=new HashSet<Short>();
	private static Map<Integer,CrossedBookTracker> crossedBookMap=new ConcurrentHashMap<Integer,CrossedBookTracker>();
	
	private long alertThreshold=120000;
	
	private long monitoringIntervalMs=30000;
	
	private Thread crossedBookMonitorThread=new Thread(this,"CrossedBookMonitorThread");
	
	private boolean isRunning=false;
	
	private boolean crossedBookDetectionEnabled=false;

	/**
     * Mail Throttler
     */
    private static MailThrottler mailThrottler=MailThrottler.getInstance();
    
	public static CrossedBookMonitor getInstance() 
	{
		return(instance);
	}

	private CrossedBookMonitor() 
	{
	}

	/**
	 * initialize()
	 * If crossed book monitoring is enabled, then override the purge flag in the mail throttler
	 * We need to preserve all the messages generated due to crossed book issues
	 * @throws InitializationException
	 */
	public void initialize() throws InitializationException
	{
		MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
		
		if(configuration==null)
		{
			String message="No configuration selected. Assuming Crossed Book Detection NOT Enabled.";
			logger.warn(message);
			return;
		}
		
		MDFClientRuntimeParameters parameters=configuration.getMDFClientRuntimeParameters();
		
    	System.out.println("### Is Crossed Book Detection Enabled?="+parameters.isCrossedBookDetectionEnabled());
    	
		if(parameters.isCrossedBookDetectionEnabled())
    	{
    		this.crossedBookDetectionEnabled=true;
    		
    		initMailThrottler();
    		
			CrossedBookDetectionInfo crossedBookDetectionInfo=parameters.getCrossedBookDetectionInfo();

    		this.initDelayedCrossedBookAlertInfo(crossedBookDetectionInfo);
    	}
		else
		{
	    	System.out.println("### Crossed Book Detection NOT Enabled");
		}
		
		return;
	}

	/**
	 * Init the mail throttler
	 * TODO enhance the mail throttler for a lazy init that will spawn the throttler thread
	 */
	private void initMailThrottler()
	{
    	System.out.println("### CrossedBookMonitor - Setting the mail throttler's purge flag to false.");
    	
    	MailThrottler.getInstance().setPurgeAfterFirstEmail(false);

    	return;
	}

	/**
	 * If specific market types have crossed book (delayed) monitoring enabled,
	 * register them with the CrossedBookMonitor
	 * If at least one market type has been registered, start the CrossedBook Monitor thread
	 * @param crossedBookDetectionInfo
	 */
	private void initDelayedCrossedBookAlertInfo(CrossedBookDetectionInfo crossedBookDetectionInfo)
	{
    	Short crossBookDelayedMarketTypes[]=crossedBookDetectionInfo.getCrossedBookAlertDelayedMarketTypes();
    	
    	if(crossBookDelayedMarketTypes!=null && crossBookDelayedMarketTypes.length>0)
    	{
        	registerMarketTypes(crossBookDelayedMarketTypes);
        	
        	this.alertThreshold=crossedBookDetectionInfo.getCrossedBookDelayedAlertThresholdMs();
        	this.monitoringIntervalMs=crossedBookDetectionInfo.getCrossedBookDelayedAlertMonitoringIntervalMs();

        	start();
    	}
    	else
    	{
    		logger.info("No market types registered for delayed crossed book monitoring.");
    	}

    	return;
	}
	
	public boolean isCrossedBookDetectionEnabled()
	{
		return(this.crossedBookDetectionEnabled);
	}

	public void shutdown()
	{
    	if(crossedBookDetectionEnabled)
    	{
    		if(mailThrottler!=null)
    		{
    	    	mailThrottler.haltService();
    	    	
    	    	try
    	    	{
    	        	logger.info("Waiting for the logger thread to exit.");
    	        	Thread.sleep(1000);
    	    	}
    	    	catch(InterruptedException e)
    	    	{
    	    	}
    		}
    		
    		if(isRunning())
    		{
    			stop();
    		}
    	}
	}
	
    /**
     * write alert messages on the stderr and through SMTP
     * @param message
     * @param level
     */
	public void raiseAlert(String message)
    {
    	StringBuffer buffer=new StringBuffer();
    	buffer.append("[").append(Level.ERROR.toString()).append("] ");
    	buffer.append("[").append(Thread.currentThread().getName()).append("] ").append(message);
    	
		logger.error(message.toString());
    	mailThrottler.enqueueError(buffer.toString());
    	return;
    }

	/**
	 * register a market type for (delayed) crossed book monitoring
	 * @param market type
	 */
	public void registerMarketType(short marketType) 
	{
		this.registeredMarketTypes.add(new Short(marketType));
	}

	/**
	 * register all the markets for delayed alert for crossed book conditions
	 * @param market types
	 */
	private void registerMarketTypes(Short marketTypes[])
	{
    	for(int index=0;index<marketTypes.length;index++)
    	{
    		System.out.println("### Registering market type for Delayed Cross Book Alert : "+marketTypes[index]);
    		this.registeredMarketTypes.add(marketTypes[index]);
    	}
	}

	/**
	 * has any market has been registered for crossed book monitoring?
	 * @return
	 */
	public boolean hasAnyMarketTypesRegistered()
	{
		return(this.registeredMarketTypes.size()>0);
	}

	/**
	 * is the given market type registered for delayed crossed book monitoring?
	 * @param market type
	 * @return
	 */
	public boolean isRegisteredForDelayedMonitoring(short marketType)
	{
		return(this.registeredMarketTypes.contains(new Short(marketType)));
	}

	public void registerCrossedBookOccurrence(CrossedBookInfo crossedBookInfo) 
	{
		logger.warn("Registering crossed book for threshold monitoring : "+crossedBookInfo.toString());

		synchronized(crossedBookMap)
		{
			this.crossedBookMap.put(new Integer(crossedBookInfo.getMarketId()), new CrossedBookTracker(crossedBookInfo));
		}
	}

	/**
	 * Clear the crossed book condition
	 * If we had raised an alert at least once, it indicates that the threshold has been exceeded once
	 * Raise a clearing alert ONLY if the threshold had exceeded previously, otherwise log as a warning and proceed
	 * @param marketId
	 * @return
	 */
	public CrossedBookInfo clearCrossedBook(int marketId) 
	{
		CrossedBookInfo clearedCrossedBookInfo=null;
		
		if(logger.isTraceEnabled())
		{
			logger.trace("Clearing crossed book for : "+marketId);
		}

		synchronized(crossedBookMap)
		{
			CrossedBookTracker tracker=this.crossedBookMap.remove(new Integer(marketId));
			
			if(tracker!=null)
			{
				clearedCrossedBookInfo=tracker.getCrossedBookInfo();

				StringBuffer buf=new StringBuffer("### CrossedBook Cleared ### ");
    			buf.append(clearedCrossedBookInfo.toString());
    			
				if(tracker.isAlertSent())
				{
					buf.append("--Cleared after exceeding the Threshold.");
	    			raiseAlert(buf.toString());
				}
				else
				{
					buf.append("--Cleared prior to exceeding the Threshold.");
					logger.warn(buf.toString());
				}
			}
		}
		
		return(clearedCrossedBookInfo);
	}
	
	/**
	 * Check crossed book status
	 * If any of the crossed books have exceeded the threshold, then raise an alert.
	 */
	private void checkCrossedBookStatus()
	{
		synchronized(crossedBookMap)
		{
			if(crossedBookMap.isEmpty())
			{
				return;
			}
			
			long now=System.currentTimeMillis();
			
			for(Iterator<CrossedBookTracker> it=crossedBookMap.values().iterator();it.hasNext();)
			{
				CrossedBookTracker tracker=it.next();
				
				CrossedBookInfo crossedBookInfo=tracker.getCrossedBookInfo();
				
				long howLong=now-crossedBookInfo.getStartTime();
				
				if(howLong>=alertThreshold)
				{
					StringBuffer buf=new StringBuffer("##### Alert threshold ").append(this.alertThreshold);
					buf.append(" Exceeded. ").append(crossedBookInfo.toString());

					if(tracker.isAlertSent())
					{
						buf.append("---Alert has already been sent.");
						
						if(logger.isDebugEnabled())
						{
							logger.debug(buf.toString());
						}
					}
					else
					{
	        			raiseAlert(buf.toString());
	        			tracker.setAlertSent();
					}
				}
			}
		}
		
		return;
	}
	
	public boolean isRunning()
	{
		return(isRunning);
	}
	
	public void start()
	{
		System.out.println("### Starting CrossedBookMonitor thread.");
		
		if (!this.crossedBookMonitorThread.isAlive())
		{
		   isRunning=true;
		   this.crossedBookMonitorThread.start();
		}
		
		return;
	}

	public void stop()
	{
		System.out.println("### Stopping CrossedBookMonitor thread.");
		
		isRunning=false;
		
		try
		{
			this.crossedBookMonitorThread.join(1000);
		}
		catch(InterruptedException e)
		{
		}
		return;
	}

	public void run()
	{
		StringBuffer buf=new StringBuffer("Crossed Book Monitor thread starting. Monitoring interval (ms)=");
		buf.append(monitoringIntervalMs).append(". Crossed Book Delayed Alert Threshold (ms) = ").append(alertThreshold);
		logger.info(buf.toString());
		
		while(isRunning)
		{
			try
			{
				Thread.sleep(this.monitoringIntervalMs);
			}
			catch(InterruptedException e)
			{
			}
			
			checkCrossedBookStatus();
		}
		
		return;
	}
	
}


