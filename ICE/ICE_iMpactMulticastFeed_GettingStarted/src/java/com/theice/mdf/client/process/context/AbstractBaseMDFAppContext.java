package com.theice.mdf.client.process.context;

import java.util.List;

import org.apache.log4j.Logger;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public abstract class AbstractBaseMDFAppContext implements MDFAppContext
{
    private static final Logger logger=Logger.getLogger(AbstractBaseMDFAppContext.class.getName());

    protected String applicationName="default";

    protected boolean isConnected=false;
    
    protected AppMode mode=AppMode.Unknown;

    protected boolean detectCrossedBook=false;
    
    protected List<String> interestedMulticastGroupNames=null;

    protected AbstractBaseMDFAppContext()
    {
    	MDFClientConfiguration mdfConfig=MDFClientConfigurator.getInstance().getCurrentConfiguration();
    	
    	if(mdfConfig!=null)
    	{
        	this.detectCrossedBook=mdfConfig.getMDFClientRuntimeParameters().isCrossedBookDetectionEnabled();
        	logger.info("### Crossed Book Detection Enabled ? : "+detectCrossedBook);
    	}
    	else
    	{
        	logger.warn("### Config is null. Unable to determine Crossed Book Detection flag. Assuming false.");
    	}
    }
    
	/**
	 * get/set application name
	 * @return
	 */
    public String getApplicationName()
    {
    	return(applicationName);
    }

    public void setApplicationName(String applicationName)
    {
    	this.applicationName=applicationName;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

	public AppMode getAppMode()
	{
		return(mode);
	}
	
    /**
     * update the connection status
     * @param connected
     */
    public void setConnected(boolean connected)
    {
        isConnected = connected;
    }

    /**
     * Is crossed book detection enabled?
     * @return
     */
    public boolean isCrossBookDetectionEnabled()
    {
    	return(detectCrossedBook);
    }
    
    public void setInterestedMulticastGroupNames(List<String> groupNames)
    {
       this.interestedMulticastGroupNames = groupNames;
    }

    public List<String> getInterestedMulticastGroupNames()
    {
       return this.interestedMulticastGroupNames;
    }
}

