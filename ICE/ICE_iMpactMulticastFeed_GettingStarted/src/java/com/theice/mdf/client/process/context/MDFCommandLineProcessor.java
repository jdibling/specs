package com.theice.mdf.client.process.context;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MDFError;
import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.process.handlers.ErrorResponseHandler;
import com.theice.mdf.client.process.handlers.MarketStateChangeHandler;
import com.theice.mdf.client.process.handlers.OptionStrategyDefinitionHandler;
import com.theice.mdf.client.process.handlers.OptionsProductDefinitionHandler;
import com.theice.mdf.client.process.handlers.ProductDefinitionHandler;
import com.theice.mdf.client.process.handlers.SystemTextHandler;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.MarketStateChangeMessage;
import com.theice.mdf.message.notification.SystemTextMessage;
import com.theice.mdf.message.response.ErrorResponse;
import com.theice.mdf.message.response.OptionsProductDefinitionResponse;
import com.theice.mdf.message.response.ProductDefinitionResponse;

/**
 * 
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Command line processor that subscribes to the general messages
 * such as Error/SystemText etc.,
 * 
 * @author Adam Athimuthu
 */
public class MDFCommandLineProcessor implements MDSubscriber
{
	private static MDFCommandLineProcessor instance=new MDFCommandLineProcessor();

	private static final Logger logger=Logger.getLogger(MDFCommandLineProcessor.class.getName());

	private MDFCommandLineProcessor()
	{
	}
	
	public static MDFCommandLineProcessor getInstance() 
	{
		return(instance);
	}

	public void initialize()
	{
		System.out.println("MDFCommandLineProcessor initializing");
        ProductDefinitionHandler.getInstance().addSubscriber(this);
        OptionsProductDefinitionHandler.getInstance().addSubscriber(this);
        OptionStrategyDefinitionHandler.getInstance().addSubscriber(this);
        MarketStateChangeHandler.getInstance().addSubscriber(this);
        SystemTextHandler.getInstance().addSubscriber(this);
        ErrorResponseHandler.getInstance().addSubscriber(this);
	}

	public void shutdown()
	{
		System.out.println("MDFCommandLineProcessor cleaningup");
        ProductDefinitionHandler.getInstance().removeSubscriber(this);
        OptionsProductDefinitionHandler.getInstance().removeSubscriber(this);
        OptionStrategyDefinitionHandler.getInstance().removeSubscriber(this);
        MarketStateChangeHandler.getInstance().removeSubscriber(this);
        SystemTextHandler.getInstance().removeSubscriber(this);
        ErrorResponseHandler.getInstance().removeSubscriber(this);
	}

	public synchronized void notifyWithMDMessage(MDMessage message)
    {
    	char messageType=message.getMessageType();
      
        switch(messageType)
        {
		case RawMessageFactory.SystemTextMessageType:
	        SystemTextMessage systemTextMessage=(SystemTextMessage) message;
	        
	        StringBuffer textMessage=new StringBuffer("SystemMessage Received at : ");
	        textMessage.append(systemTextMessage.DateTime).append(" - ");
	        textMessage.append(MessageUtil.toString(systemTextMessage.Text));
	        textMessage.append(MessageUtil.toString(systemTextMessage.TextExtraFld));

	        if (logger.isDebugEnabled())
	        {
	           logger.debug(textMessage.toString());
	        }
			
			break;
			
		case RawMessageFactory.ErrorResponseType:
			
			final ErrorResponse errorMessage=(ErrorResponse) message;
			final MDFError errorCode=MDFError.getMDFError(errorMessage.Code);
			
	        StringBuffer errorBuffer=new StringBuffer("ErrorMessage Received : ");
	        errorBuffer.append(errorCode.getDescription()).append(" - ");
	        errorBuffer.append(MessageUtil.toString(errorMessage.Text));
	        
	        if (logger.isDebugEnabled())
           {
              logger.debug(errorBuffer.toString());
           }

			break;
			
        case RawMessageFactory.ProductDefinitionResponseType:
			final ProductDefinitionResponse productDefinitionMessage=(ProductDefinitionResponse) message;
	        StringBuffer productDefinitionBuffer=new StringBuffer("ProductDefinition Received for : ");
	        productDefinitionBuffer.append(productDefinitionMessage.MarketID);
	        if (logger.isDebugEnabled())
           {
              logger.debug(productDefinitionBuffer.toString());
           }
            break;

        case RawMessageFactory.OptionsProductDefinitionResponseType:
			final OptionsProductDefinitionResponse optionsProductDefinitionMessage=(OptionsProductDefinitionResponse) message;
	        StringBuffer optionsProductDefinitionBuffer=new StringBuffer("OptionsProductDefinition Received for : ");
	        optionsProductDefinitionBuffer.append(optionsProductDefinitionMessage.MarketID);
	        if (logger.isDebugEnabled())
           {
              logger.debug(optionsProductDefinitionBuffer.toString());
           }
            break;

        case RawMessageFactory.MarketStateChangeMessageType:
			final MarketStateChangeMessage marketStateChangeMessage=(MarketStateChangeMessage) message;
	        StringBuffer marketStateChangeBuffer=new StringBuffer("Market State for [");
	        marketStateChangeBuffer.append(marketStateChangeMessage.getMarketID()).append("] changed to [");
	        marketStateChangeBuffer.append(marketStateChangeMessage.TradingStatus).append("]");
	        if (logger.isDebugEnabled())
           {
              logger.debug(marketStateChangeBuffer.toString());
           }
            break;

        default:
			System.out.println("Notification received for unknown message type: "+ messageType);
			break;
        
        }
        
    	return;
    }
	
}

