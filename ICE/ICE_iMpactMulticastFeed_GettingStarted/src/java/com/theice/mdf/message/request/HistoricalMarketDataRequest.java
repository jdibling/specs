package com.theice.mdf.message.request;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

/**
 * Domain class for Historical Market Data Request
 * 
 * @author qwang
 * @version %I%, %G% Created: Apr 17, 2007 1:36:57 PM
 * 
 * 
 */
public class HistoricalMarketDataRequest extends Request
{
   private static final short MESSAGE_LENGTH = 34;
   private char[] _groupAddress = new char[15];
   private short _port;
   private short _sessionId;
   private int _startSequenceNumber;
   private int _endSequenceNumber;

   public HistoricalMarketDataRequest()
   {
      MessageType = RawMessageFactory.HistoricalMarketDataRequestType;
      MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
   }

   public synchronized byte[] serialize()
   {
      // Buffer is pre-serialized, so that serialization occurs only once.
      if (SerializedContent == null)
      {
         SerializedContent = ByteBuffer.allocate(MESSAGE_LENGTH);

         serializeHeader();
         SerializedContent.putInt(RequestSeqID);
         for (int i = 0; i < _groupAddress.length; i++)
         {
            SerializedContent.put((byte) _groupAddress[i]);
         }
         SerializedContent.putShort(_port);
         SerializedContent.putShort(_sessionId);
         SerializedContent.putInt(_startSequenceNumber);
         SerializedContent.putInt(_endSequenceNumber);
         SerializedContent.rewind();
      }

      return SerializedContent.array();
   }

   public void deserialize(ByteBuffer inboundcontent)
   {
      RequestSeqID = inboundcontent.getInt();
      for (int i = 0; i < _groupAddress.length; i++)
      {
         _groupAddress[i] = (char) inboundcontent.get();
      }
      _port = inboundcontent.getShort();
      _sessionId = inboundcontent.getShort();
      _startSequenceNumber = inboundcontent.getInt();
      _endSequenceNumber = inboundcontent.getInt();

   }

   public String toString()
   {
      StringBuffer str = new StringBuffer();

      str.append(super.toString());
      str.append("_groupAddress=");
      str.append(MessageUtil.toString(_groupAddress));
      str.append("|");
      str.append("_port=");
      str.append(_port);
      str.append("|");
      str.append("_sessionId=");
      str.append(_sessionId);
      str.append("|");
      str.append("_startSequenceNumber=");
      str.append(_startSequenceNumber);
      str.append("|");
      str.append("_endSequenceNumber=");
      str.append(_endSequenceNumber);
      str.append("|");
      return str.toString();
   }

   /**
    * @return the mESSAGE_LENGTH
    */
   public static short getMESSAGE_LENGTH()
   {
      return MESSAGE_LENGTH;
   }

   /**
    * @return the endSequenceNumber
    */
   public int getEndSequenceNumber()
   {
      return _endSequenceNumber;
   }

   /**
    * @param endSequenceNumber
    *           the endSequenceNumber to set
    */
   public void setEndSequenceNumber(int endSequenceNumber)
   {
      _endSequenceNumber = endSequenceNumber;
   }

   /**
    * @return the groupAddress
    */
   public char[] getGroupAddress()
   {
      return _groupAddress;
   }

   /**
    * @param groupAddress
    *           the groupAddress to set
    */
   public void setGroupAddress(char[] groupAddress)
   {
      _groupAddress = groupAddress;
   }

   /**
    * @return the port
    */
   public short getPort()
   {
      return _port;
   }

   /**
    * @param port
    *           the port to set
    */
   public void setPort(short port)
   {
      _port = port;
   }

   /**
    * @return the sessionId
    */
   public short getSessionId()
   {
      return _sessionId;
   }

   /**
    * @param sessionId
    *           the sessionId to set
    */
   public void setSessionId(short sessionId)
   {
      _sessionId = sessionId;
   }

   /**
    * @return the startSequenceNumber
    */
   public int getStartSequenceNumber()
   {
      return _startSequenceNumber;
   }

   /**
    * @param startSequenceNumber
    *           the startSequenceNumber to set
    */
   public void setStartSequenceNumber(int startSequenceNumber)
   {
      _startSequenceNumber = startSequenceNumber;
   }
   
   public String getGroupAddressAsString()
   {
      return MessageUtil.toString(_groupAddress);
   }

}
