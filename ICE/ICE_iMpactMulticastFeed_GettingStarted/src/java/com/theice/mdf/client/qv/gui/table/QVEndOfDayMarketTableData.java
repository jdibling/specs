package com.theice.mdf.client.qv.gui.table;

import com.theice.mdf.message.notification.EndOfDayMarketSummaryMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketTableData 
{
	protected int _marketId;
	protected String _marketDescription=null;
    protected EndOfDayMarketSummaryMessage _endOfDayMarketSummary=null;
    
    public QVEndOfDayMarketTableData(final int marketId, final String marketDescription, final EndOfDayMarketSummaryMessage endOfDayMarketSummary)
    {
    	_marketId=marketId;
    	_marketDescription=marketDescription;
        _endOfDayMarketSummary=endOfDayMarketSummary;
    }
    
    public int getMarketId()
    {
    	return(this._marketId);
    }
    
    public String getMarketDescription()
    {
    	return(this._marketDescription);
    }
    
    public EndOfDayMarketSummaryMessage getEndOfDayMarketSummary()
    {
    	return(_endOfDayMarketSummary);
    }
	
}
