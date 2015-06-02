package com.theice.mdf.client.config.helper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class ConfigLoaderHelper
{
	/**
	 * Get a node's value by name, within a given root node
	 * @param node
	 * @param elementName
	 * @return
	 */
	public static String getNodeValueByName(Node node,String elementName)
	{
		String elementValue=null;
		
		NodeList nodeList=((Element) node).getElementsByTagName(elementName);
		
		Node childNode=nodeList.item(0);
		
		if(childNode==null)
		{
			return(null);
		}
		
		elementValue=childNode.getFirstChild().getNodeValue();
		
		return(elementValue);
	}

	/**
	 * Get a node's value as a String
	 * @param element
	 * @return
	 */
	public static String getElementValue(Element element)
	{
		String value=null;

		NodeList nodeList=element.getChildNodes();
		
		if(nodeList!=null)
		{
			Node node=(Node)nodeList.item(0);
			
			if(node!=null)
			{
				value=node.getNodeValue().trim();
			}
		}
		
		return(value);
	}

}
