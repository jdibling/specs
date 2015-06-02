package com.theice.mdf.client.domain.state;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MarketStreamState 
{
	/**
	 * Snapshot for the market not loaded
	 * Messages have to be queued
	 */
    public static final int STATEID_NOTREADY=0;

    /**
     * Snapshot messages starting to load
     * We will be done loading once we get the MarketSnapshot and zero or more SnapshotOrders for that market
     */
    public static final int STATEID_SNAPSHOTLOADING=1;

    /**
	 * Ready for processing (normal flow)
	 * Can proceed with processing from the queue (after dropping the packets earlier than the snapshot)
	 */
    public static final int STATEID_READY=2;
    
	/**
	 * out of sequence has been detected leading to a WAIT state
	 * The messages have to be queued until the missing packet(s) have been received
	 */
    public static final int STATEID_OUTOFSEQUENCE=3;
    
    protected int _id;
    protected String _name;

    protected MarketStreamState(int id, String name)
    {
    	_id=id;
    	_name=name;
    }
    
    public int getId()
    {
    	return(_id);
    }
    
    public String getName()
    {
    	return(_name);
    }

    public static final MarketStreamState NOTREADY=new MarketStreamState(STATEID_NOTREADY,"Not Ready");
    public static final MarketStreamState SNAPSHOTLOADING=new MarketStreamState(STATEID_SNAPSHOTLOADING,"Snapshot Loading");
    public static final MarketStreamState READY=new MarketStreamState(STATEID_READY,"Ready");
    public static final MarketStreamState OUTOFSEQUENCE=new MarketStreamState(STATEID_OUTOFSEQUENCE,"Out of Sequence");
    
    public String toString()
    {
    	StringBuffer buf=new StringBuffer();
    	buf.append("["+_name+"]");
    	return(buf.toString());
    }

    public static MarketStreamState[] states=
        {
    			NOTREADY,
                SNAPSHOTLOADING,
                READY,
                OUTOFSEQUENCE
        };
}
