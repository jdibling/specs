package com.theice.mdf.client.gui.panel;

import com.theice.mdf.client.gui.MDFGUIClient;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClientContext;
import com.theice.mdf.client.domain.MDRawMessageBuffer;
import com.theice.mdf.message.MDMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.net.URL;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Log Message Panel, used for displaying raw messages. Only the latest x number of messages will be
 * shown at any given time. The max number is previously configured internally.
 * 
 * @author Adam Athimuthu
 * Date: Aug 21, 2007
 * Time: 10:59:14 AM
 */
public class LogMessagePanel extends JPanel implements ActionListener
{
    /**
     * Toolbar commands
     */
    private static final String CLEAR_LOG = "ClearLog";
    private static final String SHOW_LOG = "ShowLog";

    private JButton _buttonShow=null;
    private JButton _buttonClear=null;
    
    private MDFClientContext clientContext=null;

    /**
     * MDFMessage List
     */
    private JList _list = null;

    public LogMessagePanel()
    {
        super(new BorderLayout());

        /**
         * LogMessagePanel is available only in GUI mode. So the context has to be a GUI context
         */
        clientContext=(MDFClientContext) AppManager.getAppContext();

        DefaultListModel listModel = clientContext.getMessageModel();
        
        _list = new JList(listModel);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        _list.setFont(new Font("Courier New",Font.PLAIN,14));

        add(new JScrollPane(_list), BorderLayout.CENTER);
        add(createToolBar(), BorderLayout.PAGE_START);
    }

    /**
     * Create tool bar
     * @return
     */
    private JToolBar createToolBar()
    {
        JToolBar toolbar=new JToolBar("Message Log");

        _buttonClear=new JButton();
        String imgLocation = "images/Remove24.gif";
        URL imageURL = MDFGUIClient.class.getResource(imgLocation);

        if(imageURL!=null)
        {
            _buttonClear.setIcon(new ImageIcon(imageURL,"Clear Log"));
        }
        else
        {
            _buttonClear.setText(CLEAR_LOG);
        }

        _buttonClear.setActionCommand(CLEAR_LOG);
        _buttonClear.setToolTipText("Clear log messages");
        _buttonClear.addActionListener(this);
        toolbar.add(_buttonClear);

        _buttonShow=new JButton();
        imgLocation = "images/Refresh24.gif";
        imageURL = MDFGUIClient.class.getResource(imgLocation);

        if(imageURL!=null)
        {
            _buttonShow.setIcon(new ImageIcon(imageURL,"Show Log"));
        }
        else
        {
            _buttonShow.setText(SHOW_LOG);
        }

        _buttonShow.setActionCommand(SHOW_LOG);
        String tooltip="Show the latest "+MDRawMessageBuffer.getInstance().getMaxMessageCount()+" messages";
        _buttonShow.setToolTipText(tooltip);
        _buttonShow.addActionListener(this);
        toolbar.add(_buttonShow);

        return(toolbar);
    }

    /**
     * Action listener events, for handling the toolbar button actions
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if (CLEAR_LOG.equals(cmd))
        {
        	/**
        	 * TODO use clearLogMessages() on the MDFAppContext interface
        	 */
            DefaultListModel messageListModel=clientContext.getMessageModel();

            synchronized(messageListModel)
            {
                messageListModel.clear();
            }
        }
        else if (SHOW_LOG.equals(cmd))
        {
            /**
             * Dump the message log
             * TODO 1. get the context from the app manager, and then
             * TODO 2. clearLogMessages()
             * TODO 3. logEssential(message)
             */
            DefaultListModel messageListModel=clientContext.getMessageModel();

            synchronized(messageListModel)
            {
                messageListModel.clear();

                java.util.List<MDMessage> messageList= MDRawMessageBuffer.getInstance().getLogMessageList();

                synchronized(messageList)
                {
                    for(Iterator<MDMessage> it=messageList.iterator();it.hasNext();)
                    {
                        MDMessage message=it.next();
                        messageListModel.addElement(message.toString());
                    }
                }
            }
        }
    }

}

