package com.theice.mdf.client.multicast.gateway;

import java.io.IOException;
import java.net.*;

import org.apache.log4j.*;

/**
 * <code>SocketListener</code> handles accepting socket connections from clients.
 *
 */

public class SocketListener implements Runnable
{
   private static final Logger LOGGER = Logger.getLogger(SocketListener.class);
   private boolean _stopListening = false;
   private String _serverAddr = null;
   private int _serverPort = 0;

   public SocketListener(String serverAddr, int serverPort)
   {
      _serverAddr = serverAddr;
      _serverPort = serverPort;
   }

   public void run()
   {
      LOGGER.info("SocketListener.run: starting server socket, address - "
                + _serverAddr + ", port - " + _serverPort);

      int SOCKET_BACKLOG = 0;
      ServerSocket serverSocket = null;
      try {
         serverSocket = new ServerSocket(_serverPort,
                                         SOCKET_BACKLOG,
                                         InetAddress.getByName(_serverAddr));

         LOGGER.info("SocketListener.run: server socket created and ready to accept connections, address - "
                   + _serverAddr + ", port - " + _serverPort);

         while (true)
         {
            Socket socket = serverSocket.accept();

            if (_stopListening==true)
            {
               LOGGER.info ("SocketListener.run: _stopListening flag is set to true, exit the listener.");
               return;
            }

            if (socket==null)
            {
               LOGGER.warn("SocketListener.run:: null socket returned from server socket accept.");
               continue;
            }

            String remoteAddr = null;
            try
            {
               remoteAddr = socket.getRemoteSocketAddress().toString();

               // turn off Nagling
               socket.setTcpNoDelay(true);

               // try to handle the new connection
               handleNewConnection(socket);
            }
            catch (Throwable ex)
            {
            	LOGGER.warn("SocketListener.run: Client Addr - " + remoteAddr + ", exception ocurred after accept, msg - " + ex.getMessage() + ", IGNORE IT and CONTINUE to LISTEN!!", ex);
            }
         }

      }
      catch (Throwable e)
      {
         LOGGER.fatal("SocketListener.run: exception occurred, it won't accept new connection !!", e);
      }
      finally
      {
         LOGGER.info ("SocketListener.run: exit SocketListener and close server socket.");
         if (serverSocket!=null)
         {
            try
            {
               serverSocket.close();
            }
            catch (IOException e1)
            {
            }
         }
      }

   }
 
   /**
    * Handle the new connection. It would verify if it is over login limit or thread pool limit
    * first, and then try to use a reader worker thread to handle it.
    * @param socket
    * @throws IOException
    */
   private void handleNewConnection(Socket socket)
      throws IOException
   {
	   SocketReader socketReader = new SocketReader(socket);
	   String clientAddress = socket.getRemoteSocketAddress().toString();
	   Thread readerThread = new Thread(socketReader, "SocketReader"+clientAddress);
	   readerThread.start();
   }

   public void setStopListening(boolean stopListening)
   {
      _stopListening = stopListening;
   }

}
