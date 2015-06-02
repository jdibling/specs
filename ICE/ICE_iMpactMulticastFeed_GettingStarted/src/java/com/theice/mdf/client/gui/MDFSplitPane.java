package com.theice.mdf.client.gui;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import javax.swing.JSplitPane;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.gui.table.MarketTableModel;
import com.theice.mdf.client.gui.panel.MarketTypesPanel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Jul 31, 2007
 * Time: 4:31:11 PM
 */
public class MDFSplitPane
{
    /**
     * Split Pane components
     */
    private JSplitPane splitPane;

    /**
     * Constructor
     */
    public MDFSplitPane()
    {
        init();
    }

    /**
     * initialize the split pane
     */
    private void init()
    {
        MDFClientContext clientContext=(MDFClientContext) AppManager.getAppContext();
        
        MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();

        /**
         * Initialize the market types list from the configuration file
         * Also create a tab model entry for displaying the table. Init with an empty hash map
         */
        MarketTypesPanel marketTypesPanel=clientContext.getMarketTypesPanel();
        
        Map<Short, MarketTableModel> marketTableModels=clientContext.getMarketTableModels();

        /**
         * Get the interested market types from config and build the model
         */
        List<String> interestedGroupNames = configuration.getInterestedMulticastGroupNames();
        List<MarketType> marketTypes=configuration.getInterestedMarketTypes(interestedGroupNames.get(0));
        
        for(int index=0;index<marketTypes.size();index++)
        {
            MarketType marketType=marketTypes.get(index);

            marketTypesPanel.getModel().addElement(marketType);

            String val=(String) marketType.getMarketTypeCode();

            /**
             * Create market table model for each market type
             */
            Short marketTypeCode=new Short(val);
            marketTableModels.put(marketTypeCode,new MarketTableModel(marketTypeCode));
        }

        /**
         * Panel for displaying the market table
         */
        MarketTabbedDisplayPane marketDisplayPane=clientContext.getMarketDisplayPane();
        marketDisplayPane.clearTabs();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, marketTypesPanel, marketDisplayPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        Dimension minimumSize = new Dimension(250, 100);
        marketTypesPanel.setMinimumSize(minimumSize);

        splitPane.setPreferredSize(new Dimension(1000, 600));

        return;
    }

    /**
     * cleanup
     */
    public void cleanup()
    {
    }

    /**
     * Split Pane
     * @return
     */
    public JSplitPane getSplitPane()
    {
        return(splitPane);
    }

}

