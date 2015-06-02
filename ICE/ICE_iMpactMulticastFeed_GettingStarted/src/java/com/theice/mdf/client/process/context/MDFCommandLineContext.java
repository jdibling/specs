package com.theice.mdf.client.process.context;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketInterface;

/**
 * Command Line Context is used when the application is launched in non-GUI mode
 * This component helps us breaking the swing dependency
 * 
 * Responsibilities
 * 
 * 	- Identify components (MDSubscribers) for handling System/Error Message events (equivalent of MDFStatusBar)
 *  - Expose the logEssential Method for capturing important system events
 *  - get/set application name
 * 
 * @author Adam Athimuthu
 *
 */
public class MDFCommandLineContext extends AbstractBaseMDFAppContext
{
    private static final Logger logger=Logger.getLogger(MDFCommandLineContext.class.getName());

    private static MDFAppContext instance=new MDFCommandLineContext();
    
    private Map<Integer,MarketInterface> marketsMap=new HashMap<Integer,MarketInterface>();

    private MDFCommandLineContext()
    {
    	super();
    	this.mode=AppMode.CommandLine;
    }

    public static MDFAppContext getInstance()
    {
        return(instance);
    }

    /**
     * Log the essential messages
     * @param message
     */
    public void logEssential(String message)
    {
    	logger.info(message);
    }

    /**
     * Clear all messages in the context cache
     */
    public void clearLogMessages()
    {
    }
    
    /**
     * alert with an appropriate mechanism
     * a GUI context might do this by a dialog box
     * a command context might display an error message
     * @param message
     */
    public void alert(String message)
    {
    	logger.error(message);
    	System.err.println(message);
    }

    /**
     * cache market
     * @param Market
     */
    public void cacheMarket(MarketInterface market)
    {
    	this.marketsMap.put(new Integer(market.getMarketID()), market);
    }
    
}

