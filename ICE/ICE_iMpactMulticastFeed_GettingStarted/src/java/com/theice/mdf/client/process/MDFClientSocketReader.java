package com.theice.mdf.client.process;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.UnknownMessageException;
import com.theice.mdf.client.util.MDFUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * <code>MDFClientSocketReader</code> processes the inbound messages from the price feed server.
 * A version of the MDFClientSocketReader to support the GUI client
 * 
 * @author David Chen
 * @author Adam Athimuthu
 * @since 08/28/2007
 */
public class MDFClientSocketReader implements Runnable
{
    private DataInputStream _inputStream=null;
    private BlockingQueue<MDMessage> _simpleMsgQueue=null;

    private final Logger logger=Logger.getLogger(MDFClientSocketReader.class.getName());

    public static int POLLING_TIMEOUT_MILLISECONDS=100;
    
    /**
     * Flag indicating whether the reader thread is alive
     * This will be a signal for the consumer threads to exit cleanly
     */
    private boolean readerThreadIsAlive=true;

    public MDFClientSocketReader(DataInputStream inStream)
    {
        _inputStream=inStream;
        _simpleMsgQueue=new LinkedBlockingQueue<MDMessage>();
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
                    MDMessage msg=RawMessageFactory.getObject(_inputStream);

                    // add message to the queue
                    _simpleMsgQueue.put(msg);
                }
                catch (UnknownMessageException e)
                {
                    // unknown message exception caught, the factory
                    // handled reading of the message, log it and move on
                    logger.error(e.getMessage());
                }
            }

        }
        catch(IOException e)
        {
            logger.error("MDFClientSocketReader.run: IOException caught"+ MDFUtil.getStackInfo(e));
            e.printStackTrace();
        }
        catch(Throwable e)
        {
            logger.error("MDFClientSocketReader.run: Throwable caught : "+ MDFUtil.getStackInfo(e));
            e.printStackTrace();
        }

        readerThreadIsAlive=false;

        String exitMessage="MDFClientSocketReader.run: Exit socket reader!!";
        logger.info(exitMessage);
        System.out.println(exitMessage);
        
        return;
    }

    public boolean isAlive()
    {
    	return(this.readerThreadIsAlive);
    }

    /**
     * Get the next message from the blocking queue
     *
     * @return
     */
    public MDMessage getNextMessage()
    {
        MDMessage mdMsg=null;

        try
        {
            mdMsg=(MDMessage)_simpleMsgQueue.poll(POLLING_TIMEOUT_MILLISECONDS,TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return(mdMsg);
    }
}

