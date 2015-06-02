package com.theice.mdf.client.domain.transaction;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractTransaction implements Transaction
{
    protected long bundleSequenceNumber=0L;
    protected TransactionType transactionType=TransactionType.UNKNOWN;
    
    protected AbstractTransaction()
    {
    }

    protected AbstractTransaction(long bundleSequenceNumber)
    {
    	this.bundleSequenceNumber=bundleSequenceNumber;
    }

    public long getBundleSequenceNumber()
    {
    	return(bundleSequenceNumber);
    }
    
    public TransactionType getTransactionType()
    {
    	return(this.transactionType);
    }

    public String toString()
    {
    	StringBuffer buf=new StringBuffer();
    	
    	buf.append("["+transactionType.toString()+"]");
    	
    	if(bundleSequenceNumber>0)
    	{
        	buf.append("[Bundle="+bundleSequenceNumber+"]");
    	}
    	else
    	{
        	buf.append("[NonBundle]");
    	}
    	
    	return(buf.toString());
    }
}

