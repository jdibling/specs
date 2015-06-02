package com.theice.mdf.client.config;

import com.theice.mdf.client.config.domain.ConfigurationSelectorInfo;
import com.theice.mdf.client.exception.ConfigurationException;

public class MDFClientConfiguratorHelper
{
   private static boolean clientConfigLoaded=false;
    
   public static void loadConfiguration() throws Exception
   {
      if (clientConfigLoaded==false)
      {
         MDFClientConfigurator.getInstance().init();
         MDFClientConfigurator.getInstance().configure();
         clientConfigLoaded=true;
         System.out.println("ClientConfig loaded");
      }
      else
      {
         System.out.println("ClientConfig has been loaded, skip loading.");
      }
   }

   /**
    * Choose the configuration from information supplied in the system environment
    * Don't use defaults for the GUI
    * 
    * This way config could be selected interactively
    * 
    * @throws ConfigurationException
    */
   public static boolean chooseConfiguration() throws ConfigurationException
   {
      boolean configSelected=false;
      ConfigurationSelectorInfo selectorInfo=MDFCommandLineConfigurator.getInstance().obtainConfigurationSelector(false);

      if(selectorInfo!=null)
      {
         System.out.println(selectorInfo.toString());
         MDFClientConfigurator.getInstance().chooseConfiguration(selectorInfo);
         configSelected=true;
      }
   
      return configSelected;
   }
}
