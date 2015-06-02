/**
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights
 * Reserved.
 **/
package com.theice.mdf.message.response.qv;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.response.Response;

/**
 * Domain class for QV Option Open Interest Response
 * 
 * @author qwang
 * @version     %I%, %G%
 * Created: Apr 17, 2007 9:31:53 AM
 *
 *
 */
public class QVOptionOpenInterestResponse extends Response
{
   private static final short MESSAGE_LENGTH = 88;
   
   private int _marketID;
   private short _numberOfMarkets;
   public char _contractSymbol[] = new char[35];
   private int _openInterest;
   private char _optionType;
   private long _strikePrice;
   private char _publishedDate[] = new char[10];
   private char _openInterestDate[] = new char[10];
   private char _expiryDate[] = new char[7];

   public QVOptionOpenInterestResponse()
   {
      MessageType = RawMessageFactory.OptionOpenInterestMessageType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   
   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if( SerializedContent == null )
      {
         SerializedContent = ByteBuffer.allocate( MESSAGE_LENGTH );

         serializeHeader();
         SerializedContent.putInt( RequestSeqID );
         SerializedContent.putShort(_numberOfMarkets);
         SerializedContent.putInt(_marketID);         
         for( int i=0; i<_contractSymbol.length  ; i++ )
         {
            SerializedContent.put( (byte)_contractSymbol[i] );
         }
         SerializedContent.putInt(_openInterest);         
         SerializedContent.put((byte)_optionType);
         SerializedContent.putLong(_strikePrice);
         for( int i=0; i<_publishedDate.length  ; i++ )
         {
            SerializedContent.put( (byte)_publishedDate[i] );
         }
         for( int i=0; i<_openInterestDate.length  ; i++ )
         {
            SerializedContent.put( (byte)_openInterestDate[i] );
         }         
         for( int i=0; i<_expiryDate.length  ; i++ )
         {
            SerializedContent.put( (byte)_expiryDate[i] );
         }         
         SerializedContent.rewind();
         
         if (SHORT_LOG_STR_PRE_ALLOCATED)
         {
            getShortLogStr();
         }
      }

      return SerializedContent.array();
   }

   public void deserialize( ByteBuffer inboundcontent )
   {
      RequestSeqID = inboundcontent.getInt();
      _numberOfMarkets = inboundcontent.getShort();
      _marketID = inboundcontent.getInt();      
      for( int i=0; i<_contractSymbol.length  ; i++ )
      {
         _contractSymbol[i] = (char)inboundcontent.get();
      }
      _openInterest = inboundcontent.getInt();
      _optionType = (char)inboundcontent.get();
      _strikePrice = inboundcontent.getLong();
      for( int i=0; i<_publishedDate.length  ; i++ )
      {
         _publishedDate[i] = (char)inboundcontent.get();
      }
      for( int i=0; i<_openInterestDate.length  ; i++ )
      {
         _openInterestDate[i] = (char)inboundcontent.get();
      }
      for( int i=0; i<_expiryDate.length  ; i++ )
      {
         _expiryDate[i] = (char)inboundcontent.get();
      }
   }

   public String getShortLogStr()
   {
      if (ShortLogStr==null)
      {
         StringBuffer strBuf = new StringBuffer();
         strBuf.append( getLogHeaderShortStr());

         strBuf.append( RequestSeqID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _numberOfMarkets );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _marketID );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(_contractSymbol) );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _openInterest );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _optionType );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( _strikePrice );         
         strBuf.append( LOG_FLD_DELIMITER );             
         strBuf.append( MessageUtil.toString(_publishedDate) );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(_openInterestDate) );
         strBuf.append( LOG_FLD_DELIMITER );
         strBuf.append( MessageUtil.toString(_expiryDate) );
         strBuf.append( LOG_FLD_DELIMITER );         

         ShortLogStr = strBuf.toString();
      }

      return ShortLogStr;
   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();
      
      str.append(super.toString());
      str.append("NumberOfMarkets=");
      str.append( _numberOfMarkets );
      str.append( "|");
      str.append("MarketID=");
      str.append( _marketID );   
      str.append( "|");
      str.append("ContractSymbol=");
      str.append( MessageUtil.toString(_contractSymbol) );     
      str.append( "|");
      str.append("OpenInterest=");
      str.append( _openInterest );        
      str.append( "|");
      str.append("OptionType=");
      str.append( _optionType );        
      str.append( "|");
      str.append("StrikePrice=");
      str.append( _strikePrice );        
      str.append( "|");        
      str.append("PublishedDate=");
      str.append( MessageUtil.toString(_publishedDate) );     
      str.append( "|");
      str.append("OpenInterestDate=");
      str.append( MessageUtil.toString(_openInterestDate) );     
      str.append( "|"); 
      str.append("ExpiryDate=");
      str.append( MessageUtil.toString(_expiryDate) );     
      str.append( "|");        
      return str.toString();
   }   

   public int getMarketID()
   {
      return _marketID;
   }     

   /**
    * @return the optionType
    */
   public char getOptionType()
   {
      return _optionType;
   }

   /**
    * @param optionType the optionType to set
    */
   public void setOptionType(char optionType)
   {
      _optionType = optionType;
   }

   /**
    * @return the strikePrice
    */
   public long getStrikePrice()
   {
      return _strikePrice;
   }

   /**
    * @param strikePrice the strikePrice to set
    */
   public void setStrikePrice(long strikePrice)
   {
      _strikePrice = strikePrice;
   }

   /**
    * @return the contractSymbol
    */
   public char[] getContractSymbol()
   {
      return _contractSymbol;
   }

   /**
    * @param contractSymbol the contractSymbol to set
    */
   public void setContractSymbol(char[] contractSymbol)
   {
      _contractSymbol = contractSymbol;
   }

   /**
    * @param marketID the marketID to set
    */
   public void setMarketID(int marketID)
   {
      _marketID = marketID;
   }
   /**
    * @return the numberOfMarkets
    */
   public short getNumberOfMarkets()
   {
      return _numberOfMarkets;
   }

   /**
    * @param numberOfMarkets the numberOfMarkets to set
    */
   public void setNumberOfMarkets(short numberOfMarkets)
   {
      _numberOfMarkets = numberOfMarkets;
   }

   /**
    * @return the openInterest
    */
   public int getOpenInterest()
   {
      return _openInterest;
   }

   /**
    * @param openInterest the openInterest to set
    */
   public void setOpenInterest(int openInterest)
   {
      _openInterest = openInterest;
   }


   /**
    * @return the expiryDate
    */
   public char[] getExpiryDate()
   {
      return _expiryDate;
   }


   /**
    * @param expiryDate the expiryDate to set
    */
   public void setExpiryDate(char[] expiryDate)
   {
      _expiryDate = expiryDate;
   }


   /**
    * @return the openInterestDate
    */
   public char[] getOpenInterestDate()
   {
      return _openInterestDate;
   }


   /**
    * @param openInterestDate the openInterestDate to set
    */
   public void setOpenInterestDate(char[] openInterestDate)
   {
      _openInterestDate = openInterestDate;
   }


   /**
    * @return the publishedDate
    */
   public char[] getPublishedDate()
   {
      return _publishedDate;
   }


   /**
    * @param publishedDate the publishedDate to set
    */
   public void setPublishedDate(char[] publishedDate)
   {
      _publishedDate = publishedDate;
   }

}
