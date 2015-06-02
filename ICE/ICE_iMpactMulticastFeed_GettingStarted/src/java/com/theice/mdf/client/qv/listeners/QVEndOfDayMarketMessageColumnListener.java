package com.theice.mdf.client.qv.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.theice.mdf.client.qv.gui.table.QVEndOfDayMarketMessageColumn;
import com.theice.mdf.client.qv.gui.table.QVEndOfDayMarketMessageModel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketMessageColumnListener extends MouseAdapter
{
    protected JTable _table;
    protected int _sortColumn=(-1);
    protected boolean _sortAscending=true;

    public QVEndOfDayMarketMessageColumnListener(JTable table)
    {
        _table = table;
    }

    public void mouseClicked(MouseEvent e)
    {
        TableColumnModel colModel = _table.getColumnModel();
        int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
        int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();

        if (modelIndex < 0)
        {
            return;
        }
        
        if(modelIndex!=QVEndOfDayMarketMessageColumn.COLID_MARKETID)
        {
            return;
        }
        
        if(_sortColumn == modelIndex)
        {
        	_sortAscending = !_sortAscending;
        }
        else
        {
        	_sortColumn = modelIndex;
        }

        QVEndOfDayMarketMessageModel model=(QVEndOfDayMarketMessageModel) _table.getModel();
        model.sort(_sortColumn, _sortAscending);

    }
    
}

