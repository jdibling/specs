package com.theice.mdf.client.config.domain;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.book.MulticastChannelContext;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Multicast Channel Pair Information has the details of the pair of multicast channels
 * (Live and Snapshot) for a given context
 * 
 * @author Adam Athimuthu
 */
public class MulticastChannelPairInfo
{
	protected String groupName=null;
	protected MulticastChannelContext multicastContext=null;
	protected EndPointInfo snapshotEndPoint=null;
	protected EndPointInfo liveEndPoint=null;
	
	private MulticastChannelPairInfo()
	{
	}
	
	public MulticastChannelPairInfo(String groupName,MulticastChannelContext multicastContext,EndPointInfo snapshotEndPoint,EndPointInfo liveEndPoint)
	{
		this.groupName=groupName;
		this.multicastContext=multicastContext;
		this.snapshotEndPoint=snapshotEndPoint;
		this.liveEndPoint=liveEndPoint;
	}
	
	public String getGroupName()
	{
		return(this.groupName);
	}
	
	public MulticastChannelContext getMulticastChannelContext()
	{
		return(this.multicastContext);
	}
	
	public EndPointInfo getLiveEndPoint()
	{
		return(this.liveEndPoint);
	}
	
	public EndPointInfo getSnapshotEndPoint()
	{
		return(this.snapshotEndPoint);
	}
	
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[Group=").append(groupName).append("]");
		buf.append("[Context=").append(multicastContext).append("]");
		
		buf.append("[SnapshotEndPoint=");
		if(this.snapshotEndPoint!=null)
		{
			buf.append(this.snapshotEndPoint.toString());
		}
		else
		{
			buf.append("NotDefined");
		}
		buf.append("]");

		buf.append("[LiveEndPoint=");
		if(this.liveEndPoint!=null)
		{
			buf.append(this.liveEndPoint.toString());
		}
		else
		{
			buf.append("NotDefined");
		}
		buf.append("]");
		
		return(buf.toString());
	}
}

