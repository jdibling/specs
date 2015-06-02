package com.theice.mdf.client.util;

import java.util.Date;
import java.util.logging.LogRecord;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * Only when using java.util.logging (Currently unused, as we use log4j)

 * @author Adam Athimuthu
 * Date: Aug 21, 2007
 * Time: 4:01:18 PM
 */
public class SingleLineFormatter extends java.util.logging.Formatter
{
    /**
     * Formats a given log record.
     *
     * @param record to format
     * @return log record formatted to a single line
     */
    public String format(LogRecord record)
    {
        StringBuffer buf=new StringBuffer("");
        buf.append(MDFUtil.dateFormat.format(new Date(record.getMillis()))+"|");
        buf.append(record.getLevel()+"|");
        buf.append("Thread-"+record.getThreadID()+"|");
        buf.append(record.getSourceClassName()+"."+record.getSourceMethodName()+"()|");
        buf.append(record.getMessage()+"\r\n");
        return(buf.toString());
    }

}
