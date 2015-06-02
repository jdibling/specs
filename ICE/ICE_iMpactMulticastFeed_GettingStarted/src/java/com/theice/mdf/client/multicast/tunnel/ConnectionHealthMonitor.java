package com.theice.mdf.client.multicast.tunnel;

import java.io.DataInputStream;
import org.apache.log4j.*;

/**
 * ConnectionHealthMonitor monitors the health of the socket connection.
 * If the socket connection idle time is greater than the (configurable) threshold, the TunnelProxy will 
 * try to restart itself including initializing a new socket connection.
 * 
 * ConnectionHealthMonitor thread will be started only when autoReconnect is configured to true.
 * 
 * ConnectionHealthMonitor
 * 
 * @author Shawn Cheng
 */
public class ConnectionHealthMonitor implements Runnable
{
   private static final Logger LOGGER = Logger.getLogger(ConnectionHealthMonitor.class);
   private DataInputStream _inputstream=null;
   private long _interval=3000;
   private long _threshold=30000;

   public ConnectionHealthMonitor()
   {
   }
   
   public ConnectionHealthMonitor(long interval, long threshold, DataInputStream inputstream)
   {
      this._interval = interval;
      this._threshold = threshold;
      this._inputstream = inputstream;
   }
   
   public void run()
   {
      boolean keepRunning=true;
      
      while (keepRunning)
      {
         try
         {
            Thread.sleep(_interval);
            if (System.currentTimeMillis()-TunnelProxyManager.getInstance().getLastMessageTimestamp() > _threshold)
            {
               LOGGER.error("Last Message Timestamp:"+TunnelProxyManager.getInstance().getLastMessageTimestamp());
               LOGGER.error("TCP connection has been idle for more than "+_threshold/1000+" seconds. Stoping application ...");
               
               try
               {
                  _inputstream.close();
               }
               catch(Throwable ex)
               {
                  LOGGER.error("Error when closing inputstream:"+ex, ex);
               }
               keepRunning=false;
            }
         }
         catch(InterruptedException ex)
         {
            LOGGER.error("ConnectionMonitor has been interrupted. Exiting...");
            keepRunning=false;
         }
         catch(Throwable ex)
         {
            LOGGER.error("Exception:"+ex, ex);
         }
      }
   
   }
}
