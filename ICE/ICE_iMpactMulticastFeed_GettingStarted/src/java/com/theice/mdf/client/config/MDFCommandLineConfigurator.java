package com.theice.mdf.client.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.theice.mdf.client.config.domain.ConfigurationSelectorInfo;
import com.theice.mdf.client.exception.ConfigurationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * The command line configurator is used to select the multicast client configuration from
 * the repository, without having to launch a dialog box. Used with command line (non-GUI)
 * clients.
 * 
 * System Parameters
 * 
 * -Denvironment (apitest, perftest etc.)
 * -DmulticastGroup (The actual group name as defined in the xml file)
 * -Dcontext (fullOrderDepth, priceLevel...)
 * 
 * System parameters
 * 
 * @author Adam Athimuthu
 */
public class MDFCommandLineConfigurator
{
    private static final Logger logger=Logger.getLogger(MDFCommandLineConfigurator.class.getName());

	private static final String KEY_ENVIRONMENT="environment";
	private static final String KEY_MULTICAST_GROUP="multicastGroup";
	private static final String KEY_CONTEXT="context";

	public static MDFCommandLineConfigurator instance=new MDFCommandLineConfigurator();
	
	private static final String DEFAULT_ENVIRONMENT="apitest";
	private static final String DEFAULT_MULTICAST_GROUP="ICE Futures Europe Oil";
	private static final String DEFAULT_CONTEXT="FullOrderDepth";

	private static final String formatInitMessage="CommandLineConfigurator Initialized={0}";
	
	static
	{
	}
	   
	private MDFCommandLineConfigurator()
	{
	}
	
	public static MDFCommandLineConfigurator getInstance()
	{
		return(instance);
	}
	
	/**
	 * Get configuration information from the environment
	 * @param enableDefaults, if set, internal defaults will be enabled. Otherwise this module fully relies on the system
	 * environment variables for initialization. If the enableDefaults is set to false and if there are no system variables
	 * supplied, then the configuration selector will be null
	 * @return
	 * @throws ConfigurationException
	 */
	public ConfigurationSelectorInfo obtainConfigurationSelector(boolean enableDefaults) throws ConfigurationException
	{
		String environment=null;
		String multicastGroup=null;
		String context=null;
		
		ConfigurationSelectorInfo selectorInfo=null;
		
		if(enableDefaults)
		{
			environment=getProperty(KEY_ENVIRONMENT,DEFAULT_ENVIRONMENT);
			multicastGroup=getProperty(KEY_MULTICAST_GROUP,DEFAULT_MULTICAST_GROUP);
			context=getProperty(KEY_CONTEXT,DEFAULT_CONTEXT);
		}
		else
		{
		   System.out.println("Attempting to load configuration from the system environment.");
		   environment=getProperty(KEY_ENVIRONMENT,null);
			multicastGroup=getProperty(KEY_MULTICAST_GROUP,null);
			context=getProperty(KEY_CONTEXT,null);
		}

		if(environment==null || multicastGroup==null || context==null)
		{
			String buf="No information available to select configuration.";
			logger.warn(buf);
			throw(new ConfigurationException(buf));
		}
		
		List<String> multicastGroups = new ArrayList<String>();
		String[] multicastGroupArray = multicastGroup.split("\\|");
		for (String groupName : multicastGroupArray)
		{
		   multicastGroups.add(groupName);
		}
		selectorInfo=new ConfigurationSelectorInfo(environment,multicastGroups,context);
		String buf=MessageFormat.format(formatInitMessage,new Object[]{selectorInfo.toString()}); 
		logger.info(buf);
		
		return(selectorInfo);
	}
	
	/**
	 * Generic method to get a string property from the system environment. If not defined, return default.
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private static String getProperty(String property, String defaultValue)
	{
		String value=System.getProperty(property);
		
		StringBuffer buf=new StringBuffer(property).append(" : ");

		if(null==value)
		{
			buf.append(" Not defined. Using default : ");
			value=defaultValue;
		}

		buf.append(value);
		logger.info(buf.toString());

		return(value);
	}
	
	public static void main(String[] args) throws Exception
	{
		ConfigurationSelectorInfo selectorInfo=MDFCommandLineConfigurator.getInstance().obtainConfigurationSelector(false);
		
		if(selectorInfo!=null)
		{
			System.err.println(selectorInfo.toString());
		}
		else
		{
			System.err.println("No parameters supplied in the environment. Defaults disabled.");
		}
		
		return;
	}
	
}

