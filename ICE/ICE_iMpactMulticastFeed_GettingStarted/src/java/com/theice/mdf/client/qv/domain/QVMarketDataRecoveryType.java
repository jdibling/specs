package com.theice.mdf.client.qv.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * THE CLASSES USED HERE, INCLUDING THE MESSSAGE CLASSES ARE EXAMPLE CODES ONLY.
 * THEY WON'T BE SUPPORTED AS LIBRARY.
 *
 * @author Adam Athimuthu
 */
public class QVMarketDataRecoveryType 
{
	private char _code;
	private String _value;
	
    public static final char CODE_ALL='A';
    public static final char CODE_ORDER='O';
    public static final char CODE_DEAL='D';
    
    private static Map<Character,QVMarketDataRecoveryType> _codesMap=new LinkedHashMap<Character,QVMarketDataRecoveryType>();
    
    private QVMarketDataRecoveryType(char code, String value)
    {
    	_code=code;
    	_value=value;
    }
    
    public char getCode()
    {
    	return(_code);
    }
    
    public String getValue()
    {
    	return(_value);
    }
    
    public static QVMarketDataRecoveryType lookup(char code)
    {
    	return(_codesMap.get(Character.valueOf(code)));
    }
    
    public static Collection<QVMarketDataRecoveryType> getAll()
    {
    	return(_codesMap.values());
    }
    
    public String toString()
    {
        StringBuffer buf=new StringBuffer("");
        buf.append(_code+" - "+_value);
        return(buf.toString());
    }

    public static final QVMarketDataRecoveryType ALL=new QVMarketDataRecoveryType(CODE_ALL,"All Messages");
    public static final QVMarketDataRecoveryType ORDER=new QVMarketDataRecoveryType(CODE_ORDER,"Order Related");
    public static final QVMarketDataRecoveryType DEAL=new QVMarketDataRecoveryType(CODE_DEAL,"Deal Related");
    
    static
    {
    	_codesMap.put(Character.valueOf(CODE_ALL), ALL);
    	_codesMap.put(Character.valueOf(CODE_ORDER), ORDER);
    	_codesMap.put(Character.valueOf(CODE_DEAL), DEAL);
    }

}

