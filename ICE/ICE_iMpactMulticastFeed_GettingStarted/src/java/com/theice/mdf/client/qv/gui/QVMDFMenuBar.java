package com.theice.mdf.client.qv.gui;

import com.theice.mdf.client.gui.MDFMenuBar;
import com.theice.mdf.client.qv.listeners.QVMenuListener;

import javax.swing.*;
import java.util.ArrayList;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMDFMenuBar extends MDFMenuBar
{
    private static QVMDFMenuBar _instance = new QVMDFMenuBar();

    public static final String MENU_QV="QV";

    public static final String MENUITEM_MARKER_INDEX_PRICE="Marker Index Price";
    public static final String MENUITEM_ENDOFDAY_MARKET_MESSAGE="End of Day Market Message";

    public static QVMDFMenuBar getInstance()
    {
        return _instance;
    }

    protected QVMDFMenuBar()
    {
    	super();
    }

    /**
     * create application menu items
     * override
     * @return
     */
    protected JMenu[] createAppMenuItems()
    {
    	ArrayList<JMenu> list=new ArrayList<JMenu>();
    	
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu(MENU_QV);

        menuItem = new JMenuItem(MENUITEM_MARKER_INDEX_PRICE);
        menuItem.addActionListener(new QVMenuListener());
        menu.add(menuItem);
        
        menuItem = new JMenuItem(MENUITEM_ENDOFDAY_MARKET_MESSAGE);
        menuItem.addActionListener(new QVMenuListener());
        menu.add(menuItem);
        
        list.add(menu);

    	return(list.toArray(new JMenu[0]));
    }

}

