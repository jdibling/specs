package com.theice.mdf.client.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.theice.mdf.client.config.domain.MulticastGroupDefinition;
import com.theice.mdf.client.exception.InitializationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * MulticastGroupDefinitionsConfigLoader
 * 
 *	<multicastGroups>
 *		<multicastGroup name="ICE OTC"/>
 *		<multicastGroup name="ICE Futures Europe Non Oil"/>
 *		<multicastGroup name="ICE Futures Europe Oil"/>
 *		<multicastGroup name="ICE Futures Europe Non Oil"/>	
 *		<multicastGroup name="ICE Futures US Agricultures"/>
 *		<multicastGroup name="ICE Futures US Financials"/>
 *		<multicastGroup name="CCX Futures"/>
 *		<multicastGroup name="ICE Futures Canada"/>
 *		<multicastGroup name="ICE Futures Canada Options" isOptions="true"/>
 *	</multicastGroups>
 *
 * @author Adam Athimuthu
 */
public class MulticastGroupDefinitionsConfigLoader
{
    private static final Logger logger=Logger.getLogger(MulticastGroupDefinitionsConfigLoader.class.getName());

    private static MulticastGroupDefinitionsConfigLoader instance=new MulticastGroupDefinitionsConfigLoader();
	
	private static final String KEY_MULTICASTGROUPS="multicastGroups";
	private static final String KEY_MULTICASTGROUP="multicastGroup";
	private static final String KEY_ATTRIBUTE_NAME="name";
	private static final String KEY_ATTRIBUTE_ISOPTIONS="isOptions";

	private MulticastGroupDefinitionsConfigLoader()
	{
	}
	
	public static MulticastGroupDefinitionsConfigLoader getInstance()
	{
		return(instance);
	}

	/**
	 * Load the multicast group definitions
	 * @return Multicast Group Definitions
	 * @throws InitializationException
	 */
	public Map<String,MulticastGroupDefinition> load(Node parent) throws InitializationException
	{
		Map<String,MulticastGroupDefinition> multicastGroupDefinitions=new HashMap<String,MulticastGroupDefinition>();
		
		try
		{
			NodeList list=((Element)parent).getElementsByTagName(KEY_MULTICASTGROUPS);
			Node node=list.item(0);

			if(node==null)
			{
				logger.warn("multicastGroups not defined. Returning empty collection.");
				return(multicastGroupDefinitions);
			}
			
			NodeList multicastGroupNodesList=((Element) node).getElementsByTagName(KEY_MULTICASTGROUP);

			for(int index=0;index<multicastGroupNodesList.getLength();index++)
			{
				Node multicastGroupNode=multicastGroupNodesList.item(index);
				
				if(multicastGroupNode.getNodeType()!=Node.ELEMENT_NODE)
				{
					continue;
				}
				
				NamedNodeMap marketTypeAttributes=multicastGroupNode.getAttributes();
				
				Node nameNode=marketTypeAttributes.getNamedItem(KEY_ATTRIBUTE_NAME);
				String multicastGroupName=nameNode.getNodeValue().trim();
				
				if(multicastGroupName==null || multicastGroupName.trim().length()==0)
				{
					continue;
				}
				
				Node isOptionsNode=marketTypeAttributes.getNamedItem(KEY_ATTRIBUTE_ISOPTIONS);
				boolean isOptions=false;
				
				if(isOptionsNode!=null)
				{
					String isOptionsValue=isOptionsNode.getNodeValue().trim();

					if("true".equals(isOptionsValue))
					{
						isOptions=true;
					}
				}

				MulticastGroupDefinition multicastGroupDefinition=new MulticastGroupDefinition(multicastGroupName,isOptions);
				
				if(logger.isDebugEnabled())
				{
					logger.debug("#### Multicast Group Definition loaded : "+multicastGroupDefinition.toString());
				}
				
				multicastGroupDefinitions.put(multicastGroupDefinition.getGroupName(), multicastGroupDefinition);
			}
		}
		catch(Throwable t)
		{
			String message="Error loading Multicast Group Definitions.";
			logger.error(message,t);
			throw(new InitializationException(message,t));
		}
		
		if(logger.isTraceEnabled())
		{
			logger.trace("Loaded Multicast Group Definitions : "+multicastGroupDefinitions.toString());
		}
		
		return(multicastGroupDefinitions);
	}
	
}

