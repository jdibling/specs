package com.theice.mdf.client.multicast.gateway;

import java.net.Socket;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.*;

import com.theice.mdf.message.TunnelingMessageBlock;

/**
 * <code>TunnelingManager</code>
 *
 */
public class TunnelingManager implements Runnable
{
   protected static TunnelingManager _instance = new TunnelingManager();
   protected Map<Socket, BlockingQueue<TunnelingMessageBlock>> _tunnelQueueList=null; 

   private static final Logger LOGGER=Logger.getLogger(TunnelingManager.class);
   private static final int QUEUESIZE_WARN = 5000;
   private static final int QUEUESIZE_FORCEOUT = 50000;
   private static int _queueSize_warn;
   private static int _queueSize_forceout;
   private static volatile long _lastDistributeTimestamp=0;

   static
   {
      try
      {
         _queueSize_warn = Integer.parseInt(MulticastGatewayProperties.getClientBufferSizeWarn());
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting reader warning buffer size. Use default:"+QUEUESIZE_WARN);
         _queueSize_warn = QUEUESIZE_WARN;
      }

      try
      {
         _queueSize_forceout = Integer.parseInt(MulticastGatewayProperties.getClientBufferSizeForceout());
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting reader force-out buffer size. User default:"+QUEUESIZE_FORCEOUT);
         _queueSize_forceout = QUEUESIZE_FORCEOUT;
      }
   }

   public static TunnelingManager getInstance()
   {
      return _instance;
   }

   /**
    * Initialize the Tunneling Manager
    */
   protected TunnelingManager()
   {
      _tunnelQueueList=new HashMap<Socket, BlockingQueue<TunnelingMessageBlock>>();
   }

   /**
    * register the tunnel queue
    * @param queue
    */
   public void registerTunnelQueue(Socket socket, BlockingQueue<TunnelingMessageBlock> queue)
   {
      synchronized(_tunnelQueueList)
      {
         _tunnelQueueList.put(socket, queue);
      }
   }

   /**
    * unregister the tunnel queue
    * @param queue
    */
   public void unregisterTunnelQueue(Socket socket)
   {
      synchronized(_tunnelQueueList)
      {
         LOGGER.info("unregistering the tunneling queue, socket isClosed? "+socket.isClosed()+" socket isConnected? "+socket.isConnected());
         try
         {
            socket.close();
         }
         catch(Throwable ex)
         {
            LOGGER.info("error when closing socket when unregisterTunnelQueue:"+ex);
         }
         _tunnelQueueList.remove(socket);
         LOGGER.info("tunnel queue unregistered for "+socket.getRemoteSocketAddress().toString());
      }
   }

   /**
    * distribute the message
    * @param messageBlock
    */
   public void distribute(TunnelingMessageBlock messageBlock)
   {
      messageBlock.serialize();
      synchronized(_tunnelQueueList)
      {
         _lastDistributeTimestamp=System.currentTimeMillis();
         if(_tunnelQueueList.size()>0)
         {
            if (LOGGER.isDebugEnabled())
            {
               LOGGER.debug("### TunnelingManager : Distributing - "+messageBlock.toString());
            }
            
            Collection<BlockingQueue<TunnelingMessageBlock>> allQueues = _tunnelQueueList.values();
            for(BlockingQueue<TunnelingMessageBlock> queue : allQueues)
            {
               queue.offer(messageBlock);
            }

         }
         else
         {
            if (LOGGER.isDebugEnabled())
            {
               LOGGER.debug("TunnelingManager : No readers for - "+messageBlock.toString());
            }
         }
      }

      return;
   }

   protected Thread startHealthMonitorThread()
   {
      Thread t = null;
      try
      {
         t = new Thread(TunnelingManager.getInstance(), "Client_Connection_Health_Monitor");
         t.start();
         LOGGER.info("Client connection health monitor thread started.");
      }
      catch(Throwable e)
      {
         LOGGER.error("Error starting Client Connection Health Monitor thread.", e);
         t = null;
      }

      return t;
   }

   public void run()
   {
      while (true)
      {
         LOGGER.debug("Client connection health monitor thread heart beat.");
         synchronized(_tunnelQueueList)
         {
            Set<Socket> allConnections = _tunnelQueueList.keySet();
            for (Socket socket : allConnections)
            {
               String remoteAddr = socket.getRemoteSocketAddress().toString();
               BlockingQueue<TunnelingMessageBlock> queue = _tunnelQueueList.get(socket);
               if (queue.size() > _queueSize_forceout)
               {
                  LOGGER.warn("Queue size over forceout threshold for "+remoteAddr+". Close socket now. Actual queue size:"+queue.size());
                  try
                  {
                     socket.close();
                     queue=null;
                  }
                  catch(java.io.IOException ex)
                  {
                     LOGGER.error("Error closing socket: "+remoteAddr, ex);
                  }
               }
               else if (queue.size() > _queueSize_warn)
               {
                  LOGGER.warn("Queue size over warning threshold for "+remoteAddr+". Actual queue size:"+queue.size());
               }
            }
         }

         try
         {
            Thread.sleep(5000);
         }
         catch(Exception ex)
         {
            LOGGER.error("Exception when Client Connection Health Monitor thread sleeps.", ex);
         }

      }
   }

   public static long getLastDistributeTimestamp()
   {
      return _lastDistributeTimestamp;
   }

}

