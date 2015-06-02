/**
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights
 * Reserved.
 **/
package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

/**
 * Domain class for QV Marker Index Price Response
 * 
 * @author qwang
 * @version     %I%, %G%
 * Created: Apr 17, 2007 9:31:53 AM
 *
 *
 */
public class MarkerIndexPriceMessage extends MDSequencedMessageWithMarketID
{
   private static final short MESSAGE_LENGTH = 63;

   public char _shortName[] = new char[30];
   private long _price;
   private long _publishedDateTime;
   private char _evaluationDate[] = new char[10];
   
   public MarkerIndexPriceMessage()
   {
      MessageType = RawMessageFactory.QVMarkerIndexPriceResponseType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }
   
   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.putLong(_price);
         for( int i=0; i<_shortName.length  ; i++ )
         {
            SerializedContent.put( (byte)_shortName[i] );
         }
         SerializedContent.putLong(_publishedDateTime);
         for( int i=0; i<_evaluationDate.length  ; i++ )
         {
            SerializedContent.put( (byte)_evaluationDate[i] );
         }
         SerializedContent.rewind();
         
         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
      }

      return SerializedContent.array();
   }

   public void deserializeContent( ByteBuffer inboundcontent )
   {
      _price = inboundcontent.getLong();
      for( int i=0; i<_shortName.length  ; i++ )
      {
         _shortName[i] = (char)inboundcontent.get();
      }
      _publishedDateTime = inboundcontent.getLong();
      for( int i=0; i<_evaluationDate.length  ; i++ )
      {
         _evaluationDate[i] = (char)inboundcontent.get();
      }
   }

   public String getShortLogStr()
   {
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());

         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _price );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(_shortName) );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _publishedDateTime );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(_evaluationDate ));
         strBuf.append( LOG_FLD_DELIMITER );
         
         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("Price=");
      str.append( _price );     
      str.append( "|");
      str.append("ShortName=");
      str.append( MessageUtil.toString(_shortName) );     
      str.append( "|");
      str.append("PublishedDateTime=");
      str.append( _publishedDateTime );     
      str.append( "|");
      str.append("EvaluationDate=");
      str.append( MessageUtil.toString(_evaluationDate ));     
      str.append( "|");
      return str.toString();
   }

   /**
    * @return the price
    */
   public long getPrice()
   {
      return _price;
   }

   /**
    * @param price the price to set
    */
   public void setPrice(long price)
   {
      _price = price;
   }

   /**
    * @return the publishedDateTime
    */
   public long getPublishedDateTime()
   {
      return _publishedDateTime;
   }

   /**
    * @param publishedDateTime the publishedDateTime to set
    */
   public void setPublishedDateTime(long publishedDateTime)
   {
      _publishedDateTime = publishedDateTime;
   }

   /**
    * @return the shortName
    */
   public char[] getShortName()
   {
      return _shortName;
   }

   /**
    * @param shortName the shortName to set
    */
   public void setShortName(char[] shortName)
   {
      _shortName = shortName;
   }

   /**
    * @return the priceDate
    */
   public char[] getEvaluationDate()
   {
      return _evaluationDate;
   }

   /**
    * @param priceDate the priceDate to set
    */
   public void setEvaluationDate(char[] priceDate)
   {
      _evaluationDate = priceDate;
   }

}
