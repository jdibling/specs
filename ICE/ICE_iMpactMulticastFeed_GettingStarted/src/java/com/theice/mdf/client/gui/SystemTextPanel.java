package com.theice.mdf.client.gui;

import javax.swing.*;
import java.awt.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class SystemTextPanel extends JPanel
{
    JTextArea _text=new JTextArea();

    public SystemTextPanel(String message)
    {
       this(message, 80, 20);
    }
    
    public SystemTextPanel(String message, int columns, int rows)
    {
        super();

        _text.setText(message);
        _text.setEditable(false);

        _text.setColumns(columns);
        _text.setLineWrap(true);
        _text.setRows(rows);
        _text.setWrapStyleWord(true);

        JScrollPane pane = new JScrollPane(_text,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(pane, BorderLayout.CENTER);
    }

}

