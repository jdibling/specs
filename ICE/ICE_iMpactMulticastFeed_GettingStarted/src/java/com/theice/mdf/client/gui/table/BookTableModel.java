package com.theice.mdf.client.gui.table;

import com.theice.mdf.client.domain.*;

import javax.swing.table.AbstractTableModel;

import java.util.Collection;
import java.util.SortedSet;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Book Table Model - used for displaying bids and offers, for a specific market
 *
 * @author Adam Athimuthu
 * Date: Aug 9, 2007
 * Time: 1:26:16 PM
 *
 */
public class BookTableModel extends AbstractTableModel
{
    private MarketInterface _market=null;

    /**
     * indicates if this model belongs to Bid or Offer
     */
    private char _bookFlag;

    /**
     * internal simple array of book entries
     */
    private MarketOrder[] _book=new MarketOrder[0];

    private Logger logger=Logger.getLogger(BookTableModel.class.getName());

    /**
     * Book table model
     */
    private BookTableModel()
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
    public BookTableModel(char bookFlag, MarketInterface market)
    {
        super();

        _bookFlag=bookFlag;

        _market=market;

        refreshBook();

        init();
    }

    /**
     * refresh book for the market it was associated with
     */
    public synchronized void refresh()
    {
        refreshBook();
        fireTableDataChanged();
    }

    /**
     * update book with the market supplied. If market is null, we get the market from the MarketHolder
     *
     * @param market
     */
    private void refreshBook()
    {
        synchronized(_market.getUnderlyingMarket())
        {
            if(_bookFlag==MDFConstants.BID)
            {
                Collection<MarketOrder> bids=_market.getBids();
                _book=bids.toArray(new MarketOrder[bids.size()]);
            }
            else
            {
            	Collection<MarketOrder> offers=_market.getOffers();
                _book=offers.toArray(new MarketOrder[offers.size()]);
            }
        }

        return;
    }

    /**
     * initialize
     * - internal data structure
     * - subscriptions
     */
    public void init()
    {
        return;
    }

    /**
     * cleanup
     * - remove from subscriptions
     */
    public void cleanup()
    {
        return;
    }

    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(BookTableColumn.columns.length);
    }

    /**
     * Get Row Count
     * @return
     */
    public int getRowCount()
    {
       synchronized(_market.getUnderlyingMarket())
       {
          return(_book.length);
       }
    }

    public String getColumnName(int col)
    {
        return(BookTableColumn.columns[col].getName());
    }

    public Class getColumnClass(int col)
    {
        return(BookTableColumn.columns[col].getColumnClass());
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
            System.out.println("********** Negative row/col");
            return("N/A");
        }

        synchronized(_market.getUnderlyingMarket())
        {
            MarketOrder entry=null;

            try
            {
                entry=(MarketOrder)_book[row];
            }
            catch(Exception e)
            {
                StringBuffer err=new StringBuffer("BookTableModel(row) not found : "+row+"\n");
                err.append("Dumping MarketOrders : "+_book.toString());
                logger.error(err.toString());
            }

            if(entry!=null)
            {
                switch(col)
                {
                    case BookTableColumn.COLID_ORDERID:
                        value=entry.getOrderID();
                        break;
                    case BookTableColumn.COLID_QTY:
                        value=entry.getQuantity();
                        break;
                    case BookTableColumn.COLID_PRICE:
                        value=entry.getPrice();
                        break;
                    case BookTableColumn.COLID_ISIMPLIED:
                        value=entry.getImplied();
                        break;
                    case BookTableColumn.COLID_TIMESTAMP:
                        value=entry.getDateTime();
                        break;
                    case BookTableColumn.COLID_RESERVEDFLD1:
                       String temp="N"; 
                       if (entry.isReservedFlagOn())
                       {
                          temp="Y";
                       }
                       value=temp;
                       break;
                    case BookTableColumn.COLID_PRIORITY:
                        value=entry._sequenceWithinMillis;
                        break;                       
                    default:
                        value="???";
                        break;
                }
            }
            else
            {
                logger.warn("********** Order is null "+row+"/"+col+" - Size "+getRowCount()+" "+_book.toString());
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
    
    protected boolean isReservedFlagOn(int row)
    {
       boolean reservedFlagOn=false;
       MarketOrder order = null;
       
       synchronized(_market.getUnderlyingMarket())
       {
          try
          {
             order=_book[row];
          }
          catch(Exception ex)
          {
             StringBuffer err=new StringBuffer("BookTableModel.isReservedFlagOn() row not found : "+row+"\n");
             err.append("Side=");
             err.append(_bookFlag);
             err.append(", MarketOrders size=");
             err.append(_book==null? "null" : _book.length);
             
             logger.error(err.toString());
          }
          
          if (order!=null && order.isReservedFlagOn())
          {
             reservedFlagOn=true;
          }
       }
       
       return reservedFlagOn;
       
    }


//    /**
//     * MD Subscriber notification method
//     *
//     * @param message
//     */
//    public synchronized void notifyWithMDMessageUnused(MDMessage message)
//    {
//        char messageType=' ';
//
//        if(message==null)
//        {
//            refreshBook(null);
//            fireTableDataChanged();
//            return;
//        }
//
//        /**
//         * Check the message type and fire the GUI events accordingly
//         */
//        messageType=message.getMessageType();
//
//        switch(messageType)
//        {
//            case RawMessageFactory.AddModifyOrderMessageType:
//
//                AddModifyOrderMessage addModifyOrderMessage=(AddModifyOrderMessage) message;
//
//                if(_attr._marketId==addModifyOrderMessage.getMarketID() && _attr._side==addModifyOrderMessage.Side)
//                {
//                    refreshBook(null);
//
//                    fireTableDataChanged();
//                }
//
//                break;
//
//            case RawMessageFactory.DeleteOrderMessageType:
//
//                DeleteOrderMessage deleteOrderMessage=(DeleteOrderMessage) message;
//
//                /**
//                 * TODO
//                 * For delete order messages, it is okay to fire the event on either bid or offers side
//                 * This is because, this event will be raised for both models belonging to bids/offers
//                 * Generally the GUI will have to be updated for only one of bids or offers
//                 */
//                if(_attr._marketId==deleteOrderMessage.getMarketID())
//                {
//                    refreshBook(null);
//
//                    fireTableDataChanged();
//                }
//
//                break;
//
//            case RawMessageFactory.TradeMessageType:
//
//                TradeMessage tradeMessage=(TradeMessage) message;
//
//                if(_attr._marketId==tradeMessage.getMarketID())
//                {
//                    refreshBook(null);
//
//                    fireTableDataChanged();
//                }
//
//                break;
//
//            default:
//                logger.finer("BookTableModel - Got notification for unknown nessage type : "+messageType);
//                break;
//        }
//    }

}

