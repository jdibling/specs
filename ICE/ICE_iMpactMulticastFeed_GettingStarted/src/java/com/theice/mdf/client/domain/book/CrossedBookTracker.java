package com.theice.mdf.client.domain.book;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * CrossedBookTracker is used inside the cache of the monitoring thread, for keeping track
 * of alert emails
 * 
 * @author Adam Athimuthu
 */
public class CrossedBookTracker
{
	private CrossedBookInfo crossedBookInfo=null;
	private boolean alerted=false;
	
	private CrossedBookTracker()
	{
	}
	
	public CrossedBookTracker(CrossedBookInfo crossedBookInfo)
	{
		this.crossedBookInfo=crossedBookInfo;
	}
	
	public CrossedBookInfo getCrossedBookInfo()
	{
		return(this.crossedBookInfo);
	}
	
	public boolean isAlertSent()
	{
		return(alerted);
	}
	
	public void setAlertSent()
	{
		this.alerted=true;
	}
}

