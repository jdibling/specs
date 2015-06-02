package com.theice.mdf.client.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import com.theice.mdf.client.gui.listeners.OptionMarketTableMouseAdapter;

public class OptionMarketsTable extends JPanel
{
    protected JTable _table=null;
    protected OptionMarketTableModel _model=null;

    private OptionMarketsTable()
    {
    }
    
    public OptionMarketsTable(OptionMarketTableModel model)
    {
        super();

        setLayout(new BorderLayout());

        _model=model;

        _table=new MDFGenericTable(_model);
        
        _table.setAutoCreateRowSorter(true);

        setColumnWidths();

        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _table.setRowHeight(18);
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(_table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getHorizontalScrollBar().setEnabled(true);

        _table.addMouseListener(new OptionMarketTableMouseAdapter());

        _table.setBackground(Color.DARK_GRAY);
        _table.setForeground(Color.ORANGE);
        _table.setFont(new Font("Arial",Font.PLAIN,12));

        _table.setGridColor(Color.BLACK);
        _table.setSelectionBackground(Color.GRAY);
        
        _table.setToolTipText("Double-click to view the book");

        this.add(scrollPane);
    }

    /**
     * refresh the associated models
     */
    public void refresh()
    {
        if(this._model!=null)
        {
        	_model.refresh();
        }
    }

    public void cleanup()
    {
        if(this._model!=null)
        {
        	_model.cleanup();
        }
    }

    /**
     * set column widths
     */
    private void setColumnWidths()
    {
        for(int index=0;index<OptionMarketTableColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
                    OptionMarketTableColumn.columns[index].getWdith());
        }

        return;
    }
}

