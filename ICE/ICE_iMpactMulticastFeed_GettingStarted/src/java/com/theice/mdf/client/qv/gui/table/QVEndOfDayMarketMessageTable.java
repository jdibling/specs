package com.theice.mdf.client.qv.gui.table;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import com.theice.mdf.client.qv.listeners.QVEndOfDayMarketMessageColumnListener;
import com.theice.mdf.client.util.MDFUtil;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketMessageTable extends JPanel
{
    protected JTable _table=null;
    protected QVEndOfDayMarketMessageModel _model=null;

    private QVEndOfDayMarketMessageTable()
    {
    }

    public QVEndOfDayMarketMessageTable(QVEndOfDayMarketMessageModel model)
    {
        super();

        setLayout(new BorderLayout());
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),
				"End of Day Market Messages - Broadcasted",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));

        _model=model;

        _table=new QVGenericTable(_model);
        
        JTableHeader header=_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new QVEndOfDayMarketMessageColumnListener(_table));
        
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
        for(int index=0;index<QVEndOfDayMarketMessageColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
            		QVEndOfDayMarketMessageColumn.columns[index].getWdith());
        }

        return;
    }

}



