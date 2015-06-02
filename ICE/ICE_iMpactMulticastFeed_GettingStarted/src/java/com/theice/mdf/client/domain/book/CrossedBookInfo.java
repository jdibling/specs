package com.theice.mdf.client.domain.book;

import com.theice.mdf.client.domain.transaction.Transaction;
import com.theice.mdf.client.exception.InconsistentStateException;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Contains information on a crossed book condition
 * start/end times and the top of the book information on the price levels
 * when highest bid > lowest offer
 * 
 * if end time is -1, it indicates that the condition currently exists
 * 
 * Crossed book could be triggered by a message within a bundle and briefly exist
 * until another message is processed (from the same bundle) that clears it. Usually it is not a
 * problem as this lasts only a few milliseconds (or less). This condition could be avoided, if the
 * client can treat and process the bundle as a single transaction
 * 
 * Also, when a crossed book is triggered, it is usually due to an Add/Modify Order
 * When it clears, it could be due to a delete/trade or a modify
 * 
 * This is used to check if the crossed book was cleared inside or outside from the bundle
 * A bundle can hold messages pertaining to multiple markets
 * 
 * @author Adam Athimuthu
 */
public class CrossedBookInfo
{
	private int marketId=0;
	private String marketDesc=null;
    private Long topBid=null;
    private Long topOffer=null;

    private Transaction causedBy=null;
    private Transaction clearedBy=null;
    
    private long startTime=-1L;
    private long endTime=-1L;
    
    /**
     * Complete book details at the time of crossed book occurrence
     */
    private String allBidDetailsAtOccurrence="";
    private String allOfferDetailsAtOccurrence="";

	private CrossedBookInfo()
    {
    }
    
    public CrossedBookInfo(Transaction transaction,String marketDesc,Long topBid,Long topOffer)
    {
    	this.marketId=transaction.getMarketId();
    	this.marketDesc=marketDesc;
    	this.topBid=topBid;
    	this.topOffer=topOffer;
    	this.causedBy=transaction;
    	this.startTime=System.currentTimeMillis();
    }
    
    public int getMarketId()
    {
    	return(this.marketId);
    }

    public long getStartTime()
    {
    	return(this.startTime);
    }
    
    public void setStartTime(long startTime)
    {
    	this.startTime=startTime;
    }

    public long getEndTime()
    {
    	return(this.endTime);
    }
    
    public void setEndTime(long endTime)
    {
    	this.endTime=endTime;
    }
    
    public Long getTopBid()
    {
    	return(this.topBid);
    }

    public void setTopBid(Long topBid)
    {
    	this.topBid=topBid;
    }

    public Long getTopOffer()
    {
    	return(this.topOffer);
    }

    public void setTopOffer(Long topOffer)
    {
    	this.topOffer=topOffer;
    }
    
    public Transaction getCausedBy()
    {
    	return(this.causedBy);
    }
    
    public void setClearedBy(Transaction clearedBy)
    {
    	this.clearedBy=clearedBy;
    }
    
    public Transaction getClearedBy()
    {
    	return(this.clearedBy);
    }
    
    public boolean causedByABundleMessage()
    {
    	boolean causedByABundle=false;
    	
    	if(this.causedBy!=null)
    	{
        	causedByABundle=(causedBy.getBundleSequenceNumber()>0);
    	}
    	
    	return(causedByABundle);
    }
    
    /**
     * How long the cross book condition has lasted before being cleared
     * @return
     */
    public long lastedForMilliseconds()
    {
    	long howLong=0L;
    	
    	howLong=getEndTime()-getStartTime();
    	
    	if(howLong<0)
    	{
    		howLong=0L;
    	}
    	
    	return(howLong);
    }
    
    public String getAllBidDetailsAtOccurrence()
    {
    	return(this.allBidDetailsAtOccurrence);
    }
    
    public void setAllBidDetailsAtOccurrence(String details)
    {
    	this.allBidDetailsAtOccurrence=details;
    }
    
    public String getAllOfferDetailsAtOccurrence()
    {
    	return(this.allOfferDetailsAtOccurrence);
    }

    public void setAllOfferDetailsAtOccurrence(String details)
    {
    	this.allOfferDetailsAtOccurrence=details;
    }

    /**
     * Check if the crossed book condition occurred and cleared within a bundle
     * @return
     * @throws InconsistentStateException if the crossed book not yet cleared 
     */
    public boolean hasClearedInsideABundle() throws InconsistentStateException
    {
    	boolean insideBundle=false;
    	
    	if(this.causedBy==null || this.clearedBy==null)
    	{
    		throw(new InconsistentStateException("Crossed book not cleared yet."));
    	}
    	
    	long causedByBundle=this.causedBy.getBundleSequenceNumber();
    	long clearedByBundle=this.clearedBy.getBundleSequenceNumber();

    	if(causedByBundle!=0 && causedByBundle==clearedByBundle)
		{
    		insideBundle=true;
		}
    	
    	return(insideBundle);
    }

    public String toString()
    {
    	long causedByBundle=0L;
    	long clearedByBundle=0L;

    	StringBuffer buf=new StringBuffer("CrossedBook - ");
    	buf.append("Market=[").append(this.marketId).append("] ");
    	buf.append("[").append(this.marketDesc).append("] ");
    	buf.append("TopBid=[").append(this.topBid).append("] ");
    	buf.append("TopOffer=[").append(this.topOffer).append("] ");
    	
    	buf.append("Start=[");
    	if(this.startTime>0)
    	{
        	buf.append(MDFUtil.dateFormat.format(this.startTime));
    	}
    	buf.append("] ");
    	
    	buf.append("End=[");
    	if(this.endTime>0)
    	{
        	buf.append(MDFUtil.dateFormat.format(this.endTime));
    	}
    	buf.append("] ");
    	
    	buf.append("CausedBy=[");
    	if(this.causedBy!=null)
    	{
        	buf.append(this.causedBy.toString());
        	causedByBundle=this.causedBy.getBundleSequenceNumber();
    	}
    	buf.append("] ");

    	if(this.clearedBy!=null)
    	{
        	buf.append("ClearedBy=[");
        	buf.append(this.clearedBy.toString());
        	buf.append("] ");
        	clearedByBundle=this.clearedBy.getBundleSequenceNumber();
        	
        	StringBuffer howCleared=new StringBuffer(" ### ");
        	if(causedByBundle!=0 && clearedByBundle!=0)
        	{
        		if(causedByBundle==clearedByBundle)
        		{
            		howCleared.append("Caused/Cleared within the same bundle");
        		}
        		else
        		{
            		howCleared.append("Caused/Cleared from different bundles");
        		}
        	}
        	else
        	{
        		howCleared.append("Caused/Cleared outside of bundles");
        	}
        	
        	buf.append(howCleared);
        	
        	buf.append(" ### [LastedFor : ").append(lastedForMilliseconds()).append(" ms]");
    	}
    	else
    	{
    		buf.append("### [ConditionNotCleared]");
    	}

    	return(buf.toString());
    }

}

