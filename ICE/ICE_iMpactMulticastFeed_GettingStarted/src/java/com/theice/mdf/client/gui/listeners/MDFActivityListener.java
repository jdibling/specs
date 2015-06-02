package com.theice.mdf.client.gui.listeners;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Used for tracking the UI activities such as on-demand data loading etc.,
 * 
 * @author Adam Athimuthu
 */
public interface MDFActivityListener 
{
	public void inProgress();
	public void completed();
	public void aborted();
}
