/**
 * Created by IntelliJ IDEA.
 * User: Adam Athimuthu
 * Date: Aug 3, 2007
 * Time: 11:23:40 AM
 * To change this template use File | Settings | File Templates.
 */
package com.theice.mdf.client.process.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.response.ProductDefinitionResponse;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketStateManager;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.book.Book;
import com.theice.mdf.client.domain.book.FullOrderBook;
import com.theice.mdf.client.domain.book.NullBook;
import com.theice.mdf.client.domain.book.PriceLevelBook;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * This handler processes the product definition messages and helps building the
 * data structures for representing Underlying (Futures/OTC) markets.
 * 
 * This is the primary place where a Market object gets created first. The
 * market is created within a specific "book context".
 * 
 * Markets: Futures/OTC (non-Options)
 * 
 * @author Adam Athimuthu Date: Aug 3, 2007 Time: 4:21:41 PM
 */
public class ProductDefinitionHandler extends AbstractMarketMessageHandler
{
	private static ProductDefinitionHandler _instance = new ProductDefinitionHandler();

	private static final Logger logger = Logger.getLogger(ProductDefinitionHandler.class.getName());
	
	private static final HashMap<String, List<MarketType>> MarketTypeMapByGroup = MDFClientConfigurator.getInstance().getConfigRepository().getGroupwiseMarketTypesMap();

	private static final List<Short> marketTypeIDListToCheck = MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters().getMarketTypeIDListSupportingBL();
	
	private static final char IGNORE = '3';
	
	public static ProductDefinitionHandler getInstance()
	{
		return _instance;
	}

	private ProductDefinitionHandler()
	{
	}

	/**
	 * handle the message
	 * 
	 * @param message
	 */
	protected void handleMessage(PriceFeedMessage priceFeedMessage)
	{
		MDMessage message = priceFeedMessage.getMessage();

		Market market = null;

		if(logger.isTraceEnabled())
		{
			logger.trace("Received Product Definition : " + message.toString());
		}
		
		ProductDefinitionResponse productDefinitionResponseMsg = (ProductDefinitionResponse)message;
		
		if (productDefinitionResponseMsg.ReservedField1==IGNORE)
		{
		   if ( marketTypeIDListToCheck==null || 
		       (marketTypeIDListToCheck!=null && !marketTypeIDListToCheck.contains(productDefinitionResponseMsg.RequestMarketType)))
		   {
		      logger.info("Ignore market ID "+productDefinitionResponseMsg.MarketID);
	         return;
		   }
		}
		
		/**
		 * Create the market in the appropriate hash table belonging to the
		 * market type However, an option market is always initialized with a
		 * PriceLevelBook in the OptionMarket constructor
		 */
		try
		{
			Book book = null;

			switch(AppManager.getMulticastChannelContext())
			{
			case PRICELEVEL:
				book = new PriceLevelBook(MarketInterface.NUMBER_OF_PRICELEVELS);
				break;
			case FULLORDERDEPTH:
				book = new FullOrderBook();
				break;
			default:
				book = new NullBook();
				break;
			}

			market=new Market((ProductDefinitionResponse) message, book);
			book.setMarketID(market.getMarketID());

			MarketsHolder.getInstance().storeMarket(market);
		}
		catch(Exception e)
		{
			logger.error("Error processing the product definition for : "+message.getMarketID(),e);
			e.printStackTrace();
		}

		/**
		 * Update the Models inside the context
		 */
		AppManager.getAppContext().cacheMarket(market);

		/**
		 * Init/create the market sequencer with a NOT READY state (for FUTURES)
		 * The underlying markets are immediately moved to the READY state for OPTIONS mode
		 * 
		 * Since ProductDefinition can be registered in FUTURES or OPTIONS mode, we need to rely on
		 * the configuration to see what the current market context is
		 */
		
		ProductDefinitionResponse response = (ProductDefinitionResponse)message;
		short marketType = response.RequestMarketType;
		List<String> multicastGroupNamesList = getMulticastGroupNameByMarketTypeID(marketType);
		
		search:
		for(String multicastGroupName:multicastGroupNamesList)
		{
		   if (!multicastGroupName.contains("Options") && MarketStateManager.getInstance(multicastGroupName)!=null)
		   {
		      MarketStateManager.getInstance(multicastGroupName).initSequencer(market.getMarketKey());
		      break search;
		   }
		}
		return;
	}
	
	/*
	 * There might be more than 1 multicast groups associated with 1 market type when Options and FI are supported 
	 */
	public static List<String> getMulticastGroupNameByMarketTypeID(short marketTypeID)
	{
	   Collection<List<MarketType>> marketTypesList = MarketTypeMapByGroup.values();
	   String marketTypeCode = String.valueOf(marketTypeID);
	   List<String> multicastGroupNamesList=null;
	   for(List<MarketType> marketTypeList:marketTypesList)
	   {
	      for(MarketType marketType:marketTypeList)
	      {
	         if (marketTypeCode.equals(marketType.getMarketTypeCode()))
            {
	            multicastGroupNamesList = marketType.getMulticastGroups();
	            break;
            }
	      }
	   }
	   
	   return multicastGroupNamesList;
	}

}
