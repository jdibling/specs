package com.theice.mdf.client.qv.gui.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.notification.EndOfDayMarketSummaryMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TODO how does this message work with options markets?
 * TODO The holder returns an array of "all" markets (futures/otc and options). Is this okay?
 * 
 * @author Adam Athimuthu
 */
public class QVEndOfDayMarketMessageModel extends AbstractTableModel
{
	private Vector<QVEndOfDayMarketTableData> _data=new Vector<QVEndOfDayMarketTableData>();

    public QVEndOfDayMarketMessageModel()
    {
        super();
        
        refresh();
    }
    
    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(QVEndOfDayMarketMessageColumn.columns.length);
    }

    /**
     * Get Row Count
     * @return
     */
    public synchronized int getRowCount()
    {
        return(_data.size());
    }

    public String getColumnName(int col)
    {
        return(QVEndOfDayMarketMessageColumn.columns[col].getName()); 
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return(QVEndOfDayMarketMessageColumn.columns[columnIndex].getColumnClass());
    }

    /**
     * load the table data from the markets
     */
    public void refresh()
    {
    	MarketInterface markets[]=MarketsHolder.getInstance().getAllMarkets();
    	
    	if(!_data.isEmpty())
    	{
    		int size=_data.size();
    		_data.clear();
    		fireTableRowsDeleted(0, size-1);
    	}
    	
    	for(int index=0;index<markets.length;index++)
    	{
    		MarketInterface market=markets[index];
    		
    		EndOfDayMarketSummaryMessage eodMessage=market.getEndOfDayMarketSummary();
    		
    		if(eodMessage==null)
    		{
    			continue;
    		}
    		
			_data.add(new QVEndOfDayMarketTableData(market.getMarketID(),market.getMarketDesc(),eodMessage));
    	}
    	
    	this.fireTableDataChanged();
    }

    public void cleanup()
    {
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
        QVEndOfDayMarketTableData data=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        try
        {
            data=(QVEndOfDayMarketTableData) _data.get(row);
        }
        catch(Exception e)
        {
            System.err.println("QVEndOfDayMarketData not found for row : "+row);
        }

        if(data!=null)
        {
            switch(col)
            {
                case QVEndOfDayMarketMessageColumn.COLID_MARKETID:
                    value=data.getMarketId();
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_MARKETDESC:
                    value=data.getMarketDescription();
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_TOTALVOLUME:
                    value=data.getEndOfDayMarketSummary().TotalVolume;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_BLOCKVOLUME:
                    value=data.getEndOfDayMarketSummary().BlockVolume;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_EFSVOLUME:
                    value=data.getEndOfDayMarketSummary().EFSVolume;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_EFPVOLUME:
                    value=data.getEndOfDayMarketSummary().EFPVolume;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_OPENING_PRICE:
                    value=data.getEndOfDayMarketSummary().OpeningPrice;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_HIGH:
                    value=data.getEndOfDayMarketSummary().High;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_LOW:
                    value=data.getEndOfDayMarketSummary().Low;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_VWAP:
                    value=data.getEndOfDayMarketSummary().VWAP;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_SETTLEMENT_PRICE:
                    value=data.getEndOfDayMarketSummary().SettlementPrice;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_OPEN_INTEREST:
                    value=data.getEndOfDayMarketSummary().OpenInterest;
                    break;
                case QVEndOfDayMarketMessageColumn.COLID_DATETIME:
                    value=MDFUtil.simpleDateTimeFormatter.format(data.getEndOfDayMarketSummary().DateTime);
                    break;
                default:
                    value="???";
                    break;
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
     * Sort on a given column ascending or descending
     * @param column
     * @param isAscending
     */
    public synchronized void sort(int column, boolean isAscending)
    {
    	Collections.sort(_data, new QVEndOfDayMarketMessageSorter(column,isAscending));
    	this.fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Sorter
     * @author Adam Athimuthu
     */
    protected class QVEndOfDayMarketMessageSorter implements Comparator<QVEndOfDayMarketTableData> 
    {
    	protected int _column;
    	protected boolean _isAscending;
    	
    	public QVEndOfDayMarketMessageSorter(int column,boolean isAscending)
    	{
    		_column=column;
    		_isAscending=isAscending;
    	}
    	
        public int compare(QVEndOfDayMarketTableData first, QVEndOfDayMarketTableData second)
        {
            int result=0;
            
            switch(_column)
            {
	            case QVEndOfDayMarketMessageColumn.COLID_MARKETID:
	            	result=first.getMarketId()-second.getMarketId();
	            break;
            }

            if(!_isAscending)
            {
            	result=-result;
            }
            	
            return(result);
        }
    }
}

