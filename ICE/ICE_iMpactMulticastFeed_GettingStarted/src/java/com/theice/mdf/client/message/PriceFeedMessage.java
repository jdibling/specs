package com.theice.mdf.client.message;

import java.util.concurrent.atomic.AtomicLong;

import com.theice.mdf.client.exception.InconsistentStateException;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MDSequencedMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * A wrapper class for the serialized messages for internal message handling.
 * 
 * Decorated with a means to indicate whether a given message is part of a bundle
 * along with a unique bundle sequence number
 * 
 * Since there are two types of message
 * 
 * @author Adam Athimuthu
 */
public class PriceFeedMessage
{
	private MDMessage message=null;
	
	/**
	 * bundle sequence number. non-zero if it is a bundled message 
	 */
	private long bundleSequenceNumber=0L;

	/**
	 * Is the message sequenced?
	 */
	private boolean sequenced=false;
	
	private static AtomicLong uniqueBundleSequenceNumber=new AtomicLong(0);
	
	private PriceFeedMessage()
	{
	}
	
	public PriceFeedMessage(MDMessage message)
	{
		this.message=message;
	}
	
	public PriceFeedMessage(MDSequencedMessage message,long bundleSequenceNumber)
	{
		this.message=message;
		this.sequenced=true;
		this.bundleSequenceNumber=bundleSequenceNumber;
	}
	
	public MDMessage getMessage()
	{
		return(this.message);
	}
	
	public MDSequencedMessage getSequencedMessage() throws InconsistentStateException
	{
		if(sequenced)
		{
			return((MDSequencedMessage) this.message);
		}
		else
		{
			String err=(message!=null)?message.toString():"NULL";
			throw(new InconsistentStateException("Not a sequenced message : "+err));
		}
	}
	
	public boolean isBundled()
	{
		return(bundleSequenceNumber>0);
	}
	
	public boolean isSequenced()
	{
		return(this.sequenced);
	}
	
	public long getBundleSequenceNumber()
	{
		return(this.bundleSequenceNumber);
	}

	public static long generateBundleSequenceNumber()
	{
		return(uniqueBundleSequenceNumber.incrementAndGet());
	}
	
    public String toString()
    {
    	StringBuffer buf=new StringBuffer("PriceFeedMessage=");
    	buf.append("[Sequenced? ").append(this.sequenced).append("] ");
    	
    	if(isBundled())
    	{
        	buf.append("[BundleSeqNo=").append(this.bundleSequenceNumber).append("]");
    	}
    	else
    	{
        	buf.append("[NotBundled]");
    	}
    	
    	buf.append("[").append(message.toString()).append("] ");
    	
    	return(buf.toString());
    }
}

