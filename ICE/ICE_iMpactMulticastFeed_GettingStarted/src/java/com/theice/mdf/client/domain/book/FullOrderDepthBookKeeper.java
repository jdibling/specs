package com.theice.mdf.client.domain.book;

import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.domain.transaction.Transaction;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Message processing for the FullOrder Depth Context
 * Full Order Depth order processing only applies to Future/OTC markets
 * 
 * @see AbstractMarketBase
 * @author : Adam Athimuthu
 */
public interface FullOrderDepthBookKeeper
{
	public void addOrder(AddModifyTransaction transaction) throws UnsupportedOperationException;
    public MarketOrder removeOrder(long orderId,Transaction triggeringTransaction) throws UnsupportedOperationException;
}
