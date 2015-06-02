package com.theice.mdf.client.config.domain;

import java.util.List;

import com.theice.mdf.client.domain.state.SequenceProblemAction;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * MDF Client Parameters
 * 
 * @author Adam Athimuthu
 */
public class MDFClientRuntimeParameters
{
   public static final SequenceProblemAction SEQUENCE_PROBLEM_ACTION_DEFAULT=SequenceProblemAction.SHUTDOWN;

   protected String multicastNetworkInterface=null;
   protected SequenceProblemAction sequenceProblemAction=SEQUENCE_PROBLEM_ACTION_DEFAULT;
   protected int multicastInactivityThreshold=0;
   protected CrossedBookDetectionInfo crossedBookDetectionInfo=null;
   protected int systemTextWindowLocation_x_pos=-1;
   protected int systemTextWindowLocation_y_pos=-1;
   protected boolean autoReconnectTCP=false;

   //message rate sampling
   protected String msgMonitoringGroupInfo=null;
   protected String msgMonitoringInterestedGroups=null;
   protected long msgSamplingInterval=100;
   protected long msgSamplingSize=1000;
   protected long msgWarningThreshold=1000;
   protected long msgSecondaryWarningThreshold=500;
   protected long analyzerSleepInterval=200;
   protected long analyzerDelayStart=6000;
   protected long reporterSleepInterval=120000;
   protected long healthMonitorSleepInterval=1800000; 
   
   //snapshot logger
   protected long snapshotLoggerSleepInterval=5000;
   protected short snapshotLoggerNumOfLevels=1;
   protected boolean isGetStripInfo=false;   
   protected boolean bestPriceLogEnabled=false;
   protected String[] bestPriceLogFiles=null;
   protected String bestPriceMaxFileSize=null;

   /**
    * Queue size monitoring threshold. The client will report if there is excessive backlog.
    */
   protected static final int DEFAULT_QUEUESIZE_MONITORING_THRESHOLD=999999;
   protected int queueSizeMonitoringThreshold=DEFAULT_QUEUESIZE_MONITORING_THRESHOLD;
   protected List<Short> marketTypeIDsSupportBL=null;

   public MDFClientRuntimeParameters()
   {
   }

   public SequenceProblemAction getSequenceProblemAction()
   {
      return(this.sequenceProblemAction);
   }

   public void setSequenceProblemAction(String action)
   {
      if(action==null || action.trim().length()==0)
      {
         this.sequenceProblemAction=SEQUENCE_PROBLEM_ACTION_DEFAULT;
      }
      else
      {
         try
         {
            this.sequenceProblemAction=SequenceProblemAction.valueOf(action.toUpperCase());
         }
         catch(Exception e)
         {
            this.sequenceProblemAction=SEQUENCE_PROBLEM_ACTION_DEFAULT;
         }
      }
   }

   public boolean isAutoReconnectTCP()
   {
      return autoReconnectTCP;
   }

   public void setAutoReconnectTCP(boolean autoReconnectTCP)
   {
      this.autoReconnectTCP = autoReconnectTCP;
   }

   public String getMulticastNetworkInterface()
   {
      return(this.multicastNetworkInterface);
   }

   public void setMulticastNetworkInterface(String multicastNetworkInterface)
   {
      this.multicastNetworkInterface=multicastNetworkInterface;
   }

   public int getMulticastInactivityThreshold()
   {
      return(this.multicastInactivityThreshold);
   }

   public void setMulticastInactivityThreshold(int multicastInactivityThreshold)
   {
      this.multicastInactivityThreshold=multicastInactivityThreshold;
   }

   public CrossedBookDetectionInfo getCrossedBookDetectionInfo()
   {
      return(this.crossedBookDetectionInfo);
   }

   public boolean isCrossedBookDetectionEnabled()
   {
      boolean flag=false;

      if(this.crossedBookDetectionInfo!=null)
      {
         flag=this.crossedBookDetectionInfo.isCrossedBookDetectionEnabled();
      }

      return(flag);
   }

   public void setCrossedBookDetectionInfo(CrossedBookDetectionInfo crossedBookDetectionInfo)
   {
      this.crossedBookDetectionInfo=crossedBookDetectionInfo;
   }

   /**
    * Get the queue size monitoring threshold
    * @return
    */
   public int getQueueSizeMonitoringThreshold()
   {
      return(queueSizeMonitoringThreshold);
   }

   public int getSystemTextWindowLocationXPos()
   {
      return systemTextWindowLocation_x_pos;
   }

   public void setSystemTextWindowLocationXPos(int x)
   {
      this.systemTextWindowLocation_x_pos=x;
   }

   public int getSystemTextWindowLocationYPos()
   {
      return systemTextWindowLocation_y_pos;
   }

   public void setSystemTextWindowLocationYPos(int y)
   {
      this.systemTextWindowLocation_y_pos=y;
   }

   public String toString()
   {
      StringBuffer buf=new StringBuffer();
      buf.append("[SequenceProblemAction=").append(sequenceProblemAction).append("]");
      buf.append("[AutoReconnectTCP=").append(autoReconnectTCP).append("]");
      buf.append("[MulticastNetworkInterface=").append(multicastNetworkInterface).append("]");
      buf.append("[MulticastInactivityThreshold=").append(multicastInactivityThreshold).append("]");

      buf.append("[CrossedBookDetectionInfo=");
      if(crossedBookDetectionInfo!=null)
      {
         buf.append(this.crossedBookDetectionInfo.toString());
      }
      else
      {
         buf.append("Not Enabled");
      }

      buf.append("]");		
      return(buf.toString());
   }

   public String getMsgMonitoringGroupInfo()
   {
      return msgMonitoringGroupInfo;
   }

   public void setMsgMonitoringGroupInfo(String msgMonitoringGroupInfo)
   {
      this.msgMonitoringGroupInfo = msgMonitoringGroupInfo;
   }

   public String getMsgMonitoringInterestedGroups()
   {
      return msgMonitoringInterestedGroups;
   }

   public void setMsgMonitoringInterestedGroups(
         String msgMonitoringInterestedGroups)
   {
      this.msgMonitoringInterestedGroups = msgMonitoringInterestedGroups;
   }
   
   public List<Short> getMarketTypeIDListSupportingBL()
   {
      return this.marketTypeIDsSupportBL;
   }
   
   public void setMarketTypeIDListSupportingBL(List<Short> list)
   {
      this.marketTypeIDsSupportBL = list;
   }

   public long getMsgSamplingInterval()
   {
      return msgSamplingInterval;
   }

   public void setMsgSamplingInterval(long msgSamplingInterval)
   {
      this.msgSamplingInterval = msgSamplingInterval;
   }

   public long getMsgSamplingSize()
   {
      return msgSamplingSize;
   }

   public void setMsgSamplingSize(long msgSamplingSize)
   {
      this.msgSamplingSize = msgSamplingSize;
   }

   public long getMsgWarningThreshold()
   {
      return msgWarningThreshold;
   }

   public void setMsgWarningThreshold(long msgWarningThreshold)
   {
      this.msgWarningThreshold = msgWarningThreshold;
   }
   
   public long getMsgSecondaryWarningThreshold()
   {
      return msgSecondaryWarningThreshold;
   }

   public void setMsgSecondaryWarningThreshold(long msgSecondaryWarningThreshold)
   {
      this.msgSecondaryWarningThreshold = msgSecondaryWarningThreshold;
   }

   public long getAnalyzerSleepInterval()
   {
      return analyzerSleepInterval;
   }

   public void setAnalyzerSleepInterval(long analyzerSleepInterval)
   {
      this.analyzerSleepInterval = analyzerSleepInterval;
   }
   
   public long getAnalyzerDelayStart()
   {
      return analyzerDelayStart;
   }

   public void setAnalyzerDelayStart(long analyzerDelayStart)
   {
      this.analyzerDelayStart = analyzerDelayStart;
   }

   public long getReporterSleepInterval()
   {
      return reporterSleepInterval;
   }

   public void setReporterSleepInterval(long reporterSleepInterval)
   {
      this.reporterSleepInterval = reporterSleepInterval;
   }

   public long getHealthMonitorSleepInterval()
   {
      return healthMonitorSleepInterval;
   }

   public void setHealthMonitorSleepInterval(long healthMonitorSleepInterval)
   {
      this.healthMonitorSleepInterval = healthMonitorSleepInterval;
   }
   
   public long getSnapshotLoggerSleepInterval()
   {
      return snapshotLoggerSleepInterval;
   }
   
   public void setSnapshotLoggerSleepInterval(long sleepInterval)
   {
      this.snapshotLoggerSleepInterval = sleepInterval;
   }
   
   public short getSnapshotLoggerNumOfLevels()
   {
      return snapshotLoggerNumOfLevels;
   }
   
   public void setSnapshotLoggerNumOfLevels(short levels)
   {
      this.snapshotLoggerNumOfLevels = levels;
   }
   
   public boolean isGetStripInfo()
   {
      return this.isGetStripInfo;
   }
   
   public void setISGetStripInfo(boolean value)
   {
      this.isGetStripInfo=value;
   }
   
   public boolean isBestPriceLogEnabled()
   {
      return this.bestPriceLogEnabled;
   }
   
   public void setBestPriceLog(boolean value)
   {
      this.bestPriceLogEnabled = value;
   }
   
   public String[] getBestPriceLogFiles()
   {
      return this.bestPriceLogFiles;
   }
   
   public void setBestPriceLogFiles(String[] names)
   {
      this.bestPriceLogFiles = names;
   }
   
   public String getBestPriceLogMaxSize()
   {
      return this.bestPriceMaxFileSize;
   }
   
   public void setBestPriceLogMaxSize(String value)
   {
      this.bestPriceMaxFileSize = value;
   }
}

