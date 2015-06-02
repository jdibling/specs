package com.theice.mdf.client.config.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Crossed Book Detection Information
 * 
 * @author Adam Athimuthu
 */
public class CrossedBookDetectionInfo
{
	private boolean detectCrossedBook=false;
	private List<Short> crossedBookDelayedMarketTypes=new ArrayList<Short>();
	private long crossedBookDelayedAlertThresholdMs=120000;
	private long crossedBookDelayedAlertMonitoringIntervalMs=1000;
	
	public CrossedBookDetectionInfo()
	{
	}
	
	public void enableCrossedBookDetection()
	{
		this.detectCrossedBook=true;
	}
	
	public boolean isCrossedBookDetectionEnabled()
	{
		return(this.detectCrossedBook);
	}
	
	public Short[] getCrossedBookAlertDelayedMarketTypes()
	{
		return(crossedBookDelayedMarketTypes.toArray(new Short[0]));
	}

	public void addCrossedBookAlertDelayedMarketType(short marketType)
	{
		crossedBookDelayedMarketTypes.add(new Short(marketType));
	}
	
	public void setCrossedBookAlertDelayedMarketTypes(List<Short> marketTypes)
	{
		crossedBookDelayedMarketTypes=marketTypes;
	}

	public long getCrossedBookDelayedAlertThresholdMs()
	{
		return(this.crossedBookDelayedAlertThresholdMs);
	}
	
	public void setCrossedBookDelayedAlertThresholdMs(long threshold)
	{
		this.crossedBookDelayedAlertThresholdMs=threshold;
	}
	
	public long getCrossedBookDelayedAlertMonitoringIntervalMs()
	{
		return(this.crossedBookDelayedAlertMonitoringIntervalMs);
	}
	
	public void setCrossedBookDelayedAlertMonitoringIntervalMs(long interval)
	{
		this.crossedBookDelayedAlertMonitoringIntervalMs=interval;
	}

	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[DetectCrossedBook=").append(this.detectCrossedBook).append("]");
		buf.append("[CrossedBook Delayed Alert Market Types=").append(this.crossedBookDelayedMarketTypes.toString()).append("]");
		buf.append("[DelayThreshold (ms)=").append(this.crossedBookDelayedAlertThresholdMs).append("]");
		buf.append("[DelayedAlertMonitoringInterval (ms)=").append(this.crossedBookDelayedAlertMonitoringIntervalMs).append("]");
		return(buf.toString());
	}
}

