package com.theice.mdf.client.qv.domain;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.message.notification.MarkerIndexPriceMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMessageHolder
{
    private static QVMessageHolder _instance = new QVMessageHolder();

    private static Logger logger=Logger.getLogger(QVMessageHolder.class.getName());

    /**
     * HashMap of MarkerIndexPriceMessage message maps
     * Key: MarketType
     * Value: Map<Integer,MarkerIndexPriceMessage> a map of marker indices for each market
     */
    protected Map<Short,Map<Integer,MarkerIndexPriceMessage>> _qvMarkerIndexPriceMap=new HashMap();
    
    /**
     * get the singleton instance
     * @return
     */
    public static QVMessageHolder getInstance()
    {
        return _instance;
    }

    /**
     * private constructor
     */
    private QVMessageHolder()
    {
    }

    /**
     * Get the map of marker indices for a given market type
     * @param marketType
     * @return
     */
    public synchronized Map<Integer,MarkerIndexPriceMessage> getQVMarkerPriceMap(short marketType)
    {
        return(_qvMarkerIndexPriceMap.get(new Short(marketType)));
    }

    /**
     * Store the given marker price object for a market type.
     * If it already exists, just replace the object for the key
     * @param marker index price
     */
    public synchronized void storeQVMarkerPrice(MarkerIndexPriceMessage message)
    {
    	int marketId=message.getMarketID();
    	
    	MarketInterface market=MarketsHolder.getInstance().findMarket(marketId);
    	
    	if(market==null)
    	{
    		logger.warn("Market not found while processing QV Marker Price : "+message.toString());
    		return;
    	}
    	
        short marketType=market.getMarketType();
        
        Map<Integer,MarkerIndexPriceMessage> map=_qvMarkerIndexPriceMap.get(Short.valueOf(marketType));
        
        if(map==null)
        {
        	map=new HashMap<Integer,MarkerIndexPriceMessage>();
        	
        	_qvMarkerIndexPriceMap.put(Short.valueOf(marketType), map);
        }
        
    	map.put(Integer.valueOf(marketId), message);
        
        return;
    }

    /**
     * toString
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("MarketHolder=");
        buf.append("[MarkerPriceMap="+this._qvMarkerIndexPriceMap+"]");
        return(buf.toString());
    }
}
