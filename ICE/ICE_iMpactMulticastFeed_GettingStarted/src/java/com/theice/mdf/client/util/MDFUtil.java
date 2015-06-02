package com.theice.mdf.client.util;

import java.awt.Font;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;

import com.theice.mdf.client.domain.MDFConstants;
import com.theice.mdf.client.domain.Trade;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.RawMessageFactoryImpl;
import com.theice.mdf.message.notification.TradeMessage;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * MDF Utilities
 * 
 * @author Adam Athimuthu
 * Date: Aug 22, 2007
 * Time: 3:30:09 PM
 */
public class MDFUtil
{
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.S");
    public static SimpleDateFormat simpleDateTimeFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public static SimpleDateFormat tradeTimeFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");
    public static SimpleDateFormat tradeMsTimeFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss.S z");
    
    public static Font fontArialPlain12=new Font("Arial",Font.PLAIN,12); 
    public static Font fontArialBold12=new Font("Arial",Font.BOLD,12);
    
    public static long marketDataRecoveryTimeInterval=30*1000;

	public static String linefeed=System.getProperty("line.separator");
	public static final String PROPKEY_MULTICAST_ALTERNATE_MSG_FACTORY_IMPL="multicast.alternate.msg.factory.impl";

	/**
     * get Stack Info
     * @param e
     * @return
     */
    public static String getStackInfo(Throwable e)
    {
        StringBuffer buf=new StringBuffer(e.toString()+"\n");

        StackTraceElement[] stack=e.getStackTrace();

        for(int index=0;index<stack.length;index++)
        {
            buf.append(stack[index].toString()+"\n");
        }

        return(buf.toString());
    }

    /**
     * isBuy
     * @param side
     * @return
     */
    public static boolean isBuy(char side)
    {
        return(side == MDFConstants.BID);
    }

    /**
     * checks if the trade message has to be filtered for capturing statistics
     * 
     * IsSystemPricedLeg flag is not applicable for options markets.
     * 
     * @param message
     * @return
     */
    public static boolean canProcessTrade(Trade trade)
    {
    	boolean flag=false;
    	
    	if(trade.isOptionsTrade())
    	{
    		TradeMessage optionTradeMessage=(TradeMessage) trade.getTradeMessage();
        	flag=(optionTradeMessage.BlockTradeType==' ');
    	}
    	else
    	{
    		TradeMessage tradeMessage=trade.getTradeMessage();
        	flag=(tradeMessage.IsSystemPricedLeg!='Y' && tradeMessage.BlockTradeType==' ' && !tradeMessage.IsLegDealOutsideIPL && tradeMessage.IsImpliedSpreadAtMarketOpen!='Y');
    	}
   
    	return(flag);
    }
    
    public static void setAltMessageFactoryImpl() throws Exception
    {
       String alternateMessageFactoryImpl = System.getProperty(PROPKEY_MULTICAST_ALTERNATE_MSG_FACTORY_IMPL);
       if (alternateMessageFactoryImpl!=null && alternateMessageFactoryImpl.length()>0)
       {
          Class cls = Class.forName(alternateMessageFactoryImpl);
          Class[] parTypes = null;
          Constructor ct = cls.getConstructor(parTypes);
          Object[] argList = null;
          Object implObj = ct.newInstance(argList);

          RawMessageFactory.setRawMessageFactoryImpl((RawMessageFactoryImpl)implObj);
       }
    }
}

