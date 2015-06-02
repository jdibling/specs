package com.theice.mdf.client.process.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.NewOptionStrategyDefinitionMessage;
import com.theice.mdf.message.notification.NewOptionsMarketDefinitionMessage;
import com.theice.mdf.message.response.OptionStrategyDefinitionResponse;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;
import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.domain.MDPublisher;
import com.theice.mdf.client.domain.MDRawMessageBuffer;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.util.MDFUtil;

import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Abstract message handler implements the main handle() method. This handler uses the Template pattern
 * to implement the main algorithm for processing a given message. The message specific handleMessage() method
 * should be implemented by the concrete handlers that extend from this class.
 *
 * Realtime vs. delayed notifications - this handler also provides a publish/subscribe functionality
 * by implementing the MDPublisher interface. Any MDSubscribers that register with the handler, are notified
 * on updates. For delayed notifications, a timer is used to delay this notification
 * 
 * @author Adam Athimuthu
 * Date: Aug 2, 2007
 * Time: 5:00:35 PM
 * 
 */
public abstract class AbstractMarketMessageHandler implements MarketMessageHandler, MDPublisher
{
    /**
     * Subscribers List (no event filtering)
     */
    protected List<MDSubscriber> _subscribers=Collections.synchronizedList(new ArrayList<MDSubscriber>());

    /**
     * Map of subscribers list, interested in a specific market id event
     */
    protected Map<Integer,List<MDSubscriber>> _marketSubscribers=Collections.synchronizedMap(new HashMap<Integer,List<MDSubscriber>>());

    private final Logger logger=Logger.getLogger(AbstractMarketMessageHandler.class.getName());

    /**
     * Allow for realtime notifications, by default
     */
    protected boolean _realtimeNotification=true;

    /**
     * Timer for notifications
     */
    protected Timer _timer=null;
    protected TimerTask _task=null;

    /*
     * Manually change this property to simulate crossed book 
     */
    protected static final boolean SIMULATECROSSEDBOOK=false;

    protected AbstractMarketMessageHandler()
    {
    }

    /**
     * handle the incoming message and delegate to the concrete implementations
     * Notify all the subscribers if the mode allows for realtime notifications
     * Otherwise leave it to the timer to take care of delayed notifications
     * In this case the timer must have been started during handler initialization
     * @see com.theice.mdf.client.domain.MarketHandlerFactory
     * 
     * @param priceFeedMessage
     */
    public void handle(PriceFeedMessage priceFeedMessage)
    {
        handleMessage(priceFeedMessage);

    	MDMessage message=priceFeedMessage.getMessage();
    	
        /**
         * Keep every message in an internal rolling buffer
         */
        MDRawMessageBuffer.getInstance().updateLogBuffer(message);

        if(logger.isTraceEnabled())
        {
            logger.trace("Handle: "+message.toString());
        }

        /**
         * If a realtime notification is false, we assume that a timer is available for notifications
         * otherwise, we'll leave it to the application to determine if notifications are necessary
         */
        if(_realtimeNotification)
        {
            try
            {
                notifySubscribers(message);
                
                notifyMarketIdEventSubscribers(message);
            }
            catch(Throwable e)
            {
                logger.warn("Notify subscribers failed."+MDFUtil.getStackInfo(e));
            }
        }
    }

    /**
     * Abstract method to handle the message - to be overridden by the concrete handlers
     * Based on whether the message is sequenced or not, the PriceFeedMessage can contain
     * one of the MDSequencedMessage or a regular MDMessage. The implementations must
     * check to ensure they get the proper message that can be typecast to the respective
     * interfaces
     * 
     * Current known non-sequenced messages
     * 
     * ProductDefinition, HeartbeatMessage, ErrorMessage, DebugResponse, LoginResponse
     * HistoricalMarketDataResponse, TunnelProxyResponse
     * 
     * @param PriceFeedMessage priceFeedMessage
     */
    protected abstract void handleMessage(PriceFeedMessage priceFeedMessage);

    /**
     * Start the notification timer
     * Used for delayed notifications
     * If called, also reset the realtime notification flag
     * 
     * @param interval in milliseconds
     */
    public void startNotificationTimer(int interval)
    {
        _timer=new Timer();

        _task=new TimerTask()
            {
                public void run()
                {
                    try
                    {
                        notifySubscribers(null);
                    }
                    catch(Throwable e)
                    {
                        logger.warn("Notify subscribers failed."+MDFUtil.getStackInfo(e));
                    }
                }
            };

        _timer.scheduleAtFixedRate(_task, 0, interval);
        
        _realtimeNotification=false;

        return;
    }

    /**
     * Cancel the timer if one exists
     * Used for delayed notifications
     */
    public void clearNotificationTimer()
    {
        if(_timer!=null)
        {
            _timer.cancel();

            _timer=null;
            _task=null;
        }

        return;
    }

    /**
     * if called, all notifications are turned off. Such handlers will not perform any
     * subscriber notifications. Any clients willing to get updates, will have to pull data from
     * the domain data structures.
     */
    public void switchOffNotifications()
    {
        clearNotificationTimer();
        _realtimeNotification=false;
    }

    /**
     * add subscriber
     * @param subscriber
     */
    public void addSubscriber(MDSubscriber subscriber)
    {
        this._subscribers.add(subscriber);
    }

    /**
     * remove subscriber 
     * @param subscriber
     */
    public void removeSubscriber(MDSubscriber subscriber)
    {
        this._subscribers.remove(subscriber);
    }

    public void addEventSubscriber(MDSubscriber subscriber,Integer marketId)
    {
    	synchronized(this._marketSubscribers)
    	{
        	List<MDSubscriber> eventSubscribers=this._marketSubscribers.get(marketId);
        	
        	if(eventSubscribers==null)
        	{
        		eventSubscribers=new ArrayList<MDSubscriber>();
        	}
        	
        	eventSubscribers.add(subscriber);
        	
        	if(logger.isTraceEnabled())
        	{
            	logger.trace("Subscribing to event : "+marketId+" Subscriber="+subscriber.hashCode());
        	}
        	
        	this._marketSubscribers.put(marketId, eventSubscribers);
    	}
    	
    	return;
    }
    
    public void removeEventSubscriber(MDSubscriber subscriber,Integer marketId)
    {
    	synchronized(this._marketSubscribers)
    	{
        	List<MDSubscriber> eventSubscribers=this._marketSubscribers.get(marketId);
        	
        	if(eventSubscribers!=null)
        	{
        		if(logger.isTraceEnabled())
        		{
                	logger.trace("Unsubscribing from event : "+marketId+" Subscriber="+subscriber.hashCode());
        		}
            	
        		if(eventSubscribers.remove(subscriber))
        		{
        			if(logger.isTraceEnabled())
        			{
        				logger.trace("Sucessfully unsubscribed from event : "+marketId+" successful. Subscriber="+subscriber.hashCode());
        			}
        		}
        		else
        		{
                	logger.error("Unsubscribing from event : "+marketId+" No such subscriber="+subscriber.hashCode());
        		}
        	}
        	else
        	{
        		logger.error("Unsubscribing from event : "+marketId+" No subscribers have registered with this event : "+_marketSubscribers.toString());
        	}
    	}
    }

    /**
     * notify subscribers
     * @param message
     */
    private void notifySubscribers(MDMessage message) throws Exception
    {
        for(int index=0;index<_subscribers.size();index++)
        {
            MDSubscriber subscriber=(MDSubscriber) _subscribers.get(index);

            try
            {
                subscriber.notifyWithMDMessage(message);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.warn("Notification failed. Subscriber="+subscriber.toString()+" Exception: "+e.toString());
                throw(e);
            }
        }

        return;
    }

    /**
     * notify subscribers of a specific market
     * @param message
     */
    private void notifyMarketIdEventSubscribers(MDMessage message) throws Exception
    {
    	Integer underlyingMarketId=Integer.valueOf(message.getMarketID());
    	if (message instanceof NewOptionStrategyDefinitionMessage)
    	{
    	   NewOptionStrategyDefinitionMessage newUDSMsg = (NewOptionStrategyDefinitionMessage)message;
    	   underlyingMarketId = newUDSMsg.UnderlyingMarketID;
    	}
    	else if (message instanceof OptionStrategyDefinitionResponse)
    	{
    	   OptionStrategyDefinitionResponse udsDef = (OptionStrategyDefinitionResponse)message;
    	   underlyingMarketId = udsDef.UnderlyingMarketID;
    	}
    	else if (message instanceof OptionsProductDefinitionResponse)
    	{
    	   OptionsProductDefinitionResponse optionsProductDef = (OptionsProductDefinitionResponse)message;
    	   underlyingMarketId = optionsProductDef.UnderlyingMarketID;
    	}
    	else if (message instanceof NewOptionsMarketDefinitionMessage)
    	{
    	   NewOptionsMarketDefinitionMessage flexOptionsDef = (NewOptionsMarketDefinitionMessage)message;
    	   underlyingMarketId = flexOptionsDef.UnderlyingMarketID;
    	}
    	
    	List<MDSubscriber> eventSubscribers=this._marketSubscribers.get(underlyingMarketId);
    	
    	if(eventSubscribers==null)
    	{
    		if(logger.isTraceEnabled())
    		{
                logger.trace("No event subscribers for="+underlyingMarketId);
    		}
    		
    		return;
    	}
    	
        for(int index=0;index<eventSubscribers.size();index++)
        {
            MDSubscriber subscriber=(MDSubscriber) eventSubscribers.get(index);

            try
            {
                subscriber.notifyWithMDMessage(message);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.warn("Notification failed. Subscriber="+subscriber.toString()+" Exception: "+e.toString());
                throw(e);
            }
        }

        return;
    }
    
}

