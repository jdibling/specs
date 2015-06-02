package com.theice.mdf.client.gui.panel;

import com.theice.mdf.client.gui.table.PriceLevelTable;
import com.theice.mdf.client.gui.table.PriceLevelModel;
import com.theice.mdf.client.domain.MDFConstants;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.OptionMarket;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The Price Level Panel supports both options markets as well as underlying markets
 * depending on the context
 * 
 * @author Adam Athimuthu
 * Date: Aug 20, 2007
 * Time: 12:48:26 PM
 */
public class PriceLevelPanel extends JPanel
{
    /**
     * Market information
     */
    private JLabel _marketDescription=new JLabel("",JLabel.CENTER);
    private JLabel denomLabel = new JLabel("", JLabel.CENTER);
    private String defaultDenomText = null;

    /**
     * Bid/Order tables
     */
    private PriceLevelTable _bidTable=null;
    private PriceLevelTable _offerTable=null;

    private static final Logger logger=Logger.getLogger(PriceLevelPanel.class.getName());

    /**
     * Price Levels Panel
     * @param market - get the bids/offers from the market object
     */
    public PriceLevelPanel(MarketInterface market)
    {
        super(new BorderLayout());

        /**
         * Displaying the book
         * Book (bid/offers) : D and E, F, G (for regular markets)
         */
        
        _marketDescription.setText(market.getMarketDesc()+" ("+market.getMarketID()+")");
        _marketDescription.setFont(new Font("Arial",Font.BOLD,12));
        
        StringBuffer marketInfoBuffer=new StringBuffer(market.getMarketDesc());
        marketInfoBuffer.append(" (").append(market.getMarketID()).append(")");
        
        StringBuffer denomBuf=new StringBuffer("");
        
        if(!market.isOptionMarket())
        {
            denomBuf.append("Order Price Denominator=["+market.getOrderPriceDenominator()+"] ");
            denomBuf.append("Deal Price Denominator=["+market.getDealPriceDenominator()+"] ");
        }
        else
        {
        	OptionMarket optionMarket=(OptionMarket) market;
        	
        	marketInfoBuffer.append(" StrikePrice=[").append(optionMarket.getStrikePrice());
        	marketInfoBuffer.append("] [").append(optionMarket.getOptionTypeString()).append("]");
        	
            denomBuf.append("Num Decimals Options Price=["+market.getNumDecimalsOptionsPrice()+"] ");
            denomBuf.append("Num Decimals Strike Price=["+market.getNumDecimalsStrikePrice()+"] ");
        }
        
        _marketDescription.setText(marketInfoBuffer.toString());
        _marketDescription.setFont(new Font("Arial",Font.BOLD,12));

        denomLabel=new JLabel(denomBuf.toString(),JLabel.CENTER);
        defaultDenomText=denomBuf.toString();
        denomLabel.setFont(new Font("Arial",Font.BOLD,12));
        denomLabel.setForeground(Color.black);

        JPanel top=new JPanel(new BorderLayout());
        top.add(_marketDescription, BorderLayout.NORTH);
        top.add(denomLabel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        createBidOfferTables(market);
    }

    /**
     * 1. create tables for displaying bids and offers
     * 2. subscribe to MD message events for realtime notifications
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

        _bidTable=new PriceLevelTable(new PriceLevelModel(MDFConstants.BID,market));
        bidPanel.add(_bidTable,BorderLayout.CENTER);

        _offerTable=new PriceLevelTable(new PriceLevelModel(MDFConstants.OFFER,market));
        offerPanel.add(_offerTable, BorderLayout.CENTER);

        bidOfferPanel.add(bidPanel);
        bidOfferPanel.add(offerPanel);

        add(bidOfferPanel,BorderLayout.CENTER);

        return;
    }

    /**
     * refresh the associated models
     */
    public void refresh()
    {
        if(_bidTable!=null)
        {
            _bidTable.getPriceLevelModel().refresh();
        }

        if(_offerTable!=null)
        {
            _offerTable.getPriceLevelModel().refresh();
        }
    }

    /**
     * Cleanup events
     *  unsubscribe
     */
    public void cleanup()
    {
        _bidTable.getPriceLevelModel().cleanup();
        _offerTable.getPriceLevelModel().cleanup();
        return;
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
