package com.theice.mdf.client.gui;

import java.util.List;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.MDFClientConfiguratorHelper;
import com.theice.mdf.client.config.MDFCommandLineConfigurator;
import com.theice.mdf.client.config.domain.ConfigurationSelectorInfo;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.config.gui.dialog.MDFClientConfigDialog;
import com.theice.mdf.client.domain.MarketHandlerFactory;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.exception.ConfigurationException;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.multicast.process.PriceLevelMarketHandlerFactory;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClient;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.process.MarketHandlerFactoryInterface;
import com.theice.mdf.client.process.context.MDFAppContext;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The main GUI client class that initializes the frame and launches application threads.
 * 
 * System Parameters
 * 
 * -Denvironment (apitest, perftest etc.)
 * -DmulticastGroup (The actual group name as defined in the xml file)
 * -Dcontext (fullOrderDepth, priceLevel...)
 * 
 * General Flow
 * 	Create a context (GUI/CommandLine)
 * 	Load the configuration
 * 	Determine the config dialog or command line options (depending on the mode)
 * 		For command modes, always expect the parameters to be available in the env
 * 		For GUI modes, if the config is not supplied through the env, launch the dialog
 * 	Init the App Manager
 * 	Register handlers
 * 	Start the TCP
 * 	Start the multicast client pairs
 * 	Handle Login Failures (Login Response Handler)
 * 	Handler Product Defintion responses (generic model update through the context)
 * 	AppMonitor to monitor events
 * 	Latch Activation
 * 
 * @author Adam Athimuthu
 * Date: Aug 3, 2007
 * Time: 10:08:26 AM
 */
public class MDFGUIClient
{
   private static Logger logger=Logger.getLogger(MDFGUIClient.class.getName());

   protected AppManager appManager=null;;

   protected MDFAppContext context=null;

   /**
    * MDF GUI Client Constructor
    * - init the context and configure with specific resources
    * - init the application manager with a GUI context
    * - start the GUI
    */
   public MDFGUIClient(String groupName)
   {
      try
      {
         context=MDFClientContext.getInstance();
         appManager = new AppManager(groupName);
         AppManager.addToInstancesMap(appManager);
         appManager.initialize(context);
      }
      catch(InitializationException e)
      {
         System.err.println("Application Initialization Failed : "+e.getMessage());
         System.exit(1);
      }
   }

   /**
    * launch the price feed client
    */
   public void launch()
   {
      ((MDFClientContext) context).setAppMenuBar(MDFMenuBar.getInstance());

      /**
       * Start the socket client with the necessary message handler factory
       */
      MulticastChannelContext multicastChannelContext=AppManager.getMulticastChannelContext();
      MarketHandlerFactoryInterface messageHandlerFactory=null;

      context.setApplicationName("PriceFeed Multicast Client - ["+multicastChannelContext.toString()+"]");

      switch(multicastChannelContext)
      {
      case FULLORDERDEPTH:
         messageHandlerFactory=MarketHandlerFactory.getInstance();
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
         Thread routerClientThread=appManager.startRouterClient(new MDFClient());
      }
      catch(InitializationException e)
      {
         System.err.println(e.getMessage());
         System.exit(1);
      }

      /**
       * Start the multicast clients
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

            JFrame frame=MDFClientFrame.getInstance();
            MDFClientFrame.getInstance().initApplicationComponents();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
         }
      });
   }

   /**
    * Choose the configuration interactively
    * 
    * - Environment
    * - Context
    * - Multicast Group Name
    * 
    * @throws Exception
    */
   private static void chooseConfigurationInteractive() throws Exception
   {
      Runnable doShowModalDialog = new Runnable() 
      {
         public void run() 
         {
            MDFClientConfigDialog dialog=new MDFClientConfigDialog(null,"Multicast Client Configuration");
         }
      };

      SwingUtilities.invokeAndWait(doShowModalDialog);

      return;
   }

   /**
    * MDF GUI Client
    * @param args
    */
   public static void main(String[] args)
   {
      logger.info("MDF Client Starting...");

      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }

     
      try
      {
         MDFClientConfiguratorHelper.loadConfiguration();
      }
      catch(Exception ex)
      {
         logger.error("Exception while loading client configuration: "+ex, ex);
         System.exit(1);
      }
            
      boolean clientConfigSelected=false;
      try
      {
         clientConfigSelected = MDFClientConfiguratorHelper.chooseConfiguration();
      }
      catch(Exception ex)
      {
         System.out.println("Client Config no selected, launch GUI window");
      } 
      
      if(!clientConfigSelected)
      {
         try
         {
            chooseConfigurationInteractive();
         }
         catch(Exception ex)
         {
            System.out.println("Error selecting client configuration: "+ex);
            ex.printStackTrace();
         }
      }
    
      MDFClientConfiguration mdfClientConfig=MDFClientConfigurator.getInstance().getCurrentConfiguration();

      if(mdfClientConfig!=null)
      {
         System.out.println("### Configuration Selected : "+mdfClientConfig.toString());
         List<String> interestedMCGroupName = mdfClientConfig.getInterestedMulticastGroupNames();
         MarketsHolder.init(interestedMCGroupName);

         //currently, only support 1 group in GUI
         MDFGUIClient client=new MDFGUIClient(interestedMCGroupName.get(0));
         MDFClientContext.getInstance().setInterestedMulticastGroupNames(interestedMCGroupName);
         client.launch();
      }
      else
      {
         System.err.println("Configuration not selected.");
      }
   }
}

