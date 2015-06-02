package com.theice.mdf.client.qv.gui;

import com.theice.mdf.client.multicast.process.PriceLevelMarketHandlerFactory;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.client.process.context.MDFAppContext;
import com.theice.mdf.client.qv.process.QVMDFClient;
import com.theice.mdf.client.qv.process.QVMarketHandlerFactory;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.gui.MDFClientFrame;

import javax.swing.JFrame;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The main GUI client class that initializes the frame and the application threads.
 *
 * @deprecated
 * @author Adam Athimuthu
 */
public class QVMDFGUIClient
{
    private static Logger logger=Logger.getLogger(QVMDFGUIClient.class.getName());

	protected MDFAppContext context=null;

    /**
     * MDF QV GUI Client Constructor
     * - init the context and configure with specific resources
     * - init the application
     * - start the GUI
     */
    public QVMDFGUIClient()
    {
    	AppManager appManager=AppManager.getInstance(null);
    	
    	try
    	{
    		context=MDFClientContext.getInstance();

        	appManager.initialize(context);
    	}
    	catch(InitializationException e)
    	{
    		System.err.println("Application Initialization Failed : "+e.getMessage());
    		System.exit(1);
    	}
    	
    	((MDFClientContext)context).setAppMenuBar(QVMDFMenuBar.getInstance());

    	/**
    	 * Start the socket client with the necessary message handler factory
    	 */
    	MulticastChannelContext multicastChannelContext=AppManager.getMulticastChannelContext();
    	MarketHandlerFactoryInterface messageHandlerFactory=null;
    	
    	context.setApplicationName("PriceFeed Multicast Client - ["+multicastChannelContext.toString()+"]");
    	
    	switch(multicastChannelContext)
    	{
    	case FULLORDERDEPTH:
    		messageHandlerFactory=QVMarketHandlerFactory.getInstance();
    		break;
    	case PRICELEVEL:
    		messageHandlerFactory=PriceLevelMarketHandlerFactory.getInstance();
    		break;
    	default:
    		System.err.println("Message Factory has to be initialized first");
		System.exit(1);
    		break;
    	}
    	
    	appManager.setMessageHandlerFactory(messageHandlerFactory);

    	try
    	{
        	appManager.startRouterClient(new QVMDFClient());
    	}
    	catch(InitializationException e)
    	{
    		System.err.println(e.getMessage());
    		System.exit(1);
    	}


        /**
         * Start all multicast clients
         */
        try
        {
        	appManager.startMulticastClients();
        }
        catch(InitializationException e)
        {
        	logger.error(e.getMessage());
        	System.exit(1);
        }

    	AppManager.startInactivityTimer();

        /**
         * Start the User Interface
         */
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
            	try
            	{
                	MDFClientFrame.getInstance().initialize();
                    System.out.println("### GUI Initialization Completed ###");
            	}
            	catch(InitializationException e)
            	{
            		e.printStackTrace();
            		System.exit(1);
            	}

                JFrame frame = MDFClientFrame.getInstance();
                MDFClientFrame.getInstance().initApplicationComponents();

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public String toString()
    {
    	StringBuffer buf=new StringBuffer("MDFGUIClient");
    	return(buf.toString());
    }

    /**
     * MDF QV GUIClient
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("MDF QV GUI Client Starting...");
        logger.info("MDF QV GUI Client Starting...");
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
        }
        
        new QVMDFGUIClient();

        return;
    }
    
}

