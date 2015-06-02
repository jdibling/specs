package com.theice.mdf.client.qv.gui.table;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVOptionOpenInterestColumn extends AbstractAppTableColumn
{
    public static final int COLID_MARKETID=0;
    public static final int COLID_CONTRACTSYMBOL=1;
    public static final int COLID_OPEN_INTEREST=2;
    public static final int COLID_OPTIONTYPE=3;
    public static final int COLID_STRIKE_PRICE=4;
    public static final int COLID_PUBLISHED_DATE=5;
    public static final int COLID_OPEN_INTEREST_DATE=6;
    public static final int COLID_EXPIRY_YYMM=7;

    private QVOptionOpenInterestColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private QVOptionOpenInterestColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static QVOptionOpenInterestColumn MARKETID=new QVOptionOpenInterestColumn(COLID_MARKETID,"MarketId",75);
    public static QVOptionOpenInterestColumn CONTRACTSYMBOL=new QVOptionOpenInterestColumn(COLID_CONTRACTSYMBOL,"Contract Symbol",150);
    public static QVOptionOpenInterestColumn OPEN_INTEREST=new QVOptionOpenInterestColumn(COLID_OPEN_INTEREST,"Open Int",80,Number.class);
    public static QVOptionOpenInterestColumn OPTIONTYPE=new QVOptionOpenInterestColumn(COLID_OPTIONTYPE,"Option Type",75);
    public static QVOptionOpenInterestColumn STRIKE_PRICE=new QVOptionOpenInterestColumn(COLID_STRIKE_PRICE,"Strike Price",80,Number.class);
    public static QVOptionOpenInterestColumn PUBLISHED_DATE=new QVOptionOpenInterestColumn(COLID_PUBLISHED_DATE,"Published Dt",80);
    public static QVOptionOpenInterestColumn OPEN_INTEREST_DATE=new QVOptionOpenInterestColumn(COLID_OPEN_INTEREST_DATE,"Open Int Dt",80);
    public static QVOptionOpenInterestColumn EXPIRY_YYMM=new QVOptionOpenInterestColumn(COLID_EXPIRY_YYMM,"Expiry",80);

    public static QVOptionOpenInterestColumn[] columns=
        {
    			MARKETID,
    			CONTRACTSYMBOL,
    			OPEN_INTEREST,
    			OPTIONTYPE,
    			STRIKE_PRICE,
    			PUBLISHED_DATE,
    			OPEN_INTEREST_DATE,
    			EXPIRY_YYMM
        };

}

