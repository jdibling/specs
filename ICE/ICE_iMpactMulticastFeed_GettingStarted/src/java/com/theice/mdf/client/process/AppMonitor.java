package com.theice.mdf.client.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.event.ApplicationEvent;
import com.theice.mdf.client.domain.event.ApplicationEventPublisher;
import com.theice.mdf.client.domain.event.ApplicationEventSubscriber;
import com.theice.mdf.client.domain.event.NetworkInactivity;
import com.theice.mdf.client.domain.state.ApplicationStatus;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * A thread that Monitors and takes action during key events that occur within the application
 * - reinitializes the application during sequence problems
 * - ensures a clean shutdown during abnormal situations
 * 
 * @author Adam Athimuthu
 */
public class AppMonitor implements Runnable, ApplicationEventPublisher 
{
    static Logger logger=Logger.getLogger(AppMonitor.class.getName());

    protected List<ApplicationEventSubscriber> subscribers= 
    	Collections.synchronizedList(new ArrayList<ApplicationEventSubscriber>());

    protected Object mutex=new Object(); 
    
    private String _multicastGroupName=null;

    private boolean keepRunning=true;
    
	public AppMonitor(String groupName)
	{
	   _multicastGroupName = groupName;
	}
	
	public Object getMutex()
	{
		return(mutex);
	}
	
	public void wakeup()
	{
		try
		{
			synchronized(mutex)
			{
	    		logger.info("AppMonitor waking up : "+System.currentTimeMillis());
	    		mutex.notify();
			}
		}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		logger.error(e.toString());
    	}
		finally
		{
		}
	}
	
	/**
	 * Examing the application and its current status.
	 * Take corrective action based on the status of the application
	 */
	protected void examineApplication()
	{
		String msg="";
		
		System.out.println("AppMontior - Examining the Application...");
		
		ApplicationStatus status=AppManager.getInstance(_multicastGroupName).getApplicationStatus();
		
		System.out.println("AppMontior - Application Status is..."+status.toString());

		switch(status)
		{
		case SEQUENCEPROBLEM:
			msg="AppMontior detected a Sequence Problem...";
			System.err.println(msg);
			logger.error(msg);
			doSequeceProblemAction();
			break;
			
		case NETWORKINACTIVITY:
		case NETWORKERROR:
			msg="AppMontior detected a "+status.toString();
			System.err.println(msg);
			logger.error(msg);
			doNetworkProblemAction();
			break;
			
		default:
			break;
		}
		
		return;
	}

	/**
	 * do sequence problem action
	 */
	protected void doSequeceProblemAction()
	{
		System.out.println("AppMontior - Executing SequenceProblemAction..."+AppManager.getSequenceProblemAction().toString());
		
		switch(AppManager.getSequenceProblemAction())
		{
			case RESTART:
				AppManager.getInstance(_multicastGroupName).reStartApplication();
				break;
			case SHUTDOWN:
			default:
				System.exit(2);
				break;
		}
	}
	
	/**
	 * do network problem action
	 * If there are any subscribers, then notify the event
	 * Subscribers are expected to bring up a dialog and prepare to shutdown
	 */
	protected void doNetworkProblemAction()
	{
		String msg="### AppMontior - Executing NetworkProblemAction.";
		logger.error(msg);
		System.out.println(msg);
		notifySubscribers(new NetworkInactivity());
	}

	/**
	 * Thread's run method
	 */
	public void run()
	{
		System.out.println("AppMontior Starting...");

		while(keepRunning)
		{
			try
			{
	    		synchronized(mutex)
	    		{
	        		logger.info("AppMonitor : "+System.currentTimeMillis());
	        		mutex.wait();
	    		}
	    		
        		examineApplication();
			}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		keepRunning=false;
	    		logger.error(e.toString());
	    	}
			finally
			{
			}
		}
		
		System.out.println("AppMontior Exiting...");
	}
	
	public void addSubscriber(ApplicationEventSubscriber subscriber)
	{
		subscribers.add(subscriber);
	}
	
	public void removeSubscriber(ApplicationEventSubscriber subscriber)
	{
		subscribers.remove(subscriber);
	}
	
    /**
     * notify subscribers
     * @param message
     */
    private void notifySubscribers(ApplicationEvent event)
    {
        for(int index=0;index<subscribers.size();index++)
        {
        	ApplicationEventSubscriber subscriber=subscribers.get(index);

            try
            {
                subscriber.notifyEvent(event);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.warn("ApplicationEvent Notification failed. Subscriber="+
                		subscriber.toString()+" Exception: "+e.toString());
            }
        }

        return;
    }

}

