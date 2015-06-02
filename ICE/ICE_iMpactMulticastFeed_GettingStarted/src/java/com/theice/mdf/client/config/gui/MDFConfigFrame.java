package com.theice.mdf.client.config.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.theice.mdf.client.gui.panel.LogMessagePanel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MDFConfigFrame extends JFrame
{
    public MDFConfigFrame()
	{
		initComponents();
	}

    private void initComponents()
    {
    	EnvironmentsListPanel envPanel=EnvironmentsListPanel.getInstance();
    	
        Vector<String> vector=new Vector<String>();
        vector.add("Group One");
        vector.add("Group Two");
        vector.add("Group Three");
        
        JList list = new JList(vector);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(envPanel);
        JScrollPane listScrollPane = new JScrollPane(list);

        JSplitPane splitPane;
        splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,envPanel,listScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(100);

        Dimension minimumSize = new Dimension(250, 100);
        splitPane.setPreferredSize(new Dimension(1000, 600));
        
        JSplitPane top = splitPane;
        top.setBorder(null);

        /**
         * Log messages panel
         */
        JPanel logMessagePanel=new LogMessagePanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane,BorderLayout.CENTER);

        /**
         * Status bar
         */
//        getContentPane().add(clientContext.getStatusBar(),BorderLayout.SOUTH);

        /**
         * Menu bar
         */
//        setJMenuBar(clientContext.getAppMenuBar());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(splitPane);
        
        return;
    }
    
}
