package com.theice.mdf.client.gui.menu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataColumnListener extends MouseAdapter
{
    protected JTable _table;
    protected int _sortColumn=(-1);
    protected boolean _sortAscending=true;

    public HistoricalMarketDataColumnListener(JTable table)
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
        
        if(modelIndex!=HistoricalMarketDataColumn.COLID_MARKETID)
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

        HistoricalMarketDataModel model=(HistoricalMarketDataModel) _table.getModel();
        model.sort(_sortColumn, _sortAscending);

    }
    
}

