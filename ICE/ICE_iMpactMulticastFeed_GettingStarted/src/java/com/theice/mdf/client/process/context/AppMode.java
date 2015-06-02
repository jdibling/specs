package com.theice.mdf.client.process.context;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The mode of the multicast client (GUI or CommandLine)
 * 
 * @see MDFAppContext, MDFClientContext, MDFCommandLineContext
 * @author Adam Athimuthu
 */
public enum AppMode 
{
	GUI("GUI"),
	CommandLine("CommandLine"),
	Unknown("Unknown");
	
	protected String mode="";
	
	AppMode(String mode)
	{
		this.mode=mode;
	}
	
	public String getMode()
	{
		return(this.mode);
	}

	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append("[").append(mode).append("]");
		return(buffer.toString());
	}
}

