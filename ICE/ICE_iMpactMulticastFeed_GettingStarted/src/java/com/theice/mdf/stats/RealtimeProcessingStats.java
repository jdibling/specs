/*
 * This material may not be reproduced or redistributed in whole
 * or in part without the express prior written consent of
 * IntercontinentalExchange, Inc.
 *
 * Copyright IntercontinentalExchange, Inc. 2006, All Rights Reserved.
 */
package com.theice.mdf.stats;

/**
 * @author qwang
 * @version %I%, %G% Created: Dec 6, 2007 10:12:29 AM
 * 
 * 
 */
public class RealtimeProcessingStats implements MulticastStats
{
   public static final Object STATS_LOG_HEADER =
      "StatType|GroupAddress|PortNumber|SessionId|BlockSequence|MDEventQueueSize|BookProcessingBeginTime|BookProcessingCompleteTime|MessageQueueSize|SendBeginTime" +
         "|SendCompleteTime|PersistenceQueueSize|PersistenceBeginTime|PersistenceCompleteTime|PersistenceError|CalculateDelta" +
         "|NumberOfMessagesInMDEvent|NumberOfMessageInPLEvent|MessageBlockBytes|SendEpochTimeInMillis|MDEventProcessingDelayTime|NumberOfMessageInBlock|TimeInBufferingSinceLastSent|TotalMessageSizeFromBuffering";
   

   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append(_statType).append("|").append(_groupAddress).append("|").append(_portNumber).append("|").append(_sessionId).append("|");
      builder.append(_messageBlockSequenceNumber).append("|").append(_mdEventQueueSize).append("|").append(_bookProcessingBeginTime).append("|");
      builder.append(_bookProcessingCompleteTime).append("|").append(_messageQueueSize).append("|").append(_messageBlockSendBeginTime).append("|");
      builder.append(_messageBlockSendCompleteTime).append("|").append(_persistenceQueueSize).append("|").append(_messagePersistenceBeginTime).append("|");
      builder.append(_messagePersistenceCompleteTime).append("|").append(_messagePersistenceError?"Y":"N").append("|").append(_calculateDelta?"Y":"N").append("|");
      builder.append(_numberOfMessagesInMDEvent).append("|").append(_numberOfMessagesInPLEvent).append("|").append(_messageBlockBytes).append("|");
      builder.append(_messageBlockSendEpochTimeInMillis).append("|").append(_mdEventProcessingDelayTime).append("|").append(_numberOfMessagesInBlock).append("|");
      builder.append(_timeInBufferingSinceLastSent).append("|").append(_totalMessageSizeFromBuffering);
      
      return builder.toString();
   }

   private String _statType = STAT_TYPE_FULL_ORDER_DEPTH;
   private int _mdEventQueueSize;
   private long _bookProcessingBeginTime;
   private long _bookProcessingCompleteTime;
   private int _messageQueueSize;
   private long _messageBlockSendBeginTime;
   private long _messageBlockSendCompleteTime;
   private int _persistenceQueueSize;
   private long _messagePersistenceBeginTime;
   private long _messagePersistenceCompleteTime;
   private boolean _messagePersistenceError;
   private boolean _calculateDelta;
   private int _numberOfMessagesInMDEvent;
   private int _numberOfMessagesInPLEvent;
   private int _messageBlockSequenceNumber;
   private int _messageBlockBytes;
   private long _messageBlockSendEpochTimeInMillis;
   private String _groupAddress;
   private int _portNumber;
   private short _sessionId;
   private long _mdEventProcessingDelayTime;
   private int _numberOfMessagesInBlock;
   private int _totalMessageSizeFromBuffering;
   private int _timeInBufferingSinceLastSent;

   /**
    * @param stats
    */
   public RealtimeProcessingStats(RealtimeProcessingStats stats)
   {
      _statType = stats._statType;
      _mdEventQueueSize = stats._mdEventQueueSize;
      _bookProcessingBeginTime = stats._bookProcessingBeginTime;
      _bookProcessingCompleteTime = stats._bookProcessingCompleteTime;
      _messageQueueSize = stats._messageQueueSize;
      _messageBlockSendBeginTime = stats._messageBlockSendBeginTime;
      _messageBlockSendCompleteTime = stats._messageBlockSendCompleteTime;
      _persistenceQueueSize = stats._persistenceQueueSize;
      _messagePersistenceBeginTime = stats._messagePersistenceBeginTime;
      _messagePersistenceCompleteTime = stats._messagePersistenceCompleteTime;
      _messagePersistenceError = stats._messagePersistenceError;
      _calculateDelta = stats._calculateDelta;
      _numberOfMessagesInMDEvent = stats._numberOfMessagesInMDEvent;
      _numberOfMessagesInPLEvent = stats._numberOfMessagesInPLEvent;
      _messageBlockSequenceNumber = stats._messageBlockSequenceNumber;  
      _messageBlockBytes = stats._messageBlockBytes;
      _messageBlockSendEpochTimeInMillis = stats._messageBlockSendEpochTimeInMillis;
      _groupAddress = stats._groupAddress;
      _portNumber = stats._portNumber;
      _sessionId = stats._sessionId;
      _mdEventProcessingDelayTime = stats._mdEventProcessingDelayTime;
      _numberOfMessagesInBlock = stats._numberOfMessagesInBlock;
      _totalMessageSizeFromBuffering = stats._totalMessageSizeFromBuffering;
      _timeInBufferingSinceLastSent = stats._timeInBufferingSinceLastSent;
   }
   
   /**
    * @return the groupAddress
    */
   public String getGroupAddress()
   {
      return _groupAddress;
   }
   /**
    * @param groupAddress the groupAddress to set
    */
   public void setGroupAddress(String groupAddress)
   {
      _groupAddress = groupAddress;
   }
   /**
    * @return the portNumber
    */
   public int getPortNumber()
   {
      return _portNumber;
   }
   /**
    * @param portNumber the portNumber to set
    */
   public void setPortNumber(int portNumber)
   {
      _portNumber = portNumber;
   }
   /**
    * @return the sessionId
    */
   public short getSessionId()
   {
      return _sessionId;
   }
   /**
    * @param sessionId the sessionId to set
    */
   public void setSessionId(short sessionId)
   {
      _sessionId = sessionId;
   }
   public RealtimeProcessingStats()
   {}

   /**
    * @return the bookProcessingBeginTime
    */
   public long getBookProcessingBeginTime()
   {
      return _bookProcessingBeginTime;
   }

   /**
    * @param bookProcessingBeginTime
    *           the bookProcessingBeginTime to set
    */
   public void setBookProcessingBeginTime(long bookProcessingBeginTime)
   {
      _bookProcessingBeginTime = bookProcessingBeginTime;
   }

   /**
    * @return the bookProcessingCompleteTime
    */
   public long getBookProcessingCompleteTime()
   {
      return _bookProcessingCompleteTime;
   }

   /**
    * @param bookProcessingCompleteTime
    *           the bookProcessingCompleteTime to set
    */
   public void setBookProcessingCompleteTime(long bookProcessingCompleteTime)
   {
      _bookProcessingCompleteTime = bookProcessingCompleteTime;
   }

   /**
    * @return the mdEventQueueSize
    */
   public int getMdEventQueueSize()
   {
      return _mdEventQueueSize;
   }

   /**
    * @param mdEventQueueSize
    *           the mdEventQueueSize to set
    */
   public void setMdEventQueueSize(int mdEventQueueSize)
   {
      _mdEventQueueSize = mdEventQueueSize;
   }

   /**
    * @return the messageBlockSendBeginTime
    */
   public long getMessageBlockSendBeginTime()
   {
      return _messageBlockSendBeginTime;
   }

   /**
    * @param messageBlockSendBeginTime
    *           the messageBlockSendBeginTime to set
    */
   public void setMessageBlockSendBeginTime(long messageBlockSendBeginTime)
   {
      _messageBlockSendBeginTime = messageBlockSendBeginTime;
   }

   /**
    * @return the messageBlockSendCompleteTime
    */
   public long getMessageBlockSendCompleteTime()
   {
      return _messageBlockSendCompleteTime;
   }

   /**
    * @param messageBlockSendCompleteTime
    *           the messageBlockSendCompleteTime to set
    */
   public void setMessageBlockSendCompleteTime(long messageBlockSendCompleteTime)
   {
      _messageBlockSendCompleteTime = messageBlockSendCompleteTime;
   }

   /**
    * @return the messagePersistenceBeginTime
    */
   public long getMessagePersistenceBeginTime()
   {
      return _messagePersistenceBeginTime;
   }

   /**
    * @param messagePersistenceBeginTime
    *           the messagePersistenceBeginTime to set
    */
   public void setMessagePersistenceBeginTime(long messagePersistenceBeginTime)
   {
      _messagePersistenceBeginTime = messagePersistenceBeginTime;
   }

   /**
    * @return the messagePersistenceCompleteTime
    */
   public long getMessagePersistenceCompleteTime()
   {
      return _messagePersistenceCompleteTime;
   }

   /**
    * @param messagePersistenceCompleteTime
    *           the messagePersistenceCompleteTime to set
    */
   public void setMessagePersistenceCompleteTime(long messagePersistenceCompleteTime)
   {
      _messagePersistenceCompleteTime = messagePersistenceCompleteTime;
   }

   /**
    * @return the bookType
    */
   public String getStatType()
   {
      return _statType;
   }

   /**
    * @param statType
    *           the bookType to set
    */
   public void setStatType(String statType)
   {
      _statType = statType;
   }

   /**
    * @return the messageQueueSize
    */
   public int getMessageQueueSize()
   {
      return _messageQueueSize;
   }

   /**
    * @param messageQueueSize
    *           the messageQueueSize to set
    */
   public void setMessageQueueSize(int messageQueueSize)
   {
      _messageQueueSize = messageQueueSize;
   }

   /**
    * @return the persistenceQueueSize
    */
   public int getPersistenceQueueSize()
   {
      return _persistenceQueueSize;
   }

   /**
    * @param persistenceQueueSize
    *           the persistenceQueueSize to set
    */
   public void setPersistenceQueueSize(int persistenceQueueSize)
   {
      _persistenceQueueSize = persistenceQueueSize;
   }

   /**
    * @return the calculateDelta
    */
   public boolean isCalculateDelta()
   {
      return _calculateDelta;
   }

   /**
    * @param calculateDelta
    *           the calculateDelta to set
    */
   public void setCalculateDelta(boolean calculateDelta)
   {
      _calculateDelta = calculateDelta;
   }

   /**
    * @return the numberOfMessagesInMDEvent
    */
   public int getNumberOfMessagesInMDEvent()
   {
      return _numberOfMessagesInMDEvent;
   }

   /**
    * @param numberOfMessagesInMDEvent
    *           the numberOfMessagesInMDEvent to set
    */
   public void setNumberOfMessagesInMDEvent(int numberOfMessagesInMDEvent)
   {
      _numberOfMessagesInMDEvent = numberOfMessagesInMDEvent;
   }

   /**
    * @return the numberOfMessagesInPLEvent
    */
   public int getNumberOfMessagesInPLEvent()
   {
      return _numberOfMessagesInPLEvent;
   }

   /**
    * @param numberOfMessagesInPLEvent
    *           the numberOfMessagesInPLEvent to set
    */
   public void setNumberOfMessagesInPLEvent(int numberOfMessagesInPLEvent)
   {
      _numberOfMessagesInPLEvent = numberOfMessagesInPLEvent;
   }

   /**
    * @return the messageBlockSequenceNumber
    */
   public int getMessageBlockSequenceNumber()
   {
      return _messageBlockSequenceNumber;
   }

   /**
    * @param messageBlockSequenceNumber
    *           the messageBlockSequenceNumber to set
    */
   public void setMessageBlockSequenceNumber(int messageBlockSequenceNumber)
   {
      _messageBlockSequenceNumber = messageBlockSequenceNumber;
   }

   /**
    * @return the messagePersistenceError
    */
   public boolean isMessagePersistenceError()
   {
      return _messagePersistenceError;
   }

   /**
    * @param messagePersistenceError
    *           the messagePersistenceError to set
    */
   public void setMessagePersistenceError(boolean messagePersistenceError)
   {
      _messagePersistenceError = messagePersistenceError;
   }

   /**
    * @return the messageBlockBytes
    */
   public int getMessageBlockBytes()
   {
      return _messageBlockBytes;
   }
   /**
    * @param messageBlockBytes the messageBlockBytes to set
    */
   public void setMessageBlockBytes(int messageBlockBytes)
   {
      _messageBlockBytes = messageBlockBytes;
   }
   /**
    * @return the messageBlockSendEpochTimeInMillis
    */
   public long getMessageBlockSendEpochTimeInMillis()
   {
      return _messageBlockSendEpochTimeInMillis;
   }
   /**
    * @param messageBlockSendEpochTimeInMillis the messageBlockSendEpochTimeInMillis to set
    */
   public void setMessageBlockSendEpochTimeInMillis(long messageBlockSendEpochTimeInMillis)
   {
      _messageBlockSendEpochTimeInMillis = messageBlockSendEpochTimeInMillis;
   }
   /**
    * @see com.theice.mdf.stats.MulticastStats#copy()
    */
   public MulticastStats copy()
   {
      return new RealtimeProcessingStats(this);
   }

   /**
    * @return the mdEventProcessingDelayTime
    */
   public long getMdEventProcessingDelayTime()
   {
      return _mdEventProcessingDelayTime;
   }

   /**
    * @param mdEventProcessingDelayTime the mdEventProcessingDelayTime to set
    */
   public void setMdEventProcessingDelayTime(long mdEventProcessingDelayTime)
   {
      _mdEventProcessingDelayTime = mdEventProcessingDelayTime;
   }

   /**
    * @return the numberOfMessagesInBlock
    */
   public int getNumberOfMessagesInBlock()
   {
      return _numberOfMessagesInBlock;
   }

   /**
    * @param numberOfMessagesInBlock the numberOfMessagesInBlock to set
    */
   public void setNumberOfMessagesInBlock(int numberOfMessagesInBlock)
   {
      _numberOfMessagesInBlock = numberOfMessagesInBlock;
   }

   public void setTotalMessageSizeFromBuffering(int totalMessageSizeFromBuffering)
   {
      _totalMessageSizeFromBuffering = totalMessageSizeFromBuffering;
   }

   public void setTimeInBufferingSinceLastSent(int timeInBufferingSinceLastSent)
   {
      _timeInBufferingSinceLastSent = timeInBufferingSinceLastSent;
   }
}
