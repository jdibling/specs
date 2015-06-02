package com.theice.mdf.client.multicast.gateway;

import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import org.apache.log4j.*;

import com.theice.mdf.message.*;
import com.theice.mdf.message.request.TunnelingProxyRequest;
import com.theice.mdf.message.response.TunnelingProxyResponse;

/**
 * <code>SocketReader</code> handles reading and processing requests from client.
 */

public class SocketReader implements Runnable
{
   private final static short MAX_MESSAGE_BODY_LENGTH = 1024;
   private final static int DEFAULT_SLEEPINTERVAL = 30;
   private final static long DEFAULT_FLUSHLATENCYTHRESHOLD = 50;
   private final static Logger LOGGER = Logger.getLogger(SocketReader.class);
   private final static Logger LATENCYLOGGER = Logger.getLogger("com.theice.mdf.client.multicast.gateway.latencyLogger");
   private static int _sleepInterval;
   private static long _flushLatencyThreshold;
   private Socket _socket;

	/**
    * Tunneling
    * If enabled, the socket reader accepts a connection from a tunnel client
    * After validating the request, a tunnel queue is created and registered with the tunneling manager
    * During a shutdown/exception scenario, the queue is unregistered
    */
   private TunnelingManager _tunnelingManager=TunnelingManager.getInstance();
   private BlockingQueue<TunnelingMessageBlock> _tunnelQueue=null;
	
   static
   {
      try
      {
         _sleepInterval = Integer.parseInt(MulticastGatewayProperties.getReaderSleepInterval());
      }
      catch(Exception ex)
      {
         LOGGER.warn("Error parsing reader sleep interval from property file. Use default value:"+DEFAULT_SLEEPINTERVAL, ex);
         _sleepInterval = DEFAULT_SLEEPINTERVAL;
      }
      try
      {
         _flushLatencyThreshold = Long.parseLong(MulticastGatewayProperties.getFlushLatencyThreshold());
      }
      catch(Exception ex)
      {
         LOGGER.warn("Error parsing reader flush latency threshold. Use default value:"+DEFAULT_FLUSHLATENCYTHRESHOLD, ex);
         _flushLatencyThreshold = DEFAULT_FLUSHLATENCYTHRESHOLD;
      }
   }

   public SocketReader(Socket socket)
   {
      _socket = socket;
   }
	
   public void setSleepInterval(int interval)
   {
      _sleepInterval = interval;
   }

   public void run() 
   {
      LOGGER.debug("SocketReader.run: thread started running. ");
	   
      TunnelingProxyRequest request = null;

      try 
      {
         DataInputStream inputStream = new DataInputStream(_socket.getInputStream());
		   
         // wait for request from client
         request = waitForRequest(inputStream);
         if (request!=null)
         {
            processMulticastTunnelingRequest(request);
         }
      }
      catch (InvalidRequestException e)
      {
         try
         {
            LOGGER.warn("SocketReader.run: InvalidRequestException caught, Error: " + e.getMessage());
         }
         catch (Exception e1)
         {
            // we are exiting the socket reader, don't care if there is IO exception here
         }
      }
      catch (Throwable e)
      {
         LOGGER.warn("SocketReader.run: Throwable caught, Error: " + e.getMessage());
      }
      finally
      {
         try
         {
            _socket.close();
         }
         catch(Exception ex)
         {
         }
      }
   }
     
	/**
	 * The method waits for request message from the client and return
	 * an request object if it is valid. Otherwise, it would be null.
	 *
	 * @param inputStream
	 * @return the request
	 * @throws IOException
	 */
   private TunnelingProxyRequest waitForRequest(DataInputStream inputStream)
   throws InvalidRequestException, IOException
   {
      TunnelingProxyRequest request = null;
      if (inputStream == null)
      {
         return null;
      }

      try
      {
         byte[] bytes = new byte[3];
         inputStream.readFully(bytes);
         request = new TunnelingProxyRequest();
         byte messageType = bytes[0];
         short bodyLength = ByteBuffer.wrap(bytes, 1, 2).getShort();
         
         if (bodyLength > MAX_MESSAGE_BODY_LENGTH)
         {
            throw new InvalidRequestException("Invalid request, message body length: " + bodyLength + ", over the limit.");
         }
         if (messageType != RawMessageFactory.TunnelingProxyRequestType)
         {
            throw new InvalidRequestException("Invalid request message type: " + messageType);
         }

         // read the body with the length received
         byte messageBodyBytes[] = new byte[bodyLength];
         inputStream.readFully(messageBodyBytes, 0, bodyLength);
         // deserialize the body
         request.deserialize(ByteBuffer.wrap(messageBodyBytes));
      }
      catch (InvalidRequestException ex)
      {
         throw new InvalidRequestException(ex.getMessage());
      }

      return request;
   }

	/**
	 * Process the multicast tunneling request
	 * Create an exclusive queue and register with the tunneling manager
	 * Start listening to the queue for TunnelingMessageBlock
	 * Get the messages and write back to the socket
	 * @param tunnelingRequest
	 */
   private void processMulticastTunnelingRequest(TunnelingProxyRequest tunnelingProxyRequest) throws Exception
   {
      BufferedOutputStream outputStream=null;
      TunnelingProxyResponse resposne=null;
		
      LOGGER.info("Handling Tunneling Proxy Request : "+tunnelingProxyRequest.toString());
		
      outputStream=new BufferedOutputStream(_socket.getOutputStream(), 1024);

      synchronized(_socket)
      {
         resposne=new TunnelingProxyResponse();
         resposne.RequestSeqID=tunnelingProxyRequest.RequestSeqID;
         resposne.Code=TunnelingProxyResponse.CODE_SUCCESS;
         resposne.Text=MessageUtil.toRawChars("Tunnel Request Validated Successfully", resposne.Text.length);
         LOGGER.info("Tunnel Proxy Request successfully validated.");
         outputStream.write(resposne.serialize());
         outputStream.flush();
      }
		
      /**
       * Initiate the tunnel handshake
       */
      _tunnelQueue=new LinkedBlockingQueue<TunnelingMessageBlock>();
      _tunnelingManager.registerTunnelQueue(_socket, _tunnelQueue);
		
      boolean keepTunnelActive=true;
		
      try
      {
         List<TunnelingMessageBlock> messages = new ArrayList<TunnelingMessageBlock>();
         while(keepTunnelActive)
         {	
            try
            {
               messages.clear();
               _tunnelQueue.drainTo(messages);
               int bufferSize = messages.size();
               long beginWritting = System.currentTimeMillis();
               for (TunnelingMessageBlock messageBlock : messages)
               {
                  outputStream.write(messageBlock.serialize());
               }
               outputStream.flush();
               long timeElapsed = System.currentTimeMillis() - beginWritting;
               //LOGGER.debug("Socket reader flush latency:"+timeElapsed+" ms.");
               if (timeElapsed > _flushLatencyThreshold)
               {
                  LATENCYLOGGER.warn(_socket.getRemoteSocketAddress()+"-socket flush time exceeds threshold of "+_flushLatencyThreshold+" ms. Actual elapsed time: "+timeElapsed+" ms. Buffer size="+bufferSize);
               }
            
               Thread.sleep(_sleepInterval);
            }
            catch(IOException e)
            {
               LOGGER.warn("IOException : "+e.toString());
               keepTunnelActive=false;
               throw(e);
            }
            catch(InterruptedException e)
            {
               LOGGER.warn("_tunnelQueue.put() interrupted : "+e.toString());
               keepTunnelActive=false;
               throw(e);
            }
            catch(Exception e)
            {
               LOGGER.warn("Exception in the tunnel loop : "+e.toString());
               keepTunnelActive=false;
               throw(e);
            }
         }
      }
      catch(Exception e)
      {
         LOGGER.warn("Exiting tunnel loop with exception : "+e.getMessage());
      }
      finally
      {
         LOGGER.info("Unregistering the tunneling queue");
         _tunnelingManager.unregisterTunnelQueue(_socket);
         _tunnelQueue=null;
      }
	    
      return;
   }

}


