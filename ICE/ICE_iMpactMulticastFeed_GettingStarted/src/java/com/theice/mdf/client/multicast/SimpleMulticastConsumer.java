package com.theice.mdf.client.multicast;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import com.theice.mdf.message.MulticastMessageBlock;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 */
public class SimpleMulticastConsumer implements Runnable 
{
   static Logger logger=Logger.getLogger(SimpleMulticastConsumer.class);
   private static final SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
   private Set<Integer> _missingPackets=new HashSet<Integer>();

   private MulticastReceiver _receiver=null;
   private SimpleMulticastClient _multicastClient=null;

   private boolean keepRunning=true;

   public SimpleMulticastConsumer(MulticastReceiver receiver, SimpleMulticastClient client)
   {
      _receiver=receiver;
      _multicastClient=client;
   }

   /**
    * stop the consumer
    */
   public void stop()
   {
      System.out.println("SimpleMulticastConsumer is stopping.");
      this.keepRunning=false;
   }

   /**
    * formats the given integer to a hex value
    * @param value
    * @return
    */
   private String toHexString(int value)
   {
      StringBuffer buf=new StringBuffer();
      Formatter formatter=new Formatter(buf);
      formatter.format("%x",value);
      return(buf.toString());
   }

   public void receive()
   {
      int lastSequence=(-1);
      int lastNumMessages=0;
      short sessionNumber=(-1);
      short previousSession=(-1);

      StringBuffer buf=null;

      while(keepRunning)
      {
         try
         {
            MulticastMessageBlock messageBlock=_receiver.getNextMessage(MDFMulticastClient.POLLING_TIMEOUT_MILLISECONDS);
            if (messageBlock==null)
            {
               continue;
            }
           
            if(sessionNumber!=messageBlock.SessionNumber)
            {
               if(sessionNumber<0)
               {
                  buf=new StringBuffer();
                  buf.append(dateFormatter.format(System.currentTimeMillis())).append(" : ");
                  buf.append("*** Start of Session : ").append(messageBlock.SessionNumber);
                  buf.append(" [Hex : ").append(toHexString(messageBlock.SessionNumber)).append("]");;
                  System.out.println(buf.toString());
               }
               else
               {
                  buf=new StringBuffer();
                  buf.append(dateFormatter.format(System.currentTimeMillis())).append(" : ");
                  buf.append("*** Session Change Detected. ");
                  buf.append("Previous Session : ").append(sessionNumber);
                  buf.append(" [Hex : ").append(toHexString(sessionNumber)).append("]");;
                  buf.append(" Current Session : ").append(messageBlock.SessionNumber);
                  buf.append(" [Hex : ").append(toHexString(messageBlock.SessionNumber)).append("]");;
                  System.err.println(buf.toString());

                  /**
                   * re-init the last sequence number during session changes
                   */
                  lastSequence=(-1);
                  lastNumMessages=0;
               }

               previousSession=sessionNumber;
               sessionNumber=messageBlock.SessionNumber;
            }

            /**
             * Check for out of sequence condition
             * - check for duplicate packets
             * - check for missing packets (that might arrive at a later point in time)
             * 
             */
            if(lastSequence>=0)
            {
               int expectedSequenceNumber=lastSequence+lastNumMessages;

               if(expectedSequenceNumber!=messageBlock.SequenceNumber)
               {
                  buf=new StringBuffer();
                  buf.append(dateFormatter.format(System.currentTimeMillis())).append(" : ");

                  StringBuffer errorSummary=new StringBuffer();
                  StringBuffer errorInfo=new StringBuffer();

                  if(messageBlock.SequenceNumber>expectedSequenceNumber)
                  {
                     errorSummary.append("*** Error (Sequence Gap Detected) ");

                     /**
                      * Missing packets (might indicate an out of sequence condition)
                      * Keep the missing packets in hash
                      */
                     int seq=expectedSequenceNumber;
                     errorInfo.append("[Missing=");
                     while(seq<messageBlock.SequenceNumber)
                     {
                        System.out.println("Caching Missing Packet : "+seq);

                        _missingPackets.add(new Integer(seq));

                        errorInfo.append(seq).append(",");
                        seq++;
                     }
                     errorInfo.append("]");
                  }
                  else if(messageBlock.SequenceNumber<expectedSequenceNumber)
                  {
                     /**
                      * Older packet. Check if we have this in the missing packets hash
                      * Otherwise, mark this is as duplicate and move forward
                      */
                     if(_missingPackets.remove(new Integer(messageBlock.SequenceNumber)))
                     {
                        errorSummary.append("*** Error (Out of Order Packet Received) ");
                        errorInfo.append(" [Older Packet : ").append(messageBlock.SequenceNumber).append("]");
                        errorInfo.append(" {Still Missing : ").append(_missingPackets.toString()).append("}");
                     }
                     else
                     {
                        errorSummary.append("*** Error (Duplicate Packet) ");
                        errorInfo.append(" [Duplicate Packet : ").append(messageBlock.SequenceNumber).append("]");
                     }
                  }

                  buf.append(errorSummary);
                  buf.append(" Expected : ").append(expectedSequenceNumber);
                  buf.append(" [Hex : ").append(toHexString(expectedSequenceNumber)).append("]");
                  buf.append("  Got : ").append(messageBlock.SequenceNumber);
                  buf.append(" [Hex : ").append(toHexString(messageBlock.SequenceNumber)).append("]");

                  buf.append(" ### {Previous Seq : ").append(lastSequence);
                  buf.append(" [Hex : ").append(toHexString(lastSequence)).append("]}");

                  buf.append(" Current Session : ").append(messageBlock.SessionNumber);
                  buf.append(" [Hex : ").append(toHexString(messageBlock.SessionNumber)).append("]");;

                  buf.append(" {Previous Session : ");

                  if(previousSession<0)
                  {
                     buf.append("[None]");
                  }
                  else
                  {
                     buf.append(previousSession).append(" [Hex : ").append(toHexString(previousSession)).append("]}");;
                  }
                  buf.append(errorInfo);

                  System.err.println(buf.toString());
               }
            }

            lastSequence=messageBlock.SequenceNumber;
            lastNumMessages=messageBlock.NumOfMessages;

            _multicastClient.printMessage(messageBlock);
         }
         catch(Exception e)
         {
            logger.error("Exception:"+e, e);

         }
      }

   }

   /**
    * The thread's run method
    * Retrieve from the multicast queue and start processing
    */
   public void run()
   {
      receive();
      System.out.println("SimpleMulticastConsumer ["+Thread.currentThread().getName()+"] Exiting...");

   }
   
}

