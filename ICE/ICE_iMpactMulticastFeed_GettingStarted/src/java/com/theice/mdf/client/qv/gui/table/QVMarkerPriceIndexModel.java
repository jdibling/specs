package com.theice.mdf.client.qv.gui.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.gui.listeners.MDFActivityListener;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.handlers.ErrorResponseHandler;
import com.theice.mdf.client.qv.domain.QVMessageHolder;
import com.theice.mdf.client.qv.process.QVMDFClient;
import com.theice.mdf.client.qv.process.handlers.QVMarkerPriceIndexHandler;
import com.theice.mdf.client.util.MDFUtil;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.response.ErrorResponse;
import com.theice.mdf.message.notification.MarkerIndexPriceMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class QVMarkerPriceIndexModel extends AbstractTableModel implements MDSubscriber
{
    private MDFActivityListener _activityListener=null;
	private short _marketTypeCode=(-1);
	
    private Vector<MarkerIndexPriceMessage> _data=new Vector<MarkerIndexPriceMessage>();

    private Vector<MarkerIndexPriceMessage> _onDemandBuffer=new Vector<MarkerIndexPriceMessage>();
    private short _onDemandMessageSeqNo=0;

    /**
     * Initially the model operates in broadcast mode processing the notifications from the server
     * These message have a request id set to -1
     * 
     * If the mode is set to "on demand" we specifically initiate a request with a request id and 
     * start processing the matching id in the response
     */
    private boolean _onDemandMode=false;

    private int _requestId=(-1);
    
    public QVMarkerPriceIndexModel(short marketTypeCode, MDFActivityListener activityListener)
    {
        super();
        
        _marketTypeCode=marketTypeCode;
    	_activityListener=activityListener;
        
        init();
        
        refresh();
    }
    
    /**
     * refresh
     * 
     * when called, refresh data from the broadcast buffer for the specific market type
     * 
     * for on demand mode, the model is loaded from the interactive response notification matched by
     * both the market type code and the request id.
     */
    private void refresh()
    {
		if(_marketTypeCode<0)
    	{
    		return;
    	}
    	
		Map<Integer,MarkerIndexPriceMessage> map=QVMessageHolder.getInstance().getQVMarkerPriceMap(_marketTypeCode);
    	
    	if(map!=null)
    	{
    		_data.addAll(map.values());
        	fireTableDataChanged();
    	}
    	else
    	{
    		emptyData();
    	}
    }
    
    private void emptyData()
    {
		int size=_data.size();
		
		if(size>0)
		{
    		_data.clear();
    		fireTableRowsDeleted(0, size-1);
		}
    }

    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(QVMarkerPriceIndexColumn.columns.length);
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
        return(QVMarkerPriceIndexColumn.columns[col].getName()); 
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return(QVMarkerPriceIndexColumn.columns[columnIndex].getColumnClass());
    }

    public void init()
    {
    	QVMarkerPriceIndexHandler.getInstance().addSubscriber(this);
    }

    public void cleanup()
    {
    	QVMarkerPriceIndexHandler.getInstance().removeSubscriber(this);
    }
    
//    /**
//     * Trigger an on demand request
//     * Receive the responses through the notifications callback
//     */
//    public void triggerOnDemandRequest(short marketTypeCode)
//    {
//        if(_activityListener!=null)
//        {
//        	_activityListener.inProgress();
//        }
//        
//    	ErrorResponseHandler.getInstance().addSubscriber(this);
//    	
//    	synchronized(this)
//    	{
//        	_onDemandMode=true;
//            _marketTypeCode=marketTypeCode;
//            
//            _onDemandBuffer.clear();
//            _onDemandMessageSeqNo=0;
//
//        	QVMDFClient client=(QVMDFClient) AppManager.getInstance().getClient();        
//            _requestId=client.sendQVMarkerIndexPriceRequest(_marketTypeCode);
//    	}
//        
//        return;
//    }
    
    /**
     * Trigger a broadcast and refresh the internal vector from the application data structures
     */
    public synchronized void triggerBroadcast(short marketTypeCode)
    {
    	_onDemandMode=false;
        _marketTypeCode=marketTypeCode;
        _requestId=(-1);

    	refresh();
    	
    	return;
    }
    
    /**
     * Get Value At
     * @param row
     * @param col
     * @return
     */
    public synchronized Object getValueAt(int row, int col)
    {
        Object value=null;
        MarkerIndexPriceMessage data=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        try
        {
            data=(MarkerIndexPriceMessage) _data.get(row);
        }
        catch(Exception e)
        {
            System.err.println("MarkerIndexPriceMessage not found for row : "+row);
        }

        if(data!=null)
        {
            switch(col)
            {
                case QVMarkerPriceIndexColumn.COLID_MARKETID:
                    value=data.getMarketID();
                    break;
                case QVMarkerPriceIndexColumn.COLID_PRICE:
                    value=data.getPrice();
                    break;
                case QVMarkerPriceIndexColumn.COLID_SHORTNAME:
                    value=MessageUtil.toString(data.getShortName());
                    break;
                case QVMarkerPriceIndexColumn.COLID_PUBLISHED_DATETIME:
                    value=MDFUtil.simpleDateTimeFormatter.format(data.getPublishedDateTime());
                    break;
                case QVMarkerPriceIndexColumn.COLID_VALUTATION_DATE:
                    value=MessageUtil.toString(data.getEvaluationDate());
                    break;
                default:
                    value="???";
                    break;
            }
        }

        return(value);
    }

    /**
     * Sort on a given column ascending or descending
     * @param column
     * @param isAscending
     */
    public synchronized void sort(int column, boolean isAscending)
    {
    	Collections.sort(_data, new QVMarkerPriceIndexSorter(column,isAscending));
    	this.fireTableChanged(new TableModelEvent(this));
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
     * MD Subscriber interface method
     * @param message
     */
    public void notifyWithMDMessage(MDMessage message)
    {
    	char messageType=message.getMessageType();
  
		switch(messageType)
		{
			case RawMessageFactory.QVMarkerIndexPriceResponseType:

				MarkerIndexPriceMessage theMessage=(MarkerIndexPriceMessage) message;
    	
		    	if(_onDemandMode)
		    	{
//		    		if(theMessage.RequestSeqID==_requestId)
//		    		{
//		    			_onDemandBuffer.add(theMessage);
//		    			_onDemandMessageSeqNo++;
//		    			
//		    			if(_onDemandMessageSeqNo>=theMessage.getNumberOfMarkets())
//		    			{
//		    				synchronized(this)
//		    				{
//			    				emptyData();
//			    				_data.addAll(_onDemandBuffer);
//		    				}
//		
//					    	if(_activityListener!=null)
//		                    {
//		                    	_activityListener.completed();
//		                    }
//					    	
//	                    	ErrorResponseHandler.getInstance().removeSubscriber(this);
//					    	
//		                	fireTableDataChanged();
//		    			}
//		    		}
		    	}
		    	else
		    	{
//		    		if(theMessage.RequestSeqID==(-1))
//		    		{
//		    			synchronized(this)
//		    			{
//			        		refresh();
//		    			}
//		            	fireTableDataChanged();
//		    		}
		    	}
		    	
		    	break;
    	
			case RawMessageFactory.ErrorResponseType:
				ErrorResponse errorMessage=(ErrorResponse) message;
				
		    	if(!_onDemandMode)
		    	{
		    		break;
		    	}
		    	
				if(errorMessage.RequestSeqID==_requestId)
				{
			        if(_activityListener!=null)
			        {
			        	_activityListener.aborted();
			        }
		        	ErrorResponseHandler.getInstance().removeSubscriber(this);
				}
		    	
				break;
		}
		
    	return;
    }
    
    /**
     * Sorter
     * @author Adam Athimuthu
     */
    protected class QVMarkerPriceIndexSorter implements Comparator<MarkerIndexPriceMessage> 
    {
    	protected int _column;
    	protected boolean _isAscending;
    	
    	public QVMarkerPriceIndexSorter(int column,boolean isAscending)
    	{
    		_column=column;
    		_isAscending=isAscending;
    	}
    	
        public int compare(MarkerIndexPriceMessage first, MarkerIndexPriceMessage second)
        {
            int result=0;
            
            switch(_column)
            {
	            case QVMarkerPriceIndexColumn.COLID_MARKETID:
	            	result=first.getMarketID()-second.getMarketID();
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

