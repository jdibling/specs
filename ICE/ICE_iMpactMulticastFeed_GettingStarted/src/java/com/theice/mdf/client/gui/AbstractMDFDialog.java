package com.theice.mdf.client.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.theice.mdf.client.domain.MarketKey;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract MDF dialog.
 * Contains a timer
 * Supports disposal using the escape key
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractMDFDialog extends JDialog 
{
    private static final Logger logger=Logger.getLogger(AbstractMDFDialog.class.getName());
    
    /**
     * Map containing all open (Book) dialog windows
     */
    private static Map<MarketKey,AbstractMDFDialog> allOpenDialog=new Hashtable<MarketKey,AbstractMDFDialog>(); 

    /**
     * Timer for notifications
     */
    protected Timer _timer=null;
    protected TimerTask _task=null;

    /**
     * Make it as a modal dialog
     * @param frame
     * @param market - used to obtain the bids/offers collections to init the model
     */
    protected AbstractMDFDialog(JFrame frame)
    {
        super(frame,false);
        
        setResizable(false);

        getContentPane().setLayout(new BorderLayout());
    }

    /**
     * start the application timer used for refreshing the models
     * @param interval
     */
    protected void startApplicationTimer(int interval,String timerName)
    {
        _timer=new Timer(timerName);

        _task=new TimerTask()
            {
                public void run()
                {
                    try
                    {
                    	runTimerTask();
                    }
                    catch(Throwable e)
                    {
                        logger.warn("Failed refreshing the dialog : "+ MDFUtil.getStackInfo(e));
                    }
                }
            };

        _timer.scheduleAtFixedRate(_task, 0, interval);

        return;
    }
    
    /**
     * JDialog override(s)
     * Override window close event
     * - cleanup the panels and subscriptions
     * - cancel the timer task
     */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
        	cleanup();
            
            if(_timer!=null)
            {
            	_timer.cancel();
            }
            
            dispose();
        }
    }

    /**
     * override from the JDialog
     * Supporting the escape key
     */
    protected JRootPane createRootPane()
    {
    	ActionListener actionListener=new ActionListener()
    	{
    		public void actionPerformed(ActionEvent actionEvent)
    		{
            	cleanup();
                
                if(_timer!=null)
                {
                	_timer.cancel();
                }
                
    			dispose();
    		}
    	};
    	
		JRootPane rootPane=new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		rootPane.setOpaque(true);
        
		return(rootPane);
    }
    
    public static AbstractMDFDialog getBookDialogWindow(MarketKey marketKey)
    {
       AbstractMDFDialog dialogWindow=null;
       try
       {
          dialogWindow=allOpenDialog.get(marketKey);
       }
       catch(Exception ex)
       {
          logger.error("Exception caught when retrieving dialog window for marketKey:"+marketKey, ex);
       }
       
       return dialogWindow;
    }
    
    public static void addBookToTrackingMap(MarketKey marketKey, AbstractMDFDialog dialogWindow)
    {
       try
       {
          allOpenDialog.put(marketKey, dialogWindow);
       }
       catch(Exception ex)
       {
          logger.error("Exception caught when adding dialog window to Map for marketKey:"+marketKey, ex);
       }
    }
    
    public static void removeBookFromTrackingMap(MarketKey marketKey)
    {
       try
       {
          allOpenDialog.remove(marketKey);
       }
       catch(Exception ex)
       {
          logger.error("Exception caught when removing dialog window from Map for marketKey:"+marketKey,ex);
       }
    }
    
    protected abstract void runTimerTask();
    protected abstract void cleanup();

}

