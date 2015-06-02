package com.theice.mdf.client.exception;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class ProcessingException extends Exception
{
	public ProcessingException()
	{
		super();
	}
	
	public ProcessingException(String exception)
	{
		super(exception);
	}
	
	public ProcessingException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
