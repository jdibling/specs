package com.theice.mdf.message.response;

import java.nio.ByteBuffer;

import com.theice.mdf.message.FuturesStrategyDefinition;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;

public class FuturesStrategyDefinitionResponse  extends Response
{
	private static final short FIXED_MESSAGE_LENGTH = 97;
	
	public short RequestMarketType;
	public short NumOfMarkets;
	public int MarketID;
	public char ContractSymbol[] = new char[FuturesStrategyDefinition.CONTRACT_SYMBOL_LENGTH];
	public char TradingStatus;
	public char OrderPriceDenominator;
	public int IncrementPremiumPrice; 
	public int IncrementQty;
	public int MinQty;
	public FuturesStrategyLegDefinition[] LegDefinitions;
	
	public FuturesStrategyDefinitionResponse()
	{
		MessageType = RawMessageFactory.FuturesStrategyDefinitionResponseType;
		//MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
	}

	public int getMarketID()
	{
		return(MarketID);
	}
	
   public synchronized byte[] serialize()
	{
		// Buffer is pre-serialized, so that serialization occurs only once.
		if( SerializedContent == null )
		{
		   short repeatingGroupMessageLength = 0;
		   byte numOfLegDefinitions = 0;

		   if (LegDefinitions!=null && LegDefinitions.length>0)
		   {
		      numOfLegDefinitions = (byte)LegDefinitions.length;
		      repeatingGroupMessageLength += (FuturesStrategyLegDefinition.MESSAGE_LENGTH * LegDefinitions.length);
		   }
		   
		   MessageBodyLength = (short)(FIXED_MESSAGE_LENGTH + repeatingGroupMessageLength);
		      
		   SerializedContent = ByteBuffer.allocate( HEADER_LENGTH + MessageBodyLength );
		
			serializeHeader();
			SerializedContent.putInt( RequestSeqID );
			SerializedContent.putShort( RequestMarketType );
			SerializedContent.putShort( NumOfMarkets );
			SerializedContent.putInt( MarketID );
			
			for( int i=0; i<ContractSymbol.length  ; i++ )
			{
				SerializedContent.put( (byte)ContractSymbol[i] );
			}

			SerializedContent.put( (byte)TradingStatus );
			SerializedContent.put( (byte)OrderPriceDenominator);
			SerializedContent.putInt( IncrementPremiumPrice);
			SerializedContent.putInt( IncrementQty );
			SerializedContent.putInt( MinQty );
			SerializedContent.put( numOfLegDefinitions );
			if (numOfLegDefinitions > 0)
			{
			   for(FuturesStrategyLegDefinition legDef : LegDefinitions)
			   {
			      legDef.serialize(SerializedContent);
			   }
			}
						 
			SerializedContent.rewind();
		}
		
		return SerializedContent.array();
	}
	
	public String getShortLogStr()
	{
		// too much for logging, and it is pretty static, just
		// return null
		return null;
	}
	
	public void deserialize( ByteBuffer inboundcontent )
	{
		RequestSeqID = inboundcontent.getInt();
		RequestMarketType = inboundcontent.getShort();
		NumOfMarkets = inboundcontent.getShort();
		MarketID = inboundcontent.getInt();

		for( int i=0; i<ContractSymbol.length  ; i++ )
		{
			ContractSymbol[i] = (char)inboundcontent.get();
		}
		
		TradingStatus = (char)inboundcontent.get();
		OrderPriceDenominator = (char)inboundcontent.get();
		IncrementPremiumPrice = inboundcontent.getInt();
		IncrementQty = inboundcontent.getInt();
		MinQty = inboundcontent.getInt();
		byte numOfLegDefinitions = inboundcontent.get();
		if (numOfLegDefinitions > 0)
		{
		   LegDefinitions = new FuturesStrategyLegDefinition[numOfLegDefinitions];
		   for(int i=0; i<numOfLegDefinitions; i++)
		   {
		      FuturesStrategyLegDefinition legDef = new FuturesStrategyLegDefinition();
		      /*byte msgLength = inboundcontent.get(); //leg body length
		      legDef.LegMarketID = inboundcontent.getInt();
		      legDef.LegUnderlyingMarketID = inboundcontent.getInt();
		      legDef.LegRatio = inboundcontent.getShort();
		      legDef.LegSide = (char)inboundcontent.get();*/
		      legDef.deserialize(inboundcontent);
		      int extraBytes = legDef.getActualMessageLength() - FuturesStrategyLegDefinition.MESSAGE_LENGTH;
		      
		      if (extraBytes > 0)
		      {
		         //need to consume extra bytes (possibly new field(s) have been added to the leg definition
		         consumeExtraBytes(inboundcontent, extraBytes);
		      }
		      		      
		      LegDefinitions[i] = legDef;
		   }
		}
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder(super.toString());
		str.append("RequestMarketType=");
		str.append(RequestMarketType);
		str.append("|NumOfMarkets=");
		str.append(NumOfMarkets);
		str.append("|MarketID=");
		str.append(MarketID);
		str.append("|ContractSymbol=");
		str.append(MessageUtil.toString(ContractSymbol));
		str.append("|TradingStatus=");
		str.append(TradingStatus);
		str.append("|OrderPriceDenominator=");
		str.append(OrderPriceDenominator);
		str.append("|IncrementPremiumPrice=");
		str.append(IncrementPremiumPrice);
		str.append("|IncrementQty=");
		str.append(IncrementQty);
		str.append("|MinQty=");
		str.append(MinQty);
		int numOfLegDefinitions=0;
		if (LegDefinitions!=null)
		{
		   numOfLegDefinitions=LegDefinitions.length;
		}
		str.append("|NumberOfLegDefinition=");
		str.append(numOfLegDefinitions);
		str.append("|");
		for(int i=0;i<numOfLegDefinitions;i++)
		{
		   FuturesStrategyLegDefinition legDef = LegDefinitions[i];
		   str.append(legDef.toString());
		}
		str.append("|");
		return str.toString();
	}
	
	public void setMarketID(int MarketID)
	{
		this.MarketID = MarketID;
	}
	
	private void consumeExtraBytes(ByteBuffer inboundContent, int numOfExtraBytes)
	{
	   //need to consume extra bytes (possibly new field(s) have been added to the leg definition
      for(int i=0; i < numOfExtraBytes; i++)
      {
         inboundContent.get();
      }
	}
	
}


