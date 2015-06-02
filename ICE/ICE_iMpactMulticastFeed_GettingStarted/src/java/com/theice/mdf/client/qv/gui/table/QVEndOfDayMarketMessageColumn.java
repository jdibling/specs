package com.theice.mdf.client.qv.gui.table;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketMessageColumn extends AbstractAppTableColumn
{
    public static final int COLID_MARKETID=0;
    public static final int COLID_MARKETDESC=1;
    public static final int COLID_TOTALVOLUME=2;
    public static final int COLID_BLOCKVOLUME=3;
    public static final int COLID_EFSVOLUME=4;
    public static final int COLID_EFPVOLUME=5;
    public static final int COLID_OPENING_PRICE=6;
    public static final int COLID_HIGH=7;
    public static final int COLID_LOW=8;
    public static final int COLID_VWAP=9;
    public static final int COLID_SETTLEMENT_PRICE=10;
    public static final int COLID_OPEN_INTEREST=11;
    public static final int COLID_DATETIME=12;

    private QVEndOfDayMarketMessageColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private QVEndOfDayMarketMessageColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static QVEndOfDayMarketMessageColumn MARKETID=new QVEndOfDayMarketMessageColumn(COLID_MARKETID,"MarketId",75);
    public static QVEndOfDayMarketMessageColumn MARKETDESC=new QVEndOfDayMarketMessageColumn(COLID_MARKETDESC,"Market Description",320);
    public static QVEndOfDayMarketMessageColumn TOTALVOLUME=new QVEndOfDayMarketMessageColumn(COLID_TOTALVOLUME,"TotalVol",60);
    public static QVEndOfDayMarketMessageColumn BLOCKVOLUME=new QVEndOfDayMarketMessageColumn(COLID_BLOCKVOLUME,"BlockVol",60);
    public static QVEndOfDayMarketMessageColumn EFSVOLUME=new QVEndOfDayMarketMessageColumn(COLID_EFSVOLUME,"EFS Vol",60);
    public static QVEndOfDayMarketMessageColumn EFPVOLUME=new QVEndOfDayMarketMessageColumn(COLID_EFPVOLUME,"EFP Vol",60);
    public static QVEndOfDayMarketMessageColumn OPENING_PRICE=new QVEndOfDayMarketMessageColumn(COLID_OPENING_PRICE,"OpenPrice",65);
    public static QVEndOfDayMarketMessageColumn HIGH=new QVEndOfDayMarketMessageColumn(COLID_HIGH,"High",60);
    public static QVEndOfDayMarketMessageColumn LOW=new QVEndOfDayMarketMessageColumn(COLID_LOW,"Low",60);
    public static QVEndOfDayMarketMessageColumn VWAP=new QVEndOfDayMarketMessageColumn(COLID_VWAP,"VWAP",60);
    public static QVEndOfDayMarketMessageColumn SETTLEMENT_PRICE=new QVEndOfDayMarketMessageColumn(COLID_SETTLEMENT_PRICE,"Settle Price",70);
    public static QVEndOfDayMarketMessageColumn OPEN_INTEREST=new QVEndOfDayMarketMessageColumn(COLID_OPEN_INTEREST,"OpenInterest",70);
    public static QVEndOfDayMarketMessageColumn DATETIME=new QVEndOfDayMarketMessageColumn(COLID_DATETIME,"DateTime",110);

    public static QVEndOfDayMarketMessageColumn[] columns=
        {
    		MARKETID,
			MARKETDESC,
			TOTALVOLUME,
			BLOCKVOLUME,
			EFSVOLUME,
			EFPVOLUME,
			OPENING_PRICE,
			HIGH,
			LOW,
			VWAP,
			SETTLEMENT_PRICE,
			OPEN_INTEREST,
			DATETIME
        };

}

