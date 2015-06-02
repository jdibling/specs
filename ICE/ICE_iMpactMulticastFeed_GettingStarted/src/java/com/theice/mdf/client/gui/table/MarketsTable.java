package com.theice.mdf.client.gui.table;

import com.theice.mdf.client.gui.listeners.MarketColumnListener;
import com.theice.mdf.client.gui.listeners.MarketTableMouseAdapter;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 * Date: Aug 23, 2007
 * Time: 11:15:12 AM
 */
public class MarketsTable extends JPanel
{
    protected JTable _table=null;

    protected MarketTableModel _model=null;

    private MarketsTable()
    {
    }

    /**
     * Constructor
     *
     * @param model
     */
    public MarketsTable(MarketTableModel model)
    {
        super();

        setLayout(new BorderLayout());

        _model=model;

        _table=new MDFGenericTable(_model);

        setColumnWidths();

        JTableHeader header=_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new MarketColumnListener(_table));

        _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _table.setRowHeight(18);
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(_table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getHorizontalScrollBar().setEnabled(true);
        //_table.setFillsViewportHeight(true);

        _table.addMouseListener(new MarketTableMouseAdapter());

        _table.setBackground(Color.DARK_GRAY);
        _table.setForeground(Color.ORANGE);
        _table.setFont(new Font("Arial",Font.PLAIN,12));

        _table.setGridColor(Color.BLACK);
        _table.setSelectionBackground(Color.GRAY);

        this.add(scrollPane);
    }

    /**
     * set column widths
     */
    private void setColumnWidths()
    {
        for(int index=0;index<MarketTableColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
                    MarketTableColumn.columns[index].getWdith());
        }

        return;
    }

    /**
     * Cleanup events
     *  unsubscribe
     */
    public void cleanup()
    {
        System.out.println("MarketsTablePanel : cleanup()");
        return;
    }

}

