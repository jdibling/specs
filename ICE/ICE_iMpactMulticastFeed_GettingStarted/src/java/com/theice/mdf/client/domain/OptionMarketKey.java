package com.theice.mdf.client.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Option Market Key
 * 
 * @author : Adam Athimuthu
 */
public class OptionMarketKey implements MarketKey
{
    protected final int _marketID;

    protected OptionMarketKey()
    {
    	this._marketID=-1;
    }

    public OptionMarketKey(final int marketID)
    {
    	this._marketID=marketID;
    }

	public int getMarketID() 
	{
		return _marketID;
	}
	
	public boolean isOptions()
	{
		return(true);
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
       StringBuffer buf = new StringBuffer("");
       buf.append("OptionMarketKey=[").append(_marketID).append("]");
       return(buf.toString());
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
		final OptionMarketKey other = (OptionMarketKey) obj;
		if (_marketID != other._marketID)
			return false;
		return true;
	}
}

