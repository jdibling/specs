package com.theice.mdf.client.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 * 
 * @author Adam Athimuthu
 */
public class MDFError 
{
	private char _code;
	private String _description;
	
    public static final char CODE_UNKNOWN_REQUEST='1';
    public static final char CODE_INVALID_MARKET_TYPE='2';
    public static final char CODE_MARKET_TYPE_ACCESS_DENIED='3';
    public static final char CODE_LOGIN_REQUIRED='4';
    public static final char CODE_OTHER_ERROR='X';
    
    private static Map<Character,MDFError> _codesMap=new HashMap<Character,MDFError>();
    
    private MDFError(char code, String description)
    {
    	_code=code;
    	_description=description;
    }
    
    public char getCode()
    {
    	return(_code);
    }
    
    public String getDescription()
    {
    	return(_description);
    }
    
    public static MDFError getMDFError(char code)
    {
    	return(_codesMap.get(Character.valueOf(code)));
    }
    
    public String toString()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append("[MDFError="+_code+" - "+_description+"]");
        return(buf.toString());
    }

    public static final MDFError UNKNOWN_REQUEST=new MDFError(CODE_UNKNOWN_REQUEST,"Unknown Request");
    public static final MDFError INVALID_MARKET_TYPE=new MDFError(CODE_INVALID_MARKET_TYPE,"Invalid market type");
    public static final MDFError MARKET_TYPE_ACCESS_DENIED=new MDFError(CODE_MARKET_TYPE_ACCESS_DENIED,"Market type access denied");
    public static final MDFError LOGIN_REQUIRED=new MDFError(CODE_LOGIN_REQUIRED,"Login session required for the request");
    public static final MDFError OTHER_ERROR=new MDFError(CODE_OTHER_ERROR,"Other error");
    
    static
    {
    	_codesMap.put(Character.valueOf(CODE_UNKNOWN_REQUEST), UNKNOWN_REQUEST);
    	_codesMap.put(Character.valueOf(CODE_INVALID_MARKET_TYPE), INVALID_MARKET_TYPE);
    	_codesMap.put(Character.valueOf(CODE_MARKET_TYPE_ACCESS_DENIED), MARKET_TYPE_ACCESS_DENIED);
    	_codesMap.put(Character.valueOf(CODE_LOGIN_REQUIRED), LOGIN_REQUIRED);
    	_codesMap.put(Character.valueOf(CODE_OTHER_ERROR), OTHER_ERROR);
    }

}

