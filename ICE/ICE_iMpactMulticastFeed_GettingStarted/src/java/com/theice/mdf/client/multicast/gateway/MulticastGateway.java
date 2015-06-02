package com.theice.mdf.client.multicast.gateway;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Multicast Gateway
 * 
 *   Accepts properties for multicast group address(es) and port number(s)
 *   Gets the multicast packets and forward them to TCP socket 
 *   
  * @author Shawn Cheng
 */
public class MulticastGateway
{
	private static MulticastGateway _instance = new MulticastGateway(); 
	private static final Logger LOGGER = Logger.getLogger(MulticastGateway.class);
    	    
	private MulticastGateway()
	{
   }
	
	public static MulticastGateway getInstance()
	{
		return _instance;
	}
    
    /**
     * multicast client
     * @param args
     * @throws Exception
     */
   public static void main(String args[]) throws Exception
   {
    	String _serverAddr = null;
    	int _serverPort = 0;
    	try
    	{
    		_serverAddr = MulticastGatewayProperties.getServerAddress();
        	_serverPort = Integer.parseInt(MulticastGatewayProperties.getServerPort());
    	}
    	catch(Exception ex)
    	{
    		LOGGER.error("Error parsing TCP socket connection info from property file: server IP:"+_serverAddr+", port:"+_serverPort);
    		System.exit(1);
    	}

    	SocketListener socketListner = new SocketListener(_serverAddr, _serverPort);
    	Thread listenerThread = new Thread(socketListner, "SocketListner");
    	listenerThread.start();
        
    	Thread heartbeatGeneratorThread = new Thread(new HeartBeatGenerator(), "Heartbeat Generator");
            	
    	String[] multicastAddresses = null;
      try
      {
         multicastAddresses = MulticastGatewayProperties.getMulticastGroups().split("\\|");
      }
      catch(Exception ex)
      {
         LOGGER.error("Error parsing multicast addresses from property file.");
         System.exit(1);
      }
        
      String multicastNetworkInterface = MulticastGatewayProperties.getNetworkInterface();
                
      try
      {
         List<Thread> threads = runClient(multicastAddresses, multicastNetworkInterface);
        	TunnelingManager.getInstance().startHealthMonitorThread();
        	heartbeatGeneratorThread.start();
        	
        	for (Thread t : threads)
        	{
        	   t.join();
        	}
      }
      catch(Exception ex)
      {
         LOGGER.error("Error starting multicast listener: "+ex);
         System.exit(1);
      }
      finally
      {
      }

   }
    
    /**
     * multicast client
     * @param args
     * @throws Exception
     */
   public static List<Thread> runClient(String[] addresses, String multicastNetworkInterface) throws Exception
   {  	
      List<Thread> threads = new ArrayList<Thread>(addresses.length);
      for (String address : addresses)
      {
         int portNumber=0;
         String[] addressAndPort = address.split(",");
         String ipAddress=addressAndPort[0].trim();
    		String port=addressAndPort[1].trim(); 
    		    		
	    	if(ipAddress==null || ipAddress.length()==0)
	    	{
	        	throw new Exception("Please specify IP Address correctly in the property file.");
	    	}
	    	
	    	if(port==null || port.length()==0)
	    	{
	    		throw new Exception("Please specify port number correctly in the property file.");
	    	}
	    	else
	    	{
	    		try
		    	{
		        	portNumber=Integer.parseInt(port);
		    	}
		    	catch(NumberFormatException e)
		    	{
		    		throw new Exception("Invalid Port Number : "+port);
		    	}
	    	}
	    	
	
	    	if(multicastNetworkInterface!=null && multicastNetworkInterface.length()>0)
	    	{
	        	LOGGER.info("Using network interface: "+multicastNetworkInterface);
	    	}
	    	else
	    	{
	        	LOGGER.info("Not using any specific multicast interface. If needed, specify in the property file");
	    	}
    	
	    	try
	    	{
	    		MulticastListener client = new MulticastListener(ipAddress, portNumber);
	    		client.setNetworkInterface(multicastNetworkInterface);
	    		client.openMulticastChannel();
	    		Thread t = new Thread(client, "Thread-"+ipAddress+":"+port);
	    		t.start();
	    		threads.add(t);
	    	}
	    	catch(Exception e)
	    	{
	    		LOGGER.error("Multicast Listener Failed : ", e);
	    		throw new Exception(e);
	    	}
    	}
    	return threads;
    }

}

