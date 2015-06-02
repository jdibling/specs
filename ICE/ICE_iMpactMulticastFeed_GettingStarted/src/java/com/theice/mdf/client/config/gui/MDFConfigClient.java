package com.theice.mdf.client.config.gui;

import org.apache.log4j.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfigRepository;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MDFConfigClient
{
    private static Logger logger=Logger.getLogger(MDFConfigClient.class.getName());
    
    public MDFConfigClient()
    {
    	init();
    }
    
    private void init()
    {
		try
		{
			MDFClientConfigurator config=MDFClientConfigurator.getInstance();
			config.init();
			
			MDFClientConfigRepository configRepository=config.configure();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return;
    }
    
    private void showHelloThereDialog() throws Exception 
    {
    	   Runnable doShowModalDialog = new Runnable() 
    	   {
    	      public void run() 
    	      {
    	         JOptionPane.showMessageDialog(null,"HelloThere");
    	      }
    	   };
    	   
    	   SwingUtilities.invokeAndWait(doShowModalDialog);
    }
    
    private void launch()
    {
    	try
    	{
        	showHelloThereDialog();
    	}
    	catch(Exception e)
    	{
    	}
    	finally
    	{
    	}
    	
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new MDFConfigFrame();
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    /**
     * MDF Client
     * @param args
     */
    public static void main(String[] args)
    {
        logger.info("MDF Config Client Starting...");
        
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
        }
        
        MDFConfigClient client=new MDFConfigClient();
        client.launch();

        return;
    }
}
