package com.theice.mdf.client.gui;

import com.theice.mdf.client.config.MDFClientConfigurator;
import com.theice.mdf.client.config.domain.MDFClientConfiguration;
import com.theice.mdf.client.domain.MDFError;
import com.theice.mdf.client.domain.MDSubscriber;
import com.theice.mdf.client.examples.SimpleClientConfigurator;
import com.theice.mdf.client.process.handlers.ErrorResponseHandler;
import com.theice.mdf.client.process.handlers.SystemTextHandler;
import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.notification.SystemTextMessage;
import com.theice.mdf.message.response.ErrorResponse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 * Date: Aug 2, 2007
 * Time: 12:44:34 PM
 */
public class MDFStatusBar extends JPanel implements MDSubscriber
{
    private JLabel status;

    private Date date = new Date();
    private JLabel heartbeat=new JLabel("");

    private static MDFStatusBar _instance=new MDFStatusBar();
    private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa MM/dd/yyyy");
    
    private static final int SYSMSG_XPOS=MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters().getSystemTextWindowLocationXPos();
    private static final int SYSMSG_YPOS=MDFClientConfigurator.getInstance().getConfigRepository().getMDFClientRuntimeParameters().getSystemTextWindowLocationYPos();

//  private final java.util.Timer timer = new java.util.Timer();
//  private final int interval=5*1000;

    /**
     * Connection status
     */
    private boolean connected=false;

    private MDFStatusBar()
    {
        init();
    }

    public static MDFStatusBar getInstance()
    {
        return _instance;
    }

    /**
     * init
     * subscribe to System/Error Message Events
     */
    private void init()
    {
        setLayout(new BorderLayout());
        
        status=new JLabel("Not Connected");
        status.setMinimumSize(new Dimension(100,400));
        status.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(status, BorderLayout.WEST);

        JPanel heartBeatHolder = new JPanel(new BorderLayout());
        heartBeatHolder.add(heartbeat, BorderLayout.EAST);
        heartBeatHolder.setBorder(BorderFactory.createLineBorder(Color.gray));
        add(heartBeatHolder, BorderLayout.EAST);
        
        SystemTextHandler.getInstance().addSubscriber(this);
        ErrorResponseHandler.getInstance().addSubscriber(this);
    }

    /**
     * Set Status of the connection
     * @param connectionStatus
     */
    public void setStatus(boolean connectionStatus)
    {
        this.connected=connectionStatus;
        
        MDFClientConfiguration configuration=MDFClientConfigurator.getInstance().getCurrentConfiguration();

        if(this.connected)
        {
            this.status.setText("Connected to : "+configuration.getTcpInfo().getEndPointInfo().getDisplayable());
        }
        else
        {
            this.status.setText("Not Connected");
        }
    }

    /**
     * MD Subscriber event notifications
     * @param message
     */
    public synchronized void notifyWithMDMessage(MDMessage message)
    {
    	char messageType=message.getMessageType();
      
        switch(messageType)
        {
//			case RawMessageFactory.HeartBeatMessageType:
//		        HeartBeatMessage theMessage=(HeartBeatMessage) message;
//		        heartbeat.setText("Heartbeat: "+sdf.format(theMessage.DateTime));
//				break;
	
			case RawMessageFactory.SystemTextMessageType:
		        SystemTextMessage systemTextMessage=(SystemTextMessage) message;
		        StringBuffer textMessage=new StringBuffer(MessageUtil.toString(systemTextMessage.Text));
		        textMessage.append(MessageUtil.toString(systemTextMessage.TextExtraFld));

				new SystemTextDialog(null,textMessage.toString(),systemTextMessage.DateTime,SYSMSG_XPOS,SYSMSG_YPOS);
				
				break;
				
			case RawMessageFactory.ErrorResponseType:
				
				final ErrorResponse errorMessage=(ErrorResponse) message;
				final MDFError errorCode=MDFError.getMDFError(errorMessage.Code);
				
				new Thread()
				{
					public void run()
					{
						JOptionPane.showMessageDialog(null,
								MessageUtil.toString(errorMessage.Text),
								errorCode.getDescription(),
                                JOptionPane.ERROR_MESSAGE);				
					}
				}.start();
				
				break;
				
			default:
				System.out.println("Notification received for unknown message type: "+ messageType);
				break;
		}
    }
    
//    /**
//     * Start the simulation timer
//     */
//    protected void startSimulationTimer()
//    {
//        timer.scheduleAtFixedRate(
//            new TimerTask()
//            {
//                public void run()
//                {
//                    SystemTextMessage systemText=new SystemTextMessage();
//                    systemText.DateTime=System.currentTimeMillis();
//                    systemText.Text="######## Sample System Text ########".toCharArray();
//                    notifyWithMDMessage(systemText);
//                }
//            }, interval,interval);
//    }

}

