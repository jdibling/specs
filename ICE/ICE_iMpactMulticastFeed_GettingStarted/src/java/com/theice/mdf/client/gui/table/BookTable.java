package com.theice.mdf.client.gui.table;

import javax.swing.*;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.Enumeration;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Book Table - used for displaying bids and offers for a specific market
 * 
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 11:29:43 AM
 *
 */
public class BookTable extends JPanel
{
    protected JTable _table=null;

    protected BookTableModel _model=null;
    
    private BookTable()
    {
    }

    /**
     * Display the book
     */
    public BookTable(BookTableModel model,boolean optionMarketMode)
    {
        super();

        setLayout(new BorderLayout());

        _model=model;
        
        _table=new MDFGenericTable(_model);

        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _table.setRowHeight(22);

        _table.setBackground(Color.DARK_GRAY);
        _table.setForeground(Color.ORANGE);
        _table.setFont(new Font("Arial",Font.PLAIN,12));
        _table.setGridColor(Color.BLACK);
        
        setColumnWidths();
        
        if(optionMarketMode)
        {
        	applyFiltersForOptionMarkets();
        }
        
        JScrollPane scrollPane = new JScrollPane(_table);
        scrollPane.getHorizontalScrollBar().setEnabled(true);

        this.add(scrollPane);
    }

    /**
     * Get book table model
     * @return
     */
    public BookTableModel getBookTableModel()
    {
        return(_model);
    }

    /**
     * set column widths
     */
    private void setColumnWidths()
    {
        for(int index=0;index<BookTableColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
                    BookTableColumn.columns[index].getWdith());
        }

        return;
    }
    
    /**
     * Remove the implied column that is not applicable for option markets
     */
    private void applyFiltersForOptionMarkets()
    {
    	TableColumn column=(TableColumn) _table.getColumn(BookTableColumn.COLNAME_ISIMPLIED);
    	
    	if(column!=null)
    	{
    		_table.removeColumn(column);
    	}
    }

}

