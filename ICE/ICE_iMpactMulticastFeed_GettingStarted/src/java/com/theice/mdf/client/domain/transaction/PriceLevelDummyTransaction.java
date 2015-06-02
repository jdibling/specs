package com.theice.mdf.client.domain.transaction;

/**
 * This class is a dummy wrapper class for Transaction.
 * In order to leverage on existing crossed book detection, we need to wrap price level change message
 * to mimic a Transaction. 
 * When used in Price Level context, all crossed book triggering/clearing event will be outside of a bundle.
 * 
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 */
public class PriceLevelDummyTransaction implements Transaction
{
   private TransactionType transactionType=TransactionType.PRICELEVELDUMMY;
   private int _marketId=0;
   private String _causedBy=""; 
   
   public PriceLevelDummyTransaction(int marketId, String causedBy)
   {
      this._marketId=marketId;
      this._causedBy=causedBy;
   }
   
   public long getBundleSequenceNumber()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getMarketId()
   {
      // TODO Auto-generated method stub
      return this._marketId;
   }
   
   public TransactionType getTransactionType()
   {
      // TODO Auto-generated method stub
      return this.transactionType;
   }
   
   public String toString()
   {
      return this._causedBy;
   }

}
