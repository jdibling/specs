package com.theice.mdf.client.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.theice.mdf.client.gui.table.OptionMarketTableModel;
import com.theice.mdf.client.gui.table.OptionMarketsTable;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.domain.MDFConstants;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.exception.InvalidStateException;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * Dependent markets dialog is used for displaying the dependent (options) markets
 * 
 * Multicast Group: Options (if a group is market as options, the it also implies price level)
 * Context: PriceLevel
 * 
 * In this scenario, choosing a particular underlying market should only bring up the dependent
 * markets (options), and not the book dialog. Choosing an options market should display
 * the book dialog for options
 * 
 * @author Adam Athimuthu
 */
public class DependentMarketsDialog extends AbstractMDFDialog
{
    /**
     * The market associated with this dialog
     */
	protected MarketInterface _market=null;

    /**
     * Book Panel for displaying a table of bids/offers
     */
    protected JTabbedPane _tabbedPane=new JTabbedPane();
    
    /**
     * Option Markets tab is applicable only for regular markets
     * If the current market is itself an option market, it won't contain further dependent markets
     */
    protected OptionMarketsTable _optionMarketsPanel=null;
    
    private final Logger logger=Logger.getLogger(DependentMarketsDialog.class.getName());

    /**
     * Timer for notifications
     */
    protected Timer _timer=null;
    protected TimerTask _task=null;

    /**
     * @param frame
     * @param market - the underlying market
     */
    public DependentMarketsDialog(JFrame frame, MarketInterface market) throws InvalidStateException
    {
    	super(frame);
    	
    	if(market.isOptionMarket())
    	{
    		throw(new InvalidStateException("Dependent markets dialog is not applicable for options markets." ));
    	}
    	
        setTitle("Options Markets");

        Container container=getContentPane();
        container.setLayout(new BorderLayout());
        
        _market=market;
        _tabbedPane.setPreferredSize(new Dimension(1000,600));

        prepareTabs(market);

        container.add(_tabbedPane,BorderLayout.NORTH);

        /**
         * Start the application timer to receive messages
         */
        startApplicationTimer(MDFConstants.DEPENDENT_MARKETS_REFRESH_INTERVAL,"DependentMarketsDialogTimer");

        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }
    
    protected void prepareTabs(MarketInterface market) throws InvalidStateException
    {
    	MulticastChannelContext channelContext=AppManager.getMulticastChannelContext();

    	switch(channelContext)
    	{
	    	case PRICELEVEL:
	    		/**
	    		 * PriceLevel context is the only time where we can expect to have any options markets
	    		 * PriceLevel in addition to the optionsEnabled flag in config should determine if the options panel is shown
	    		 * Display the option market tab only when the current context is an underlying market
	    		 * Also, in this mode, we display the Price Level tab, if the calling context is the OptionsMarket
	    		 * 
	    		 * Create the option market table model for this underlying market and init the OptionMarketTable
	    		 */
	    		OptionMarketTableModel optionMarketTableModel=new OptionMarketTableModel((Market) _market);
	    		
				_optionMarketsPanel=new OptionMarketsTable(optionMarketTableModel);
				_tabbedPane.addTab("Options Markets", _optionMarketsPanel);
	    		break;
	    		
	    	default:
	    		throw(new InvalidStateException("Dependent markets dialog is not applicable for context other than Price Level." ));
    	}

    	return;
    }

    /**
     * Run the timer task
     */
    protected void runTimerTask()
    {
        if(_market.resetDependentMarketsUpdatedIfTrue())
        {
        	if(_optionMarketsPanel!=null)
        	{
                _optionMarketsPanel.refresh();
        	}
        }
    }

    /**
     * cleanup
     */
    protected void cleanup()
    {
    	if(_optionMarketsPanel!=null)
    	{
            _optionMarketsPanel.cleanup();
    	}
    	AbstractMDFDialog.removeBookFromTrackingMap(_market.getMarketKey());
    }
    
}

