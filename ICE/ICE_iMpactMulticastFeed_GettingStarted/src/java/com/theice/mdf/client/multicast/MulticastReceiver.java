package com.theice.mdf.client.multicast;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import com.theice.logging.core.domain.BasicAppStatus;
import com.theice.logging.domain.ComponentStatus;
import com.theice.mdf.client.ClientState;
import com.theice.mdf.client.domain.EndPointInfo;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.util.MailThrottler;
import com.theice.mdf.message.MulticastMessageBlock;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * TODO make the multicast receive buffer size configurable
 * 
 * @author Adam Athimuthu
 */
public class MulticastReceiver implements Runnable
{
    static Logger logger=Logger.getLogger(MulticastReceiver.class.getName());

	private static int DEFAULT_RECEIVE_BUFFER_SIZE_BYTES=16777216;
	
    private MulticastSocket _multicastSocket=null;
    private InetAddress _inetGroup=null;

    private EndPointInfo _endPoint=null;
    private String _networkInterface=null;
    private String _multicastGroupName=null;
    private int _receiveBufferSize=DEFAULT_RECEIVE_BUFFER_SIZE_BYTES;
    
	private boolean keepRunning=true;
	private boolean _forSnapshotChannel=false;
	
	//private AppManager appManager=AppManager.getInstance();

	protected BlockingQueue<MulticastMessageBlock> _queue=new LinkedBlockingQueue<MulticastMessageBlock>(); 
    
    public static final int MAX_DATAGRAM_SIZE=1400;
    private final ClientState _clientState;

	 public MulticastReceiver(EndPointInfo endPoint,String networkInterface, String groupName, boolean forSnapshot)
    {
    	_endPoint=endPoint;
    	_networkInterface=networkInterface;
    	_multicastGroupName=groupName;
    	_forSnapshotChannel=forSnapshot;
    	String key = groupName + (_forSnapshotChannel?" Snapshot":"");
    	_clientState = new ClientState(key, "MCFeedClient", groupName, _endPoint.getIpAddress()+":"+_endPoint.getPort());
    	
    }
    
    public void setReceiveBufferSize(int receiveBufferSize)
    {
    	_receiveBufferSize=receiveBufferSize;
    }

    /**
     * open multicast channel
     * 
     * @throws IOException
     */
    public void openMulticastChannel() throws IOException
    {
        String osName=System.getProperty("os.name");

        try
    	{
        	_inetGroup=InetAddress.getByName(_endPoint.getIpAddress());
            
            if(!_inetGroup.isMulticastAddress()) 
            {
                logger.error("Not a multicast address : "+_endPoint.getIpAddress());
                throw(new IOException("Not a multicast address"));
            }
            
            if("Linux".compareTo(osName)==0 || "SunOS".compareTo(osName)==0)
            {
                _multicastSocket=new MulticastSocket(new InetSocketAddress(_inetGroup,_endPoint.getPort()));
            }
            else
            {
                _multicastSocket=new MulticastSocket(_endPoint.getPort());
            }

            System.out.println("Current Receive Buffer Size = "+_multicastSocket.getReceiveBufferSize());
            _multicastSocket.setReceiveBufferSize(_receiveBufferSize);
            System.out.println("Setting the Multicast Socket Receive Buffer Size to = "+_multicastSocket.getReceiveBufferSize());
            
            /**
             * Multicast Network Interface
             */
            if(_networkInterface!=null)
            {
               _multicastSocket.setInterface(InetAddress.getByName(_networkInterface));
               System.out.println("Multicast Network Interface : "+_networkInterface);
            }
            
            /**
             * Inactivity threshold
             */
            int timeout=AppManager.getMulticastInactivityThreshold();
            
            if(timeout>0)
            {
               System.out.println("*** Setting Multicast Channel Timeout to : "+timeout);
            	_multicastSocket.setSoTimeout(timeout);
            }

            _multicastSocket.joinGroup(_inetGroup);
            
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    		sendAlert("IOException", "IOException in MulticastReceiver", AppManager.SOCKET_IOEXCEPTION_CODE);
    		
    		throw(e);
    	}
    }

    /**
     * Close the multicast channel
     */
    public void closeMulticastChannel()
    {
    	if(_multicastSocket==null || _multicastSocket.isClosed())
    	{
    		logger.warn("MulticastReceiver.closeMulticastChannel : Socket already closed");
    		return;
    	}
    	
    	try
    	{
    		StringBuffer buf=new StringBuffer();
    		buf.append(Thread.currentThread().getName()).append(" [Thread=").append(Thread.currentThread().getId());
    		buf.append("] - Closing the Multicast Channel");
        	System.out.println(buf.toString());
        	logger.info(buf.toString());

        	_multicastSocket.leaveGroup(_inetGroup);
        	_multicastSocket.close();
        	_multicastSocket=null;
    	}
    	catch(IOException e)
    	{
    		logger.warn("Exception while closing multicast channel : "+e.toString());
    	}
    }

    /**
     * The thread's run method
     */
    public void run()
    {
    	try
    	{
        	process();
    	}
    	/*catch(SocketTimeoutException e)
    	{
			logger.error(e.toString());
			if (_multicastGroupName!=null)
			{
			   AppManager.getInstance(_multicastGroupName).setApplicationStatus(ApplicationStatus.NETWORKINACTIVITY);
			   AppManager.getInstance(_multicastGroupName).getAppMonitor().wakeup();
			}
    	}
    	catch(IOException e)
    	{
			logger.error(e.toString());
			if (_multicastGroupName!=null)
			{
			   AppManager.getInstance(_multicastGroupName).setApplicationStatus(ApplicationStatus.NETWORKERROR);
			   AppManager.getInstance(_multicastGroupName).getAppMonitor().wakeup();
			}
    	}
    	*/
    	catch(Throwable e)
    	{
    		logger.error(e.toString(), e);
    		MailThrottler.getInstance().enqueueError(e.toString());
    		
    	}
    	finally
    	{
    		closeMulticastChannel();
    	}
    }
    
	/**
	 * stop the receiver
	 */
	public void stop()
	{
		System.out.println("Stopping the receiver...");
		this.keepRunning=false;
	}

    /**
     * receive messages through the multicast channel and process them using appropriate handlers
     */
    protected void process() throws Exception
    {
    	byte[] buffer=new byte[MulticastReceiver.MAX_DATAGRAM_SIZE];
    	DatagramPacket packet=null;
    	_clientState.setComponentStatus(ComponentStatus.UP);
    	boolean hadSocketTimeoutException=false;
    	boolean hadIOException=false;
    	long numberOfSocketTimeoutExceptions=0;
    	long lastSocketTimeoutExceptionAlertSentTimeStamp=0;
    	final String SOCKETSTATUS="SocketStatus";
    	final String SOCKETIOEXCEPTION="SocketIOException";
    	
    	while(keepRunning)
    	{
    		packet=new DatagramPacket(buffer,buffer.length);
    		
    		try
    		{
    			_multicastSocket.receive(packet);
    		}
    		catch(SocketTimeoutException ioe)
    		{
    		   hadSocketTimeoutException=true;
    		   numberOfSocketTimeoutExceptions++;
    		   long currentTime = System.currentTimeMillis();
    		   if (currentTime-lastSocketTimeoutExceptionAlertSentTimeStamp > 300000)
    		   {
    		      StringBuilder msg = new StringBuilder(_multicastGroupName);
    		      msg.append("-SocketTimeoutException while trying to receive messages through multicast : ");
    		      msg.append(ioe.toString());
    		      msg.append(". Number of SocketTimeoutExceptions so far: ");
    		      msg.append(numberOfSocketTimeoutExceptions);
    		      logger.error(msg.toString());
    		      MailThrottler.getInstance().enqueueError(msg.toString());
   		      sendAlert(SOCKETSTATUS, "SocketTimeOutException in MulticastReceiver", AppManager.SOCKET_TIMEOUT_ERROR_CODE);
   		      lastSocketTimeoutExceptionAlertSentTimeStamp = currentTime; 
    		   }
    			
    			continue;
    			//throw(ioe);
    		}
    		catch(IOException ioe)
    		{
    			String msg="IOException while receiving messages through multicast : "+ioe.toString();
    		   logger.error(msg);
    		   MailThrottler.getInstance().enqueueError(msg);
    		   hadIOException=true;
    		   sendAlert(SOCKETIOEXCEPTION, "IOException in MulticastReceiver", AppManager.SOCKET_IOEXCEPTION_CODE);
    		   
    		   Thread.sleep(15000);
    		   continue;
    			//throw(ioe);
    		}
    		
    		if(!keepRunning)
    		{
    			System.out.println("MulticastReceiver received a stop signal...");
    			break;
    		}
    		
    		if (hadSocketTimeoutException)
    		{
    		   sendAlert(SOCKETSTATUS, "Cleared", AppManager.CLEARED);
    		   hadSocketTimeoutException=false;
    		}
    		
    		if (hadIOException)
    		{
    		   sendAlert(SOCKETIOEXCEPTION, "Cleared", AppManager.CLEARED);
    		   hadIOException=false;
    		}
    		
    		try
    		{
    		   ByteArrayInputStream stream=new ByteArrayInputStream(packet.getData(),0,packet.getLength());
    		   MulticastMessageBlock messageBlock=new MulticastMessageBlock();
    		   messageBlock.deserialize(new DataInputStream(stream));
    		
    		   /**
    		    * if set, simulate out of sequence
    		    */
    		   if (_multicastGroupName!=null)
    		   {
    		      if(AppManager.getInstance(_multicastGroupName).isSimulatedMode() && dropPacket())
    		      {
    		         AppManager.getInstance(_multicastGroupName).setSimulatedMode(false);
    		         continue;
    		      }
    		   }
    		
    		   /**
    		    * schedule for processing
    		    */
    		   if(logger.isTraceEnabled())
    		   {
    		      logger.trace("**Queueing Multicast Block : "+messageBlock.toString());
    		   }

    		   _queue.put(messageBlock);
    		}
    		catch(Exception ex)
    		{
    		   logger.error("MulticastReceiver: error when deserializing message block. Exception="+ex, ex);
    		}
    	}
    	
		System.out.println("Receiver Exiting...");
    }
    
    /**
     * get the next message from queue
     * @param poll milliseconds 
     * @return multicast message block
     */
    public MulticastMessageBlock getNextMessage(int pollMilliseconds) throws InterruptedException
    {
    	MulticastMessageBlock message=null;
    	
        try
        {
            message=(MulticastMessageBlock)_queue.poll(pollMilliseconds, TimeUnit.MILLISECONDS);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
			logger.warn("queue.poll() interrupted : "+e.toString());
			throw(e);
        }

    	return(message);
    }
    
    /**
     * drop packets to simulate out of sequence conditions
     * @return
     */
    protected boolean dropPacket() throws Exception
    {
    	boolean flag=false;
    	
    	Random random=new Random();
    	
    	int randomNumber=random.nextInt(100000);
    	
    	if(randomNumber>=7777 && randomNumber<=7778)
    	{
        	System.out.println("Random Number : "+randomNumber);
			System.out.println(">>> About to DROP PACKETS intentionally!!!");
			flag=true;
			Thread.sleep(1000);
    	}
    	
    	return(flag);
    }
    
    /**
     * get the next message from queue
     * TODO provide for a clean way of getting interrupted, by introducing a time unit as to how long to wait
     * @return multicast message block
     * @deprecated
     */
    public MulticastMessageBlock getNextMessage() throws InterruptedException
    {
    	MulticastMessageBlock message=null;
    	
        try
        {
            message=(MulticastMessageBlock)_queue.take();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            logger.warn("queue.take() interrupted : "+e.toString());
            throw(e);
        }

    	return(message);
    }
    
    public EndPointInfo getMulticastEndPoint()
    {
    	return(_endPoint);
    }
    
    public ClientState getClientState()
    {
       return _clientState;
    }
    
    public void sendAlert(String alertType, String msg, byte errorCode)
    {
       try
       {
          //AppManager.sendAlert(_multicastGroupName, _endPoint.getIpAddress()+":"+_endPoint.getPort(), msg, errorCode, _clientState);
          AppManager.sendAlert(alertType+"-"+_endPoint.getIpAddress()+":"+_endPoint.getPort(), _multicastGroupName, msg, errorCode, _clientState);
       }
       catch(Throwable ex)
       {
          logger.error("Failed sending alert: "+ex, ex);
       }
    }
    
    public void sendHealthyStatus()
    {
       if (_forSnapshotChannel)
       {
          return;
       }
       
       try
       {
          //AppManager.sendHealthyStatus(this._clientStatus);
          //_clientState.setComponentStatus(ComponentStatus.UP);
          AppManager.sendComponentStatus(_clientState);
       }
       catch(Throwable ex)
       {
          logger.error("Failed sending healthy status: "+ex, ex);
       }
    }
}

