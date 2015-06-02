package com.theice.mdf.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 17, 2007
 * Time: 3:55:11 PM
 *
 */
public class MDFMessageDialog extends JDialog
{

    private MDFMessagePanel _panel=null;

    public MDFMessageDialog(JFrame frame, String message)
    {
        super(frame,true);

        getContentPane().setLayout(new BorderLayout());
        setTitle("Message");

        Container container=getContentPane();

        _panel=new MDFMessagePanel(message);
        container.add(_panel,BorderLayout.CENTER);

        /**
         * Dispose on close
         */
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }

    /**
     * process window close event
     * @param e
     */
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            dispose();
        }
    }

}


