package com.theice.mdf.client.gui.table;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 * Date: Aug 8, 2007
 * Time: 3:28:03 PM
 */
public class MarketTableColumn extends AbstractAppTableColumn
{
    /**
     * Column Ids
     */
    public static final int COLID_MARKETID=0;
    public static final int COLID_MARKETDESC=1;
    public static final int COLID_TRADINGSTATUS=2;
    public static final int COLID_ORDERPRICEDENOMINATOR=3;
    public static final int COLID_SETTLEPRICEDENOMINATOR=4;
    public static final int COLID_INCREMENTPRICE=5;
    public static final int COLID_INCREMENTQTY=6;
    public static final int COLID_LOTSIZE=7;
    public static final int COLID_MATURITYYEAR=8;
    public static final int COLID_MATURITYMONTH=9;
    public static final int COLID_MATURITYDAY=10;
    public static final int COLID_ISSPREAD=11;
    public static final int COLID_MINQTY=12;
    public static final int COLID_UNITQUANTITY=13;
    public static final int COLID_CURRENCY=14;
    public static final int COLID_CONTRACTSYMBOL=15;
    public static final int COLID_ISCRACKSPREAD=16;
    public static final int COLID_PRIMARYMARKETID=17;
    public static final int COLID_SECONDARYMARKETID=18;
    public static final int COLID_ISOPTIONS=19;
    public static final int COLID_OPTIONTYPE=20;
    public static final int COLID_STRIKEPRICE=21;
    public static final int COLID_SECONDSTRIKE=22;
    public static final int COLID_DEALPRICEDENOMINATOR=23;
    public static final int COLID_MINSTRIKEPRICE=24;
    public static final int COLID_MAXSTRIKEPRICE=25;
    public static final int COLID_INCREMENTSTRIKEPRICE=26;
    public static final int COLID_NUMDECIMALSSTRIKEPRICE=27;
    public static final int COLID_MINOPTIONSPRICE=28;
    public static final int COLID_MAXOPTIONSPRICE=29;
    public static final int COLID_INCREMENTOPTIONSPRICE=30;
    public static final int COLID_NUMDECIMALSOPTIONSPRICE=31;
    public static final int COLID_TICKVALUE=32;
    public static final int COLID_ALLOWOPTIONS=33;
    public static final int COLID_CLEAREDALIAS=34;
    public static final int COLID_ALLOWIMPLIED=35;
    public static final int COLID_OPTIONSEXPYEAR=36;
    public static final int COLID_OPTIONSEXPMONTH=37;
    public static final int COLID_OPTIONSEXPDAY=38;
    public static final int COLID_MINPRICE=39;
    public static final int COLID_MAXPRICE=40;
    public static final int COLID_PRODUCTID=41;
    public static final int COLID_PRODUCTNAME=42;
    public static final int COLID_HUBID=43;
    public static final int COLID_HUBALIAS=44;
    public static final int COLID_STRIPID=45;
    public static final int COLID_STRIPNAME=46;
    public static final int COLID_RESERVEDFLD1=47;
    public static final int COLID_ISFORSERIALOPTIONS=48;
    public static final int COLID_ISTRADABLE=49;
   
    /**
     * Constructor
     */
    private MarketTableColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private MarketTableColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static MarketTableColumn ID=new MarketTableColumn(COLID_MARKETID,"Id",50,Integer.class);
    public static MarketTableColumn DESC=new MarketTableColumn(COLID_MARKETDESC,"Name",350);
    public static MarketTableColumn TRADINGSTATUS=new MarketTableColumn(COLID_TRADINGSTATUS,"TrStat",50);
    public static MarketTableColumn ORDERPRICEDENOMINATOR=new MarketTableColumn(COLID_ORDERPRICEDENOMINATOR,"Ord.Denom",50);
    public static MarketTableColumn SETTLEPRICEDENOMINATOR=new MarketTableColumn(COLID_SETTLEPRICEDENOMINATOR,"Stl.Denom",50);
    public static MarketTableColumn INCREMENTPRICE=new MarketTableColumn(COLID_INCREMENTPRICE,"IncPrice",50, Number.class);
    public static MarketTableColumn INCREMENTQTY=new MarketTableColumn(COLID_INCREMENTQTY,"IncQty",50,Number.class);
    public static MarketTableColumn LOTSIZE=new MarketTableColumn(COLID_LOTSIZE,"LotSize",50);
    public static MarketTableColumn MATURITYYEAR=new MarketTableColumn(COLID_MATURITYYEAR,"MatYr",50,Number.class);
    public static MarketTableColumn MATURITYMONTH=new MarketTableColumn(COLID_MATURITYMONTH,"MatMon",50,Number.class);
    public static MarketTableColumn MATURITYDAY=new MarketTableColumn(COLID_MATURITYDAY,"MatDay",50,Number.class);
    public static MarketTableColumn ISSPREAD=new MarketTableColumn(COLID_ISSPREAD,"Spread?",50);
    public static MarketTableColumn MINQTY=new MarketTableColumn(COLID_MINQTY,"MinQty",50,Number.class);
    public static MarketTableColumn UNITQUANTITY=new MarketTableColumn(COLID_UNITQUANTITY,"UnitQty",50,Number.class);
    public static MarketTableColumn CURRENCY=new MarketTableColumn(COLID_CURRENCY,"Curr",50);
    public static MarketTableColumn CONTRACTSYMBOL=new MarketTableColumn(COLID_CONTRACTSYMBOL,"Symbol",300);
    public static MarketTableColumn ISCRACKSPREAD=new MarketTableColumn(COLID_ISCRACKSPREAD,"CrackSpread?",50);
    public static MarketTableColumn PRIMARYMARKETID=new MarketTableColumn(COLID_PRIMARYMARKETID,"PrimaryMktId",50);
    public static MarketTableColumn SECONDARYMARKETID=new MarketTableColumn(COLID_SECONDARYMARKETID,"SecMktId",50);
    public static MarketTableColumn ISOPTIONS=new MarketTableColumn(COLID_ISOPTIONS,"Options?",50);
    public static MarketTableColumn OPTIONTYPE=new MarketTableColumn(COLID_OPTIONTYPE,"OptionType",50);
    public static MarketTableColumn STRIKEPRICE=new MarketTableColumn(COLID_STRIKEPRICE,"StrikePrice",50);
    public static MarketTableColumn SECONDSTRIKE=new MarketTableColumn(COLID_SECONDSTRIKE,"SecondStrike",50);
    public static MarketTableColumn DEALPRICEDENOMINATOR=new MarketTableColumn(COLID_DEALPRICEDENOMINATOR,"DealPriceDenom",50);
    public static MarketTableColumn MINSTRIKEPRICE=new MarketTableColumn(COLID_MINSTRIKEPRICE,"MinStrikePrice",50);
    public static MarketTableColumn MAXSTRIKEPRICE=new MarketTableColumn(COLID_MAXSTRIKEPRICE,"MaxStrikePrice",50);
    public static MarketTableColumn INCREMENTSTRIKEPRICE=new MarketTableColumn(COLID_INCREMENTSTRIKEPRICE,"IncStrikePrice",50);
    public static MarketTableColumn NUMDECIMALSSTRIKEPRICE=new MarketTableColumn(COLID_NUMDECIMALSSTRIKEPRICE,"NumDecStrikePrice",50);
    public static MarketTableColumn MINOPTIONSPRICE=new MarketTableColumn(COLID_MINOPTIONSPRICE,"MinOptionsPrice",50);
    public static MarketTableColumn MAXOPTIONSPRICE=new MarketTableColumn(COLID_MAXOPTIONSPRICE,"MaxOptionsPrice",50);
    public static MarketTableColumn INCREMENTOPTIONSPRICE=new MarketTableColumn(COLID_INCREMENTOPTIONSPRICE,"IncOptionsPrice",50);
    public static MarketTableColumn NUMDECIMALSOPTIONSPRICE=new MarketTableColumn(COLID_NUMDECIMALSOPTIONSPRICE,"NumDecOptionsPrice",50);
    public static MarketTableColumn TICKVALUE=new MarketTableColumn(COLID_TICKVALUE,"TickValue",50);
    public static MarketTableColumn ALLOWOPTIONS=new MarketTableColumn(COLID_ALLOWOPTIONS,"Allow Options",50);
    public static MarketTableColumn CLEAREDALIAS=new MarketTableColumn(COLID_CLEAREDALIAS,"Cleared Alias",100);
    public static MarketTableColumn ALLOWIMPLIED=new MarketTableColumn(COLID_ALLOWIMPLIED,"Allow Implied",50);
    public static MarketTableColumn OPTIONSEXPYEAR=new MarketTableColumn(COLID_OPTIONSEXPYEAR,"Options Exp Yr",50,Number.class);
    public static MarketTableColumn OPTIONSEXPMONTH=new MarketTableColumn(COLID_OPTIONSEXPMONTH,"Options Exp Mon",50,Number.class);
    public static MarketTableColumn OPTIONSEXPDAY=new MarketTableColumn(COLID_OPTIONSEXPDAY,"Options Exp Day",50,Number.class);
    public static MarketTableColumn MINPRICE=new MarketTableColumn(COLID_MINPRICE,"MinPrice",50,Number.class);
    public static MarketTableColumn MAXPRICE=new MarketTableColumn(COLID_MAXPRICE,"MaxPrice",50,Number.class);
    public static MarketTableColumn PRODUCTID=new MarketTableColumn(COLID_PRODUCTID,"ProductId",80,Short.class);
    public static MarketTableColumn PRODUCTNAME=new MarketTableColumn(COLID_PRODUCTNAME,"ProductName",220);
    public static MarketTableColumn HUBID=new MarketTableColumn(COLID_HUBID,"HubId",50,Short.class);
    public static MarketTableColumn HUBALIAS=new MarketTableColumn(COLID_HUBALIAS,"HubAlias",100);
    public static MarketTableColumn STRIPID=new MarketTableColumn(COLID_STRIPID,"StripId",50,Short.class);
    public static MarketTableColumn STRIPNAME=new MarketTableColumn(COLID_STRIPNAME,"StripName",200);
    public static MarketTableColumn RESERVEDFLD1=new MarketTableColumn(COLID_RESERVEDFLD1,"ReservedFld1",50);
    public static MarketTableColumn ISFORSERIALOPTIONS=new MarketTableColumn(COLID_ISFORSERIALOPTIONS,"SerialOptions?",50);
    public static MarketTableColumn ISTRADABLE=new MarketTableColumn(COLID_ISTRADABLE,"Tradable?",50);


    /**
     * All Columns
     */
    public static MarketTableColumn[] columns=
    {
       ID,
       DESC,
       TRADINGSTATUS,
       ORDERPRICEDENOMINATOR,
       SETTLEPRICEDENOMINATOR,
       INCREMENTPRICE,
       INCREMENTQTY,
       LOTSIZE,
       MATURITYYEAR,
       MATURITYMONTH,
       MATURITYDAY,
       ISSPREAD,
       MINQTY,
       UNITQUANTITY,
       CURRENCY,
       CONTRACTSYMBOL,
       ISCRACKSPREAD,
       PRIMARYMARKETID,
       SECONDARYMARKETID,
       ISOPTIONS,
       OPTIONTYPE,
       STRIKEPRICE,
       SECONDSTRIKE,
       DEALPRICEDENOMINATOR,
       MINSTRIKEPRICE,
       MAXSTRIKEPRICE,
       INCREMENTSTRIKEPRICE,
       NUMDECIMALSSTRIKEPRICE,
       MINOPTIONSPRICE,
       MAXOPTIONSPRICE,
       INCREMENTOPTIONSPRICE,
       NUMDECIMALSOPTIONSPRICE,
       TICKVALUE,
       ALLOWOPTIONS,
       CLEAREDALIAS,
       ALLOWIMPLIED,
       OPTIONSEXPYEAR,
       OPTIONSEXPMONTH,
       OPTIONSEXPDAY,
       MINPRICE,
       MAXPRICE,
       PRODUCTID,
       PRODUCTNAME,
       HUBID,
       HUBALIAS,
       STRIPID,
       STRIPNAME,
       RESERVEDFLD1,
       ISFORSERIALOPTIONS,
       ISTRADABLE
    };

}

