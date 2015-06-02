package com.theice.mdf.client.process.handlers;

import com.theice.mdf.message.MDMessage;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.response.LoginResponse;
import com.theice.mdf.client.message.PriceFeedMessage;
import com.theice.mdf.client.process.AppManager;
import com.theice.mdf.client.process.context.MDFAppContext;
import org.apache.log4j.Logger;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 * Date: Sep 4, 2007
 */
public class LoginResponseHandler extends AbstractMarketMessageHandler
{
    private static LoginResponseHandler _instance=new LoginResponseHandler();

    private static Logger logger=Logger.getLogger(LoginResponseHandler.class.getName());

    private LoginResponseHandler()
    {
    }

    public static LoginResponseHandler getInstance()
    {
        return(_instance);
    }
    
    /**
     * handle the message
     * @param message
     */
    protected void handleMessage(PriceFeedMessage priceFeedMessage)
    {
    	MDMessage message=priceFeedMessage.getMessage();
    	
        String log="*** Login Response: "+message.toString();
        
        MDFAppContext appContext=AppManager.getAppContext();
        appContext.logEssential(log);
        LoginResponse loginResponse=(LoginResponse) message;
        
        if(loginResponse.Code!=LoginResponse.CODE_LOGIN_SUCCESS)
        {
        	StringBuffer errorMessage=new StringBuffer("Aborting due to login failure: ");
        	errorMessage.append(MessageUtil.toString(loginResponse.Text));
        	logger.error(errorMessage.toString());
            System.err.println(log);
        	appContext.alert(errorMessage.toString());
        	System.exit(2);
        }
        
        logger.info(log);
        System.out.println(log);
        
        return;
        
    }
}

