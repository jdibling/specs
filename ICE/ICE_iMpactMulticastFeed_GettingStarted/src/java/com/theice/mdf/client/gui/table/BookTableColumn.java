package com.theice.mdf.client.gui.table;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 11:39:10 AM
 */
public class BookTableColumn extends AbstractAppTableColumn
{
    /**
     * Column Ids
     */
    public static final int COLID_ORDERID=0;
    public static final int COLID_QTY=1;
    public static final int COLID_PRICE=2;
    public static final int COLID_ISIMPLIED=3;
    public static final int COLID_TIMESTAMP=4;
    public static final int COLID_RESERVEDFLD1=6;
    public static final int COLID_PRIORITY=5;

    public static final String COLNAME_ORDERID="OrderId";
    public static final String COLNAME_QTY="Qty";
    public static final String COLNAME_PRICE="Price";
    public static final String COLNAME_ISIMPLIED="Implied?";
    public static final String COLNAME_TIMESTAMP="TimeStamp";
    public static final String COLNAME_RESERVEDFLD1="ReservedFld1";
    public static final String COLNAME_Priority="Priority";

    /**
     * Constructor
     */
    private BookTableColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private BookTableColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static BookTableColumn ORDERID=new BookTableColumn(COLID_ORDERID,"OrderId",100);
    public static BookTableColumn QTY=new BookTableColumn(COLID_QTY,"Qty",40,Number.class);
    public static BookTableColumn PRICE=new BookTableColumn(COLID_PRICE,"Price",50, Number.class);
    public static BookTableColumn ISIMPLIED=new BookTableColumn(COLID_ISIMPLIED,"Implied?",55);
    public static BookTableColumn TIMESTAMP=new BookTableColumn(COLID_TIMESTAMP,"TimeStamp",110);
    public static BookTableColumn PRIORITY =new BookTableColumn(COLID_PRIORITY,"Priority",55);    
    public static BookTableColumn RESERVEDFLD1=new BookTableColumn(COLID_RESERVEDFLD1,"ReservedFld1",55);

    /**
     * All Columns
     */
    public static BookTableColumn[] columns=
        {
                ORDERID,
                QTY,
                PRICE,
                ISIMPLIED,
                TIMESTAMP,
                PRIORITY,
                RESERVEDFLD1
        };

}

