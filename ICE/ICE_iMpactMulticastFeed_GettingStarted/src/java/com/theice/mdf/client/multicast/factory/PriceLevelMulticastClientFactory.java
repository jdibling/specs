package com.theice.mdf.client.multicast.factory;

import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.multicast.MDFMulticastClient;
import com.theice.mdf.client.multicast.dispatcher.PriceLevelMulticastDispatcher;
import com.theice.mdf.client.multicast.dispatcher.PriceLevelSnapshotMulticastDispatcher;
import com.theice.mdf.client.multicast.dispatcher.MDFMulticastDispatcher;
import com.theice.mdf.client.multicast.handler.MulticastClientConfig;
import com.theice.mdf.client.process.AppManager;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * <p/>
 *
 * Creates the incremental and snapshot multicast configurations for a given stream
 * Used by the AppManager
 * 
 * @author Adam Athimuthu
 */
public class PriceLevelMulticastClientFactory implements MulticastClientFactory 
{
    private Logger logger=Logger.getLogger(PriceLevelMulticastClientFactory.class.getName());

    private static PriceLevelMulticastClientFactory _instance=new PriceLevelMulticastClientFactory();

    public static PriceLevelMulticastClientFactory getInstance()
    {
        return(_instance);
    }

    protected PriceLevelMulticastClientFactory()
    {
    }

    /**
     * create incremental multicast client
     * @return
     * @throws InitializationException
     */
	public MDFMulticastClient createIncrementalMulticastClient(String multicastGroupName) throws InitializationException
	{
    	MDFMulticastDispatcher dispatcher=null;
    	MulticastClientConfig multicastClientConfig=null;
    	MDFMulticastClient multicastClient=null;
    	String clientName=null;
    	
    	try
    	{
        	clientName="PriceLevel";

        	System.out.println("Creating MulticastClient : "+clientName);
        	
        	dispatcher=new PriceLevelMulticastDispatcher(multicastGroupName, AppManager.getInstance(multicastGroupName).getMessageHandlerFactory());
        	
        	MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
        	
        	multicastClientConfig=new MulticastClientConfig(configuration.getMulticastChannelPairInfo(multicastGroupName).getLiveEndPoint(),
        			configuration.getMDFClientRuntimeParameters().getMulticastNetworkInterface(),clientName,dispatcher);
        	
        	multicastClient=new MDFMulticastClient(multicastClientConfig, multicastGroupName, false);
    	}
    	catch(Throwable t)
    	{
    		logger.error(t.getMessage());
    		throw(new InitializationException("Exception duirng createIncrementalMulticastClient of PriceLevel : ",t));
    	}
    	
    	return(multicastClient);
	}
	
    /**
     * create snapshot multicast client
     * @return
     * @throws InitializationException
     */
	public MDFMulticastClient createSnapshotMulticastClient(String multicastGroupName) throws InitializationException
	{
    	MDFMulticastDispatcher dispatcher=null;
    	MulticastClientConfig multicastClientConfig=null;
    	MDFMulticastClient snapshotMulticastClient=null;
    	String clientName=null;

    	try
		{
	    	clientName="PriceLevelSnapshot";

        	System.out.println("Creating Incremental MulticastClient : "+clientName);
        	
	    	dispatcher=new PriceLevelSnapshotMulticastDispatcher(multicastGroupName, new CountDownLatch(1));
	    	
        	MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
        	
	    	multicastClientConfig=new MulticastClientConfig(configuration.getMulticastChannelPairInfo(multicastGroupName).getSnapshotEndPoint(),
	    			configuration.getMDFClientRuntimeParameters().getMulticastNetworkInterface(),clientName,dispatcher);
	    	
	    	snapshotMulticastClient=new MDFMulticastClient(multicastClientConfig, multicastGroupName, true);
		}
    	catch(Throwable t)
    	{
    		logger.error(t.getMessage());
    		throw(new InitializationException("Exception duirng createSnapshotMulticastClient of PriceLevel : ",t));
    	}
    	
    	return(snapshotMulticastClient);
    	
	}
	
	public String toString()
	{
		return(PriceLevelMulticastClientFactory.class.getName());
	}
}

