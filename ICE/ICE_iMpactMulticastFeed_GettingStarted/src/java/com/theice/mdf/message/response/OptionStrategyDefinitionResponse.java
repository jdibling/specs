package com.theice.mdf.message.response;

import java.nio.ByteBuffer;

import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.OptionStrategyDefinition;
import com.theice.mdf.message.RawMessageFactory;

/**
 * OptionsProductDefinitionResponse
 * 
 * This response is generated when the client requests a Product Definition Request with the
 * Security Type set to 'O' (options)
 * 
 * @author Adam Athimuthu
 */
public class OptionStrategyDefinitionResponse  extends Response
{
	private static final short FIXED_MESSAGE_LENGTH = 67;
	
	public short RequestMarketType;
	public short NumOfMarkets;
	public int MarketID;
	public int UnderlyingMarketID;
	public char ContractSymbol[] = new char[OptionStrategyDefinition.CONTRACT_SYMBOL_LENGTH];
	public char TradingStatus;
	public char OrderPriceDenominator;
	public int IncrementPremiumPrice; 
	public int IncrementQty;
	public int MinQty;
	public OptionStrategyLegDefinition[] LegDefinitions;
	public OptionStrategyHedgeDefinition[] HedgeDefinitions;
	
	public OptionStrategyDefinitionResponse()
	{
		MessageType = RawMessageFactory.OptionStrategyDefinitionResponseType;
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
		   byte numOfHedgeDefinitions = 0;
		   if (LegDefinitions!=null && LegDefinitions.length>0)
		   {
		      numOfLegDefinitions = (byte)LegDefinitions.length;
		      repeatingGroupMessageLength += (OptionStrategyLegDefinition.MESSAGE_LENGTH * LegDefinitions.length);
		   }
		   if (HedgeDefinitions!=null && HedgeDefinitions.length>0)
		   {
		      numOfHedgeDefinitions = (byte)HedgeDefinitions.length;
		      repeatingGroupMessageLength += (OptionStrategyHedgeDefinition.MESSAGE_LENGTH * HedgeDefinitions.length);
		   }
		   
		   MessageBodyLength = (short)(FIXED_MESSAGE_LENGTH + repeatingGroupMessageLength);
		      
		   SerializedContent = ByteBuffer.allocate( HEADER_LENGTH + MessageBodyLength );
		
			serializeHeader();
			SerializedContent.putInt( RequestSeqID );
			SerializedContent.putShort( RequestMarketType );
			SerializedContent.putShort( NumOfMarkets );
			SerializedContent.putInt( MarketID );
			SerializedContent.putInt( UnderlyingMarketID );
			
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
			   for(OptionStrategyLegDefinition legDef : LegDefinitions)
			   {
			      legDef.serialize(SerializedContent);
			   }
			}
			
			SerializedContent.put( numOfHedgeDefinitions );
			if (numOfHedgeDefinitions > 0)
			{
			   for(OptionStrategyHedgeDefinition hedgeDef : HedgeDefinitions)
			   {
			      hedgeDef.serialize(SerializedContent);
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
		UnderlyingMarketID = inboundcontent.getInt();

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
		   LegDefinitions = new OptionStrategyLegDefinition[numOfLegDefinitions];
		   for(int i=0; i<numOfLegDefinitions; i++)
		   {
		      OptionStrategyLegDefinition legDef = new OptionStrategyLegDefinition();
		      /*byte msgLength = inboundcontent.get(); //leg body length
		      legDef.LegMarketID = inboundcontent.getInt();
		      legDef.LegUnderlyingMarketID = inboundcontent.getInt();
		      legDef.LegRatio = inboundcontent.getShort();
		      legDef.LegSide = (char)inboundcontent.get();*/
		      legDef.deserialize(inboundcontent);
		      int extraBytes = legDef.getActualMessageLength() - OptionStrategyLegDefinition.MESSAGE_LENGTH;
		      
		      if (extraBytes > 0)
		      {
		         //need to consume extra bytes (possibly new field(s) have been added to the leg definition
		         consumeExtraBytes(inboundcontent, extraBytes);
		      }
		      		      
		      LegDefinitions[i] = legDef;
		   }
		}
		byte numOfHedgeDefinitions = inboundcontent.get();
		if (numOfHedgeDefinitions > 0)
		{
		   HedgeDefinitions = new OptionStrategyHedgeDefinition[numOfHedgeDefinitions];
		   for(int i=0; i<numOfHedgeDefinitions; i++)
		   {
		      OptionStrategyHedgeDefinition hedgeDef = new OptionStrategyHedgeDefinition();
		      /*byte msgLength = inboundcontent.get();
		      hedgeDef.HedgeMarketID = inboundcontent.getInt();
		      hedgeDef.HedgeSecurityType = (char)inboundcontent.get();
		      hedgeDef.HedgeSide = (char)inboundcontent.get();
		      hedgeDef.HedgePrice = inboundcontent.getLong();
		      hedgeDef.HedgePriceDenominator = (char)inboundcontent.get();
		      hedgeDef.HedgeDelta = inboundcontent.getShort();*/
		      hedgeDef.deserialize(inboundcontent);
		      int extraBytes = hedgeDef.getActualMessageLength() - OptionStrategyHedgeDefinition.MESSAGE_LENGTH;
            if (extraBytes > 0)
            {
               //need to consume extra bytes (possibly new field(s) have been added to the hedge definition
               consumeExtraBytes(inboundcontent, extraBytes);
            }
            HedgeDefinitions[i] = hedgeDef;
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
		str.append("|UnderlyingMarketID=");
		str.append(UnderlyingMarketID);
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
		int numOfHedgeDefinitions=0;
		if (LegDefinitions!=null)
		{
		   numOfLegDefinitions=LegDefinitions.length;
		}
		if (HedgeDefinitions!=null)
		{
		   numOfHedgeDefinitions=HedgeDefinitions.length;
		}
		str.append("|NumberOfLegDefinition=");
		str.append(numOfLegDefinitions);
		str.append("|");
		for(int i=0;i<numOfLegDefinitions;i++)
		{
		   OptionStrategyLegDefinition legDef = LegDefinitions[i];
		   str.append(legDef.toString());
		}
		str.append("NumberOfHedgeDefinition=");
		str.append(numOfHedgeDefinitions);
		str.append("|");
		for(int i=0;i<numOfHedgeDefinitions;i++)
      {
         OptionStrategyHedgeDefinition hedgeDef = HedgeDefinitions[i];
         str.append(hedgeDef.toString());
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


