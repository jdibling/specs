package com.theice.mdf.client.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.*;

import com.theice.mdf.client.multicast.monitor.MulticastMonitor;

/**
 * Created by IntelliJ IDEA.
 * User: binamdar
 * Date: Feb 19, 2008
 * Time: 3:39:28 PM
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * 
 * IntercontinentalExchange, Inc. Copyright IntercontinentalExchange, Inc.
 * 2002, All Rights Reserved.
 */
public class MailThrottler implements Runnable
{
   private BlockingQueue<String> _queue=new LinkedBlockingQueue<String>();

   private boolean _done = false;
   private static int DEFAULT_MAIL_DELAY = 60000;
   private StringBuffer _aggregateException = new StringBuffer();
   private static MailThrottler _this;
   private static final Logger Log = Logger.getLogger(MailThrottler.class);
   private long _lastTrigger;
   private long _mailDelay = DEFAULT_MAIL_DELAY;
   private static int MAX_MESSAGES_TO_THROTTLE=1500;
   private static int SLEEP_INTERVAL=10000;

   private boolean _purgeAfterFirstEmail=false;
   private boolean _sendAllAsWarning=false;

   private MailThrottler()
   {
      // Singleton pattern
      String mailDelay = System.getProperty("com.cpex.util.MailThrottler.mailDelay");
      String purgeAfterFirstEmail = System.getProperty("com.cpex.util.MailThrottler.purgeAfterFirstEmail");
      String maxMessageToThrottle = System.getProperty("com.cpex.util.MailThrottler.maxMessageToThrottle"); 
      String sleepInterval = System.getProperty("com.cpex.util.MailThrottler.sleepInterval");

      if (mailDelay!=null && mailDelay.length()>0)
      {
         try
         {
            _mailDelay = new Integer(mailDelay).longValue();
         }
         catch(Exception ex)
         {
            Log.error("Error parsing MailDelay", ex);
         }
      }

      if (purgeAfterFirstEmail!=null && purgeAfterFirstEmail.equalsIgnoreCase("true"))
      {
         _purgeAfterFirstEmail = true;
      }

      if (maxMessageToThrottle!=null && maxMessageToThrottle.length()>0)
      {
         try
         {
            MAX_MESSAGES_TO_THROTTLE = Integer.parseInt(maxMessageToThrottle);
         }
         catch(Exception ex)
         {
            Log.error("Error parsing MaxMessagesToThrottle", ex);
         }
      }
      
      if (sleepInterval!=null && sleepInterval.length()>0)
      {
         try
         {
            SLEEP_INTERVAL = Integer.parseInt(sleepInterval);
         }
         catch(Exception ex)
         {
            Log.error("Error parsing SleepInterval", ex);
         }
      }

      Log.debug("Mail Delay="+_mailDelay+", PurgeAfterFirstEmail="+_purgeAfterFirstEmail+", MaxMessagesToThrottle="+MAX_MESSAGES_TO_THROTTLE+", SleepInterval="+SLEEP_INTERVAL);
   }

   public static MailThrottler getInstance()
   {
      if (_this == null)
      {
         _this = new MailThrottler();
         _this._lastTrigger = System.currentTimeMillis();
         new Thread(_this).start();
      }

      return _this;
   }

   public void run()
   {
      // Only exit after all current messages have been handled.
      while (!_done || !_queue.isEmpty())
      {
         try
         {
            long timeDiff = Math.abs(System.currentTimeMillis() - _lastTrigger);
            if (!_queue.isEmpty())
            {
               if(timeDiff > _mailDelay)
               {
                  if (_purgeAfterFirstEmail) 
                  {
                     try
                     {
                        ArrayList<String> messagesInQ = new ArrayList<String>();
                        int numOfMessagesInQ = _queue.drainTo(messagesInQ);
                        for (int i=0; i<numOfMessagesInQ; i++)
                        {
                           _aggregateException.append(messagesInQ.get(i));
                           if (i >= MAX_MESSAGES_TO_THROTTLE && numOfMessagesInQ > MAX_MESSAGES_TO_THROTTLE)
                           {
                              int numOfMessageTruncated = numOfMessagesInQ - MAX_MESSAGES_TO_THROTTLE;
                              _aggregateException.append("****** Truncating excessive messages. See logs for more details ("+numOfMessageTruncated+" messages were truncated) ******");
                              break;
                           }
                        }
                     }
                     catch(Exception ex)
                     {
                        _aggregateException.append("Failed to process error messages in queue. Please check the logs for errors immediately.");
                     }
                  }
                  else
                  {
                     int currentMessageAggregateCount=0;
                     int size=_queue.size();
                     for (int i=0;i<size;i++)
                     {
                        String exp = pop();
                        _aggregateException.append(exp);
                        currentMessageAggregateCount++;
                        if (currentMessageAggregateCount >= MAX_MESSAGES_TO_THROTTLE)
                        {
                           break;
                        }
                     }
                  }

                  if (!_sendAllAsWarning)
                  {
                     handleError(_aggregateException.toString() + "\n\n" + MulticastMonitor.getAllClientStatus());
                  }
                  else
                  {
                     handleWarning(_aggregateException.toString() + "\n\n" + MulticastMonitor.getAllClientStatus());
                  }

                  _aggregateException.setLength(0);

                  _lastTrigger = System.currentTimeMillis();
               }
               else
               {
                  delay();
               }
            }
            else
            {
               delay();
            }
         }
         catch (InterruptedException ex)
         {
            ;
         }
         catch (ClassCastException ex)
         {
            ;
         }
      }
   }

   public void delay()
   {
      try
      {
         Thread.sleep(SLEEP_INTERVAL);
      }
      catch (InterruptedException ie)
      {
         ie.printStackTrace();
      }
   }

   public void enqueueError(String ex)
   {
      // New errors are not added after stop() is called.
      if (!_done)
      {
         push(ex);
      }
   }

   private void handleError(String ex)
   {
      sendErrorEmail(ex);
   }

   private void handleWarning(String ex)
   {
      sendWarningEmail(ex);
   }

   /**
    * Flush the queue
    */
   public void flush()
   {
      try
      {
         String ex;
         int size=_queue.size();

         for (int i=0;i<size;i++)
         {
            ex = pop();
            _aggregateException.append(ex);
         }
         handleError(_aggregateException.toString());
      }
      catch (InterruptedException ie)
      {
         ie.printStackTrace();
      }
   }

   public void haltService()
   {
      _done = true;
      flush();
   }

   public void sendErrorEmail(String ex)
   {
      Log.debug(ex);
      Log.error(ex);
   }

   public void sendWarningEmail(String ex)
   {
      Log.debug(ex);
      Log.warn(ex);
   }

   public void push(String ex)
   {
      String divider = "\n============================================================================================\n";

      try
      {
         _queue.put(divider);
         _queue.put(ex);
      }
      catch(InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   private String pop() throws InterruptedException
   {
      String message="";
      message=_queue.take();
      return(message);
   }

   /**
    * Override the purge flag
    * @param flag
    */
   public void setPurgeAfterFirstEmail(boolean flag)
   {
      _purgeAfterFirstEmail=flag;
   }

   public boolean isSendAllAsWarning()
   {
      return _sendAllAsWarning;
   }

   public void setSendAllAsWarning(boolean asWarning)
   {
      _sendAllAsWarning = asWarning;
   }

   /**
    * Inner class for the throttled message 
    * @author Adam Athimuthu
    */
   public class Message
   {
      private Level level=Level.ERROR;
      private String message="";

      private Message()
      {
      }

      public Message(String message)
      {
         this.message=message;
      }

      public Message(String message,Level level)
      {
         this.message=message;
         this.level=level;
      }

      public String getMessage()
      {
         return(this.message);
      }

      public Level getLevel()
      {
         return(this.level);
      }
   }

}

