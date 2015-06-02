package com.theice.mdf.client.multicast.tunnel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.MulticastMessageBlock;
import com.theice.mdf.message.TunnelingMessageBlock;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TunnelMulticastSender
 * 
 * @author Adam Athimuthu  
 */
public class TunnelMulticastSender 
{
    private final Logger logger=Logger.getLogger(TunnelMulticastSender.class.getName());
    
    private EndPointInfo _endPoint=null;
	private InetAddress _groupAddress=null;
	private MulticastSocket _socket=null;
	
	private long _numberOfBlocksSent=0L;
	private long _lastUpdateTimestamp=System.currentTimeMillis();
	
	public TunnelMulticastSender(EndPointInfo endPoint) throws Exception
	{
		_endPoint=endPoint;
		
		_groupAddress=InetAddress.getByName(endPoint.getIpAddress());
		_socket=new MulticastSocket();
		_socket.setTimeToLive(255);
		
		String multicastInterface=TunnelProxyConfigurator.getMulticastInterface();
		
		if(multicastInterface!=null)
		{
			_socket.setInterface(InetAddress.getByName(multicastInterface));
			System.out.println("Tunnel Multicast Interface : "+multicastInterface);
		}
		
		_socket.joinGroup(_groupAddress);
	}
	
	/**
	 * send multicast message at the local endpoint
	 * @param byteStream
	 */
	public void sendMessage(byte[] byteStream)
	{
		StringBuffer buffer=new StringBuffer();
		
		if(logger.isTraceEnabled())
		{
			buffer.append("Multicasting ").append(byteStream.length).append(" bytes to ");
			buffer.append(_endPoint.toString());
			logger.trace(buffer.toString());
		}
		
		try
		{
			DatagramPacket packet=new DatagramPacket(byteStream,byteStream.length,_groupAddress,_endPoint.getPort());
			_socket.send(packet);
			
			_numberOfBlocksSent++;
			_lastUpdateTimestamp = System.currentTimeMillis();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			logger.error("Exception sending multicast packet : "+e.toString());
		}
		
		return;
	}

	/**
	 * send multicast message at the local endpoint
	 * @param block
	 * @deprecated
	 */
	public void sendMessage(TunnelingMessageBlock block)
	{
		MulticastMessageBlock multicastBlock=block.getMulticastMessageBlock();

		System.out.println("%%% Sending Locally : "+_endPoint.toString()+"\n"+multicastBlock.toString());

		byte[] bytes=multicastBlock.serialize();

		try
		{
			DatagramPacket packet=new DatagramPacket(bytes,bytes.length,_groupAddress,_endPoint.getPort());
			_socket.send(packet);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			logger.error("Exception sending multicast packet : "+e.toString());
		}
		
		return;
	}

	public void close()
	{
		if(_socket!=null)
		{
			_socket.close();
		}
		
		return;
	}
	
	public long getNumberOfBlocksSent()
	{
		return(_numberOfBlocksSent);
	}
	
	public long getLastUpdateTimestamp()
	{
		return(_lastUpdateTimestamp);
	}
	
	public String getStatistics()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(_endPoint).append("\t").append(this.getNumberOfBlocksSent())
              .append("\t").append(MDFUtil.dateFormat.format(this.getLastUpdateTimestamp()));
		return(buffer.toString());
	}
}

