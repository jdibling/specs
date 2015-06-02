package com.theice.mdf.client.process;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.theice.logging.domain.ComponentStatus;
import com.theice.mdf.client.ClientState;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.config.domain.MulticastGroupDefinition;
import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.multicast.handler.MarketLoadManager;
import com.theice.mdf.client.process.context.MDFAppContext;
import com.theice.mdf.client.util.MailThrottler;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.request.DebugRequest;
import com.theice.mdf.message.request.LoginRequest;
import com.theice.mdf.message.request.LogoutRequest;
import com.theice.mdf.message.request.ProductDefinitionRequest;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractMDFClient implements MDFClientInterface
{
   private static Logger logger=Logger.getLogger(AbstractMDFClient.class.getName());
   private static Logger secondaryLogger=Logger.getLogger("com.theice.mdf.client.secondaryLogger");
   private static final long MILLIS_BEFORE_TCP_CONNECTION_FAILURE_ALERT = 1800000; //milliseconds 
   private static final String TCP_CONNECTION_STATUS = "TCPConnection";

   private int requestSeqID=0;

   protected Socket clientSoc=null;

   protected MarketHandlerFactoryInterface _factory=null;

   private MDFAppContext _context=null;

   private boolean _autoRetry=false;

   public AbstractMDFClient()
   {
      _context=AppManager.getAppContext();
      _autoRetry=AppManager.isAutoReconnectTCP();
   }

   /**
    * Connect to the feed server
    */
   public boolean connect()
   {
      boolean isConnected=false;
      String message="";

      try
      {
         MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();

         EndPointInfo tcpEndPoint=configuration.getTcpInfo().getEndPointInfo();

         // Connect to the feed server
         message="MDFClient: Connecting to feed server - " +tcpEndPoint.getDisplayable();
         logger.info(message);
         _context.logEssential(message);

         clientSoc=new Socket(tcpEndPoint.getIpAddress(),tcpEndPoint.getPort());
         clientSoc.setTcpNoDelay(true);

         message="MDFClient: Connected to server";
         logger.info(message);
         _context.logEssential(message);

         isConnected=true;
      }
      catch (IOException e)
      {
         message="MDFClient: IOException caught : "+e.toString();
         logger.error(message);
         _context.logEssential(message);
         e.printStackTrace();
      }
      catch(Exception e)
      {
         message="MDFClient: Exception caught : "+e.toString();
         logger.error(message);
         _context.logEssential(message);
         e.printStackTrace();
      }

      _context.setConnected(isConnected);

      return(isConnected);
   }

   /**
    * process
    */
   public void process()
   {
      String logMessage="";

      logger.info("### TCP/IP Socket Thread waiting for start signal ###");

      try
      {
         AppManager.getConsumersReadyLatch().await();
      }
      catch(InterruptedException e)
      {
      }

      short productDefDownloadAttempts=0;

      do
      {   
         logger.info("### TCP/IP Socket Thread Ready to Start ###");

         try
         {
            if (productDefDownloadAttempts++ > 0)
            {
               String errMsg = "ProductDefinitions download failed or not completed, will try again in 45 seconds.";
               logger.info(errMsg);
               MailThrottler.getInstance().enqueueError(errMsg);
               try
               {
                  clientSoc.close();
               }
               catch(IOException e)
               {
                  logger.info("Error when closing socket: "+e, e);
               }
               MarketsHolder.getInstance().clearAllMarkets();
               MarketLoadManager.getInstance().resetMarketLoadStatus();
               Thread.sleep(45000);
            }

            openTcpConnection_KeepTryingIfNeeded();

            InputStream inStream = clientSoc.getInputStream();

            // Start socket reader thread for processing response/streamed data from server
            MDFClientSocketReader reader = new MDFClientSocketReader(new DataInputStream(inStream));
            Thread readerThread = new Thread(reader, "ReaderThread");
            readerThread.start();

            // Start message consumer thread for processing messages
            Thread consumerThread = new Thread(createMessageConsumer(reader), "ConsumerThread");
            consumerThread.start();

            OutputStream outStream = clientSoc.getOutputStream();

            // send debug request
            logMessage="MDFClient: send debug request";
            logger.info(logMessage);
            _context.logEssential(logMessage);
            sendDebugRequest(outStream);

            // send login request
            logMessage="MDFClient: send login request";
            logger.info(logMessage);
            _context.logEssential(logMessage);
            login(outStream);

            // send product definition requests
            logMessage="MDFClient: send product definition requests";
            logger.info(logMessage);
            _context.logEssential(logMessage);
            requestProductDefintions(outStream);
            //wait for reader and consumer threads
            //consumerThread, upon receiving product definitions, will updateLoadStatus from MarketLoadManager
            //if all product definitions are loaded, it will then close the socket connection
            readerThread.join();
            consumerThread.join(); 
         }
         catch(IOException e)
         {
            logMessage="MDFClient: IOException caught : "+e.toString();
            _context.logEssential(logMessage);
            logger.error(logMessage);
            e.printStackTrace();
         }
         catch(InterruptedException e)
         {
            logMessage="MDFClient: InterruptedException caught : "+e.toString();
            logger.error(logMessage);
            _context.logEssential(logMessage);
            e.printStackTrace();
         }
      } 
      while(MarketLoadManager.getInstance().getLoadingStatus() != MarketLoadManager.LOAD_COMPLETED);

      logMessage="MDFClient: Exiting...";
      logger.error(logMessage);
      _context.logEssential(logMessage);

   }

   private void openTcpConnection_KeepTryingIfNeeded()
   {
      boolean hasRaisedTcpConnectionError = false;
      long sleepTimeIncrementInMillis = 5*60*1000;
      long sleepTimeInMillis = 30000;
      int extendedConnectionFailedAttempts=0;
      long tcpConnectionWarningThreshold = MILLIS_BEFORE_TCP_CONNECTION_FAILURE_ALERT;
      String tcpConnectionWarningThresholdProperty = System.getProperty("tcpConnectionWarningThreshold");
      if (tcpConnectionWarningThresholdProperty!=null && tcpConnectionWarningThresholdProperty.length()>0)
      {
         try
         {
            tcpConnectionWarningThreshold = Long.parseLong(tcpConnectionWarningThresholdProperty);
         }
         catch(Exception ex)
         {
            logger.error("Error getting tcp connection warning threshold, use default. "+ex, ex);
         }
      }
      MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
      EndPointInfo tcpEndPoint=configuration.getTcpInfo().getEndPointInfo();
      ClientState clientState = new ClientState("MCFeedClient", "MCFeedClient", "MCFeedClient", tcpEndPoint.getIpAddress()+":"+tcpEndPoint.getPort());
      clientState.setComponentStatus(ComponentStatus.UP);
      
      long startTime = System.currentTimeMillis();
      
      do
      {
         if (connect()==false)
         {
            String logMessage="Failed to connect to the feed server.";
            logger.error(logMessage);
            _context.logEssential(logMessage);
            _context.alert(logMessage);

            if (!_autoRetry)
            {
               return;
            }
            else
            {
               if (System.currentTimeMillis() - startTime >= tcpConnectionWarningThreshold)
               {
                  //TCP login has been failing for over 30 mins, raise an alert
                  String errorMsg = "Failing to connect to the feed server for extended period of time. Please check connection. Client will keep trying.";
                  MailThrottler.getInstance().enqueueError(errorMsg);
                  secondaryLogger.error(errorMsg);
                  if (!hasRaisedTcpConnectionError)
                  {
                     AppManager.sendAlert(TCP_CONNECTION_STATUS, "MulticastCient", errorMsg, AppManager.TCP_CONNECTION_ERROR_CODE, clientState);
                     hasRaisedTcpConnectionError = true;
                  }
                  //most likely this is an extended outage, let's sleep longer
                  sleepTimeInMillis = 10*60*1000 + extendedConnectionFailedAttempts++*sleepTimeIncrementInMillis;
               }
               try
               {
                  logger.info("Sleep before next TCP connection attempt, sleep time in millis: "+sleepTimeInMillis);
                  Thread.sleep(sleepTimeInMillis);
               }
               catch(Exception ex)
               {
                  logger.error("Exception happened when sleeping:"+ex);
               }
            }
         }
      }
      while(_autoRetry && _context.isConnected()==false);

      if (hasRaisedTcpConnectionError)
      {
         //clear the error
         AppManager.sendAlert(TCP_CONNECTION_STATUS, "MulticastClient", "Cleared", AppManager.CLEARED, clientState);
      }
   }
   
   /**
    * Send a debug request
    * @param outStream
    * @throws java.io.IOException
    */
   private void sendDebugRequest(OutputStream outStream) throws IOException
   {
      DebugRequest debugRequest = new DebugRequest();
      debugRequest.RequestSeqID = getRequestSeqID();
      outStream.write(debugRequest.serialize());
   }

   /**
    * Send login request
    *
    * @param outStream
    * @throws java.io.IOException
    */
   protected void login(OutputStream outStream) throws IOException
   {
      LoginRequest loginRequest = createLoginRequest();
      outStream.write(loginRequest.serialize());
   }

   /**
    * Request for the product defintions of the market types that we are interested in
    * @param outStream
    * @param securityType (Futures or Options)
    * @throws java.io.IOException
    */
   protected void requestProductDefintions(OutputStream outStream) throws IOException
   {
      MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();

      List<String> interestedMulticastGroupNames = configuration.getInterestedMulticastGroupNames();
      Map<String, MulticastGroupDefinition> multicastGroupDefinitionMap = configuration.getMulticastGroupDefinitionMap();

      ArrayList<Short> allInterestedMarketTypeCodesForFutures = new ArrayList<Short>();
      ArrayList<Short> allInterestedMarketTypeCodesForOptions = new ArrayList<Short>();

      for(String multicastGroupName:interestedMulticastGroupNames)
      {
         MulticastGroupDefinition groupDefinition = multicastGroupDefinitionMap.get(multicastGroupName);
         short[] multicastTypeCodes = configuration.getInterestedMarketTypeCodes(multicastGroupName);
         for(int i=0; i<multicastTypeCodes.length; i++)
         {
            allInterestedMarketTypeCodesForFutures.add(multicastTypeCodes[i]);
            if (groupDefinition.isOptions())
            {
               allInterestedMarketTypeCodesForOptions.add(multicastTypeCodes[i]);
            }
         }
      }

      List<ProductDefinitionRequest> requests=new ArrayList<ProductDefinitionRequest>();

      for(Short marketTypeCode:allInterestedMarketTypeCodesForFutures)
      {
         ProductDefinitionRequest pdRequest = new ProductDefinitionRequest();
         pdRequest.MarketType=marketTypeCode;
         pdRequest.RequestSeqID=getRequestSeqID();
         pdRequest.SecurityType=ProductDefinitionRequest.SECURITY_TYPE_FUTRES_OTC;

         /**
          * Register with the load manager for tracking
          * If a market type already exists, just overlay so we don't send a duplicate request
          */
         if(MarketLoadManager.getInstance().registerLoadRequest(pdRequest))
         {
            requests.add(pdRequest);
         }
         else
         {
            logger.warn("Avoid duplicate Product Definition Request for : "+pdRequest.MarketType);
         }
      }

      for(Iterator<ProductDefinitionRequest> it=requests.iterator();it.hasNext();)
      {
         ProductDefinitionRequest request=it.next();
         outStream.write(request.serialize());
      }

      //request for options product definitions if needed
      requests = new ArrayList<ProductDefinitionRequest>();
      for(Short marketTypeCode:allInterestedMarketTypeCodesForOptions)
      {
         ProductDefinitionRequest pdRequest = new ProductDefinitionRequest();
         pdRequest.MarketType=marketTypeCode;
         pdRequest.RequestSeqID=getRequestSeqID();
         pdRequest.SecurityType=ProductDefinitionRequest.SECURITY_TYPE_OPTION;

         /**
          * Register with the load manager for tracking
          * If a market type already exists, just overlay so we don't send a duplicate request
          */
         if(MarketLoadManager.getInstance().registerLoadRequest(pdRequest))
         {
            requests.add(pdRequest);
         }
         else
         {
            logger.warn("Avoid duplicate Product Definition Request for : "+pdRequest.MarketType);
         }
      }

      for(Iterator<ProductDefinitionRequest> it=requests.iterator();it.hasNext();)
      {
         ProductDefinitionRequest request=it.next();
         outStream.write(request.serialize());
      }

      if (configuration.isInterestedInUDS())
      {
         //request for UDS market definitions if needed
         requests = new ArrayList<ProductDefinitionRequest>();
         for(Short marketTypeCode:allInterestedMarketTypeCodesForOptions)
         {
            ProductDefinitionRequest pdRequest = new ProductDefinitionRequest();
            pdRequest.MarketType=marketTypeCode;
            pdRequest.RequestSeqID=getRequestSeqID();
            pdRequest.SecurityType=ProductDefinitionRequest.SECURITY_TYPE_UDS_OPTIONS;

            /**
             * Register with the load manager for tracking
             * If a market type already exists, just overlay so we don't send a duplicate request
             */
            if(MarketLoadManager.getInstance().registerLoadRequest(pdRequest))
            {
               requests.add(pdRequest);
            }
            else
            {
               logger.warn("Avoid duplicate UDS Product Definition Request for : "+pdRequest.MarketType);
            }
         }

         for(Iterator<ProductDefinitionRequest> it=requests.iterator();it.hasNext();)
         {
            ProductDefinitionRequest request=it.next();
            outStream.write(request.serialize());
         }
      }

      return;
   }

   /**
    * Increment and return the request sequence ID
    * @return the request sequence ID
    */
   protected int getRequestSeqID()
   {
      return(requestSeqID++);
   }

   /**
    * Create Base Login Request
    * @return
    */
   protected LoginRequest createBaseLoginRequest()
   {
      LoginRequest loginRequest = new LoginRequest();

      MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();

      loginRequest.UserName
      = MessageUtil.toRawChars(configuration.getTcpInfo().getUserName(),loginRequest.UserName.length);
      loginRequest.Password
      = MessageUtil.toRawChars(configuration.getTcpInfo().getPassword(),loginRequest.Password.length);
      loginRequest.RequestSeqID=getRequestSeqID();

      if (configuration.getMDFClientRuntimeParameters().isGetStripInfo())
      {
         loginRequest.GetStripInfoMessages = 'Y';
      }

      //ReservedField1 is currently not used. Reserved for future use.
      if (System.getProperty("ReservedField1")!=null)
      {
         try
         {
            short reservedField1 = Short.parseShort(System.getProperty("ReservedField1"));
            loginRequest.ReservedField1 = reservedField1;
         }
         catch(Exception ex)
         {
         }
      }

      return(loginRequest);
   }

   /**
    * Return the factory associated with this client
    * @return factory
    */
   public MarketHandlerFactoryInterface getFactory()
   {
      return(_factory);
   }

   protected void requestLogout(OutputStream outStream) throws IOException
   {
      LogoutRequest logoutReq = new LogoutRequest();
      logoutReq.RequestSeqID = getRequestSeqID();
      outStream.write(logoutReq.serialize());
   }

   public void logoutAndCloseSocket() 
   {
      try
      {
         logger.info("*** TCP Socket is no longer needed. Log out and close it...");
         logger.info("*** Sending logout request...");
         requestLogout(clientSoc.getOutputStream());
         logger.info("*** Closing socket...");
         clientSoc.close();
         AppManager.stopInactivityTimer();
      } 
      catch(Exception ex)
      {
         String errMsg="logoutAndCloseSocket(): Exception caught : "+ex.toString();
         logger.error(errMsg, ex);
      }
   }

   /**
    * Template Methods
    */
   protected abstract LoginRequest createLoginRequest();

   protected abstract Runnable createMessageConsumer(MDFClientSocketReader reader);
}
