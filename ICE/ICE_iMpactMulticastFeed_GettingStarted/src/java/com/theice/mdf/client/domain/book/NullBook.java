package com.theice.mdf.client.domain.book;

import java.util.Collection;
import java.util.ArrayList;
import com.theice.mdf.client.domain.PriceLevel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Null Book is used for the underlying markets when we have the multicast channel set to
 * OptionsTopOfBook. In this context, we don't care about keeping a book for the underlying markets
 * 
 * @see Market and the Options Top of Book Multicaster and related processing
 * 
 * @author Adam Athimuthu
 */
public class NullBook extends AbstractBook 
{
	public BookContext getContext()
	{
		return(BookContext.NULLBOOK);
	}

    public PriceLevel[] getPriceLevels(char side) throws UnsupportedOperationException
    {
    	throw(new UnsupportedOperationException("NullBook operation called"));
    }

    public void initialize()
    {
    }
    
    public Collection getBids()
    {
       return new ArrayList();
    }
    
    public Collection getOffers()
    {
       return new ArrayList();
    }
}
