package com.theice.mdf.client.gui.panel;

import com.theice.mdf.client.gui.table.BookTable;
import com.theice.mdf.client.gui.table.BookTableModel;
import com.theice.mdf.client.domain.MDFConstants;
import com.theice.mdf.client.domain.MarketInterface;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Book Panel consists of two sub panels for displaying bids and offers
 *
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 11:05:41 AM
 */
public class BookPanel extends JPanel
{
    /**
     * Market information
     */
    private JLabel _marketDescription=new JLabel("",JLabel.CENTER);
    private JLabel denomLabel=null;
    private String defaultDenomText = null;
    
    /**
     * Bid/Order tables
     */
    private BookTable _bidTable=null;
    private BookTable _offerTable=null;

    private Logger logger=Logger.getLogger(BookPanel.class.getName());

    /**
     * Markets JList Constructor
     * @param market - get the bids/offers from the market object
     */
    public BookPanel(MarketInterface market)
    {
        super(new BorderLayout());

        /**
         * Displaying the book
         * Book (bid/offers) : D and E, F, G
         */
        _marketDescription.setText(market.getMarketDesc()+" ("+market.getMarketID()+")");
        _marketDescription.setFont(new Font("Arial",Font.BOLD,12));
        
        StringBuffer denomBuf=new StringBuffer("");
        
        /**
         * Book panel is currently not used for options markets, so the following "else"
         * condition never likely to occur at runtime
         */
        if(!market.isOptionMarket())
        {
            denomBuf.append("Order Priceeee Denominator=["+market.getOrderPriceDenominator()+"] ");
            denomBuf.append("Deal Price Denominator=["+market.getDealPriceDenominator()+"] ");
        }
        else
        {
            denomBuf.append("Num Decimals Options Price=["+market.getNumDecimalsOptionsPrice()+"] ");
            denomBuf.append("Num Decimals Strike Price=["+market.getNumDecimalsStrikePrice()+"] ");
        }
        
        denomLabel=new JLabel(denomBuf.toString(),JLabel.CENTER);
        denomLabel.setFont(new Font("Arial",Font.BOLD,12));
        denomLabel.setForeground(Color.black);
        defaultDenomText=denomBuf.toString();

        JPanel top=new JPanel(new BorderLayout());
        top.add(_marketDescription, BorderLayout.NORTH);
        top.add(denomLabel, BorderLayout.CENTER);
        
        add(top, BorderLayout.NORTH);

        createBidOfferTables(market);
    }

    /**
     * create tables for displaying bids and offers
     *
     * @param market
     */
    private void createBidOfferTables(MarketInterface market)
    {
        JPanel bidOfferPanel=new JPanel();
        bidOfferPanel.setLayout(new GridLayout(1,1));

        JPanel bidPanel=new JPanel();
        JPanel offerPanel=new JPanel();

        bidPanel.setLayout(new BorderLayout());
        bidPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1),
        		"Bids",TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));

        offerPanel.setLayout(new BorderLayout());
        offerPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1),
        		"Offers",TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));

        _bidTable=new BookTable(new BookTableModel(MDFConstants.BID,market),
        		market.isOptionMarket());
        bidPanel.add(_bidTable,BorderLayout.CENTER);

        _offerTable=new BookTable(new BookTableModel(MDFConstants.OFFER,market),
        		market.isOptionMarket());
        offerPanel.add(_offerTable, BorderLayout.CENTER);
        
        bidOfferPanel.add(bidPanel);
        bidOfferPanel.add(offerPanel);

        add(bidOfferPanel,BorderLayout.CENTER);

        return;
    }

    /**
     * Cleanup
     */
    public void cleanup()
    {
        _bidTable.getBookTableModel().cleanup();
        _offerTable.getBookTableModel().cleanup();
        return;
    }

    /**
     * refresh the associated models
     */
    public void refresh()
    {
        if(_bidTable!=null)
        {
            _bidTable.getBookTableModel().refresh();
        }

        if(_offerTable!=null)
        {
            _offerTable.getBookTableModel().refresh();
        }
    }

    /**
     * Force Refresh book table
     * TODO For testing purposes only
     */
    public void refreshBook()
    {
        _bidTable.getBookTableModel().fireTableDataChanged();
        _offerTable.getBookTableModel().fireTableDataChanged();
    }
    
    public void appendDenomLabelText(String str)
    {
       denomLabel.setText(str);
       denomLabel.setForeground(Color.red);
    }
    
    public void resetDenomLabelText()
    {
       denomLabel.setText(defaultDenomText);
       denomLabel.setForeground(Color.black);
    }    

}
