package com.theice.mdf.client.qv.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.gui.listeners.MDFActivityListener;
import com.theice.mdf.client.qv.gui.table.QVMarkerPriceIndexModel;
import com.theice.mdf.client.qv.gui.table.QVMarkerPriceIndexTable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Marker Index Price Dialog - support for displaying broadcast messages as well as
 * on-demand messages
 * 
 * @author Adam Athimuthu
 */
public class QVMarkerIndexPriceDialog extends JDialog implements ActionListener
{
	protected QVPriceDialogRequestPanel _requestPanel=null;
	
	protected QVMarkerPriceIndexModel _responseTableModel=null;
	
	public QVMarkerIndexPriceDialog(JFrame frame, String title)
    {
        super(frame,true);
        
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        setTitle(title);
        setPreferredSize(new Dimension(800,500));
        
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
		_requestPanel=new QVPriceDialogRequestPanel(this);
        add(_requestPanel,BorderLayout.NORTH);

        MarketType marketType=_requestPanel.getSelectedMarketType();
        short marketTypeCode=Short.parseShort(marketType.getMarketTypeCode());
    	
    	_responseTableModel=new QVMarkerPriceIndexModel(marketTypeCode,_requestPanel);
        add(new QVMarkerPriceIndexTable(_responseTableModel),BorderLayout.CENTER);
        
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
    
    /**
     * Handle dialog events
     * @param Action Event
     */
    public void actionPerformed(ActionEvent e) 
    {
    	if(e.getSource()==_requestPanel.getMarketTypesComboBox())
    	{
        	if(!_requestPanel.getRequestCommand().isEnabled())
        	{
        		MarketType marketType=(MarketType) _requestPanel.getSelectedMarketType();
        		_responseTableModel.triggerBroadcast(Short.parseShort(marketType.getMarketTypeCode()));
        	}
        	
        	return;
    	}

    	if(e.getActionCommand()==QVPriceDialogRequestPanel.actionViewBroadcast)
    	{
    		_requestPanel.getRequestCommand().setEnabled(false);
    		
    		MarketType marketType=(MarketType) _requestPanel.getSelectedMarketType();
    		
    		_responseTableModel.triggerBroadcast(Short.parseShort(marketType.getMarketTypeCode()));
    	}
    	else if(e.getActionCommand()==QVPriceDialogRequestPanel.actionViewOnDemand)
    	{
    		_requestPanel.getRequestCommand().setEnabled(true);
    	}
    	else if(e.getActionCommand()==QVPriceDialogRequestPanel.actionSend)
    	{
//    		MarketType marketType=(MarketType) _requestPanel.getSelectedMarketType();
//    		
//            _responseTableModel.triggerOnDemandRequest(Short.parseShort(marketType.getMarketTypeCode()));
    	}
    }
    
}

