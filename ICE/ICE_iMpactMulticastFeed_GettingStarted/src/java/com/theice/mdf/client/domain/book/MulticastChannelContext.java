package com.theice.mdf.client.domain.book;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Multicast Channel Context enumeration. These enumerated values correspond to the context that we set in
 * the simpleClient.properties file
 * 
 * The multicast channel context determines the following:
 * 
 * 1. The Book Context associated with the underlying market, triggering also the type of book
 * 2. The type of messages that we listen to in the multicast channel
 * 3. The type of handlers that are used for message processing (factories)
 * 4. The GUI components/tables/tabs that are available (further triggered by the book context)
 * 
 * @author Adam Athimuthu
 */
public enum MulticastChannelContext 
{
	FULLORDERDEPTH("FullOrderDepth"),
	PRICELEVEL("PriceLevel");
	
	protected String _name="";
	
	MulticastChannelContext(String name)
	{
		this._name=name;
	}
	
	public String getName()
	{
		return(_name);
	}
	
	public static MulticastChannelContext getMulticastChannelContextFor(String contextKey)
	{
		MulticastChannelContext context=null;
		
		if(contextKey.compareToIgnoreCase(FULLORDERDEPTH.getName())==0)
		{
			context=FULLORDERDEPTH;
		}
		else if(contextKey.compareToIgnoreCase(PRICELEVEL.getName())==0)
		{
			context=PRICELEVEL;
		}
		
		return(context);
	}
	
	public String toString()
	{
		return(_name);
	}
}
