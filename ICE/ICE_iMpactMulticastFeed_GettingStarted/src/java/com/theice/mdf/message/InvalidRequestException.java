package com.theice.mdf.message;

/**
 * <code>InvalidRequestException</code> is used when request is inavlid.
 *
 * @author David Chen
 * @version %I%, %G%
 * @since 12/12/2006
 */
public class InvalidRequestException extends Exception
{
   /**
    * 
    */
   private static final long serialVersionUID = 2242792740286183917L;

   public InvalidRequestException(String errMsg)
   {
      super(errMsg);
   }
}
