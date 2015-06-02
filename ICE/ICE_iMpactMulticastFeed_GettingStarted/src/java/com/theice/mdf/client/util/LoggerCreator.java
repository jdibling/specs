package com.theice.mdf.client.util;

import java.util.Properties;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Logger Creator - dynamically creates loggers for thread specific logging
 *   
 * @author Adam Athimuthu
 */
public class LoggerCreator
{
	private static final String LOGENTRY_PATTERN="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%t]: %m%n";
	private static final String LOGFILE_PATH="./logs/";

	/**
	 * create thread specific file logger
	 * @param thread
	 * @return
	 * @throws Exception
	 */
	public static Logger createFileLogger(String loggerName) throws Exception
	{
    	Layout layout=new PatternLayout(LOGENTRY_PATTERN);
    	
    	RollingFileAppender fileAppender=new RollingFileAppender();
    	fileAppender.setName(loggerName);
    	fileAppender.setFile(LOGFILE_PATH+loggerName+".log", true, true, 8192);
    	fileAppender.setLayout(layout);
    	
    	Logger logger=Logger.getLogger(loggerName);
    	logger.addAppender(fileAppender);

    	return(logger);
	}
	
    /**
     * Thread logger Test
     * 
     * @author aathimut
     */
	public static class ThreadLoggerTest implements Runnable
    {
		private static ThreadLocal<Logger> threadLocalLogger=new ThreadLocal<Logger>();

		public ThreadLoggerTest()
    	{
    	}
    	
		private void initThread()
    	{
    		try
    		{
    			Logger logger=LoggerCreator.createFileLogger(Thread.currentThread().getName());
    			threadLocalLogger.set(logger);
    		}
    		catch(Exception e)
    		{
    		}
    	}

		public void run()
    	{
			Logger logger=null;
			
			initThread();
			
			logger=(Logger) threadLocalLogger.get();
			logger.info("Init..."+System.currentTimeMillis());

			while(true)
    		{
    			logger.info("Processing..."+System.currentTimeMillis());
    			
    			try
    			{
        			Thread.sleep(4000);
    			}
    			catch(InterruptedException e){}
    		}
    	}
		
		static
		{
			Properties props = System.getProperties();
			props.setProperty("log4j.configuration", "file:./log4j.xml");
		}
		
	    public static void main(String args[]) throws Exception
	    {
			Thread thread=null;
			
	    	Logger logger=LogManager.getLogger(ThreadLoggerTest.class);
	    	logger.info("How are you?");
	    	
	    	logger.error("Sample Error");

	    	for(int index=0;index<3;index++)
	    	{
	    		ThreadLoggerTest simple=new ThreadLoggerTest();
	    		thread=new Thread(simple,"Simple");
	    		thread.start();
	    	}
	    	
			thread.join();
	    }
    }

}

