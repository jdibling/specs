package com.theice.mdf.client.qv.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.theice.mdf.client.gui.listeners.MDFActivityListener;
import com.theice.mdf.client.qv.domain.QVMarketDataRecoveryType;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarketDataRecoveryRequestPanel extends JPanel implements MDFActivityListener
{
    public static final String actionSend="Send";
	
    public static final String inputDateFormatString="yyyy-MM-dd HH:mm:ss";
    
	protected JButton _command=null;
	protected JFormattedTextField _endTime=null;
	protected JComboBox _recoveryType=null;
	
	public QVMarketDataRecoveryRequestPanel(ActionListener listener)
	{
    	setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Request",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));
        setFont(MDFUtil.fontArialPlain12);

        DateFormat displayFormat = new SimpleDateFormat(inputDateFormatString);
        DateFormatter displayFormatter = new DateFormatter(displayFormat);
        DateFormat editFormat = new SimpleDateFormat(inputDateFormatString);
        DateFormatter editFormatter = new DateFormatter(editFormat);
        
        DefaultFormatterFactory factory=new DefaultFormatterFactory(displayFormatter,displayFormatter,editFormatter);
        _endTime=new JFormattedTextField(factory, new Date());
        
        add(new JLabel("End Time (Retrieval Interval=30 seconds): "));
        add(_endTime);
        
        add(new JLabel("Recovery Type : "));
        _recoveryType=new JComboBox(QVMarketDataRecoveryType.getAll().toArray());
        add(_recoveryType);
        
        _command=new JButton(actionSend);
        _command.addActionListener(listener);
        add(_command);
	}
	
	public JButton getRequestCommand()
	{
		return(this._command);
	}

	public String getEndTime()
	{
		return(_endTime.getText());
	}
	
	public char getRecoveryType()
	{
		return(((QVMarketDataRecoveryType)_recoveryType.getSelectedItem()).getCode());
	}

	/**
     * MDF Activity Listener callbacks
     */
	public void inProgress()
	{
		_command.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		return;
	}
	
	public void completed()
	{
		_command.setEnabled(true);
		setCursor(Cursor.getDefaultCursor());
		return;
	}

	public void aborted()
	{
		_command.setEnabled(true);
		setCursor(Cursor.getDefaultCursor());
		return;
	}
}

