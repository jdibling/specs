package com.theice.mdf.client.exception;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class ConfigurationException extends Exception
{
	public ConfigurationException()
	{
		super();
	}
	
	public ConfigurationException(String exception)
	{
		super(exception);
	}
	
	public ConfigurationException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
