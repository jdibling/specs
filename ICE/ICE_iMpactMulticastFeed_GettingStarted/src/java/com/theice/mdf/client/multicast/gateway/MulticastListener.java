package com.theice.mdf.client.multicast.gateway;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.MulticastMessageBlock;
import com.theice.mdf.message.TunnelingMessageBlock;

/**
 * Multicast Gateway
 * 
 *   Accepts properties for multicast group address(es) and port number(s)
 *   Gets the multicast packets and forward them to TCP socket 
 *   
 * @author Shawn Cheng
 */
public class MulticastListener implements Runnable
{
   private static final Logger LOGGER = Logger.getLogger(MulticastListener.class);
   private String _ipAddress=null;
   private int _port=0;
   private String _networkInterface=null;
   private MulticastSocket _multicastSocket=null;
   private InetAddress _inetGroup=null;
   
   private static final TunnelingManager tunnelingMgr=TunnelingManager.getInstance();

   public MulticastListener()
   {
   }

   public MulticastListener(String ipAddress,int port)
   {
      _ipAddress=ipAddress;
      _port=port;
   }

   public void setNetworkInterface(String networkInterface)
   {
      _networkInterface=networkInterface;
   }

   /**
    * open multicast channel
    * @throws IOException
    */
   protected void openMulticastChannel() throws IOException
   {
      String osName=System.getProperty("os.name");

      try
      {
         _inetGroup=InetAddress.getByName(_ipAddress);

         if(!_inetGroup.isMulticastAddress()) 
         {
            System.err.println( "Not a multicast address : "+_ipAddress);
            System.exit(1);
         }

         if("Linux".compareTo(osName)==0)
         {
            _multicastSocket=new MulticastSocket(new InetSocketAddress(_inetGroup,_port));
         }
         else
         {
            _multicastSocket=new MulticastSocket(_port);
         }

         if(_networkInterface!=null)
         {
            _multicastSocket.setInterface(InetAddress.getByName(_networkInterface));
            System.out.println("Multicast Network Interface : "+_networkInterface);
         }

         _multicastSocket.joinGroup(_inetGroup);

      }
      catch(IOException e)
      {
         LOGGER.error("Error opening multicast channel:",e);
         throw(e);
      }
   }

   /**
    * receive and output the messages from the multicast channel
    * @throws Exception
    */
   public void receive() throws Exception
   {
      byte[] buffer=new byte[1400];
      DatagramPacket packet=null;

      StringBuffer buf=null;

      while(true)
      {
         try
         {
            packet=new DatagramPacket(buffer,buffer.length);

            _multicastSocket.receive(packet);

            if(packet.getLength()==0)
            {
               System.err.println("*** Received a ZERO length packet");
               LOGGER.error("Received a ZERO length packet");
               continue;
            }

            TunnelingMessageBlock tunnelBlock = new TunnelingMessageBlock();
            tunnelBlock.GroupAddress=MessageUtil.toRawChars(this._ipAddress, tunnelBlock.GroupAddress.length);
            tunnelBlock.Port=_port;
            tunnelBlock.serializeWithGivenMessageBlockByteArray(packet.getData());

            tunnelingMgr.distribute(tunnelBlock);
            if (Thread.interrupted())
            {
               LOGGER.info("MulticastListener interrupted .... exiting....");
               break;
            }
         }
         catch(Throwable e)
         {
            buf=new StringBuffer();
            byte bytes[]=packet.getData();
            for(int index=0;index<packet.getLength();index++)
            {
               buf.append("["+bytes[index]+"]");
            }
            LOGGER.error("Exception while deserialization: Length: "+packet.getLength()+
                  " - "+buf.toString(), e);

         }
      }

   }

   /**
    * @param messageBlock
    */
   protected void printMessage(MulticastMessageBlock messageBlock)
   {
      System.out.println(Thread.currentThread().getName()+"-"+_ipAddress+":"+_port);
      System.out.println(messageBlock.toString());
   }

   public void run() 
   {
      try 
      {
         receive();
      }
      catch(Exception ex)
      {
         LOGGER.error(Thread.currentThread().getName()+"-MulticastListener.run() exception:", ex);
      }
   }

   /**
    * @return the ipAddress
    */
   public String getIpAddress()
   {
      return _ipAddress;
   }

   /**
    * @param ipAddress the ipAddress to set
    */
   public void setIpAddress(String ipAddress)
   {
      _ipAddress = ipAddress;
   }

   /**
    * @return the port
    */
   public int getPort()
   {
      return _port;
   }

   /**
    * @param port the port to set
    */
   public void setPort(int port)
   {
      _port = port;
   }

}