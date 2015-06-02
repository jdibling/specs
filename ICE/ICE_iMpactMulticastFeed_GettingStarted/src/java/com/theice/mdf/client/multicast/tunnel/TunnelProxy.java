package com.theice.mdf.client.multicast.tunnel;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.message.request.TunnelingProxyRequest;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TunnelProxy
 * 
 * @author Adam Athimuthu  
 */
public class TunnelProxy 
{
   private static final Logger logger=Logger.getLogger(TunnelProxy.class.getName());
   private static final boolean autoReconnect=TunnelProxyConfigurator.tunnelProxyAutoReconnect();
   private static final int autoReconnectInterval=TunnelProxyConfigurator.getTunnelProxyAutoreconnectInterval();
   private EndPointInfo socketEndPoint=null;
   private Socket socket=null;
	
   public TunnelProxy()
   {
      String ipAddress=TunnelProxyConfigurator.getServerAddress();
      int port=TunnelProxyConfigurator.getServerPort();
      socketEndPoint=new EndPointInfo(ipAddress,port);
   }
	
	/**
	 * Open tunnel
	 * @throws Exception
	 */
   public void openTunnel() throws Exception
   {
      try
      {
         logger.info("Opening Tunnel on : "+socketEndPoint.toString());
			
         socket=new Socket(socketEndPoint.getIpAddress(),socketEndPoint.getPort());
         socket.setTcpNoDelay(true);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         throw(e);
      }
   }

	/**
	 * close tunnel
	 */
   public void closeTunnel()
   {
      if(socket!=null)
      {
         logger.info("Closing Tunnel : "+socketEndPoint.toString());
         try
         {
            socket.close();
         }
         catch(Exception e)
         {
         }
      }
   }
	
	/**
	 * send tunnel request
	 * @throws Exception
	 */
   public void sendTunnelRequest() throws Exception
   {
      TunnelingProxyRequest tunnelingRequest=new TunnelingProxyRequest();
		
		tunnelingRequest.RequestSeqID=12345;
		tunnelingRequest.TunnelingMagicNumber=TunnelProxyConfigurator.getTunnelingMagicNumber();
		
		if(logger.isDebugEnabled())
		{
			logger.debug("Sending : "+tunnelingRequest.toString());
		}
		
		OutputStream outStream=socket.getOutputStream();
		
		outStream.write(tunnelingRequest.serialize());
		
		return;
	}
	
   public EndPointInfo getSocketEndPoint()
   {
      return(socketEndPoint);
   }
	
   public DataInputStream getInputStream() throws IOException
   {
      DataInputStream stream=null;
		
      if(socket!=null)
      {
         stream=new DataInputStream(socket.getInputStream());
      }
		
      return(stream);
   }
	
	/**
	 * main
	 * @param args
	 */
   public static void main(String[] args)
   {
      logger.info("TunnelProxy Starting...");
      logger.info("TunnelProxy autoReconnect is set to "+TunnelProxy.autoReconnect);
		
      TunnelProxy tunnelProxy=new TunnelProxy();
      ConnectionHealthMonitor healthMonitor=null;
      Thread healthMonitorThread=null;
		
      do
      {
         try
         {
            TunnelProxyManager.getInstance().initialize();
            logger.info("TunnelProxy opening tunnel...");

            tunnelProxy.openTunnel();
				
            TunnelReceiver tunnelReceiver=new TunnelReceiver(tunnelProxy.getSocketEndPoint(),
                  new DataInputStream(tunnelProxy.getInputStream()));
				
            Thread tunnelReceiverThread=new Thread(tunnelReceiver,"TunnelReceiver");
				
            tunnelReceiverThread.start();
								
            tunnelProxy.sendTunnelRequest();
			   
            if (TunnelProxy.autoReconnect)
            {
               healthMonitor=new ConnectionHealthMonitor(3000, 30000, tunnelProxy.getInputStream());
               healthMonitorThread=new Thread(healthMonitor, "HealthMonitor");
               healthMonitorThread.start();  
            }
				
            logger.info("Joining Tunnel Receiver Thread...");
            tunnelReceiverThread.join();
         }
         catch(IOException e)
         {
            logger.error("IOException:"+e,e);
         }
         catch(InitializationException e)
         {
            logger.error("InitializationException:"+e,e);
         }
         catch(Exception e)
         {
            logger.error("Exception:"+e,e);
         }
         finally
         {
            tunnelProxy.closeTunnel();
            if (healthMonitorThread!=null)
            {
               healthMonitorThread.interrupt();
            }
         }
			
         if (TunnelProxy.autoReconnect)
         {
            logger.info("TunnelProxy autoReconnect is true... will reconnect in about "+autoReconnectInterval+" seconds.");
            try
            {
               Thread.sleep(autoReconnectInterval*1000);
            }
            catch(Exception ex)
            {
               logger.error("Tunnel Proxy exception when waiting to reconnect:"+ex);
            }
         }
      } 
      while (TunnelProxy.autoReconnect);

      return;
   }

}
