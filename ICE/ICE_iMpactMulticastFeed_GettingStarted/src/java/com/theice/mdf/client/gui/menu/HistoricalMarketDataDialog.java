package com.theice.mdf.client.gui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataDialog extends JDialog implements ActionListener
{
    private static Logger logger=Logger.getLogger(HistoricalMarketDataDialog.class.getName());

    protected HistoricalMarketDataRequestPanel _requestPanel=null;
	protected HistoricalMarketDataModel _responseTableModel=null;
	
	public HistoricalMarketDataDialog(JFrame frame, String title)
    {
        super(frame,true);
        
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        setTitle(title);
        setPreferredSize(new Dimension(900,600));
        
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
		_requestPanel=new HistoricalMarketDataRequestPanel(this);
        add(_requestPanel,BorderLayout.NORTH);

    	_responseTableModel=new HistoricalMarketDataModel(_requestPanel);
        add(new HistoricalMarketDataTable(_responseTableModel),BorderLayout.CENTER);
        
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
    	if(e.getActionCommand()==HistoricalMarketDataRequestPanel.actionSend)
    	{
    		short session=_requestPanel.getSessionNumber();
    		String group=_requestPanel.getGroupAddress();
    		short port=_requestPanel.getPort();
    		int startSequence=_requestPanel.getStartSequence();
    		int endSequence=_requestPanel.getEndSequence();
    		
    		StringBuffer buf=new StringBuffer();
    		buf.append("Session : ").append(session);
    		buf.append(" Group : ").append(group);
    		buf.append(" Port: ").append(port);
    		buf.append(" Start Seq: ").append(startSequence);
    		buf.append(" End Seq: ").append(endSequence);
    		System.out.println("*** Historical Market Data Request : "+buf.toString());
    		
            _responseTableModel.triggerOnDemandRequest(session,group,port,startSequence,endSequence);
    	}
    }
    
}

