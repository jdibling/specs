package com.theice.mdf.client.domain.state;

import com.theice.mdf.message.request.ProductDefinitionRequest;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The load tracking key is composed of the market type and the security type (F or O)
 * 
 * @author Adam Athimuthu
 */
public class MarketLoadTrackingKey
{
	private short marketType=-1;
	private char securityType=ProductDefinitionRequest.SECURITY_TYPE_FUTRES_OTC;
	
	public MarketLoadTrackingKey(Short marketType,char securityType)
	{
		this.marketType=marketType;
		this.securityType=securityType;
	}
	
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("LoadTrackingKey=[").append(this.marketType).append("][").append(this.securityType).append("]");
		return(buf.toString());
	}
	
	public short getMarketType()
	{
		return(this.marketType);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + marketType;
		result = prime * result + securityType;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof MarketLoadTrackingKey))
			return false;
		final MarketLoadTrackingKey other = (MarketLoadTrackingKey) obj;
		if(marketType != other.marketType)
			return false;
		if(securityType != other.securityType)
			return false;
		return true;
	}

}

