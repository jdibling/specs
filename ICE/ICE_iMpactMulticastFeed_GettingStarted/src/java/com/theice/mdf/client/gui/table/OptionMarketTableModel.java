package com.theice.mdf.client.gui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.OptionMarket;
import com.theice.mdf.client.domain.OptionMarketKey;
import com.theice.mdf.client.multicast.handler.NewFlexOptionDefinitionHandler;
import com.theice.mdf.client.multicast.handler.NewOptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.handlers.MarketStateChangeHandler;
import com.theice.mdf.client.process.handlers.OptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.handlers.OptionsProductDefinitionHandler;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.NewOptionStrategyDefinitionMessage;
import com.theice.mdf.message.notification.NewOptionsMarketDefinitionMessage;
import com.theice.mdf.message.response.OptionStrategyDefinitionResponse;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Option market table model is used to feed the GUI for displaying the option markets
 * 
 * TODO State change for options
 *
 * @author Adam Athimuthu
 */
public class OptionMarketTableModel extends AbstractTableModel implements MDSubscriber
{
    private final Logger logger=Logger.getLogger(OptionMarketTableModel.class.getName());

    /**
	 * The underlying market for which this model holds the options
	 */
	private Market _underlyingMarket=null;

	/**
     * internal array of OptionMarket objects
     */
    private List<OptionMarket> _optionMarkets=new ArrayList<OptionMarket>();

    private OptionMarketTableModel()
    {
    }

    public OptionMarketTableModel(Market market)
    {
        super();
        
        _underlyingMarket=market;
        
    	init();
    }

    /**
     * refresh
     * Sort the option markets array on StrikePrice followed by Call/Put
     */
    public void refresh()
    {
    	Collection<OptionMarket> dependentMarkets=null;
    	
    	synchronized(_underlyingMarket.getUnderlyingMarket())
    	{
        	Map<OptionMarketKey,OptionMarket> optionMarkets=_underlyingMarket.getOptionMarkets();
        	
        	if(optionMarkets.size()==0)
        	{
        		if(logger.isTraceEnabled())
        		{
            		logger.trace("No options markets for : "+_underlyingMarket.getMarketID());
        		}
        		return;
        	}
        	
        	dependentMarkets=optionMarkets.values();
    	}

    	synchronized(this)
    	{
        	_optionMarkets.clear();
        	
        	if(dependentMarkets!=null)
        	{
            	_optionMarkets.addAll(dependentMarkets);
                Collections.sort(_optionMarkets,_optionMarketComparator);
        	}
    	}

		fireTableDataChanged();
    }
    
    /**
     * initialize
     * called after all the product definitions have been loaded
     * that we can just make a single copy of the options markets
     */
    public void init()
    {
        refresh();
        
        _underlyingMarket.resetDependentMarketsUpdatedIfTrue();

        OptionsProductDefinitionHandler.getInstance().addEventSubscriber(this,this._underlyingMarket.getMarketID());
        OptionStrategyDefinitionHandler.getInstance().addEventSubscriber(this,this._underlyingMarket.getMarketID());
        NewOptionStrategyDefinitionHandler.getInstance().addEventSubscriber(this,this._underlyingMarket.getMarketID());
        NewFlexOptionDefinitionHandler.getInstance().addEventSubscriber(this, this._underlyingMarket.getMarketID());
        /**
         * For market changes, we will do just a global subscription instead of any filters
         * If the market id in the state change message match one of the underlying markets, we will update the state
         * of that options market
         */
        MarketStateChangeHandler.getInstance().addSubscriber(this);
    }

    public void cleanup()
    {
        OptionsProductDefinitionHandler.getInstance().removeEventSubscriber(this,this._underlyingMarket.getMarketID());
        OptionStrategyDefinitionHandler.getInstance().removeEventSubscriber(this,this._underlyingMarket.getMarketID());
        NewOptionStrategyDefinitionHandler.getInstance().removeEventSubscriber(this,this._underlyingMarket.getMarketID());
        NewFlexOptionDefinitionHandler.getInstance().removeEventSubscriber(this, this._underlyingMarket.getMarketID());
        MarketStateChangeHandler.getInstance().removeSubscriber(this);
        
    }

    /**
     * get the option markets
     * @return
     */
    public synchronized OptionMarket[] getOptionMarkets()
    {
        return(_optionMarkets.toArray(new OptionMarket[0]));
    }

    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(OptionMarketTableColumn.columns.length);
    }

    /**
     * Get Row Count
     * @return
     */
    public synchronized int getRowCount()
    {
        return(_optionMarkets.size());
    }

    public String getColumnName(int col)
    {
        return(OptionMarketTableColumn.columns[col].getName()); 
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return(OptionMarketTableColumn.columns[columnIndex].getColumnClass());
    }
    
    public OptionMarket getMarketAt(int row)
    {
       OptionMarket market = null;
       synchronized(this)
       {
           try
           {
               market=(OptionMarket) _optionMarkets.get(row);
           }
           catch(Exception e)
           {
               System.err.println("Option Market not found for row : "+row);
           }
       }
       return market;
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

        OptionMarket market=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        synchronized(this)
        {
            try
            {
                market=(OptionMarket) _optionMarkets.get(row);
            }
            catch(Exception e)
            {
                System.err.println("Option Market not found for row : "+row);
            }

            if(market!=null)
            {
                OptionsProductDefinitionResponse source=null;
                OptionStrategyDefinitionResponse udsSourceFromTcp=null;
                NewOptionStrategyDefinitionMessage udsSourceFromMC=null;
                NewOptionsMarketDefinitionMessage flexOptionsSource=null;
                
                if (market.isUDSMarket())
                {
                   if (market.getUDSSourceFromTcp()!=null)
                   {
                      udsSourceFromTcp = market.getUDSSourceFromTcp();
                   }
                   else
                   {
                      udsSourceFromMC = market.getUDSSourceFromMC();
                   }
                }
                else
                {
                   source = market.getOptionsSource();
                   flexOptionsSource = market.getFlexOptionsSource();
                }

                switch(col)
                {
                    case OptionMarketTableColumn.COLID_MARKETID:
                        value=market.getMarketID();
                        break;
                    case OptionMarketTableColumn.COLID_OPTIONTYPE:
                        value=market.getOptionTypeString();
                        break;
                    case OptionMarketTableColumn.COLID_STRIKEPRICE:
                        value=market.getStrikePrice();
                        break;
                    case OptionMarketTableColumn.COLID_CONTRACTSYMBOL:
                        value=market.getContractSymbol();
                        break;
                    case OptionMarketTableColumn.COLID_TRADINGSTATUS:
                        value=market.getTradingStatus();
                        break;
                    case OptionMarketTableColumn.COLID_ORDERPRICEDENOMINATOR:
                        value=market.getOrderPriceDenominator(); 
                        break;
                    case OptionMarketTableColumn.COLID_INCREMENTQTY:
                        value=market.getIncrementQty();
                        break;
                    case OptionMarketTableColumn.COLID_LOTSIZE:
                        value=market.getLotSize();
                        break;
                    case OptionMarketTableColumn.COLID_DEALPRICEDENOMINATOR:
                        value=market.getDealPriceDenominator();
                        break;
                    case OptionMarketTableColumn.COLID_SETTLEPRICEDENOMINATOR:
                       value=market.getSettlePriceDenominator();
                       break;
                    case OptionMarketTableColumn.COLID_MINQTY:
                        value=market.getMinQty();
                        break;
                    case OptionMarketTableColumn.COLID_CURRENCY:
                        value=market.getCurrency();
                        break;
                    case OptionMarketTableColumn.COLID_NUMDECIMALSSTRIKEPRICE:
                        value=market.getNumDecimalsStrikePrice();
                        break;
                    case OptionMarketTableColumn.COLID_MINOPTIONSPRICE:
                        value=market.getMinOptionsPrice();
                        break;
                    case OptionMarketTableColumn.COLID_MAXOPTIONSPRICE:
                        value=market.getMaxOptionsPrice();
                        break;
                    case OptionMarketTableColumn.COLID_INCREMENTPREMIUMPRICE:
                        value=market.getIncrementPremiumPrice(); 
                        break;
                    case OptionMarketTableColumn.COLID_OPTIONSEXPYEAR:
                        value=market.getOptionsExpYear();
                        break;
                    case OptionMarketTableColumn.COLID_OPTIONSEXPMONTH:
                        value=market.getOptionsExpMonth();
                        break;
                    case OptionMarketTableColumn.COLID_OPTIONSEXPDAY:
                        value=market.getOptionsExpDay();
                        break;
                    case OptionMarketTableColumn.COLID_OPTIONSSETTLEMENTTYPE:
                        value=market.getOptionsSettlementType();
                        break;
                    case OptionMarketTableColumn.COLID_OPTIONSEXPIRATIONTYPE:
                        value=market.getOptionsExpirationType();
                        break;
                    case OptionMarketTableColumn.COLID_SERIALUNDERLYINGMARKETID:
                        value=market.getSerialUnderlyingMarketID();
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
     * MD Subscriber event notifications
     * 
     * We get only selected notifications based on the filter market id.
     * The underlying market id is the filter because, the options table model basically presents all the options markets
     * underneath the underlying market
     * 
     * This callback will take effect only when all the product definitions for the underlying markets
     * have been loaded, but the options are not yet loaded at the time of launching this dialog (rare cases)
     * 
     * @param message
     */
    public synchronized void notifyWithMDMessage(MDMessage message)
    {
        char messageType=message.getMessageType();
        switch(messageType)
        {
            case RawMessageFactory.OptionsProductDefinitionResponseType:

            	OptionsProductDefinitionResponse optionsProductDefinition=(OptionsProductDefinitionResponse) message;
            	
            	if(optionsProductDefinition.UnderlyingMarketID!=this._underlyingMarket.getMarketID())
            	{
            		StringBuffer buf=new StringBuffer("Notification received for an unneeded underlying market. Should not happen.");
            		buf.append(" Expected : ").append(_underlyingMarket.getMarketID());
            		buf.append(" Received : ").append(optionsProductDefinition.toString());
            		logger.error(buf.toString());
            		break;
            	}
            	
            	this.refresh();
                fireTableDataChanged();
                break;

            case RawMessageFactory.OptionStrategyDefinitionResponseType:

               OptionStrategyDefinitionResponse udsDefinition=(OptionStrategyDefinitionResponse) message;
               
               if(udsDefinition.UnderlyingMarketID!=this._underlyingMarket.getMarketID())
               {
                  StringBuffer buf=new StringBuffer("Notification received for an unneeded underlying market. Should not happen.");
                  buf.append(" Expected : ").append(_underlyingMarket.getMarketID());
                  buf.append(" Received : ").append(udsDefinition.toString());
                  logger.error(buf.toString());
                  break;
               }
               
                this.refresh();
                fireTableDataChanged();
                break;
            
            case RawMessageFactory.NewOptionStrategyDefinitionMessageType:

                NewOptionStrategyDefinitionMessage newUDSMarket=(NewOptionStrategyDefinitionMessage) message;
               
                if(newUDSMarket.UnderlyingMarketID!=this._underlyingMarket.getMarketID())
                {
                   StringBuffer buf=new StringBuffer("Notification received for an unneeded underlying market. Should not happen.");
                   buf.append(" Expected : ").append(_underlyingMarket.getMarketID());
                   buf.append(" Received : ").append(newUDSMarket.toString());
                   logger.error(buf.toString());
                   break;
                }
               
                this.refresh();
                fireTableDataChanged();
               
                break;
                
            case RawMessageFactory.NewOptionsMarketDefinitionMessageType:
               
                NewOptionsMarketDefinitionMessage newFlexOptionsMarket=(NewOptionsMarketDefinitionMessage)message;
                if (newFlexOptionsMarket.UnderlyingMarketID!=this._underlyingMarket.getMarketID())
                {
                   StringBuffer buf=new StringBuffer("Notification received for an unneeded underlying market. Should not happen.");
                   buf.append(" Expected : ").append(_underlyingMarket.getMarketID());
                   buf.append(" Received : ").append(newFlexOptionsMarket.toString());
                   logger.error(buf.toString());
                   break;
                }

                this.refresh();
                fireTableDataChanged();

                break;
               
            case RawMessageFactory.MarketStateChangeMessageType:
            	
            	/**
            	 * if this was for the options market associated with this underlying market
            	 * then update the state of the market. Otherwise just ignore this message.
            	 */
                int row=refreshOptionsMarketState(message.getMarketID());
                
                if(row>=0)
                {
            		System.err.println("TODO : Verify handling of Options Market Trading Status...");
                    fireTableCellUpdated(row, OptionMarketTableColumn.COLID_TRADINGSTATUS);
                }

                break;

            default:
                System.out.println("Notification received for unknown message type: "+messageType);
                break;
        }
    }

    /**
     * refresh the options market state for the given market id and return the index (row)
     * @param marketId
     * @return row
     */
    protected synchronized int refreshOptionsMarketState(long marketId)
    {
    	int row=-1;
    	
    	for(int index=0;index<this._optionMarkets.size();index++)
    	{
    		if(_optionMarkets.get(index).getMarketID()==marketId)
    		{
    			row=index;
    			break;
    		}
    	}
    	
    	return(row);
    }

    /**
     * Options Market Comparator
     */
    static final Comparator<OptionMarket> _optionMarketComparator =
        new Comparator<OptionMarket>()
        {
            /**
             * Sort on the following criteria
             * - lowest to highest strikeprice
             * - Call/Put alternating
             * @param e1
             * @param e2
             * @return
             */
            public int compare(OptionMarket e1, OptionMarket e2)
            {
                int result=0;
                
                if(e1.getStrikePrice()<e2.getStrikePrice())
                {
                    result=-1;
                }
                else if(e1.getStrikePrice()>e2.getStrikePrice())
                {
                    result=1;
                }
                else
                {
                	if(e1.getOptionType()=='C')
                	{
                		result=-1;
                	}
                	else
                	{
                		if (e1.getOptionType()=='U' && e2.getOptionType()=='U')
                		{
                		   return e1.getMarketID()>e2.getMarketID()? -1 : 1;
                		}
                		else
                		{
                		   result=1;
                		}
                	}
                }

                return(result);
            }
        };

}

