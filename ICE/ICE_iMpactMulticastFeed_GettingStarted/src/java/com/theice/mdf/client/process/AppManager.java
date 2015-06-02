package com.theice.mdf.client.process;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.theice.logging.core.AlarmLogger;
import com.theice.logging.core.ManagedObjectStatusLogger;
import com.theice.logging.core.domain.Alarm;
import com.theice.logging.core.domain.AppStatus;
import com.theice.logging.core.domain.BasicAlarm;
import com.theice.logging.core.domain.BasicAppStatus;
import com.theice.logging.core.domain.ManagedObject;
import com.theice.logging.domain.Severity;
import com.theice.mdf.client.ClientState;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.config.domain.MDFClientRuntimeParameters;
import com.theice.mdf.client.domain.MDRawMessageBuffer;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.domain.state.ApplicationStatus;
import com.theice.mdf.client.domain.state.SequenceProblemAction;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.multicast.MDFMulticastClient;
import com.theice.mdf.client.multicast.factory.FullOrderDepthMulticastClientFactory;
import com.theice.mdf.client.multicast.factory.MulticastClientFactory;
import com.theice.mdf.client.multicast.factory.PriceLevelMulticastClientFactory;
import com.theice.mdf.client.multicast.handler.MarketStateManager;
import com.theice.mdf.client.process.context.MDFAppContext;
import com.theice.mdf.client.util.MailThrottler;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * <p/>
 *
 * Manages the application resources
 * Initializes loggers
 * Initialize the proper multicast client factory depending on the multicast channel context
 * 
 * @author Adam Athimuthu
 * Date: Aug 8, 2007
 * Time: 11:48:08 AM
 */
public class AppManager
{
   private static Map<String, AppManager> instancesMap=new Hashtable<String,AppManager>();
   private static final Logger logger=Logger.getLogger(AppManager.class.getName());
   public static final byte CLEARED = 0;
   public static final byte SOCKET_TIMEOUT_ERROR_CODE = 1;
   public static final byte SEQUENCE_GAP_ERROR_CODE = 2;
   public static final byte SOCKET_IOEXCEPTION_CODE = 3;
   public static final byte TCP_CONNECTION_ERROR_CODE = 4;

   //multicast group this AppManager belongs to
   private final String _multicastGroupName;

   /**
    * MDF App Context (can be a GUI context or a Command Line Context)
    */
   private static MDFAppContext appContext=null;

   /**
    * Mail Throttler
    */
   private static MailThrottler mailThrottler=MailThrottler.getInstance();

   /**
    * Application's multicast channel context
    * By default, the application is initialized with a Full Order Depth context
    * Currently, FOD and PL cannot be supported at the same time.
    */
   private static MulticastChannelContext multicastChannelContext=null;

   /**
    * Factory that has the handlers for processing messages
    * Initialized based on the book context
    * Channel: multicast
    */
   protected MarketHandlerFactoryInterface _messageHandlerFactory=null;

   /**
    * Factory that has the handlers for processing core messages and uses a bypass handler
    * for handling the historical messages
    * Channel: TCP/IP socket
    */
   protected static MarketHandlerFactoryInterface coreMessageHandlerFactory=CoreMessageHandlerFactory.getInstance();

   /**
    * Out of sequence action
    */
   protected static SequenceProblemAction sequenceProblemAction=SequenceProblemAction.SHUTDOWN;

   /**
    * Multicast Inactivity Threshold
    */
   protected static int multicastInactivityThreshold=0;

   /**
    * TCP/IP Socket Client
    */
   protected static MDFClientInterface mdfClient=null;

   /**
    * Multicast channel thread pair for the given context
    */
   private Thread incrementalMulticastThread=null;
   private Thread snapshotMulticastThread=null;

   /**
    * Current session number
    */
   private Integer _session=-1;

   /**
    * Application's internal status and static data load status
    */
   protected ApplicationStatus applicationStatus=ApplicationStatus.NORMAL;
   //protected static boolean marketDataLoadCompleted=false;

   /**
    * Consumers (UI and models) use this latch as a signal for the application components that they are ready
    * to process messages
    * Consumer: UI, Producer: Application (network components)
    */
   protected static CountDownLatch consumersReadyLatch=new CountDownLatch(1);

   private static Timer timer=new Timer();
   private static final long maxInactivityInterval=30*1000; 
   protected static boolean autoReconnectTCP=false;
   private boolean _mdfClientNeeded=true; //for snapshot logger, mdfClient is not needed because static data will come from a file
   private short counter=0;
      
   /**
    * Simulated mode
    */
   private boolean simulate=false;
   protected AppMonitor _monitor=null;
   
   public static AppManager getInstance(String groupName)
   {
      return(instancesMap.get(groupName));
   }

   public static void addToInstancesMap(AppManager appManager)
   {
      instancesMap.put(appManager._multicastGroupName, appManager);
   }

   public AppManager(String multicastGroupName)
   {
      this._multicastGroupName = multicastGroupName;
      this._monitor = new AppMonitor(multicastGroupName);
   }
       
   /**
    * Initialize the application manager
    * 
    * Initialize the book context. The context determines the type of book inside the market
    * 		and triggers the type of multicaster factory that we are looking for
    * 
    * Java logger init - uses the java.util.logging.config.file system property to find the mdflogging.properties file
    * 
    * @throws InitializationException
    */
   public void initialize(MDFAppContext applicationContext) throws InitializationException
   {
      System.out.println("### Application Manager Initializing...");

      if (AppManager.appContext == null)
      {
         AppManager.appContext=applicationContext;
      }

      System.out.println("### Application Mode="+appContext.getAppMode().toString());

      /**
       * Crossed Book Monitor initialization
       */
      CrossedBookMonitor.getInstance().initialize();

      /**
       * Initialize the context
       */
      initContext();

      /**
       * Start the application monitor
       * - interested components may subscribe to the appmonitor
       */
      System.out.println("### Starting the AppMonitor thread...");
      new Thread(_monitor,"AppMonitor-"+_multicastGroupName).start();

      return;
   }

   /**
    * init context
    * @throws InitializationException
    */
   private void initContext() throws InitializationException
   {
      System.out.println("Initializing the application specific contexts...");

      try
      {
         MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();

         if(configuration==null)
         {
            String message="No configuration selected.";
            logger.error(message);
            throw(new InitializationException(message));
         }

         List<MarketType> interestedMarketTypes=configuration.getInterestedMarketTypes(this._multicastGroupName);

         if(interestedMarketTypes==null || interestedMarketTypes.size()==0)
         {
            String message="At least one market type should be selected.";
            logger.error(message);
            throw(new InitializationException(message));
         }

         MDFClientRuntimeParameters parameters=configuration.getMDFClientRuntimeParameters();

         if(parameters==null)
         {
            String message="Runtime parameters object is null.";
            logger.error(message);
            throw(new InitializationException(message));
         }

         sequenceProblemAction=parameters.getSequenceProblemAction();
         multicastInactivityThreshold=parameters.getMulticastInactivityThreshold();
         autoReconnectTCP=parameters.isAutoReconnectTCP();
         _session=-1;
         
         if (multicastChannelContext==null)
         {
            multicastChannelContext=configuration.getMulticastChannelPairInfo(this._multicastGroupName).getMulticastChannelContext();
         }
        

         System.out.println("### Is autoReconnectTCP enabled? "+autoReconnectTCP);
      }
      catch(Throwable t)
      {
         String message="Error getting the Book Context : "+t.getMessage();
         System.out.println(message);
         t.printStackTrace();
         throw(new InitializationException(message));
      }

      return;
   }

   /**
    * Shutdown application threads
    */
   public void shutdownApplicationComponents()
   {
      logger.info("Shutting down the application components/threads.");

      CrossedBookMonitor.getInstance().shutdown();

      return;
   }
   /**
    * set market handler factory
    * @param MessageHandlerFactory
    */
   public void setMessageHandlerFactory(MarketHandlerFactoryInterface messageHandlerFactory)
   {
      _messageHandlerFactory=messageHandlerFactory;
   }

   /**
    * create the socket client
    * initialize the socket client with a proper message handler factory for this book context
    */
   public static Thread startRouterClient(MDFClientInterface client) throws InitializationException
   {
      Thread routerClientThread=null;

      try
      {
         mdfClient=client;

         if(client==null)
         {
            String msg="Socket client is null";
            System.err.println(msg);
            throw(new InitializationException(msg));
         }

         if(client.getFactory()==null)
         {
            String msg="Message Handler Factory can't be null";
            System.err.println(msg);
            throw(new InitializationException(msg));
         }

         routerClientThread=new Thread((Runnable) client, "TCPConnectionThread");
         routerClientThread.start();
      }
      catch(Exception e)
      {
         e.printStackTrace();
         logger.error("Error starting the Market Router Client : "+e.toString());
      }

      return(routerClientThread);
   }

   public void startMulticastClients(boolean mdfClientNeeded) throws InitializationException
   {
      String clientName="";
      MulticastClientFactory multicastFactory=null;
      counter++;

      if(mdfClientNeeded && mdfClient==null)
      {
         String msg="Socket client has to be initialized first.";
         logger.error(msg);
         throw(new InitializationException(msg));
      }

      if(_messageHandlerFactory==null)
      {
         String msg="Message Handler Factory is null.";
         logger.error(msg);
         throw(new InitializationException(msg));
      }

      logger.info("Starting Multicast Clients: Incremental/Snapshot for BookContext : "+multicastChannelContext);

      /**
       * Create the MulticastClient Factory for this book context
       */
      switch(multicastChannelContext)
      {
      case FULLORDERDEPTH:
         multicastFactory=FullOrderDepthMulticastClientFactory.getInstance();
         break;
      case PRICELEVEL:
         multicastFactory=PriceLevelMulticastClientFactory.getInstance();
         break;
      default:
         String msg="No multicast factory associated with this multicast channel context.";
         logger.error(msg);
         throw(new InitializationException(msg));
      }

      logger.info("MulticastFactory : "+multicastFactory.toString());

      try
      {
         MDFMulticastClient snapshotMulticastClient=multicastFactory.createSnapshotMulticastClient(this._multicastGroupName);
         clientName=snapshotMulticastClient.getConfig().getName()+"-"+this._multicastGroupName+"-"+counter;
         snapshotMulticastThread=new Thread((Runnable) snapshotMulticastClient,clientName);
         snapshotMulticastThread.start();

         MDFMulticastClient multicastClient=multicastFactory.createIncrementalMulticastClient(this._multicastGroupName);
         clientName=multicastClient.getConfig().getName()+"-"+this._multicastGroupName+"-"+counter;
         incrementalMulticastThread=new Thread((Runnable) multicastClient,clientName);
         incrementalMulticastThread.start();
      }
      catch(InitializationException e)
      {
         logger.error(e.getMessage());
         throw(e);
      }

      return;
   }
   /**
    * Creates the incremental and snapshot multicast clients using the appropriate factory
    * The factory is chosen based on the book context
    * 
    * starts the multicast client pair for each channel (snapshot/incremental)
    * 
    * Possible factories:
    * 
    *	FullOrderDepthMulticastClientFactory
    * 	PriceLevelMulticastClientFactory
    * 	OptionOrderMulticastClientFactory
    * 
    * @throws InitializationException
    */
   public void startMulticastClients() throws InitializationException
   {
      this.startMulticastClients(true);
   }

   /**
    * Restart multicast clients
    * 
    * Leave the socket as-is
    * If the client threads are still running and alive, send an interrupt and then restart
    * Initialize the internal data structures
    * Set the status of market sequencers to NOTREADY so we can do the synchronization again
    * 
    * @throws InitializationException
    */
   public void reStartApplication()
   {
      System.out.println("Restarting the application components");

      if(snapshotMulticastThread!=null && snapshotMulticastThread.isAlive())
      {
         snapshotMulticastThread.interrupt();
      }
      else
      {
         System.out.println("Snapshot Multicast thread not alive.");
      }

      if(incrementalMulticastThread!=null && incrementalMulticastThread.isAlive())
      {
         incrementalMulticastThread.interrupt();
      }
      else
      {
         System.out.println("Incremental Multicast thread not alive.");
      }

      System.out.println("*** Initializing the market state manager");

      MarketStateManager.getInstance(this._multicastGroupName).initialize();

      System.out.println("*** Initializing all markets");

      MarketsHolder.getInstance().initialize(_multicastGroupName);

      System.out.println("*** Restarting the multicast clients");

      try
      {
         startMulticastClients(_mdfClientNeeded);
      }
      catch(InitializationException e)
      {
         System.err.println("Exception while restarting the application. Exiting... : "+e.getMessage());
         System.exit(2);
      }

      System.out.println("Finished Restarting the application components");

      return;
   }


   /**
    * Logout the router client
    */
   public void stopRouterClient()
   {
      logger.info("Stopping the router client.");
   }

   public static MDFClientInterface getClient()
   {
      return(mdfClient);
   }

   public static MulticastChannelContext getMulticastChannelContext()
   {
      return(multicastChannelContext);
   }

   public MarketHandlerFactoryInterface getMessageHandlerFactory()
   {
      return(_messageHandlerFactory);
   }

   public static MarketHandlerFactoryInterface getCoreMessageHandlerFactory()
   {
      return(coreMessageHandlerFactory);
   }

   public ApplicationStatus getApplicationStatus()
   {
      return(this.applicationStatus);
   }

   public void setApplicationStatus(ApplicationStatus status)
   {
      this.applicationStatus=status;
   }

   //public static boolean isMarketDataLoadCompleted()
   //{
   //   return(marketDataLoadCompleted);
   //}

   /**
    * Mark market static data load completion
    * This milestone is very important in the sense that many threads depend on this for making
    * further progress...
    * 
    * Also, certain internal data structures/models need to be initialized by the context once we know
    * the exact number of markets are known
    */
   //public static void markMarketDataLoadCompletion()
   //{
   //   marketDataLoadCompleted=true;
   //}

   public boolean isSimulatedMode()
   {
      return(simulate);
   }

   public static SequenceProblemAction getSequenceProblemAction()
   {
      return(sequenceProblemAction);
   }

   public void setSimulatedMode(boolean flag)
   {
      simulate=flag;
   }

   public AppMonitor getAppMonitor()
   {
      return(_monitor);
   }

   public static int getMulticastInactivityThreshold()
   {
      return(multicastInactivityThreshold);
   }

   public int getSession()
   {
      return(_session.intValue());
   }

   public static CountDownLatch getConsumersReadyLatch()
   {
      return(consumersReadyLatch);
   }

   public static void activateConsumersReadyLatch()
   {
      consumersReadyLatch.countDown();
   }

   public static MDFAppContext getAppContext()
   {
      return(appContext);
   }

   public static boolean isAutoReconnectTCP()
   {
      return autoReconnectTCP;
   }

   public static void setAutoReconnectTCP(boolean reconnect)
   {
      autoReconnectTCP = reconnect;
   }

   public void setMDFClientNeeded(boolean mdfClientNeeded)
   {
      this._mdfClientNeeded = mdfClientNeeded;
   }

   public static void startInactivityTimer()
   {
      System.out.println("### Starting the inactivity timer.");

      TimerTask task=new TimerTask()
      {
         public void run()
         {
            long now=System.currentTimeMillis();
            long lastMessageTimeStamp=MDRawMessageBuffer.getInstance().getLastMessageTimeStamp();
            long elapsed=(now-lastMessageTimeStamp);

            if(elapsed>=maxInactivityInterval)
            {
               String message="No messages received for "+elapsed/1000+" seconds. Shutting down the client.";
               appContext.alert(message);
               System.exit(1);
            }
         }
      };

      timer.scheduleAtFixedRate(task, maxInactivityInterval, maxInactivityInterval);

      return;
   }

   public static void stopInactivityTimer()
   {
      if(timer!=null)
      {
         timer.cancel();
         timer=new Timer();
      }
   }

   /**
    * Mark session if not already done
    * @param session
    */
   public void markSession(int session)
   {
      StringBuffer buf=new StringBuffer();

      synchronized(_session)
      {
         if(_session==-1)
         {
            System.out.println("AppManager: Mark session : "+session);
            _session=Integer.valueOf(session);
         }
         else
         {
            if(_session!=session)
            {
               buf.append("Attempt to mark a session that is different from current. ");
               buf.append("Current Session : ").append(this._session);
               buf.append(" Attempted : ").append(session);
               System.err.println(buf.toString());
            }
            else
            {
               System.out.println("Session already set to : "+_session);
            }
         }
      }
   }

   /**
    * write alert messages on the stderr and through SMTP
    * @param message
    * @param level
    */
   public static void writeAlert(Level level,String message)
   {
      StringBuffer buffer=new StringBuffer();
      buffer.append("[").append(level.toString()).append("] ");
      buffer.append("[").append(Thread.currentThread().getName()).append("] ").append(message);
      System.err.println(buffer.toString());
      mailThrottler.enqueueError(buffer.toString());
      return;
   }
   
   public static void sendAlert(String alarmUniqueID, String multicastGroupName, String message, byte typeOfError, ClientState clientState)
   {
      if (clientState==null)
      {
         return;
      }
      
      Severity severity = null;
      switch (typeOfError)
      {
         case SOCKET_TIMEOUT_ERROR_CODE:
            severity = Severity.Major;
            break;
         case SEQUENCE_GAP_ERROR_CODE:
            severity = Severity.Major;
            break;
         case SOCKET_IOEXCEPTION_CODE:
            severity = Severity.Critical;
            break;
         case TCP_CONNECTION_ERROR_CODE:
            severity = Severity.Critical;
            break;
         case CLEARED:
            severity = Severity.Clear;
            break;
         default:
            severity = Severity.Info;
      }
      
      Alarm alarm=new BasicAlarm(alarmUniqueID, multicastGroupName, severity, clientState, message);
      AlarmLogger.getInstance().log(alarm);
   }
   
   public static void sendComponentStatus(ManagedObject clientState)
   {
      ManagedObjectStatusLogger.getInstance().log(new BasicAppStatus(clientState));
   }

}

