package com.theice.mdf.client.gui.table;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 */
public class OptionMarketTableColumn extends AbstractAppTableColumn
{
    /**
     * Column Ids
     */
    public static final int COLID_MARKETID=0;
    public static final int COLID_OPTIONTYPE=1;
    public static final int COLID_STRIKEPRICE=2;
    public static final int COLID_CONTRACTSYMBOL=3;
    public static final int COLID_TRADINGSTATUS=4;
    public static final int COLID_ORDERPRICEDENOMINATOR=5;
    public static final int COLID_SETTLEPRICEDENOMINATOR=6;
    public static final int COLID_INCREMENTQTY=7;
    public static final int COLID_LOTSIZE=8;
    public static final int COLID_DEALPRICEDENOMINATOR=9;
    public static final int COLID_MINQTY=10;
    public static final int COLID_CURRENCY=11;
    public static final int COLID_NUMDECIMALSSTRIKEPRICE=12;
    public static final int COLID_MINOPTIONSPRICE=13;
    public static final int COLID_MAXOPTIONSPRICE=14;
    public static final int COLID_INCREMENTPREMIUMPRICE=15;
    public static final int COLID_OPTIONSEXPYEAR=16;
    public static final int COLID_OPTIONSEXPMONTH=17;
    public static final int COLID_OPTIONSEXPDAY=18;
    public static final int COLID_OPTIONSSETTLEMENTTYPE=19;
    public static final int COLID_OPTIONSEXPIRATIONTYPE=20;
    public static final int COLID_SERIALUNDERLYINGMARKETID=21;

    private OptionMarketTableColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private OptionMarketTableColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static OptionMarketTableColumn MARKETID=new OptionMarketTableColumn(COLID_MARKETID,"Market Id",100,Integer.class);
    public static OptionMarketTableColumn OPTIONTYPE=new OptionMarketTableColumn(COLID_OPTIONTYPE,"Option Type",75, Character.class);
    public static OptionMarketTableColumn STRIKEPRICE=new OptionMarketTableColumn(COLID_STRIKEPRICE,"Strike Price",75,Long.class);
    public static OptionMarketTableColumn CONTRACTSYMBOL=new OptionMarketTableColumn(COLID_CONTRACTSYMBOL,"Contract Symbol",300);
    public static OptionMarketTableColumn TRADINGSTATUS=new OptionMarketTableColumn(COLID_TRADINGSTATUS,"TrStat",50);
    public static OptionMarketTableColumn ORDERPRICEDENOMINATOR=new OptionMarketTableColumn(COLID_ORDERPRICEDENOMINATOR,"Ord.Denom",50);
    public static OptionMarketTableColumn SETTLEPRICEDENOMINATOR=new OptionMarketTableColumn(COLID_SETTLEPRICEDENOMINATOR,"Stl.Denom",50);
    public static OptionMarketTableColumn INCREMENTQTY=new OptionMarketTableColumn(COLID_INCREMENTQTY,"IncQty",50,Number.class);
    public static OptionMarketTableColumn LOTSIZE=new OptionMarketTableColumn(COLID_LOTSIZE,"LotSize",50);
    public static OptionMarketTableColumn DEALPRICEDENOMINATOR=new OptionMarketTableColumn(COLID_DEALPRICEDENOMINATOR,"DealPriceDenom",50);
    public static OptionMarketTableColumn MINQTY=new OptionMarketTableColumn(COLID_MINQTY,"MinQty",50,Number.class);
    public static OptionMarketTableColumn CURRENCY=new OptionMarketTableColumn(COLID_CURRENCY,"Curr",50);
    public static OptionMarketTableColumn NUMDECIMALSSTRIKEPRICE=new OptionMarketTableColumn(COLID_NUMDECIMALSSTRIKEPRICE,"NumDecStrikePrice",50);
    public static OptionMarketTableColumn MINOPTIONSPRICE=new OptionMarketTableColumn(COLID_MINOPTIONSPRICE,"MinOptionsPrice",50);
    public static OptionMarketTableColumn MAXOPTIONSPRICE=new OptionMarketTableColumn(COLID_MAXOPTIONSPRICE,"MaxOptionsPrice",50);
    public static OptionMarketTableColumn INCREMENTPREMIUMPRICE=new OptionMarketTableColumn(COLID_INCREMENTPREMIUMPRICE,"IncPremPrice",50);
    public static OptionMarketTableColumn OPTIONSEXPYEAR=new OptionMarketTableColumn(COLID_OPTIONSEXPYEAR,"Options Exp Yr",50,Number.class);
    public static OptionMarketTableColumn OPTIONSEXPMONTH=new OptionMarketTableColumn(COLID_OPTIONSEXPMONTH,"Options Exp Mon",50,Number.class);
    public static OptionMarketTableColumn OPTIONSEXPDAY=new OptionMarketTableColumn(COLID_OPTIONSEXPDAY,"Options Exp Day",50,Number.class);
    public static OptionMarketTableColumn OPTIONSSETTLEMENTTYPE=new OptionMarketTableColumn(COLID_OPTIONSSETTLEMENTTYPE,"OptSettl.Type",50, Character.class);
    public static OptionMarketTableColumn OPTIONSEXPIRATIONTYPE=new OptionMarketTableColumn(COLID_OPTIONSEXPIRATIONTYPE,"OptExp Type",50, Character.class);
    public static OptionMarketTableColumn SERIALUNDERLYINGMARKETID=new OptionMarketTableColumn(COLID_SERIALUNDERLYINGMARKETID,"Serial Options Market Id",100,Integer.class);
    
    /**
     * All Columns
     */
    public static OptionMarketTableColumn[] columns=
        {
                MARKETID,
                OPTIONTYPE,
                STRIKEPRICE,
                CONTRACTSYMBOL,
                TRADINGSTATUS,
                ORDERPRICEDENOMINATOR,
                SETTLEPRICEDENOMINATOR,
                INCREMENTQTY,
                LOTSIZE,
                DEALPRICEDENOMINATOR,
                MINQTY,
                CURRENCY,
                NUMDECIMALSSTRIKEPRICE,
                MINOPTIONSPRICE,
                MAXOPTIONSPRICE,
                INCREMENTPREMIUMPRICE,
                OPTIONSEXPYEAR,
                OPTIONSEXPMONTH,
                OPTIONSEXPDAY,
                OPTIONSSETTLEMENTTYPE,
                OPTIONSEXPIRATIONTYPE,
                SERIALUNDERLYINGMARKETID
        };

}

