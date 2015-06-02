package com.theice.mdf.client.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class InvestigationStatus
{
	private char _status;
	private String _statusDescription;
	
    public static final char STATUS_UNDER_INVESTIGATION='1';
    public static final char STATUS_INVESTIGATION_COMPLETED='2';
    
    private static Map<Character,InvestigationStatus> _codesMap=new HashMap<Character,InvestigationStatus>();
    
    private InvestigationStatus(char status, String statusDescription)
    {
    	_status=status;
    	_statusDescription=statusDescription;
    }
    
    public char getStatus()
    {
    	return(_status);
    }
    
    public String getStatusDescription()
    {
    	return(_statusDescription);
    }
    
    public static InvestigationStatus getInvestigationStatus(char status)
    {
    	return(_codesMap.get(Character.valueOf(status)));
    }
    
    public String toString()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append("[InvestigationStatus="+_status+" - "+_statusDescription+"]");
        return(buf.toString());
    }

    public static final InvestigationStatus UNDER_INVESTIGATIOIN=new InvestigationStatus(STATUS_UNDER_INVESTIGATION,
    		"Under Investigation");
    public static final InvestigationStatus INVESTIGATIOIN_COMPLETED=new InvestigationStatus(STATUS_INVESTIGATION_COMPLETED,
    		"Investigation Completed");
    
    static
    {
    	_codesMap.put(Character.valueOf(STATUS_UNDER_INVESTIGATION), UNDER_INVESTIGATIOIN);
    	_codesMap.put(Character.valueOf(STATUS_INVESTIGATION_COMPLETED), INVESTIGATIOIN_COMPLETED);
    }

}

