package com.theice.mdf.client.domain.transaction;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public interface Transaction
{
    public TransactionType getTransactionType();
    public long getBundleSequenceNumber();
    public int getMarketId();
}

