package com.theice.mdf.client.multicast.monitor;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class MulticastMessageBlockMonitor implements Runnable 
{
   private static final int BLOCK_HEADER_LENGTH = 16;
   private String _networkInterface=null;

   private static int DEFAULT_RECEIVE_BUFFER_SIZE_BYTES=16777216;
   private int _receiveBufferSize=DEFAULT_RECEIVE_BUFFER_SIZE_BYTES;

   private MulticastSocket _multicastSocket=null;
   private InetAddress _inetGroup=null;
   private int _inactivityThreshold=30000;
   private String _multicastGroupIP;
   private int _port;

   public static void main(String[] args)
   {
      String ip = args[0];
      int port = Integer.parseInt(args[1]);
      MulticastMessageBlockMonitor monitor = new MulticastMessageBlockMonitor(ip, port);
      if (args.length>2 && args[2] != null && args[2].length()>0)
      {
         System.out.println("set network interface to "+args[2]);
         monitor.setNetworkInterface(args[2]);
      }
      Thread receivingThread = new Thread(monitor);
      receivingThread.start();
   }

   public MulticastMessageBlockMonitor(String ip, int port)
   {
      _multicastGroupIP = ip;
      _port = port;
   }

   public void setNetworkInterface(String networkInterface)
   {
      this._networkInterface=networkInterface;
   }

   public void setReceiveBufferSize(int receiveBufferSize)
   {
      _receiveBufferSize=receiveBufferSize;
   }

   /**
    * open multicast channel
    * @throws IOException
    */
   protected void openMulticastChannel() throws Exception
   {
      String osName=System.getProperty("os.name");
      try
      {
         _inetGroup=InetAddress.getByName(_multicastGroupIP);

         if(!_inetGroup.isMulticastAddress()) 
         {
            StringBuffer buf=new StringBuffer();
            buf.append("Not a multicast address : "+_multicastGroupIP);
            throw(new Exception(buf.toString()));
         }

         if("Linux".compareTo(osName)==0)
         {
            _multicastSocket=new MulticastSocket(new InetSocketAddress(_inetGroup, _port));
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

         System.out.println("Current Receive Buffer Size = "+_multicastSocket.getReceiveBufferSize());
         _multicastSocket.setReceiveBufferSize(_receiveBufferSize);
         System.out.println("Setting the Multicast Socket Receive Buffer Size to = "+_multicastSocket.getReceiveBufferSize());

         if(_inactivityThreshold>0)
         {
            System.out.println("*** Setting Multicast Channel Timeout to : "+_inactivityThreshold);
            _multicastSocket.setSoTimeout(_inactivityThreshold);
         }

         _multicastSocket.joinGroup(_inetGroup);

      }
      catch(IOException e)
      {
         e.printStackTrace();
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

      System.out.println("### Entering multicast receive loop ### : "+_multicastGroupIP);
      long numOfPacketsReceived=0;
      while(true)
      {
         packet=new DatagramPacket(buffer,buffer.length);

         try
         {
            _multicastSocket.receive(packet);
         }
         catch(SocketTimeoutException ioe)
         {
            System.out.println("SocketTimeoutException. Channel is inactive");
         }
         catch(IOException ioe)
         {
            System.out.println("IOException while receiving messages through multicast : "+ioe.toString());
            throw(ioe);
         }

         if(packet.getLength()==0)
         {
            System.out.println("*** Received a ZERO length packet");
            continue;
         }

         String sourceOfGoodPacket = null;
         ByteArrayInputStream stream=new ByteArrayInputStream(packet.getData(),0,packet.getLength());

         try
         {
            DataInputStream inputStream = new DataInputStream(stream);
            byte[] bytes = new byte[BLOCK_HEADER_LENGTH];
            inputStream.readFully(bytes);
            ByteBuffer headerByteBuffer = ByteBuffer.wrap(bytes);
            short sessionId = headerByteBuffer.getShort();
            int sequenceNumber = headerByteBuffer.getInt();
            short numOfMessages = headerByteBuffer.getShort();
            long sentDateTime = headerByteBuffer.getLong();

            if (sessionId <= 0 || numOfMessages <0 || numOfMessages > 100 || sentDateTime <= 0 || sequenceNumber <= 0)
            {
               System.out.println(System.currentTimeMillis()+": Bad message block: source="+packet.getSocketAddress().toString());
               System.out.println("Source of a good packet="+sourceOfGoodPacket);
            }
            else if (sourceOfGoodPacket==null)
            {
               sourceOfGoodPacket = packet.getSocketAddress().toString();
            }

            //System.out.println("sid="+sessionId+", seqNum="+sequenceNumber+", numOfMsgs="+numOfMessages+", timestamp="+sentDateTime);

         }
         catch(Exception e)
         {
            System.out.println("Exception while deserializing message block: Length: "+packet.getLength()+", Exception:"+e);
            e.printStackTrace();
         }

         if ((++numOfPacketsReceived) % 200 == 0)
         {
            System.out.print("."); //heartbeat
         }
      }
   }

   /**
    * thread's run method
    */
   public void run()
   {
      try
      {
         runClient();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }

      System.out.println("Monitoring thread exiting...");

      return;
   }

   /**
    * multicast client
    * @param args
    * @throws Exception
    */
   public void runClient()
   {     
      System.out.println("MulticastChannelClient - Joining : "+_multicastGroupIP);

      try
      {
         openMulticastChannel();
         receive();
      }
      catch(Exception e)
      {
         System.out.println("Multicast Client Failed : "+e.toString());
         e.printStackTrace();
      }

      try
      {
         System.err.println("Thread Exiting : "+Thread.currentThread().getName()+"/"+Thread.currentThread().getId());
      }
      catch(Exception e)
      {
      }

      return;
   }

   /**
    * toString
    * 
    * @return String
    */
   public String toString()
   {
      StringBuffer buffer=new StringBuffer();

      buffer.append("MulticastClient : ");

      if(_multicastGroupIP!=null)
      {
         buffer.append(this._multicastGroupIP);
      }
      else
      {
         buffer.append("[null]");
      }

      return(buffer.toString());
   }

}

