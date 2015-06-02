package com.theice.mdf.client.gui.table;

import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.message.response.ProductDefinitionResponse;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.client.process.handlers.MarketSnapshotHandler;
import com.theice.mdf.client.process.handlers.MarketStateChangeHandler;
import com.theice.mdf.client.process.handlers.ProductDefinitionHandler;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The market table model is used to feed the GUI for displaying the markets for a given market type
 * 
 * TODO subscribe with an event filter=marketType to avoid unnecessary message notifications
 *
 * @author Adam Athimuthu
 * Date: Aug 8, 2007
 * Time: 9:09:06 AM
 */
public class MarketTableModel extends AbstractTableModel implements MDSubscriber
{
    /**
     * internal array of Market objects
     */
    private ArrayList<Market> _markets=new ArrayList<Market>();
    
    private Short marketType=null;

    private MarketTableModel()
    {
    }

    public MarketTableModel(Short marketType)
    {
        super();
        this.marketType=marketType;
        init();
    }

    /**
     * TODO: MarketTableModel : Do selective subscription using marketType
     */
    public void init()
    {
        ProductDefinitionHandler.getInstance().addSubscriber(this);
        MarketStateChangeHandler.getInstance().addSubscriber(this);
        MarketSnapshotHandler.getInstance().addSubscriber(this);
    }

    public void cleanup()
    {
        ProductDefinitionHandler.getInstance().removeSubscriber(this);
        MarketStateChangeHandler.getInstance().removeSubscriber(this);
        MarketSnapshotHandler.getInstance().removeSubscriber(this);
    }

    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(MarketTableColumn.columns.length);
    }

    /**
     * Get Row Count
     * @return
     */
    public int getRowCount()
    {
        return(_markets.size());
    }

    public String getColumnName(int col)
    {
        return(MarketTableColumn.columns[col].getName()); 
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return(MarketTableColumn.columns[columnIndex].getColumnClass());
    }

    /**
     * Get Value At
     * @param row
     * @param col
     * @return
     */
    public Object getValueAt(int row, int col)
    {
        Object value=null;

        Market market=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        synchronized(_markets)
        {
            try
            {
                market=(Market) _markets.get(row);
            }
            catch(Exception e)
            {
                System.err.println("Market Type not found for row : "+row);
            }

            if(market!=null)
            {
                ProductDefinitionResponse source=market.getSource();

                switch(col)
                {
                    case MarketTableColumn.COLID_MARKETID:
                        value=market.getMarketID();
                        break;
                    case MarketTableColumn.COLID_MARKETDESC:
                        value=market.getMarketDesc();
                        break;
                    case MarketTableColumn.COLID_TRADINGSTATUS:
                        value=source.TradingStatus;
                        break;
                    case MarketTableColumn.COLID_ORDERPRICEDENOMINATOR:
                        value=source.OrderPriceDenominator;
                        break;
                    case MarketTableColumn.COLID_INCREMENTPRICE:
                        value=source.IncrementPrice;
                        break;
                    case MarketTableColumn.COLID_INCREMENTQTY:
                        value=source.IncrementQty;
                        break;
                    case MarketTableColumn.COLID_LOTSIZE:
                        value=source.LotSize;
                        break;
                    case MarketTableColumn.COLID_MATURITYYEAR:
                        value=source.MaturityYear;
                        break;
                    case MarketTableColumn.COLID_MATURITYMONTH:
                        value=source.MaturityMonth;
                        break;
                    case MarketTableColumn.COLID_MATURITYDAY:
                        value=source.MaturityDay;
                        break;
                    case MarketTableColumn.COLID_ISSPREAD:
                        value=source.IsSpread;
                        break;
                    case MarketTableColumn.COLID_MINQTY:
                        value=source.MinQty;
                        break;
                    case MarketTableColumn.COLID_UNITQUANTITY:
                        value=source.UnitQuantity;
                        break;
                    case MarketTableColumn.COLID_CURRENCY:
                        value= MessageUtil.toString(source.Currency);
                        break;
                    case MarketTableColumn.COLID_CONTRACTSYMBOL:
                        value=market.getContractSymbol();
                        break;
                    case MarketTableColumn.COLID_ISCRACKSPREAD:
                        value=source.IsCrackSpread;
                        break;
                    case MarketTableColumn.COLID_PRIMARYMARKETID:
                        value=source.PrimaryMarketID;
                        break;
                    case MarketTableColumn.COLID_SECONDARYMARKETID:
                        value=source.SecondaryMarketID;
                        break;
                    case MarketTableColumn.COLID_ISOPTIONS:
                        value=source.IsOptions;
                        break;
                    case MarketTableColumn.COLID_OPTIONTYPE:
                        value=source.OptionType;
                        break;
                    case MarketTableColumn.COLID_STRIKEPRICE:
                        value=source.StrikePrice;
                        break;
                    case MarketTableColumn.COLID_SECONDSTRIKE:
                        value=source.SecondStrike;
                        break;
                    case MarketTableColumn.COLID_DEALPRICEDENOMINATOR:
                        value=source.DealPriceDenominator;
                        break;
                    case MarketTableColumn.COLID_SETTLEPRICEDENOMINATOR:
                       value=source.SettlementPriceDenominator;
                       break;
                    case MarketTableColumn.COLID_MINSTRIKEPRICE:
		                value=source.MinStrikePrice;
		                break;
		            case MarketTableColumn.COLID_MAXSTRIKEPRICE:
		                value=source.MaxStrikePrice;
		                break;
		            case MarketTableColumn.COLID_INCREMENTSTRIKEPRICE:
		                value=source.IncrementStrikePrice;
		                break;
		            case MarketTableColumn.COLID_NUMDECIMALSSTRIKEPRICE:
		                value=source.NumDecimalsStrikePrice;
		                break;
		            case MarketTableColumn.COLID_MINOPTIONSPRICE:
		                value=source.MinOptionsPrice;
		                break;
		            case MarketTableColumn.COLID_MAXOPTIONSPRICE:
		                value=source.MaxOptionsPrice;
		                break;
		            case MarketTableColumn.COLID_INCREMENTOPTIONSPRICE:
		                value=source.IncrementOptionsPrice;
		                break;
		            case MarketTableColumn.COLID_NUMDECIMALSOPTIONSPRICE:
		                value=source.NumDecimalsOptionsPrice;
		                break;
		            case MarketTableColumn.COLID_TICKVALUE:
		                value=source.TickValue;
		                break;
					case MarketTableColumn.COLID_ALLOWOPTIONS:
						value=source.AllowOptions;
		                break;
					case MarketTableColumn.COLID_CLEAREDALIAS:
						value=MessageUtil.toString(source.ClearedAlias);
		                break;
					case MarketTableColumn.COLID_ALLOWIMPLIED:
						value=source.AllowImplied;
		                break;
					case MarketTableColumn.COLID_OPTIONSEXPYEAR:
						value=source.OptionsExpirationYear;
		                break;
					case MarketTableColumn.COLID_OPTIONSEXPMONTH:
						value=source.OptionsExpirationMonth;
		                break;
					case MarketTableColumn.COLID_OPTIONSEXPDAY:
						value=source.OptionsExpirationDay;
		                break;
					case MarketTableColumn.COLID_MINPRICE:
						value=source.MinPrice;
						break;
					case MarketTableColumn.COLID_MAXPRICE:
						value=source.MaxPrice;
						break;
					case MarketTableColumn.COLID_PRODUCTID:
						value=source.ProductID;
						break;
					case MarketTableColumn.COLID_PRODUCTNAME:
						value=MessageUtil.toString(source.ProductName);
						break;
					case MarketTableColumn.COLID_HUBID:
						value=source.HubID;
						break;
					case MarketTableColumn.COLID_HUBALIAS:
						value=MessageUtil.toString(source.HubAlias);
						break;
					case MarketTableColumn.COLID_STRIPID:
						value=source.StripID;
						break;
					case MarketTableColumn.COLID_STRIPNAME:
						value=MessageUtil.toString(source.StripName);
						break;
               case MarketTableColumn.COLID_RESERVEDFLD1:
                  value=source.ReservedField1;
                  break;
               case MarketTableColumn.COLID_ISFORSERIALOPTIONS:
                  value=source.IsSerialOptionsSupported;
                  break;
               case MarketTableColumn.COLID_ISTRADABLE:
                  value=source.IsTradable;
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

    /**
     * get the markets vector
     * @return
     */
    public synchronized List getMarkets()
    {
        return(_markets);
    }

    /**
     * add market and sort the internal array using a custom comparator
     * @param market
     */
    public synchronized void addMarket(Market market)
    {
        _markets.add(market);

        Collections.sort(_markets,_marketComparator);

        return;
    }
    
    /**
     * refresh the market state for the given market id and return the index (row)
     * @param marketId
     * @return row
     */
    protected synchronized int refreshMarketState(long marketId)
    {
    	int row=-1;
    	
    	for(int index=0;index<_markets.size();index++)
    	{
    		if(_markets.get(index).getMarketID()==marketId)
    		{
    			row=index;
    			break;
    		}
    	}
    	
    	return(row);
    }

    /**
     * MD Subscriber event notifications
     * @param message
     */
    public synchronized void notifyWithMDMessage(MDMessage message)
    {
        char messageType=message.getMessageType();

        switch(messageType)
        {
            case RawMessageFactory.ProductDefinitionResponseType:
            	
            	ProductDefinitionResponse productDefinition=(ProductDefinitionResponse) message;
            	
            	if(productDefinition.RequestMarketType!=this.marketType.shortValue())
            	{
//            		StringBuffer buf=new StringBuffer("MarketTableModel for [").append(this.marketType);
//            		buf.append("] Got product definition for an unneeded market type : ").append(+productDefinition.RequestMarketType);
//            		System.err.println(buf.toString());
            		break;
            	}

                fireTableDataChanged();
                break;

            case RawMessageFactory.MarketStateChangeMessageType:
            case RawMessageFactory.MarketSnapshotMessageType:   

            	/**
            	 * TODO With pre-defined options markets, check and make sure if we don't process state changes for options
            	 * TODO this is taken care by not processing any markets that we fail to lookup
            	 */
            	int row=refreshMarketState(message.getMarketID());
                
                if(row>=0)
                {
                    fireTableCellUpdated(row, MarketTableColumn.COLID_TRADINGSTATUS);
                }
                
                break;

            default:
                System.out.println("Notification received for unknown message type: "+messageType);
                break;
        }
    }

    /**
     * Sort on a given column ascending or descending
     * @param column
     * @param isAscending
     */
    public synchronized void sort(int column, boolean isAscending)
    {
    	Collections.sort(_markets, new MarketSorter(column,isAscending));
    	this.fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Market Comparator
     */
    static final Comparator<Market> _marketComparator =
        new Comparator<Market>()
        {
            /**
             * Sort on the following criteria
             * - all non-spread markets should appear first
             * - within a given spread/non-spread, sorting should be done by Maturity year/month/day
             * @param e1
             * @param e2
             * @return
             */
            public int compare(Market e1, Market e2)
            {
                int result=0;
                
                if(e1.getSource().IsSpread<e2.getSource().IsSpread)
                {
                    result=-1;
                }
                else if(e1.getSource().IsSpread>e2.getSource().IsSpread)
                {
                    result=1;
                }
                else
                {
                    result=e1.getSource().MaturityYear-e2.getSource().MaturityYear;

                    if(result==0)
                    {
                        result=e1.getSource().MaturityMonth-e2.getSource().MaturityMonth;

                        if(result==0)
                        {
                            result=e1.getSource().MaturityDay-e2.getSource().MaturityDay;
                        }
                    }
                }

                return(result);
            }
        };

    /**
     * Sorter
     * @author Adam Athimuthu
     */
    protected class MarketSorter implements Comparator<Market> 
    {
    	protected int _column;
    	protected boolean _isAscending;
    	
    	public MarketSorter(int column,boolean isAscending)
    	{
    		_column=column;
    		_isAscending=isAscending;
    	}
    	
        public int compare(Market first, Market second)
        {
            int result=0;
            
            switch(_column)
            {
	            case MarketTableColumn.COLID_MARKETID:
	            	result=first.getMarketID()-second.getMarketID();
	               break;
	            case MarketTableColumn.COLID_MARKETDESC:
	               result=first.getMarketDesc().compareTo(second.getMarketDesc());
	               break;
            }

            if(!_isAscending)
            {
            	result=-result;
            }
            	
            return(result);
        }
    }
    
    protected boolean isReservedFlagOn(int row)
    {
       Market market = _markets.get(row);
       if (market!=null && market.getSource()!=null)
       {
          ProductDefinitionResponse source=market.getSource();
          if (source.ReservedField1=='3')
          {
             return true;
          }
       }
       
       return false;
    }
}

