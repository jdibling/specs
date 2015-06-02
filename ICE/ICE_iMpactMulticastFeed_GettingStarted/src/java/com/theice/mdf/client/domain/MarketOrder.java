package com.theice.mdf.client.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Represents both bids and offers, it's immutable so we can pass it around as a value object
 * without making copies.
 * 
 * @author aathimut
 * @author qwang
 * 
 * Date: Aug 6, 2007
 * Time: 10:11:38 AM
 */
public class MarketOrder implements Comparable<MarketOrder> 
{
	protected int _sequence = -99;
	
	private long _dateTimeAddedToBook;
   private long _dateTimeRemovedFromBook;

    /**
     * Order (Bid/Offer) Details
     */
    private final int _marketID;
    private final long _orderID;
    //_orderSeqID is here for legacy reason, it's not used anymore
    private final int _orderSeqID;
    private final char _side;
    private final long _price;
    private final int _quantity;
    private final char _implied;
    private final char _isRFQ;
    private final long _dateTime;
	public int _sequenceWithinMillis;
    
	/**
	 * bundle sequence number. non-zero if it is a bundled message 
	 */
	private long bundleSequenceNumber=0L;

    /**
     * Was this order received through a snapshot order message?
     */
    private boolean snapshotOrder=false;
    private boolean _reservedFlagOn=false;
    private boolean _isModifyOrder=false;

    /**
     * Construct a regular Market Order
     * @param marketID
     * @param orderID
     * @param orderSeqID
     * @param side
     * @param price
     * @param quantity
     * @param implied
     * @param isRFQ
     * @param dateTime
     */
    public MarketOrder(final int marketID, final long orderID, final int orderSeqID, final char side, final long price, final int quantity, final char implied, final char isRFQ, final long dateTime, boolean reservedFlagOn, int sequenceWithinMillis)
    {
       super();
       _marketID = marketID;
       _orderID = orderID;
       _orderSeqID = orderSeqID;
       _side = side;
       _price = price;
       _quantity = quantity;
       _implied = implied;
       _isRFQ = isRFQ;
       _dateTime = dateTime;
       _reservedFlagOn=reservedFlagOn;
       _sequenceWithinMillis = sequenceWithinMillis;
    }

    /**
    * @return the dateTime
    */
   public long getDateTime()
   {
      return _dateTime;
   }

   /**
    * @return the implied
    */
   public char getImplied()
   {
      return _implied;
   }
   
   /**
    * isImplied?
    * @return
    */
   public boolean isImplied()
   {
	   return(_implied=='Y'?true:false);
   }

   /**
    * @return the isRFQ
    */
   public char getIsRFQ()
   {
      return _isRFQ;
   }

   /**
    * @return the marketID
    */
   public int getMarketID()
   {
      return _marketID;
   }

   /**
    * @return the orderID
    */
   public long getOrderID()
   {
      return _orderID;
   }

   /**
    * @return the orderSeqID
    */
   public int getOrderSeqID()
   {
      return _orderSeqID;
   }

   /**
    * @return the price
    */
   public long getPrice()
   {
      return _price;
   }

   /**
    * @return the quantity
    */
   public int getQuantity()
   {
      return _quantity;
   }

   /**
    * @return the side
    */
   public char getSide()
   {
      return _side;
   }

   /**
     * Set the order collection sequence value
     *
     * @param sequence
     * @level developer
     */
    void setSequence(int sequence)
    {
        _sequence = sequence;
    }

    /**
     * Get the order collection sequence value.
     *
     * @level developer
     */
    public int getSequence()
    {
        return _sequence;
    }

    /**
     * Check if RFQ
     *
     * @return
     */
    public boolean isRfq()
    {
        return (_isRFQ == 'Y');
    }

    /**
     * isBuy
     *
     * @return
     */
    public boolean isBuy()
    {
        return (_side == '1');
    }

	public long getBundleSequenceNumber()
	{
		return(this.bundleSequenceNumber);
	}

    public void setBundleSequenceNumber(long bundleSequenceNumber)
    {
    	this.bundleSequenceNumber=bundleSequenceNumber;
    }

    public boolean isSnapshotOrder()
    {
    	return(this.snapshotOrder);
    }

    public void setAsSnapshotOrder()
    {
    	this.snapshotOrder=true;
    }
    
    public boolean isModifyOrder()
    {
       return this._isModifyOrder;
    }
    
    public void setIsModifyOrder(boolean flag)
    {
       this._isModifyOrder = flag;
    }

    public long getDateTimeAddedToBook()
    {
       return _dateTimeAddedToBook;
    }

    public void setDateTimeAddedToBook(long timeAddedToBook)
    {
       _dateTimeAddedToBook = timeAddedToBook;
    }
    
    public long getDateTimeRemovedFromBook()
    {
       return _dateTimeRemovedFromBook;
    }

    public void setDateTimeRemovedFromBook(long timeRemovedFromBook)
    {
       _dateTimeRemovedFromBook = timeRemovedFromBook;
    }
    
    public boolean isReservedFlagOn()
    {
       return _reservedFlagOn;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
       StringBuffer buf = new StringBuffer("Order=");
       buf.append("[MarketID:" + this._marketID + "]");
       buf.append("[OrderID:" + this._orderID + "]");
       buf.append("[OrderSeqID:" + this._orderSeqID + "]");
       buf.append("[Side:" + this._side+ "]");
       buf.append("[Price:" + this._price + "]");
       buf.append("[Quantity:" + this._quantity + "]");
       buf.append("[Implied:" + this._implied + "]");
       buf.append("[IsRFQ:" + this._isRFQ + "]");
       buf.append("[IsModifyOrder:" + this._isModifyOrder + "]");
       buf.append("[DateTime:" + this._dateTime + "]");
       
       if(this.bundleSequenceNumber!=0)
       {
           buf.append("--[BundleSeqNo:" + this.bundleSequenceNumber+ "]");
       }
       
       if(this.snapshotOrder)
       {
           buf.append("-[SNAPSHOT]");
       }
       
       buf.append("[ReservedFlagOn:" + this._reservedFlagOn + "]");
       
       return (buf.toString());
    }


   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + _marketID;
      result = PRIME * result + (int) (_orderID ^ (_orderID >>> 32));
      return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final MarketOrder other = (MarketOrder) obj;
      if (_marketID != other._marketID)
         return false;
      if (_orderID != other._orderID)
         return false;
      return true;
   }
   
   /**
     * Filter out RFQ orders, so RFQ flag is not used for compare logic
     * Only compare bids to bids and offers to offers. So we only check isBuy on one of the orders
     * 
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(MarketOrder anotherOrder)
   {
      if (anotherOrder == null)
      {
         throw new IllegalArgumentException("Order cannot be null.");
      }

      if (this.equals(anotherOrder))
      {
         return 0;
      }      

      if (this._price == anotherOrder.getPrice())
      {
         if (this._implied == 'Y' && anotherOrder._implied == 'N')
         {
            return 1;
         }
         else if (this._implied == 'N' && anotherOrder._implied == 'Y')
         {
            return -1;
         }
         // if price is equal, then its based on time sent
          if (this._dateTime == anotherOrder.getDateTime())
          {
        	  //if same date time check if 
        	  return (this._sequenceWithinMillis < anotherOrder._sequenceWithinMillis) ?-1:1;

        	 //if timestamp is the same, it doesn't really matter on the client side
             //but let's compare orderID to make this consistent with the equals() method.
             //return (this._orderID < anotherOrder._orderID)?-1:1;
          }
          else
          {
              return (this._dateTime < anotherOrder.getDateTime())?-1:1;
          }
      }
      else
      {
         if (this._side == MDFConstants.BID)
         {
            //highest bid comes first
            return (this._price > anotherOrder.getPrice()) ? -1 : 1;
         }
         else
         {
            //lowest ask comes first
            return (this._price < anotherOrder.getPrice()) ? -1 : 1;
         }
      }
   }
}



