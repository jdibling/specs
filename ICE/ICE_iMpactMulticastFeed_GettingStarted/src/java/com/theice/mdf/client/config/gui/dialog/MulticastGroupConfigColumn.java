package com.theice.mdf.client.config.gui.dialog;

import com.theice.mdf.client.gui.table.AbstractAppTableColumn;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MulticastGroupConfigColumn extends AbstractAppTableColumn
{
    public static final int COLID_GROUPNAME=0;
    public static final int COLID_CONTEXT=1;
    public static final int COLID_SNAPSHOT_ENDPOINT=2;
    public static final int COLID_LIVE_ENDPOINT=3;

    private MulticastGroupConfigColumn(int id, String name, int width, Class columnClass)
    {
        super(id,name,width,columnClass);
    }

    private MulticastGroupConfigColumn(int id, String name, int width)
    {
        super(id,name,width,String.class);
    }

    public static MulticastGroupConfigColumn GROUPNAME=new MulticastGroupConfigColumn(COLID_GROUPNAME,"GroupName",200);
    public static MulticastGroupConfigColumn CONTEXT=new MulticastGroupConfigColumn(COLID_CONTEXT,"Context",100);
    public static MulticastGroupConfigColumn SNAPSHOTENDPOINT=new MulticastGroupConfigColumn(COLID_SNAPSHOT_ENDPOINT,"Snapshot Multicast Group",200);
    public static MulticastGroupConfigColumn LIVEENDPOINT=new MulticastGroupConfigColumn(COLID_LIVE_ENDPOINT,"Live Multicast Group",200);

    public static MulticastGroupConfigColumn[] columns=
        {
    			GROUPNAME,
                CONTEXT,
                SNAPSHOTENDPOINT,
                LIVEENDPOINT
        };

}

