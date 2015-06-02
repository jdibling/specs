package com.theice.mdf.client.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastChannelInfo 
{
    private String _key="";
    private String _channelType="";
    private String _channelName="";
    private EndPointInfo _endPointInfo=null;

    private MulticastChannelInfo()
    {
    }

    public MulticastChannelInfo(String key, String channelType,String channelName,EndPointInfo endPointInfo)
    {
    	this._key=key;
    	this._channelType=channelType;
    	this._channelName=channelName;
    	this._endPointInfo=endPointInfo;
    }
    
    public String getKey()
    {
    	return(this._key);
    }
    
    public String getChannelType()
    {
    	return(this._channelType);
    }
    
    public void setChannelType(String channelType)
    {
    	this._channelType=channelType;
    }
    
    public String getChannelName()
    {
    	return(this._channelName);
    }
    
    public void setChannelName(String channelName)
    {
    	this._channelName=channelName;
    }
    
    public EndPointInfo getEndPointInfo()
    {
    	return(this._endPointInfo);
    }
    
    public void setEndPointInfo(EndPointInfo endPointInfo)
    {
    	this._endPointInfo=endPointInfo;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_key == null) ? 0 : _key.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MulticastChannelInfo))
			return false;
		final MulticastChannelInfo other = (MulticastChannelInfo) obj;
		if (_key == null)
		{
			if (other._key != null)
				return false;
		}
		else if (!_key.equals(other._key))
			return false;
		return true;
	}

	public String toString()
    {
    	StringBuffer buf=new StringBuffer();
    	buf.append("["+_key+"]");
    	buf.append("["+_channelType+"]");
    	buf.append("["+_channelName+"]");
    	buf.append("["+_endPointInfo+"]");
    	return(buf.toString());
    }

}

