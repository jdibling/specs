package com.theice.mdf.client.app;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.event.ApplicationEvent;
import com.theice.mdf.client.domain.event.ApplicationEventSubscriber;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.AppMonitor;
import com.theice.mdf.client.process.context.AppMode;
import com.theice.mdf.client.process.context.MDFAppContext;
import com.theice.mdf.client.process.context.MDFCommandLineContext;
import com.theice.mdf.client.util.MailThrottler;

/**
 * Equivalent of the MDF Client Main Frame (GUI)
 * 
 * Listens to application events to trap network inactivity and error conditions
 * Signals to the application with a ready latch
 * 
 * @author Adam Athimuthu
 */
public class MDFCommandLineEventConsumer implements ApplicationEventSubscriber
{
   private static MDFCommandLineEventConsumer instance=new MDFCommandLineEventConsumer();

   private static final Logger logger=Logger.getLogger(MDFCommandLineEventConsumer.class.getName());

   /**
    * Client Context
    */
   private MDFCommandLineContext clientContext=null;

   private MDFCommandLineEventConsumer()
   {
   }

   public static MDFCommandLineEventConsumer getInstance() 
   {
      return(instance);
   }


   public void initialize() throws InitializationException
   {
      MDFAppContext appContext=AppManager.getAppContext();

      if(appContext==null)
      {
         throw(new InitializationException("AppContext is null while initializing the Command Event Consumer"));
      }

      if(appContext.getAppMode()!=AppMode.CommandLine)
      {
         throw(new InitializationException("Context should be CommandLine when the application is running in command mode."));
      }

      clientContext=(MDFCommandLineContext) appContext; 

      return;
   }

   /**
    * Delayed initialization to take care of wiring to the application components
    * such as AppMonitor and event notifications
    */
   public void initApplicationComponents(String groupName)
   {
      logger.info("MDFCommandEventConsumer is subscribing to AppMonitor for application events");
      AppMonitor monitor=AppManager.getInstance(groupName).getAppMonitor();
      monitor.addSubscriber(this);

      System.out.println("### Activating the Ready to Start Latch ###");
      AppManager.activateConsumersReadyLatch(); //this might be called multiple times if there are multiple groups, but that's ok.

      return;
   }

   /**
    * Handle application events
    */
   public void notifyEvent(ApplicationEvent event)
   {
      String msg="";

      logger.info("MDFCommandEventConsumer received application event : "+event.toString());

      switch(event.getStatus())
      {
      //case NETWORKINACTIVITY:
      case NETWORKERROR:
         msg="Application is shutting down due to "+event.getStatus().toString();
         System.err.println(msg);
         MailThrottler.getInstance().enqueueError(msg);
         try
         {
            Thread.sleep(30000);
         }
         catch(Exception ex)
         {}
         System.exit(1);
         break;
      default:
         break;
      }
      return;
   }
}
