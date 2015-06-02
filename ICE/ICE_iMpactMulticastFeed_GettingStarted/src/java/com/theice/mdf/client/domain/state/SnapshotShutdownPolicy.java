package com.theice.mdf.client.domain.state;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Snapshot Shutdown Policy
 * 
 * if the shutdown policy is based on COUNTER, then we'll rely on the market count
 * otherwise, a TIMER mechanism will be used
 * 
 * @see Snapshot dispatchers for FullOrderDepth, PriceLevel and OptionsTopOfBook
 * 
 * @author Adam Athimuthu
 */
public enum SnapshotShutdownPolicy 
{
	COUNTER("Counter"),
	TIMER("Timer");
	
	protected String _name="";
	
	SnapshotShutdownPolicy(String name)
	{
		this._name=name;
	}
	
	public String toString()
	{
		return(_name);
	}
}

