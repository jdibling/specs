package com.theice.mdf.client.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * This object holds all markets (underlying as well as options) for a given market type
 * 
 * @author Adam Athimuthu
 * Date: Aug 3, 2007
 * Time: 1:39:15 PM
 */
public class MarketsInfo implements Serializable
{
    /**
     * Key: MarketId
     * Value: MarketInterface
     */
    private Map<Integer,MarketInterface> _markets=new HashMap<Integer,MarketInterface>();
    private short _marketType=0;

    /**
     * Construct the markets object for a given market type
     * @param marketType
     */
    public MarketsInfo(short marketType)
    {
        _marketType=marketType;
    }

    /**
     * get the markets Map
     * @return
     */
    public Map getMarkets()
    {
        return(_markets);
    }

    /**
     * get the market type
     * @return
     */
    public int getMarketType()
    {
        return(_marketType);
    }

    /**
     * Stores the market in the internal hash table keyed by market ID
     * If the market already exists, the entry is replaced
     * @param market
     */
    public void storeMarket(MarketInterface market)
    {
        _markets.put(Integer.valueOf(market.getMarketID()),market);
    }

    /**
     * Retrieves the market stored in the internal hash table
     * @param marketId
     * @return Market
     */
    public MarketInterface retriveMarket(int marketId)
    {
        return(_markets.get(Integer.valueOf(marketId)));
    }

    /**
     * toString
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append("["+_marketType+"]");

        if(_markets!=null)
        {
            for(Iterator<MarketInterface> it=_markets.values().iterator();it.hasNext();)
            {
            	MarketInterface theMarket=it.next();
                buf.append(theMarket.toString()+"\n");
            }
        }
        buf.append("\n");
        return(buf.toString());
    }

}

