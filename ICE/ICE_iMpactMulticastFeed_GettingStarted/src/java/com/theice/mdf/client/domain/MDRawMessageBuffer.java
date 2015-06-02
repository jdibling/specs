package com.theice.mdf.client.domain;

import com.theice.mdf.message.MDMessage;

import java.util.List;
import java.util.LinkedList;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The Raw message buffer is used to keep the latest messages. The maximum number is currently
 * preset to 1000
 *
 * @author Adam Athimuthu
 * Date: Aug 21, 2007
 * Time: 10:44:02 AM
 */
public class MDRawMessageBuffer
{
    private static MDRawMessageBuffer _instance=new MDRawMessageBuffer();

    /**
     * rolling buffer of log messages
     */
    protected List<MDMessage> _logMessageList= new LinkedList();
    protected int _logMessageMax=1000;
    
    protected long _lastMessageTimeStamp;

    /**
     * get the singleton instance
     * @return
     */
    public static MDRawMessageBuffer getInstance()
    {
        return _instance;
    }

    /**
     * private constructor
     */
    private MDRawMessageBuffer()
    {
    }

    /**
     * Get the log message list
     * @return
     */
    public List<MDMessage> getLogMessageList()
    {
        return(this._logMessageList);
    }

    /**
     * Get the maximum message count
     * @return
     */
    public int getMaxMessageCount()
    {
        return(this._logMessageMax);
    }

    /**
     * Get the last message timestamp
     * @return
     */
    public long getLastMessageTimeStamp()
    {
        return(this._lastMessageTimeStamp);
    }

    /**
     * log the message to an internal buffer
     * @param message
     */
    public void updateLogBuffer(MDMessage message)
    {
        synchronized(_logMessageList)
        {
            if(_logMessageList.size()>_logMessageMax)
            {
                ((LinkedList)_logMessageList).removeFirst();
            }

            ((LinkedList)_logMessageList).addLast(message);
        }
        
        _lastMessageTimeStamp=System.currentTimeMillis();

        return;
    }

}

