package com.theice.mdf.client.domain.state;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Used by the application monitor if a sequence problem is detected. 
 * 
 * @see AppMonitor, simpleClient.properties (sequence.problem.action)
 * 
 * @author Adam Athimuthu
 */
public enum SequenceProblemAction 
{
	SHUTDOWN("shutdown"),
	RESTART("restart");
	
	protected String _name="";
	
	SequenceProblemAction(String name)
	{
		this._name=name;
	}
	
	public String getAction()
	{
		return(_name);
	}
	
	public String toString()
	{
		return(_name);
	}
}

