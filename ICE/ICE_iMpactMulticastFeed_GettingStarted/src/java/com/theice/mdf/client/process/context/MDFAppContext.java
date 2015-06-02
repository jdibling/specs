package com.theice.mdf.client.process.context;

import java.util.List;

import com.theice.mdf.client.domain.MarketInterface;

/**
 * This interface helps us breaking the swing dependency
 * 
 * Responsibilities
 * 
 * 	- Identify components (MDSubscribers) for handling System/Error Message events (equivalent of MDFStatusBar)
 *  - Expose the logEssential Method for capturing important system events
 *  - get/set application name
 *  - isConnected
 *  - adding markets to a static cache that can serve the purpose of a "generic" model (see ProductDefinitionHandler)
 * 
 * @author Adam Athimuthu
 *
 */
public interface MDFAppContext
{
	public AppMode getAppMode();
	
	/**
	 * get/set application name
	 * @return
	 */
    public String getApplicationName();

    public void setApplicationName(String applicationName);

    /**
     * Check if the application has a good connection
     * @return
     */
    public boolean isConnected();
    
    /**
     * update the connection status
     * @param connected
     */
    public void setConnected(boolean connected);

    /**
     * Log the essential messages
     * @param message
     */
    public void logEssential(String message);

    /**
     * Clear all messages in the context cache
     */
    public void clearLogMessages();
    
    /**
     * alert with an appropriate mechanism
     * a GUI context might do this by a dialog box
     * a command context might display an error message
     * @param message
     */
    public void alert(String message);

    /**
     * cache market
     * @param Market
     */
    public void cacheMarket(MarketInterface market);
    
    /**
     * Is crossed book detection enabled?
     * @return
     */
    public boolean isCrossBookDetectionEnabled();
    
    public void setInterestedMulticastGroupNames(List<String> groupNames);
    
    public List<String> getInterestedMulticastGroupNames();
    
}

