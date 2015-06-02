package com.theice.mdf.client.exception;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class InvalidStateException extends Exception
{
	public InvalidStateException()
	{
		super();
	}
	
	public InvalidStateException(String exception)
	{
		super(exception);
	}
	
	public InvalidStateException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
