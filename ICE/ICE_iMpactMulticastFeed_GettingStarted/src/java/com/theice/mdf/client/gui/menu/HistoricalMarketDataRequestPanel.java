package com.theice.mdf.client.gui.menu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.examples.SimpleClientConfigurator;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.gui.listeners.MDFActivityListener;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataRequestPanel extends JPanel implements MDFActivityListener
{
    public static final String actionSend="Send";
	
	protected JButton _command=null;
	
	protected JTextField _sessionNumber=null;
	protected JTextField _group=null;
	protected JTextField _port=null;
	protected JTextField _startSequence=null;
	protected JTextField _endSequence=null;
	
	public HistoricalMarketDataRequestPanel(ActionListener listener)
	{
		String group="";
		String multicastGroupName=MDFClientContext.getInstance().getInterestedMulticastGroupNames().get(0);
		int port=0;
		int session=AppManager.getInstance(multicastGroupName).getSession();
		
    	setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Request",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));
        setFont(MDFUtil.fontArialPlain12);
        
        try
        {
        	MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
        	
        	group=configuration.getMulticastChannelPairInfo(multicastGroupName).getLiveEndPoint().getIpAddress();
        	port=configuration.getMulticastChannelPairInfo(multicastGroupName).getLiveEndPoint().getPort();
        }
        catch(Throwable t)
        {
        	group="";
        }

        _group=new JTextField(group,15);
        _sessionNumber=new JTextField(Integer.toString(session),8);
        _port=new JTextField(Integer.toString(port),8);
        _startSequence=new JTextField("",8);
        _endSequence=new JTextField("",8);
        
        add(new JLabel("Session : "));
        add(_sessionNumber);
        
        add(new JLabel("Group : "));
        add(_group);
        
        add(new JLabel("Port : "));
        add(_port);
        
        add(new JLabel("Start Seq: "));
        add(_startSequence);
        
        add(new JLabel("End Seq: "));
        add(_endSequence);
        
        _command=new JButton(actionSend);
        _command.addActionListener(listener);
        add(_command);
	}
	
	public JButton getRequestCommand()
	{
		return(this._command);
	}

	public short getSessionNumber()
	{
		short session=0;
		
		try
		{
			session=Short.parseShort(_sessionNumber.getText());
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		return(session);
	}
	
	public String getGroupAddress()
	{
		return(this._group.getText());
	}
	
	public short getPort()
	{
		short port=0;
		
		try
		{
			port=Short.parseShort(_port.getText());
		}
		catch(NumberFormatException e)
		{
		}
		
		return(port);
	}
	
	public int getStartSequence()
	{
		int seq=0;
		
		try
		{
			seq=Integer.parseInt(this._startSequence.getText());
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		return(seq);
	}
	
	public int getEndSequence()
	{
		int seq=0;
		
		try
		{
			seq=Integer.parseInt(this._endSequence.getText());
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		return(seq);
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

