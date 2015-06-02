package com.theice.mdf.client.gui.listeners;

import com.theice.mdf.client.gui.table.OptionMarketTableModel;
import com.theice.mdf.client.gui.BookDialog;
import com.theice.mdf.client.gui.MDFClientFrame;
import com.theice.mdf.client.domain.OptionMarket;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 */
public class OptionMarketTableMouseAdapter extends MouseAdapter
{
    /**
     * Handle double click on the table
     * @param e
     */
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount() == 2)
        {
            JTable target = (JTable) e.getSource();
            int viewRowIndex = target.getSelectedRow();
            if (viewRowIndex<0)
            {
               return;
            }
            
            int row = target.convertRowIndexToModel(viewRowIndex);
            
            OptionMarketTableModel model=(OptionMarketTableModel) target.getModel();
            OptionMarket optionMarket=(OptionMarket) model.getOptionMarkets()[row];
            
            BookDialog dialog=new BookDialog(MDFClientFrame.getInstance(),optionMarket);
        }

        return;
    }

    /**
     * Handle right mouse click for inspection purposes
     * TODO currently disabled
     * @param e
    public void mouseReleased(MouseEvent e)
    {
        JTable target = (JTable) e.getSource();

        if(e.getButton()==3)
        {
            OptionMarketTableModel model=(OptionMarketTableModel) target.getModel();

            int row = target.rowAtPoint( e.getPoint());
            int column = target.columnAtPoint( e.getPoint() );
            
            OptionMarket market=(OptionMarket) (model.getOptionMarkets()[row]);

            if(market!=null)
            {
                String str=market.toString();
                new MDFMessageDialog(MDFClientFrame.getInstance(),str);
            }

        }
    }
    */
}

