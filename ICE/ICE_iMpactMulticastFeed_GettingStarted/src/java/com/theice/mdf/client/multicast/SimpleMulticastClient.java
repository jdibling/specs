package com.theice.mdf.client.multicast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.*;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.MulticastMessageBlock;

/**
 * Simple Multicast Client
 * 
 *   Accepts system properties for group address and port number
 *   Gets the multicast packets and displays onto stdout 
 *   
 * Monitoring and Alterts
 * 
 * 	The Simple Multicast Client monitors the traffic and sends out alters during exception situations such as:
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
public class SimpleMulticastClient 
{
   private static final Logger logger = Logger.getLogger(SimpleMulticastClient.class);
   private static final String PROPERTY_GROUP_ADDRESS="multicast.group.address";
   private static final String PROPERTY_PORT="multicast.port";
   private static final String PROPERTY_MULTICAST_NETWORK_INTERFACE="multicast.network.interface";
   private static final SimpleDateFormat DTFORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   
   private String _ipAddress=null;
   private String _multicastGroupName=null;
   private int _port=0;

   public SimpleMulticastClient()
   {
   }
   
   public SimpleMulticastClient(String ipAddress, int port)
   {
      _ipAddress=ipAddress;
      _port=port;
   }

   public SimpleMulticastClient(String ipAddress, int port, String multicastGroupName)
   {
      this(ipAddress, port);
      _multicastGroupName=multicastGroupName;
   }
   
   /**
    * multicast client
    * @param args
    * @throws Exception
    */
   public static void main(String args[]) throws Exception
   {
      try
      {
         MDFUtil.setAltMessageFactoryImpl();
      }
      catch(Exception ex)
      {
        logger.error("Error setting alternate message factory impl: "+ex, ex);
      }
      
      runClient(new SimpleMulticastClient(), args);
      
      System.out.println("SimpleMulticastClient exiting...");
   }
   
   public static void runClient(SimpleMulticastClient client, String args[])
   {
      String ipAddress=null;
      int portNumber=0;
      String multicastNetworkInterface=null;
      Thread receiverThread=null;
      Thread consumerThread=null;

      if (client.getIpAddress()!=null && client.getIpAddress().length()!=0)
      {
         ipAddress=client.getIpAddress();
      }
      else
      {
         ipAddress=System.getProperty(PROPERTY_GROUP_ADDRESS);
      }
      
      if (client.getPort()!=0)
      {
         portNumber = client.getPort();
      }
      else
      {
         try
         {
            portNumber = Integer.parseInt(System.getProperty(PROPERTY_PORT));
         }
         catch(Exception ex)
         {
            System.err.println("Error when getting port number from system property:"+ex);
            portNumber=0;
         }
      }

      if(ipAddress==null)
      {
         System.err.println("Please supply an IP Address using system property : "+PROPERTY_GROUP_ADDRESS);
         System.exit(1);
      }

      if(portNumber==0)
      {
         System.err.println("Please supply the port using system property : "+PROPERTY_PORT);
         System.exit(1);
      }

      multicastNetworkInterface=System.getProperty(PROPERTY_MULTICAST_NETWORK_INTERFACE);

      if(multicastNetworkInterface!=null)
      {
         System.out.println("Using network interface: "+multicastNetworkInterface);
      }
      else
      {
         System.out.println("Not using any specific multicast interface. If needed, specify using property : "+PROPERTY_PORT);
      }

      System.out.println("SimpleMulticastClient - Joining : "+ipAddress+"/"+portNumber);

      try
      {
         EndPointInfo endpoint = new EndPointInfo(ipAddress, portNumber);
         MulticastReceiver receiver = new MulticastReceiver(endpoint, multicastNetworkInterface, client.getMulticastGroupName(), false);
         try
         {
            receiver.openMulticastChannel();
         }
         catch(IOException e)
         {
            logger.error("Error opening multicast channel : "+e.toString());
            e.printStackTrace();
            throw(e);
         }
         receiverThread = new Thread(receiver, "MulticastReceiver");
         receiverThread.start();

         SimpleMulticastConsumer consumer = new SimpleMulticastConsumer(receiver, client);
         consumerThread = new Thread(consumer, "MulticastConsumer");
         consumerThread.start();

         receiverThread.join();
         consumerThread.join();
      }
      catch(Exception e)
      {
         System.err.println("Multicast Client Failed : "+e.toString());
         e.printStackTrace();
         System.exit(1);
      }      
      
   }
   
   protected void printMessage(MulticastMessageBlock messageBlock)
   {
      try
      {
         String dtString=DTFORMATTER.format(System.currentTimeMillis())+" ";
         StringBuffer msgHeader=new StringBuffer(dtString);
         msgHeader.append("SessionNumber=" + messageBlock.SessionNumber);
         msgHeader.append("|SequenceNumber=" + messageBlock.SequenceNumber);
         msgHeader.append("|NumOfMessages=" + messageBlock.NumOfMessages);
         msgHeader.append("|BlockBodyLength=" + messageBlock.BlockBodyLength);
         msgHeader.append("|SentDateTime=" + messageBlock.SentDateTime);
         System.out.println(msgHeader.toString());
         
         List<MDSequencedMessage> mdMessages=messageBlock.getMdMessages();
         if(mdMessages!=null)
         {
            for (int index = 0; index < mdMessages.size(); index++)
            {
               StringBuffer buffer=new StringBuffer(dtString);
               buffer.append(mdMessages.get(index).toString());
               System.out.println(buffer.toString());

            }
         }
      }
      catch(Exception ex)
      {
         System.err.println("Error printing message:"+ex);
         ex.printStackTrace();
         logger.error("Error printing message:"+ex, ex);
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
   
   public String getMulticastGroupName()
   {
      return _multicastGroupName;
   }
   
}

