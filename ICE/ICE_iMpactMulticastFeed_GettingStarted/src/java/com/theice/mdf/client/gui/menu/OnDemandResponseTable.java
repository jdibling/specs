package com.theice.mdf.client.gui.menu;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.theice.mdf.client.gui.table.MDFGenericTable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class OnDemandResponseTable extends MDFGenericTable 
{
    public OnDemandResponseTable(TableModel model)
    {
        super(model);

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setRowHeight(22);
        setBackground(Color.DARK_GRAY);
        setForeground(Color.ORANGE);
        setFont(new Font("Courier",Font.PLAIN,12));
        setGridColor(Color.BLACK);
    }

}
