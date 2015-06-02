package com.theice.mdf.client.gui.listeners;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.theice.mdf.client.gui.table.MarketTableModel;
import com.theice.mdf.client.gui.AbstractMDFDialog;
import com.theice.mdf.client.gui.BookDialog;
import com.theice.mdf.client.gui.DependentMarketsDialog;
import com.theice.mdf.client.gui.MDFClientFrame;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.exception.InvalidStateException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Handles the selection from the underlying market
 *
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 9:34:30 AM
 */
public class MarketTableMouseAdapter extends MouseAdapter
{
    /**
     * Handle double click on the table
     * 
     * There are two possible flows:
     * 
     * 1. If the current multicast group is set to "options", then we will show the dependent (options) markets
     * 2. Otherwise, show the book dialog associated with the underlying markets
     * 
     * @param event
     */
    public void mouseClicked(MouseEvent event)
    {
        if(event.getClickCount() == 2)
        {
            JTable target = (JTable) event.getSource();
            int row = target.getSelectedRow();
            
            if(row<0)
            {
            	return;
            }

            MarketTableModel model=(MarketTableModel) target.getModel();

            Market market=(Market) model.getMarkets().get(row);
            
            MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
            
            MarketKey marketKey = market.getMarketKey();
            
            AbstractMDFDialog dialog = AbstractMDFDialog.getBookDialogWindow(marketKey);
            
            if (dialog==null)
            {
               if(configuration.getMulticastGroupDefinition(MDFClientContext.getInstance().getInterestedMulticastGroupNames().get(0)).isOptions())
               {
                  try
                  {
                     AbstractMDFDialog.addBookToTrackingMap(marketKey,new DependentMarketsDialog(MDFClientFrame.getInstance(),market));
                  }
                  catch(InvalidStateException e)
                  {
                     e.printStackTrace();
                     System.err.println("Failed to launch the DependentMarketsDialog : "+e.toString());
                  }
               }
               else
               {
                  AbstractMDFDialog.addBookToTrackingMap(marketKey,new BookDialog(MDFClientFrame.getInstance(),market));
               }
            }
            else //dialog window already exists
            {
               dialog.setVisible(true);
            }
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
            MarketTableModel model=(MarketTableModel) target.getModel();

            int row = target.rowAtPoint( e.getPoint());
            int column = target.columnAtPoint( e.getPoint() );
            
            Market market=(Market) model.getMarkets().get(row);

            if(market!=null)
            {
                String str=market.toString();
                new MDFMessageDialog(MDFClientFrame.getInstance(),str);
            }

        }
    }
        */
}

