package com.theice.mdf.client.qv.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.examples.SimpleClientConfigurator;
import com.theice.mdf.client.gui.listeners.MDFActivityListener;
import com.theice.mdf.client.gui.panel.MarketTypesPanel;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVPriceDialogRequestPanel extends JPanel implements MDFActivityListener
{
    public static final String actionSend="Send";
    public static final String actionViewBroadcast="View Broadcast";
    public static final String actionViewOnDemand="Request";
	
	protected JButton _command=null;
	protected JComboBox _marketTypesComboBox=null;
	protected JRadioButton _radioBroadcast=null;
	protected JRadioButton _radioOnDemand=null;
	
	public QVPriceDialogRequestPanel(ActionListener listener)
	{
    	MarketType marketType=null;

    	setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Request",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));
        setFont(MDFUtil.fontArialPlain12);
         
        Vector<MarketType> marketTypes=SimpleClientConfigurator.getInterestedMarketTypes();
        _marketTypesComboBox=new JComboBox(marketTypes);
        
    	JList list=MarketTypesPanel.getInstance().getList();
    	
    	if(list!=null)
    	{
			marketType=(MarketType) list.getSelectedValue();
    	}

        if(marketType==null)
        {
        	marketType=(MarketType) _marketTypesComboBox.getSelectedItem();
        }
        else
        {
        	_marketTypesComboBox.setSelectedItem(marketType);
        }
        
        _marketTypesComboBox.addActionListener(listener);
        
        ButtonGroup buttonGroup=new ButtonGroup();
        _radioBroadcast=new JRadioButton(actionViewBroadcast);
        _radioBroadcast.addActionListener(listener);
        _radioBroadcast.setSelected(true);
        buttonGroup.add(_radioBroadcast);
        
        _radioOnDemand=new JRadioButton(actionViewOnDemand);
        _radioOnDemand.addActionListener(listener);
        buttonGroup.add(_radioOnDemand);
        
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(_radioBroadcast);
        radioPanel.add(_radioOnDemand);
        add(radioPanel);

        add(new JLabel("Market Type : "));
        add(_marketTypesComboBox);

        _command=new JButton(actionSend);
		_command.setEnabled(false);
        _command.addActionListener(listener);
        add(_command);
	}
	
	public JButton getRequestCommand()
	{
		return(this._command);
	}

	public JComboBox getMarketTypesComboBox()
	{
		return(this._marketTypesComboBox);
	}
	
	public MarketType getSelectedMarketType()
	{
		return((MarketType) this._marketTypesComboBox.getSelectedItem());
	}
	
    /**
     * MDF Activity Listener callbacks
     */
	public void inProgress()
	{
		_command.setEnabled(false);
		_radioBroadcast.setEnabled(false);
		_marketTypesComboBox.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		return;
	}
	
	public void completed()
	{
		_command.setEnabled(true);
		_radioBroadcast.setEnabled(true);
		_marketTypesComboBox.setEnabled(true);
		setCursor(Cursor.getDefaultCursor());
		return;
	}

	public void aborted()
	{
		_command.setEnabled(true);
		_radioBroadcast.setEnabled(true);
		_marketTypesComboBox.setEnabled(true);
		setCursor(Cursor.getDefaultCursor());
		return;
	}
}

