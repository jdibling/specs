package com.theice.mdf.client.examples;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.request.*;

import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 * <code>SimpleClient</code> is a small program that demonstrates how to connect to the price
 * feed server, sending requests and reading the response/notification messages. This class handles
 * the connection and sending requests, while a separate socket reader thread is started to process
 * the inbound messages.
 *
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author David Chen
 * @since 12/28/2006
 */
public class SimpleClient
{
   private static int RequestSeqID = 0;

   public static void main(String[] args)
   {
      try
      {
         // Connect to the feed server
         System.out.println("SimpleClient.main: Connecting to feed server - " + SimpleClientConfigurator.getServerAddress()
                   + ":" + SimpleClientConfigurator.getServerPort());

         Socket clientSoc = new Socket(SimpleClientConfigurator.getServerAddress(),
                                       SimpleClientConfigurator.getServerPort());
         clientSoc.setTcpNoDelay(true);
         InputStream inStream = clientSoc.getInputStream();
         System.out.println("SimpleClient.main: Connected to server");

         // Start socket reader thread for processing response/streamed data from server
         SimpleClientSocketReader reader = new SimpleClientSocketReader(new DataInputStream(inStream));
         Thread readerThread = new Thread(reader, "ReaderThread");
         readerThread.start();

         // Start message consumer thread for processing messages
         SimpleClientMessageConsumer consumer = new SimpleClientMessageConsumer(reader);
         Thread consumerThread = new Thread(consumer, "ConsumerThread");
         consumerThread.start();

         OutputStream outStream = clientSoc.getOutputStream();

         // send debug request
         System.out.println("SimpleClient.main: send debug request");
         sendDebugRequest(outStream);

         // send login request
         System.out.println("SimpleClient.main: send login request");
         login(outStream);

         // send product definition requests
         System.out.println("SimpleClient.main: send product definition requests");
         requestProductDefintions(outStream);

         readerThread.join();

      }
      catch (IOException e)
      {
         System.out.println("SimpleClient.main: IOException caught");
         e.printStackTrace();
      }
      catch (InterruptedException e)
      {
         System.out.println("SimpleClient.main: InterruptedException caught");
         e.printStackTrace();
      }
   }

   /**
    * Request Logout
    * @param outStream
    * @throws IOException
    */
   private static void requestLogout(OutputStream outStream) throws IOException
   {
      LogoutRequest logoutReq = new LogoutRequest();
      logoutReq.RequestSeqID = getRequestSeqID();
      outStream.write(logoutReq.serialize());
   }

   /**
    * Send a debug request
    * @param outStream
    * @throws IOException
    */
   private static void sendDebugRequest(OutputStream outStream) throws IOException
   {
      DebugRequest debugRequest = new DebugRequest();
      debugRequest.RequestSeqID = getRequestSeqID();
      outStream.write(debugRequest.serialize());
   }

   /**
    * Request for the product defintions of the market types that we are interested in
    * @param outStream
    * @throws IOException
    */
   private static void requestProductDefintions(OutputStream outStream) throws IOException
   {
      int[] marketTypes = SimpleClientConfigurator.getMarketTypes();

      // iterate through the market types that we are interested in
      // and send out the market data request
      for (int i=0; i<marketTypes.length; i++)
      {
         ProductDefinitionRequest pdRequest = null;
         short marketTypeID = (short) marketTypes[i];
         if (SimpleClientConfigurator.isForOptions())
         {
            pdRequest = getProductDefinitionRequest(marketTypeID, ProductDefinitionRequest.SECURITY_TYPE_OPTION);
         }
         else
         {
            pdRequest = getProductDefinitionRequest(marketTypeID, ProductDefinitionRequest.SECURITY_TYPE_FUTRES_OTC);
         }
         
         outStream.write(pdRequest.serialize());
         
         if (SimpleClientConfigurator.isGetUDS())
         {
            pdRequest = getProductDefinitionRequest(marketTypeID, ProductDefinitionRequest.SECURITY_TYPE_UDS_OPTIONS);
            outStream.write(pdRequest.serialize());
         }
         
         if (SimpleClientConfigurator.isGetUDSForFutures())
         {
            pdRequest = getProductDefinitionRequest(marketTypeID, ProductDefinitionRequest.SECURITY_TYPE_UDS_FUTURES);
            outStream.write(pdRequest.serialize());
         }
      }
   }
   
   private static ProductDefinitionRequest getProductDefinitionRequest(short marketTypeID, char securityType)
   {
      ProductDefinitionRequest pdRequest = new ProductDefinitionRequest();
      pdRequest.MarketType = marketTypeID;
      pdRequest.RequestSeqID = getRequestSeqID();
      pdRequest.SecurityType = securityType;
      
      return pdRequest;
   }

   /**
    * Send login request
    *
    * @param outStream
    * @throws IOException
    */
   private static void login(OutputStream outStream) throws IOException
   {
      LoginRequest loginRequest = new LoginRequest();

      String userName = System.getProperty("mdf.client.username");
      if (userName==null || userName.length()==0)
      {
         userName = SimpleClientConfigurator.getUserName();
      }
      
      loginRequest.UserName
         = MessageUtil.toRawChars(userName, loginRequest.UserName.length);
      loginRequest.Password
         = MessageUtil.toRawChars(SimpleClientConfigurator.getPassword(), loginRequest.Password.length);
      loginRequest.RequestSeqID = getRequestSeqID();

      if (SimpleClientConfigurator.isGetStripInfo())
      {
         loginRequest.GetStripInfoMessages = 'Y';
      }
      
      outStream.write(loginRequest.serialize());
   }

   /**
    * Increment and return the request sequence ID
    * @return the request sequence ID
    */
   private static int getRequestSeqID()
   {
      return RequestSeqID++;
   }
}

