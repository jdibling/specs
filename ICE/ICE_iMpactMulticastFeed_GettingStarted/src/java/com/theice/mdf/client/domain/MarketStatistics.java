package com.theice.mdf.client.domain;

import com.theice.mdf.message.notification.MarketSnapshotMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TODO Use trading status to update the underlying market's state
 * 
 * @author Adam Athimuthu
 * Date: Aug 7, 2007
 * Time: 3:33:02 PM
 */
public class MarketStatistics
{
   private short _marketType;
	private int _marketID;
	private char _tradingStatus;
	private int _totalVolume;
	private int _blockVolume;
	private int _efsVolume;
	private int _efpVolume;
	private int _openInterest;
	private String _openInterestDate="";
	private long _openingPrice;
	private long _settlementPrice;
	private long _settleDateTime;
	private char _settlementOfficial;
	private long _high;
	private long _low;
	private long _vwap;
	private int _numOfBookEntries;
	private long _lastTradePrice;
	private int _lastTradeQuantity;
	private long _lastTradeDateTime;

    public MarketStatistics()
    {
    }
    
    public MarketStatistics(MarketSnapshotMessage snapshot)
    {
		_marketType=snapshot.MarketType;
		_marketID=snapshot.getMarketID();
		_tradingStatus=snapshot.TradingStatus;
		_totalVolume=snapshot.TotalVolume;
		_blockVolume=snapshot.BlockVolume;
		_efsVolume=snapshot.EFSVolume;
		_efpVolume=snapshot.EFPVolume;
		_openInterest=snapshot.OpenInterest;
		char[] oiDate = snapshot.OpenInterestDate;
		if (oiDate!=null && oiDate.length>0 && oiDate[0]!='\0')
		{
		   _openInterestDate=new String(oiDate);
		}
		_openingPrice=snapshot.OpeningPrice;
		_settlementPrice=snapshot.SettlementPrice;
		_settleDateTime=snapshot.SettlementPriceDateTime;
		_settlementOfficial=snapshot.IsSettlementPriceOfficial;
		_high=snapshot.High;
		_low=snapshot.Low;
		_vwap=snapshot.VWAP;
		_numOfBookEntries=snapshot.NumOfBookEntries;
		_lastTradePrice=snapshot.LastTradePrice;
		_lastTradeQuantity=snapshot.LastTradeQuantity;
		_lastTradeDateTime=snapshot.LastTradeDateTime;
    }
    

	public short getMarketType()
	{
		return _marketType;
	}

	public int getMarketID()
	{
		return _marketID;
	}

	public char getTradingStatus()
	{
		return _tradingStatus;
	}

	public int getTotalVolume()
	{
		return _totalVolume;
	}

	public int getBlockVolume()
	{
		return _blockVolume;
	}

	public int getEfsVolume()
	{
		return _efsVolume;
	}

	public int getEfpVolume()
	{
		return _efpVolume;
	}

	public int getOpenInterest()
	{
		return _openInterest;
	}
	
	public String getOpenInterestDate()
	{
	   return _openInterestDate;
	}

	public long getOpeningPrice()
	{
		return _openingPrice;
	}

	public long getSettlementPrice()
	{
		return _settlementPrice;
	}
	
	public long getSettleDateTime()
	{
	   return _settleDateTime;
	}
	
	public char getSettlementOfficial()
	{
	   return _settlementOfficial;
	}

	public long getHigh()
	{
		return _high;
	}

	public long getLow()
	{
		return _low;
	}

	public long getVwap()
	{
		return _vwap;
	}

	public int getNumOfBookEntries()
	{
		return _numOfBookEntries;
	}

	public long getLastTradePrice()
	{
		return _lastTradePrice;
	}

	public int getLastTradeQuantity()
	{
		return _lastTradeQuantity;
	}

	public long getLastTradeDateTime()
	{
		return _lastTradeDateTime;
	}

	public void setMarketType(short type)
	{
		_marketType = type;
	}

	public void setMarketID(int _marketid)
	{
		_marketID = _marketid;
	}

	public void setTradingStatus(char status)
	{
		_tradingStatus = status;
	}

	public void setTotalVolume(int volume)
	{
		_totalVolume = volume;
	}

	public void setBlockVolume(int volume)
	{
		_blockVolume = volume;
	}

	public void setEfsVolume(int volume)
	{
		_efsVolume = volume;
	}

	public void setEfpVolume(int volume)
	{
		_efpVolume = volume;
	}

	public void setOpenInterest(int interest)
	{
		_openInterest = interest;
	}
	
	public void setOpenInterestDate(String date)
	{
	   _openInterestDate = date;
	}

	public void setOpeningPrice(long price)
	{
		_openingPrice = price;
	}

	public void setSettlementPrice(long price)
	{
		_settlementPrice = price;
	}
	
	public void setSettlePriceDateTime(long dateTime)
	{
	   _settleDateTime = dateTime;
	}
	
	public void setSettlementOfficial(char isOfficial)
	{
	   _settlementOfficial = isOfficial;
	}

	public void setHigh(long _high)
	{
		this._high = _high;
	}

	public void setLow(long _low)
	{
		this._low = _low;
	}

	public void setVwap(long _vwap)
	{
		this._vwap = _vwap;
	}

	public void setNumOfBookEntries(int numOfBookEntries)
	{
		_numOfBookEntries = numOfBookEntries;
	}

	public void setLastTradePrice(long tradePrice)
	{
		_lastTradePrice = tradePrice;
	}

	public void setLastTradeQuantity(int tradeQuantity)
	{
		_lastTradeQuantity = tradeQuantity;
	}

	public void setLastTradeDateTime(long tradeDateTime)
	{
		_lastTradeDateTime = tradeDateTime;
	}

    /**
     * to String
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("Market Statistics");
		buf.append("[MarketType="+_marketType+"]");
		buf.append("[MarketId="+_marketID+"]");
		buf.append("[TradingStatus="+_tradingStatus+"]");
		buf.append("[TotalVol="+_totalVolume+"]");
		buf.append("[BlockVol="+_blockVolume+"]");
		buf.append("[EFS="+_efsVolume+"]");
		buf.append("[EFP="+_efpVolume+"]");
		buf.append("[OpenInterest="+_openInterest+"]");
		buf.append("[OpenInterestDate="+_openInterestDate+"]");
		buf.append("[OpeningPrice="+_openingPrice+"]");
		buf.append("[SettlementPrice="+_settlementPrice+"]");
		buf.append("[High="+_high+"]");
		buf.append("[Low="+_low+"]");
		buf.append("[VWAP="+_vwap+"]");
		buf.append("[NumBookEntries="+_numOfBookEntries+"]");
		buf.append("[LastTradePrice="+_lastTradePrice+"]");
		buf.append("[LastTradeQty="+_lastTradeQuantity+"]");
		buf.append("[LastTradeDateTime="+_lastTradeDateTime+"]");
        return(buf.toString());
    }
}
