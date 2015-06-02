package com.theice.mdf.client.gui;

import javax.swing.*;

import com.theice.mdf.client.util.MDFUtil;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class SystemTextDialog extends JDialog
{
    private SystemTextPanel _panel=null;

    public SystemTextDialog(JFrame frame, String message, long timeStamp)
    {
        this(frame, message, timeStamp, -1, 0);
    }
    
    public SystemTextDialog(JFrame frame, String message, long timeStamp, int xPos, int yPos)
    {
       super(frame,false);
     
       getContentPane().setLayout(new BorderLayout());
       
       setTitle("System Text - ("+MDFUtil.dateFormat.format(timeStamp)+")");

       Container container=getContentPane();

       if (xPos < 0)
       {
          _panel=new SystemTextPanel(message);
          setLocationRelativeTo(frame);
       }
       else
       {
          _panel=new SystemTextPanel(message, 60, 3);
          setLocation(xPos, yPos);
       }
       
       container.add(_panel,BorderLayout.CENTER);

       setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

       pack();
       
       setVisible(true);
    
    }   
 
    protected void processWindowEvent(WindowEvent e)
    {
        if(e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            dispose();
        }
    }

}


