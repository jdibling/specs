package com.theice.mdf.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketStatistics;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Statistics information is initialized using message type C = MarketSnapshot
 * 
 * Statistics is updated using the following message types
 * J = MarketStatistics
 * M = OpenInterest
 * N = OpenPrice
 * O = SettlementPrice
 * 
 * @author Adam Athimuthu
 * Date: Aug 13, 2007
 * Time: 10:12:56 AM
 */
public class StatisticsPanel extends JPanel
{
	private MarketInterface _market=null;
	
	private JTextField _high=new JTextField(5);
	private JTextField _low=new JTextField(5);
	private JTextField _vwap=new JTextField(5);

	private JTextField _totalVolume=new JTextField(5);
	private JTextField _blockVolume=new JTextField(5);
	private JTextField _efsVolume=new JTextField(5);
	private JTextField _efpVolume=new JTextField(5);
	
	private JTextField _openInterest=new JTextField(5);
	private JTextField _openInterestDate=new JTextField(10);
	private JTextField _openingPrice=new JTextField(5);
	private JTextField _settlementPrice=new JTextField(5);
	private JTextField _settleDateTime=new JTextField(10);

	private JTextField _lastTradeQty=new JTextField(5);
	private JTextField _lastTradePrice=new JTextField(5);
	private JTextField _lastTradeDateTime=new JTextField(14);
	private JTextField _settlementOfficial=new JTextField(1);
	
	private static final Font font=new Font("Arial",Font.PLAIN,12);
	private static final Font fontTradeTime=new Font("Arial",Font.PLAIN,10);
	private static final Color bgColorField=new Color(2,2,82);
	private static final Color bgColorPanel=new Color(204,204,204);
	
	private JTextField[] _fields=new JTextField[]
			                    {
			    					_high,
			    					_low,
			    					_vwap,
			    					_efsVolume,
			    					_totalVolume,
			    					_blockVolume,
			    					_settleDateTime,
			    					_efpVolume,
			    					_openInterest,
			    					_openInterestDate,
			    					_openingPrice,
			    					_settlementPrice,
			    					_lastTradeQty,
			    					_lastTradePrice,
			    					_lastTradeDateTime,
			    					_settlementOfficial
			                    };

	private GridBagConstraints gbc=null;
	
	public StatisticsPanel(MarketInterface market)
    {
        super(new BorderLayout());
        _market=market;
        buildPanel();
    }

    /**
     * init components to display the statistics
     * @param Market
     */
    private void buildPanel()
    {
    	JPanel panel=new JPanel(new GridBagLayout());

        gbc=new GridBagConstraints(0,1,1,1,2,2,
        		GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,4),1,1);
	        
        refresh();

    	for(int index=0;index<_fields.length;index++)
    	{
    		_fields[index].setEditable(false);
    		_fields[index].setFont(font);
    		_fields[index].setBackground(bgColorField);
    		_fields[index].setForeground(Color.white);
    	}
    	_lastTradeDateTime.setFont(fontTradeTime);

    	int row=0;
        addGridComponent(0, row, 4, 4, panel, new JLabel("High : "));
        addGridComponent(1, row, 8, 8, panel, _high);
        addGridComponent(2, row, 4, 4, panel, new JLabel("Low : "));
        addGridComponent(3, row, 8, 8, panel, _low);
        addGridComponent(4, row, 4, 4, panel, new JLabel("VWAP : "));
        addGridComponent(5, row, 8, 8, panel, _vwap);
        addGridComponent(6, row, 4, 4, panel, new JLabel("EFS Volume : "));
        addGridComponent(7, row, 8, 8, panel, _efsVolume);

        row++;
        addGridComponent(0, row, 4, 4, panel, new JLabel("Total Volume : "));
        addGridComponent(1, row, 8, 8, panel, _totalVolume);
        addGridComponent(2, row, 4, 4, panel, new JLabel("Block Volume : "));
        addGridComponent(3, row, 8, 8, panel, _blockVolume);
        addGridComponent(4, row, 4, 4, panel, new JLabel("SettleDateTime : "));
        addGridComponent(5, row, 8, 8, panel, _settleDateTime);
        addGridComponent(6, row, 4, 4, panel, new JLabel("EFP Volume : "));
        addGridComponent(7, row, 8, 8, panel, _efpVolume);

        row++;
        addGridComponent(0, row, 4, 4, panel, new JLabel("Open Interest : "));
        addGridComponent(1, row, 8, 8, panel, _openInterest);
        addGridComponent(2, row, 4, 4, panel, new JLabel("OI Date : "));
        addGridComponent(3, row, 8, 8, panel, _openInterestDate);
        addGridComponent(4, row, 4, 4, panel, new JLabel("Open Price : "));
        addGridComponent(5, row, 8, 8, panel, _openingPrice);
        addGridComponent(6, row, 4, 4, panel, new JLabel("Settle Price : "));
        addGridComponent(7, row, 8, 8, panel, _settlementPrice);

        row++;
		addGridComponent(0, row, 4, 4, panel, new JLabel("LastTrade Qty : "));
		addGridComponent(1, row, 8, 8, panel, _lastTradeQty);
		addGridComponent(2, row, 4, 4, panel, new JLabel("LastTradePrice: "));
		addGridComponent(3, row, 8, 8, panel, _lastTradePrice);
		addGridComponent(4, row, 4, 4, panel, new JLabel("LastTradeTime  : "));
		addGridComponent(5, row, 8, 8, panel, _lastTradeDateTime);
      addGridComponent(6, row, 4, 4, panel, new JLabel("Settle Official : "));
      addGridComponent(7, row, 8, 8, panel, _settlementOfficial);

		JPanel masterPanel=new JPanel(new BorderLayout());
		masterPanel.add(panel,BorderLayout.CENTER);
		masterPanel.add(new JLabel(" "),BorderLayout.SOUTH);
		masterPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Statistics",
        		TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));
		
		panel.setBackground(bgColorPanel);
		masterPanel.setBackground(bgColorPanel);

		add(new JScrollPane(masterPanel), BorderLayout.CENTER);
		
        return;
    }
    
    private void addGridComponent(int gridx,int gridy,int weightx,int weighty,Container container,JComponent component)
    {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		
		container.add(component,gbc);
		
    	return;
    }

    /**
     * Refresh data from the market statistics
     */
    public synchronized void refresh()
    {
    	MarketStatistics stats=null;
    	
    	synchronized(_market.getUnderlyingMarket())
    	{
    		stats=_market.getStatistics();

        	if(stats==null)
        	{
        		initialize();
        		return;
        	}

        	_high.setText(Long.toString(stats.getHigh()));
        	_low.setText(Long.toString(stats.getLow()));
        	_vwap.setText(Long.toString(stats.getVwap()));

        	_totalVolume.setText(Integer.toString(stats.getTotalVolume()));
        	_blockVolume.setText(Integer.toString(stats.getBlockVolume()));
        	_efsVolume.setText(Integer.toString(stats.getEfsVolume()));
        	_efpVolume.setText(Integer.toString(stats.getEfpVolume()));
        	
        	_openInterest.setText(Integer.toString(stats.getOpenInterest()));
        	_openInterestDate.setText(stats.getOpenInterestDate());
        	_openingPrice.setText(Long.toString(stats.getOpeningPrice()));
        	_settlementPrice.setText(Long.toString(stats.getSettlementPrice()));
        	_settlementOfficial.setText(Character.toString(stats.getSettlementOfficial()));

        	if (stats.getSettleDateTime() > 0)
        	{
        	   _settleDateTime.setText(MDFUtil.simpleDateTimeFormatter.format(stats.getSettleDateTime()));
        	}
        	
        	if(stats.getLastTradeQuantity()>0 || stats.getLastTradePrice()>0)
        	{
            	_lastTradeQty.setText(Integer.toString(stats.getLastTradeQuantity()));
            	_lastTradePrice.setText(Long.toString(stats.getLastTradePrice()));
            	_lastTradeDateTime.setText(MDFUtil.tradeTimeFormatter.format(stats.getLastTradeDateTime()));
        	}
        	else
        	{
            	_lastTradeQty.setText("");
        		_lastTradePrice.setText("");
        		_lastTradeDateTime.setText("");
        	}
    	}
    	
    	return;
    }
    
    public void initialize()
    {
    	_high.setText("");
    	_low.setText("");
    	_vwap.setText("");

    	_totalVolume.setText("");
    	_blockVolume.setText("");
    	_efsVolume.setText("");
    	_efpVolume.setText("");
    	
    	_openInterest.setText("");
    	_openInterestDate.setText("");
    	_openingPrice.setText("");
    	_settlementPrice.setText("");
    	_settleDateTime.setText("");
    	_settlementOfficial.setText("");

    	_lastTradeQty.setText("");
		_lastTradePrice.setText("");
		_lastTradeDateTime.setText("");
    }

    public void cleanup()
    {
    }

//    /**
//     * MD Subscriber event notifications
//     * Subscriptions Currently disabled
//     * @param message
//     */
//    public synchronized void notifyWithMDMessage(MDMessage message)
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
//            case RawMessageFactory.MarketSnapshotMessageType:
//            	refresh();
//                break;
//
//            case RawMessageFactory.MarketStatisticsMessageType:
//            	
//            	MarketStatisticsMessage marketStatisticsMessage=(MarketStatisticsMessage) message;
//            	
//            	_high.setText(Long.toString(marketStatisticsMessage.High));
//            	_low.setText(Long.toString(marketStatisticsMessage.Low));
//            	_vwap.setText(Long.toString(marketStatisticsMessage.VWAP));
//
//            	_totalVolume.setText(Integer.toString(marketStatisticsMessage.TotalVolume));
//            	_blockVolume.setText(Integer.toString(marketStatisticsMessage.BlockVolume));
//            	_efsVolume.setText(Integer.toString(marketStatisticsMessage.EFSVolume));
//            	_efpVolume.setText(Integer.toString(marketStatisticsMessage.EFPVolume));
//            	
//            	break;
//            	
//            case RawMessageFactory.OpenInterestMessageType:
//            	OpenInterestMessage openInterestMessage=(OpenInterestMessage) message;
//            	_openInterest.setText(Integer.toString(openInterestMessage.OpenInterest));
//            	break;
//            	
//            case RawMessageFactory.OpenPriceMessageType:
//            	OpenPriceMessage openPriceMessage=(OpenPriceMessage) message;
//            	_openingPrice.setText(Long.toString(openPriceMessage.OpenPrice));
//            	break;
//            	
//            case RawMessageFactory.SettlementPriceMessageType:
//            	SettlementPriceMessage settlementPriceMessage=(SettlementPriceMessage) message;
//            	_settlementPrice.setText(Long.toString(settlementPriceMessage.SettlementPrice));
//            	break;
//            	
//            case RawMessageFactory.TradeMessageType:
//            	TradeMessage tradeMessage=(TradeMessage) message;
//
//            	if(MDFUtil.canProcessTrade(tradeMessage))
//                {
//            		if(tradeMessage.Price>0)
//            		{
//                    	_lastTradePrice.setText(Long.toString(tradeMessage.Price));
//            		}
//            		
//            		if(tradeMessage.Quantity>0)
//            		{
//                    	_lastTradeQty.setText(Integer.toString(tradeMessage.Quantity));
//            		}
//                	
//                	if(tradeMessage.DateTime>0L)
//                	{
//                    	_lastTradeDateTime.setText(MDFUtil.tradeTimeFormatter.format(tradeMessage.DateTime));
//                	}
//                }
//            	break;
//            	
//            default:
//                System.out.println("Notification received for unknown message type: "+messageType);
//                break;
//        }
//
//    }
    
}

