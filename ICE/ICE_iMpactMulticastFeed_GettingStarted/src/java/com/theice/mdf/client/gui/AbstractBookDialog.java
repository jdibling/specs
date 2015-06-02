package com.theice.mdf.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import org.apache.log4j.Logger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.theice.mdf.client.domain.MarketInterface;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract book dialog. Implements the basic functionality for supporting regular and option markets
 * Contains a timer.
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractBookDialog extends AbstractMDFDialog 
{
    private final Logger logger=Logger.getLogger(AbstractBookDialog.class.getName());

    /**
     * The market associated with this dialog
     */
	protected MarketInterface _market=null;

    /**
     * Book Panel for displaying a table of bids/offers
     */
    protected JTabbedPane _tabbedPane=new JTabbedPane();
    
    /**
     * BookDialog Constructor
     * Make it as a modal dialog
     * @param frame
     * @param market - used to obtain the bids/offers collections to init the model
     */
    protected AbstractBookDialog(JFrame frame, MarketInterface market)
    {
        super(frame);
        
        setResizable(false);

        getContentPane().setLayout(new BorderLayout());
        
        _market=market;

        /**
         * Pane for PriceLevels and the Book
         */
        _tabbedPane.setPreferredSize(new Dimension(800,300));
    	
        /**
         * Dispose on close
         */
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

}

