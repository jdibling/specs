package com.theice.mdf.client.config.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;
import com.theice.mdf.client.gui.table.MDFStandardTable;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastGroupConfigTable extends JPanel
{
    protected MDFStandardTable _table=null;
    protected MulticastGroupConfigModel _model=null;
    
    private MulticastGroupConfigTable()
    {
    }

    public MulticastGroupConfigTable(MulticastGroupConfigModel model,MouseListener mouseListener)
    {
        super();

        setLayout(new BorderLayout());
		setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.blue, 1),"Multicast Groups",
        		TitledBorder.LEADING,TitledBorder.TOP,new Font("Arial",Font.BOLD,12)));

        _model=model;
        _table=new MDFStandardTable(_model);
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _table.addMouseListener(mouseListener);
        
        JTableHeader header=_table.getTableHeader();
        header.setUpdateTableInRealTime(true);
        setColumnWidths();
        
        JScrollPane scrollPane=new JScrollPane(_table);
        add(scrollPane);
    }

    public void setSelectionListener(ListSelectionListener listener)
    {
        _table.getSelectionModel().addListSelectionListener(listener);
    }

    public MDFStandardTable getTable()
    {
        return(_table);
    }
    
    public void init()
    {
    }

    public void cleanup()
    {
    	_model.cleanup();
    }
    
    private void setColumnWidths()
    {
        for(int index=0;index<MulticastGroupConfigColumn.columns.length;index++)
        {
            _table.getColumnModel().getColumn(index).setPreferredWidth(
            		MulticastGroupConfigColumn.columns[index].getWdith());
        }

        return;
    }
    
    public MulticastChannelPairInfo getSelectedChannelPairInfo()
    {
        int selectedRow=_table.getSelectedRow();

    	if(selectedRow<0)
    	{
    		return(null);
    	}
    	
    	int modelIndex=_table.convertRowIndexToModel(selectedRow);
    	
    	MulticastChannelPairInfo info=_model.getMulticastChannelPairInfo(modelIndex);
    	
    	return(info);
    }

	public void autoSelectFirstRow()
	{
		int rows=_table.getModel().getRowCount();
		
		if(rows>0)
		{
			_table.setRowSelectionInterval(0,0);
		}
		
		return;
	}
	
}

