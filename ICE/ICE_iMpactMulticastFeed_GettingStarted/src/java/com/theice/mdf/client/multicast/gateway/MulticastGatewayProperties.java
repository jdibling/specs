package com.theice.mdf.client.multicast.gateway;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MulticastGatewayProperties {

   private static final Logger LOGGER = Logger.getLogger(MulticastGatewayProperties.class);
   private static final String PROPERTY_MULTICAST_GROUPS="mdf.multicast.groups";
   private static final String PROPERTY_MULTICAST_NETWORK_INTERFACE="multicast.network.interface";
   private static final String PROPERTY_MULTICAST_GATEWAY_TCPSERVERIP = "mdf.multicast.gateway.tcpserverip";
   private static final String PROPERTY_MULTICAST_GATEWAY_TCPSERVERPORT = "mdf.multicast.gateway.tcpserverport";
   private static final String PROPERTY_MULTICAST_GATEWAY_SOCKETREADER_SLEEPINTERVAL = "mdf.multicast.gateway.socketreader.sleepinterval";
   private static final String PROPERTY_MULTICAST_GATEWAY_SOCKETREADER_LATENCYTHRESHOLD = "mdf.multicast.gateway.socketreader.flushLatencyThreshold";
   private static final String PROPERTY_MULTICAST_GATEWAY_SOCKETCLIENT_BUFFERSIZE_WARN = "mdf.multicast.gateway.socketclient.buffersize.warn";
   private static final String PROPERTY_MULTICAST_GATEWAY_SOCKETCLIENT_BUFFERSIZE_FORCEOUT = "mdf.multicast.gateway.socketclient.buffersize.forceout";
   private static final String PROPERTY_MULTICAST_GATEWAY_TCP_HEARTBEAT_THRESHOLD = "mdf.multicast.gateway.tcp.heartbeat.generate.threshold";
   private static final Properties resources=new Properties();
		    
   static
   {
      try
      {
         resources.load(new FileInputStream("multicastGateway.properties"));
      }
      catch(Throwable ex)
      {
         LOGGER.error("Error reading properties:"+ex);
         System.exit(1);
      }
   }
	
   private MulticastGatewayProperties()
   {
   }
		
   public static String getMulticastGroups()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GROUPS).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GROUPS, ex);
      }
		
      return value;
   }
	
   public static String getNetworkInterface()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_NETWORK_INTERFACE).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_NETWORK_INTERFACE, ex);
      }
		
      return value;		
   }
	
   public static String getServerAddress()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_TCPSERVERIP).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_TCPSERVERIP, ex);
      }
      
      return value;	
   }
	
   public static String getServerPort()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_TCPSERVERPORT).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_TCPSERVERPORT, ex);
      }

      return value;
   }
	
   public static String getReaderSleepInterval()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_SOCKETREADER_SLEEPINTERVAL).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_SOCKETREADER_SLEEPINTERVAL, ex);
      }
      
      return value;		
   }
	
   public static String getFlushLatencyThreshold()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_SOCKETREADER_LATENCYTHRESHOLD).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_SOCKETREADER_LATENCYTHRESHOLD, ex);
      }
      return value;
   }
	
   public static String getClientBufferSizeWarn()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_SOCKETCLIENT_BUFFERSIZE_WARN).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_SOCKETCLIENT_BUFFERSIZE_WARN, ex);
      }
      return value;
   }
	
   public static String getClientBufferSizeForceout()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_SOCKETCLIENT_BUFFERSIZE_FORCEOUT).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_SOCKETCLIENT_BUFFERSIZE_FORCEOUT, ex);
      }
      return value;
   }
	
   public static String getTCPConnectionHeartbeatThreshold()
   {
      String value = null;
      try
      {
         value = resources.getProperty(PROPERTY_MULTICAST_GATEWAY_TCP_HEARTBEAT_THRESHOLD).trim();
      }
      catch(Exception ex)
      {
         LOGGER.error("Error getting " + PROPERTY_MULTICAST_GATEWAY_TCP_HEARTBEAT_THRESHOLD, ex);
      }
      return value;
   }
	   
}
