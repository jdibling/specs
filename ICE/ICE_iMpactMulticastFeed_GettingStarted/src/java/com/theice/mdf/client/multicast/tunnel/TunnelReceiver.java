package com.theice.mdf.client.multicast.tunnel;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.exception.InvalidStateException;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.TunnelingMessageBlock;
import com.theice.mdf.message.UnknownMessageException;
import com.theice.mdf.message.response.TunnelingProxyResponse;
import com.theice.mdf.message.MulticastMessageBlock;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TunnelReceiver
 * 
 * @author Adam Athimuthu  
 */
public class TunnelReceiver implements Runnable 
{
   private final Logger logger=Logger.getLogger(TunnelReceiver.class.getName());
   private EndPointInfo _socketEndPoint=null;
   private DataInputStream _inputStream=null;
   private final int STATISTICS_INTERVAL=30;

	/**
	 * Multicast senders map
	 */
   private Map<EndPointInfo,TunnelMulticastSender> _senders=new HashMap<EndPointInfo,TunnelMulticastSender>();

	/**
	 * Timer task for reporting the multicast tunnel statistics (defult 30 seconds)
	 */
   protected Timer _timer=null;
   protected TimerTask _task=null;

   public TunnelReceiver(EndPointInfo endPoint,DataInputStream inStream)
   {
      _socketEndPoint=endPoint;
      _inputStream=inStream;
   }
    
    /**
     * thread's run method
     */
   public void run()
   {
      boolean keepRunning=true;
      StringBuffer buffer=new StringBuffer();

      logger.info("TunnelReceiver.run: Entering.");
      startStatisticsTimer(STATISTICS_INTERVAL);

      try
      {
        	/**
        	 * Wait for the tunnel login proxy response indicating success/failure
        	 * If we get a failure, close the connection
        	 * It is quite possible we get forced out by the server due to invalid magic etc.,
        	 */
         MDMessage message=RawMessageFactory.getObject(_inputStream);
         
         TunnelProxyManager.getInstance().setLastMessageTimestamp(System.currentTimeMillis());

         if(message.getMessageType()!=RawMessageFactory.TunnelingProxyResponseType)
         {
            StringBuffer buf=new StringBuffer();
            buf.append("Unknown message received. Check if tunneling was enabled on the server\n");
            buf.append(message.toString());
            throw(new InvalidStateException(buf.toString()));
         }

         TunnelingProxyResponse tunnelingProxyResponse=(TunnelingProxyResponse) message;

         System.out.println("Tunnel Proxy Response : "+tunnelingProxyResponse.toString());
            
         if(tunnelingProxyResponse.Code!=TunnelingProxyResponse.CODE_SUCCESS)
         {
            System.err.println("Failed to establish a tunnel : "+MessageUtil.toString(tunnelingProxyResponse.Text));
            throw(new Exception("Tunneling Request Failure."));
         }

        	/**
        	 * Start receiving tunnel messages
        	 */
         Exception ex=null;
        
         while(keepRunning)
         {
            try
            {
               TunnelingMessageBlock block=new TunnelingMessageBlock(); 

               byte[] bytes=new byte[TunnelingMessageBlock.BLOCK_HEADER_LENGTH];
               _inputStream.readFully(bytes);
               TunnelProxyManager.getInstance().setLastMessageTimestamp(System.currentTimeMillis());
                    
               ByteBuffer headerByteBuffer = ByteBuffer.wrap(bytes);
               block.populateHeaderFields(headerByteBuffer);

               if(logger.isTraceEnabled())
               {
                  buffer=new StringBuffer();
                  buffer.append("%%% Tunnel Header : ").append(MessageUtil.toString(block.GroupAddress)).append("/");
                  buffer.append(block.Port).append(" [Body Length=").append(block.BlockBodyLength).append("]");
                  logger.trace(buffer.toString());
               }

               byte[] multicastMessageBytes=new byte[block.BlockBodyLength];
               _inputStream.readFully(multicastMessageBytes);
               
               if (block.BlockBodyLength==MulticastMessageBlock.BLOCK_HEADER_LENGTH)
               {
                  //Gateway Heartbeat message
                  if (block.Port==0)
                  {
                     logger.info("Received Gateway heartbeat ... ");
                     continue;
                  }
               }
                    
//                    MulticastMessageBlock multicastMessageBlock=new MulticastMessageBlock();
//                    multicastMessageBlock.deserialize(ByteBuffer.wrap(multicastMessageBytes));
//                    System.out.println("%%%%% Publishing : "+multicastMessageBlock.toString());
                    
               EndPointInfo endPoint=new EndPointInfo(MessageUtil.toString(block.GroupAddress),block.Port);
                	
               if(endPoint==null)
               {
                  System.err.println("Fatal Error. End Point Sent by the tunnel server is null.");
                  continue;
               }
                	
               TunnelMulticastSender sender=_senders.get(endPoint);
                	
               if(sender==null)
               {
                  EndPointInfo localEndPoint=TunnelProxyConfigurator.getLocalEndPoint(endPoint);

                  buffer=new StringBuffer();
                  buffer.append("%%% Creating MulticastSocket for : ").append(endPoint.toString());
                  buffer.append(" at ").append(localEndPoint.toString());
                  buffer.append(" ### Statistics will be reported every ").append(STATISTICS_INTERVAL);
                  buffer.append(" seconds.");
                        
                  logger.info(buffer.toString());

                  sender=new TunnelMulticastSender(localEndPoint);
                        
                  _senders.put(endPoint, sender);
               }
               logger.debug("TunnelReceiver sending message...");
               sender.sendMessage(multicastMessageBytes);
                    
            }
            catch (IOException e)
            {
               logger.error("TunnelReceiver.run: IOException caught: " + MDFUtil.getStackInfo(e));
               e.printStackTrace();
               keepRunning=false;
               ex=e;
            }
            catch(UnknownMessageException e)
            {
               logger.error(e.getMessage());
               e.printStackTrace();
               keepRunning=false;
               ex=e;
            }
            catch(Exception e)
            {
               logger.error(e.getMessage());
               e.printStackTrace();
               keepRunning=false;
               ex=e;
            }
            
            if (!keepRunning)
            {
               stopStatisticsTimer();
               System.out.println("Exception:"+ex);
               System.out.println("Tunnel closing...");
            }
         }
         
      }
      catch(InvalidStateException e)
      {
         logger.error("TunnelReceiver.run: : "+ MDFUtil.getStackInfo(e));
         e.printStackTrace();
      }
      catch (Throwable e)
      {
         logger.error("TunnelReceiver.run: Throwable caught : "+ MDFUtil.getStackInfo(e));
         e.printStackTrace();
      }
      finally
      {
         try
         {
            logger.info("TunnelReceiver.run: Closing the multicast channels.");

            for(Iterator<TunnelMulticastSender> it=_senders.values().iterator();it.hasNext();)
            {
               TunnelMulticastSender sender=it.next();
               sender.close();
            }
                
            logger.info("Stopping the timer.");
                
            stopStatisticsTimer();
         }
         catch(Exception e)
         {
         }   
      }

      logger.info("TunnelReceiver.run: Exiting.");
        
      return;
    	
   }
    
   protected void stopStatisticsTimer()
   {
      if(_timer!=null)
      {
         _timer.cancel();
      }
   }
   
   /**
     * Start the statistics timer
     * @param interval in milliseconds
     */
   public void startStatisticsTimer(int interval)
   {
      _timer=new java.util.Timer();

      _task=new TimerTask()
      {
         public void run()
         {
            try
            {
               reportStatistics();
            }
            catch(Throwable e)
            {
               logger.warn("Statistics reporting failed : "+MDFUtil.getStackInfo(e));
            }
         }
                
      };

      _timer.scheduleAtFixedRate(_task, interval*1000, interval*1000);
        
      return;
   }

    /**
     * report statistics
     */
   public void reportStatistics()
   {
    	String newline=System.getProperty("line.separator");    	
    	StringBuffer buffer=new StringBuffer();
    	buffer.append(newline);
    	buffer.append("### Tunnel Proxy Report at : ").append(MDFUtil.dateFormat.format(System.currentTimeMillis())+")");
    	buffer.append(newline).append(newline);
    	buffer.append("Tunnel Socket Address : ").append(_socketEndPoint.toString());
    	buffer.append(newline).append(newline);
    	buffer.append("Server Multicast Addr\tLocal Multicast Addr\tBlocks Sent\tLast Sent");
    	buffer.append(newline);
    	buffer.append("---------------------\t--------------------\t-----------\t------------");
    	buffer.append(newline).append(newline);

    	for(Iterator<Map.Entry<EndPointInfo, TunnelMulticastSender>> it=_senders.entrySet().iterator();it.hasNext();)
    	{
    		Map.Entry<EndPointInfo,TunnelMulticastSender> entry=it.next();
    		
    		buffer.append(entry.getKey().toString());
    		buffer.append("\t");
    		buffer.append(entry.getValue().getStatistics());
        	buffer.append(newline);
    	}
    	
    	buffer.append(newline);
    	buffer.append("### End Statistics ###").append(newline);
    	buffer.append(newline);
    	buffer.append(newline);
    	System.out.println(buffer.toString());

    	return;
   }


}

