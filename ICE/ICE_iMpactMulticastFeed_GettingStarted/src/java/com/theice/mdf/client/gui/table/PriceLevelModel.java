package com.theice.mdf.client.gui.table;

import com.theice.mdf.client.domain.*;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * PriceLevel Table Model - used for displaying bid and offer price levels, for a specific market
 *
 * 1. Bids (sorted on descending order of price)
 * 2. Offers (sorted on ascending order of price)
 *
 * @author Adam Athimuthu
 * Date: Aug 20, 2007
 * Time: 1:01:51 PM
 *
 */
public class PriceLevelModel extends AbstractTableModel
{
    private MarketInterface _market=null;

    /**
     * indicates if this model belongs to Bid or Offer
     */
    private char _bookFlag;
    
    /**
     * PriceLevel objects
     */
    private PriceLevel[] _priceLevels=new PriceLevel[0];

    private Logger logger=Logger.getLogger(PriceLevelModel.class.getName());

    /**
     * PriceLevel table model
     */
    private PriceLevelModel()
    {
        super();
    }

    /**
     * Constructor, supplied with the mode (bid/offer) and the market
     * Based on the book flag, we init the internal collection
     *
     * @param bookFlag
     * @param market
     */
    public PriceLevelModel(char bookFlag, MarketInterface market)
    {
        super();
        
        _market=market;

        _bookFlag=bookFlag;

        refreshPriceLevels();

        init();
    }

    /**
     * refresh for the market it was associated with
     */
    public synchronized void refresh()
    {
        refreshPriceLevels();
        fireTableDataChanged();
    }

    /**
     * refresh the price levels
     * @param market
     */
    private void refreshPriceLevels()
    {
        synchronized(_market.getUnderlyingMarket())
        {
        	_priceLevels=_market.getPriceLevels(_bookFlag);
        }
    	
    }
    
//    /**
//     * update price levels with the market supplied. If market is null, we get the market from the MarketHolder
//     *
//     * @param market
//     */
//    private void refreshPriceLevelsOld()
//    {
//        synchronized(_market.getUnderlyingMarket())
//        {
//            /**
//             * If bid, then do a descending order sort
//             */
//            if(_bookFlag==MDFConstants.BID)
//            {
//            	Collection<PriceLevel> bids=_market.getBidPriceLevels();
//                
//                int length=bids.size();
//
//                if(length>0)
//                {
//                    _priceLevels=new PriceLevel[length];
//
//                    int index=length;
//
//                    for(Iterator it=bids.iterator();it.hasNext();)
//                    {
//                        _priceLevels[--index]=new PriceLevel((PriceLevel) it.next());
//                    }
//                }
//                else
//                {
//                    _priceLevels=new PriceLevel[0];
//                }
//            }
//            else
//            {
//            	Collection<PriceLevel> offers=_market.getOfferPriceLevels();
//                
//                int length=offers.size();
//                
//                if(length>0)
//                {
//                    _priceLevels=new PriceLevel[length];
//
//                    int index=0;
//
//                    for(Iterator it=offers.iterator();it.hasNext();)
//                    {
//                        _priceLevels[index++]=new PriceLevel((PriceLevel) it.next());
//                    }
//                	
//                }
//                else
//                {
//                    _priceLevels=new PriceLevel[0];
//                }
//            }
//
//        }
//
//        return;
//    }

    /**
     * initialize tasks
     * - internal data structure
     * - subscriptions
     */
    public void init()
    {
        return;
    }

    /**
     * cleanup tasks
     * - remove from subscriptions
     */
    public void cleanup()
    {
        return;
    }

    /**
     * Get the side as bid/offer, that this model is currently configured with
     * @return
     */
    public char getSide()
    {
        return(_bookFlag);
    }

    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(PriceLevelTableColumn.columns.length);
    }

    /**
     * Get Row Count
     * @return
     */
    public int getRowCount()
    {
        return(_priceLevels.length);
    }

    public String getColumnName(int col)
    {
        return(PriceLevelTableColumn.columns[col].getName());
    }

    public Class getColumnClass(int col)
    {
        return(PriceLevelTableColumn.columns[col].getColumnClass());
    }

    /**
     * Get Value At
     * @param row
     * @param col
     * @return
     */
    public Object getValueAt(int row, int col) throws ArrayIndexOutOfBoundsException
    {
        Object value=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        synchronized(_priceLevels)
        {
            PriceLevel priceLevel=null;

            try
            {
                priceLevel=_priceLevels[row];
            }
            catch(Exception e)
            {
                StringBuffer err=new StringBuffer("PriceLevelModel(row) not found : "+row+"\n");
                err.append("Dumping PriceLevels : "+_priceLevels.toString());
                logger.warn(err.toString());
            }

            if(priceLevel!=null)
            {
                switch(col)
                {
                    case PriceLevelTableColumn.COLID_PRICE:
                        value=priceLevel.getPrice();
                        break;
                    case PriceLevelTableColumn.COLID_QTY:
                        value=priceLevel.getQuantity();
                        break;
                    case PriceLevelTableColumn.COLID_ORDERCOUNT:
                        value=priceLevel.getOrderCount();
                        break;
                    case PriceLevelTableColumn.COLID_IMPLIEDQUANTITY:
                        value=priceLevel.getImpliedQuantity();
                        break;
                    case PriceLevelTableColumn.COLID_IMPLIEDCOUNT:
                        value=priceLevel.getImpliedOrderCount();
                        break;
                    default:
                        value="???";
                        break;
                }
            }
        }

        return(value);
    }

    /**
     * make the cells non-editable
     * @param row
     * @param col
     * @return
     */
    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

}

