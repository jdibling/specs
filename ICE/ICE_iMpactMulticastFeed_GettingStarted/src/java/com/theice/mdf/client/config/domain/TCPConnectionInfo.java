package com.theice.mdf.client.config.domain;

import com.theice.mdf.client.domain.EndPointInfo;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TCP Connection Config
 * 
 * @author Adam Athimuthu
 */
public class TCPConnectionInfo
{
	protected EndPointInfo tcpEndPointInfo=null;
	protected String userName=null;
	protected String password=null;

	private TCPConnectionInfo()
	{
	}

	public TCPConnectionInfo(EndPointInfo tcpInfo,String userName,String password)
	{
		this.tcpEndPointInfo=tcpInfo;
		this.userName=userName;
		this.password=password;
	}
	
	public String getUserName()
	{
		return(this.userName);
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getPassword()
	{
		return(this.password);
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public EndPointInfo getEndPointInfo()
	{
		return(this.tcpEndPointInfo);
	}
	
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[TCPInfo=").append(this.tcpEndPointInfo.toString()).append("]");
		buf.append("[UserName=").append(userName).append("]");
		buf.append("[Password=").append(password).append("]");
		return(buf.toString());
	}
}

