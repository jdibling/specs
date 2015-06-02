package com.theice.mdf.client.domain.book;

import java.util.Collection;

import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.PriceLevel;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.domain.transaction.Transaction;
import com.theice.mdf.client.exception.ProcessingException;

import java.io.Serializable;
import java.lang.UnsupportedOperationException;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * An interface to abstract the book associated with a market
 * 
 * There are currently two known implementations of a book
 * 
 * 1. FullOrderDepth Book that holds all the individual orders
 * 2. PriceLevel Only Book that holds just the top 5 price levels as sent by the backend system
 * 
 * In the Full Order Depth implementation, the price levels are calculated using the orders as they
 * are added/modified or deleted
 * 
 * In the Price level implementation, the information is kept "as-is" that was sent from the server
 * 
 * @see Market. The Market object holds an abstraction of a book. The concrete implementation depends on
 * the creator of markets based on a specific context  
 * 
 * @author Adam Athimuthu
 */
public interface Book extends Serializable
{
    /**
     * Methods applicable for order depth
     * Books that don't deal with full order depth, must throw UnsupportedOperationException
     */
    public void processAddOrder(AddModifyTransaction transaction) throws UnsupportedOperationException;
    
    /**
     * process remove order
     * @param marketOrder, the order that has to be removed from the book
     * @param triggeringTransaction, the transaction that triggered this removal, such as a Modify Order that might replace this
     * 			order after removal
     * @return
     * @throws UnsupportedOperationException
     */
    public MarketOrder processRemoveOrder(MarketOrder removedOrder,Transaction triggeringTransaction) throws UnsupportedOperationException;
    
    public Collection<MarketOrder> getBids() throws UnsupportedOperationException;
    public Collection<MarketOrder> getOffers() throws UnsupportedOperationException;

    /**
     * PriceLevel operations
     */
    public void addPriceLevel(PriceLevel priceLevel) throws UnsupportedOperationException, ProcessingException;
    public void changePriceLevel(PriceLevel priceLevel) throws UnsupportedOperationException, ProcessingException;
    public PriceLevel removePriceLevel(byte priceLevelPosition, char side)
    	throws UnsupportedOperationException,ProcessingException;
    
    public PriceLevel[] getPriceLevels(char side);

    /**
     * Book Context/Control
     */
    public boolean resetBookUpdatedIfTrue();
	public BookContext getContext();
    public void initialize();
    
    /**
     * Crossed Book Detection
     */
    public boolean isCrossed();
    public int getMarketID();
    public void setMarketID(int marketID);
}

