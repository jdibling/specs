package com.theice.mdf.client.domain;

import com.theice.mdf.client.domain.book.BookContext;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.<p/>
 * 
 * This class keeps track of the accumulated quantity on a price level for bids or offers (mutually exclusive).
 * Price Level cannot be immutable because we need to change the quantity very frequently for busy markets, 
 * thus a copy constructor is needed if we need to pass a price level instance as a value object to other
 * parts of the system.
 * 
 * @author aathimut
 * @author qwang
 * @version %I%, %G% Created: Aug 25, 2007 11:31:17 PM
 * 
 */
public class PriceLevel
{
	protected final long _price;
	private int _quantity;

	protected char _side;
	protected byte _position;
	protected int _orderCount;
	protected int _impliedQuantity;
	protected int _impliedOrderCount;

	/**
	 * The context in which this price level was constructed
	 */
	protected BookContext _bookContext;

	/**
    * Create a new price level from the first order for the level. Set the context to Full Order Depth
    * Initialize the order count, implied quantity/count
    * @param MarketOrder
    * @see FullOrderBook
    */
   public PriceLevel(MarketOrder order)
   {
      this._price = order.getPrice();
      this._quantity = order.getQuantity();
      this._orderCount=1;
      
      if(order.isImplied())
      {
    	  _impliedOrderCount=1;
    	  _impliedQuantity+=order.getQuantity();
      }
      
      _bookContext=BookContext.FULLORDERDEPTH;
   }

   /**
    * Create a new price level in the price level book context
    * @param price
    * @param quantity
    * @param side
    * @param position
    * @param orderCount
    * @param impliedQuantity
    * @param impliedOrderCount
    * @see PriceLevelBook
    */
   public PriceLevel(long price,int quantity,char side,
		   byte position,int orderCount,int impliedQuantity,int impliedOrderCount)
   {
      _price=price;
      _quantity=quantity;
      _side=side;
      _position=position;
      _orderCount=orderCount;
      _impliedQuantity=impliedQuantity;
      _impliedOrderCount=impliedOrderCount;
      
      _bookContext=BookContext.PRICELEVEL;
   }

   /**
    * Copy constructor to duplicate a priceLevel so that we can pass it as a value object
    * @param priceLevel
    */
   public PriceLevel(PriceLevel priceLevel)
   {
      this._price = priceLevel._price;
      this._quantity = priceLevel._quantity;
      this._side=priceLevel._side;
      this._position=priceLevel._position;
      this._orderCount=priceLevel._orderCount;
      this._impliedQuantity=priceLevel._impliedQuantity;
      this._impliedOrderCount=priceLevel._impliedOrderCount;
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

   public void increase(int quantity)
   {
	   _quantity+=quantity;
   }
   
   public void decrease(int quantity)
   {
	   _quantity-=quantity;
   }

   public char getSide()
   {
	   return(_side);
   }
   
   public byte getPosition()
   {
	   return(_position);
   }
   
   public int getOrderCount()
   {
	   return(this._orderCount);
   }
   
   public void incrementOrderCount()
   {
	   _orderCount++;
   }
   
   public void decrementOrderCount()
   {
	   _orderCount--;
   }

   public int getImpliedQuantity()
   {
	   return(this._impliedQuantity);
   }
   
   public void increaseImpliedQuantity(int quantity)
   {
	   _impliedQuantity+=quantity;
   }
   
   public void decreaseImpliedQuantity(int quantity)
   {
	   _impliedQuantity-=quantity;
   }

   public int getImpliedOrderCount()
   {
	   return(this._impliedOrderCount);
   }
   
   public void incrementImpliedOrderCount()
   {
	   _impliedOrderCount++;
   }
   
   public void decrementImpliedOrderCount()
   {
	   _impliedOrderCount--;
   }

   /**
    * isBuy
    * @return
    */
   public boolean isBuy()
   {
       return (_side == '1');
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer("Price Level Details:");
      buf.append("[Price:" + this._price + "]");
      buf.append("[Quantity:" + this._quantity + "]");
      
      if(_bookContext!=BookContext.FULLORDERDEPTH)
      {
          buf.append("[Side:" + this._side + "]");
          buf.append("[Position:" + this._position + "]");
          buf.append("[ImpliedQuantity:" + this._impliedQuantity + "]");
          buf.append("[ImpliedOrderCount:" + this._impliedOrderCount + "]");
      }
      
      buf.append("\n");
      
      return (buf.toString());
   }

}

