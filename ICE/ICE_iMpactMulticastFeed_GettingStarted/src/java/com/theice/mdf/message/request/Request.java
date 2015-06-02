package com.theice.mdf.message.request;

import com.theice.mdf.message.MDMessage;

/**
 * Created by IntelliJ IDEA.
 * User: dchen
 * Date: Dec 21, 2006
 * Time: 1:50:59 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Request extends MDMessage
{
	public int RequestSeqID;

   public int getMarketID()
   {
      return -1;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();

      str.append(super.toString());
      str.append("RequestSeqID=");
		str.append(RequestSeqID);
		str.append( "|");

      return str.toString();
   }

   public String getShortLogStr()
   {
      // there aren't many requests, just use the long str
      return toString();
   }

   public void setMarketID(int MarketID)
   {
   }   
}
