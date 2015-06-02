package com.theice.mdf.client.config.domain;

import com.theice.mdf.client.domain.book.MulticastChannelContext;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Multicast Group Information contains the details of the multicast channels
 * for various contexts such as FullOrderDepth, PriceLevel, OptionsTopOfBook
 * 
 * Options markets only support Price Level, so Full Order Depth could be null for those markets
 * 
 * @author Adam Athimuthu
 */
public class MulticastGroupInfo
{
	protected String groupName=null;
	protected MulticastChannelPairInfo fullOrderInfo=null;
	protected MulticastChannelPairInfo priceLevelInfo=null;
	
	private MulticastGroupInfo()
	{
	}
	
	public MulticastGroupInfo(String groupName)
	{
		this.groupName=groupName;
	}
	
	public void setFullOrderInfo(MulticastChannelPairInfo fullOrderInfo)
	{
		this.fullOrderInfo=fullOrderInfo;
	}
	
	public void setPriceLevelInfo(MulticastChannelPairInfo priceLevelInfo)
	{
		this.priceLevelInfo=priceLevelInfo;
	}
	
	public String getGroupName()
	{
		return(this.groupName);
	}
	
	public MulticastChannelPairInfo getFullOrderInfo()
	{
		return(this.fullOrderInfo);
	}
	
	public MulticastChannelPairInfo getPriceLevelInfo()
	{
		return(this.priceLevelInfo);
	}
	
	public MulticastChannelPairInfo getMulticastChannelPairInfo(MulticastChannelContext context)
	{
		MulticastChannelPairInfo channelPairInfo=null;
		
		switch(context)
		{
		case PRICELEVEL:
			channelPairInfo=priceLevelInfo;
			break;
		case FULLORDERDEPTH:
			channelPairInfo=fullOrderInfo;
		default:
			break;
		}
		
		return(channelPairInfo);
	}
	
	public MulticastChannelPairInfo getMulticastChannelPairInfo(String contextName)
	{
		MulticastChannelContext context=MulticastChannelContext.getMulticastChannelContextFor(contextName);
		
		if(context==null)
		{
			context=MulticastChannelContext.FULLORDERDEPTH;
		}
		
		return(getMulticastChannelPairInfo(context));
	}
	
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[Group=").append(groupName).append("]");
		
		buf.append("[FullOrderMulticastPairInfo=");
		if(this.fullOrderInfo!=null)
		{
			buf.append(this.fullOrderInfo.toString());
		}
		else
		{
			buf.append("NotDefined");
		}
		buf.append("]");
		
		buf.append("[PriceLevelMulticastPairInfo=");
		if(this.priceLevelInfo!=null)
		{
			buf.append(this.priceLevelInfo.toString());
		}
		else
		{
			buf.append("NotDefined");
		}
		buf.append("]");

		return(buf.toString());
	}
}

