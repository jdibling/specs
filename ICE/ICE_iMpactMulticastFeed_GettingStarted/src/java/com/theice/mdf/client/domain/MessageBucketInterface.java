package com.theice.mdf.client.domain;

public interface MessageBucketInterface
{
   public void setBucketNumber(long bucketNumber);
   public void setMessageCount(long count);
   public long getBucketNumber();
   public long getMessageCount();
   public void increaseMessageCount();
   
}
