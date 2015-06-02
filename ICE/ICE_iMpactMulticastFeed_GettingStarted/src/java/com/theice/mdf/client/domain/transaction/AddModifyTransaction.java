package com.theice.mdf.client.domain.transaction;

import com.theice.mdf.client.domain.MarketOrder;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class AddModifyTransaction extends AbstractTransaction
{
	private MarketOrder marketOrder=null;
	
	private AddModifyTransaction()
	{
	}

	public AddModifyTransaction(MarketOrder order)
	{
		this(order,0L);
	}

	public AddModifyTransaction(MarketOrder order,long bundleSequenceNumber)
	{
		super(bundleSequenceNumber);
		this.transactionType=TransactionType.ADDMODIFY;
		this.marketOrder=order;
	}
	
    public int getMarketId()
    {
    	return(marketOrder.getMarketID());
    }
	
	public MarketOrder getOrder()
	{
		return(this.marketOrder);
	}

    public String toString()
    {
    	StringBuffer buf=new StringBuffer(super.toString());
    	buf.append("-"+marketOrder.toString());
    	return(buf.toString());
    }
}

