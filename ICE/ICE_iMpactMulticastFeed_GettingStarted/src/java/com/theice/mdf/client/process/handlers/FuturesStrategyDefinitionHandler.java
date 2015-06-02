/**
 * Created by IntelliJ IDEA.
 * User: Adam Athimuthu
 * Date: Aug 3, 2007
 * Time: 11:23:40 AM
 * To change this template use File | Settings | File Templates.
 */
package com.theice.mdf.client.process.handlers;

import java.util.List;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.OptionMarket;
import com.theice.mdf.client.domain.book.Book;
import com.theice.mdf.client.domain.book.FullOrderBook;
import com.theice.mdf.client.domain.book.NullBook;
import com.theice.mdf.client.domain.book.PriceLevelBook;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.multicast.handler.MarketStateManager;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.response.FuturesStrategyDefinitionResponse;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY. THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * This handler processes the options product defintion messages and helps building the data structures for representing options
 * markets.
 * 
 * This is the primary place where an Options Market object gets created first. The market is created with a Price Level context.
 * The Underlying market has to exist first.
 * 
 * TODO Make sure the options markets/sequencer creation is proper TODO check the options snapshot channel TODO check whether
 * subscription to futures vs. options market is mutually exclusive
 * 
 * @author Adam Athimuthu
 */
public class FuturesStrategyDefinitionHandler extends AbstractMarketMessageHandler
{
	private static FuturesStrategyDefinitionHandler _instance = new FuturesStrategyDefinitionHandler();

	private static final Logger logger = Logger.getLogger(FuturesStrategyDefinitionHandler.class.getName());

	public static FuturesStrategyDefinitionHandler getInstance()
	{
		return _instance;
	}

	private FuturesStrategyDefinitionHandler()
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
		Market udsMarket = null;

		if (logger.isTraceEnabled())
		{
			logger.trace("Received Futures Product Definition : " + message.toString());
		}

		FuturesStrategyDefinitionResponse theMessage = (FuturesStrategyDefinitionResponse) message;

		/**
		 * Locate the underlying market Create the market in the appropriate hash table belonging to the market type an option
		 * market is always initialized with a PriceLevelBook in the OptionMarket constructor
		 */

		Book book = null;

		switch (AppManager.getMulticastChannelContext())
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

		market = (Market) MarketsHolder.getInstance().findMarket(theMessage.MarketID);

		if (market == null)
		{
			logger.error("Underlying market not found while trying to create OptionsMarket : " + theMessage.toString());
			return;
		}

		try
		{
			udsMarket = new Market(theMessage, book);
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		MarketsHolder.getInstance().storeMarket(udsMarket);

		/**
		 * Update the Models inside the context
		 */
		AppManager.getAppContext().cacheMarket(udsMarket);

		/**
		 * Init/create the market sequencer with a NOT READY state if the channel context is for options, we don't need to track the
		 * underlying markets If we have the options product definition handler registered, we are certainly operating in the
		 * options mode
		 */

		// OptionsProductDefinitionResponse response = (OptionsProductDefinitionResponse)message;
		short marketTypeID = theMessage.RequestMarketType;
		List<String> multicastGroupNamesList = ProductDefinitionHandler.getMulticastGroupNameByMarketTypeID(marketTypeID);

		search: for (String multicastGroupName : multicastGroupNamesList)
		{
			if (multicastGroupName.contains("Options") && MarketStateManager.getInstance(multicastGroupName) != null)
			{
				MarketStateManager.getInstance(multicastGroupName).initSequencer(udsMarket.getMarketKey());
				break search;
			}
		}

		return;
	}

}
