package com.theice.mdf.client.multicast.tunnel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Properties;
import java.util.StringTokenizer;

import com.theice.mdf.client.domain.EndPointInfo;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu  
 */
public class TunnelProxyConfigurator 
{
	private static Properties PROXYCONFIG=new Properties();
	
	private static final String KEY_FEEDSERVER_IPADDRESS="mdf.server.address";
	private static final String KEY_FEEDSERVER_PORT="mdf.server.port";
	private static final String KEY_TUNNELING_MAGIC_NUMBER="tunneling.magic.number";
	private static final String KEY_TUNNEL_MULTICAST_INTERFACE="tunnel.multicast.interface";
	private static final String KEY_TUNNEL_PROXY_AUTORECONNECT="tunnel.proxy.autoreconnect";
	private static final String KEY_TUNNEL_PROXY_AUTORECONNECT_INTERVAL="tunnel.proxy.autoreconnect.interval";
	
	private static final String DEFAULT_CONFIGFILE="tunnelProxy.properties";

	static
	{
		String configFileName=System.getProperty("config");
		   
		if(configFileName==null)
		{
			configFileName=DEFAULT_CONFIGFILE;
		}
   
		try
		{
			PROXYCONFIG.load(new FileInputStream(configFileName));
		}
		catch(IOException e)
		{
		   System.out.println("Failed to load : "+configFileName);
		   e.printStackTrace();
		}
	}
	
	private TunnelProxyConfigurator()
	{
	}
	
	/**
	 * For a given ip/port this method will return a string of a mapped ip/port in the following format
	 * 
	 * xxx.xxx.xxx.xxx,port
	 * 
	 * @param EndPointInfo
	 * @return local EndPointInfo
	 */
	public static EndPointInfo getLocalEndPoint(EndPointInfo endPoint)
	{
		StringBuffer buffer=new StringBuffer();
		Formatter formatter=new Formatter(buffer);
		
		EndPointInfo localEndPoint=new EndPointInfo(endPoint.getIpAddress(),endPoint.getPort());
		
		formatter.format("mapping.%s,%-6s", endPoint.getIpAddress(), endPoint.getPort());
		
		String groupAndPort=PROXYCONFIG.getProperty(buffer.toString().trim());
		
        if(groupAndPort!=null)
        {
        	StringTokenizer tokenizer=new StringTokenizer(groupAndPort,",");
        	
            if(tokenizer.hasMoreTokens())
            {
            	localEndPoint.setIpAddress(tokenizer.nextToken());
                
                if(tokenizer.hasMoreTokens())
                {
                	try
                	{
                		localEndPoint.setPort(Integer.parseInt(tokenizer.nextToken()));
                	}
                	catch(NumberFormatException e)
                	{
                	}
                	
                }
            }
        }
		
		return(localEndPoint);
	}
	
	/**
	 * get tunneling magic number
	 * @return
	 */
	public static long getTunnelingMagicNumber()
	{
		String value=(String) PROXYCONFIG.getProperty(KEY_TUNNELING_MAGIC_NUMBER);
		
		if(value==null)
		{
			value="55555";
		}

		return(Long.valueOf(value));
	}
	
	/**
	* Get the tunneling server IP address
	* @return the server IP address
	*/
	public static String getServerAddress()
	{
		return PROXYCONFIG.getProperty(KEY_FEEDSERVER_IPADDRESS);
	}

	/**
	* Get the tunnel multicast interface
	* @return the server IP address
	*/
	public static String getMulticastInterface()
	{
		return PROXYCONFIG.getProperty(KEY_TUNNEL_MULTICAST_INTERFACE);
	}

	/**
	* Get the tunneling server port
	* @return the port number
	*/
	public static int getServerPort()
	{
		return Integer.valueOf(PROXYCONFIG.getProperty(KEY_FEEDSERVER_PORT)).intValue();
	}
	
	public static boolean tunnelProxyAutoReconnect()
	{
		boolean flag = false;
		String value = PROXYCONFIG.getProperty(KEY_TUNNEL_PROXY_AUTORECONNECT);
		if (value!=null && value.length()>0)
		{
			if (value.trim().equalsIgnoreCase("true"))
			{
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * Get the tunnel proxy auto reconnect interval
	 * @return the time (in seconds) interval between each reconnect attempt
	 */
	public static int getTunnelProxyAutoreconnectInterval()
	{
		int interval=30;
		try
		{
			interval=Integer.parseInt(PROXYCONFIG.getProperty(KEY_TUNNEL_PROXY_AUTORECONNECT_INTERVAL).trim());
		}
		catch(Exception ex)
		{
			System.out.println("Error getting tunnel proxy auto reconnect interval. Use default of 30 seconds.");
		}
		return interval;
	}

	/**
	 * main()
	 * @param args
	 */
	public static void main(String[] args)
	{
		EndPointInfo mappedEndPoint=TunnelProxyConfigurator.getLocalEndPoint(new EndPointInfo("239.12.255.201", 7100));
		
		if(mappedEndPoint!=null)
		{
			System.out.println(mappedEndPoint.toString());
		}

		mappedEndPoint=TunnelProxyConfigurator.getLocalEndPoint(new EndPointInfo("239.12.255.201", 8100));
		
		if(mappedEndPoint!=null)
		{
			System.out.println(mappedEndPoint.toString());
		}
		
		mappedEndPoint=TunnelProxyConfigurator.getLocalEndPoint(new EndPointInfo("239.12.255.333", 9100));
		
		if(mappedEndPoint!=null)
		{
			System.out.println(mappedEndPoint.toString());
		}
		
		System.out.println("Magic Number : "+TunnelProxyConfigurator.getTunnelingMagicNumber());
		
		return;
	}

}

