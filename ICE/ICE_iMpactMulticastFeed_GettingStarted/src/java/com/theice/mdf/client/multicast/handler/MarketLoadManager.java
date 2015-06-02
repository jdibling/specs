package com.theice.mdf.client.multicast.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.domain.state.MarketLoadStatus;
import com.theice.mdf.client.domain.state.MarketLoadTrackingKey;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.handlers.ErrorResponseHandler;
import com.theice.mdf.client.process.handlers.OptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.handlers.OptionsProductDefinitionHandler;
import com.theice.mdf.client.process.handlers.ProductDefinitionHandler;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.request.ProductDefinitionRequest;
import com.theice.mdf.message.response.ErrorResponse;
import com.theice.mdf.message.response.OptionStrategyDefinitionResponse;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;
import com.theice.mdf.message.response.ProductDefinitionResponse;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Mutex : load status
 * Consumer : Multicast Client(s)
 * 
 * @author Adam Athimuthu
 */
public class MarketLoadManager implements MDSubscriber
{
    private final Logger logger=Logger.getLogger(MarketLoadManager.class.getName());
    
    protected static MarketLoadManager _instance = new MarketLoadManager();
    
    /**
     * Key: LoadTrackingKey (MarketType+SecurityType)
     * Value: MarketTypeLoadStatus
     */
    protected Map<MarketLoadTrackingKey,MarketLoadStatus> _loadPending=new HashMap<MarketLoadTrackingKey,MarketLoadStatus>();
    protected Map<MarketLoadTrackingKey,MarketLoadStatus> _loadCompleted=new HashMap<MarketLoadTrackingKey,MarketLoadStatus>();
    protected Map<MarketLoadTrackingKey,MarketLoadStatus> _loadError=new HashMap<MarketLoadTrackingKey,MarketLoadStatus>();

    /**
     * map used for associating the request id to the market type, mainly for processing errors
     * Key: Request ID
     * Value: LoadTrackingKey (MarketType+SecurityType)
     */
    protected Map<Integer,MarketLoadTrackingKey> _requestMarketTypeAssociation=new HashMap<Integer,MarketLoadTrackingKey>();

    public static final int LOAD_COMPLETED=0;
    public static final int LOAD_PENDING=1;
    public static final int LOAD_FAILED=2;
    
    protected Integer _loadingStatus=new Integer(LOAD_PENDING); 

    protected Object _loadMutex=new Object(); 

    public static MarketLoadManager getInstance()
    {
        return(_instance);
    }
    
    protected MarketLoadManager()
    {
    	ProductDefinitionHandler.getInstance().addSubscriber(this);
    	OptionsProductDefinitionHandler.getInstance().addSubscriber(this);
    	OptionStrategyDefinitionHandler.getInstance().addSubscriber(this);
    	ErrorResponseHandler.getInstance().addSubscriber(this);
    }

    public Integer getLoadingStatus()
    {
    	return(_loadingStatus);
    }

    public void resetMarketLoadStatus()
    {
       synchronized(_loadPending)
       {
          _loadPending.clear();
          _loadCompleted.clear();
          _loadError.clear();
       }
    }
    
    /**
     * create load status for the given specific market type and security type (Futures or Options)
     * 
     * @param productDefinitionRequest
     * @return true, if successfully registered. false, if this is a duplicate request for the market type
     */
    public boolean registerLoadRequest(ProductDefinitionRequest productDefinitionRequest)
    {
    	short marketType=productDefinitionRequest.MarketType;
    	
    	synchronized(_loadPending)
    	{
        	MarketLoadTrackingKey loadTrackingKey=new MarketLoadTrackingKey(marketType,productDefinitionRequest.SecurityType);

        	if(_loadPending.get(loadTrackingKey)!=null)
    		{
    			logger.warn("Duplicate request for : "+loadTrackingKey.toString());
    			return(false);
    		}

        	MarketLoadStatus status=new MarketLoadStatus(loadTrackingKey);
        	status.setRequestSequenceId(productDefinitionRequest.RequestSeqID);

        	_loadPending.put(loadTrackingKey,status);

        	_requestMarketTypeAssociation.put(Integer.valueOf(productDefinitionRequest.RequestSeqID),loadTrackingKey);        	
    	}
    	
    	return(true);
    }
    
    /**
     * Check the pending queue and the completed/error queues and market the load status accordingly
     * 
     * If there are items still in the pending queue, leave the status as load pending
     * 
     * Otherwise, depending on what is in the completed/error queues, set the status accordingly and
     * notify the waiters
     */
    protected void updateLoadStatus()
    {
		if(_loadPending.size()==0)
		{
			if(_loadCompleted.size()>0)
			{
				_loadingStatus=LOAD_COMPLETED;
	    		
	        	//AppManager.markMarketDataLoadCompletion();
	        	
				logger.info("Load status set to COMPLETED");
			}
			else
			{
				_loadingStatus=LOAD_FAILED;
				logger.info("Load status set to FAILED");
			}
			
	    	ProductDefinitionHandler.getInstance().removeSubscriber(this);
	    	OptionsProductDefinitionHandler.getInstance().removeSubscriber(this);
	    	OptionStrategyDefinitionHandler.getInstance().removeSubscriber(this);
	    	ErrorResponseHandler.getInstance().removeSubscriber(this);
	    	
	    	synchronized(_loadMutex)
	    	{
		    	_loadMutex.notifyAll();
	    	}
	    	
	    	//close TCP connection
	    	AppManager.getClient().logoutAndCloseSocket();
		}
    	
    	return;
    }

    /**
     * return mutex
     * @return
     */
    public Object getMutex()
    {
    	return(_loadMutex);
    }
    
    /**
     * Based on the product definition, update the market load status
     * for the particular market type
     * 
     * If we are done with loading all markets for a specific market type, then move the market type
     * to a complete status
     * 
     * @param productDefinition
     */
    protected void processLoadStatus(ProductDefinitionResponse productDefinition)
    {
    	synchronized(_loadPending)
    	{
        	short marketType=productDefinition.RequestMarketType;
        	
        	MarketLoadTrackingKey loadTrackingKey=new MarketLoadTrackingKey(marketType,ProductDefinitionRequest.SECURITY_TYPE_FUTRES_OTC);

        	MarketLoadStatus status=_loadPending.get(loadTrackingKey);
        	
        	if(status==null)
        	{
        		logger.warn("Market type load status not found for : "+loadTrackingKey.toString());
        		return;
        	}
        	
        	status.setNumberOfMarketsExpected(productDefinition.NumOfMarkets);
        	
        	if(status.incrementNumberOfMarketsLoaded()==productDefinition.NumOfMarkets)
        	{
        		logger.info("MarketLoadManager: All markets loaded for : "+marketType);
        		
        		_loadCompleted.put(loadTrackingKey,_loadPending.remove(loadTrackingKey));
        		
        		updateLoadStatus();
        	}
    	}
    	
    	return;
    }
    
    /**
     * Based on the options product definition, update the market load status
     * for the particular market type
     * 
     * If we are done with loading all markets for a specific market type, then move the market type
     * to a complete status
     * 
     * @param optionsProductDefinition
     */
    protected void processLoadStatus(OptionsProductDefinitionResponse optionsProductDefinition)
    {
    	synchronized(_loadPending)
    	{
        	short marketType=optionsProductDefinition.RequestMarketType;
        	
        	MarketLoadTrackingKey loadTrackingKey=new MarketLoadTrackingKey(marketType,ProductDefinitionRequest.SECURITY_TYPE_OPTION);

        	MarketLoadStatus status=_loadPending.get(loadTrackingKey);
        	
        	if(status==null)
        	{
        		logger.warn("Market type load status not found for : "+loadTrackingKey.toString());
        		return;
        	}
        	
        	status.setNumberOfMarketsExpected(optionsProductDefinition.NumOfMarkets);
        	
        	if(status.incrementNumberOfMarketsLoaded()==optionsProductDefinition.NumOfMarkets)
        	{
        		logger.info("MarketLoadManager: All markets loaded : "+loadTrackingKey.toString());
        		
        		_loadCompleted.put(loadTrackingKey,_loadPending.remove(loadTrackingKey));
        		
        		updateLoadStatus();
        	}
    	}
    	
    	return;
    }

    protected void processLoadStatus(OptionStrategyDefinitionResponse udsProductDefinition)
    {
      synchronized(_loadPending)
      {
         short marketType=udsProductDefinition.RequestMarketType;
         
         MarketLoadTrackingKey loadTrackingKey=new MarketLoadTrackingKey(marketType,ProductDefinitionRequest.SECURITY_TYPE_UDS_OPTIONS);

         MarketLoadStatus status=_loadPending.get(loadTrackingKey);
         
         if(status==null)
         {
            logger.warn("Market type load status not found for : "+loadTrackingKey.toString());
            return;
         }
         
         status.setNumberOfMarketsExpected(udsProductDefinition.NumOfMarkets);
         
         if(status.incrementNumberOfMarketsLoaded()==udsProductDefinition.NumOfMarkets)
         {
            logger.info("MarketLoadManager: All markets loaded : "+loadTrackingKey.toString());
            
            _loadCompleted.put(loadTrackingKey,_loadPending.remove(loadTrackingKey));
            
            updateLoadStatus();
         }
      }
      
      return;
    }
    /**
     *
     * If we received an error for a specific product defintion response, then we set the
     * status of the load accordingly
     *  
     * @param error response
     */
    protected void processLoadStatus(ErrorResponse errorResponse)
    {
    	synchronized(_loadPending)
    	{
        	Integer sequenceId=Integer.valueOf(errorResponse.RequestSeqID);
        	
        	MarketLoadTrackingKey loadTrackingKey=_requestMarketTypeAssociation.get(sequenceId);
        	
        	if(loadTrackingKey==null)
        	{
        		logger.warn("Unable to lookup Sequence. Error probably unrelated: "+sequenceId);
        		return;
        	}
        	
        	short marketType=loadTrackingKey.getMarketType();

        	MarketLoadStatus status=_loadPending.get(loadTrackingKey);
        	
        	if(status==null)
        	{
        		logger.warn("processLoadStatus : Load status not found for market type : "+marketType);
        		return;
        	}
        	
        	_loadError.put(loadTrackingKey, _loadPending.remove(loadTrackingKey));

        	updateLoadStatus();
    	}
    	
    	return;
    }

    /**
     * Call back for ProductDefinition/OptionsProductDefinitionResponse/Error handler subscriptions
     * 
     * @MDMessage 
     */
    public void notifyWithMDMessage(MDMessage message)
    {
    	switch(message.getMessageType())
    	{
	    	case RawMessageFactory.ProductDefinitionResponseType:
	    		if(logger.isTraceEnabled())
	    		{
	        		logger.trace("Market Load Manager : Processing product defintion for : "+message.getMarketID());
	    		}
	    		processLoadStatus((ProductDefinitionResponse) message);
	    		break;
	    		
	    	case RawMessageFactory.OptionsProductDefinitionResponseType:
	    		if(logger.isTraceEnabled())
	    		{
	        		logger.trace("Market Load Manager : Processing options product defintion for : "+message.getMarketID());
	    		}
	    		processLoadStatus((OptionsProductDefinitionResponse) message);
	    		break;
	      
	    	case RawMessageFactory.OptionStrategyDefinitionResponseType:
            if(logger.isTraceEnabled())
            {
               logger.trace("Market Load Manager : Processing options product defintion for : "+message.getMarketID());
            }
            processLoadStatus((OptionStrategyDefinitionResponse) message);
            break;
	    		
	    	case RawMessageFactory.ErrorResponseType:
	    		if(logger.isTraceEnabled())
	    		{
	        		logger.trace("Market Load Manager : Processing error for : "+message.getMarketID());
	    		}
	    		processLoadStatus((ErrorResponse) message);
	    		break;
    	}

    	return;
    }
    
}

