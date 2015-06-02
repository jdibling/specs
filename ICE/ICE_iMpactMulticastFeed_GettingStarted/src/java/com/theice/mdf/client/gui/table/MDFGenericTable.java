package com.theice.mdf.client.gui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;

import com.theice.mdf.client.domain.OptionMarket;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Generic MDF table
 *
 * @author Adam Athimuthu
 * Date: Aug 23, 2007
 * Time: 12:52:21 PM
 */
public class MDFGenericTable extends JTable
{
    TableModel _tableModel=null; 
    public MDFGenericTable(TableModel model)
    {
        super(model);
        _tableModel = model;
    }

    /**
     * override prepare renderer
     *
     * @param renderer
     * @param rowIndex
     * @param vColIndex
     * @return
     */
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex)
    {
        Component component = super.prepareRenderer(renderer, rowIndex, vColIndex);
        
        if (_tableModel instanceof MarketTableModel)
        {
           MarketTableModel tm = (MarketTableModel)_tableModel;
           if (tm.isReservedFlagOn(rowIndex))
           {
              component.setForeground(Color.CYAN);
           }
           else
           {
              component.setForeground(Color.ORANGE);
           }
        }
        
        if (_tableModel instanceof BookTableModel)
        {
           BookTableModel bm = (BookTableModel)_tableModel;
           if (bm.isReservedFlagOn(rowIndex))
           {
              component.setForeground(Color.CYAN);
           }
           else
           {
              component.setForeground(Color.ORANGE);
           }
        }


        if(isCellSelected(rowIndex, vColIndex))
        {
            return(component);
        }

        if(rowIndex%2==0)
        {
            component.setBackground(Color.BLACK);
        }
        else
        {
            component.setBackground(getBackground());
        }
        
        if (_tableModel instanceof OptionMarketTableModel)
        {
           OptionMarketTableModel tableModel = (OptionMarketTableModel)_tableModel;
           OptionMarket uds = tableModel.getMarketAt(rowIndex);
           String tooltipDesc = "";
           if (uds!=null && uds.isUDSMarket())
           {
              tooltipDesc = uds.getShortUDSDesc();
           }
           TableColumn column = this.getColumnModel().getColumn(0);
           DefaultTableCellRenderer defaultRenderer =  new DefaultTableCellRenderer();
           defaultRenderer.setToolTipText(tooltipDesc);
           column.setCellRenderer(defaultRenderer);
        }
        
        return(component);
    }
}

