package com.theice.mdf.client.app;

import java.util.List;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.MDFClientConfiguratorHelper;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.domain.MarketHandlerFactory;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.multicast.process.PriceLevelMarketHandlerFactory;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClient;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.client.process.context.MDFAppContext;
import com.theice.mdf.client.process.context.MDFCommandLineContext;
import com.theice.mdf.client.process.context.MDFCommandLineProcessor;
import com.theice.mdf.client.util.MailThrottler;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The main command line client that initializes the frame and launches application threads.
 * 
 * System Parameters
 * 
 * -Denvironment (apitest, perftest etc.)
 * -DmulticastGroup (The actual group name as defined in the xml file)
 * -Dcontext (fullOrderDepth, priceLevel)
 * 
 * @author Adam Athimuthu
 */
public class MDFMulticastCommandLineClient
{
   private static Logger logger=Logger.getLogger(MDFMulticastCommandLineClient.class.getName());
   List<String> _interestedMulticastGroupNames=null;
   protected MDFAppContext context=null;

   public MDFMulticastCommandLineClient(List<String> groupNames)
   {
      this._interestedMulticastGroupNames = groupNames;
      try
      {
         this.context=MDFCommandLineContext.getInstance();
         for (String groupName:_interestedMulticastGroupNames)
         {
            AppManager appManager = new AppManager(groupName);
            AppManager.addToInstancesMap(appManager);
            appManager.initialize(context);
         }
      }
      catch(InitializationException e)
      {
         System.err.println("Application Initialization Failed : "+e.getMessage());
         System.exit(1);
      }
   }

   /**
    * Launch the application
    * @return the TCP client thread
    */
   public Thread launch()
   {
      Thread routerClientThread=null;

      /**
       * Start the socket client with the necessary message handler factory
       */
      for (String groupName : _interestedMulticastGroupNames)
      {
         MulticastChannelContext multicastChannelContext=AppManager.getMulticastChannelContext();
         MarketHandlerFactoryInterface messageHandlerFactory=null;

         context.setApplicationName("PriceFeed Multicast Client - ["+multicastChannelContext.toString()+"]");

         switch(multicastChannelContext)
         {
         case FULLORDERDEPTH:
            messageHandlerFactory=MarketHandlerFactory.getInstance();
            break;
         case PRICELEVEL:
            messageHandlerFactory=PriceLevelMarketHandlerFactory.getInstance();
            break;
         default:
            System.err.println("Message Factory has to be initialized first");
            System.exit(1);
            break;
         }

         AppManager.getInstance(groupName).setMessageHandlerFactory(messageHandlerFactory);
      }

      try
      {
         routerClientThread=AppManager.startRouterClient(new MDFClient());
      }
      catch(InitializationException e)
      {
         System.err.println(e.getMessage());
         System.exit(1);
      }

      /**
       * Start the multicast clients
       */
      try
      {
         for (String groupName: _interestedMulticastGroupNames)
         {
            AppManager.getInstance(groupName).startMulticastClients();
         }
      }
      catch(InitializationException e)
      {
         logger.error(e.getMessage());
         System.exit(1);
      }

      //AppManager.getInstance().startInactivityTimer();

      return(routerClientThread);

   }

   /**
    * Multicast Command Line Client
    * @param args
    */
   public static void main(String[] args)
   {
      Thread routerClientThread=null;

      logger.info("Multicast Command Client Starting...");
      try
      {
         MDFClientConfiguratorHelper.loadConfiguration();
      }
      catch(Exception ex)
      {
         logger.error("Exception while loading client configuration: "+ex, ex);
         System.out.println("Exit client");
         System.exit(1);
      }
      
      boolean clientConfigSelected = false;
      try
      {
         clientConfigSelected = MDFClientConfiguratorHelper.chooseConfiguration();
      }
      catch(Exception ex)
      {
         System.out.println("Error selecting client config: "+ex);
         ex.printStackTrace();
      } 
  
      if (clientConfigSelected==false)
      {
         logger.fatal("Client config not selected, exit client");
         System.exit(2);
      }
      
      MDFClientConfiguration mdfClientConfig=MDFClientConfigurator.getInstance().getCurrentConfiguration();
      if(mdfClientConfig!=null)
      {
         System.out.println("### Configuration Selected : "+mdfClientConfig.toString());
      }
      else
      {
         System.err.println("Configuration not selected.");
         System.exit(2);
      }
      
      List<String> interestedMCGroupName = mdfClientConfig.getInterestedMulticastGroupNames();
      MarketsHolder.init(interestedMCGroupName);
      MDFMulticastCommandLineClient client=new MDFMulticastCommandLineClient(interestedMCGroupName);
      routerClientThread=client.launch();

      /**
       * Initialize the command line processor and event consumer
       */
      MDFCommandLineProcessor.getInstance().initialize();
      System.out.println("### Client Initialization Completed ###");

      try
      {
         MDFCommandLineEventConsumer.getInstance().initialize();

      }
      catch(InitializationException e)
      {
         System.err.println(e.toString());
         System.exit(2);
      }

      for (String groupName:interestedMCGroupName)
      {
         MDFCommandLineEventConsumer.getInstance().initApplicationComponents(groupName);
      }

      try
      {
         routerClientThread.join();
      }
      catch(Throwable t)
      {
         System.out.println("Client thread exiting...");
      }

      /*
       * When routerClientThread dies, there are two possibilities:
       * 1. TCP connection was successfully established and then closed after use, it is considered "connected".
       * 2. TCP connection fails and autoReconnectTCP is set to false. The client will not retry and therefore should be stopped.
       *    If autoReconnectTCP is set to true, routerClientThread will keep on trying and will never reach here.
       */
      if (!AppManager.getAppContext().isConnected())
      {
         //MDFCommandLineProcessor.getInstance().shutdown();
         //AppManager.getInstance().stopInactivityTimer();
         //AppManager.getInstance().shutdownApplicationComponents();
         System.out.println("TCP connection failed. Application exiting...");
         System.exit(1);
      }
      else
      {
         //OK to log it as an Error to trigger an email alert.
         StringBuilder msg = new StringBuilder("TCP logged in successfully. Multicast Commandline Client started."+System.getProperty("line.separator"));
         msg.append("Multicast Group(s) Joined: "+interestedMCGroupName);
         
         MailThrottler.getInstance().enqueueError(msg.toString());
      }

      System.out.println("MDF Client main thread exiting...");

      return;
   }
}

