package com.theice.mdf.client.gui.table;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 1:22:24 PM
 */
public abstract class AbstractAppTableColumn
{
    protected int _id=0;
    protected String _name=null;
    protected int _width=0;
    protected Class _class=String.class;

    protected AbstractAppTableColumn()
    {
    }

    /**
     * Constructor
     */
    protected AbstractAppTableColumn(int id, String name, int width, Class columnClass)
    {
        _id=id;
        _name=name;
        _width=width;
        _class=columnClass;
    }

    public String getName()
    {
        return(_name);
    }

    public int getWdith()
    {
        return(_width);
    }

    public Class getColumnClass()
    {
        return(_class);
    }

}

