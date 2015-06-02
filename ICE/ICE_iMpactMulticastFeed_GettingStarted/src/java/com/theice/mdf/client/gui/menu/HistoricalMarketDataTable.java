package com.theice.mdf.client.gui.menu;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import com.theice.mdf.client.util.MDFUtil;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataTable extends JPanel
{
    protected JTable _table=null;
    protected HistoricalMarketDataModel _model=null;

    private HistoricalMarketDataTable()
    {
    }

    public HistoricalMarketDataTable(HistoricalMarketDataModel model)
    {
        super();

        setLayout(new BorderLayout());
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Response",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));

        _model=model;
        _table=new OnDemandResponseTable(_model);
        
        JTableHeader header=_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new HistoricalMarketDataColumnListener(_table));
        
        setColumnWidths();
        add(new JScrollPane(_table));
    }

    public JTable getTable()
    {
        return(_table);
    }
    
    public void init()
    {
    }

    public void cleanup()
    {
    	_model.cleanup();
    }
    
    private void setColumnWidths()
    {
        for(int index=0;index<HistoricalMarketDataColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
            		HistoricalMarketDataColumn.columns[index].getWdith());
        }

        return;
    }

}



