package com.theice.mdf.client.exception;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class InconsistentStateException extends Exception
{
	public InconsistentStateException()
	{
		super();
	}
	
	public InconsistentStateException(String exception)
	{
		super(exception);
	}
	
	public InconsistentStateException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
