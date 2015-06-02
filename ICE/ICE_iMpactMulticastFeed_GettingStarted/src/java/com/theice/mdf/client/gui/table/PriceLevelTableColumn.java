package com.theice.mdf.client.gui.table;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 20, 2007
 * Time: 1:00:02 PM
 */
public class PriceLevelTableColumn extends AbstractAppTableColumn
{
    /**
     * Column Ids
     */
    public static final int COLID_PRICE=0;
    public static final int COLID_QTY=1;
    public static final int COLID_ORDERCOUNT=2;
    public static final int COLID_IMPLIEDQUANTITY=3;
    public static final int COLID_IMPLIEDCOUNT=4;

    public static final int NUMBER_OF_COLUMNS=5;
    
    /**
     * Constructor
     */
    private PriceLevelTableColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private PriceLevelTableColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static PriceLevelTableColumn PRICE=new PriceLevelTableColumn(COLID_PRICE,"Price",90, Number.class);
    public static PriceLevelTableColumn QTY=new PriceLevelTableColumn(COLID_QTY,"Qty",90,Number.class);
    public static PriceLevelTableColumn ORDERCOUNT=new PriceLevelTableColumn(COLID_ORDERCOUNT,"OrderCnt",65,Number.class);
    public static PriceLevelTableColumn IMPLIEDQUANTITY=new PriceLevelTableColumn(COLID_IMPLIEDQUANTITY,"ImplQty",65,Number.class);
    public static PriceLevelTableColumn IMPLIEDCOUNT=new PriceLevelTableColumn(COLID_IMPLIEDCOUNT,"ImplCnt",65,Number.class);

    /**
     * All Columns
     */
    public static PriceLevelTableColumn[] columns=
        {
                PRICE,
                QTY,
                ORDERCOUNT,
                IMPLIEDQUANTITY,
                IMPLIEDCOUNT
        };

}
