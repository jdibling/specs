package com.theice.mdf.client.domain;

/**
 * Created by IntelliJ IDEA.
 * User: Adam Athimuthu
 * Date: Jul 31, 2007
 * Time: 5:35:42 PM
 *
 * <p/>
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 */
public class MDFClientConfig
{
    private String server=null;
    private String port=null;
    private String userName=null;
    private String password=null;
    private String marketTypes=null;
    private boolean enablerBuffering=false;
    private boolean getBundleMarket=false;
    private boolean getImpliedOrders=false;
    private boolean getOptionsMessage=false;

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getMarketTypes()
    {
        return marketTypes;
    }

    public void setMarketTypes(String marketTypes)
    {
        this.marketTypes = marketTypes;
    }

    public boolean isEnablerBuffering()
    {
        return enablerBuffering;
    }

    public void setEnablerBuffering(boolean enablerBuffering)
    {
        this.enablerBuffering = enablerBuffering;
    }

    public boolean isGetBundleMarket()
    {
        return getBundleMarket;
    }

    public void setGetBundleMarket(boolean getBundleMarket)
    {
        this.getBundleMarket = getBundleMarket;
    }

    public boolean isGetImpliedOrders()
    {
        return getImpliedOrders;
    }

    public void setGetImpliedOrders(boolean getImpliedOrders)
    {
        this.getImpliedOrders = getImpliedOrders;
    }

    public boolean isGetOptionsMessage()
    {
        return getOptionsMessage;
    }

    public void setGetOptionsMessage(boolean getOptionsMessage)
    {
        this.getOptionsMessage = getOptionsMessage;
    }

}


