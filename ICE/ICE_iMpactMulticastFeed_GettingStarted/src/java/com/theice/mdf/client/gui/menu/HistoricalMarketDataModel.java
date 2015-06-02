package com.theice.mdf.client.gui.menu;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import org.apache.log4j.Logger;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.exception.ProcessingException;
import com.theice.mdf.client.gui.listeners.MDFActivityListener;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.MDFClient;
import com.theice.mdf.client.process.handlers.BypassMessageHandler;
import com.theice.mdf.client.process.handlers.ErrorResponseHandler;
import com.theice.mdf.client.process.handlers.HistoricalMarketDataHandler;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MDSequencedMessage;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.response.ErrorResponse;
import com.theice.mdf.message.response.HistoricalMarketDataResponse;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class HistoricalMarketDataModel extends AbstractTableModel implements MDSubscriber
{
    private final Logger logger=Logger.getLogger(HistoricalMarketDataModel.class.getName());

    private MDFActivityListener _activityListener=null;
	private Vector<MDMessage> _data=new Vector<MDMessage>();

    private int _requestId=(-1);
    
    private Vector<MDMessage> _onDemandBuffer=new Vector<MDMessage>();
    private int _expectedNumberOfMessages=0;
    private int _currentSeq=0;

    public HistoricalMarketDataModel(MDFActivityListener activityListener)
    {
        super();
    	_activityListener=activityListener;
    }
    
    /**
     * Get Column Count
     * @return
     */
    public int getColumnCount()
    {
        return(HistoricalMarketDataColumn.columns.length);
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
        return(HistoricalMarketDataColumn.columns[col].getName()); 
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return(HistoricalMarketDataColumn.columns[columnIndex].getColumnClass());
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
    public synchronized Object getValueAt(int row, int col)
    {
        Object value=null;
        MDMessage data=null;

        if(row<0 || col<0)
        {
            return("N/A");
        }

        try
        {
            data=(MDMessage) _data.get(row);
        }
        catch(Exception e)
        {
            System.err.println("Historical Market Data (MDMessage) not found for row : "+row);
        }

        if(data!=null)
        {
            switch(col)
            {
                case HistoricalMarketDataColumn.COLID_MARKETID:
                    value=data.getMarketID();
                    break;
                case HistoricalMarketDataColumn.COLID_MESSAGESTRING:
                    value=data.toString();
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
     * Trigger an on demand request
     * Receive the responses through the notifications callback
     * @param session
     * @param group
     * @param port
     * @param startSeq
     * @param endSeq
     */
    public void triggerOnDemandRequest(short session, String group, short port, int startSeq, int endSeq)
    {
        if(_activityListener!=null)
        {
        	_activityListener.inProgress();
        }

    	ErrorResponseHandler.getInstance().addSubscriber(this);

        synchronized(this)
        {
			emptyData();

            _onDemandBuffer.clear();
            _expectedNumberOfMessages=0;
            _currentSeq=startSeq;
            
            MDFClient client=(MDFClient) AppManager.getClient();

        	try
        	{
        		HistoricalMarketDataHandler.getInstance().addSubscriber(this);
        		BypassMessageHandler.getInstance().addSubscriber(this);
        		
                _requestId=client.sendHistoricalMarketDataRequest(session, group, port, startSeq, endSeq);
        	}
        	catch(ProcessingException e)
        	{
        		System.out.println("Exception while requesting historical market data information : "+e.toString());
        	}
            
        }
        
        return;
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
     * Sort on a given column ascending or descending
     * @param column
     * @param isAscending
     */
    public synchronized void sort(int column, boolean isAscending)
    {
    	Collections.sort(_data, new MDMessageSorter(column,isAscending));
    	this.fireTableChanged(new TableModelEvent(this));
    }

    /**
     * MD Subscriber interface method
     * @param message
     */
    public void notifyWithMDMessage(MDMessage message)
    {
    	char messageType=message.getMessageType();
		StringBuffer buf=new StringBuffer();
  
		switch(messageType)
		{
			case RawMessageFactory.HistoricalMarketDataResponseType:
				/**
				 * This marks the start of the historical data message
				 * If at least one message is available, we'll get this summary message with the NumberOfMessages
				 * field set. Then we move on to receiving the number of messages indicated by this field
				 * until we are done
				 */
		    	HistoricalMarketDataResponse theMessage=(HistoricalMarketDataResponse) message;
		    	
	    		if(theMessage.RequestSeqID==_requestId)
	    		{
	    			_expectedNumberOfMessages=theMessage.getNumberOfMessages();
	    			
	    			if(logger.isDebugEnabled())
	    			{
						buf=new StringBuffer();
						buf.append("### Begin Historical Response. Expected Number of messages : ");
						buf.append(_expectedNumberOfMessages).append(" ###");
						logger.debug(buf.toString());
	    			}
					
//	    			if(_expectedNumberOfMessages>=0)
//	    			{
//	    				synchronized(this)
//	    				{
//		    				_data.addAll(_onDemandBuffer);
//	    				}
//	
//				    	if(_activityListener!=null)
//	                    {
//	                    	_activityListener.completed();
//	                    }
//				    	
//						HistoricalMarketDataHandler.getInstance().removeSubscriber(this);
//				    	
//	                	fireTableDataChanged();
//	    			}
	    		}
	    		
	    		break;

			case RawMessageFactory.ErrorResponseType:
				ErrorResponse errorMessage=(ErrorResponse) message;
				
				if(errorMessage.RequestSeqID==_requestId)
				{
			        if(_activityListener!=null)
			        {
			        	_activityListener.aborted();
			        }
		        	ErrorResponseHandler.getInstance().removeSubscriber(this);
					HistoricalMarketDataHandler.getInstance().removeSubscriber(this);
					BypassMessageHandler.getInstance().removeSubscriber(this);
				}
		    	
				break;
				
			default:
				/**
				 * We can expect pretty much "ANY" message type, including order, trade or price level aggregation
				 * messages, since this is historic data and we are trying to get the raw data displayed
				 * We'll just capture and do a toString...
				 * 
				 */
    			_onDemandBuffer.add(message);
    			_expectedNumberOfMessages--;
    			
				if(logger.isDebugEnabled())
				{
					buf=new StringBuffer();
					buf.append("Historical Response : ").append(_currentSeq).append(" : ").append(message);
					buf.append(" [Remaining : ").append(_expectedNumberOfMessages).append("]");
					logger.debug(buf.toString());
				}

				/**
				 * Fix the sequence number field and populate from start to end sequentially
				 * All the raw messages that we receive are supposed to be sequenced, so we can
				 * safely type cast them to a Sequenced Message for fixing the sequence number
				 */
				if(message instanceof MDSequencedMessage)
				{
					((MDSequencedMessage) message).setSequenceNumber(_currentSeq);
					_currentSeq++;
				}
				else
				{
					buf=new StringBuffer();
					buf.append("Historical message received is not a SequencedMessage : ");
					buf.append(message.toString());
					logger.warn(buf.toString());
				}

				if(_expectedNumberOfMessages<=0)
    			{
					if(logger.isDebugEnabled())
					{
						buf=new StringBuffer();
						buf.append("### End Historical Response ###");
						logger.debug(buf.toString());
					}

					synchronized(this)
    				{
	    				_data.addAll(_onDemandBuffer);
    				}

			    	if(_activityListener!=null)
                    {
                    	_activityListener.completed();
                    }
			    	
					HistoricalMarketDataHandler.getInstance().removeSubscriber(this);
					BypassMessageHandler.getInstance().removeSubscriber(this);
		        	ErrorResponseHandler.getInstance().removeSubscriber(this);
			    	
                	fireTableDataChanged();
	    		}
				break;
		}
		
		return;
    }

    /**
     * Sorter
     * @author Adam Athimuthu
     */
    protected class MDMessageSorter implements Comparator<MDMessage> 
    {
    	protected int _column;
    	protected boolean _isAscending;
    	
    	public MDMessageSorter(int column,boolean isAscending)
    	{
    		_column=column;
    		_isAscending=isAscending;
    	}
    	
        public int compare(MDMessage first, MDMessage second)
        {
            int result=0;
            
            switch(_column)
            {
	            case HistoricalMarketDataColumn.COLID_MARKETID:
	            	result=first.getMarketID()-second.getMarketID();
	            	break;
	            
	            default:
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

