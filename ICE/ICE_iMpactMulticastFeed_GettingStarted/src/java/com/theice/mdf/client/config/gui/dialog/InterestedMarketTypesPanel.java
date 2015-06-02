package com.theice.mdf.client.config.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Interested market types panel
 * 
 * Collaborations:
 * 
 * Since the market types we display depends on the multicast channel pair, we subscribe to
 * the multicast group table
 * 
 * @author Adam Athimuthu
 */
public class InterestedMarketTypesPanel extends JPanel implements ListSelectionListener,ActionListener
{
    private static Logger logger=Logger.getLogger(InterestedMarketTypesPanel.class.getName());

    protected JCheckBox[] availableMarketTypesCheckBoxes=null;
    protected MarketType[] availableMarketTypes=new MarketType[0];
    
    protected JPanel availableMarketTypesPanel=new JPanel(new GridLayout(5,3));
	protected JLabel messageLabel=new JLabel("");
	protected JPanel messagePanel=new JPanel();
	
    protected JTextPane informationTextPane=new JTextPane();
    
    public static final String actionCheckAll="CheckAll";
	protected JButton commandCheckAll=new JButton(actionCheckAll);
    public static final String actionUnCheckAll="UnCheckAll";
	protected JButton commandUnCheckAll=new JButton(actionUnCheckAll);
	protected JCheckBox udsCheckbox=new JCheckBox("Incl. UDS");

    /**
     * Reference to the multicast group config table in order for us to get the currently
     * selected multicast channel group name
     */
	protected MulticastGroupConfigTable multicastGroupConfigTable=null;

	public InterestedMarketTypesPanel(ActionListener listener,MulticastGroupConfigTable multicastGroupConfigTable)
	{
		super(new BorderLayout());

		/**
		 * Subscribe to list selection events
		 */
		this.multicastGroupConfigTable=multicastGroupConfigTable;
		this.multicastGroupConfigTable.setSelectionListener(this);
		
    	setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Interested Market Types",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));
        setFont(MDFUtil.fontArialPlain12);
        
        add(createInformationTextPane(),BorderLayout.NORTH);

        JScrollPane checkBoxScrollPane=new JScrollPane(createMarketTypesCheckBoxes());
        add(checkBoxScrollPane,BorderLayout.CENTER);
        
		messagePanel.add(messageLabel);
		messagePanel.setVisible(false);
        add(messagePanel,BorderLayout.SOUTH);
	}
	
	private MarketType[] chooseAvailableMarketTypes(String multicastGroup)
	{
		List<MarketType> availableMarketTypesList=
			MDFClientConfigurator.getInstance().getConfigRepository().getMarketTypes(multicastGroup);
		
		availableMarketTypes=availableMarketTypesList.toArray(new MarketType[0]);
		
		Arrays.sort(availableMarketTypes,new MarketTypeSorter());

		return(availableMarketTypes);
	}
	
	/**
	 * create market types check boxes
	 * @return
	 */
	private JComponent createMarketTypesCheckBoxes()
	{
		MulticastChannelPairInfo channelPair=multicastGroupConfigTable.getSelectedChannelPairInfo();
		
		availableMarketTypes=new MarketType[0];
		availableMarketTypesPanel.removeAll();
		availableMarketTypesCheckBoxes=new JCheckBox[0];
		
		if(channelPair==null)
		{
			availableMarketTypesPanel.setVisible(false);
			messageLabel.setText("Please select a multicast group first.");
			messagePanel.setVisible(true);
			return(availableMarketTypesPanel);
		}
		
		if(logger.isTraceEnabled())
		{
			logger.trace("Looking for market types in : "+channelPair.getGroupName());
		}

		chooseAvailableMarketTypes(channelPair.getGroupName());

		if(availableMarketTypes==null || availableMarketTypes.length==0)
		{
			availableMarketTypesPanel.setVisible(false);
			messageLabel.setText("No market types available for this channel.");
			messagePanel.setVisible(true);
			return(availableMarketTypesPanel);
		}
		
		availableMarketTypesPanel.setFont(MDFUtil.fontArialPlain12);
		
        availableMarketTypesCheckBoxes=new JCheckBox[availableMarketTypes.length];
        
        for(int index=0;index<availableMarketTypesCheckBoxes.length;index++)
        {
        	availableMarketTypesCheckBoxes[index]=new JCheckBox(availableMarketTypes[index].getDisplayableMarketTypeInfo());
        	availableMarketTypesCheckBoxes[index].setSelected(true);
        	availableMarketTypesCheckBoxes[index].setFont(MDFUtil.fontArialPlain12);
        	
    		if(logger.isTraceEnabled())
    		{
        		logger.trace("### Adding : "+availableMarketTypesCheckBoxes[index].getText());
    		}

        	availableMarketTypesPanel.add(availableMarketTypesCheckBoxes[index]);
        }
        
		messagePanel.setVisible(false);
		availableMarketTypesPanel.setVisible(true);
		
		/**
		 * revalidate the panel
		 */
		availableMarketTypesPanel.revalidate();
		availableMarketTypesPanel.repaint();
		
        return(availableMarketTypesPanel);
	}
	
	/**
	 * Create information text pane
	 * @return
	 */
	private JComponent createInformationTextPane()
	{
        String text="<html><body><table><tr><td>"+
		"<font color=red><b>Uncheck the market type(s) that you are not interested in subscribing to.<br>"+
		"At least one market type should be selected."+
		"</b></font></td></tr></table><body></html>";

        JPanel panel=new JPanel(new FlowLayout());
        
        informationTextPane=new JTextPane();
        informationTextPane.setContentType("text/html");
		informationTextPane.setEditable(false);
		informationTextPane.setBackground(Color.white);
		informationTextPane.setBorder(BorderFactory.createLineBorder(Color.black));
		informationTextPane.setText(text);
		
		panel.add(informationTextPane);
		
		JPanel buttonPanel=new JPanel();
		commandCheckAll.addActionListener(this);
		buttonPanel.add(commandCheckAll);
        panel.add(new JLabel(""));
		commandUnCheckAll.addActionListener(this);
		buttonPanel.add(commandUnCheckAll);
        panel.add(new JLabel(""));
        udsCheckbox.addActionListener(this);
        buttonPanel.add(udsCheckbox);
		panel.add(buttonPanel);
		
        return(panel);
	}

	/**
	 * Change all available markets either checked or unchecked
	 * @param enable
	 */
	private void changeAllAvailableMarketTypes(boolean checked)
	{
        for(int index=0;index<availableMarketTypesCheckBoxes.length;index++)
        {
    		if(logger.isTraceEnabled())
        	{
        		logger.trace("### Check/Uncheck : "+availableMarketTypesCheckBoxes[index].getText()+(checked?"-Enable":"-Disable"));
        	}
        	availableMarketTypesCheckBoxes[index].setSelected(checked);
        }
        
		return;
	}
	
	/**
	 * On selection change from the multicast group table,
	 * re-create the available market types
	 */
    public void valueChanged(ListSelectionEvent event)
    {
        if (event.getValueIsAdjusting()) 
        {
            return;
        }
        
		MulticastChannelPairInfo channelPair=multicastGroupConfigTable.getSelectedChannelPairInfo();

        createMarketTypesCheckBoxes();
        if (channelPair!=null && channelPair.getGroupName().contains(MDFClientConfigurator.MULTICAST_GROUP_NAME_OPTIONS_KEY_WORD))
        {
           this.udsCheckbox.setEnabled(true);
        }
        else
        {
           this.udsCheckbox.setSelected(false);
           this.udsCheckbox.setEnabled(false);
        }
    }
    
    /**
     * Get the selected interested market types
     * @return
     */
	public List<MarketType> getInterestedMarketTypes()
	{
		List<MarketType> selectedMarketTypes=new ArrayList<MarketType>();
		
		if(!this.availableMarketTypesPanel.isEnabled())
		{
			logger.warn("No market types selected.");
			return(selectedMarketTypes);
		}

	    if(availableMarketTypesCheckBoxes==null)
	    {
			logger.warn("No market types checkboxes selected.");
			return(selectedMarketTypes);
	    }
	    
	    for(int index=0;index<availableMarketTypesCheckBoxes.length;index++)
	    {
	    	if(!availableMarketTypesCheckBoxes[index].isSelected())
	    	{
	    		if(logger.isTraceEnabled())
	    		{
		    		logger.trace("Not Selected : "+this.availableMarketTypes[index]);
	    		}
	    		continue;
	    	}
	    	
    		selectedMarketTypes.add(availableMarketTypes[index]);
	    }
		
		if(logger.isTraceEnabled())
		{
			logger.trace("Selected Market Types : "+selectedMarketTypes.toString());
		}

		return(selectedMarketTypes);
	}
	
	public boolean isInterestedInUDS()
	{
	   return this.udsCheckbox.isSelected();
	}
    
	public void disableUDSCheckbox()
	{
	   this.udsCheckbox.setSelected(false);
	   this.udsCheckbox.setEnabled(false);
	}
	
    public void actionPerformed(ActionEvent e) 
    {
    	if(e.getSource()==this.commandCheckAll)
    	{
    		changeAllAvailableMarketTypes(true);
    	}
    	else if(e.getSource()==this.commandUnCheckAll)
    	{
    		changeAllAvailableMarketTypes(false);
    	}
    	
    	return;
    }
    
    /**
     * Sorter
     * @author Adam Athimuthu
     */
    protected class MarketTypeSorter implements Comparator<MarketType>
    {
    	public MarketTypeSorter()
    	{
    	}
    	
        public int compare(MarketType first, MarketType second)
        {
            int result=0;
            
            int firstCode=Integer.valueOf(first.getMarketTypeCode());
            int secondCode=Integer.valueOf(second.getMarketTypeCode());
            
        	result=firstCode-secondCode;
        	
            return(result);
        }
    }
}

