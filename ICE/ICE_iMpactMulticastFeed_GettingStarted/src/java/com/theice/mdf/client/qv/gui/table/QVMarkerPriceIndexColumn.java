package com.theice.mdf.client.qv.gui.table;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarkerPriceIndexColumn extends AbstractAppTableColumn
{
    public static final int COLID_MARKETID=0;
    public static final int COLID_PRICE=1;
    public static final int COLID_SHORTNAME=2;
    public static final int COLID_PUBLISHED_DATETIME=3;
    public static final int COLID_VALUTATION_DATE=4;

    private QVMarkerPriceIndexColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private QVMarkerPriceIndexColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static QVMarkerPriceIndexColumn MARKETID=new QVMarkerPriceIndexColumn(COLID_MARKETID,"MarketId",75);
    public static QVMarkerPriceIndexColumn PRICE=new QVMarkerPriceIndexColumn(COLID_PRICE,"Price",100, Number.class);
    public static QVMarkerPriceIndexColumn SHORTNAME=new QVMarkerPriceIndexColumn(COLID_SHORTNAME,"Short Name",100);
    public static QVMarkerPriceIndexColumn PUBLISHED=new QVMarkerPriceIndexColumn(COLID_PUBLISHED_DATETIME,"Published",150);
    public static QVMarkerPriceIndexColumn VALUATION_DATE=new QVMarkerPriceIndexColumn(COLID_VALUTATION_DATE,"Valuation Date",150);

    public static QVMarkerPriceIndexColumn[] columns=
        {
    			MARKETID,
                PRICE,
                SHORTNAME,
                PUBLISHED,
                VALUATION_DATE
        };

}

