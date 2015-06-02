package com.theice.mdf.client.gui.table;

import com.theice.mdf.client.domain.MDFConstants;

import javax.swing.*;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Table used for displaying price levels for bids and offers, for a specific market
 *
 * 1. Bids
 * 2. Offers
 *
 * @author Adam Athimuthu
 * Date: Aug 20, 2007
 * Time: 12:55:35 PM
 */
public class PriceLevelTable extends JPanel
{
    /**
     * The mode/side that is currently used to configure this price level table 
     */
    protected char _side= MDFConstants.BID;

    protected JTable _table=null;

    protected PriceLevelModel _model=null;

    private PriceLevelTable()
    {
    }

    /**
     * Display the Price Level
     */
    public PriceLevelTable(PriceLevelModel model)
    {
        super();

        if(model==null)
        {
            System.err.println("Null model supplied while initializing the price level table");
            return;
        }

        _side=model.getSide();

        setLayout(new BorderLayout());

        _model=model;

        /**
         * Create the table
         */
        _table=new MDFGenericTable(_model);

        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _table.setRowHeight(22);

        _table.setBackground(Color.DARK_GRAY);
        _table.setForeground(Color.ORANGE);
        _table.setFont(new Font("Arial",Font.PLAIN,16));
        _table.setGridColor(Color.BLACK);

        setColumnWidths();

        /**
         * Adjust the column display order if it is bid
         */
        if(_side==MDFConstants.BID)
        {
        	/**
        	 * We are essentially reversing the order of columns display so Bid/Offers appear as mirror images
        	 * 0 1 2 3 4 will be displayed as 4 3 2 1 0
        	 */
        	int index=0;
        	int count=_table.getColumnModel().getColumnCount();
        	int last=count-1;
        	
        	while(index<count)
        	{
                _table.getColumnModel().moveColumn(0,last);
                index++;
        		last--;
        	}
        }
        else
        {
            ;
        }

        _table.getTableHeader().setReorderingAllowed(false);
        _table.getTableHeader().setResizingAllowed(false);

        /**
         * Add the table to the scrollpane
         */
        JScrollPane scrollPane = new JScrollPane(_table);

        this.add(scrollPane);
    }

    /**
     * get the table
     * @return
     */
    public JTable getTable()
    {
        return(_table);
    }

    /**
     * Get price level table model
     * @return
     */
    public PriceLevelModel getPriceLevelModel()
    {
        return(_model);
    }

    /**
     * set column widths
     */
    private void setColumnWidths()
    {
        for(int index=0;index<PriceLevelTableColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
                    PriceLevelTableColumn.columns[index].getWdith());
        }

        return;
    }

}



