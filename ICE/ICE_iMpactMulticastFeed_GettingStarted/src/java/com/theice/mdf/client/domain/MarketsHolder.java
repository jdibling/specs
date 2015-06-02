/**
 * Created by IntelliJ IDEA.
 * User: aathimut
 * Date: Aug 3, 2007
 * Time: 3:05:36 PM
 * To change this template use File | Settings | File Templates.
 */
package com.theice.mdf.client.domain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfigRepository;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The Market Holder holds all the market objects for each market type
 * 
 * @author Adam Athimuthu Date: Aug 6, 2007 Time: 10:10:13 AM
 */
public class MarketsHolder implements Serializable
{
	public static final String STATIC_DATA_FILE_NAME = "staticData.dat";

	private static MarketsHolder _instance = new MarketsHolder();
	private static Logger logger = Logger.getLogger(MarketsHolder.class.getName());
	private static MDFClientConfigRepository currentConfigRepository = null;
   private static Map<Short,List<String>> marketTypeCodeToMulticastGroupNameMap=null;

	/**
	 * HashMap of marketType and MarketsInfo Key: MarketType Value: MarketsInfo
	 */
	protected Map<Short, MarketsInfo> _markets = new HashMap<Short, MarketsInfo>();

	/**
	 * Map of all markets (both options and underlying)
	 */
	protected Map<Integer, MarketInterface> _allMarkets = new HashMap<Integer, MarketInterface>();

	/**
	 * Map of all underlying markets
	 */
	protected Map<Integer, MarketInterface> _allUnderlyingMarkets = new HashMap<Integer, MarketInterface>();

	/**
	 * Map of all options markets
	protected Map<Integer, MarketInterface> _allOptionsMarkets = new HashMap<Integer, MarketInterface>();
	 */

   /**
    * private constructor
    */
   private MarketsHolder()
   {
   }
   
	/**
	 * get the singleton instance
	 * 
	 * @return
	 */
	public synchronized static MarketsHolder getInstance()
	{
		return _instance;
	}
	
   public synchronized static void init(List<String> interestedMCGroupNames)
   {
      currentConfigRepository = MDFClientConfigurator.getInstance().getConfigRepository();
      marketTypeCodeToMulticastGroupNameMap = currentConfigRepository.getMulticastGroupNameListMapKeyedByMarketTypeCode();
   }

	/**
	 * Get the markets for the given market type
	 * 
	 * @param marketType
	 * @return
	 */
	protected MarketsInfo getMarketsInfo(short marketType)
	{
		return(_markets.get(new Short(marketType)));
	}

	/**
	 * find market by the given market id
	 * 
	 * @param marketId
	 * @return
	 */
	public MarketInterface findMarket(int marketId)
	{
		return(_allMarkets.get(new Integer(marketId)));
	}

	/**
	 * Get all markets
	 * 
	 * @return
	 */
	public synchronized MarketInterface[] getAllMarkets()
	{
		return(_allMarkets.values().toArray(new MarketInterface[0]));
	}

	/**
	 * Store the given market into the correct map that belongs to the
	 * particular market type If there is already an entry, just replace it
	 * 
	 * @param market
	 */
	public synchronized void storeMarket(MarketInterface market)
	{
		if(!market.isOptionMarket())
		{
			short marketType = market.getMarketType();

			MarketsInfo marketsInfo = getMarketsInfo(marketType);

			if(marketsInfo == null)
			{
				marketsInfo = new MarketsInfo(marketType);
			}

			marketsInfo.storeMarket(market);

			_markets.put(new Short(marketType), marketsInfo);
		}
		else
		{
			if(logger.isTraceEnabled())
			{
				logger.trace("### Adding options market to the underlying market.");
			}
			
			((Market) market.getUnderlyingMarket()).addOptionsMarket((OptionMarket) market);
		}

		/**
		 * Update the hash table of all markets (for both underlying and options markets)
		 */
		_allMarkets.put(new Integer(market.getMarketID()), market);

		return;
	}

	/**
	 * initialize all markets
	 */
	public synchronized void initialize(String multicastGroupName)
	{
		if(logger.isTraceEnabled())
		{
			logger.trace("Initializing all markets. Number of markets : "+_allMarkets.size());
		}

		if(_allMarkets.size() == 0)
		{
			System.err.println("No markets found while trying to initialize");
			return;
		}

		for(Iterator<MarketInterface> it = _allMarkets.values().iterator();it.hasNext();)
		{
			MarketInterface market = it.next();
			List<String> mcGroupsContainingThisMarketType = marketTypeCodeToMulticastGroupNameMap.get(market.getMarketType());
						
			if (mcGroupsContainingThisMarketType.contains(multicastGroupName))
			{
			   if ((!multicastGroupName.endsWith("Options") && !market.isOptionMarket()) ||
                 (multicastGroupName.endsWith("Options") && market.isOptionMarket()) )
			   {
			      market.initialize();
			   }
			}
		}

		System.out.println("*** Done initializing all markets for group: "+multicastGroupName);

		return;
	}
	
	public synchronized void clearAllMarkets()
	{
	   _markets.clear();
	   _allMarkets.clear();
	   _allUnderlyingMarkets.clear();
	}
	
	public synchronized static void serializeTofile() throws Exception 
	{
	   FileOutputStream fos = null;
	   ObjectOutputStream out = null;

	   fos = new FileOutputStream(STATIC_DATA_FILE_NAME);
	   out = new ObjectOutputStream(fos);
	   out.writeObject(MarketsHolder.getInstance());
	   out.close();
	}  

	public synchronized static void deserializeFromFile() throws Exception
	{
	   FileInputStream fis = null;
	   ObjectInputStream in = null;

	   fis = new FileInputStream(STATIC_DATA_FILE_NAME);
	   in = new ObjectInputStream(fis);
	   _instance = (MarketsHolder)in.readObject();
	   in.close();
	}

	/**
	 * toString
	 * 
	 * @return
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer("MarketHolder=");
		buf.append("[" + this._markets + "]");
		return(buf.toString());
	}
}
