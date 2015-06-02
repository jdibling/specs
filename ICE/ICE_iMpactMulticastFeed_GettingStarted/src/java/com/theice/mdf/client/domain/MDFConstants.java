package com.theice.mdf.client.domain;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 17, 2007
 * Time: 1:14:13 PM
 *
 */
public class MDFConstants
{
    /**
     * Bid vs. Offer
     */
    public static char BID='1';
    public static char OFFER='2';

    /**
     * Interval in ms for refreshing the book/pricelevel display
     */
    public static final int BOOK_REFRESH_INTERVAL=200;
    
    /**
     * Interval in ms for refreshing the dependent (options) markets
     * Need not be as frequent as the book dialog
     */
    public static final int DEPENDENT_MARKETS_REFRESH_INTERVAL=500;
    
}

