package com.theice.mdf.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import com.theice.mdf.client.domain.InvestigationStatus;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.SpotTradeInfo;
import com.theice.mdf.client.domain.Trade;
import com.theice.mdf.client.process.handlers.TradeMessageHandler;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.notification.TradeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Trade History - displays the recent 50 trades for a given market
 * 
 * @author Adam Athimuthu
 */
public class TradeHistoryPanel extends JPanel
{
	private MarketInterface _market=null;
	
    private TradeHistoryListModel _tradesListModel=new TradeHistoryListModel();
    private JList _tradesList=new JList(_tradesListModel);

    public TradeHistoryPanel(MarketInterface market)
	{
        super(new BorderLayout());

        _market=market;
        
        buildPanel();
        init();
	}
	
    private void buildPanel()
    {
    	setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Recent Trades",
        		TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));
    	
    	_tradesList.setBackground(Color.black);
    	_tradesList.setForeground(Color.white);
    	_tradesList.setFont(new Font("Arial",Font.PLAIN,12));
    	_tradesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	
    	DefaultListSelectionModel selectionModel=new DefaultListSelectionModel()
    	{
    	    public boolean isSelectedIndex(int index) 
    	    {
    	    	return (false);
    	    }

    	    public boolean isSelectionEmpty() 
    	    {
    	    	return (true);
    	    }
    	};
    	_tradesList.setSelectionModel(selectionModel);
    	
    	JScrollPane scrollPane=new JScrollPane(_tradesList);
    	scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    	add(scrollPane,BorderLayout.CENTER);
    	
    	refresh();

    	return;
    }

    /**
     * refresh information from the market's recent trades list
     */
    public synchronized void refresh()
    {
    	String[] trades=new String[0];
    	
    	synchronized(_market.getUnderlyingMarket())
    	{
        	List<Trade> recentTrades=_market.getRecentTrades();
        	
	    	int size=recentTrades.size();
	    	
	    	if(size>0)
	    	{
		    	trades=new String[size];

		    	int index=0;
		    	
	        	for(Iterator it=recentTrades.iterator();it.hasNext();index++)
	        	{
	        		Trade trade=(Trade) it.next();
	        		
	        		trades[size-index-1]=getTradeInformation(trade);
	        	}
	    	}
	    	else
	    	{
		    	trades=new String[0];
	    	}
    	}

    	_tradesListModel.setHistory(trades);
    	this.repaint();

    	return;
    }

    /**
     * Get trade information as a string so we can display in the history panel
     * @param tradeMessage
     * @return
     */
    private String getTradeInformation(Trade trade)
    {
    	StringBuffer buffer=new StringBuffer(" ");
    	
    	TradeMessage tradeMessage=(TradeMessage) trade.getTradeMessage();
    	
    	buffer.append(tradeMessage.Quantity);
    	buffer.append(" @ ");
    	buffer.append(tradeMessage.Price);
    	buffer.append("   ");
    	buffer.append("("+MDFUtil.tradeMsTimeFormatter.format(tradeMessage.DateTime)+") ");
    	
    	if(trade.isSpotTrade())
    	{
    		SpotTradeInfo _spotTradeInfo = trade.getSpotTradeInfo();
    		buffer.append("   ");
    		buffer.append("DeliveryBeginDateTime:");
    		buffer.append(MDFUtil.tradeMsTimeFormatter.format(_spotTradeInfo.getDeliveryBeginDateTime()));
    		buffer.append("   ");
    		buffer.append("DeliveryEndDateTime:");
    		buffer.append(MDFUtil.tradeMsTimeFormatter.format(_spotTradeInfo.getDeliveryEndDateTime()));
    	}
    	
    	if(!trade.isOptionsTrade())
    	{
        	buffer.append("IsSystemPricedLeg="+tradeMessage.IsSystemPricedLeg);
        	
        	if(tradeMessage.IsSystemPricedLeg=='Y' && tradeMessage.SystemPricedLegType!=' ')
        	{
            	buffer.append(" [SystemPricedLegType=").append(tradeMessage.SystemPricedLegType).append("]");
        	}
    	}
    	
    	if(tradeMessage.BlockTradeType!=' ')
    	{
    	   String tradeTypeDesc = TradeMessageHandler.getOffExchangeDealTradeType(tradeMessage.BlockTradeType);
    	   buffer.append(" : ["+tradeTypeDesc+"]");
    	}
    	
    	InvestigationStatus investigationStatus=trade.getInvestigationStatus();
    	
    	if(investigationStatus!=null)
    	{
        	buffer.append(" : ["+investigationStatus.getStatusDescription()+"]");
    	}
    	
    	if(trade.isCancelled())
    	{
    		buffer.append(" : <<CANCELLED>>");
    	}
    	
    	if (trade.isAdjusted())
    	{
    	   buffer.append(" : <<ADJUSTED>>");
    	}
    	
    
    	
    	return(buffer.toString());
    }

    /**
     * initialize
     */
    public void init()
    {
    }

    public void cleanup()
    {
    }
    
//  /**
//  * Directly process a single trade in order to update the  history
//  * The latest trade should be inserted at the beginning of the list
//  * @param tradeMessage
//  */
// private void refreshSingleTrade(TradeMessage tradeMessage)
// {
// 	int size=_tradesListModel.getSize();
// 	
// 	String tradeMsg=getTradeInformation(tradeMessage);
// 	
// 	if(size>=_market._maxRecentTrades)
// 	{
// 		_tradesListModel.removeLast();
// 	}
// 	
// 	_tradesListModel.addFirst(tradeMsg);
// 	
// 	return;
// }
//    /**
//     * MD Subscriber event notifications
//     * Subscriptions currently disabled
//     * @param message
//     * @deprecated
//     */
//    private void notifyWithMDMessage(MDMessage message)
//    {
//        char messageType=message.getMessageType();
//        
//    	int marketId=((MDMessage)message).getMarketID();
//    	
//    	if(_market.getMarketID()!=marketId)
//    	{
//    		return;
//    	}
//
//        switch(messageType)
//        {
//            case RawMessageFactory.TradeMessageType:
//            	TradeMessage tradeMessage=(TradeMessage) message;
//            	
//            	if(tradeMessage.Quantity>0)
//                {
//                    refreshSingleTrade(tradeMessage);
//                }
//                break;
//
//            default:
//                System.out.println("Notification received for unknown message type: "+messageType);
//                break;
//        }
//        
//    	return;
//    }
}

