package com.theice.mdf.client.examples;

import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.UnknownMessageException;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * <code>SimpleClientSocketReader</code> processes the inbound messages from the
 * price feed server and print the debug info out to the console.
 *
 * @author David Chen
 * @since 12/28/2006
 */

public class SimpleClientSocketReader implements Runnable
{
   private DataInputStream _inputStream = null;
   private BlockingQueue _simpleMsgQueue = null;

   public SimpleClientSocketReader(DataInputStream inStream)
   {
      _inputStream = inStream;
      _simpleMsgQueue = new LinkedBlockingQueue();
   }

   public void run()
   {
      try
      {
         while (true)
         {
            try
            {
               // Wait for message from the feed server
               MDMessage msg = RawMessageFactory.getObject(_inputStream);

              // add message to the queue
              _simpleMsgQueue.put(msg);
            }
            catch (UnknownMessageException e)
            {
               // unknown message exception caught, the factory
               // handled reading of the message, log it and move on
               System.out.println(e.getMessage());
            }
         }

      }
      catch (IOException e)
      {
         System.out.println("SimpleClientSocketReader.run: IOException caught");
         e.printStackTrace();
      }
      catch (Throwable e)
      {
         System.out.println("SimpleClientSocketReader.run: Throwable caught");
         e.printStackTrace();
      }

      System.out.println("SimpleClientSocketReader.run: Exit socket reader!!");
   }


    /**
     * Get the next message from the blocking queue
     * @return
     */
   public MDMessage getNextMessage()
   {
      MDMessage mdMsg=null;

      try
      {
          mdMsg = (MDMessage)_simpleMsgQueue.take();
      }
      catch (InterruptedException e)
      {
          e.printStackTrace();
      }

      return mdMsg;
   }
}

