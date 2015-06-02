package com.theice.mdf.client.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 13, 2007
 * Time: 10:46:58 AM
 */
public class MarketType
{
    private String _marketTypeCode=null;

    private String _marketTypeDesc=null;

    /**
     * The various multicast groups that this market type can be part of
     */
    private List<String> multicastGroups=new ArrayList<String>();

    /**
     * Constructor
     * @param marketTypeCode
     * @param marketTypeDesc
     */
    public MarketType(String marketTypeCode, String marketTypeDesc)
    {
        this._marketTypeCode=marketTypeCode;
        this._marketTypeDesc=marketTypeDesc;
    }

    /**
     * Constructor
     * @param marketTypeCode
     * @param marketTypeDesc
     * @param multicastGroups
     */
    public MarketType(String marketTypeCode, String marketTypeDesc, List<String> multicastGroups)
    {
    	this(marketTypeCode,marketTypeDesc);
    	this.multicastGroups=multicastGroups;
    }

    public String getMarketTypeCode()
    {
        return _marketTypeCode;
    }

    public void setMarketTypeCode(String marketTypeCode)
    {
        this._marketTypeCode = marketTypeCode;
    }

    public String getMarketTypeDesc()
    {
        return _marketTypeDesc;
    }

    public void setMarketTypeDesc(String marketTypeDesc)
    {
        this._marketTypeDesc = marketTypeDesc;
    }
    
    public List<String> getMulticastGroups()
    {
    	return(this.multicastGroups);
    }
    
    public void setMulticastGroup(List<String> multicastGroups)
    {
    	this.multicastGroups=multicastGroups;
    }

    public String getDisplayableMarketTypeInfo()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append(_marketTypeCode);
        buf.append(" - ["+_marketTypeDesc+"]");
        return(buf.toString());
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_marketTypeCode == null) ? 0 : _marketTypeCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MarketType other = (MarketType) obj;
		if (_marketTypeCode == null) {
			if (other._marketTypeCode != null)
				return false;
		} else if (!_marketTypeCode.equals(other._marketTypeCode))
			return false;
		return true;
	}

	/**
     * toString
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append(_marketTypeCode);
        buf.append(" - ["+_marketTypeDesc+"] ");

        buf.append("MulticastGroups=[");
        if(this.multicastGroups!=null)
        {
            buf.append(this.multicastGroups.toString());
        }
        else
        {
            buf.append("None Specified");
        }
        buf.append("]");

        return(buf.toString());
    }
}

