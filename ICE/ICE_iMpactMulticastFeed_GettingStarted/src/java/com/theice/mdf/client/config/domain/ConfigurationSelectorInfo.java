package com.theice.mdf.client.config.domain;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class ConfigurationSelectorInfo
{
	private String environment=null;
	private List<String> multicastGroups=null;
	private String context=null;

	private static final String formatConfigSelectorInfo="Environment=[{0}] MulticastGroup(s)={1} Context=[{2}]";
	
	private ConfigurationSelectorInfo()
	{
	}
	
	public ConfigurationSelectorInfo(String environment,List<String> multicastGroups,String context)
	{
		this.environment=environment;
		this.multicastGroups=multicastGroups;
		this.context=context;
	}
	
	public String getEnvironment()
	{
		return(this.environment);
	}
	
	public List<String> getMulticastGroups()
	{
		return(this.multicastGroups);
	}
	
	public String getContext()
	{
		return(this.context);
	}
	
	public String toString()
	{
		String buf=MessageFormat.format(formatConfigSelectorInfo,new Object[]{this.environment,this.multicastGroups,this.context}); 
		return(buf);
	}
	
	public static void main(String[] args)
	{
	   List<String> groups = new ArrayList<String>(2);
	   groups.add("ICE Futures Europe Non Oil");
	   groups.add("ICE Futures Europe Oil");
	   ConfigurationSelectorInfo selectInfo = new ConfigurationSelectorInfo("Prod",groups,"PriceLevel");
	   System.out.println("ConfigurationSelectorInfo: "+selectInfo.toString());
	}
	
}
