package com.theice.mdf.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Message factory class that would create message objects from inputstream
 *
 * @author David Chen
 * @version 1.0
 * @created 05-Dec-2006 15:01:54
 */

public class RawMessageFactory
{
   private static RawMessageFactoryImpl FactoryImpl = new RawMessageFactoryImpl();

	public final static char DebugRequestType                		= '5';
	public final static char DebugResponseType               		= 'P';
	public final static char ErrorResponseType               		= 'S';
	public final static char LoginRequestType                		= '1';
	public final static char LoginResponseType               		= 'A';
	public final static char LogoutRequestType               		= '6';
	public final static char LogoutResponseType              		= 'R';
	public final static char MarketSnapshotMessageType       		= 'C';
	public final static char MarketSnapshotOrderMessageType  		= 'D';
	public final static char ProductDefinitionRequestType    		= '2';
	public final static char ProductDefinitionResponseType   		= 'B';
	public final static char OptionsProductDefinitionResponseType	= 'p';
	public final static char OptionStrategyDefinitionResponseType  = 'q';
	public final static char FuturesStrategyDefinitionResponseType  = 'd';
	
	public final static char AddModifyOrderMessageType       		= 'E';
	public final static char CancelledTradeMessageType       		= 'I';
	public final static char DeleteOrderMessageType          		= 'F';
	public final static char HeartBeatMessageType            		= 'Q';
	public final static char InvestigatedTradeMessageType    		= 'H';
	public final static char MarketStateChangeMessageType    		= 'K';
	public final static char MarketStatisticsMessageType     		= 'J';
	public final static char OpenInterestMessageType         		= 'M';
	public final static char OpenPriceMessageType            		= 'N';
	public final static char SettlementPriceMessageType      		= 'O';
	public final static char SystemTextMessageType           		= 'L';
	public final static char TradeMessageType                		= 'G';
	public final static char SpotMarketTradeMessageType             = 'Y';
	public final static char BundleMarkerMessageType         		= 'T';
	public final static char NewOptionStrategyDefinitionMessageType= 'U';
	public final static char NewFuturesStrategyDefinitionMessageType= '9';
	public final static char IntervalPriceLimitNotificationMessageType='V';
	public final static char OldStyleOptionsTradeAndMarketStatsMessageType='W';
	public final static char AuctionNotificationMessageType        = 'X';
   public final static char HistoricalMarketDataRequestType     	= '7';
   public final static char HistoricalMarketDataResponseType    	= '8';
   public final static char MarketEventMessageType              	= 'f';
   public final static char PreOpenPriceIndicatorMessageType      = 'g';   
   public final static char StripInfoMessageType                  = 'i';
   public final static char RequestForQuoteMessageType            = 'k';
   public final static char NewOptionsMarketDefinitionMessageType = 'l';

   //qv message types
   public final static char QVMarkerIndexPriceResponseType        = 'z';
   public final static char QVEndOfDayMarketSummaryMessageType    = 'u';
   public final static char OptionOpenInterestMessageType         = 'v';
   public final static char OptionSettlementPriceMessageType      = 'w';
   
   //Price level message types
   public final static char AddPriceLevelMessageType               = 't';
   public final static char ChangePriceLevelMessageType            = 's';
   public final static char DeletePriceLevelMessageType            = 'r';
   public final static char MarketSnapshotPriceLevelMessageType    = 'm';

   /**
    * Special Messages
    */
   public final static char TunnelingProxyRequestType              = '!';
   public final static char TunnelingProxyResponseType             = '@';
   public final static char UnknownTestMessageType                 = '?';
   public final static char MonitoringRequestType                  = '+';
   public final static char MonitoringResponseType                 = '-';
   
   private RawMessageFactory()
   {
   }

	public static MDMessage getObject( DataInputStream inputStream )
      throws IOException, UnknownMessageException, InvalidRequestException
	{
      return FactoryImpl.getObject(inputStream, null);
	}

   public static MDMessage getObject(ByteBuffer byteBuffer) throws IOException, UnknownMessageException,
         InvalidRequestException
   {
      return FactoryImpl.getObject(null, byteBuffer);
   }
   
   public static void setRawMessageFactoryImpl(RawMessageFactoryImpl impl)
   {
      FactoryImpl = impl;
   }
}
	