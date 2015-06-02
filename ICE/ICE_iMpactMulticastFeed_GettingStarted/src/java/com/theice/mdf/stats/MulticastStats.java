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
public interface MulticastStats
{
   public static final String STAT_TYPE_FULL_ORDER_DEPTH =   "FULL_ORDER_DEPTH";
   public static final String STAT_TYPE_PRICE_LEVEL =        "PRICE_LEVEL_TOP5";
   public static final String STAT_TYPE_OPTION_TOP_OF_BOOK = "OPTION_TOP_OF_BK";
   public static final String STAT_TYPE_SNAPSHOT_PL5 =       "SNAPSHOT_PL_TOP5";
   public static final String STAT_TYPE_SNAPSHOT_OPTION =    "SNAPSHOT_PL_OPTN";
   public static final String STAT_TYPE_SNAPSHOT_FOD =       "SNAPSHOT_FULL_OD";

   public static final String LOG_ENTRY_DELIMITER = "|";
   
   /**
    * @return the messageBlockSequenceNumber
    */
   public int getMessageBlockSequenceNumber();
   /**
    * @param messageBlockSequenceNumber the messageBlockSequenceNumber to set
    */
   public void setMessageBlockSequenceNumber(int messageBlockSequenceNumber);
   /**
    * @return the statType
    */
   public String getStatType();
   /**
    * @param statType the statType to set
    */
   public void setStatType(String statType);
   /**
    * @return the messageBlockSendEpochTimeInMillis
    */
   public long getMessageBlockSendEpochTimeInMillis();
   /**
    * @param messageBlockSendEpochTimeInMillis the messageBlockSendEpochTimeInMillis to set
    */
   public void setMessageBlockSendEpochTimeInMillis(long messageBlockSendEpochTimeInMillis);  
   /**
    * @return the messageBlockBytes
    */
   public int getMessageBlockBytes();
   /**
    * @param messageBlockBytes the messageBlockBytes to set
    */
   public void setMessageBlockBytes(int messageBlockBytes);
   /**
    * @return the messageBlockSendBeginTime
    */
   public long getMessageBlockSendBeginTime();
   /**
    * @param messageBlockSendBeginTime the messageBlockSendBeginTime to set
    */
   public void setMessageBlockSendBeginTime(long messageBlockSendBeginTime);
   /**
    * @return the messageBlockSendCompleteTime
    */
   public long getMessageBlockSendCompleteTime();
   /**
    * @param messageBlockSendCompleteTime the messageBlockSendCompleteTime to set
    */
   public void setMessageBlockSendCompleteTime(long messageBlockSendCompleteTime);
   /**
    * @return
    */
   public MulticastStats copy();
   /**
    * @return the groupAddress
    */
   public String getGroupAddress();
   /**
    * @param groupAddress the groupAddress to set
    */
   public void setGroupAddress(String groupAddress);
   /**
    * @return the sessionId
    */
   public short getSessionId();
   /**
    * @param sessionId the sessionId to set
    */
   public void setSessionId(short sessionId);
   /**
    * @return the portNumber
    */
   public int getPortNumber();
   /**
    * @param portNumber the portNumber to set
    */
   public void setPortNumber(int portNumber);
  
   /**
    * @param numberOfMessageInBlock
    */
   public void setNumberOfMessagesInBlock(int numberOfMessageInBlock);
}
