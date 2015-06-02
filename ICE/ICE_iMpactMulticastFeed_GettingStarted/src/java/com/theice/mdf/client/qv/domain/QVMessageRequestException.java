package com.theice.mdf.client.qv.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMessageRequestException extends Exception
{
	public QVMessageRequestException()
	{
		super();
	}
	
	public QVMessageRequestException(String exception)
	{
		super(exception);
	}
	
	public QVMessageRequestException(String message, Throwable cause) 
	{
        super(message, cause);
    }

}
