package com.theice.mdf.client.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class EndPointInfo 
{
    private String _ipAddress="";
    private int _port=9999;

    private EndPointInfo()
    {
    }

    public EndPointInfo(String ipAddress,int port)
    {
    	this._ipAddress=ipAddress;
    	this._port=port;
    }
    
    public EndPointInfo(EndPointInfo endPointInfo)
    {
    	this._ipAddress=endPointInfo._ipAddress;
    	this._port=endPointInfo._port;
    }
    
    public String getIpAddress()
    {
    	return(this._ipAddress);
    }
    
    public void setIpAddress(String ipAddress)
    {
    	this._ipAddress=ipAddress;
    }
    
    public int getPort()
    {
    	return(_port);
    }
    
    public void setPort(int port)
    {
    	this._port=port;
    }
    
    public String getDisplayable()
    {
    	return(_ipAddress+":"+_port);
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_ipAddress == null) ? 0 : _ipAddress.hashCode());
		result = prime * result + _port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EndPointInfo other = (EndPointInfo) obj;
		if (_ipAddress == null) {
			if (other._ipAddress != null)
				return false;
		} else if (!_ipAddress.equals(other._ipAddress))
			return false;
		if (_port != other._port)
			return false;
		return true;
	}

	public String toString()
    {
    	StringBuffer buf=new StringBuffer();
    	buf.append("["+_ipAddress+"]");
    	buf.append("["+_port+"]");
    	return(buf.toString());
    }

}

