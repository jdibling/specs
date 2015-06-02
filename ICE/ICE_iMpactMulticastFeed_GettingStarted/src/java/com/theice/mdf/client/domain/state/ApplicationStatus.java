package com.theice.mdf.client.domain.state;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * ApplicationStatus
 *
 * Keeps track of the application's overall status. If we detect out of sequence conditions,
 * the application initiates a re-initialization
 * 
 * @author Adam Athimuthu
 */
public enum ApplicationStatus 
{
	NORMAL("Normal"),
	SHUTDOWN("ShutDown"),
	SEQUENCEPROBLEM("SequenceProblem"),
	NETWORKINACTIVITY("NetworkInactivity"),
	NETWORKERROR("NetworkError");
	
	protected String _name="";
	
	ApplicationStatus(String name)
	{
		this._name=name;
	}
	
	public String toString()
	{
		return(_name);
	}
}

