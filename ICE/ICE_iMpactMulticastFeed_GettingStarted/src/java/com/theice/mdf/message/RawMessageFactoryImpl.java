package com.theice.mdf.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.theice.mdf.message.notification.*;
import com.theice.mdf.message.pricelevel.*;
import com.theice.mdf.message.request.*;
import com.theice.mdf.message.response.*;

/**
 * The standard message object factory implementation
 * 
 * @author David Chen
 * @version 1.0
 * @created 05-Dec-2006 15:01:54
 */
public class RawMessageFactoryImpl
{
   private final static short MAX_MESSAGE_BODY_LENGTH = 1024;

   public MDMessage getObject(DataInputStream inputStream, ByteBuffer byteBuffer) throws IOException,
         UnknownMessageException, InvalidRequestException
   {
      MDMessage theBase = null;

      // read the message type and body length
      byte[] bytes = new byte[3];
      if (inputStream != null)
      {
         inputStream.readFully(bytes);
      }
      else
      {
         byteBuffer.get(bytes);
      }

      MDMessage monitoringReqMsg = getCustomRequest(bytes);
      if(monitoringReqMsg!=null){
    	  return monitoringReqMsg;    		 
      }
      
      // instantiate an empty object
      byte messageType = bytes[0];
      theBase = getObject(messageType);
      short bodyLength;
      if ((theBase != null) && (theBase.getMessageType() == RawMessageFactory.DebugRequestType))
      {
         // treat debug request differently so that it works when it is sent
         // through
         // telnet or F5 because it is hard to manipulate the ASCII string to
         // come up
         // with a binary short value of 4, just use the hardcoded FIXED LENGTH
         bodyLength = 4;
      }
      else
      {
         // 2nd and 3rd byte are used for body length
         bodyLength = ByteBuffer.wrap(bytes, 1, 2).getShort();
      }

      // make sure that length value is reasonable, most likely it could
      // be programmatic error or potential attack from client in requests
      if (bodyLength > MAX_MESSAGE_BODY_LENGTH)
      {
         throw new InvalidRequestException("Invalid request, message body length: " + bodyLength + ", over the limit.");
      }

      // read the body with the length received
      byte messageBodyBytes[] = new byte[bodyLength];
      if (inputStream != null)
      {
         inputStream.readFully(messageBodyBytes, 0, bodyLength);
      }
      else
      {
         byteBuffer.get(messageBodyBytes);
      }

      if (theBase != null)
      {
         theBase.MessageBodyLength = bodyLength;

         // deserialize the body
         theBase.deserialize(ByteBuffer.wrap(messageBodyBytes));
      }
      else
      {
         throw new UnknownMessageException("Unknown message type: " + (char) messageType);
      }

      return theBase;
   }
      
   public MDMessage getCustomRequest(byte[] bytes) {
	   return null;   
   }

	protected MDMessage getObject( byte theMessageType ) throws UnknownMessageException
   {
		MDMessage theBase = null;

		switch( (char) theMessageType )
		{

			case RawMessageFactory.DebugRequestType:
				theBase = new DebugRequest();
			   break;

			case RawMessageFactory.DebugResponseType:
				theBase = new DebugResponse();
            break;

			case RawMessageFactory.ErrorResponseType:
				theBase = new ErrorResponse();
			   break;

			case RawMessageFactory.LoginRequestType:
				theBase = new LoginRequest();
			   break;

			case RawMessageFactory.LoginResponseType:
				theBase = new LoginResponse();
			   break;

			case RawMessageFactory.LogoutRequestType:
				theBase = new LogoutRequest();
			   break;

			case RawMessageFactory.MarketSnapshotMessageType:
				theBase = new MarketSnapshotMessage();
			   break;

			case RawMessageFactory.ProductDefinitionRequestType:
				theBase = new ProductDefinitionRequest();
			   break;

			case RawMessageFactory.ProductDefinitionResponseType:
				theBase = new ProductDefinitionResponse();
			   break;
			   
			case RawMessageFactory.OptionsProductDefinitionResponseType:
				theBase = new OptionsProductDefinitionResponse();
			   break;

         case RawMessageFactory.OptionStrategyDefinitionResponseType:
            theBase = new OptionStrategyDefinitionResponse();
            break;

         case RawMessageFactory.FuturesStrategyDefinitionResponseType:
             theBase = new FuturesStrategyDefinitionResponse();
             break;
            
			case RawMessageFactory.AddModifyOrderMessageType:
				theBase = new AddModifyOrderMessage();
			   break;

			case RawMessageFactory.CancelledTradeMessageType:
				theBase = new CancelledTradeMessage();
			   break;

			case RawMessageFactory.DeleteOrderMessageType:
				theBase = new DeleteOrderMessage();
			   break;

			case RawMessageFactory.HeartBeatMessageType:
				theBase = new HeartBeatMessage();
			   break;

			case RawMessageFactory.InvestigatedTradeMessageType:
				theBase = new InvestigatedTradeMessage();
			   break;

			case RawMessageFactory.MarketStateChangeMessageType:
				theBase = new MarketStateChangeMessage();
			   break;

			case RawMessageFactory.MarketStatisticsMessageType:
				theBase = new MarketStatisticsMessage();
			   break;

			case RawMessageFactory.OpenInterestMessageType:
				theBase = new OpenInterestMessage();
			   break;

			case RawMessageFactory.OpenPriceMessageType:
				theBase = new OpenPriceMessage();
			   break;

			case RawMessageFactory.SettlementPriceMessageType:
				theBase = new SettlementPriceMessage();
			   break;

			case RawMessageFactory.SystemTextMessageType:
				theBase = new SystemTextMessage();
			   break;

			case RawMessageFactory.TradeMessageType:
				theBase = new TradeMessage();
			   break;
			case RawMessageFactory.SpotMarketTradeMessageType:
				theBase = new SpotMarketTradeMessage();
			   break;

			case RawMessageFactory.BundleMarkerMessageType:
				theBase = new BundleMarkerMessage();
			   break;

			case RawMessageFactory.NewOptionStrategyDefinitionMessageType:
			   theBase = new NewOptionStrategyDefinitionMessage();
			   break;

			case RawMessageFactory.NewFuturesStrategyDefinitionMessageType:
				   theBase = new NewFuturesStrategyDefinitionMessage();
				   break;

			case RawMessageFactory.QVMarkerIndexPriceResponseType:
            theBase = new MarkerIndexPriceMessage();
            break;

         case RawMessageFactory.HistoricalMarketDataRequestType:
            theBase = new HistoricalMarketDataRequest();
            break;

         case RawMessageFactory.HistoricalMarketDataResponseType:
            theBase = new HistoricalMarketDataResponse();
            break;

         case RawMessageFactory.QVEndOfDayMarketSummaryMessageType:
            theBase = new EndOfDayMarketSummaryMessage();
            break;

         case RawMessageFactory.AddPriceLevelMessageType:
            theBase = new AddPriceLevelMessage();
            break;

         case RawMessageFactory.ChangePriceLevelMessageType:
            theBase = new ChangePriceLevelMessage();
            break;

         case RawMessageFactory.DeletePriceLevelMessageType:
            theBase = new DeletePriceLevelMessage();
            break;

         case RawMessageFactory.MarketSnapshotOrderMessageType:
            theBase = new MarketSnapshotOrderMessage();
            break;

         case RawMessageFactory.MarketSnapshotPriceLevelMessageType:
            theBase = new MarketSnapshotPriceLevelMessage();
            break;

         case RawMessageFactory.TunnelingProxyRequestType:
            theBase = new TunnelingProxyRequest();
            break;

         case RawMessageFactory.TunnelingProxyResponseType:
            theBase = new TunnelingProxyResponse();
            break;

         case RawMessageFactory.MarketEventMessageType:
            theBase = new MarketEventMessage();
            break;

         case RawMessageFactory.PreOpenPriceIndicatorMessageType:
            theBase = new PreOpenPriceIndicatorMessage();
            break;
            
         case RawMessageFactory.OptionOpenInterestMessageType:
            theBase = new OptionOpenInterestMessage();
            break;
            
         case RawMessageFactory.OptionSettlementPriceMessageType:
            theBase = new OptionSettlementPriceMessage();
            break;

         case RawMessageFactory.StripInfoMessageType:
            theBase = new StripInfoMessage();
            break;

         case RawMessageFactory.RequestForQuoteMessageType:
            theBase = new RequestForQuoteMessage();
            break;
            
         case RawMessageFactory.NewOptionsMarketDefinitionMessageType:
            theBase = new NewOptionsMarketDefinitionMessage();
            break;

         case RawMessageFactory.IntervalPriceLimitNotificationMessageType:
            theBase = new IntervalPriceLimitNotificationMessage();
            break;
            
         case RawMessageFactory.OldStyleOptionsTradeAndMarketStatsMessageType:
            theBase = new OldStyleOptionsTradeAndMarketStatsMessage();
            break;
            
         case RawMessageFactory.AuctionNotificationMessageType:
            theBase = new AuctionNotificationMessage();
            break;
            
         case RawMessageFactory.UnknownTestMessageType:
            theBase = new UnknownTestMessage();
            break;

         default:
            // unknow message, don't know how to instantiate, set it to null
            theBase = null;

		}
		return theBase;
	}

   public MDSequencedMessage getSequencedMessage(FileChannel inputFileChannel)
         throws IOException, InvalidRequestException, UnknownMessageException
   {
      ByteBuffer headerBuffer = ByteBuffer.allocate(MDMessage.HEADER_LENGTH);      
      inputFileChannel.read(headerBuffer);      
      headerBuffer.rewind();
      MDSequencedMessage sequencedMessage = (MDSequencedMessage) getObject(headerBuffer
            .get());
      short messageBodyLength = 0;
      messageBodyLength = headerBuffer.getShort();
      sequencedMessage.MessageBodyLength = messageBodyLength;
      // each message has an extra byte '|' at the end in the persisted file
      ByteBuffer inboundcontent = ByteBuffer.allocate(messageBodyLength + 1);
      inputFileChannel.read(inboundcontent);
      inboundcontent.rewind();
      byte messageDelimeter = inboundcontent.get(messageBodyLength);
      sequencedMessage.deserialize(inboundcontent);
      return sequencedMessage;
   }

   public MDSequencedMessageWithMarketID getSequencedMessageWithMarketID(ByteBuffer allBytesForMessage)
         throws IOException, InvalidRequestException, UnknownMessageException
   {
      MDSequencedMessageWithMarketID sequencedMessageWithMarketID = (MDSequencedMessageWithMarketID) getObject(allBytesForMessage
            .get());
      short totalBodyLength = 0;
      totalBodyLength = allBytesForMessage.getShort();
      sequencedMessageWithMarketID.MessageBodyLength = totalBodyLength;
      sequencedMessageWithMarketID.deserialize(allBytesForMessage);
      return sequencedMessageWithMarketID;
   }
   
   public void readAndDumpMessages(FileChannel inputFileChannel) throws IOException
   {
      int sequenceNumber=0;
      while(true)
      {
         ByteBuffer headerBuffer = ByteBuffer.allocate(MDMessage.HEADER_LENGTH);      
         int bytesRead = inputFileChannel.read(headerBuffer); 
         if (bytesRead <= 0)
         {
            break;
         }
         headerBuffer.rewind();
         MDSequencedMessage sequencedMessage = null;
         byte messageType = headerBuffer.get();
         
         try
         {
            sequencedMessage = (MDSequencedMessage) getObject(messageType);
         }
         catch(UnknownMessageException ex)
         {
            System.out.println("Unknown Message: "+ex.getMessage());
         }

         short messageBodyLength = 0;
         messageBodyLength = headerBuffer.getShort();
         // each message has an extra byte '|' at the end in the persisted file
         ByteBuffer inboundcontent = ByteBuffer.allocate(messageBodyLength + 1);
         inputFileChannel.read(inboundcontent);
         inboundcontent.rewind();
         sequenceNumber++;
         if (sequencedMessage!=null)
         {
            sequencedMessage.MessageBodyLength = messageBodyLength;
            sequencedMessage.deserialize(inboundcontent);
            sequencedMessage.setSequenceNumber(sequenceNumber);
            System.out.println("Message: "+sequencedMessage);
         }
         else
         {
            System.out.println("Unknown Message: MessageType="+(char)messageType+"|MessageBodyLength="+messageBodyLength+"|SequenceNumber="+sequenceNumber);
         }
      };
   }
}
