package com.theice.mdf.client.multicast.factory;

import java.lang.reflect.Constructor;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.exception.InitializationException;
import com.theice.mdf.client.multicast.MDFMulticastClient;
import com.theice.mdf.client.multicast.dispatcher.FullOrderDepthMulticastDispatcher;
import com.theice.mdf.client.multicast.dispatcher.FullOrderDepthSnapshotMulticastDispatcher;
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
public class FullOrderDepthMulticastClientFactory implements MulticastClientFactory 
{
    private Logger logger=Logger.getLogger(FullOrderDepthMulticastClientFactory.class.getName());

    private static FullOrderDepthMulticastClientFactory _instance=new FullOrderDepthMulticastClientFactory();

    public static FullOrderDepthMulticastClientFactory getInstance()
    {
        return(_instance);
    }

    protected FullOrderDepthMulticastClientFactory()
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
        	clientName="FullOrderDepth";

        	System.out.println("Creating MulticastClient : "+clientName);
        	
        	dispatcher=new FullOrderDepthMulticastDispatcher(multicastGroupName, AppManager.getInstance(multicastGroupName).getMessageHandlerFactory());
        	
        	MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
        	
        	multicastClientConfig=new MulticastClientConfig(configuration.getMulticastChannelPairInfo(multicastGroupName).getLiveEndPoint(),
        			configuration.getMDFClientRuntimeParameters().getMulticastNetworkInterface(),clientName,dispatcher);
        	
        	multicastClient=new MDFMulticastClient(multicastClientConfig, multicastGroupName, false);
    	}
    	catch(Throwable t)
    	{
    		logger.error("Error creating incremental multicast client",t);
    		throw(new InitializationException("Exception during createIncrementalMulticastClient of FullOrderDepth : ",t));
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
	    	clientName="FullOrderDepthSnapShot";

        	System.out.println("Creating Incremental MulticastClient : "+clientName);
        	
        	String alternateDispatcherClass=System.getProperty("mdf.client.alternate.dispatcher.class");
        	if (alternateDispatcherClass==null || alternateDispatcherClass.length()==0)
        	{
        	   dispatcher=new FullOrderDepthSnapshotMulticastDispatcher(multicastGroupName, new CountDownLatch(1));
        	}
        	else
        	{	    	
        	   Class cls = Class.forName(alternateDispatcherClass);
        	   Class[] parameterTypes = new Class[2];
        	   parameterTypes[0] = String.class;
        	   parameterTypes[1] = java.util.concurrent.CountDownLatch.class;
        	   Constructor ct = cls.getConstructor(parameterTypes);
        	   Object arglist[] = new Object[2];
        	   arglist[0] = multicastGroupName;
        	   arglist[1] = new CountDownLatch(1);
        	   dispatcher = (MDFMulticastDispatcher)ct.newInstance(arglist);
        	}
        	
        	MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();
        	
	    	multicastClientConfig=new MulticastClientConfig(configuration.getMulticastChannelPairInfo(multicastGroupName).getSnapshotEndPoint(),
	    			configuration.getMDFClientRuntimeParameters().getMulticastNetworkInterface(),clientName,dispatcher);
	    	
	    	snapshotMulticastClient=new MDFMulticastClient(multicastClientConfig, multicastGroupName, true);
		}
    	catch(Throwable t)
    	{
    		logger.error("Error creating snapshot multicast client",t);
    		throw(new InitializationException("Exception duirng createSnapshotMulticastClient of FullOrderDepth : ",t));
    	}
    	
    	return(snapshotMulticastClient);
    	
	}
	
	public String toString()
	{
		return(FullOrderDepthMulticastClientFactory.class.getName());
	}
	
}

