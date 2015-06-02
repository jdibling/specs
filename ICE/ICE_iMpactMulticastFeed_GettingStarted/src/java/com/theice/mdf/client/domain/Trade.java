package com.theice.mdf.client.domain;

import com.theice.mdf.message.notification.SpotMarketTradeMessage;
import com.theice.mdf.message.notification.TradeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY. THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class Trade
{
	private TradeMessage _tradeMessage = null;
	private boolean _optionsTrade = false;
	private boolean _cancelled = false;
	private boolean _adjusted = false;
	private InvestigationStatus _investigationStatus = null;
	private SpotTradeInfo _spotTradeInfo = null;
	

	/**
	 * bundle sequence number. non-zero if it is a bundled message
	 */
	private long bundleSequenceNumber = 0L;

	public Trade(TradeMessage tradeMessage)
	{
		_tradeMessage = tradeMessage;
		this._adjusted = (tradeMessage.IsAdjustedTrade == 'Y');
	}

	public Trade(SpotMarketTradeMessage spotTradeMessage)
	{
		_tradeMessage = new TradeMessage();
		_tradeMessage.setMarketID(spotTradeMessage.getMarketID());
		_tradeMessage.OrderID = spotTradeMessage.OrderID;
		_tradeMessage.DateTime = spotTradeMessage.DateTime;
		_tradeMessage.Quantity = spotTradeMessage.Quantity;
		_tradeMessage.Price = spotTradeMessage.Price;
		SpotTradeInfo spotTradeInfo = new SpotTradeInfo(spotTradeMessage.DeliveryBeginDateTime, spotTradeMessage.DeliveryEndDateTime);
		this._spotTradeInfo = spotTradeInfo;
	}

	public Trade(TradeMessage tradeMessage, boolean isOptionsTrade)
	{
		this(tradeMessage);
		this._optionsTrade = isOptionsTrade;
		this._adjusted = (tradeMessage.IsAdjustedTrade == 'Y');
	}

	public TradeMessage getTradeMessage()
	{
		return (_tradeMessage);
	}

	public boolean isCancelled()
	{
		return (_cancelled);
	}

	public boolean isSpotTrade()
	{
		return (_spotTradeInfo != null);
	}

	public void setCancelled(boolean cancelled)
	{
		_cancelled = cancelled;
	}

	public boolean isAdjusted()
	{
		return _adjusted;
	}

	public boolean isOptionsTrade()
	{
		return (_optionsTrade);
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus)
	{
		_investigationStatus = investigationStatus;
	}

	public InvestigationStatus getInvestigationStatus()
	{
		return (_investigationStatus);
	}

	public long getBundleSequenceNumber()
	{
		return (this.bundleSequenceNumber);
	}

	public void setBundleSequenceNumber(long bundleSequenceNumber)
	{
		this.bundleSequenceNumber = bundleSequenceNumber;
	}

	public SpotTradeInfo getSpotTradeInfo()
	{
		return this._spotTradeInfo;
	}
	
	/**
	 * toString
	 * 
	 * @return
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer("");

		buf.append("[OptionsTrade=" + _optionsTrade + "]");

		buf.append("[OptionsTrade?=").append(_optionsTrade).append("]");

		buf.append("[").append(_tradeMessage.toString()).append("]");

		if (_cancelled)
		{
			buf.append("[Cancelled]");
		}

		if (_adjusted)
		{
			buf.append("[Adjusted]");
		}

		if (_investigationStatus != null)
		{
			buf.append(_investigationStatus.toString());
		}

		if (this.bundleSequenceNumber != 0)
		{
			buf.append("--[BundleSeqNo:" + this.bundleSequenceNumber + "]");
		}

		return (buf.toString());
	}

}
