package com.theice.mdf.client.config.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfigRepository;
import com.theice.mdf.client.config.domain.MDFClientEnvConfigRepository;
import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.domain.book.MulticastChannelContext;
import com.theice.mdf.client.gui.table.MDFStandardTable;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastConnectivityConfigPanel extends JPanel
{
	protected MulticastGroupConfigModel multicastGroupConfigModel=null;
	protected MulticastGroupConfigTable multicastGroupConfigTable=null;
	
	protected InterestedMarketTypesPanel interestedMarketTypesPanel=null;
	
	protected JLabel textTcpAddress=new JLabel();
	protected JLabel textUserName=new JLabel();
	protected JLabel textPassword=new JLabel();
	protected JTextField userName=new JTextField();
	protected JTextField password=new JTextField();
//	protected JTextField textMulticastNetworkInterface=new JTextField(5);
//	protected JTextField textSequenceProblemAction=new JTextField(5);
//	protected JTextField textMulticastInactivityThreshold=new JTextField(5);
	
	private static final Color bgColorField=new Color(2,2,82);
	private static final Color bgColorPanel=new Color(204,204,204);
	private static final Dimension fieldSize=new Dimension(120,20);
	
	private GridBagConstraints gbc=null;
	
	private JLabel[] _fields=new JLabel[]
	            			                    {
													textTcpAddress,
													textUserName,
													textPassword
//													textMulticastNetworkInterface,
//													textSequenceProblemAction,
//													textMulticastInactivityThreshold
	            			                    };
	
	public MulticastConnectivityConfigPanel(String environment,MulticastChannelContext context,
			ActionListener actionListener,MouseListener mouseListener)
	{
    	setBorder(new TitledBorder(BorderFactory.createEmptyBorder(),"Multicast Connectivity Config",
        		TitledBorder.LEADING,TitledBorder.TOP,MDFUtil.fontArialBold12));
        setFont(MDFUtil.fontArialPlain12);
        setLayout(new BorderLayout());

        /**
         * Panel containing the TCP connectivity for the selected environment
         * Table containing the multicast groups for the selected environment
         */
        add(createConnectionConfigPanel(environment),BorderLayout.NORTH);
        
        multicastGroupConfigModel=new MulticastGroupConfigModel(environment,context);
        multicastGroupConfigTable=new MulticastGroupConfigTable(multicastGroupConfigModel,mouseListener);
        
		multicastGroupConfigTable.autoSelectFirstRow();

//        JScrollPane scrollPane=new JScrollPane(multicastGroupConfigTable);
//        scrollPane.getHorizontalScrollBar().setEnabled(true);
  
        add(multicastGroupConfigTable,BorderLayout.CENTER);
        
        /**
         * Panel containing the available market types that match the selected multicast group
         */
        interestedMarketTypesPanel=new InterestedMarketTypesPanel(actionListener,multicastGroupConfigTable);
        
        if (!multicastGroupConfigModel.getMulticastChannelPairInfo(0).getGroupName().contains(MDFClientConfigurator.MULTICAST_GROUP_NAME_OPTIONS_KEY_WORD))
        {
           interestedMarketTypesPanel.disableUDSCheckbox();
        }
        
        JScrollPane marketTypesScrollPane=new JScrollPane(interestedMarketTypesPanel);

        add(marketTypesScrollPane,BorderLayout.SOUTH);
        
	}
	
	private void refreshContents(String environment)
	{
		MDFClientConfigRepository repository=MDFClientConfigurator.getInstance().getConfigRepository();
		
		MDFClientEnvConfigRepository envConfig=repository.getConfig(environment);

		textTcpAddress.setText(envConfig.getTcpInfo().getEndPointInfo().getDisplayable());
		userName.setPreferredSize(fieldSize);
		password.setPreferredSize(fieldSize);
		userName.setText(envConfig.getTcpInfo().getUserName());
		password.setText(envConfig.getTcpInfo().getPassword());

		/*
		String networkInterface=repository.getMDFClientRuntimeParameters().getMulticastNetworkInterface();
		
		if(networkInterface!=null)
		{
			textMulticastNetworkInterface.setText(networkInterface);
		}
		else
		{
			textMulticastNetworkInterface.setText("<default>");
		}
		
		textSequenceProblemAction.setText(repository.getMDFClientRuntimeParameters().getSequenceProblemAction().getAction());
		textMulticastInactivityThreshold.setText(Integer.toString(repository.getMDFClientRuntimeParameters().getMulticastInactivityThreshold()));
		*/

		return;
	}
	
	public void triggerModelUpdate(String environment,MulticastChannelContext context)
	{
		if(environment==null || context==null)
    	{
			System.err.println("Environment or Context is null while trying to refresh the model.");
    		return;
    	}
		
		refreshContents(environment);
		
		this.multicastGroupConfigModel.refresh(environment, context);
		this.multicastGroupConfigTable.autoSelectFirstRow();
		return;
	}
	
	private JComponent createConnectionConfigPanel(String environment)
    {
    	JPanel panel=new JPanel(new GridBagLayout());

        gbc=new GridBagConstraints(0,1,1,1,2,2,
        		GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,4),1,1);
	        
		refreshContents(environment);

    	for(int index=0;index<_fields.length;index++)
    	{
//    		_fields[index].setEditable(false);
    		_fields[index].setFont(MDFUtil.fontArialPlain12);
    		_fields[index].setBackground(bgColorField);
    		_fields[index].setForeground(Color.black);
    	}

    	int row=0;
        addGridComponent(0, row, 4, 4, panel, createLabel("TCP Info : "));
        addGridComponent(1, row, 8, 8, panel, textTcpAddress);
        addGridComponent(2, row, 4, 4, panel, createLabel("UserName : "));
        addGridComponent(3, row, 8, 8, panel, userName);
        addGridComponent(4, row, 4, 4, panel, createLabel("Password : "));
        addGridComponent(5, row, 8, 8, panel, password);

        /*
		row++;
        addGridComponent(0, row, 4, 4, panel, createLabel("Network Interface : "));
        addGridComponent(1, row, 8, 8, panel, textMulticastNetworkInterface);
        addGridComponent(2, row, 4, 4, panel, createLabel("SeqProblem Action : "));
        addGridComponent(3, row, 8, 8, panel, textSequenceProblemAction);
        addGridComponent(4, row, 4, 4, panel, createLabel("InactivityThreshold : "));
        addGridComponent(5, row, 8, 8, panel, textMulticastInactivityThreshold);
        */

		JPanel masterPanel=new JPanel(new BorderLayout());
		masterPanel.add(panel,BorderLayout.CENTER);
		masterPanel.add(new JLabel(" "),BorderLayout.SOUTH);
		masterPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"TCP/Parameters",
        		TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));
		
		panel.setBackground(bgColorPanel);
		masterPanel.setBackground(bgColorPanel);

        return(new JScrollPane(masterPanel));
    }
    
    private JLabel createLabel(String text)
    {
    	JLabel label=new JLabel(text);
    	label.setFont(MDFUtil.fontArialPlain12);
    	return(label);
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

	public MulticastChannelPairInfo getSelectedChannelPairInfo()
	{
		return(multicastGroupConfigTable.getSelectedChannelPairInfo());
	}
	
	/**
	 * get the selected interested market types
	 * @return MarketType[]
	 */
	public List<MarketType> getInterestedMarketTypes()
	{
		return(interestedMarketTypesPanel.getInterestedMarketTypes());
	}
	
    public MDFStandardTable getMulticastGroupConfigTable()
    {
        return(this.multicastGroupConfigTable.getTable());
    }
    
    public boolean isInterestedInUDS()
    {
       return interestedMarketTypesPanel.isInterestedInUDS();
    }
    
}

