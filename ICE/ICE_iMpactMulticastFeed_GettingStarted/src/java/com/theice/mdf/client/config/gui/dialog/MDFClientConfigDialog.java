package com.theice.mdf.client.config.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfigRepository;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.config.domain.MDFClientEnvConfigRepository;
import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;
import com.theice.mdf.client.config.domain.MulticastGroupDefinition;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.domain.book.MulticastChannelContext;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * MDF Config Dialog
 * 
 * @author Adam Athimuthu
 */
public class MDFClientConfigDialog extends JDialog implements ActionListener,MouseListener
{
    private static Logger logger=Logger.getLogger(MDFClientConfigDialog.class.getName());

    protected MDFClientConfigPanel mdfClientConfigPanel=null;
	protected MulticastConnectivityConfigPanel multicastConnectivityPanel=null;
	
	protected MulticastChannelPairInfo channelPairInfo=null;
	
    public static final String actionOk="Ok";
	protected JButton commandOk=null;

	public MDFClientConfigDialog(JFrame frame, String title)
    {
        super(frame,true);
        
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        setTitle(title);
        setPreferredSize(new Dimension(800,700));
        
        setupPanels();
        
        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }
	
	protected void setupPanels()
	{
		mdfClientConfigPanel=new MDFClientConfigPanel(this);
        add(mdfClientConfigPanel,BorderLayout.NORTH);

        String environment=mdfClientConfigPanel.getSelectedEnvironment();
        MulticastChannelContext context=mdfClientConfigPanel.getSelectedMulticastContext();
        
        multicastConnectivityPanel=new MulticastConnectivityConfigPanel(environment,context,this,this);
        add(multicastConnectivityPanel,BorderLayout.CENTER);
        
        add(createCommandPanel(),BorderLayout.SOUTH);
        
        return;
	}
	
	private JPanel createCommandPanel()
	{
		JPanel panel=new JPanel(new GridLayout(1,3));
		
        commandOk=new JButton(actionOk);
		commandOk.setEnabled(true);
        commandOk.addActionListener(this);
        panel.add(new JLabel(""));
        panel.add(commandOk);
        panel.add(new JLabel(""));
        
        return(panel);
	}

	/**
	 * Window closing
	 */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            dispose();
        }
    }
    
    private void triggerGroupModelUpdate()
    {
    	String environment=mdfClientConfigPanel.getSelectedEnvironment();
    	MulticastChannelContext context=mdfClientConfigPanel.getSelectedMulticastContext();
    	
		multicastConnectivityPanel.triggerModelUpdate(environment, context);
		
    	return;
    }
    
    public MulticastChannelPairInfo getSelectedMulticastChannelPairInfo()
    {
    	return(channelPairInfo);
    }
    
    /**
     * Choose the configuration
     */
    private void chooseConfiguration()
    {
		channelPairInfo=multicastConnectivityPanel.getSelectedChannelPairInfo();
		
		if(channelPairInfo!=null)
		{
    		String env=mdfClientConfigPanel.getSelectedEnvironment();
        	MulticastChannelContext context=mdfClientConfigPanel.getSelectedMulticastContext();
        	List<MarketType> interestedMarketTypes=multicastConnectivityPanel.getInterestedMarketTypes();
        	boolean isInterestedInUDS=multicastConnectivityPanel.isInterestedInUDS();
        	
        	/**
        	 * Choose the env specific repository and create a client configuration
        	 * with the TCP information applicable for that environment
        	 * 
        	 * Next, the channel pair information chosen by the user/env, is used to initialize the multicast group
        	 * 
        	 * Finally, the interested market types applicable for the selected multicast group is used to finalize the config
        	 * 
        	 * This is the selected configuration used by the application
        	 */
        	MDFClientConfigRepository repository=MDFClientConfigurator.getInstance().getConfigRepository();
        	MDFClientEnvConfigRepository envRepository=repository.getConfig(env);
        	
        	String groupName=channelPairInfo.getGroupName();
        	MulticastGroupDefinition multicastGroupDefinition=repository.getMulticastGroupDefinition(groupName);
        	HashMap<String, MulticastGroupDefinition> mcGroupDefinitionMap = new HashMap<String, MulticastGroupDefinition>();
        	mcGroupDefinitionMap.put(groupName, multicastGroupDefinition);

    		MDFClientConfiguration mdfClientConfig=new MDFClientConfiguration(env,mcGroupDefinitionMap,envRepository.getTcpInfo());
    		com.theice.mdf.client.config.domain.TCPConnectionInfo tcpInfo = mdfClientConfig.getTcpInfo();
    		//user the username and password from the config dialog window
    		tcpInfo.setUserName(multicastConnectivityPanel.userName.getText());
    		tcpInfo.setPassword(multicastConnectivityPanel.password.getText());
    		
    		HashMap<String, MulticastChannelPairInfo> channelPairInfoMap = new HashMap<String, MulticastChannelPairInfo>();
    		channelPairInfoMap.put(groupName, channelPairInfo);
    		mdfClientConfig.setMulticastChannelPairInfoMap(channelPairInfoMap);
    		
    		mdfClientConfig.setMDFClientRuntimeParameters(MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters());
    		HashMap<String, List<MarketType>> interestedMarketTypesMap = new HashMap<String, List<MarketType>>();
    		interestedMarketTypesMap.put(groupName, interestedMarketTypes);
    		mdfClientConfig.setInterestedMarketTypesMap(interestedMarketTypesMap);
    		mdfClientConfig.setIsInterestedInUDS(isInterestedInUDS);

    		if(logger.isTraceEnabled())
    		{
        		logger.trace("### Choosing Configuration ### "+mdfClientConfig.toString());
        		logger.trace("### Interested Market Types : "+multicastConnectivityPanel.getInterestedMarketTypes().toString());
    		}

    		MDFClientConfigurator.getInstance().setCurrentConfiguration(mdfClientConfig);
		}
		
    	return;
    }

    /**
     * Action Listener Methods
     */
    public void actionPerformed(ActionEvent e) 
    {
    	if(e.getSource()==mdfClientConfigPanel.getEnvironmentsComboBox())
    	{
    		triggerGroupModelUpdate();
        	return;
    	}
    	
    	if(e.getSource()==mdfClientConfigPanel.getFullOrderDepthRadioButton() ||
    		e.getSource()==mdfClientConfigPanel.getPriceLevelRadioButton())
    	{
    		triggerGroupModelUpdate();
        	return;
    	}
    	
    	/**
    	 * When the user clicks OK, then we transfer the configuration that the user has chosen,
    	 * to the current config object
    	 */
    	if(e.getSource()==this.commandOk)
    	{
    		this.chooseConfiguration();
    		this.dispose();
    	}

		return;
    }
    
    /**
     * Mouse Listener Methods
     * Handle double click on the getMulticastGroupConfigTable
     * @param e
     */
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount() == 2)
        {
        	JTable target=(JTable) e.getSource();
        	
            int row=target.getSelectedRow();

        	if(e.getSource()!=this.multicastConnectivityPanel.getMulticastGroupConfigTable())
        	{
            	System.err.println("Non standard table");
        		return;
        	}
        	
        	if(row<0)
        	{
        		return;
        	}
        	
        	int modelIndex=target.convertRowIndexToModel(row);
        	
        	MulticastGroupConfigModel model=(MulticastGroupConfigModel) target.getModel();
        	
        	MulticastChannelPairInfo info=model.getMulticastChannelPairInfo(modelIndex);
        	
        	chooseConfiguration();
        	
    		this.dispose();
        }

        return;
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseWheelMoved(MouseWheelEvent e){}

    public void mouseDragged(MouseEvent e){}

    public void mouseMoved(MouseEvent e){}

}

