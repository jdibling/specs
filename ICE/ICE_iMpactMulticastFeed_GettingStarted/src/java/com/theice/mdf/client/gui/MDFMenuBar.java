package com.theice.mdf.client.gui;

import com.theice.mdf.client.gui.listeners.MarketMenuListener;
import com.theice.mdf.client.qv.listeners.QVMenuListener;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 7, 2007
 * Time: 12:56:23 PM
 *
 */
public class MDFMenuBar extends JMenuBar
{
    private static MDFMenuBar _instance = new MDFMenuBar();

    /**
     * Menu Constants
     */
    public static final String MENU_FILE="File";
    public static final String MENU_VIEW="View";
    public static final String MENU_ONDEMAND="On Demand";
    public String MENU_HELP="Help";

    public static final String MENU_ROUTER="Router";

    public static final String MENUITEM_MARKETS="Markets";
    public static final String MENUITEM_LOG="Log";
    public static final String MENUITEM_TABS="Tabs";
    public static final String MENUITEM_HISTORICAL_REPLAY="Historical Replay";
    //public static final String MENUITEM_OPTION_OPEN_INTEREST="Option Open Interest";

    public static final String MENUITEM_LOGIN="Login";

    public static final String MENUITEM_ABOUT="About";

    public static MDFMenuBar getInstance()
    {
        return _instance;
    }

    protected MDFMenuBar()
    {
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File");
        add(menu);

        menuItem = new JMenuItem("Exit",KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        menu.add(menuItem);

        /**
         * Create application specific menu items
         */
        JMenu[] appMenu=createAppMenuItems();
        
        for(int index=0;index<appMenu.length;index++)
        {
        	add(appMenu[index]);
        }
        
        menu = new JMenu("Help");
        add(menu);

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(new MarketMenuListener());
        menu.add(menuItem);
        
    }

    /**
     * create application menu items
     * @return
     */
    protected JMenu[] createAppMenuItems()
    {
    	ArrayList<JMenu> list=new ArrayList<JMenu>();
    	
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu(MENU_ONDEMAND);
        add(menu);

        menuItem = new JMenuItem(MENUITEM_HISTORICAL_REPLAY);
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(new MarketMenuListener());
        menu.add(menuItem);

        //menuItem = new JMenuItem(MENUITEM_OPTION_OPEN_INTEREST);
        //menuItem.addActionListener(new QVMenuListener());
        //menu.add(menuItem);
        
    	return(list.toArray(new JMenu[0]));
    }

    /**
     * create application menu items
     * @return
     */
    protected JMenu[] createEmptyAppMenuItems()
    {
    	ArrayList<JMenu> list=new ArrayList<JMenu>();
    	
        /*
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("View");
        add(menu);

        menuItem = new JMenuItem("Markets");
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(new MarketMenuListener());
        menu.add(menuItem);

        menuItem = new JMenuItem("Log");
        menuItem.addActionListener(new MarketMenuListener());
        menu.add(menuItem);

        menuItem = new JMenuItem("Tabs");
        menuItem.getAccessibleContext().setAccessibleDescription("");
        menuItem.addActionListener(new MarketMenuListener());
        menuItem.setEnabled(false);
        menu.add(menuItem);

        menu = new JMenu("Router");
        menu.setEnabled(false);
        add(menu);

        menuItem = new JMenuItem("Login");
        menuItem.addActionListener(new MarketMenuListener());
        menu.add(menuItem);
        */

    	return(list.toArray(new JMenu[0]));
    }
}

