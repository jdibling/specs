package com.theice.mdf.message.response;

import com.theice.mdf.message.MDMessage;

/**
 * Created by IntelliJ IDEA.
 * User: dchen
 * Date: Jan 11, 2007
 * Time: 4:53:35 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Response extends MDMessage
{
	public int RequestSeqID;

   public String toString()
   {
      StringBuffer str = new StringBuffer();

      str.append(super.toString());
      str.append("RequestSeqID=");
		str.append(RequestSeqID);
		str.append( "|");

      return str.toString();
   }
}
