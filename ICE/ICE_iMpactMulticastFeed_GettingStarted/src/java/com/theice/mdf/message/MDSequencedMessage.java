/*
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights Reserved.
 */
package com.theice.mdf.message;

import java.nio.ByteBuffer;

/**
 * Message that are sequenced. The attribute sequence number is transient because
 * it is not defined in any message in the multicast spec. It is carried by the mulitcast
 * block and derived when it is unmarshalled by the client.
 *
 * @author qwang
 * @version     %I%, %G%
 * Created: Sep 26, 2007 11:03:36 AM
 */
public abstract class MDSequencedMessage extends MDMessage implements HasSequenceNumber
{
   // this is not defined in any message, should not be serialized.
   private int _sequenceNumber= 0;

   public MDSequencedMessage()
   {
      super();
   }

   public MDSequencedMessage(MDSequencedMessage mdMsg)
   {
      super(mdMsg);
      _sequenceNumber = mdMsg.getSequenceNumber();
   }
   
   public void deserialize(ByteBuffer inboundcontent)
   {
      deserializeHeader(inboundcontent);
      deserializeContent(inboundcontent);
   }

   protected void deserializeHeader(ByteBuffer inboundcontent)
   {
      //empty impl, sequence message might/might not have any additional header field, leave it up to the impl but default to empty
   }

   protected abstract void deserializeContent(ByteBuffer inboundcontent);

   /**
    * @see com.theice.mdf.message.HasSequenceNumber#getSequenceNumber()
    */
   public int getSequenceNumber()
   {
      return _sequenceNumber;
   }

   /**
    * @see com.theice.mdf.message.HasSequenceNumber#setSequenceNumber(int)
    */
   public void setSequenceNumber(int sequenceNumber)
   {
      _sequenceNumber = sequenceNumber;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer(super.toString());
      str.append("SequenceNumber=");
      str.append(_sequenceNumber);
      str.append( LOG_FLD_DELIMITER);
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
      _sequenceNumber = Integer.valueOf(fields[5]);
   }

   public String getLogHeaderShortStr()
   {
      StringBuffer str = new StringBuffer(super.getLogHeaderShortStr());
      str.append(_sequenceNumber);
      str.append(LOG_FLD_DELIMITER);
      return str.toString();
   }

   /**
    * @return
    */
   protected int getLastHeaderFieldIndex()
   {
      return 5;
   }
}
