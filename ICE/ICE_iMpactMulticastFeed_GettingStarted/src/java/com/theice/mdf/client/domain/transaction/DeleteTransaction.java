package com.theice.mdf.client.domain.transaction;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class DeleteTransaction extends AbstractTransaction
{
    private int marketId=(-1);
    private long orderId=0L;
	
	private DeleteTransaction()
	{
	}

	public DeleteTransaction(int marketId,long orderId)
	{
		this(marketId,orderId,0L);
	}
	
	public DeleteTransaction(int marketId,long orderId,long bundleSequenceNumber)
	{
		super(bundleSequenceNumber);
		this.marketId=marketId;
		this.orderId=orderId;
		this.transactionType=TransactionType.DELETE;
	}
	
	public long getOrderId()
	{
		return(this.orderId);
	}

	public int getMarketId()
	{
		return(this.marketId);
	}

    public String toString()
    {
    	StringBuffer buf=new StringBuffer(super.toString());
    	buf.append("-[Order="+orderId+"]");
    	return(buf.toString());
    }
}

