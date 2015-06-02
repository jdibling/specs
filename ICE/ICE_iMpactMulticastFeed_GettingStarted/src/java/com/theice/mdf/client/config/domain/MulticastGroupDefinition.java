package com.theice.mdf.client.config.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Multicast Group Definition contains the group name and whether it is options
 * 
 * @author Adam Athimuthu
 */
public class MulticastGroupDefinition
{
	protected String groupName=null;
	protected boolean isOptions=false;
	
	private MulticastGroupDefinition()
	{
	}
	
	public MulticastGroupDefinition(String groupName)
	{
		this.groupName=groupName;
	}
	
	public MulticastGroupDefinition(String groupName,boolean isOptions)
	{
		this.groupName=groupName;
		this.isOptions=isOptions;
	}

	public String getGroupName()
	{
		return(this.groupName);
	}
	
	public boolean isOptions()
	{
		return(this.isOptions);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((groupName == null) ? 0 : groupName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof MulticastGroupDefinition))
			return false;
		final MulticastGroupDefinition other = (MulticastGroupDefinition) obj;
		if(groupName == null)
		{
			if(other.groupName != null)
				return false;
		}
		else if(!groupName.equals(other.groupName))
			return false;
		return true;
	}

	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[MultiastGroup=").append(groupName).append("]");
		buf.append("[isOptions=").append(this.isOptions).append("]");
		return(buf.toString());
	}
}

