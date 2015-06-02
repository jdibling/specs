package com.theice.mdf.client.config.domain;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Configuration repository for a specific environment
 * 
 * @author Adam Athimuthu
 */
public class MDFClientEnvConfigRepository
{
	protected String envName=null;
	protected TCPConnectionInfo tcpConfig=null;

	/**
	 * Map keyed by the Group Name
	 */
	protected Map<String,MulticastGroupInfo> multicastGroupsMap=new HashMap<String,MulticastGroupInfo>();
	
	/**
	 * List of interested market types
	 */
	protected Map<String, List<MarketType>> interestedMarketTypesMap=new Hashtable<String, List<MarketType>>();
	
	protected MDFClientEnvConfigRepository()
	{
	}

	public MDFClientEnvConfigRepository(String envName,TCPConnectionInfo tcpConfig)
	{
		this.envName=envName;
		this.tcpConfig=tcpConfig;
	}

	public MDFClientEnvConfigRepository(String envName,EndPointInfo tcpInfo,String userName,String password)
	{
		this(envName,new TCPConnectionInfo(tcpInfo,userName,password));
	}
	
	public String getEnvironment()
	{
		return(this.envName);
	}
	
	public TCPConnectionInfo getTcpInfo()
	{
		return(this.tcpConfig);
	}
	
	public void setMulticastGroupsMap(Map<String,MulticastGroupInfo> multicastGroupsMap)
	{
		this.multicastGroupsMap=multicastGroupsMap;
	}
	
	public Map<String,MulticastGroupInfo> getMulticastGroupsMap()
	{
		return(this.multicastGroupsMap);
	}

	/**
	 * get the Multicast Group Information for the given group
	 * @param groupName
	 * @return
	 */
	public MulticastGroupInfo getMulticastGroup(String groupName)
	{
		return(multicastGroupsMap.get(groupName));
	}

	/**
	 * Get multicast channel pair info for the given group and the context name
	 * @param groupName
	 * @param contextName
	 * @return
	 */
	public Map<String, MulticastChannelPairInfo> getMulticastChannelPairInfoMap(List<String> groupNames,String contextName)
	{
		Map<String, MulticastChannelPairInfo> channelPairMap=new HashMap<String,MulticastChannelPairInfo>();
		for(String groupName : groupNames)
		{
		   MulticastGroupInfo group=getMulticastGroup(groupName);
		   if(group!=null)
		   {
		      channelPairMap.put(groupName, group.getMulticastChannelPairInfo(contextName));
		   }
		}
		
		return channelPairMap;
	}
	
    public List<MarketType> getInterestedMarketTypes(String groupName)
    {
    	return(this.interestedMarketTypesMap.get(groupName));
    }
    
    /**
     * set interested market types depending on what has been checked in the dialog panel
     * corresponding to the multicast group
     * Must be called prior to launching the app. Also, the list can't be empty
     * 
     * @param interestedMarketTypes
     */
    public void setInterestedMarketTypesMap(Map<String, List<MarketType>> interestedMarketTypesMap)
    {
    	this.interestedMarketTypesMap=interestedMarketTypesMap;
    }
    
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[EnvName=").append(this.envName).append("]").append(MDFUtil.linefeed);
		buf.append("[TcpInfo=").append(this.tcpConfig.toString()).append("]").append(MDFUtil.linefeed);
		buf.append("[InterestedMarketTypes=").append(this.interestedMarketTypesMap.toString()).append("]").append(MDFUtil.linefeed);
		buf.append("[MulticastGroups=").append(this.multicastGroupsMap.toString()).append("]");
		return(buf.toString());
	}
}

