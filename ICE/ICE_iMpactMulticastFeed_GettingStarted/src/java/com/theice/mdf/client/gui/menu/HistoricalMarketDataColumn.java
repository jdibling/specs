package com.theice.mdf.client.gui.menu;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataColumn extends AbstractAppTableColumn
{
    public static final int COLID_MARKETID=0;
    public static final int COLID_MESSAGESTRING=1;

    private HistoricalMarketDataColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private HistoricalMarketDataColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static HistoricalMarketDataColumn MARKETID=new HistoricalMarketDataColumn(COLID_MARKETID,"MarketId",75);
    public static HistoricalMarketDataColumn MESSAGESTRING=new HistoricalMarketDataColumn(COLID_MESSAGESTRING,"Message",500);

    public static HistoricalMarketDataColumn[] columns=
        {
    			MARKETID,
    			MESSAGESTRING
        };

}

