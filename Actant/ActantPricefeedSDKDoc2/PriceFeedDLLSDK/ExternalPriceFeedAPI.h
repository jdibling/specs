#ifndef _ExtPfApi_h_
#define _ExtPfApi_h_

///////////////////////////////////////////////////////////////////////////////
//
// ExternalPriceFeedAPI:
//
// The entrypoints, enums and structures required to write a ClientModule DLL.
// This function is shared by the calling IIO.
// This file should be included in every file involved in implementing the
// interface with the calling IIO. It should not be changed and represents the
// only supported means of implementing a ClientModule DLL.
//
///////////////////////////////////////////////////////////////////////////////

#pragma pack(push, 8)

const int EX_PF_API_VERSION	= 4;

//
// External Price Feed interface details
//
namespace ExtPF
{
	enum OptionType
	{
		OTUndefined			= ' ',
		OTCall				= 'C',
		OTPut				= 'P'
	};

	enum InstrumentType
	{
		ITUndefined			= 0,
		ITStock				= 1,
		ITIndex				= 2,
		ITOption			= 3,
		ITFuture			= 4,
		ITStrategy			= 5
	};

	enum UpdateType
	{
		UTInvalid			=  0,
		UTBid				=  1,
		UTAsk				=  2,	
		UTLast				=  3,
		UTTotalVolume		=  4,
		UTOpenInterest		=  5,
		UTOpen				=  7,
		UTHigh				=  8,
		UTLow				=  9,
		UTSettle			= 10,
		UTYClose			= 12,
		UTChange			= 13,
		UTBidDirection		= 15,
	};

	enum LogCategory
	{
		LCInvalid			= 0,
		LCError				= 1,
		LCWarning			= 2,
		LCInfoToFile		= 3,
		LCInfoToGUI			= 4,
	};

	enum MarketSelection
	{
		MarketBBO			= 0,
		MarketNBBO			= 1,
		MarketIndicative	= 2,
	};

	//	market states from the external API have to be mapped to use one of the following values.
	//	for special exchange specific states that can't be found in the list, use one that is closest.
	//	e.g. use MSOpen if it's still possible to trade, use MSHalt, if it's not possible to add orders, etc.
	enum MarketStatus
	{
		MSOpen				= 1,		//	the market is open for normal unrestricted trading, also called regular
		MSClosed			= 2,		//	market closed
		MSFast				= 3,		//	trading with relaxed restrictions
		MSHalt				= 4,		//	trading is halted (some exchanges seem to differ between halted and suspended)
		MSSuspend			= 5,		//	trading is suspended
		MSPreOpening		= 6,		//	market is about to open, also called pre trading
		MSPostTrading		= 7,		//	market has closed
		MSMarketRotation	= 8,		//	transition from closed to open market
		MSUnavailable		= 9,		//	the exchange system or the connection to it is down
	};

	struct Instrument
	{
		InstrumentType		Type;
		int					InstrumentId;				//	unique identifier of instrument used in callbacks
		char				InstrumentExchangeId[32];	//	Exchange specific instrument identifier

		char				BaseId[8];
		char				Market[5];
		char				UnderlyingBaseId[8];
		char				UnderlyingMarket[5];
		double				StrikePrice;				//	be aware of rounding issues when comparing to external definition!
		int					ExpDate;					//	YYYYMMDD i.e. 20111022			
		char				OptionType;
	};

	struct StrategyLeg
	{
		Instrument			LegInstrument;
		int					Ratio;						//	Ratio > 0 is buy, Ratio < 0 is sell
	};

	struct Strategy
	{
		char				StrategyType;				//	same as used on GUI and defined in parameter files
		int					InstrumentId;				//	unique over instruments and strategies
		char				InstrumentExchangeId[32];	//	Exchange specific instrument identifier

		int					NumLegs;
		StrategyLeg			Legs[4];
		double				StockPrice;					//	mind the rounding problems when matching with external API
		bool				PercentageDelta;
	};

	struct PriceUpdateEntry
	{
		UpdateType			eUpdateType;
		double				dPrice;
	};

	struct QuantityUpdateEntry
	{
		UpdateType			eUpdateType;
		long				lQuantity;
	};

	struct PriceQuoteUpdateEntry
	{
		UpdateType			eUpdateType;
		double				dPrice;
		long				lQuantity;
		int					iDepth;
		int					iMarket;					//	use MarketSelection enum when possible
		char				Source[10];
	};

}

//
// The functions the ClientModule must implement
//
namespace ClientModule
{
	int Startup ( const char* pPluginPriceFeedConfiguration, const char* pRegistryLocation );
	int Shutdown ();
	int Connect ();
	int Disconnect ();

	//	subscriptions for instruments
	int SubscribeBBO ( const ExtPF::Instrument& instrument );
	int SubscribeNBBO ( const ExtPF::Instrument& instrument );
	int SubscribeSettlement ( const ExtPF::Instrument& instrument );
	int Unsubscribe ( const ExtPF::Instrument& instrument );

	//	subscriptions for strategies
	int SubscribeStrategyBBO ( const ExtPF::Strategy& strategy );
	int UnsubscribeStrategy ( const ExtPF::Strategy& strategy );

	//	subscriptions for market status
	int SubscribeMarketStatus ( const ExtPF::Instrument& instrument );
	int UnsubscribeMarketStatus ( const ExtPF::Instrument& instrument );
	int SubscribeStrategyMarketStatus(const ExtPF::Strategy& strategy);
	int UnsubscribeStrategyMarketStatus(const ExtPF::Strategy& strategy);
}

//
// The callback functions the ClientModule uses to talk back to the calling IIO
//
// LogMsg:
// Used to write a message to the Aqtor log file. The category can be used to select the severity of the message
// and to select if it additionally is output to the application logger window.
//
// PriceFeed:
// Used to send a collection of price updates to Aqtor on a selected instrument. Prices which have assiciated quantities should be grouped
// in the PriceQuoteUpdateEntry array, if there is just a price these go in the PriceUpdateEntry array and quantity only goes in the 
// QuantityUpdateEntry array.
// It is best to pass as much information as possible to Aqtor in one call rather than making many calls to PriceFeed with smaller sets of data.
//
// MarketStatus:
// Used to send up the market status for an instrument or strategy.
//
// ConnectionLost:
// For the ClientModule to report a disconnection from the market price feed connection.
// Once reporeted to Aqtor there is no going back, the application must be restarted.
//
// ConnectionUnreliable:
// Informs Aqtor that the price feed is slow or dangerously behind the market. Any quotes currently in the market
// which depend on these prices will be held. The user can return the quotes to the market as they see fit.
//
namespace ClientCallback
{
	void LogMsg ( const ExtPF::LogCategory logCategory, const char* pBaseId, const char* pMessage );

	void PriceFeed ( int instrumentId, short iNbrPU, ExtPF::PriceUpdateEntry* pPriceUpdates, short iNbrQU, ExtPF::QuantityUpdateEntry* pQuantityUpdates, short iNbrPQU, ExtPF::PriceQuoteUpdateEntry* pPriceQuoteUpdates, float latency = -1.0 );

	void MarketStatus ( int instrumentId, ExtPF::MarketStatus status );

	void ConnectionLost ();
	void ConnectionUnreliable ();
}

namespace ExtPfAPIInternal
{
	// structure defintions used by the dll interface. Client code should use the 
	// wrappers defined in the ClientCallback namespace above.

	struct CallbackFunctions
	{
		void* InstanceHandle;

		void (*Log)					( int logCategory, char* baseId, char* message );
		void (*PriceFeed)			( void* handle, int instrumentId, short iNbrPU, ExtPF::PriceUpdateEntry* pPriceUpdates, short iNbrQU, ExtPF::QuantityUpdateEntry* pQuantityUpdates, short iNbrPQU, ExtPF::PriceQuoteUpdateEntry* pPriceQuoteUpdatese, float latency );
		void (*MarketStatus)		( void* handle, int instrumentId, ExtPF::MarketStatus status );
		void (*ConnectionLost)		( void* handle );
		void (*ConnectionUnreliable)( void* handle );
	};
}

#pragma pack(pop)

#endif
