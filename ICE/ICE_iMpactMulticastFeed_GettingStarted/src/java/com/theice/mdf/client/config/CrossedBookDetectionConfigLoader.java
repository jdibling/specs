package com.theice.mdf.client.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.theice.mdf.client.config.domain.CrossedBookDetectionInfo;
import com.theice.mdf.client.config.helper.ConfigLoaderHelper;
import com.theice.mdf.client.exception.InitializationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * CrossedBookDetectionConfigLoader
 * 
 *	<detectCrossedBook>
 *		<delayedAlert>
 *			<marketTypes>
 *				<marketType code="0" />
 *				<marketType code="1" />
 *			</marketTypes>
 *			<delayedAlertThreshold>120000</delayedAlertThreshold>
 *			<delayedAlertMonitoringInterval>1000</delayedAlertMonitoringInterval>
 *		</delayedAlert>
 *	</detectCrossedBook>
 *
 * @author Adam Athimuthu
 */
public class CrossedBookDetectionConfigLoader
{
    private static final Logger logger=Logger.getLogger(CrossedBookDetectionConfigLoader.class.getName());

    private static CrossedBookDetectionConfigLoader instance=new CrossedBookDetectionConfigLoader();
	
	private static final String KEY_DETECTCROSSEDBOOK="detectCrossedBook";
	private static final String KEY_DELAYED_ALERT="delayedAlert";
	private static final String KEY_DELAYED_ALERT_THRESHOLD="delayedAlertThreshold";
	private static final String KEY_DELAYED_ALERT_MONITORING_INTERVAL="delayedAlertMonitoringInterval";

	private static final String KEY_MARKET_TYPES="marketTypes";
	private static final String KEY_MARKET_TYPE="marketType";
	private static final String KEY_ATTRIBUTE_MARKET_TYPE_CODE="code";

	private CrossedBookDetectionConfigLoader()
	{
	}
	
	public static CrossedBookDetectionConfigLoader getInstance()
	{
		return(instance);
	}

	/**
	 * Load the crossed book detection parameters
	 * Also, load the associated delayed monitoring parameters (if applicable)
	 * @return CrossedBookDetectionInfo
	 * @throws InitializationException
	 */
	public CrossedBookDetectionInfo load(Node parent) throws InitializationException
	{
		CrossedBookDetectionInfo crossedBookDetectionInfo=null;
		
		try
		{
			Node node=null;
			NodeList list=((Element)parent).getElementsByTagName(KEY_DETECTCROSSEDBOOK);
			
			node=list.item(0);

			if(node==null)
			{
				logger.warn("detectCrossedBook not defined. Returning default (Crossed Book Detection Disabled).");
				return(crossedBookDetectionInfo);
			}
			
			crossedBookDetectionInfo=new CrossedBookDetectionInfo();
			crossedBookDetectionInfo.enableCrossedBookDetection();
			
			loadDelayedCrossedBookMonitoringInfo(node, crossedBookDetectionInfo);
		}
		catch(Throwable t)
		{
			String message="Error loading crossed book detection info.";
			logger.error(message,t);
			throw(new InitializationException(message,t));
		}
		
		if(logger.isTraceEnabled())
		{
			logger.trace("Loaded Crossed Book Detection Info : "+crossedBookDetectionInfo.toString());
		}
		
		return(crossedBookDetectionInfo);
	}
	
	/**
	 * load delayed crossed book monitoring information
	 * - all market types for which delayed monitoring is enabled
	 * - threshold
	 * - alert monitoring interval
	 * @param parent
	 */
	private void loadDelayedCrossedBookMonitoringInfo(Node parent,CrossedBookDetectionInfo crossedBookDetectionInfo)
		throws InitializationException
	{
		Node node=null;
		NodeList list=((Element)parent).getElementsByTagName(KEY_DELAYED_ALERT);
		
		node=list.item(0);

		if(node==null)
		{
			logger.warn("detectCrossedBook not defined. Returning default (Crossed Book Detection Disabled).");
			return;
		}
		
		/**
		 * Check if we have at least one market type defined for delayed alert monitoring
		 */
		List<Short> crossedBookDelayedMarketTypes=loadMarketTypeCodes((Element) node,KEY_MARKET_TYPES);
		
		if(crossedBookDelayedMarketTypes.size()==0)
		{
			return;
		}
		
		crossedBookDetectionInfo.setCrossedBookAlertDelayedMarketTypes(crossedBookDelayedMarketTypes);
		
		/**
		 * Load delayed monitoring parameters
		 */
		String delayedAlertThreshold=ConfigLoaderHelper.getNodeValueByName(node, KEY_DELAYED_ALERT_THRESHOLD);
		long threshold=0L;
		
		if(delayedAlertThreshold==null)
		{
			System.out.println("### Delayed Alert Monitoring Threshold not defined. Using default : "+threshold);
		}
		else
		{
			threshold=Integer.valueOf(delayedAlertThreshold).longValue();
		}
		
		crossedBookDetectionInfo.setCrossedBookDelayedAlertThresholdMs(threshold);
		
		String delayedAlertMonitoringInterval=ConfigLoaderHelper.getNodeValueByName(node, KEY_DELAYED_ALERT_MONITORING_INTERVAL);
		long interval=0L;
		
		if(delayedAlertMonitoringInterval==null)
		{
			System.out.println("### Delayed Alert Monitoring Interval not defined. Using default : "+interval);
		}
		else
		{
			interval=Integer.valueOf(delayedAlertMonitoringInterval).longValue();
		}
		
		crossedBookDetectionInfo.setCrossedBookDelayedAlertMonitoringIntervalMs(interval);
		
		return;
	}

	/**
	 * load market types from the given root node
	 * @param root node
	 * @param subTreeTag
	 * @return List<Short>
	 * @throws InitializationException
	 */
	private List<Short> loadMarketTypeCodes(Element root,String subTreeTag) throws InitializationException
	{
		List<Short> crossedBookDelayedMarketTypeCodes=new ArrayList<Short>();
		
		try
		{
			NodeList list=root.getElementsByTagName(subTreeTag);
			Node node=list.item(0);
				
			if(node==null)
			{
				String message=subTreeTag+" not defined.";
				logger.warn(message);
				throw(new InitializationException(message));
			}

			NodeList marketTypesNodesList=root.getElementsByTagName(KEY_MARKET_TYPE);

			for(int index=0;index<marketTypesNodesList.getLength();index++)
			{
				Node marketTypesNode=marketTypesNodesList.item(index);
				
				if(marketTypesNode.getNodeType()!=Node.ELEMENT_NODE)
				{
					continue;
				}
				
				NamedNodeMap marketTypeAttributes=marketTypesNode.getAttributes();
				
				Node marketTypeCodeNode=marketTypeAttributes.getNamedItem(KEY_ATTRIBUTE_MARKET_TYPE_CODE);
				String marketTypeCode=marketTypeCodeNode.getNodeValue().trim();
				
				if(marketTypeCode==null || marketTypeCode.trim().length()==0)
				{
					continue;
				}
				
				if(logger.isDebugEnabled())
				{
					logger.debug("#### Market Type for Delayed Alert loaded : "+marketTypeCode);
				}
				
				crossedBookDelayedMarketTypeCodes.add(Short.valueOf(marketTypeCode));
			}
		}
		catch(Throwable t)
		{
			String message="Error loading market types for delayed crossed book alerts.";
			logger.error(message,t);
			throw(new InitializationException(message,t));
		}
		
		return(crossedBookDelayedMarketTypeCodes);
	}
}

