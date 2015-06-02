package com.theice.mdf.client.config.gui.dialog;

import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 */
public class MulticastGroupConfigTableMouseAdapter extends MouseAdapter
{
    /**
     * Handle double click on the table
     * @param e
     */
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
        	JTable target=(JTable) e.getSource();
        	
        	System.err.println(target.toString());
        	
            int row=target.getSelectedRow();

        	if(row<0)
        	{
        		return;
        	}
        	
        	int modelIndex=target.convertRowIndexToModel(row);
        	
        	MulticastGroupConfigModel model=(MulticastGroupConfigModel) target.getModel();
        	
        	MulticastChannelPairInfo info=model.getMulticastChannelPairInfo(modelIndex);
        	
        	System.out.println("Selected : "+info.toString());
        }

        return;
    }
}

