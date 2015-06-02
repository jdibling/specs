/**
 * Created by IntelliJ IDEA.
 * User: aathimut
 * Date: Aug 1, 2007
 * Time: 8:36:50 AM
 * To change this template use File | Settings | File Templates.
 */
package com.theice.mdf.client.process;

import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.gui.table.MarketTableModel;
import com.theice.mdf.client.gui.panel.MarketTypesPanel;
import com.theice.mdf.client.gui.MarketTabbedDisplayPane;
import com.theice.mdf.client.gui.MDFStatusBar;
import com.theice.mdf.client.process.context.AbstractBaseMDFAppContext;
import com.theice.mdf.client.process.context.AppMode;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Keeps the client context information such as
 * Connection Configuration
 * Connection Status
 * Model
 *
 * @author Adam Athimuthu
 * @version 1.0
 */
public class MDFClientContext extends AbstractBaseMDFAppContext
{
    private static final Logger logger=Logger.getLogger(MDFClientContext.class.getName());
    
    private static MDFClientContext _instance=new MDFClientContext();

    /**
     * The model that holds the market messages received from the server
     */
    private final DefaultListModel _messageModel=new DefaultListModel();

    /**
     * Status and Menu
     */
    private MDFStatusBar _statusBar=null;

    private JMenuBar _menuBar=null;
    
    /**
     * The model that holds the market types
     */
    private MarketTypesPanel _marketTypesList=null;

    /**
     * HashMap of Markets Tables
     * Key: MarketType
     */
    private HashMap<Short,MarketTableModel> _marketTableModels=new HashMap<Short,MarketTableModel>();
    private MarketTabbedDisplayPane _marketTablePane=new MarketTabbedDisplayPane();

    /**
     * Client Context
     */
    private MDFClientContext()
    {
    	super();
    	
    	this.mode=AppMode.GUI;

        _statusBar=MDFStatusBar.getInstance();
        _marketTypesList=MarketTypesPanel.getInstance();
        _messageModel.clear();
        _menuBar=new JMenuBar();
    }

    /**
     *
     * @return
     */
    public static MDFClientContext getInstance()
    {
        return _instance;
    }

    /**
     * Get the MessageModel
     */
    public DefaultListModel getMessageModel()
    {
        return(this._messageModel);
    }
    
    /**
     * call to override the empty menubar
     * @param menuBar
     */
    public void setAppMenuBar(JMenuBar menuBar)
    {
        _menuBar=menuBar;
    }

    /**
     * update the connection status
     * @param connected
     */
    public void setConnected(boolean connected)
    {
    	super.setConnected(connected);

        if(_statusBar!=null)
        {
            _statusBar.setStatus(isConnected);
        }
    }

    /**
     * get status bar
     * @return
     */
    public MDFStatusBar getStatusBar()
    {
        return _statusBar;
    }

    /**
     * get Menu Bar
     * @return
     */
    public JMenuBar getAppMenuBar()
    {
        return _menuBar;
    }

    /**
     * Get the market types list
     */
    public MarketTypesPanel getMarketTypesPanel()
    {
        return(this._marketTypesList);
    }

    /**
     * Get the market table models map
     * @return
     */
    public Map<Short, MarketTableModel> getMarketTableModels()
    {
        return(_marketTableModels);
    }

    /**
     * cache market
     * add the market to the market table model pertaining to the corresponding market type
     * 
     * For the underlying markets, the models are pre-created for each market type, so it is an error if
     * we don't find a model for a market type
     * 
     * Whereas, for options markets, they are created when the OptionsProductDefinitions are sent across.
     * 
     * @param Market
     */
    public void cacheMarket(MarketInterface market)
    {
    	if(!market.isOptionMarket())
    	{
            MarketTableModel tableModel=(MarketTableModel) _marketTableModels.get(new Short(market.getMarketType()));

            if(tableModel!=null)
            {
                tableModel.addMarket((Market) market);

//        		MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
//        		
//    			if(configuration.getMulticastGroupDefinition().isOptions())
//    			{
//    			}
            }
            else
            {
                logger.error("Table model NOT FOUND for : "+market.getMarketType());
            }
    	}
    	else
    	{
    		/**
    		 * Got cacheMarket call for an options market...Nothing to do.
    		 */
    	}
        
        return;
    }
    
    /**
     * get market table pane (tabbed)
     * @return
     */
    public MarketTabbedDisplayPane getMarketDisplayPane()
    {
        return(this._marketTablePane);
    }
    
    /**
     * Logs the essential messages into the internal log's model
     * @param message
     */
    public void logEssential(String message)
    {
        if(_messageModel!=null)
        {
            _messageModel.addElement(message);
        }
        return;
    }
    
    /**
     * Clear all messages in the context cache
     */
    public void clearLogMessages()
    {
    	System.out.println("TODO : clearLogMessages");
    	System.out.println("TODO : clearLogMessages");
    	System.out.println("TODO : clearLogMessages");
    	System.out.println("TODO : clearLogMessages");
    	System.out.println("TODO : clearLogMessages");
    	System.out.println("TODO : clearLogMessages");
    	System.out.println("TODO : clearLogMessages");
    	return;
    }
    
    /**
     * alert with an appropriate mechanism
     * a GUI context might do this by a dialog box
     * a command context might display an error message
     * @param message
     */
    public void alert(final String message)
    {
		Runnable dialog=new Runnable() 
		{
			public void run() 
			{
				JOptionPane.showMessageDialog(null, message, "Alert", JOptionPane.ERROR_MESSAGE);
			}
		};

		try
		{
			SwingUtilities.invokeAndWait(dialog);
		}
		catch(Throwable t) {}
        
    	return;
    }
    
}

