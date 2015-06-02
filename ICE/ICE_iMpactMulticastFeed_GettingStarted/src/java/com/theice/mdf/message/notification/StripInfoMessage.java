package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;
import com.theice.mdf.message.*;

/**
 * StripInfoMessage.java
 */

public class StripInfoMessage extends MDSequencedMessage
{
   private static final short MESSAGE_LENGTH = 87;

   public short StripID;
   public char[] StripType = new char[20];
   public short BeginYear;
   public short BeginMonth;
   public short BeginDay;
   public short EndYear;
   public short EndMonth;
   public short EndDay;
   public char[] StripName = new char[50];
      
   public StripInfoMessage()
   {
      MessageType = RawMessageFactory.StripInfoMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public int getMarketID()
   {
      return -1;
   }
   
   public void setMarketID(int marketID)
   {
   }

   public synchronized byte[]serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );
         MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;

         serializeHeader();
         SerializedContent.putShort( StripID );
         for( int i=0; i<StripType.length  ; i++ )
         {
            SerializedContent.put( (byte)StripType[i] );
         }

         SerializedContent.putShort(BeginYear);
         SerializedContent.putShort(BeginMonth);
         SerializedContent.putShort(BeginDay);
         SerializedContent.putShort(EndYear);
         SerializedContent.putShort(EndMonth);
         SerializedContent.putShort(EndDay);
         for( int i=0; i<StripName.length  ; i++ )
         {
            SerializedContent.put( (byte)StripName[i] );
         }
         
         SerializedContent.rewind();
         
         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
      }

      return SerializedContent.array();
   }

   public String getShortLogStr()
   {
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());

         strBuf.append( StripID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(StripType) );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( BeginYear );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( BeginMonth );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( BeginDay );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EndYear );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EndMonth );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( EndDay );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(StripName) );
         strBuf.append( LOG_FLD_DELIMITER );
        
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   public void deserializeContent(ByteBuffer inboundcontent)
   {
      StripID = inboundcontent.getShort();
      for( int i=0; i<StripType.length  ; i++ )
      {
         StripType[i] = (char)inboundcontent.get();
      }
      BeginYear = inboundcontent.getShort();
      BeginMonth = inboundcontent.getShort();
      BeginDay = inboundcontent.getShort();
      EndYear = inboundcontent.getShort();
      EndMonth = inboundcontent.getShort();
      EndDay = inboundcontent.getShort();
      for( int i=0; i<StripName.length  ; i++ )
      {
         StripName[i] = (char)inboundcontent.get();
      }
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("StripID=");
      str.append(StripID);
      str.append( "|");
      str.append("StripType=");
      str.append(MessageUtil.toString(StripType));
      str.append( "|");
      str.append("BeginYear=");
      str.append(BeginYear);
      str.append( "|");
      str.append("BeginMonth=");
      str.append(BeginMonth);
      str.append( "|");
      str.append("BeginDay=");
      str.append(BeginDay);
      str.append( "|");
      str.append("EndYear=");
      str.append(EndYear);
      str.append( "|");
      str.append("EndMonth=");
      str.append(EndMonth);
      str.append( "|");
      str.append("EndDay=");
      str.append(EndDay);
      str.append( "|");
      str.append("StripName=");
      str.append(MessageUtil.toString(StripName));
      str.append( "|");
     
      return str.toString();
   }
   
}

   