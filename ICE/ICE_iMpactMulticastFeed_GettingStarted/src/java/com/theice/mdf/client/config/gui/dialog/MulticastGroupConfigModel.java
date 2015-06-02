package com.theice.mdf.client.config.gui.dialog;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientEnvConfigRepository;
import com.theice.mdf.client.config.domain.MulticastChannelPairInfo;
import com.theice.mdf.client.config.domain.MulticastGroupInfo;
import com.theice.mdf.client.domain.book.MulticastChannelContext;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastGroupConfigModel extends AbstractTableModel
{
    private static Logger logger=Logger.getLogger(MulticastGroupConfigModel.class.getName());

    private Vector<MulticastChannelPairInfo> multicastChannelPairs=new Vector<MulticastChannelPairInfo>();

    public MulticastGroupConfigModel(String environment,MulticastChannelContext context)
    {
        super();
        
        init();
        
        refresh(environment,context);
    }
    
    public synchronized void refresh(String environment,MulticastChannelContext context)
    {
		if(environment==null || context==null)
    	{
			System.err.println("Environment or Context is null while trying to refresh the model.");
    		return;
    	}
		
		emptyData();
		
		/**
		 * Iterate through the client configuration
		 * For each group, return the connectivity information for the given context
		 * 
		 * Certain contexts may be null...In the case of Options multicast groups, we don't have full order depth
		 * Those null values are dropped out
		 */
		MDFClientEnvConfigRepository config=MDFClientConfigurator.getInstance().getConfigRepository().getConfig(environment);	
		
		Collection<MulticastGroupInfo> groupInfoCollection=config.getMulticastGroupsMap().values();
		
		for(Iterator<MulticastGroupInfo> it=groupInfoCollection.iterator();it.hasNext();)
		{
			MulticastGroupInfo groupInfo=it.next();
			
			MulticastChannelPairInfo channelPairInfo=groupInfo.getMulticastChannelPairInfo(context);
			
			if(channelPairInfo!=null)
			{
				multicastChannelPairs.add(channelPairInfo);
			}
			else
			{
				logger.warn("Channel Pair Info is null for : "+context+" (fullOrderDepth may be absent for options groups.)");
			}
		}

    	fireTableDataChanged();

    	return;
    }
    
    private void emptyData()
    {
		int size=multicastChannelPairs.size();
		
		if(size>0)
		{
    		multicastChannelPairs.clear();
    		fireTableRowsDeleted(0, size-1);
		}
    }

    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(MulticastGroupConfigColumn.columns.length);
    }

    /**
     * Get Row Count
     * @return
     */
    public synchronized int getRowCount()
    {
        return(multicastChannelPairs.size());
    }

    public String getColumnName(int col)
    {
        return(MulticastGroupConfigColumn.columns[col].getName()); 
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return(MulticastGroupConfigColumn.columns[columnIndex].getColumnClass());
    }

    public void init()
    {
    }

    public void cleanup()
    {
    }
    
    /**
     * Get Value At
     * @param row
     * @param col
     * @return
     */
    public synchronized Object getValueAt(int row, int col)
    {
        Object value=null;
        MulticastChannelPairInfo data=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        try
        {
            data=(MulticastChannelPairInfo) multicastChannelPairs.get(row);
        }
        catch(Exception e)
        {
            System.err.println("Data not found for row : "+row);
        }

        if(data!=null)
        {
            switch(col)
            {
                case MulticastGroupConfigColumn.COLID_GROUPNAME:
                    value=data.getGroupName();
                    break;
                case MulticastGroupConfigColumn.COLID_CONTEXT:
                    value=data.getMulticastChannelContext();
                    break;
                case MulticastGroupConfigColumn.COLID_SNAPSHOT_ENDPOINT:
                    value=data.getSnapshotEndPoint().getDisplayable();
                    break;
                case MulticastGroupConfigColumn.COLID_LIVE_ENDPOINT:
                    value=data.getLiveEndPoint().getDisplayable();
                    break;
                default:
                    value="???";
                    break;
            }
        }

        return(value);
    }

    /**
     * Sort on a given column ascending or descending
     * @param column
     * @param isAscending
     */
    public synchronized void sort(int column, boolean isAscending)
    {
    	this.fireTableChanged(new TableModelEvent(this));
    }
    
    public MulticastChannelPairInfo getMulticastChannelPairInfo(int row)
    {
    	return((MulticastChannelPairInfo) multicastChannelPairs.get(row));
    }

    /**
     * make the cells non-editable
     * @param row
     * @param col
     * @return
     */
    public boolean isCellEditable(int row, int col)
    {
        return false; 
    }

}

