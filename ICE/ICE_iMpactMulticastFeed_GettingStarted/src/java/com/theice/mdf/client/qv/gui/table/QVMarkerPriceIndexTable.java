package com.theice.mdf.client.qv.gui.table;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import com.theice.mdf.client.qv.listeners.QVMarkerPriceIndexColumnListener;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarkerPriceIndexTable extends JPanel
{
    protected JTable _table=null;
    protected QVMarkerPriceIndexModel _model=null;

    private QVMarkerPriceIndexTable()
    {
    }

    public QVMarkerPriceIndexTable(QVMarkerPriceIndexModel model)
    {
        super();

        setLayout(new BorderLayout());
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Response",
        		TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));

        _model=model;
        _table=new QVGenericTable(_model);
        
        JTableHeader header=_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new QVMarkerPriceIndexColumnListener(_table));
        
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
        for(int index=0;index<QVMarkerPriceIndexColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
            		QVMarkerPriceIndexColumn.columns[index].getWdith());
        }

        return;
    }

}



