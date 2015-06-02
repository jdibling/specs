package com.theice.mdf.client.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.theice.mdf.client.config.helper.ConfigLoaderHelper;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.exception.InitializationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * AllMarketTypesConfigLoader
 * 
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
 * @author Adam Athimuthu
 */
public class AllMarketTypesConfigLoader
{
    private static final Logger logger=Logger.getLogger(AllMarketTypesConfigLoader.class.getName());

    private static AllMarketTypesConfigLoader instance=new AllMarketTypesConfigLoader();
	
	private static final String KEY_ALL_MARKET_TYPES="allMarketTypes";
	private static final String KEY_MARKET_TYPE="marketType";
	private static final String KEY_ATTRIBUTE_MARKET_TYPE_CODE="code";
	private static final String KEY_ATTRIBUTE_MARKET_TYPE_NAME="name";
	
	private static final String KEY_MARKET_TYPE_MULTICAST_GROUP="multicastGroup";
	private static final String KEY_ATTRIBUTE_MULTICASTGROUP_NAME="name";
	
	private AllMarketTypesConfigLoader()
	{
	}
	
	public static AllMarketTypesConfigLoader getInstance()
	{
		return(instance);
	}

	/**
	 * Load all market types
	 * @return all the market types
	 * @throws InitializationException
	 */
	public List<MarketType> load(Node parent) throws InitializationException
	{
		List<MarketType> marketTypes=new ArrayList<MarketType>();
		
		try
		{
			NodeList list=((Element)parent).getElementsByTagName(KEY_ALL_MARKET_TYPES);
			Node node=list.item(0);

			if(node==null)
			{
				String message="allMarketTypes node not defined.";
				logger.error(message);
				throw(new InitializationException(message));
			}

			NodeList marketTypesNodesList=((Element) node).getElementsByTagName(KEY_MARKET_TYPE);

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
					logger.error("marketTypeCode is null.");
					continue;
				}
				
				Node marketTypeNameNode=marketTypeAttributes.getNamedItem(KEY_ATTRIBUTE_MARKET_TYPE_NAME);
				String marketTypeName=marketTypeNameNode.getNodeValue().trim();
				
				if(marketTypeName==null || marketTypeName.trim().length()==0)
				{
					logger.error("marketTypeName not configured for : "+marketTypeCode);
					continue;
				}

				/**
				 * Load all the multicast groups that this market type is associated with.
				 */
				List<String> multicastGroupNames=this.loadMulticastGroups(marketTypesNode,marketTypeName);

				marketTypes.add(new MarketType(marketTypeCode,marketTypeName,multicastGroupNames));
			}
			
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			throw(new InitializationException(t.getMessage()));
		}
		
		if(logger.isTraceEnabled())
		{
			logger.trace("### Loaded MarketTypes : "+marketTypes.toString());
		}
		
		return(marketTypes);
	}

	/**
	 * Multicast group names associated with a specific market type
	 * @param parent
	 * @param marketTypeName
	 * @return
	 * @throws InitializationException
	 */
	private List<String> loadMulticastGroups(Node parent,String marketTypeName) throws InitializationException
	{
		List<String> multicastGroupNames=new ArrayList<String>();
		
		try
		{
			NodeList multicastGroupNodesList=((Element)parent).getElementsByTagName(KEY_MARKET_TYPE_MULTICAST_GROUP);

			for(int index=0;index<multicastGroupNodesList.getLength();index++)
			{
				Node multicastGroupNode=multicastGroupNodesList.item(index);
				
				if(multicastGroupNode.getNodeType()!=Node.ELEMENT_NODE)
				{
					continue;
				}
				
				NamedNodeMap multicastGroupAttributes=multicastGroupNode.getAttributes();
				
				Node nameNode=multicastGroupAttributes.getNamedItem(KEY_ATTRIBUTE_MULTICASTGROUP_NAME);
				String multicastGroupName=nameNode.getNodeValue().trim();
				
				if(multicastGroupName==null || multicastGroupName.trim().length()==0)
				{
					continue;
				}

				multicastGroupNames.add(multicastGroupName);
			}
		}
		catch(Throwable t)
		{
			String message="Error loading Multicast Group Names.";
			logger.error(message,t);
			throw(new InitializationException(message,t));
		}
		
		if(logger.isTraceEnabled())
		{
			StringBuffer buf=new StringBuffer(marketTypeName);
			buf.append(" ==> Associated Multicast Groups : ").append(multicastGroupNames.toString());
			logger.trace(buf.toString());
		}
		
		return(multicastGroupNames);
	}
				
}

