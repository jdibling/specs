package com.theice.mdf.client.domain.transaction;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Transaction type enumeration indicates whether an order message is for Add/Modify, Delete or Trade
 * 
 * @author Adam Athimuthu
 */
public enum TransactionType 
{
	ADDMODIFY("AddModify"),
	DELETE("Delete"),
	TRADE("Trade"),
	PRICELEVELDUMMY("PriceLevelDummy"),
	UNKNOWN("Unknown");
	
	protected String action="";
	
	TransactionType(String action)
	{
		this.action=action;
	}
	
	public String toString()
	{
		return(action);
	}
}

