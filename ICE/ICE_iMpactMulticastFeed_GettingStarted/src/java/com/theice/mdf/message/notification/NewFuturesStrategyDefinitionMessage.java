package com.theice.mdf.message.notification;

import java.nio.ByteBuffer;

import com.theice.mdf.message.FuturesStrategyDefinition;
import com.theice.mdf.message.MDSequencedMessageWithMarketID;
import com.theice.mdf.message.MessageUtil;
import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.response.FuturesStrategyLegDefinition;

/**
 * NewFuturesStrategyDefinitionMessage
 */
public class NewFuturesStrategyDefinitionMessage extends MDSequencedMessageWithMarketID
{
	//TODO : 12.800 check message length
   public static final short FIXED_MESSAGE_LENGTH = 90;

   public char ContractSymbol[] = new char[FuturesStrategyDefinition.CONTRACT_SYMBOL_LENGTH];
   public char TradingStatus;
   public char OrderPriceDenominator;
   public int IncrementPremiumPrice; 
   public int IncrementQty;
   public int MinQty;
   public FuturesStrategyLegDefinition[] LegDefinitions;

   public NewFuturesStrategyDefinitionMessage()
   {
      MessageType = RawMessageFactory.NewFuturesStrategyDefinitionMessageType;
      //MessageBodyLength = MESSAGE_LENGTH - HEADER_LENGTH;
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

   public void deserializeContent( ByteBuffer inboundcontent )
   {
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

   private void consumeExtraBytes(ByteBuffer inboundContent, int numOfExtraBytes)
   {
      //need to consume extra bytes (possibly new field(s) have been added to the leg definition
      for(int i=0; i < numOfExtraBytes; i++)
      {
         inboundContent.get();
      }
   }
}