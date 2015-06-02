package com.theice.mdf.client.gui;

import javax.swing.*;
import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 17, 2007
 * Time: 3:47:16 PM
 *
 */
public class MDFMessagePanel extends JPanel
{
    JTextArea _text=new JTextArea();

    /**
     * Message panel
     * @param message
     */
    public MDFMessagePanel(String message)
    {
        super();

        _text.setText(message);
        _text.setEditable(false);

        _text.setColumns(80);
        _text.setLineWrap(true);
        _text.setRows(30);
        _text.setWrapStyleWord(true);

        JScrollPane pane = new JScrollPane(_text,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(pane, BorderLayout.CENTER);
    }

}

