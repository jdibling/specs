package com.theice.mdf.client.gui;

import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.AppMonitor;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.process.context.AppMode;
import com.theice.mdf.client.process.context.MDFAppContext;
import com.theice.mdf.client.domain.event.ApplicationEvent;
import com.theice.mdf.client.domain.event.ApplicationEventSubscriber;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.gui.panel.LogMessagePanel;

import javax.swing.*;

import java.awt.*;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * The MDF client's main frame and an event subscriber of the AppMonitor
 * 
 * @author Adam Athimuthu
 * Date: Jul 31, 2007
 * Time: 4:31:11 PM
 */
public class MDFClientFrame extends JFrame implements ApplicationEventSubscriber 
{
    private static MDFClientFrame _instance = new MDFClientFrame();

    private static final Logger logger=Logger.getLogger(MDFClientFrame.class.getName());

    /**
     * Client Context
     */
    private MDFClientContext clientContext=null;

    public static MDFClientFrame getInstance()
    {
        return _instance;
    }

    /**
     * MDF Client Frame Constructor
     */
    private MDFClientFrame()
    {
        super();
    }

    /**
     * Initialize
     * - get the GUI app context from the app manager
     * - init GUI components
     */
    public void initialize() throws InitializationException
    {
    	MDFAppContext appContext=AppManager.getAppContext();
    	
    	if(appContext==null)
    	{
    		throw(new InitializationException("AppContext is null while initializing GUI main frame"));
    	}
    	
    	if(appContext.getAppMode()!=AppMode.GUI)
    	{
    		throw(new InitializationException("Context should be GUI when the application is running in GUI mode."));
    	}
    	
        clientContext=(MDFClientContext) appContext; 

        initComponents();
        
        setTitle(clientContext.getApplicationName());
    }

    /**
     * initialize components
     */
    private void initComponents()
    {
        MDFSplitPane pane = new MDFSplitPane();
        JSplitPane top = pane.getSplitPane();
        top.setBorder(null);

        /**
         * Log messages panel
         */
        JPanel logMessagePanel=new LogMessagePanel();

        /**
         * Market types and market table display
         */
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, logMessagePanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(500);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane,BorderLayout.CENTER);

        /**
         * Status bar
         */
        getContentPane().add(clientContext.getStatusBar(),BorderLayout.SOUTH);

        /**
         * Menu bar
         */
        setJMenuBar(clientContext.getAppMenuBar());
        
        return;
    }
    
    /**
     * Delayed initialization to take care of wiring to the application components
     * such as AppMonitor and event notifications
     */
    public void initApplicationComponents()
    {
        logger.info("ClientFrame is subscribing to AppMonitor for application events");
        AppMonitor monitor=AppManager.getInstance(MDFClientContext.getInstance().getInterestedMulticastGroupNames().get(0)).getAppMonitor();
        monitor.addSubscriber(this);
        
        System.out.println("### Activating the Ready to Start Latch ###");
        AppManager.activateConsumersReadyLatch();
        
        return;
    }
    
    /**
     * Handle application events
     */
	public void notifyEvent(ApplicationEvent event)
	{
		String msg="";
		
		logger.info("ClientFrame received application event : "+event.toString());

		switch(event.getStatus())
		{
		case NETWORKINACTIVITY:
		case NETWORKERROR:
			msg="Application is shutting down due to "+event.getStatus().toString();
			System.out.println(msg);
    		JOptionPane.showMessageDialog(null, msg, event.toString(),JOptionPane.ERROR_MESSAGE);
    		System.exit(1);
			break;
		default:
			break;
		}
		return;
	}
}

