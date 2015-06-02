package com.theice.mdf.client.exception;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class SequenceException extends Exception
{
	public SequenceException()
	{
		super();
	}
	
	public SequenceException(String exception)
	{
		super(exception);
	}
	
	public SequenceException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
