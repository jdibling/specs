package com.theice.mdf.message;

import java.nio.ByteBuffer;

/**
 * @author qwang
 * @version %I%, %G% Created: Sep 26, 2007 11:03:36 AM
 */
public abstract class MDSequencedMessageWithMarketID extends MDSequencedMessage
{
   private int _marketID = -1;

   public MDSequencedMessageWithMarketID()
   {
      super();
   }

   public MDSequencedMessageWithMarketID(MDSequencedMessageWithMarketID mdMsg)
   {
      super(mdMsg);
      _marketID = mdMsg._marketID;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer(super.toString());
      str.append("MarketID=");
      str.append(_marketID);
      str.append(LOG_FLD_DELIMITER);
      return str.toString();
   }

   protected String[] getFieldTokens(String rawString)
   {
      String[] fields = rawString.split("\\||=");
      return fields;
   }

   protected void populateHeaderFromString(String[] fields)
   {
      MessageBodyLength = Short.valueOf(fields[3]);
      _marketID = Integer.valueOf(fields[7]);
   }

   protected void serializeHeader()
   {
      super.serializeHeader();
      SerializedContent.putInt(_marketID);
   }

   protected void deserializeHeader(ByteBuffer inboundcontent)
   {
      _marketID = inboundcontent.getInt();
   }

   public String getLogHeaderShortStr()
   {
      StringBuffer str = new StringBuffer(super.getLogHeaderShortStr());
      str.append(_marketID);
      str.append(LOG_FLD_DELIMITER);
      return str.toString();
   }

   /**
    * @return the marketID
    */
   public int getMarketID()
   {
      return _marketID;
   }

   /**
    * @param marketID
    *           the marketID to set
    */
   public void setMarketID(int marketID)
   {
      _marketID = marketID;
   }

   /**
    * @return
    */
   protected int getLastHeaderFieldIndex()
   {
      return 7;
   }
}
