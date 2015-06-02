package com.theice.mdf.client.config.domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.util.MDFUtil;

/**
/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The selected configuration specific to:
 * 
 * - an environment
 * - group definition
 * - context
 * 
 * @author Adam Athimuthu
 */
public class MDFClientConfiguration
{
	protected String envName=null;

	protected Map<String, MulticastGroupDefinition> multicastGroupDefinitionMap=null;
	
	protected TCPConnectionInfo tcpConfig=null;
	
	protected MDFClientRuntimeParameters parameters=new MDFClientRuntimeParameters();
	
	protected Map<String, MulticastChannelPairInfo> multicastChannelPairInfoMap=null;
	
	/**
	 * List of interested market types
	 */
	protected Map<String, List<MarketType>> interestedMarketTypesMap=new Hashtable<String, List<MarketType>>();
	
	protected boolean isInterestedInUDS = false;
	
	protected MDFClientConfiguration()
	{
		super();
	}

	public MDFClientConfiguration(String envName, Map<String, MulticastGroupDefinition> multicastGroupDefinitionMap,TCPConnectionInfo tcpConfig)
	{
		this.envName=envName;
		this.multicastGroupDefinitionMap=multicastGroupDefinitionMap;
		this.tcpConfig=tcpConfig;
	}

	public MDFClientConfiguration(String envName, Map<String, MulticastGroupDefinition> multicastGroupDefinitionMap, EndPointInfo tcpInfo,String userName,String password)
	{
		this(envName,multicastGroupDefinitionMap,new TCPConnectionInfo(tcpInfo,userName,password));
	}
	
	public String getEnvironment()
	{
		return(this.envName);
	}
	
	public List<String> getInterestedMulticastGroupNames()
	{
	   ArrayList<String> groupNames = new ArrayList<String>();
	   groupNames.addAll(this.multicastGroupDefinitionMap.keySet());
	   
	   return groupNames;
	}
	
	public Map<String, MulticastGroupDefinition> getMulticastGroupDefinitionMap()
	{
	   return this.multicastGroupDefinitionMap;
	}
	
	public MulticastGroupDefinition getMulticastGroupDefinition(String groupName)
	{
		return(this.multicastGroupDefinitionMap.get(groupName));
	}
	
	public TCPConnectionInfo getTcpInfo()
	{
		return(this.tcpConfig);
	}
	
	public MDFClientRuntimeParameters getMDFClientRuntimeParameters()
	{
		return(this.parameters);
	}
	
	public void setMDFClientRuntimeParameters(MDFClientRuntimeParameters parameters)
	{
		this.parameters=parameters;
	}
	
	public void setMulticastChannelPairInfoMap(Map<String, MulticastChannelPairInfo> multicastChannelPairMap)
	{
		this.multicastChannelPairInfoMap=multicastChannelPairMap;
	}
	
	public Map<String, MulticastChannelPairInfo> getMulticastChannelPairInfoMap()
	{
		return(this.multicastChannelPairInfoMap);
	}
	
	public MulticastChannelPairInfo getMulticastChannelPairInfo(String groupName)
	{
	   return this.multicastChannelPairInfoMap.get(groupName);
	}
	
   public Map<String, List<MarketType>> getInterestedMarketTypesMap()
   {
      return this.interestedMarketTypesMap;
   }
   
	public List<MarketType> getInterestedMarketTypes(String groupName)
   {
    	return(this.interestedMarketTypesMap.get(groupName));
   }
    
    public short[] getInterestedMarketTypeCodes(String groupName)
    {
       List<MarketType> interestedMarketTypes = interestedMarketTypesMap.get(groupName);
       short[] marketTypeCodes=new short[interestedMarketTypes.size()];
    	
    	for(int index=0;index<interestedMarketTypes.size();index++)
    	{
    		marketTypeCodes[index]=Short.valueOf(interestedMarketTypes.get(index).getMarketTypeCode());
    	}

    	return(marketTypeCodes);
    }
    
    public void setInterestedMarketTypesMap(Map<String, List<MarketType>> interestedMarketTypesMap)
    {
    	if(interestedMarketTypesMap!=null)
    	{
        	this.interestedMarketTypesMap=interestedMarketTypesMap;
    	}
    }
    
    public void setInterestedMarketTypes(String groupName, List<MarketType> interestedMarketTypes)
    {
       this.interestedMarketTypesMap.put(groupName, interestedMarketTypes);
    }
    
    public boolean isInterestedInUDS()
    {
       return isInterestedInUDS;
    }
    
    public void setIsInterestedInUDS(boolean value)
    {
       isInterestedInUDS = value;
    }
    
	public String toString()
	{
		StringBuffer buf=new StringBuffer("MDF Client Configuration Selected : ").append(MDFUtil.linefeed);
		buf.append("[EnvName=").append(this.envName).append("]").append(MDFUtil.linefeed);
		buf.append("[TcpInfo=").append(this.tcpConfig.toString()).append("]").append(MDFUtil.linefeed);
		buf.append("[MulticastGroupDefinition=").append(this.multicastGroupDefinitionMap.toString()).append("]").append(MDFUtil.linefeed);
		buf.append("[Parameters=").append(this.parameters.toString()).append("]").append(MDFUtil.linefeed);
		buf.append("[MulticastGroup=").append(this.multicastChannelPairInfoMap.toString()).append("]").append(MDFUtil.linefeed);
		buf.append("[InterestedMarketTypes=").append(this.interestedMarketTypesMap.toString()).append("]").append(MDFUtil.linefeed);
		return(buf.toString());
	}
}

