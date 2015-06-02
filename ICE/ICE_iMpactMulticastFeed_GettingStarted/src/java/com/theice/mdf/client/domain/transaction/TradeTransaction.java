package com.theice.mdf.client.domain.transaction;

import com.theice.mdf.client.domain.Trade;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY. THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class TradeTransaction extends AbstractTransaction
{
	private Trade trade = null;

	private TradeTransaction()
	{
	}

	public TradeTransaction(Trade trade)
	{
		this(trade, 0L);
	}

	public TradeTransaction(Trade trade, long bundleSequenceNumber)
	{
		super(bundleSequenceNumber);
		this.transactionType = TransactionType.TRADE;
		this.trade = trade;
	}

	public Trade getTrade()
	{
		return (this.trade);
	}

	public int getMarketId()
	{
		return (trade.getTradeMessage().getMarketID());
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("-[Trade=" + trade.toString() + "]");
		return (buf.toString());
	}
}
