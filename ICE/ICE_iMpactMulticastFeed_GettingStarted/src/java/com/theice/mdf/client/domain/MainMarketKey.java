package com.theice.mdf.client.domain;

import java.io.Serializable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Key of the underlying market as well as the Option Market
 * With pre-defined options enabled, a market can either identify an options market
 * or an underlying market.
 *
 * @author : Adam Athimuthu
 */
public class MainMarketKey implements MarketKey, Serializable
{
    protected final int _marketID;
    
    protected MainMarketKey()
    {
    	_marketID=-1;
    }

    public MainMarketKey(final int marketID)
    {
        _marketID=marketID;
    }

	public int getMarketID() 
	{
		return _marketID;
	}
	
	public boolean isOptions()
	{
		return(false);
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
       StringBuffer buf = new StringBuffer("");
       buf.append("[" + this._marketID + "]");
       return (buf.toString());
    }

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + _marketID;
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MainMarketKey other = (MainMarketKey) obj;
		if (_marketID != other._marketID)
			return false;
		return true;
	}

}
