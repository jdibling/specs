package com.theice.mdf.client.domain.state;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Market type load status is used to keep track of loading the product definitions for all
 * the markets. When we request product definition for each of the market types, one of the following
 * should happen:
 * 
 * 1. Getting all the Product Definitions for the markets under the market type
 * 2. An error message indicating "no permission"
 * 3. An error message indicating "invalid market"
 * 
 * In all the above cases, we rely on the request id that we originally sent in the request,
 * so we can keep track of the status.
 * 
 * Once we are done with loading all the market types, then the multicast channel can be
 * opened for receiving and processing snapshots and realtime feed
 * 
 * We un-join from the snapshot channel, once we receive snapshots belonging to the "good"
 * markets that we find in the market type load status object
 * 
 * @author Adam Athimuthu
 */
public class MarketLoadStatus 
{
	private MarketLoadTrackingKey loadTrackingKey;
	private int _requestSequenceId;
	private int _numberOfMarketsExpected;
	private int _numberOfMarketsLoaded;

	public MarketLoadStatus(MarketLoadTrackingKey loadTrackingKey)
	{
		this.loadTrackingKey=loadTrackingKey;
	}
	
	public short getMarketType()
	{
		return(this.loadTrackingKey.getMarketType());
	}

	public int getRequestSequenceId() {
		return _requestSequenceId;
	}

	public void setRequestSequenceId(int requestSequenceId) 
	{
		this._requestSequenceId = requestSequenceId;
	}

	public int getNumberOfMarketsExpected() 
	{
		return _numberOfMarketsExpected;
	}

	public void setNumberOfMarketsExpected(int numberOfMarketsExpected) 
	{
		this._numberOfMarketsExpected = numberOfMarketsExpected;
	}

	public int getNumberOfMarketsLoaded() 
	{
		return _numberOfMarketsLoaded;
	}

	public void setNumberOfMarketsLoaded(int numberOfMarketsLoaded) 
	{
		this._numberOfMarketsLoaded = numberOfMarketsLoaded;
	}
	
	public int incrementNumberOfMarketsLoaded() 
	{
		return(++_numberOfMarketsLoaded);
	}
	
    /**
     * toString
     * @return
     */
    public String toString()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append("["+this.loadTrackingKey+"]");
        buf.append("["+this._requestSequenceId+"]");
        buf.append("["+this._numberOfMarketsExpected+"]");
        buf.append("["+this._numberOfMarketsLoaded+"]");
        
        return(buf.toString());

    }
}

