package com.theice.mdf.client.multicast.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.MulticastChannelInfo;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.client.util.MailThrottler;

/**
 * MulticastChannelClient
 *
 * Advanced version of the simple multicast client. This client essentially does the same as
 * what the simple multicast client does, but on a number of channels simultaneously
 *
 *   Accepts system properties for group addresses and port numbers for the multicast channels
 *   Gets the multicast packets and displays onto stdout
 *
 * Monitoring and Alterts
 *
 * 	The Simple Multicast client monitors the traffic and sends out alters during exception situations such as:
 *
 * 	- out of order packets
 * 	- session changes
 * 	- duplicate packets
 * 	- idle time (no messages) for more than 20 seconds
 *
 * An email is sent out during exception situations based on the appenders configured in the log4j config file
 *
 * @author Adam Athimuthu
 */
public class MulticastMonitor
{
    private static final String DEFAULT_CHANNEL_KEY = "UnknownChannelKey";
	private static final String DEFAULT_CHANNEL_NAME = "UnknownChannelName";
	private static final String DEFAULT_CHANNEL_TYPE = "UnknownChannelType";
	private static final String PROPERTY_GROUP_ADDRESSES="multicast.group.addresses";
    private static final String PROPERTY_SILENT="silent";
    private static String PROPKEY_MULTICAST_INACTIVITY_THRESHOLD="multicast.inactivity.threshold";
    private static String PROPKEY_MULTICAST_NETWORK_INTERFACE="multicast.network.interface";
    private static String PROPKEY_MULTICAST_RETRY_INTERVAL="multicast.retry.interval";
    private static String PROPKEY_MULTICAST_RECEIVE_BUFFER_SIZE="multicast.receive.buffer.size";
    private static String PROPERTY_MULTICAST_MONITOR_SEQ_CHECK_USE_MSG_HEADER_ONLY="multicast.monitor.seqcheck.use.headeronly";
        
	private static Logger logger=null;
	private static boolean useHeaderOnlyForSeqNumberCheck=false; //default to look into each msg
	private static MailThrottler mailThrottler=MailThrottler.getInstance();
    private static ArrayList<MulticastChannelClient> clients = new ArrayList<MulticastChannelClient>();

	static
	{
		logger=LogManager.getLogger(MulticastMonitor.class);
		String useHeaderOnly=System.getProperty(PROPERTY_MULTICAST_MONITOR_SEQ_CHECK_USE_MSG_HEADER_ONLY);
		if (useHeaderOnly!=null && useHeaderOnly.equals("true"))
		{
		   useHeaderOnlyForSeqNumberCheck=true;
		}
	}

    /**
     * Parse the endpoint information supplied as a string of the following pattern
     *
     * ipAddress:port:channelName,ipAddress:port:channelName
     *
     * @param groupAddresses
     * @return
     */
    public static MulticastChannelInfo[] parseEndPointInformation(String groupAddresses)
    {
    	List<MulticastChannelInfo> multicastChannelInfoList=new ArrayList<MulticastChannelInfo>();
    	StringTokenizer groupTokenizer=new StringTokenizer(groupAddresses,",");

    	while(groupTokenizer.hasMoreTokens())
    	{
        	String groupToken=groupTokenizer.nextToken();

        	StringTokenizer endPointTokenizer=new StringTokenizer(groupToken,":");

        	String ipAddress=endPointTokenizer.nextToken();
        	String port=endPointTokenizer.nextToken();
        	int portNumber=0;

        	try
        	{
            	portNumber=Integer.parseInt(port);
        	}
        	catch(NumberFormatException e)
        	{
        		logger.error("Invalid Port Number : "+port);
        		System.exit(1);
        	}

        	/**
        	 * The configuration entry should of the format ChannelType-ChannelName
        	 * For example, EurOil-FODSnapshot, EUROil-PLSnapshot etc.,
        	 */
        	String channelType=DEFAULT_CHANNEL_TYPE;
        	String channelName=DEFAULT_CHANNEL_NAME;
        	
        	String channelInfoKey=DEFAULT_CHANNEL_KEY;

        	try
        	{
            	channelInfoKey=endPointTokenizer.nextToken();
            	
            	if(channelInfoKey!=null)
            	{
                	StringTokenizer channelInfoTokenizer=new StringTokenizer(channelInfoKey,"-");
                
                	channelName = channelInfoTokenizer.nextToken();

                	if(channelName == null)
                	{
                		channelName = DEFAULT_CHANNEL_NAME;
                	}
                	
                	channelType = channelInfoTokenizer.nextToken();

                	if(channelType == null)
                	{
                		channelType = DEFAULT_CHANNEL_TYPE;
                	}

            	}
        	}
        	catch(Exception e)
        	{
        	}
        	
        	EndPointInfo endPointInfo=new EndPointInfo(ipAddress,portNumber);
        	
        	MulticastChannelInfo multicastChannelInfo=new MulticastChannelInfo(channelInfoKey,channelType,channelName,endPointInfo);

        	multicastChannelInfoList.add(multicastChannelInfo);
        	
    	}

    	return(multicastChannelInfoList.toArray(new MulticastChannelInfo[0]));
    }

    /**
     * main
     * @param args
     */
    public static void main(String args[])
    {
    	String groupAddresses=System.getProperty(PROPERTY_GROUP_ADDRESSES);
    	CountDownLatch coordinatorLatch=null;
    	boolean isSilent=false;
    	int inactivityThreshold=(-1);
    	int retryIntervalMs=(-1);

    	if(groupAddresses==null)
    	{
        	System.err.println("Please supply the IP Address/Port numbers using system property : "+PROPERTY_GROUP_ADDRESSES);
    		System.exit(1);
    	}

    	String silentMode=System.getProperty(PROPERTY_SILENT);

    	if(silentMode!=null && silentMode.trim().length()!=0)
    	{
    		isSilent=Boolean.parseBoolean(silentMode);
    		System.out.println("Silent ? "+isSilent);
    	}

    	String multicastInactivityThreshold=System.getProperty(PROPKEY_MULTICAST_INACTIVITY_THRESHOLD);

    	if(multicastInactivityThreshold!=null && multicastInactivityThreshold.trim().length()!=0)
    	{
    		inactivityThreshold=Integer.parseInt(multicastInactivityThreshold);
    		System.out.println("Inactivity Threshold="+inactivityThreshold);
    	}
    	else
    	{
    		System.out.println("Inactivity Threshold=[Indefinite Wait]");
    	}
    	
    	String multicastRetryInterval=System.getProperty(PROPKEY_MULTICAST_RETRY_INTERVAL);
    	
    	if(multicastRetryInterval!=null && multicastRetryInterval.trim().length()!=0)
    	{
    		retryIntervalMs=Integer.parseInt(multicastRetryInterval);
    		System.out.println("Retry Interval="+retryIntervalMs);
    	}
    	
    	MulticastChannelInfo multicastChannelInfo[]=parseEndPointInformation(groupAddresses);
    	
    	String networkInterface=System.getProperty(PROPKEY_MULTICAST_NETWORK_INTERFACE);
    	
    	if(networkInterface!=null)
    	{
    		System.out.println("Network interface="+networkInterface);
    	}
    	else
    	{
    		System.out.println("No specific network interface specified. If needed, use system property : "+PROPKEY_MULTICAST_NETWORK_INTERFACE);
    	}

    	try
    	{
    	   MDFUtil.setAltMessageFactoryImpl();
    	}
    	catch(Exception ex)
    	{
    	  logger.error("Error setting alternate message factory impl: "+ex, ex);
    	}
  
    	coordinatorLatch=new CountDownLatch(multicastChannelInfo.length);

    	for(int index=0;index<multicastChannelInfo.length;index++)
    	{
    		EndPointInfo endPointInfo=multicastChannelInfo[index].getEndPointInfo();
    		String channelName=multicastChannelInfo[index].getChannelName();
    		String channelType=multicastChannelInfo[index].getChannelType();
    		
    		String threadName = null;
    		if(DEFAULT_CHANNEL_NAME.equalsIgnoreCase(channelName) || DEFAULT_CHANNEL_TYPE.equalsIgnoreCase(channelType)){
    			threadName="MCMonitor-"+endPointInfo.getIpAddress()+"-"+endPointInfo.getPort();
    		} else {
    			threadName="MCMonitor-"+endPointInfo.getIpAddress()+"-"+endPointInfo.getPort()+"-"+channelName+"-"+channelType;
    		}
    		
    		logger.info("Starting thread ["+threadName+"]");
    		
        	MulticastChannelClient client=new MulticastChannelClient(multicastChannelInfo[index],isSilent,coordinatorLatch, useHeaderOnlyForSeqNumberCheck);
        	client.setInactivityThreshold(inactivityThreshold);
        	
        	if(networkInterface!=null)
        	{
        		client.setNetworkInterface(networkInterface);
        	}
        	
        	if(retryIntervalMs>0)
        	{
        		client.setRetryIntervalMs(retryIntervalMs);
        	}

        	String receiveBufferSize=System.getProperty(PROPKEY_MULTICAST_RECEIVE_BUFFER_SIZE);
        	
        	if(receiveBufferSize!=null && receiveBufferSize.trim().length()!=0)
        	{
        		client.setReceiveBufferSize(Integer.parseInt(receiveBufferSize));
        	}

        	Thread thread=new Thread(client,threadName);
        	thread.start();

        	clients.add(client);
    	}

    	try
    	{
    		System.out.println("Waiting for the latch");
        	coordinatorLatch.await();
    		System.out.println("Latch Activated");
    	}
    	catch(Exception e)
    	{
    	}

    	logger.info("Main Thread Exiting : "+Thread.currentThread().getName()+"/"+Thread.currentThread().getId());
    	
    	mailThrottler.haltService();
    	
    	try
    	{
        	System.out.println("Waiting for the logger thread to exit.");
        	Thread.sleep(1000);
    	}
    	catch(InterruptedException e)
    	{
    	}
    	
    	return;
    }

    public static String getAllClientStatus()
    {
       StringBuffer statusStr = new StringBuffer();
       for (MulticastChannelClient client : clients)
       {
          statusStr.append(client.getEndPointInfo()).append("    ").append(client.isActive()?"UP" : "DOWN");
          statusStr.append("\n");
       }

       return statusStr.toString();
    }
}

