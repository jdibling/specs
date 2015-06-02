package com.theice.mdf.client.multicast.tunnel;

import org.apache.log4j.Logger;

import com.theice.mdf.client.exception.InitializationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu  
 */
public class TunnelProxyManager 
{
   private static TunnelProxyManager _instance=new TunnelProxyManager();

   private Logger logger=Logger.getLogger(TunnelProxyManager.class.getName());

   private static final String DEFAULT_LOGFILE="mdflogging.properties";
    
   private volatile long _lastMessageTimestamp=0;
   
   public static TunnelProxyManager getInstance()
   {
      return(_instance);
   }

    /**
     * TunnelProxyManager
     */
   protected TunnelProxyManager()
   {
   }

    /**
     * Initialize the application manager
     * Start the logs maanger
     * @throws InitializationException
     */
   public void initialize() throws InitializationException
   {
      logger.info("TunnelProxyManager Initializing...");
      _lastMessageTimestamp=System.currentTimeMillis();
    	
      return;
   }

   public void setLastMessageTimestamp(long timestamp)
   {
      _lastMessageTimestamp = timestamp;
   }

   public long getLastMessageTimestamp()
   {
      return _lastMessageTimestamp;
   } 

}

