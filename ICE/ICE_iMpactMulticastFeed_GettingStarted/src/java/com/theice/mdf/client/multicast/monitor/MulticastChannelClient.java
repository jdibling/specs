package com.theice.mdf.client.multicast.monitor;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.domain.MulticastChannelInfo;
import com.theice.mdf.client.exception.ProcessingException;
import com.theice.mdf.client.util.LoggerCreator;
import com.theice.mdf.client.util.MailThrottler;
import com.theice.mdf.message.MulticastMessageBlock;

import com.theice.logging.core.AlarmLogger;
import com.theice.logging.core.ManagedObjectMetricLogger;
import com.theice.logging.core.ManagedObjectStatusLogger;
import com.theice.logging.core.domain.Alarm;
import com.theice.logging.core.domain.BasicAlarm;
import com.theice.logging.core.domain.BasicAppStatus;
import com.theice.logging.core.domain.ManagedObject;
import com.theice.logging.core.domain.Metric;
import com.theice.logging.domain.ComponentStatus;
import com.theice.logging.domain.Severity;

/**
 * Multicast Channel Client
 * 
 *   Accepts system properties for group address and port number
 *   Gets the multicast packets and displays onto stdout 
 *   
 * Monitoring and Alerts
 * 
 * 	The Multicast Client monitors the traffic and sends out alters during exception situations such as:
 * 
 * 	- out of order packets
 * 	- session changes
 * 	- duplicate packets
 * 	- idle time (no messages) for more than 20 seconds
 * 
 * An email is sent out during exception situations based on the appenders configured in the log4j config file
 * Emails are throttled so as not to put load on the mail server. Also, if there are no multicast traffic in a given
 * channel, we detect using a timeout. At this point the thread makes this connection to an inactive state. Alerts are not
 * generated until the connection is backup again.
 * 
 * Inactivity Detection - the multicast channel client detects inactivity by maintaining the last received
 * timestamp. The threshold can be specified using a system property.
 * 
 * SNMP Traps
 * ----------
 * 
 * The multicast client implements the ManagedObject interface, in order to report status via SNMP
 * ICE Logger framework is used for sending status/alerts via SNMP
 * 
 * Traps currently being sent for Managed Objects (MC Channels)
 * 		Status Trap					- Indicates a channel going up/down
 * 		Metric Trap					- Sequence Gap, Out Of Sequence etc., (Cleared when a session change is detected)
 * 		Alarm (EMSInfo)/Alerts		- Exceptions/Alarms
 * 
 * @see ManagedObject
 * 
 * @author Adam Athimuthu
 */
public class MulticastChannelClient implements ManagedObject, Runnable 
{
	private static ThreadLocal<Logger> threadLocalLoggers=new ThreadLocal<Logger>();
	private static final Logger alertLogger=LogManager.getLogger(MulticastChannelClient.class);
	
	public static int DEFAULT_RETRY_INTERVAL_MS=5000;

	private MulticastChannelInfo _multicastChannelInfo=null;
	private String _networkInterface=null;

	private static int DEFAULT_RECEIVE_BUFFER_SIZE_BYTES=16777216;
	private int _receiveBufferSize=DEFAULT_RECEIVE_BUFFER_SIZE_BYTES;
	
    private boolean _silent=false;
    private CountDownLatch _coordinatorLatch=null;
    
    private MulticastSocket _multicastSocket=null;
    private InetAddress _inetGroup=null;
    
    private int _inactivityThreshold=(-1);
    private int _retryIntervalMs=DEFAULT_RETRY_INTERVAL_MS;
    
    private Set<Integer> _missingPackets=new HashSet<Integer>();
    
	private static final SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
	private static MailThrottler mailThrottler=MailThrottler.getInstance();
	
	private boolean _active=false;
	private long _lastSeqGapAlarmTimestamp=0;
	private long _lastDupPacketAlarmTimestamp=0;
	private long _lastOutOfSeqAlarmTimestamp=0;
	private final long _emsAlarmThresholdInMillis=60000;
	private boolean _seqNumberCheckUsingMsgHeaderOnly=true; //no need to look at each individual message in a block
	
	/**
	 * Key prefixes for sending SNMP traps for Alarm/Metric
	 * In order to make it unique, these prefixes have to be attached with the specific channel key
	 * that is experiencing this problem
	 */
	private static final String KEYPREFIX_DUPLICATEPACKETS="DuplicatePackets";
	private static final String KEYPREFIX_SEQUENCEGAP="SequenceGap";
	private static final String KEYPREFIX_OUTOFORDERPACKETS="OutOfOrderPackets";
	private static final String KEYPREFIX_SESSIONCHANGE="MulticastSessionChange";
	
	private static final String ALARM_TITLE_SEQGAP = "Sequence Gap";
	private static final String ALARM_TITLE_DUPPACKET = "Duplicate Packet";
	private static final String ALARM_TITLE_OUTOFSEQ = "Out of Order Packet Received";

    protected MulticastChannelClient()
    {
    }

    public MulticastChannelClient(MulticastChannelInfo multicastChannelInfo,boolean silent,CountDownLatch coordinatorLatch, boolean seqNumCheckUsingHeaderOnly)
    {
       _multicastChannelInfo=multicastChannelInfo;
       _silent=silent;
       _coordinatorLatch=coordinatorLatch;
       _seqNumberCheckUsingMsgHeaderOnly=seqNumCheckUsingHeaderOnly;
    }
    
    public boolean isSilent()
    {
    	return(_silent);
    }
    
    public void setNetworkInterface(String networkInterface)
    {
    	this._networkInterface=networkInterface;
    }
    
    public void setRetryIntervalMs(int retryInterval)
    {
    	_retryIntervalMs=retryInterval;
    }

    public void setReceiveBufferSize(int receiveBufferSize)
    {
    	_receiveBufferSize=receiveBufferSize;
    }

    /**
     * open multicast channel
     * @throws IOException
     */
    protected void openMulticastChannel() throws IOException,ProcessingException
    {
        String osName=System.getProperty("os.name");
        
        EndPointInfo endPointInfo=this._multicastChannelInfo.getEndPointInfo();

    	try
    	{
        	_inetGroup=InetAddress.getByName(endPointInfo.getIpAddress());
            
            if(!_inetGroup.isMulticastAddress()) 
            {
            	StringBuffer buf=new StringBuffer();
            	buf.append("Not a multicast address : "+endPointInfo.getIpAddress());
            	writeAlert(buf.toString());
                throw(new ProcessingException(buf.toString()));
            }

            if("Linux".compareTo(osName)==0 || "SunOS".compareTo(osName)==0)
            {
                _multicastSocket=new MulticastSocket(new InetSocketAddress(_inetGroup,endPointInfo.getPort()));
            }
            else
            {
                _multicastSocket=new MulticastSocket(endPointInfo.getPort());
            }
            
    		if(_networkInterface!=null)
    		{
    			_multicastSocket.setInterface(InetAddress.getByName(_networkInterface));
    			System.out.println("Multicast Network Interface : "+_networkInterface);
    		}
    		
            System.out.println("Current Receive Buffer Size = "+_multicastSocket.getReceiveBufferSize());
            _multicastSocket.setReceiveBufferSize(_receiveBufferSize);
            System.out.println("Setting the Multicast Socket Receive Buffer Size to = "+_multicastSocket.getReceiveBufferSize());
            
            if(_inactivityThreshold>0)
            {
                System.out.println("*** Setting Multicast Channel Timeout to : "+_inactivityThreshold);
            	_multicastSocket.setSoTimeout(_inactivityThreshold);
            }

            _multicastSocket.joinGroup(_inetGroup);
            
            setActive(true);
            
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    		throw(e);
    	}
    }
    
    public boolean isActive()
    {
    	return(_active);
    }
    
    public void setActive(boolean flag)
    {
    	_active=flag;
    	
    	ManagedObjectStatusLogger.getInstance().log(new BasicAppStatus(this));
    }
    
    /**
     * write alert messages
     * @param message
     */
    protected static void writeAlert(String message)
    {
    	alertLogger.error(message);
    	mailThrottler.enqueueError("["+Thread.currentThread().getName()+"] "+message);
    	return;
    }
    
    /**
     * formats the given integer to a hex value
     * @param value
     * @return
     */
    private String toHexString(int value)
    {
		StringBuffer buf=new StringBuffer();
		Formatter formatter=new Formatter(buf);
		formatter.format("%x",value);
		return(buf.toString());
    }
    
    public void setInactivityThreshold(int inactivityThreshold)
    {
    	_inactivityThreshold=inactivityThreshold;
    }

    /**
     * receive and output the messages from the multicast channel
     * @throws Exception
     */
    public void receive() throws Exception
    {
    	byte[] buffer=new byte[1400];
    	DatagramPacket packet=null;
    	boolean keepRunning=true;
    	
    	int lastSequence=(-1);
    	int lastNumMessages=0;
    	short sessionNumber=(-1);
    	short previousSession=(-1);
    	
    	StringBuffer buf=null;

		Logger threadLogger=null;
		
		System.out.println("### Entering multicast receive loop ### : "+getEndPointInfo().toString()+" SilentMode="+isSilent());
		
		while(keepRunning)
    	{
    		packet=new DatagramPacket(buffer,buffer.length);
    		
    		try
    		{
        		_multicastSocket.receive(packet);

        		/**
        		 * If control got here, that means we were successful in receiving without timing out
        		 * If the thread was originally inactive, send out an alert and mark the thread active
        		 */
        		if(!isActive())
        		{
        			writeAlert("Channel has become Active.");
                    setActive(true);
        		}
    		}
    		catch(SocketTimeoutException ioe)
    		{
    			StringBuffer errorMessage=new StringBuffer();
    			errorMessage.append("Timed out while trying to receive messages through multicast. Channel is marked [Inactive] : ");
    			errorMessage.append(ioe.toString());
    			
    			/**
    			 * If active, we send an alert make the flag to 'inactive'
    			 * 
    			 * If the thread has already been inactive, we just log it and not send any alerts until the system
    			 * is back up again
    			 */
    			if(isActive())
    			{
        			writeAlert(errorMessage.toString());
                    setActive(false);
    			}
    			else
    			{
    				alertLogger.error(errorMessage.toString());
    			}
    		}
    		catch(IOException ioe)
    		{
    			writeAlert("IOException while receiving messages through multicast : "+ioe.toString());
                setActive(false);
    			throw(ioe);
    		}

    		if(!isActive())
    		{
        		/**
        		 * Indicates a timeout while trying to receive from the socket.
        		 * In this situation, log an entry in the local alert log, sleep and retry 
        		 */
    			try
    			{
    				alertLogger.error("Channel is in inactive state. Will retry after "+_retryIntervalMs+" ms");
        			Thread.sleep(_retryIntervalMs);
    			}
    			catch(InterruptedException e)
    			{
    			}
    			continue;
    		}
    		
    		if(packet.getLength()==0)
    		{
    			writeAlert("*** Received a ZERO length packet");
    			continue;
    		}
    		
    		//System.out.println("Addr="+packet.getAddress().toString()+", SocketAddr="+packet.getSocketAddress().toString());
    		
    		ByteArrayInputStream stream=new ByteArrayInputStream(packet.getData(),0,packet.getLength());
    		
    		MulticastMessageBlock messageBlock=new MulticastMessageBlock();

    		try
    		{
    		   DataInputStream inputStream = new DataInputStream(stream);
    		   short numberOfMsgs=0;
    		   //if (_seqNumberCheckUsingMsgHeaderOnly)
    		   //{
    		      //skip all message body bytes
    		   //   numberOfMsgs = messageBlock.deserializeMessageHeaderOnly(inputStream);
    		   //}
    		   //else
    		   //{
    		      //peek into each individual message
    		      messageBlock.deserialize(inputStream);
    		      numberOfMsgs = (short)messageBlock.getMdMessages().size();
    		   //}
    		          		          		
        		if(messageBlock.NumOfMessages!=numberOfMsgs)
        		{
        			writeAlert("*** Error (Block Discrepancy) : NumOfMessage != size of the message list");
        		}
        		
        		if(sessionNumber!=messageBlock.SessionNumber)
        		{
        			/**
        			 * The very first message we receive, we create the thread specific logger with the session id
        			 */
            		if(sessionNumber<0)
            		{
            			buf=new StringBuffer();
            			buf.append(dateFormatter.format(System.currentTimeMillis())).append(" : ");
            			buf.append("*** Start of Session : ").append(messageBlock.SessionNumber);
        				buf.append(" [Hex : ").append(toHexString(messageBlock.SessionNumber)).append("]");;
        				buf.append(" SilentMode=").append(isSilent());
        				
        				initThreadLogger(messageBlock.SessionNumber);
        				threadLogger=(Logger) threadLocalLoggers.get();
        				
        				if(threadLogger!=null)
        				{
            				threadLogger.debug(buf.toString());
        				}
        				else
        				{
        					System.err.println("Thread specific logger is null.");
        					System.out.println(buf.toString());
        				}
            		}
            		else
            		{
            			buf=new StringBuffer();
            			buf.append(dateFormatter.format(System.currentTimeMillis())).append(" : ");
            			buf.append("*** Session Change Detected. ");
            			buf.append("Previous Session : ").append(sessionNumber);
        				buf.append(" [Hex : ").append(toHexString(sessionNumber)).append("]");
            			buf.append(" Current Session : ").append(messageBlock.SessionNumber);
        				buf.append(" [Hex : ").append(toHexString(messageBlock.SessionNumber)).append("]");
        				buf.append(" SilentMode=").append(isSilent());
        				
        				writeAlert(buf.toString());

        				/**
        				 * Send an Alert trap
        				 * Log the change metric and clear the internal metric cache
        				Metric metric=new BasicMetric(METRIC_SESSIONCHANGE,"Session Change",this);
        				metric.setText(buf.toString());
        				System.out.println("##### Logging the session change metric : "+metric.toString());
        				ManagedObjectMetricLogger.getInstance().log(metric);
        				 */
        				
        				/**
        				 * If metric traps are used, this call will clear the metric logger's internal cache
        				 * Currently, the metric traps are disabled.
        				 * ManagedObjectMetricLogger.getInstance().clear(this); 
        				 */
        				
        				/**
        				 * During session changes, recreate the logger
        				 */
        				initThreadLogger(messageBlock.SessionNumber);
        				threadLogger=(Logger) threadLocalLoggers.get();
        				
        				if(threadLogger!=null)
        				{
            				threadLogger.info("New logger created for session : "+messageBlock.SessionNumber);
            				threadLogger.info(buf.toString());
        				}
        				else
        				{
        					System.err.println("Thread specific logger is null for session : "+messageBlock.SessionNumber);
        					System.out.println(buf.toString());
        				}

        				/**
        				 * re-init the last sequence number during session changes
        				 */
        		    	lastSequence=(-1);
        		    	lastNumMessages=0;
            		}
            		
            		previousSession=sessionNumber;
        			sessionNumber=messageBlock.SessionNumber;
        		}
        		
    			/**
    			 * Check for out of sequence condition
    			 * - check for duplicate packets
    			 * - check for missing packets (that might arrive at a later point in time)
    			 * 
    			 */
        		if(lastSequence>=0)
        		{
        			int expectedSequenceNumber=lastSequence+lastNumMessages;
        			
        			if(expectedSequenceNumber!=messageBlock.SequenceNumber)
        			{
        				buf=new StringBuffer();
            			buf.append(dateFormatter.format(System.currentTimeMillis())).append(" : ");

        				StringBuffer errorSummary=new StringBuffer();
        				StringBuffer errorInfo=new StringBuffer();
        				
        				Metric metric=null;
        				Alarm alarm=null;
        				
            			if(messageBlock.SequenceNumber>expectedSequenceNumber)
            			{
            				errorSummary.append("*** Error (Sequence Gap Detected) ");

            				
                			/**
                			 * Missing packets (might indicate an out of sequence condition)
                			 * Keep the missing packets in hash
                			 */
            				int seq=expectedSequenceNumber;

            				//TODO  :NM : Change : Adding count of sequence number
            				errorSummary.append("[Number of Sequence Missing =" + (messageBlock.SequenceNumber - seq) + "]");
            				//errorInfo.append("[Missing=");
            				while(seq<messageBlock.SequenceNumber)
            				{
            					threadLogger.warn("Caching Missing Packet : "+seq);
            					
            					_missingPackets.add(new Integer(seq));
            					
            					//errorInfo.append(seq).append(",");
            					seq++;
            				}
            				//errorInfo.append("]");

            				String key=KEYPREFIX_SEQUENCEGAP+getManagedObjectKey();
            				/**
            				 * Construct a unique metric for this channel
            				 * metric=new BasicMetric(key,"Sequence Gap",this);
            				 */
            				
            				/**
            				 * Generate an alarm trap if needed
            				 */
            				
            				alarm = getSeqGapAlarmIfNeeded(key);
            				
            			}
            			else if(messageBlock.SequenceNumber<expectedSequenceNumber)
            			{
                			/**
                			 * Older packet. Check if we have this in the missing packets hash
                			 * Otherwise, mark this is as duplicate and move forward
                			 */
            				if(_missingPackets.remove(new Integer(messageBlock.SequenceNumber)))
            				{
                				errorSummary.append("*** Error (Out of Order Packet Received) ");
            					errorInfo.append(" [Older Packet : ").append(messageBlock.SequenceNumber).append("]");
            					errorInfo.append(" {Still Missing : ").append(_missingPackets.toString()).append("}");
            					
                				String key=KEYPREFIX_OUTOFORDERPACKETS+getManagedObjectKey();
                				
                				/**
                				 * Construct a unique metric for this channel
                				 * metric=new BasicMetric(METRIC_OUTOFORDERPACKETS,"Out of Order Packet Received",this);
                				 */

                				/**
                				 * Generate an alarm trap
                				 */
                				
                				alarm = getOutOfSeqAlarmIfNeeded(key);
                				
            				}
            				else
            				{
                				errorSummary.append("*** Error (Duplicate Packet) ");
            					errorInfo.append(" [Duplicate Packet : ").append(messageBlock.SequenceNumber).append("]");
            					
                				String key=KEYPREFIX_DUPLICATEPACKETS+getManagedObjectKey();
                				/**
                				 * Construct a unique metric for this channel
                				 * metric=new BasicMetric(METRIC_DUPLICATEPACKETS,"Duplicate Packet",this);
                				 */
                				
                				alarm = getDupPacketAlarmIfNeeded(key);
            				}
            			}

            			buf.append(errorSummary);
        				buf.append(" Expected : ").append(expectedSequenceNumber);
        				buf.append(" [Hex : ").append(toHexString(expectedSequenceNumber)).append("]");
        				buf.append("  Got : ").append(messageBlock.SequenceNumber);
        				buf.append(" [Hex : ").append(toHexString(messageBlock.SequenceNumber)).append("]");
        				
            			buf.append(" ### {Previous Seq : ").append(lastSequence);
        				buf.append(" [Hex : ").append(toHexString(lastSequence)).append("]}");

            			buf.append(" Current Session : ").append(messageBlock.SessionNumber);
        				buf.append(" [Hex : ").append(toHexString(messageBlock.SessionNumber)).append("]");;

        				buf.append(" {Previous Session : ");
            			
        				if(previousSession<0)
        				{
                			buf.append("[None]");
        				}
        				else
        				{
                			buf.append(previousSession).append(" [Hex : ").append(toHexString(previousSession)).append("]}");;
        				}
        				buf.append(errorInfo);

        				writeAlert(buf.toString());
        				
        				/**
        				 * If set, send the specific metric trap
        				 */
        				if(metric!=null)
        				{
            				metric.setText(buf.toString());
            				threadLogger.debug("##### Logging the metric : "+metric.toString());
            				ManagedObjectMetricLogger.getInstance().log(metric);
        				}

        				if(alarm!=null)
        				{
        					alarm.addAlarmDetails(buf.toString());
            			threadLogger.debug("##### Logging the alarm : "+alarm.toString());
            			AlarmLogger.getInstance().log(alarm);
        				}
        			}
        		}
        		
        		lastSequence=messageBlock.SequenceNumber;
        		lastNumMessages=messageBlock.NumOfMessages;
        		
				if(!isSilent())
				{
    				if(threadLogger!=null)
    				{
        				threadLogger.debug(messageBlock.toString());
    				}
    				else
    				{
    					System.err.println("### Thread specific logger is null. Should have been created at the start of session!!!");
    					System.out.println(messageBlock.toString());
    				}
				}
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    			
    			buf=new StringBuffer();
    			
    			byte bytes[]=packet.getData();
    			
    			for(int index=0;index<packet.getLength();index++)
    			{
    				buf.append("["+bytes[index]+"]");
    			}
    			writeAlert("Exception while deserialization: Length: "+packet.getLength()+" - "+buf.toString());
    		}
    	}
		
		return;
    }

    /**
     * @return the end point info
     */
    public EndPointInfo getEndPointInfo()
    {
       return(this._multicastChannelInfo.getEndPointInfo());
    }
    
    public String getChannelName()
    {
    	return(this._multicastChannelInfo.getChannelName());
    }

    public String getChannelType()
    {
    	return(this._multicastChannelInfo.getChannelType());
    }

    /**
     * init logger for the session
     * @param session
     */
	private void initThreadLogger(short sessionNumber)
	{
		String loggerName=Thread.currentThread().getName()+"-"+sessionNumber;
		
		try
		{
			cleanThreadLogger();
			
			Logger threadLogger=LoggerCreator.createFileLogger(loggerName);
			threadLocalLoggers.set(threadLogger);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Clean the thread's logger
	 */
	private void cleanThreadLogger()
	{
		try
		{
			Logger threadLogger=(Logger) threadLocalLoggers.get();
			
			if(threadLogger!=null)
			{
				threadLogger.info("Closing the logger.");
				threadLogger=null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private BasicAlarm getSeqGapAlarmIfNeeded(String key)
	{
	   BasicAlarm alarm = null;
	   long currentTimestamp = System.currentTimeMillis();
	   if (currentTimestamp - _lastSeqGapAlarmTimestamp >= _emsAlarmThresholdInMillis)
	   {
	      alarm=new BasicAlarm(key, ALARM_TITLE_SEQGAP, Severity.Major, this);
	      _lastSeqGapAlarmTimestamp = currentTimestamp;   
	   }
	      
	   return alarm;
	}
	
	private BasicAlarm getDupPacketAlarmIfNeeded(String key)
	{
      BasicAlarm alarm = null;
      long currentTimestamp = System.currentTimeMillis();
      if (currentTimestamp - _lastDupPacketAlarmTimestamp >= _emsAlarmThresholdInMillis)
      {
         alarm=new BasicAlarm(key, ALARM_TITLE_DUPPACKET, Severity.Major, this);
         _lastDupPacketAlarmTimestamp = currentTimestamp;   
      }
         
      return alarm;	   
	}
	
	private BasicAlarm getOutOfSeqAlarmIfNeeded(String key)
	{
      BasicAlarm alarm = null;
      long currentTimestamp = System.currentTimeMillis();
      if (currentTimestamp - _lastOutOfSeqAlarmTimestamp >= _emsAlarmThresholdInMillis)
      {
         alarm=new BasicAlarm(key, ALARM_TITLE_OUTOFSEQ, Severity.Major, this);
         _lastOutOfSeqAlarmTimestamp = currentTimestamp;   
      }
         
      return alarm;	   
	}

	/**
    * thread's run method
    */
   public void run()
   {
	   try
	   {
		   runClient();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }

	   return;
   }

   /**
    * multicast client
    * @param args
    * @throws Exception
    */
   public void runClient()
   {  	
	   System.out.println("MulticastChannelClient - Joining : "+getEndPointInfo().toString());
	   try
	   {
		   openMulticastChannel();
		   receive();
	   }
	   catch(Exception e)
	   {
		   writeAlert("Multicast Client Failed : "+e.toString());
		   e.printStackTrace();
	   }
	   
	   try
	   {
		   System.err.println("Thread Exiting : "+Thread.currentThread().getName()+"/"+Thread.currentThread().getId());
		   _coordinatorLatch.countDown();    	
	   }
	   catch(Exception e)
	   {
	   }

	   return;
   }

	/**
	 * Unique Key
	 */
	public String getManagedObjectKey()
	{
    	return(this._multicastChannelInfo.getKey());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((_multicastChannelInfo == null) ? 0 : _multicastChannelInfo
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MulticastChannelClient))
			return false;
		final MulticastChannelClient other = (MulticastChannelClient) obj;
		if (_multicastChannelInfo == null)
		{
			if (other._multicastChannelInfo != null)
				return false;
		}
		else if (!_multicastChannelInfo.equals(other._multicastChannelInfo))
			return false;
		return true;
	}

	/**
	 * Application specific type of the managed object
	 * For example, for the Multicast Client, this could be the ChannelType 
	 */
	public String getManagedObjectType()
	{
    	return(this._multicastChannelInfo.getChannelType());
	}
	
	/**
	 * Application specific name of the managed object
	 * For example, for the Multicast Client, this could be the ChannelName 
	 */
	public String getManagedObjectName()
	{
    	return(this._multicastChannelInfo.getChannelName());
	}
	
	/**
	 * This flag will indicate to the framework if the environment name should be appended
	 * to the MO name while sending the traps
	 * @return false
	 */
	public boolean qualifyManagedObjectWithEnvironment()
	{
		return(false);
	}
   
	/**
	 * Physical address such as IP address/port, used in the status trap
	 */
	public String getManagedObjectDeploymentInfo()
	{
		String deploymentInfo=getEndPointInfo().getIpAddress()+":"+getEndPointInfo().getPort();
		return(deploymentInfo);
	}

	/**
	 * Get component status
	 * @return ComponentStatus
	 */
	public ComponentStatus getComponentStatus()
	{
		ComponentStatus status=ComponentStatus.UNKNOWN;
		
		if(isActive())
		{
			status=ComponentStatus.UP;
		}
		else
		{
			status=ComponentStatus.DOWN;
		}
		
		return(status);
	}
	
	/**
    * toString
    * 
    * @return String
    */
   public String toString()
   {
	   StringBuffer buffer=new StringBuffer();
	   
	   buffer.append("MulticastClient : ");

	   if(_multicastChannelInfo!=null)
	   {
		   buffer.append(this._multicastChannelInfo.toString());
	   }
	   else
	   {
		   buffer.append("[null]");
	   }
	   
	   return(buffer.toString());
   }

}

