package com.theice.mdf.client.gui;

import com.theice.mdf.client.gui.table.MarketsTable;

import java.awt.*;
import javax.swing.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * A tabbed display pane used to display all the markets for a given market type.
 * 
 * @author Adam Athimuthu
 * Date: Aug 2, 2007
 * Time: 11:43:31 AM
 *
 */
public class MarketTabbedDisplayPane extends JPanel
{
    private static MarketTabbedDisplayPane _instance = new MarketTabbedDisplayPane();
    
    private JTabbedPane tabbedPane=null;

    /**
     * Constructor
     */
    public MarketTabbedDisplayPane()
    {
        try
        {
            tabbedPane=new JTabbedPane();

            setLayout(new BorderLayout());
            add(tabbedPane);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * add the given market table
     * @param marketsTable
     */
    public void setMarketsTableTab(MarketsTable marketsTable)
    {
        tabbedPane.removeAll();
        tabbedPane.addTab("Markets (Double-click on any market to view the book)", marketsTable);
        tabbedPane.setRequestFocusEnabled(false);
        return;
    }

    /**
     * clear Tabs
     */
    public void clearTabs()
    {
        tabbedPane.removeAll();
        tabbedPane.addTab("No Selection",new JLabel("No Market Types Selected."));
    }

}

