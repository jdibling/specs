package com.theice.mdf.client.multicast.gateway;

import java.util.ArrayList;
import org.apache.log4j.*;

import com.theice.mdf.message.MulticastMessageBlock;
import com.theice.mdf.message.TunnelingMessageBlock;
import com.theice.mdf.message.MDSequencedMessage;

public class HeartBeatGenerator implements Runnable 
{
   private static final Logger LOGGER = Logger.getLogger(HeartBeatGenerator.class);
   private static final TunnelingMessageBlock HEARTBEATMSG=getHeartBeatMessage(); 

   public void run()
   {
      long threshold=0;
      try 
      {
         threshold = Long.parseLong(MulticastGatewayProperties.getTCPConnectionHeartbeatThreshold());
      }
      catch(Exception ex)
      {
         LOGGER.error("Error parsing heart beat interval:"+ex);
         LOGGER.error("Use default heart beat interval: 5 seconds.");
         threshold=5000;
      }

      while (true)
      {
         try
         {
            Thread.sleep(3000);
            if (LOGGER.isDebugEnabled())
            {
               LOGGER.debug("HeartBeatGenerator heartbeat. Last active message timestamp:"+TunnelingManager.getLastDistributeTimestamp());
            }
            if (System.currentTimeMillis()-TunnelingManager.getLastDistributeTimestamp()>threshold)
				{
               TunnelingManager.getInstance().distribute(HEARTBEATMSG);
               if (LOGGER.isDebugEnabled())
               {
                  LOGGER.debug("Gateway heartbeat message sent at "+System.currentTimeMillis());
               }
				}
         }
         catch(InterruptedException ex)
         {
            LOGGER.info("Interrupted...exiting ...");
            break;
         }
         catch(Exception ex)
         {
            LOGGER.error("Hearbeat generator exception:"+ex);
         }
      }
   }
	
   private static TunnelingMessageBlock getHeartBeatMessage()
   {
      TunnelingMessageBlock tunnelingMessage = new TunnelingMessageBlock();
      MulticastMessageBlock multicastMsg = new MulticastMessageBlock();
      multicastMsg.setMdMessages(new ArrayList<MDSequencedMessage>());
      tunnelingMessage.setMulticastMessageBlock(multicastMsg);
      
      return tunnelingMessage;
   }
	
}
