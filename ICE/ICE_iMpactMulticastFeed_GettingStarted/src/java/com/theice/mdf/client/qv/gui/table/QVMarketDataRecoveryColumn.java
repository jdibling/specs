package com.theice.mdf.client.qv.gui.table;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarketDataRecoveryColumn extends AbstractAppTableColumn
{
    public static final int COLID_MARKETID=0;
    public static final int COLID_ORDERID=1;
    public static final int COLID_ORDERSEQUENCEID=2;
    public static final int COLID_SIDE=3;
    public static final int COLID_PRICE=4;
    public static final int COLID_PRICEDENOMINATOR=5;
    public static final int COLID_QUANTITY=6;
    public static final int COLID_DATETIME=7;
    public static final int COLID_STATUS=8;
    public static final int COLID_OPTIONTYPE=9;
    public static final int COLID_STRIKEPRICE=10;
    public static final int COLID_STRIKEPRICEDENOM=11;
    public static final int COLID_TRADETYPE=12;
    public static final int COLID_ISSYSTEMPRICELEG=13;
    public static final int COLID_BUSTED=14;
    public static final int COLID_CONTRACTSYMBOL=15;
    public static final int COLID_ISCRACKSPREAD=16;
    public static final int COLID_ISCONTRA=17;

    private QVMarketDataRecoveryColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private QVMarketDataRecoveryColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static QVMarketDataRecoveryColumn MARKETID=new QVMarketDataRecoveryColumn(COLID_MARKETID,"MarketId",75);
    public static QVMarketDataRecoveryColumn ORDERID=new QVMarketDataRecoveryColumn(COLID_ORDERID,"OrderId",100);
    public static QVMarketDataRecoveryColumn ORDERSEQUENCEID=new QVMarketDataRecoveryColumn(COLID_ORDERSEQUENCEID,"OrdSeqId",50);
    public static QVMarketDataRecoveryColumn SIDE=new QVMarketDataRecoveryColumn(COLID_SIDE,"Side",40);
    public static QVMarketDataRecoveryColumn PRICE=new QVMarketDataRecoveryColumn(COLID_PRICE,"Price",60);
    public static QVMarketDataRecoveryColumn PRICEDENOMINATOR=new QVMarketDataRecoveryColumn(COLID_PRICEDENOMINATOR,"PriceDenom",50);
    public static QVMarketDataRecoveryColumn QUANTITY=new QVMarketDataRecoveryColumn(COLID_QUANTITY,"Quantity",75);
    public static QVMarketDataRecoveryColumn DATETIME=new QVMarketDataRecoveryColumn(COLID_DATETIME,"DateTime",110);
    public static QVMarketDataRecoveryColumn STATUS=new QVMarketDataRecoveryColumn(COLID_STATUS,"Status",50);
    public static QVMarketDataRecoveryColumn OPTIONTYPE=new QVMarketDataRecoveryColumn(COLID_OPTIONTYPE,"OptionType",50);
    public static QVMarketDataRecoveryColumn STRIKEPRICE=new QVMarketDataRecoveryColumn(COLID_STRIKEPRICE,"StrikePrice",75);
    public static QVMarketDataRecoveryColumn STRIKEPRICEDENOM=new QVMarketDataRecoveryColumn(COLID_STRIKEPRICEDENOM,"StrikePriceDenom",50);
    public static QVMarketDataRecoveryColumn TRADETYPE=new QVMarketDataRecoveryColumn(COLID_TRADETYPE,"TradeType",50);
    public static QVMarketDataRecoveryColumn ISSYSTEMPRICELEG=new QVMarketDataRecoveryColumn(COLID_ISSYSTEMPRICELEG,"IsSystemPricedLeg?",50);
    public static QVMarketDataRecoveryColumn BUSTED=new QVMarketDataRecoveryColumn(COLID_BUSTED,"Busted",50);
    public static QVMarketDataRecoveryColumn CONTRACTSYMBOL=new QVMarketDataRecoveryColumn(COLID_CONTRACTSYMBOL,"ContractSymbol",110);
    public static QVMarketDataRecoveryColumn ISCRACKSPREAD=new QVMarketDataRecoveryColumn(COLID_ISCRACKSPREAD,"IsCrackSpread",50);
    public static QVMarketDataRecoveryColumn ISCONTRA=new QVMarketDataRecoveryColumn(COLID_ISCONTRA,"IsContra",50);

    public static QVMarketDataRecoveryColumn[] columns=
        {
    			MARKETID,
    			ORDERID,
    			ORDERSEQUENCEID,
    			SIDE,
    			PRICE,
    			PRICEDENOMINATOR,
    			QUANTITY,
    			DATETIME,
    			STATUS,
    			OPTIONTYPE,
    			STRIKEPRICE,
    			STRIKEPRICEDENOM,
    			TRADETYPE,
    			ISSYSTEMPRICELEG,
    			BUSTED,
    			CONTRACTSYMBOL,
    			ISCRACKSPREAD,
    			ISCONTRA
        };

}

