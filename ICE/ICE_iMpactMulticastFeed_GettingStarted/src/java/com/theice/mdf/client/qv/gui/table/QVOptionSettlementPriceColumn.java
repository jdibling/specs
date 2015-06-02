package com.theice.mdf.client.qv.gui.table;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVOptionSettlementPriceColumn extends AbstractAppTableColumn
{
    public static final int COLID_MARKETID=0;
    public static final int COLID_CONTRACTSYMBOL=1;
    public static final int COLID_SETTLEMENT_PRICE=2;
    public static final int COLID_OPTIONTYPE=3;
    public static final int COLID_STRIKE_PRICE=4;
    public static final int COLID_VALUTATION_DATE=5;

    private QVOptionSettlementPriceColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private QVOptionSettlementPriceColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static QVOptionSettlementPriceColumn MARKETID=new QVOptionSettlementPriceColumn(COLID_MARKETID,"MarketId",75);
    public static QVOptionSettlementPriceColumn CONTRACTSYMBOL=new QVOptionSettlementPriceColumn(COLID_CONTRACTSYMBOL,"Contract Symbol",250);
    public static QVOptionSettlementPriceColumn SETTLEMENT_PRICE=new QVOptionSettlementPriceColumn(COLID_SETTLEMENT_PRICE,"Settlement Price",100,Number.class);
    public static QVOptionSettlementPriceColumn OPTIONTYPE=new QVOptionSettlementPriceColumn(COLID_OPTIONTYPE,"Option Type",75);
    public static QVOptionSettlementPriceColumn STRIKE_PRICE=new QVOptionSettlementPriceColumn(COLID_STRIKE_PRICE,"Strike Price",100,Number.class);
    public static QVOptionSettlementPriceColumn VALUATION_DATE=new QVOptionSettlementPriceColumn(COLID_VALUTATION_DATE,"Valuation Date",150);

    public static QVOptionSettlementPriceColumn[] columns=
        {
    			MARKETID,
    			CONTRACTSYMBOL,
    			SETTLEMENT_PRICE,
    			OPTIONTYPE,
    			STRIKE_PRICE,
    			VALUATION_DATE
        };

}

