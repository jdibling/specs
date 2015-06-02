package com.theice.mdf.client.gui.panel;

import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.gui.table.MarketTableModel;
import com.theice.mdf.client.gui.table.MarketsTable;
import com.theice.mdf.client.domain.MarketType;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 7, 2007
 * Time: 4:02:37 PM
 *
 */
public class MarketTypesPanel extends JPanel implements ListSelectionListener
{
    /**
     * List of Market Type objects
     */
    private JList _list;

    /**
     * List model contains the list of MarketType objects
     */
    private DefaultListModel _listModel;

    /**
     * Singleton instance
     */
    private static MarketTypesPanel _instance=new MarketTypesPanel();

    public static MarketTypesPanel getInstance()
    {
        return(_instance);
    }

    /**
     * Markets JList Constructor
     */
    private MarketTypesPanel()
    {
        super(new BorderLayout());

        _listModel=new DefaultListModel();

        _list = new JList(_listModel);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _list.setSelectedIndex(0);
        _list.addListSelectionListener(this);
        _list.setFont(new Font("Arial",Font.PLAIN,12));

        add(new JScrollPane(_list), BorderLayout.CENTER);
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
        MDFClientContext context=MDFClientContext.getInstance();
        
        if (e.getValueIsAdjusting())
        {
            return;
        }

        JList theList = (JList)e.getSource();

        DefaultListModel listModel=(DefaultListModel) theList.getModel();

        if(!theList.isSelectionEmpty())
        {
            int index=theList.getSelectedIndex();

            MarketType marketType=(MarketType) listModel.getElementAt(index);
            
            String marketTypeCode=marketType.getMarketTypeCode();

            /**
             * Find the model and activate it in the Table pane
             */
            MarketTableModel model=context.getMarketTableModels().get(new Short(marketTypeCode));

            if(model!=null)
            {
                context.getMarketDisplayPane().setMarketsTableTab(new MarketsTable(model));
            }
            else
            {
                System.err.println("FATAL ERROR. Model not found for "+marketTypeCode);
            }
        }
    }
}

