package com.theice.mdf.client.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.theice.mdf.client.domain.MarketType;

/**
 * <code>SimpleClientConfigurator</code> contains the list of properties
 * used in the simple client.
 *
 * @author David Chen
 * @since 12/28/2006
 */
public class SimpleClientConfigurator
{
   private static Properties CLIENT_CONFIG_PROPS = new Properties();
   
   private static final String DEFAULT_CONFIGFILE="multicastClient.properties";
   
   private static final String PROP_KEY_FEED_SERVER_ADDRESS       = "mdf.server.address";
   private static final String PROP_KEY_FEED_SERVER_PORT          = "mdf.server.port";
   private static final String PROP_KEY_CLIENT_USERANME           = "mdf.client.username";
   private static final String PROP_KEY_CLIENT_PASSWORD           = "mdf.client.password";
   private static final String PROP_KEY_MARKET_TYPES              = "mdf.client.interest.market.types";
   private static final String PROP_KEY_CLIENT_ALL_MARKET_TYPES = "mdf.client.all.markets";
   private static final String PROP_KEY_CLIENT_FOR_OPTIONS        = "mdf.client.for.options";
   private static final String PROP_KEY_CLIENT_GET_STRIP_INFO     = "mdf.client.get.strip.info";
   private static final String PROP_KEY_CLIENT_GET_UDS_OPTIONS    = "mdf.client.get.uds";
   private static final String PROP_KEY_CLIENT_GET_UDS_FUTURES    = "mdf.client.get.uds.futures";

   private static HashMap ALL_MARKET_TYPE_MAP = new HashMap();
   
   // Load properties from simpleClient.properties
   static
   {

	   String configFileName=System.getProperty("config");
	   
	   if(configFileName==null)
	   {
		   configFileName=DEFAULT_CONFIGFILE;
	   }
	   
	   try
	   {
    	  
		   CLIENT_CONFIG_PROPS.load(new FileInputStream(configFileName));
         
	        /**
	         * read and parse market types
	         */
	        try
	        {
	            String marketTypesPropVal = CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_ALL_MARKET_TYPES);
	
	            String[] marketTypeList = marketTypesPropVal.split("\\|");
	            for (int i = 0; i < marketTypeList.length; i++)
	            {
	                String[] valList = marketTypeList[i].split(",");
	                String typeID = valList[0].trim();
	                String typeName = valList[1].trim();
	                ALL_MARKET_TYPE_MAP.put(Short.valueOf(typeID), typeName);
	            }
	        }
	        catch(Exception e)
	        {
	            System.out.println("Error loading property : "+PROP_KEY_CLIENT_ALL_MARKET_TYPES);
	        }
	        
	   }
	   catch (IOException e)
	   {
		   System.out.println("Failed to load simpleClient.properties.");
		   e.printStackTrace();
	   }
   }

   private SimpleClientConfigurator()
   {
   }

   /**
    * Get the feed server IP address
    * @return the server IP address
    */
   public static String getServerAddress()
   {
      return CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_FEED_SERVER_ADDRESS);
   }

   /**
    * Get the feed server port
    * @return the port number
    */
   public static int getServerPort()
   {
      return Integer.valueOf(CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_FEED_SERVER_PORT)).intValue();
   }

   /**
    * Get user name for login
    * @return the user name
    */
   public static String getUserName()
   {
      return CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_USERANME);
   }

   /**
    * Get the password for login
    * @return the password
    */
   public static String getPassword()
   {
      return CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_PASSWORD);
   }
   
   public static boolean isForOptions()
   {
      return "true".equals(CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_FOR_OPTIONS));
   }

   public static boolean isGetStripInfo()
   {
      return "true".equals(CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_GET_STRIP_INFO));
   }
   
   public static boolean isGetUDS()
   {
      return "true".equals(CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_GET_UDS_OPTIONS));
   }
   
   public static boolean isGetUDSForFutures()
   {
      return "true".equals(CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_CLIENT_GET_UDS_FUTURES));
   }
   
   /**
    * Get the market types that we are interested in
    * @return the array of market types
    */
   public static int[] getMarketTypes()
   {
      String mktTypes = CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_MARKET_TYPES);
      String[] list = mktTypes.split(",");
      int[] IDs = new int[list.length];
      for (int i=0; i<list.length; i++)
      {
         IDs[i] = Integer.valueOf(list[i]).intValue();
      }

      return IDs;
   }

    /**
     * Get the market types "as is" from the property file
     * @return
     */
   public static String getMarketTypesAsString()
   {
       return(CLIENT_CONFIG_PROPS.getProperty(PROP_KEY_MARKET_TYPES));
   }

    public static HashMap getAllMarketTypes()
    {
        return(ALL_MARKET_TYPE_MAP);
    }
    /**
     * builds the interested market types and returns a vector of MarketType objects
     * @return
     */
    public static Vector<MarketType> getInterestedMarketTypes()
    {
        Vector<MarketType> marketTypes=new Vector<MarketType>(30);

        Map allMarketTypes=getAllMarketTypes();

        if(allMarketTypes==null)
        {
            System.err.println("Market types table is empty. Check the properties file.");
            return(marketTypes);
        }

        String codeList=getMarketTypesAsString();

        StringTokenizer tokenizer = new StringTokenizer(codeList, ",");

        while (tokenizer.hasMoreTokens())
        {
            String code = tokenizer.nextToken();

            String desc=(String) allMarketTypes.get(Short.valueOf(code));

            if(desc==null)
            {
                System.err.println("Market Code invalid : "+code);
                continue;
            }

            marketTypes.addElement(new MarketType(code,desc));
        }

        return(marketTypes);
    }
   
}

