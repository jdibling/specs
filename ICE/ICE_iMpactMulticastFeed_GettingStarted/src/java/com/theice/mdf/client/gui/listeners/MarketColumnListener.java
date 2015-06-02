package com.theice.mdf.client.gui.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.theice.mdf.client.gui.table.MarketTableColumn;
import com.theice.mdf.client.gui.table.MarketTableModel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MarketColumnListener extends MouseAdapter
{
    protected JTable _table;
    protected int _sortColumn=(-1);
    protected boolean _sortAscending=true;

    public MarketColumnListener(JTable table)
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
        
        if(modelIndex!=MarketTableColumn.COLID_MARKETID && modelIndex!=MarketTableColumn.COLID_MARKETDESC)
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

        MarketTableModel model=(MarketTableModel) _table.getModel();
        model.sort(_sortColumn, _sortAscending);

    }
    
}

