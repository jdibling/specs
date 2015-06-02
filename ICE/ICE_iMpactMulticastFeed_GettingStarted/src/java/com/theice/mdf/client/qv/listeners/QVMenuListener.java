package com.theice.mdf.client.qv.listeners;

import com.theice.mdf.client.gui.MDFClientFrame;
import com.theice.mdf.client.qv.gui.QVEndOfDayMarketMessageDialog;
import com.theice.mdf.client.qv.gui.QVMDFMenuBar;
import com.theice.mdf.client.qv.gui.QVMarkerIndexPriceDialog;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * QV menu listener
 *
 * @author Adam Athimuthu
 */
public class QVMenuListener implements ActionListener
{
    private Logger logger=Logger.getLogger(QVMenuListener.class.getName());
    /**
     * action performed event handler
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem) (e.getSource());
        String text=source.getText();
        
        try
        {
            if(text.compareTo(QVMDFMenuBar.MENUITEM_MARKER_INDEX_PRICE)==0)
            {
            	new QVMarkerIndexPriceDialog(MDFClientFrame.getInstance(),"QV Marker Index Price");
            }
            else if(text.compareTo(QVMDFMenuBar.MENUITEM_ENDOFDAY_MARKET_MESSAGE)==0)
            {
            	new QVEndOfDayMarketMessageDialog(MDFClientFrame.getInstance(),"End of Day Market Messages - Broadcasted");
            }
            
        }
        catch(Exception ex)
        {
        	logger.warn("Error bringing up the QV Dialog : "+ex.toString());
        	ex.printStackTrace();
        }

    }
}

