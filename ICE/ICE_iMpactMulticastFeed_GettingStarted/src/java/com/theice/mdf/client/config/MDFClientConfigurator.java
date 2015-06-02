package com.theice.mdf.client.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.theice.mdf.client.config.domain.ConfigurationSelectorInfo;
import com.theice.mdf.client.config.domain.CrossedBookDetectionInfo;
import com.theice.mdf.client.config.domain.MDFClientConfigRepository;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.config.domain.MDFClientEnvConfigRepository;
import com.theice.mdf.client.config.domain.MDFClientRuntimeParameters;
import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;
import com.theice.mdf.client.config.domain.MulticastGroupDefinition;
import com.theice.mdf.client.config.domain.MulticastGroupInfo;
import com.theice.mdf.client.config.helper.ConfigLoaderHelper;
import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.exception.ConfigurationException;
import com.theice.mdf.client.exception.InitializationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Feed Handler Configurator
 * 
 * <multicastClientConfig>
 * 
 * 	<environment name="apitest">
 * 		<tcp ip="12.129.93.133" port="2000"
 * 			userName="YourTestId"
 * 			password="YourTestPassword"/>
 *
 * 		<multicast>
 * 			<group name="ICE Futures US Agricultures">
 * 				<fullOrderDepth>
 * 					<live ip="233.156.208.0" port="20000"/>
 * 					<snapshot ip="233.156.208.1" port="20001"/>
 * 				</fullOrderDepth>
 * 				<priceLevel>
 * 					<live ip="233.156.208.2" port="20002"/>
 * 					<snapshot ip="233.156.208.3" port="20003"/>
 * 				</priceLevel>
 * 			</group>
 * 		</multicast>
 *  </environment>
 *
 *	<parameters>
 *		<!--	
 *			Sequence Problem Action (optional). Values: shutdown (default), restart
 *			<sequenceProblemAction>shutdown</sequenceProblemAction>
 *		-->
 *		<!--
 *			Multicast Network Interface (optional).
 *			If not set, the default interface will be used by the network library
 *			<multicastNetworkInterface>127.0.0.1</multicastNetworkInterface>
 *		-->
 *		<!--
 *			Multicast Inactivity Threshold in milliseconds (optional). If not set, the default=0 (infinite)
 *		-->
 *		<multicastInactivityThreshold>30000</multicastInactivityThreshold>
 *		<!--
 *			Crossed Book Detection (optional). Values: false (default), true
 *			If this tag is present, then crossed book detection will be enabled
 *			Optionally, certain market types can be included for delayed crossed book alerts. Delayed alerts are typically
 *				used for OTC and Crack market types
 *		-->
 *		<detectCrossedBook>
 *			<crossedBookDelayedAlert>
 *				<marketTypes>
 *					<marketType code="0" />
 *					<marketType code="1" />
 *				</marketTypes>
 *				<delayedAlertThreshold>120000</delayedAlertThreshold>
 *				<delayedAlertMonitoringInterval>1000</delayedAlertMonitoringInterval>
 *			</crossedBookDelayedAlert>
 *		</detectCrossedBook>
 *	</parameters>
 *
 *	<!--
 *		List of all market types with their corresponding multicast groups
 *		The multicast group name must match what has been specified in the multicast tag
 *		More than one market type may be mapped to a multicast group channel
 *		Refer to the spec document, Section Appendix C: Supported Market Types
 *	-->
 *	<allMarketTypes>
 *		<marketType code="0" name="Financial Gas">
 *			<multicastGroups name="ICE OTC"/>
 *		</marketType>
 *		... 
 *		<marketType code="26" name="Canadian Oilseeds">
 *			<multicastGroup name="ICE Futures Canada"/>
 *			<multicastGroup name="ICE Futures Canada Options"/>
 *		</marketType>
 *		<...........>
 *	</allMarketTypes>
 *
 *<InterestedMulticastGroups>
      <multicastGroup name="ICE Futures Europe Non Oil"/>
      <multicastGroup name="ICE Futures Europe Oil"/>
   </InterestedMulticastGroups>

 *   
 * </multicastClientConfig>
 * 
 * @author Adam Athimuthu
 *
 */
public class MDFClientConfigurator 
{
    private static final Logger logger=Logger.getLogger(MDFClientConfigurator.class.getName());

    public static final String MULTICAST_GROUP_NAME_OPTIONS_KEY_WORD = "Options";
    public static final String DEFAULT_CONFIG_FILE="config/multicastClientConfig.xml";
    public static String configFileName=DEFAULT_CONFIG_FILE;

	public static final String CONNECTION_WEBLOGIC="weblogic";
	public static final String CONNECTION_SUNRMI="sunrmi";
	
	public static MDFClientConfigurator instance=new MDFClientConfigurator();
	
	protected Document document=null;
	
	private static final String KEY_MULTICAST_CLIENT_CONFIG="multicastClientConfig";
	
	private static final String KEY_ENVIRONMENT="environment";
	private static final String KEY_ATTRRIBUTE_ENVIRONMENTNAME="name";

	private static final String KEY_PARAMETERS="parameters";
	private static final String KEY_SEQUENCEPROBLEMACTION="sequenceProblemAction";
	private static final String KEY_TCP_AUTORECONNECT="autoReconnectTCP";
	private static final String KEY_MULTICAST_NETWORK_INTERFACE="multicastNetworkInterface";
	private static final String KEY_MULTICAST_INACTIVITY_THRESHOLD="multicastInactivityThreshold";
	private static final String KEY_BEST_PRICE_LOG="bestPriceLog";
	private static final String KEY_BEST_PRICE_LOG_FILES="bestPriceLogFiles";
	private static final String KEY_BEST_PRICE_LOG_MAX_SIZE="bestPriceLogMaxSize";
	private static final String KEY_GET_STRIP_INFO="getStripInfo";
			
	private static final String KEY_TCP="tcp";
	private static final String KEY_ATTRRIBUTE_IP="ip";
	private static final String KEY_ATTRRIBUTE_PORT="port";
	private static final String KEY_ATTRRIBUTE_USERNAME="userName";
	private static final String KEY_ATTRRIBUTE_PASSWORD="password";
	
	private static final String KEY_MULTICAST="multicast";

	private static final String KEY_GROUP="group";
	private static final String KEY_ATTRRIBUTE_GROUPNAME="name";
	
	private static final String KEY_FULLORDERDEPTH="fullOrderDepth";
	private static final String KEY_PRICELEVEL="priceLevel";
	
	private static final String KEY_SNAPSHOT="snapshot";
	private static final String KEY_LIVE="live";
   private static final String KEY_SYSTEM_MSG_WINDOW_LOCATION = "systemTextMsgLocation";
   
   private static final String KEY_NUMBER_OF_PRICELEVELS_OPTIONS = "number.of.pricelevels.optioninstance";
   
   private static int NUMBER_OF_PRICELEVELS_OPTIONS = 1; 
   
   private static final String MC_MONITOR_GROUPINFO="msgMonitorGroupInfo";
   private static final String MC_MONITOR_INTERESTED_GROUPS="msgMonitoringInterestedGroups";
   private static final String MC_MONITOR_SAMPLING_INTERVAL="msgMonitorSamplingInterval";
   private static final String MC_MONITOR_SAMPLING_SIZE="msgMonitorSamplingSize";
   private static final String MC_MONITOR_WARNING_THRESHOLD="msgMonitorWarningThreshold";
   private static final String MC_MONITOR_SECONDARY_WARNING_THRESHOLD="msgMonitorSecWarningThreshold";
   private static final String MC_MONITOR_ANALYZER_SLEEP_INTERVAL="mcMonitorSleepInterval";
   private static final String MC_MONITOR_ANALYZER_DELAY_START="mcMonitorDelayStart";
   private static final String MC_MONITOR_REPORTER_SLEEP_INTERVAL="mcReporterSleepInterval";
   private static final String MC_MONITOR_HEALTHMONITOR_SLEEP_INTERVAL="mcHealthMonitorSleepInterval";
   
   private static final String MC_SNAPSHOT_LOGGER_SLEEP_INTERVAL = "mcSnapshotLoggerSleepInterval";
   private static final String MC_SNAPSHOT_LOGGER_NUM_OF_LEVELS = "mcSnapshotLoggerNumOfPriceLevels";
   private static final String MC_MARKET_TYPES_SUPPORTING_BL = "mcMarketTypesSupportBL";
   private static final String SYS_PROPERTY_MULTICAST_NETWORK_INTERFACE="multicast.network.interface";
   private static final String MC_VERIFY_ISMODIFYORDER_FLAG = "multicast.verify.ismodifyorder.flag";

   private static byte AMOrderType = 0;
   private static byte MSSOrderType = 0;
   private static boolean NeedToVerifyModifyOrderFlag = false;
   
	protected MDFClientConfigRepository configRepository=new MDFClientConfigRepository();
	
	/**
	 * The selected client configuration
	 */
	protected MDFClientConfiguration mdfClientConfig=null;
	
	static
	{
		configFileName=System.getProperty("config");
	   
		if(configFileName==null)
		{
			configFileName=DEFAULT_CONFIG_FILE;
		}
	
		try
		{
		   AMOrderType = (byte)System.getProperty("AMOrderType").charAt(0);
		   MSSOrderType = (byte)System.getProperty("MSSOrderType").charAt(0);
		}
		catch(Exception ex)
		{
		   if (logger.isDebugEnabled())
		   {
		      logger.debug("Error getting order type: "+ex, ex);
		   }
		}
		
		String numOfPriceLevelsForOptions = System.getProperty(KEY_NUMBER_OF_PRICELEVELS_OPTIONS);
		if(numOfPriceLevelsForOptions!=null)
		{
			try
			{
				NUMBER_OF_PRICELEVELS_OPTIONS = Integer.parseInt(numOfPriceLevelsForOptions);
			}
			catch (NumberFormatException nfe)
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("Error getting number.of.pricelevels.optioninstance: "+nfe, nfe);
				}
			}
		}
		
		if ("true".equals(System.getProperty(MC_VERIFY_ISMODIFYORDER_FLAG)))
      {
         NeedToVerifyModifyOrderFlag = true;
         logger.info("NeedToVerifyModifyOrderFlag is set to true");
      }
	}
	   
	private MDFClientConfigurator()
	{
	}
	
	public static MDFClientConfigurator getInstance()
	{
		return(instance);
	}
	
	/**
	 * load the default xml config file
	 * @return
	 * @throws InitializationException
	 */
	public void init() throws InitializationException
	{
		init(configFileName);
	}

	/**
	 * load the given config file
	 * @return
	 * @throws InitializationException
	 */
	public void init(String fileName) throws InitializationException
	{
		load(fileName);
	}

	/**
	 * load the xml file
	 * @param fileName
	 * @throws InitializationException
	 */
	protected Document load(String fileName) throws InitializationException
	{
		try
		{
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			
			DocumentBuilder builder=factory.newDocumentBuilder();
			document=builder.parse(new File(fileName));
		}
		catch(ParserConfigurationException e)
		{
			e.printStackTrace();
			throw(new InitializationException(e.getMessage()));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw(new InitializationException(e.getMessage()));
		}
		catch(SAXException e)
		{
			e.printStackTrace();
			throw(new InitializationException(e.getMessage()));
		}
		
		return(document);
	}
	
	/**
	 * build the multicast groups map
	 * 
	 * @return map of multicast group information keyed by the group name
	 */
	protected Map<String,MulticastGroupInfo> buildMulticastGroupsMap(NodeList groupNodesList) throws InitializationException
	{
		Map<String,MulticastGroupInfo> multicastGroupsMap=new HashMap<String,MulticastGroupInfo>();
		
		for(int index=0;index<groupNodesList.getLength();index++)
		{
			Node groupNode=groupNodesList.item(index);
			
			if(groupNode.getNodeType()!=Node.ELEMENT_NODE)
			{
				continue;
			}
			
			NamedNodeMap groupAttributes=groupNode.getAttributes();
			
			Node groupNameNode=groupAttributes.getNamedItem(KEY_ATTRRIBUTE_GROUPNAME);
			String groupName=groupNameNode.getNodeValue().trim();
			
			if(groupName==null || groupName.trim().length()==0)
			{
				logger.error("groupName not configured");
				continue;
			}
			
			MulticastGroupInfo multicastGroupInfo=new MulticastGroupInfo(groupName);
			
			/**
			 * The full order depth context may be null for options markets, as they only support price level
			 */
			MulticastChannelPairInfo fullOrderChannelPair=retrieveMulticastPairs(groupNode,KEY_FULLORDERDEPTH,groupName);
			multicastGroupInfo.setFullOrderInfo(fullOrderChannelPair);
			
			MulticastChannelPairInfo priceLevelChannelPair=retrieveMulticastPairs(groupNode,KEY_PRICELEVEL,groupName);
			multicastGroupInfo.setPriceLevelInfo(priceLevelChannelPair);
			
			multicastGroupsMap.put(groupName, multicastGroupInfo);
		}
		
		if(logger.isTraceEnabled())
		{
			logger.trace("Loaded Multicast Groups Map : "+multicastGroupsMap.toString());
		}

		return(multicastGroupsMap);
	}
	
	/**
	 * Retrieve multicast channel pairs for a given context
	 * If this is not defined, return null
	 * 
	 * @param rootNode
	 * @param contextKey
	 * @return
	 * @throws InitializationException
	 */
	protected MulticastChannelPairInfo retrieveMulticastPairs(Node rootNode, String contextKey, String groupName) throws InitializationException
	{
		MulticastChannelPairInfo channelPair=null;
		
		NodeList contextNodeList=((Element) rootNode).getElementsByTagName(contextKey);
		
		if(contextNodeList==null)
		{
			StringBuffer buf=new StringBuffer("Multicast information not defined. Context=").append(contextKey);
			buf.append(" GroupName=").append(groupName);
			logger.warn(buf.toString());
			return(null);
		}

		Node contextNode=contextNodeList.item(0);
		
		if(contextNode==null)
		{
			logger.warn("Multicast information not defined for : "+contextKey);
			return(null);
		}

		/**
		 * Get the channel pair (snapshot and live information) for this channel context
		 */
		NodeList endPointNodeList=((Element) contextNode).getElementsByTagName(KEY_SNAPSHOT);
		Node endPointNode=endPointNodeList.item(0);
		EndPointInfo snapshotEndPointInfo=this.retrieveEndPointInfo(endPointNode);
		
		endPointNodeList=((Element) contextNode).getElementsByTagName(KEY_LIVE);
		endPointNode=endPointNodeList.item(0);
		EndPointInfo liveEndPointInfo=this.retrieveEndPointInfo(endPointNode);
		
		MulticastChannelContext multicastContext=MulticastChannelContext.getMulticastChannelContextFor(contextKey);
		
		if(multicastContext==null)
		{
			logger.warn("Invalid multicast context : "+contextKey+" Assuming FullOrderDepth");
			multicastContext=MulticastChannelContext.FULLORDERDEPTH;
		}
		
		channelPair=new MulticastChannelPairInfo(groupName,multicastContext,snapshotEndPointInfo,liveEndPointInfo);

		return(channelPair);
	}

	/**
	 * Retrieve end point information ip/port from a given node
	 * @param rootNode
	 * @return
	 */
	protected EndPointInfo retrieveEndPointInfo(Node rootNode)
	{
		NamedNodeMap tcpAttributes=rootNode.getAttributes();
		
		Node ipNode=tcpAttributes.getNamedItem(KEY_ATTRRIBUTE_IP);
		String ip=ipNode.getNodeValue().trim();

		Node portNode=tcpAttributes.getNamedItem(KEY_ATTRRIBUTE_PORT);
		String port=portNode.getNodeValue().trim();

		EndPointInfo endPointInfo=new EndPointInfo(ip,Integer.valueOf(port));
		
		return(endPointInfo);
	}
	
	/**
	 * configure the runtime parameters
	 * @return
	 */
	private MDFClientRuntimeParameters loadRuntimeParameters(Element root) throws InitializationException
	{
		MDFClientRuntimeParameters parameters=new MDFClientRuntimeParameters();
		
		try
		{
			NodeList list=root.getElementsByTagName(KEY_PARAMETERS);
			Node node=list.item(0);
				
			if(node==null)
			{
				logger.warn("Parameters not defined. Returning default.");
				return(parameters);
			}
			
			String sequenceProblemAction=ConfigLoaderHelper.getNodeValueByName(node,KEY_SEQUENCEPROBLEMACTION);
			
			if(sequenceProblemAction==null)
			{
				sequenceProblemAction=MDFClientRuntimeParameters.SEQUENCE_PROBLEM_ACTION_DEFAULT.name();
				System.out.println("### sequenceProblemAction not defined. Using default : "+sequenceProblemAction);
			}
			
			boolean autoReconnectTCP=false;
			String autoReconnect=ConfigLoaderHelper.getNodeValueByName(node, KEY_TCP_AUTORECONNECT);
			if (autoReconnect!=null && autoReconnect.equalsIgnoreCase("true"))
			{
			   autoReconnectTCP=true;
			}
			
			String multicastNetworkInterface=System.getProperty(SYS_PROPERTY_MULTICAST_NETWORK_INTERFACE);

         if(multicastNetworkInterface!=null && multicastNetworkInterface.length()>0)
         {
            logger.info("Using network interface from system property: "+multicastNetworkInterface);
         }
         else
         {
            multicastNetworkInterface=ConfigLoaderHelper.getNodeValueByName(node, KEY_MULTICAST_NETWORK_INTERFACE);
            if(multicastNetworkInterface==null || multicastNetworkInterface.length()==0)
            {
               System.out.println("### multicastNetworkInterface not defined. Using default ### ");
               multicastNetworkInterface=null;
            }
            else
            {
               logger.info("Using multicast interface specified in config file: "+multicastNetworkInterface);
            }
         }
			
			String multicastInactivityThreshold=ConfigLoaderHelper.getNodeValueByName(node, KEY_MULTICAST_INACTIVITY_THRESHOLD);
			int threshold=0;
			
			if(multicastInactivityThreshold==null)
			{
				System.out.println("### Threshold not defined. Using default : "+threshold);
			}
			else
			{
				threshold=Integer.valueOf(multicastInactivityThreshold).intValue();
			}
			
			String getStripInfo=ConfigLoaderHelper.getNodeValueByName(node, KEY_GET_STRIP_INFO);
			boolean toGetStripInfo=false;
			try
			{
			   toGetStripInfo="true".equals(getStripInfo);
			   parameters.setISGetStripInfo(toGetStripInfo);
			}
			catch(Exception ex)
			{
			   logger.error("Error parsing GetStripInfo flag: "+ex, ex);
			}
			
			try
			{
			   boolean bestPriceLog="true".equalsIgnoreCase(ConfigLoaderHelper.getNodeValueByName(node, KEY_BEST_PRICE_LOG).trim());
			   parameters.setBestPriceLog(bestPriceLog);
			}
			catch(Throwable ex)
			{
			}
			
			try
			{
			   String[] bestPriceLogFiles=ConfigLoaderHelper.getNodeValueByName(node, KEY_BEST_PRICE_LOG_FILES).split("\\|");
			   parameters.setBestPriceLogFiles(bestPriceLogFiles);
			}
			catch(Throwable ex)
			{
			}
			
			try
			{
			   String bestPriceLogMaxSize=ConfigLoaderHelper.getNodeValueByName(node, KEY_BEST_PRICE_LOG_MAX_SIZE).trim();
			   parameters.setBestPriceLogMaxSize(bestPriceLogMaxSize);
			}
			catch(Throwable ex)
			{ 
			}
						
			String sysMsgWinLocation=ConfigLoaderHelper.getNodeValueByName(node, KEY_SYSTEM_MSG_WINDOW_LOCATION);
			if (sysMsgWinLocation!=null && sysMsgWinLocation.length()>0)
			{
			   int x=-1;
			   int y=-1;
			   try 
			   {
			      String[] info=sysMsgWinLocation.split("\\|");
			      x=Integer.parseInt(info[0].trim());
			      y=Integer.parseInt(info[1].trim());
			      parameters.setSystemTextWindowLocationXPos(x);
			      parameters.setSystemTextWindowLocationYPos(y);
			   }
			   catch(Exception ex)
			   {
			      System.err.println("Error setting system text message window location:"+ex);
			   }
			}
			
			String marketTypesSupportingBL=ConfigLoaderHelper.getNodeValueByName(node, MC_MARKET_TYPES_SUPPORTING_BL);
			if (marketTypesSupportingBL!=null && marketTypesSupportingBL.length()>0)
			{
			   try
			   {
			      String[] info=marketTypesSupportingBL.split("\\|");
			      if (info!=null && info.length>0)
			      {
			         ArrayList<Short> marketTypeIDList = new ArrayList<Short>();
			         for (String marketTypeID:info)
			         {
			            marketTypeIDList.add(Short.parseShort(marketTypeID));
			         }
			         parameters.setMarketTypeIDListSupportingBL(marketTypeIDList);
			      }
			   }
			   catch(Exception ex)
			   {
			      logger.error("Error parsing "+MC_MARKET_TYPES_SUPPORTING_BL+": "+marketTypesSupportingBL);
			   }
			}
			
			CrossedBookDetectionInfo crossedBookDetectionInfo=CrossedBookDetectionConfigLoader.getInstance().load(node);
						
		   parameters.setSequenceProblemAction(sequenceProblemAction);
			parameters.setAutoReconnectTCP(autoReconnectTCP);
			parameters.setMulticastNetworkInterface(multicastNetworkInterface);
			parameters.setMulticastInactivityThreshold(threshold);
			
			parameters.setCrossedBookDetectionInfo(crossedBookDetectionInfo);
			
			String msgMonitoringGroupInfo=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_GROUPINFO);
			if (msgMonitoringGroupInfo!=null && msgMonitoringGroupInfo.length()>0)
			{
			   parameters.setMsgMonitoringGroupInfo(msgMonitoringGroupInfo);
			   try
			   {
			      String msgMonitoringInterestedGroups=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_INTERESTED_GROUPS);
			      String msgSamplingInterval=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_SAMPLING_INTERVAL);
			      String msgSamplingSize=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_SAMPLING_SIZE);
			      String msgWarningThreshold=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_WARNING_THRESHOLD);
			      String msgSecondaryWarningThreshold=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_SECONDARY_WARNING_THRESHOLD);
			      String msgAnalyzerSleepInterval=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_ANALYZER_SLEEP_INTERVAL);
			      String msgAnalyzerDelayStart=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_ANALYZER_DELAY_START);
			      String msgReporterSleepInterval=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_REPORTER_SLEEP_INTERVAL);
			      String msgHealthMonitorSleepInterval=ConfigLoaderHelper.getNodeValueByName(node, MC_MONITOR_HEALTHMONITOR_SLEEP_INTERVAL);
			      String msgSnapshotLoggerSleepInterval=ConfigLoaderHelper.getNodeValueByName(node, MC_SNAPSHOT_LOGGER_SLEEP_INTERVAL);
			      String msgSnapshotLoggerNumOfLevel=ConfigLoaderHelper.getNodeValueByName(node, MC_SNAPSHOT_LOGGER_NUM_OF_LEVELS);

			      parameters.setMsgMonitoringInterestedGroups(msgMonitoringInterestedGroups);
			      parameters.setMsgSamplingInterval(Long.parseLong(msgSamplingInterval));
			      parameters.setMsgSamplingSize(Long.parseLong(msgSamplingSize));
			      parameters.setMsgWarningThreshold(Long.parseLong(msgWarningThreshold));
			      parameters.setMsgSecondaryWarningThreshold(Long.parseLong(msgSecondaryWarningThreshold));
			      parameters.setAnalyzerSleepInterval(Long.parseLong(msgAnalyzerSleepInterval));
			      parameters.setAnalyzerDelayStart(Long.parseLong(msgAnalyzerDelayStart));
			      parameters.setReporterSleepInterval(Long.parseLong(msgReporterSleepInterval));
			      parameters.setHealthMonitorSleepInterval(Long.parseLong(msgHealthMonitorSleepInterval));
			      parameters.setSnapshotLoggerSleepInterval(Long.parseLong(msgSnapshotLoggerSleepInterval));
			      if (msgSnapshotLoggerNumOfLevel!=null && msgSnapshotLoggerNumOfLevel.length()>0)
			      {
			         parameters.setSnapshotLoggerNumOfLevels(Short.parseShort(msgSnapshotLoggerNumOfLevel));
			      }
			   }
			   catch(Exception e)
			   {
			      logger.warn("Error reading mc monitoring parameter, use defaults. Exception="+e.getMessage(),e);
			   }
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			throw(new InitializationException(t.getMessage()));
		}
		
		return(parameters);
	}

	/**
	 * configure
	 * @return MDFClientConfigRepository
	 */
	public MDFClientConfigRepository configure() throws InitializationException
	{
		logger.info("MDF Client Configurator is reading the configuration");
		
		try
		{
			NodeList rootList=document.getElementsByTagName(KEY_MULTICAST_CLIENT_CONFIG);
			Node root=(Node) rootList.item(0);

			/**
			 * Load the multicast group definitions
			 */
			Map<String,MulticastGroupDefinition> multicastGroupDefinitions=MulticastGroupDefinitionsConfigLoader.getInstance().load(root);
			
			if(multicastGroupDefinitions==null || multicastGroupDefinitions.size()==0)
			{
				String message="Error loading multicast group definitions. Check the multicast configuration xml file.";
				logger.error(message);
				throw(new InitializationException(message));
			}
			
			configRepository.setMulticastGroupDefinitions(multicastGroupDefinitions);

			/**
			 * Load all the market types and associate them with the corresponding multicast groups
			 */
			List<MarketType> allMarketTypes=AllMarketTypesConfigLoader.getInstance().load(root);
			
			for(Iterator<MarketType> it=allMarketTypes.iterator();it.hasNext();)
			{
				MarketType marketType=it.next();
				this.configRepository.addMarketType(marketType);
			}
			
			/**
			 * Load the environment specific configuration
			 */
			NodeList list=((Element) root).getElementsByTagName(KEY_ENVIRONMENT);

			int numberOfNodes=list.getLength();
			
			for(int index=0;index<numberOfNodes;index++)
			{
				Node envNode=list.item(index);
				
				NamedNodeMap envAttributes=envNode.getAttributes();
				
				Node envNameNode=envAttributes.getNamedItem(KEY_ATTRRIBUTE_ENVIRONMENTNAME);
				String envName=envNameNode.getNodeValue().trim();
				
				/**
				 * Get TCP Information
				 */
				NodeList tcpNodeList=((Element) envNode).getElementsByTagName(KEY_TCP);
				
				Node tcpNode=tcpNodeList.item(0);
				NamedNodeMap tcpAttributes=tcpNode.getAttributes();
				
				EndPointInfo endPointInfo=retrieveEndPointInfo(tcpNode);

				Node userNameNode=tcpAttributes.getNamedItem(KEY_ATTRRIBUTE_USERNAME);
				String userName=userNameNode.getNodeValue().trim();
				
				Node passwordNode=tcpAttributes.getNamedItem(KEY_ATTRRIBUTE_PASSWORD);
				String password=passwordNode.getNodeValue().trim();
				
				MDFClientEnvConfigRepository envConfigRepository=new MDFClientEnvConfigRepository(envName,endPointInfo,userName,password);
				
				/**
				 * Get Multicast Information for this environment
				 */
				NodeList multicastNodeList=((Element) envNode).getElementsByTagName(KEY_MULTICAST);
				Node multicastNode=multicastNodeList.item(0);
				
				NodeList groupNodesList=((Element)multicastNode).getElementsByTagName(KEY_GROUP);
				Node groupNode=groupNodesList.item(0);
				
				/**
				 * Build the multicast groups map for this environment
				 */
				Map<String,MulticastGroupInfo> multicastGroupsMap=buildMulticastGroupsMap(groupNodesList);
				envConfigRepository.setMulticastGroupsMap(multicastGroupsMap);
				
				/**
				 * add the config for this env to the repository
				 */
				configRepository.addConfigRepository(envConfigRepository);
			}
			
			if(logger.isTraceEnabled())
			{
				logger.trace("### Loading Runtime Parameters ");
			}
			
			configRepository.setMDFClientRuntimeParameters(loadRuntimeParameters(((Element) root)));

			/**
			 * Load ALL market types table
			 */
			if(logger.isTraceEnabled())
			{
				logger.trace("### Loading ALL MarketTypes");
			}
		}
		catch(InitializationException e)
		{
			e.printStackTrace();
			throw(e);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			throw(new InitializationException(t.getMessage()));
		}
		
		return(configRepository);
	}

	public MDFClientConfiguration getCurrentConfiguration()
	{
		return(this.mdfClientConfig);
	}
	
	public void setCurrentConfiguration(MDFClientConfiguration mdfClientConfig)
	{
		this.mdfClientConfig=mdfClientConfig;
	}

	/**
	 * create the current configuration based on the following:
	 * 
	 * - environment
	 * - group/context (leads to channel pair)
	 * - interested market types
	 * 
	 * @param env
	 * @param groupName
	 * @param contextName
	 * @param interestedMarketTypesList
	 * @return
	 * @throws ConfigurationException
	 */
	public MDFClientConfiguration chooseConfiguration(String env, List<String> groupNames,String contextName,Map<String, List<MarketType>> interestedMarketTypesMap)
		throws ConfigurationException
	{
		Map<String, MulticastGroupDefinition> multicastGroupDefinitionMap=this.configRepository.getMulticastGroupDefinitions(groupNames);
    	MDFClientEnvConfigRepository envRepository=this.configRepository.getConfig(env);
    	
		MDFClientConfiguration selectedConfig=new MDFClientConfiguration(env,multicastGroupDefinitionMap,envRepository.getTcpInfo());
		
    	Map<String, MulticastChannelPairInfo> channelPairInfoMap=envRepository.getMulticastChannelPairInfoMap(groupNames, contextName);
    	
    	if(channelPairInfoMap==null)
    	{
    		StringBuffer message=new StringBuffer("Channel Pair is null. Group=").append(groupNames).append(". Context=").append(contextName);
    		logger.error(message);
    		throw(new ConfigurationException(message.toString()));
    	}
    	
		selectedConfig.setMulticastChannelPairInfoMap(channelPairInfoMap);
		
		selectedConfig.setMDFClientRuntimeParameters(MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters());
		selectedConfig.setInterestedMarketTypesMap(interestedMarketTypesMap);
		
		this.mdfClientConfig=selectedConfig;
		
		logger.info("### Choosing configuration : "+selectedConfig.toString());
		
		return(selectedConfig);
	}

	/**
	 * MulticastChannelContext (FullOrder, PriceLevel etc., can be used to lookup the channel pair info)
	 * 
	 * @param env
	 * @param groupName
	 * @param contextName
	 * @return
	 * @throws ConfigurationException
	 */
	public MDFClientConfiguration chooseConfiguration(String env, List<String> groupNames,String contextName)
		throws ConfigurationException
	{
		//Map<String, List<MarketType>> interestedMarketTypesMap=getConfigRepository().getMarketTypesMap(groupNames);
		Map<String, List<MarketType>> interestedMarketTypesMap=getConfigRepository().getGroupwiseMarketTypesMap();
		MDFClientConfiguration selectedConfig=chooseConfiguration(env,groupNames,contextName,interestedMarketTypesMap);

		return(selectedConfig);
	}
	
	/**
	 * choose the configuration through information from the selector
	 * @param selector
	 * @return
	 * @throws ConfigurationException
	 */
	public MDFClientConfiguration chooseConfiguration(ConfigurationSelectorInfo selector) throws ConfigurationException
	{
		return(this.chooseConfiguration(selector.getEnvironment(),selector.getMulticastGroups(),selector.getContext()));
	}

	public MDFClientConfigRepository getConfigRepository()
	{
		return(this.configRepository);
	}

	/**
	 * main
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		MDFClientConfigurator configurator=MDFClientConfigurator.getInstance();
		
		try
		{
			configurator.init();
			
			MDFClientConfigRepository configRepository=configurator.configure();
			
			System.out.println(configRepository.toString());
			
			logger.info(configRepository.toString());
			
			ConfigurationSelectorInfo selectorInfo=MDFCommandLineConfigurator.getInstance().obtainConfigurationSelector(true);
			
			if(selectorInfo!=null)
			{
				System.out.println("Configuration Selector loaded from the environment. "+selectorInfo.toString());
				
				MDFClientConfiguration config=configurator.chooseConfiguration(selectorInfo);
				
				if(config!=null)
				{
					System.out.println("--------- Configuration chosen from the repository for the given env/group/context.");
					System.out.println(config.toString());
					System.out.println("---------");
				}
			}
			else
			{
				System.err.println("No configuration selection information provided through the system variables.");
			}
			
		}
		catch(ConfigurationException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return;
	}
	
   public static byte getMsgTypeForAMOrder()
   {
      return (byte)AMOrderType;
   }
   
   public static byte getMsgTypeForMSSOrder()
   {
      return (byte)MSSOrderType;
   }
   
   public static boolean isVerifyingModifyOrderFlag()
   {
      return NeedToVerifyModifyOrderFlag;
   }
   
   public static int getNumberOfPriceLevelsOptions()
   {
      return NUMBER_OF_PRICELEVELS_OPTIONS;
   }
   
}

