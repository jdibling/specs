package com.theice.mdf.client.config.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientEnvConfigRepository;

import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class EnvironmentsListPanel extends JPanel implements ListSelectionListener
{
    /**
     * List of Environments
     */
    private JList _list;

    /**
     * List model contains the list of environment objects
     */
    private DefaultListModel _listModel;

    /**
     * Singleton instance
     */
    private static EnvironmentsListPanel _instance=new EnvironmentsListPanel();

    public static EnvironmentsListPanel getInstance()
    {
        return(_instance);
    }

    /**
     * Markets JList Constructor
     */
    private EnvironmentsListPanel()
    {
        super(new BorderLayout());

        _listModel=new DefaultListModel();

        _list = new JList(_listModel);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _list.setSelectedIndex(0);
        _list.addListSelectionListener(this);
        _list.setFont(new Font("Arial",Font.PLAIN,12));

        add(new JScrollPane(_list), BorderLayout.CENTER);
        
        init();
    }
    
    private void init()
    {
    	String[] environments=MDFClientConfigurator.getInstance().getConfigRepository().getEnvironments();
    	
    	for(int index=0;index<environments.length;index++)
    	{
        	_listModel.addElement(environments[index]);
    	}
    }

    /**
     * get model
     * @return
     */
    public DefaultListModel getModel()
    {
        return((DefaultListModel)_list.getModel());
    }
    
    public JList getList()
    {
    	return(_list);
    }

    /**
     * handle selection events
     * Show all the markets for the specific market type that has been selected
     * @param e
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting())
        {
            return;
        }

        JList theList = (JList)e.getSource();

        DefaultListModel listModel=(DefaultListModel) theList.getModel();

        if(!theList.isSelectionEmpty())
        {
            int index=theList.getSelectedIndex();

            String envName=(String) listModel.getElementAt(index);
            
            System.out.println("Environment Selected : "+envName);
            
            MDFClientEnvConfigRepository config=MDFClientConfigurator.getInstance().getConfigRepository().getConfig(envName);

            System.out.println("Environment Properties : "+config.toString());
            
//            MarketTableModel model=context.getMarketTableModels().get(new Short(marketTypeCode));
//
//            if(model!=null)
//            {
//                context.getMarketDisplayPane().setMarketsTableTab(new MarketsTable(model));
//            }
//            else
//            {
//                System.err.println("FATAL ERROR. Model not found for "+marketTypeCode);
//            }
        }
    }
}

