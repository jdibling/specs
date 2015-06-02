package com.theice.mdf.client.qv.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.theice.mdf.client.qv.gui.table.QVEndOfDayMarketMessageModel;
import com.theice.mdf.client.qv.gui.table.QVEndOfDayMarketMessageTable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketMessageDialog extends JDialog
{
	protected QVEndOfDayMarketMessageModel _responseTableModel=null;
	
	public QVEndOfDayMarketMessageDialog(JFrame frame, String title)
    {
        super(frame,true);
        
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        setTitle(title);
        setPreferredSize(new Dimension(900,500));
        
        setupPanels();
        
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }
	
	/**
	 * setup panels
	 */
	protected void setupPanels()
	{
    	_responseTableModel=new QVEndOfDayMarketMessageModel();
        add(new QVEndOfDayMarketMessageTable(_responseTableModel),BorderLayout.CENTER);
        
        return;
	}

	/**
	 * Window closing
	 */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
        	if(_responseTableModel!=null)
        	{
        		_responseTableModel.cleanup();        		
        	}
            dispose();
        }
    }
}

