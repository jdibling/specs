package com.theice.mdf.client.config.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.MarketType;
import com.theice.mdf.client.util.MDFUtil;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * The configuration repository
 * Contains global parameters and the environment specific maps
 * 
 * @author Adam Athimuthu
 */
public class MDFClientConfigRepository
{
    private static final Logger logger=Logger.getLogger(MDFClientConfigRepository.class.getName());

    protected MDFClientRuntimeParameters parameters=new MDFClientRuntimeParameters();
   
   /**
    * Map of config repository for each environment
    * Key: Environment
    */
   protected Map<String,MDFClientEnvConfigRepository> configMap=new HashMap<String,MDFClientEnvConfigRepository>();
   protected Map<Short, List<String>> marketTypeCodeToMulticastGroupNameMap=new HashMap<Short, List<String>>();

   /**
    * All multicast groups
    * Key: groupName
    */
   protected Map<String,MulticastGroupDefinition> multicastGroupDefinitions=new HashMap<String,MulticastGroupDefinition>();
   
   /**
    * List of all market types
    */
   protected List<MarketType> allMarketTypes=new ArrayList<MarketType>();
   
   /**
    * Groupwise market types map
    * Key: MulticastGroupName, Value=ArrayList of MarketType object that belong to the multicast group
    * Due to pre-defined options, one market type could be part of multiple groups (Futures/OTC and Options group)
    */
   protected HashMap<String,List<MarketType>> groupwiseMarketTypesMap=new HashMap<String,List<MarketType>>();
   
   public MDFClientConfigRepository()
   {
   }

   public MDFClientRuntimeParameters getMDFClientRuntimeParameters()
   {
      return(this.parameters);
   }
   
   public void setMDFClientRuntimeParameters(MDFClientRuntimeParameters parameters)
   {
      this.parameters=parameters;
   }
   
   /**
    * get config map
    * @return
    */
   public Map<String,MDFClientEnvConfigRepository> getConfigMap()
   {
      return(configMap);
   }

   /**
    * Add a config repository for a specific environment
    * @param configRepository
    */
   public void addConfigRepository(MDFClientEnvConfigRepository mdfClientConfig)
   {
      configMap.put(mdfClientConfig.getEnvironment(), mdfClientConfig);
   }
   
   /**
    * Get environments
    * @return
    */
   public String[] getEnvironments()
   {
      String[] environments=configMap.keySet().toArray(new String[0]);
      return(environments);
   }

   public MDFClientEnvConfigRepository getConfig(String envName)
   {
      return(configMap.get(envName));
   }
   
   /**
    * @return
    */
   public List<MarketType> getAllMarketTypes()
   {
      return(this.allMarketTypes);
   }

   public HashMap<String,List<MarketType>> getGroupwiseMarketTypesMap()
   {
      return(this.groupwiseMarketTypesMap);
   }
   
   public Map<String,MulticastGroupDefinition> getMulticastGroupDefinitions()
   {
      return(this.multicastGroupDefinitions);
   }
   
   public void setMulticastGroupDefinitions(Map<String,MulticastGroupDefinition> multicastGroupDefinitions)
   {
      this.multicastGroupDefinitions=multicastGroupDefinitions;
   }

   public MulticastGroupDefinition getMulticastGroupDefinition(String groupName)
   {
      return(this.multicastGroupDefinitions.get(groupName));
   }
   
   public Map<String, MulticastGroupDefinition> getMulticastGroupDefinitions(List<String> groupNames)
   {
      Map<String, MulticastGroupDefinition> groupDefinitionMap = new HashMap<String, MulticastGroupDefinition>();
      for (String groupName:groupNames)
      {
         groupDefinitionMap.put(groupName, this.multicastGroupDefinitions.get(groupName));
      }
            
      return groupDefinitionMap;
   }

   /**
    * Get the list of 
    * @param multicastGroup
    * @return
    */
   public List<MarketType> getMarketTypes(String multicastGroup)
   {
      List<MarketType> list=groupwiseMarketTypesMap.get(multicastGroup);
      
      if(list==null)
      {
         logger.warn("No market types found for multicast group : "+multicastGroup);
         list=new ArrayList<MarketType>();
      }

      return(list);
   }
   
   public Map<Short, List<String>> getMulticastGroupNameListMapKeyedByMarketTypeCode()
   {
      return marketTypeCodeToMulticastGroupNameMap;
   }
   
   public List<String> getMulticastGroupNameListByMarketTypeCode(Short marketTypeCode)
   {
      return marketTypeCodeToMulticastGroupNameMap.get(marketTypeCode);
   }
   
   /**
    * add the market type to the corresponding array list based on the multicast group names.
    * due to the pre-defined options, we can have one market type belonging to multiple multicast groups.
    * (futures group and pre-defined groups are mutually exclusive, but both of them have a specific market type listed)
    * 
    * also, add the market type to the global market types vector
    * @param marketType
    */
   public void addMarketType(MarketType marketType)
   {
      List<String> multicastGroups=marketType.getMulticastGroups();
      
      if(multicastGroups==null)
      {
         logger.warn("Multicast Group(s) is null while adding the market type to the groupwise map : "+marketType.toString());
         return;
      }

      for(Iterator<String> it=multicastGroups.iterator();it.hasNext();)
      {
         String multicastGroupName=it.next();
         
         List<MarketType> marketTypesList=groupwiseMarketTypesMap.get(multicastGroupName);
         
         if(marketTypesList==null)
         {
            groupwiseMarketTypesMap.put(multicastGroupName, marketTypesList=new ArrayList<MarketType>());
         }
         
         marketTypesList.add(marketType);
      }
      
      allMarketTypes.add(marketType);
      
      short marketTypeCode = Short.parseShort(marketType.getMarketTypeCode());
      marketTypeCodeToMulticastGroupNameMap.put(marketTypeCode, multicastGroups);
      
      return;
   }

   public String toString()
   {
      StringBuffer buf=new StringBuffer();
      buf.append("[Parameters=").append(this.parameters.toString()).append("]").append(MDFUtil.linefeed);
      buf.append("[ConfigMap=").append(this.configMap.toString()).append("]").append(MDFUtil.linefeed);
      buf.append("[MulticastGroupDefinitions=").append(MDFUtil.linefeed);
      buf.append(this.multicastGroupDefinitions.toString()).append(MDFUtil.linefeed);
      buf.append("[AllMarketTypes=").append(MDFUtil.linefeed);
      for(Iterator<Map.Entry<String,List<MarketType>>> it=groupwiseMarketTypesMap.entrySet().iterator();it.hasNext();)
      {
         Map.Entry<String,List<MarketType>> set=it.next();
         
         buf.append("{").append(set.getKey()).append(" ==> ").append(set.getValue().toString()).append("}").append(MDFUtil.linefeed);
      }

      return(buf.toString());
   }
}