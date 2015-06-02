package com.theice.mdf.client.process.handlers;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.DeleteOrderMessage;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientRuntimeParameters;
import com.theice.mdf.client.domain.Market;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.transaction.DeleteTransaction;
import com.theice.mdf.client.examples.SimpleClientConfigurator;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * This handler processes all the Delete Order messages. Based on the market id, it removes the specific
 * order from the internal collections. Based on the side, the bid/offer collections are updated along with the
 * price levels.
 *
 * Context: FullOrderDepth
 * Markets: Futures/OTC only (No Option Markets, as they don't support FullOrderDepth)
 * -----------------------
 * 
 * The DeleteOrder Handler is applicable only during the fullorder depth processing contexts.
 * During PriceLevel processing, we don't get this message. Instead, a DeletePriceLevel is sent.
 * Since options markets don't support full order depth, this handler doesn't apply for options
 * 
 * @see FullOrderDepthMulticasterDispatcher and associated factories
 * 
 * User: Adam Athimuthu
 * Date: Aug 10, 2007
 * Time: 2:55:59 PM
 */
public class DeleteOrderHandler extends AbstractMarketMessageHandler
{
    private static DeleteOrderHandler _instance = new DeleteOrderHandler();

    private static final Logger logger=Logger.getLogger(DeleteOrderHandler.class.getName());
    private static final Logger ordActiveTimeLogger=Logger.getLogger("com.theice.mdf.client.domain.marketstate.ordActiveTimeLogger");
    private static final boolean logBestPrice = MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters().isBestPriceLogEnabled();
    private static final char SEPARATOR = ',';
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

    public static DeleteOrderHandler getInstance()
    {
        return _instance;
    }

    private DeleteOrderHandler()
    {
       dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
        DeleteOrderMessage theMessage=null;
        MarketOrder removedOrder=null;

        if(logger.isTraceEnabled())
        {
	        logger.trace("DeleteOrderHandler.handleMessage() : Entering +["+message.toString()+"]");
        }

        try
        {
            theMessage=(DeleteOrderMessage) message;

            Market market=(Market) MarketsHolder.getInstance().findMarket(theMessage.getMarketID());

            if(market!=null)
            {
            	long bundleSequenceNumber=0L;
            	
                if(priceFeedMessage.isBundled())
                {
                	bundleSequenceNumber=priceFeedMessage.getBundleSequenceNumber();
                }

                DeleteTransaction transaction=new DeleteTransaction(theMessage.getMarketID(),theMessage.OrderID,bundleSequenceNumber);
                removedOrder=market.removeOrder(theMessage.OrderID,transaction);
                
                if (removedOrder!=null && logBestPrice)
                {
                   logOrderActiveTime(removedOrder);
                }
                
                if (removedOrder==null)
                {
                   logger.warn("DeleteOrderMessageHandler: did not find this order in the book "+theMessage);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Failure processing DeleteOrder: "+e.toString());
        }

        return;
    }
    
    protected void logOrderActiveTime(MarketOrder removedOrder)
    {
       long removedDateTime=removedOrder.getDateTimeRemovedFromBook();
       StringBuilder strBuffer = new StringBuilder(dateTimeFormatter.format(removedDateTime));
       strBuffer.append(SEPARATOR).append(removedOrder.getMarketID()).
       append(SEPARATOR).append(removedOrder.getOrderID()).
       append(SEPARATOR).append(removedDateTime-removedOrder.getDateTimeAddedToBook());

       ordActiveTimeLogger.log(Level.INFO, strBuffer.toString());
    }

}

