package com.theice.mdf.client.multicast.handler;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.multicast.dispatcher.MDFMulticastDispatcher;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastClientConfig 
{
    private EndPointInfo _endPoint=null;
    private String _networkInterface=null;
    private MDFMulticastDispatcher _dispatcher=null;
    private String _name="Default";

    private MulticastClientConfig()
    {
    }

    public MulticastClientConfig(EndPointInfo endPoint,String networkInterface,String name,MDFMulticastDispatcher dispatcher)
    {
    	this._endPoint=endPoint;
    	this._networkInterface=networkInterface;
    	this._dispatcher=dispatcher;
    	this._name=name;
    }
    
    public EndPointInfo getMulticastEndPoint()
    {
    	return(_endPoint);
    }
    
    public String getIpAddress()
    {
    	return(_endPoint.getIpAddress());
    }
    
    public int getPort()
    {
    	return(_endPoint.getPort());
    }
    
    public String getName()
    {
    	return(_name);
    }
    
    public String getNetworkInterface()
    {
    	return(_networkInterface);
    }
    
    public MDFMulticastDispatcher getDispatcher()
    {
    	return(_dispatcher);
    }
    
    public String toString()
    {
    	StringBuffer buf=new StringBuffer();
    	buf.append("["+_endPoint.toString()+"]");
    	buf.append("["+_name+"]");
    	buf.append("["+_networkInterface+"]");
    	buf.append("["+_dispatcher.toString()+"]");
    	return(buf.toString());
    }


}

