package com.theice.mdf.client.process;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * MDF Client Interface - provides contract for supporting multiple client implementations
 *
 * @author Adam Athimuthu
 */
public interface MDFClientInterface
{
    public void process();
    
    public void logoutAndCloseSocket();
    
    /**
     * the factory associate with this client
     */
    public MarketHandlerFactoryInterface getFactory();
}
