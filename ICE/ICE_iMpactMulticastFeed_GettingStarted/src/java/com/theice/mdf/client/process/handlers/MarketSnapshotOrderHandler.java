package com.theice.mdf.client.process.handlers;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * This handler processes the market snapshort order messages. These messages are used to build
 * the initial book for each market. For each message, we build the order and add it to the internal
 * orders' collection. Based on the Side, these orders are also added to the bid or offers collection.
 *
 * @author Adam Athimuthu
 * Date: Aug 3, 2007
 * Time: 11:23:40 AM
 *
 */
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.notification.MarketSnapshotOrderMessage;
import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.domain.MarketInterface;
import com.theice.mdf.client.domain.MarketsHolder;
import com.theice.mdf.client.domain.MarketOrder;
import com.theice.mdf.client.domain.book.FullOrderDepthBookKeeper;
import com.theice.mdf.client.domain.transaction.AddModifyTransaction;
import com.theice.mdf.client.message.PriceFeedMessage;

import org.apache.log4j.Logger;

/**
 * This handler is used for processing the Snapshot Order messages.
 * 
 * Context: FullOrderDepth
 * Channels: Snapshot and Incremental
 * Markets: Futures/OTC only (No Option Markets, as they don't support FullOrderDepth)
 * 
 * <p/> THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES
 * ONLY. THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * @see FullOrderDepthMulticasterDispatcher and associated factories
 * @see AddModifyOrderHandler
 */
public class MarketSnapshotOrderHandler extends AbstractMarketMessageHandler
{
    private static MarketSnapshotOrderHandler _instance = new MarketSnapshotOrderHandler();

    private static final Logger logger=Logger.getLogger(MarketSnapshotOrderHandler.class.getName());

    public static MarketSnapshotOrderHandler getInstance()
    {
        return _instance;
    }

    private MarketSnapshotOrderHandler()
    {
    }

    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
        MarketSnapshotOrderMessage theMessage=null;

        if(logger.isTraceEnabled())
        {
            logger.trace("MarketSnapshotOrder.handleMessage() : ["+message.toString()+"]");
        }

        /**
         * Get the correct market and store the bid/offer in the map
         */
        try
        {
            theMessage=(MarketSnapshotOrderMessage) message;

            /**
             * Filter out RFQ orders
             */
            if(theMessage.IsRFQ=='Y')
            {
                if(logger.isDebugEnabled())
                {
                	logger.debug("Filtering out RFQ Order.");
                }
                return;
            }

            MarketInterface market=MarketsHolder.getInstance().findMarket(theMessage.getMarketID());

            MarketOrder marketOrder=new MarketOrder(
            		theMessage.getMarketID(),
            		theMessage.OrderID,
            		theMessage.OrderSeqID,
            		theMessage.Side,
            		theMessage.Price,
            		theMessage.Quantity,
            		theMessage.Implied,
            		theMessage.IsRFQ,
            		theMessage.DateTime,
            		theMessage.getMessageType()==MDFClientConfigurator.getMsgTypeForMSSOrder(),
            		theMessage.SequenceWithinMillis);

            /**
             * mark this order as SNAPSHOT
             */
            marketOrder.setAsSnapshotOrder();
            
            ((FullOrderDepthBookKeeper) market).addOrder(new AddModifyTransaction(marketOrder));

        }
        catch(Exception e)
        {
            logger.error("Failure processing MarketSnapshot message: "+e.getMessage(), e);
        }
    }
}

