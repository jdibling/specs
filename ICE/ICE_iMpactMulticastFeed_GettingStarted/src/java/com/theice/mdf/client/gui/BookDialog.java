package com.theice.mdf.client.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

import com.theice.mdf.client.gui.panel.BookPanel;
import com.theice.mdf.client.gui.panel.PriceLevelPanel;
import com.theice.mdf.client.gui.panel.StatisticsPanel;
import com.theice.mdf.client.gui.panel.TradeHistoryPanel;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.domain.GUIComponentsContext;
import com.theice.mdf.client.domain.MDFConstants;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.book.MulticastChannelContext;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Book Dialog - displaying the Book for a specific market and the price level
 * The dialog also contains information on the market statistics and trade
 *
 * The dialog pulls the information from the application data structures for refreshing the
 * book and pricelevel models used for display. For efficiency, we use a timer task scheduled at fixed
 * intervals (200 ms). The timer checks to see if the market information is 'dirty' by checking a flag.
 * If the market's order information  hasn't changed since the last refresh, the timer simply returns.
 * Otherwise, a message is sent to the book and price level panels for refreshing the GUI.
 * 
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 10:01:07 AM
 *
 */
public class BookDialog extends AbstractBookDialog
{
	/**
	 * Book Panel has the order depth information
	 * If the application is in price level context, this panel will not be displayed
	 */
    protected BookPanel _bookPanel=null;
    
    /**
     * Price level panels are displayed for both full order depth as well as Price Level contexts
     */
    protected PriceLevelPanel _priceLevelPanel=null;
    
    /**
     * Panel for displaying market statistics
     */
    protected StatisticsPanel _statisticsPanel=null;

    /**
     * Panel for displaying recent trades
     */
    protected TradeHistoryPanel _tradeHistoryPanel=null;

    private final Logger logger=Logger.getLogger(BookDialog.class.getName());

    /**
     * BookDialog Constructor
     * Make it as a modal dialog
     * @param frame
     * @param market - used to obtain the bids/offers collections to init the model
     */
    public BookDialog(JFrame frame, MarketInterface market)
    {
    	super(frame,market);
    	
        if(market.isOptionMarket())
        {
            setTitle("Book for Options Market");
        }
        else
        {
            setTitle("Book");
        }

        Container container=getContentPane();
        
        prepareTabs(market);

        container.add(_tabbedPane,BorderLayout.NORTH);

        /**
         * Statistics
         */
        _statisticsPanel=new StatisticsPanel(market);
        container.add(_statisticsPanel, BorderLayout.CENTER);

        /**
         * Trades History
         * (List/Table of Trades (Upto 50): G and H,I)
         */
        _tradeHistoryPanel=new TradeHistoryPanel(market);
        container.add(_tradeHistoryPanel, BorderLayout.SOUTH);

        /**
         * Dispose on close
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         */

        /**
         * Start the application timer to receive messages
         */
        startApplicationTimer(MDFConstants.BOOK_REFRESH_INTERVAL,"BookDialogTimer");
        
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }
    
    /**
     * prepare the tabs based on the current multicast channel context and if this dialog
     * currently is used for the options market
     * 
     * If this dialog is used by the underlying market, we have the following scenarios:
     * 
     * 1. PriceLevel/Full Order display if the multicast channel context is FullOrderDepth
     * 2. PriceLevel only, if the multicast channel context is PriceLevel
     * 
     * In addition, a tab is displayed to hold the options markets
     * 
     * If the dialog is used by the options market (multicast mode=OptionsTopOfBook), then:
     * 
     * 1. Initialize only the PriceLevel tab
     * 
     * @param Market
     */
    protected void prepareTabs(MarketInterface market)
    {
    	MulticastChannelContext channelContext=AppManager.getMulticastChannelContext();
    	GUIComponentsContext guiContext = null;

    	switch(channelContext)
    	{
	    	case FULLORDERDEPTH:
	    		_priceLevelPanel=new PriceLevelPanel(market);
	    		_tabbedPane.addTab("Price Level View", _priceLevelPanel);

	    		_bookPanel=new BookPanel(market);
	    		_tabbedPane.addTab("Order View", _bookPanel);
	    		
	    		guiContext = new GUIComponentsContext();
            guiContext.setBookPanel(_bookPanel);
            guiContext.setPriceLevelPanel(_priceLevelPanel);
            market.setGUIComponentsContext(guiContext);
	    		
	    		break;
	    		
	    	case PRICELEVEL:
	    		/**
	    		 * PriceLevel context is the only time where we can expect to have any options markets
	    		 * PriceLevel in addition to the optionsEnabled flag in config should determine if the options panel is shown
	    		 * Display the option market tab only when the current context is an underlying market
	    		 * Also, in this mode, we display the Price Level tab, if the calling context is the OptionsMarket
	    		 */
	    		String priceLevelPanelTitle=(_market.isOptionMarket()?"Options Price Level View":"Price Level View");
	    		
	    		_priceLevelPanel=new PriceLevelPanel(market);
	    		_tabbedPane.addTab(priceLevelPanelTitle, _priceLevelPanel);
	    		
	    		guiContext = new GUIComponentsContext();
            guiContext.setPriceLevelPanel(_priceLevelPanel);
            market.setGUIComponentsContext(guiContext);

	    		break;
    	}

    	return;
    }

    /**
     * Run the timer task
     */
    protected void runTimerTask()
    {
    	if(_market.resetBookUpdatedIfTrue())
    	{
    		if(_bookPanel!=null)
    		{
                _bookPanel.refresh();
    		}
    		
    		if(_priceLevelPanel!=null)
    		{
                _priceLevelPanel.refresh();
    		}
    	}
    	
        if(_market.resetOrderUpdatedIfTrue() || _market.resetMarketStatsUpdatedIfTrue())
        {
            _statisticsPanel.refresh();
        }

    	if(_market.resetTradeHistoryUpdatedIfTrue())
        {
            if(_tradeHistoryPanel!=null)
        	{
                _tradeHistoryPanel.refresh();
        	}
        }
    	
    	return;
    }

    /**
     * cleanup
     */
    protected void cleanup()
    {
       if(_bookPanel!=null)
       {
          _bookPanel.cleanup();
       }

       if(_priceLevelPanel!=null)
       {
          _priceLevelPanel.cleanup();
       }

       if(_statisticsPanel!=null)
       {
          _statisticsPanel.cleanup();
       }

       if(_tradeHistoryPanel!=null)
       {
          _tradeHistoryPanel.cleanup();
       }
       
       if (_market.getGUIComponentsContext() != null)
       {
          _market.setGUIComponentsContext(null);
       }

       AbstractMDFDialog.removeBookFromTrackingMap(_market.getMarketKey());

       return;
    }
}

