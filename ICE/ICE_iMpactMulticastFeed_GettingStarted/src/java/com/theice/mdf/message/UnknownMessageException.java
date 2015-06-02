package com.theice.mdf.message;

/**
 * <code>UnknownMessageException</code> is used when message is inavlid.
 *
 * @author David Chen
 * @version %I%, %G%
 * @since 12/12/2006
 */

public class UnknownMessageException extends Exception
{
   /**
    * 
    */
   private static final long serialVersionUID = -5978308002997554193L;

   public UnknownMessageException(String errMsg)
   {
      super(errMsg);
   }
}
