package com.theice.mdf.client.domain.book;

import com.theice.mdf.client.domain.PriceLevel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Message processing for the Price Level Context
 * PriceLevel Book Context is supported by Futures/OTC as well as the Options Markets
 * 
 * @see AbstractMarketBase
 * @author : Adam Athimuthu
 */
public interface PriceLevelBookKeeper
{
    public void addPriceLevel(PriceLevel priceLevel);
    public void changePriceLevel(PriceLevel priceLevel);
    public void removePriceLevel(byte priceLevelPosition, char side);

}
