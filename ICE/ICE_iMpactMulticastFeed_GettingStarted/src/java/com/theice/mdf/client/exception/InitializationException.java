package com.theice.mdf.client.exception;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class InitializationException extends Exception
{
	public InitializationException()
	{
		super();
	}
	
	public InitializationException(String exception)
	{
		super(exception);
	}
	
	public InitializationException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
