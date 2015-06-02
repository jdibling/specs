package com.theice.mdf.client.qv.listeners;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.theice.mdf.client.gui.MDFGUIClient;
import com.theice.mdf.client.qv.gui.table.QVMarkerPriceIndexColumn;
import com.theice.mdf.client.qv.gui.table.QVMarkerPriceIndexModel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarkerPriceIndexColumnListener extends MouseAdapter
{
    protected JTable _table;
    protected int _sortColumn=(-1);
    protected boolean _sortAscending=true;

    public QVMarkerPriceIndexColumnListener(JTable table)
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
        
        if(modelIndex!=QVMarkerPriceIndexColumn.COLID_MARKETID)
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

        QVMarkerPriceIndexModel model=(QVMarkerPriceIndexModel) _table.getModel();
        model.sort(_sortColumn, _sortAscending);

    }
    
}

