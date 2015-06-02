package com.theice.mdf.client.gui.listeners;

import com.theice.mdf.client.gui.MDFClientFrame;
import com.theice.mdf.client.gui.MDFMenuBar;
import com.theice.mdf.client.gui.menu.HistoricalMarketDataDialog;
import com.theice.mdf.client.domain.MarketsHolder;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Market menu listener. Use for troubleshooting and inspecting internal data structures
 *
 * @author Adam Athimuthu
 * Date: Aug 7, 2007
 * Time: 1:03:06 PM
 */
public class MarketMenuListener implements ActionListener
{
    /**
     * action performed event handler
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem) (e.getSource());
        String text=source.getText();

        if(text.compareTo(MDFMenuBar.MENUITEM_MARKETS)==0)
        {
            /**
             * Dump the market
             */
            System.out.println(MarketsHolder.getInstance().toString());
        }
        else if(text.compareTo(MDFMenuBar.MENUITEM_LOG)==0)
        {
            /**
             * Dump the message log
            DefaultListModel messageListModel=context.getMessageModel();

            synchronized(messageListModel)
            {
                messageListModel.clear();

                List messageList=MDRawMessageBuffer.getInstance().getLogMessageList();

                synchronized(messageList)
                {
                    for(Iterator it=messageList.iterator();it.hasNext();)
                    {
                        MDMessage message=(MDMessage) it.next();
                        messageListModel.addElement(message.toString());
                    }
                }
            }
             */
        }
        else if(text.compareTo(MDFMenuBar.MENUITEM_HISTORICAL_REPLAY)==0)
        {
        	new HistoricalMarketDataDialog(MDFClientFrame.getInstance(),"Historical Data Replay");
        }
        else if(text.compareTo(MDFMenuBar.MENUITEM_ABOUT)==0)
        {
            String message = "Market Data Feed Multicast Client - Version 1.0";
            JOptionPane pane = new JOptionPane(message);
            JDialog dialog = pane.createDialog(MDFClientFrame.getInstance(), "MDF Multicast Client");
            dialog.setVisible(true);
        }

    }
}

