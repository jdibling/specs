package com.theice.mdf.client.domain.book;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Book Context enumeration
 * 
 * The book context is determined based on the Multicast Channel Context as well as the market to which
 * the book is attached to.
 * 
 * For example, an option market is always initialized with a PriceLevel book, whereas the underlying market
 * can by either Full Order Depth or Price Level. Also, when we are dealing with an OptionsTopOfBook multicast
 * channel, the underlying market is constructed with a NullBook
 * 
 * @see SimpleClientConfigurator, MulticastChannelContext
 * 
 * @author Adam Athimuthu
 */
public enum BookContext 
{
	FULLORDERDEPTH,
	PRICELEVEL,
	NULLBOOK
}

