
#include "ExternalPriceFeedAPI.h"
#include <string>
#include <map>
#include <set>
#include "windows.h"

///////////////////////////////////////////////////////////////////////////////
//
// DemoClient:
//
// A simple ClientModule implementation which demonstrates the use of the
// External Price Feed API. It receives and reacts on all the calls, collecting
// subscription requests from the caller and delivering prices on these instruments
// accordingly.
// It implements a price delivery thread and handles the calls from the IIO.
// The DemoClient project compiles and links in the ExternalPriceFeedAPI.cpp
// file which provides the DLL entry poins which are mapped by the calling IIO
// and implements the callback function used by the Client to deliver prices,
// log messages and report on connection problems.
// The raw DLL entry points are pre-implemented in this way as they are mapped
// by the calling IIO in an untyped manner, this approach reduces the change of
// type errors. The ClientModule functions they call are defined in a namespace
// of the same name allowing any type errors to be reported by the compiler.
//
///////////////////////////////////////////////////////////////////////////////



//
// struct EnabledInstrument:
//
// Stores the minimum instrument state details to allow the price feed to be demonstrated
// The instrument identifier and a set of flags indicating the type of subscriptions required on the instrument 
//
struct EnabledInstrument
{
	EnabledInstrument() 
		: m_InstrumentId		(-1)
		, m_InstrumentType		(ExtPF::ITUndefined)
		, m_BBOEnabled			(false)
		, m_NBBOEnabled			(false)
		, m_SettlementEnabled	(false) 
	{
	}

	int						m_InstrumentId;
	ExtPF::InstrumentType	m_InstrumentType;
	bool					m_BBOEnabled;
	bool					m_NBBOEnabled;
	bool					m_SettlementEnabled;
};

//
// A collection of instrument details which represents the active subscriptions.
// Accessed by multiple threads so must be protected by a critical section
//
typedef std::map<int, EnabledInstrument> EnabledInstruments;
EnabledInstruments	enabledInstruments;

typedef std::set<int> MarketStatusSubscribers;
MarketStatusSubscribers marketStatusSubscribers;

CRITICAL_SECTION	_CriticalSection;

//
// A thread is created at startup to simulate the market price feed.
// It sends prices up for each instrument with active subscriptions every two seconds
// The types of prices sent up for each instrument depend on the subscription calls received for it.
// The thread terminates when the _running flag is cleared
//
HANDLE				_hThreadHandle;
bool				_running = true;

DWORD WINAPI PriceThreadFunction( LPVOID lpThis )
{
    DWORD dwExitCode = 0;

	//
	// Arrays of structures used to deliver price and quantity info over the API
	// Each PriceFeed call relates to one instrument only. The more information delivered with each PriceFeed
	// call the better. Once call containining multiple fields has advantages over multiple calls each containing
	// one field. This function demonstrates the grouping of an instrument's BBO, NBBO and settlement prices all in one
	// PriceFeed call.
	//
	ExtPF::PriceQuoteUpdateEntry PriceQuotes[6];
	ExtPF::PriceUpdateEntry Prices[1];
	ExtPF::QuantityUpdateEntry Quantity[1];

	int iIterCount = 0;
	while ( _running )
	{
		Sleep( 2000 );

		//
		// Access to the enabledInstruments collection is protected. It is accessed by this 
		// PriceFeed thread and by the Subscription calls which come over the DLL boundary
		//
		::EnterCriticalSection( &_CriticalSection );

		//
		// The ClientModule should inform Aqtor if there are problems with the price feed
		// ConnectionUnreliable causes Aqtor to disable quotes which depend on the price feed, it is possible to recover from this without a restart
		// ConnectionLost indicates that a restart of the application is required.
		// The following code just demostrates the use of these calls
		//

		//if ( iIterCount == 40 )
		//{
		//	ClientCallback::ConnectionUnreliable();
		//}
		//if ( iIterCount > 45 )
		//{
		//	ClientCallback::ConnectionLost();
		//	_running = false;
		//	continue;
		//}

		//
		// Consider each enabled instrument in turn. For each of the subsription types it has send some sample
		// prices back over the API to demonstrate the price feed functionality
		//

		static long long frequency;
		::QueryPerformanceFrequency((LARGE_INTEGER*) &frequency);

		long long startTime;
		QueryPerformanceCounter( (LARGE_INTEGER*)&startTime );

		for (EnabledInstruments::iterator iter = enabledInstruments.begin(); iter != enabledInstruments.end(); ++iter)
		{
			EnabledInstrument& enabledInst = iter->second;

			int iNbrQuotes		= 0;
			int iNbrPrices		= 0;
			int iNbrQuantity	= 0;

			//
			// If the SubscribeBBO function has been called on this instrument send some sample prices 
			//
			if ( enabledInst.m_BBOEnabled )
			{
				PriceQuotes[iNbrQuotes].eUpdateType	= ExtPF::UTBid;
				PriceQuotes[iNbrQuotes].dPrice		= 7.9 + iIterCount/5.0;
				PriceQuotes[iNbrQuotes].lQuantity	= 100;
				PriceQuotes[iNbrQuotes].iDepth		= 0;					// Depth of zero implies BBO
				PriceQuotes[iNbrQuotes].iMarket		= ExtPF::MarketBBO;
				PriceQuotes[iNbrQuotes].Source[0]	= 'C';					// Indicates which market generated the price
				PriceQuotes[iNbrQuotes].Source[1]	= 0;
				iNbrQuotes++;

				PriceQuotes[iNbrQuotes].eUpdateType	= ExtPF::UTAsk;
				PriceQuotes[iNbrQuotes].dPrice		= 8.9 + iIterCount/5.0;
				PriceQuotes[iNbrQuotes].lQuantity	= 90;
				PriceQuotes[iNbrQuotes].iDepth		= 0;
				PriceQuotes[iNbrQuotes].iMarket		= ExtPF::MarketBBO;
				PriceQuotes[iNbrQuotes].Source[0]	= 'C';
				PriceQuotes[iNbrQuotes].Source[1]	= 0;
				iNbrQuotes++;

				PriceQuotes[iNbrQuotes].eUpdateType	= ExtPF::UTBid;
				PriceQuotes[iNbrQuotes].dPrice		= 7.9 + iIterCount/5.0 + 0.1;
				PriceQuotes[iNbrQuotes].lQuantity	= 10;
				PriceQuotes[iNbrQuotes].iDepth		= 0;						// Depth of zero implies BBO
				PriceQuotes[iNbrQuotes].iMarket		= ExtPF::MarketIndicative;
				PriceQuotes[iNbrQuotes].Source[0]	= 'C';						// Indicates which market generated the price
				PriceQuotes[iNbrQuotes].Source[1]	= 0;
				iNbrQuotes++;

				PriceQuotes[iNbrQuotes].eUpdateType	= ExtPF::UTAsk;
				PriceQuotes[iNbrQuotes].dPrice		= 8.9 + iIterCount/5.0 - 0.1;
				PriceQuotes[iNbrQuotes].lQuantity	= 20;
				PriceQuotes[iNbrQuotes].iDepth		= 0;
				PriceQuotes[iNbrQuotes].iMarket		= ExtPF::MarketIndicative;
				PriceQuotes[iNbrQuotes].Source[0]	= 'C';
				PriceQuotes[iNbrQuotes].Source[1]	= 0;
				iNbrQuotes++;

				Quantity[iNbrQuantity].eUpdateType	= ExtPF::UTTotalVolume;
				Quantity[iNbrQuantity].lQuantity	= 1000 + iIterCount;
				iNbrQuantity++;
			}
			//
			// If the SubscribeNBBO function has been called on this instrument send some sample prices 
			//
			if ( enabledInst.m_NBBOEnabled )
			{
				PriceQuotes[iNbrQuotes].eUpdateType	= ExtPF::UTBid;
				PriceQuotes[iNbrQuotes].dPrice		= 7.9 + iIterCount/5.0;
				PriceQuotes[iNbrQuotes].lQuantity	= 100;
				PriceQuotes[iNbrQuotes].iDepth		= 0;
				PriceQuotes[iNbrQuotes].iMarket		= ExtPF::MarketNBBO;
				PriceQuotes[iNbrQuotes].Source[0]	= 'B';		// Indicates which market generated the price, i.e. which market is best in NBBO
				PriceQuotes[iNbrQuotes].Source[1]	= 0;		// A comma sep list in order of contribution size may be supplied here
				iNbrQuotes++;

				PriceQuotes[iNbrQuotes].eUpdateType	= ExtPF::UTAsk;
				PriceQuotes[iNbrQuotes].dPrice		= 8.9 + iIterCount/5.0;
				PriceQuotes[iNbrQuotes].lQuantity	= 90;
				PriceQuotes[iNbrQuotes].iDepth		= 0;
				PriceQuotes[iNbrQuotes].iMarket		= ExtPF::MarketNBBO;
				PriceQuotes[iNbrQuotes].Source[0]	= 'A';
				PriceQuotes[iNbrQuotes].Source[1]	= 0;
				iNbrQuotes++;

			}
			//
			// If the SubscribeSettlement function has been called on this instrument send some sample prices 
			//
			if ( enabledInst.m_SettlementEnabled )
			{
				Prices[iNbrPrices].eUpdateType	= ExtPF::UTYClose;
				Prices[iNbrPrices].dPrice		= 111.10 + iIterCount;
				iNbrPrices++;
			}

			//
			// Send all the generated prices over the API in one call. The size fields indicate how may array entries have been populated
			//
			ClientCallback::PriceFeed( enabledInst.m_InstrumentId, iNbrPrices, Prices, iNbrQuantity, Quantity, iNbrQuotes, PriceQuotes );
		}

		for (MarketStatusSubscribers::const_iterator itr = marketStatusSubscribers.begin(); itr != marketStatusSubscribers.end(); ++itr)
		{
			// Sending a market status message
			ClientCallback::MarketStatus( *itr, iIterCount%2 ? ExtPF::MSOpen : ExtPF::MSClosed );
		}

		long long endTime;
		QueryPerformanceCounter( (LARGE_INTEGER*)&endTime );

		double elapsedSeconds = double(endTime - startTime)/frequency;

		size_t iNbrCalls = enabledInstruments.size();
		if ( iNbrCalls > 0 )
		{
			char buffer[200];
#ifdef _WIN64
			sprintf_s( buffer, "%Id calls took %.6f seconds", iNbrCalls, elapsedSeconds );
#else
			sprintf( buffer, "%d calls took %.6f seconds", iNbrCalls, elapsedSeconds );
#endif
			ClientCallback::LogMsg( ExtPF::LCInfoToGUI, "DLL", buffer );
		}

		::LeaveCriticalSection( &_CriticalSection );

		iIterCount++;
	}

	return dwExitCode;
}

///////////////////////////////////////////////////////////////////////////////
//
// ClientModule Implementation:
//
// To write a external price feed DLL the functions defined in the namespace ClientModule must be implemented.
// These functions are called by the external DLL functions from the Actant Quote which are defined in the 
// supplied file ExternalPriceFeedAPI.cpp.
//
// Startup:
// Used by the Client DLL to perform any initialisation. Returning an error code (non zero) from this function will
// prevent a successful Actant Quote application startup.
// The Startup function supplies two strings, one is the registry directory of the IIO which loaded the Client DLL,
// the other one is the contents of the "PluginPriceFeedConfiguration" string read from the IIO's registry config.
// If the Client DLL can be configured from details stored in a single string it is easier to use this, if more complex
// config is required the full registry access should be used.
//
// Shutdown:
// Called at the application shutdown allowing the Client DLL to perform a graceful termination of threads etc.
//
// Connect:
// Called after Startup to tell the Client DLL to make its connections to external markets or price sources.
// The function should not return until this has been performed successfully. Returning an error code prevents
// application startup.
//
// Disconnect:
// Called at shutdown to tell the Client DLL to discoeect from its price sources. Not called if the DLL has
// previously reported a loss of connection by calling ConnectionLost. Shutdown always called at application shutdown.
//
// SubscribeBBO:
// A request to supply BBO prices for the defined instrument. The Client DLL should subsequently send a snapshot of the current
// market prices followed by incremental updates. BBO covers all instruemnt price types other than NBBO and Yesterday's close (settlement)
// Prices are delivered from the Client DLL to Actant by calling the ClientCallback::PriceFeed function.
//
// SubscribeNBBO:
// A request to supply the NBBO prices for the defined instrument. The Client DLL should supply a snapshot of the current BBO followed
// by updates as they happen.
//
// SubscribeSettlement:
// A request to supply the Settlement (Yesterday's Close) price for the defined instrument.
//
// Unsubscribe:
// A request to turn off all subscriptions for the specified instrument.
//
// SubscribeStrategyBBO:
// A request to supply BBO prices for the defined strategy. The Client DLL should subsequently send a snapshot of the current
// market prices followed by incremental updates. BBO covers all instruemnt price types other than NBBO and Yesterday's close
// (settlement). This includes the indicative prices.
// Prices are delivered from the Client DLL to Actant by calling the ClientCallback::PriceFeed function.
//
// UnsubscribeStrategy:
// A request to turn off all subscriptions for the specified strategy.
//
///////////////////////////////////////////////////////////////////////////////

int ClientModule::Startup( const char* pPluginPriceFeedConfiguration, const char* pRegistryLocation )
{
	ClientCallback::LogMsg( ExtPF::LCInfoToGUI, "DLL", "Startup Called" );

	DWORD dwThreadId = 0;
	::InitializeCriticalSection( &_CriticalSection );
	_hThreadHandle = ::CreateThread( NULL, 0, PriceThreadFunction, 0, 0, &dwThreadId );

	return 0;
}

int ClientModule::Shutdown()
{
	ClientCallback::LogMsg( ExtPF::LCInfoToGUI, "DLL", "Shutdown Called" );

	_running = false;

	if (WaitForMultipleObjects(1, &_hThreadHandle, TRUE, 5000) == WAIT_TIMEOUT)
		ClientCallback::LogMsg( ExtPF::LCError, "DLL", "Timed out wating for thread to stop" );

	return 0;
}

int ClientModule::Connect()
{
	ClientCallback::LogMsg( ExtPF::LCInfoToFile, "DLL", "Connect Called" );
	return 0;
}

int ClientModule::Disconnect()
{
	ClientCallback::LogMsg( ExtPF::LCInfoToFile, "DLL", "Disconnect Called" );
	return 0;
}

int ClientModule::SubscribeBBO(const ExtPF::Instrument& instrument)
{
	::EnterCriticalSection( &_CriticalSection );

	EnabledInstrument& enabledInst = enabledInstruments[instrument.InstrumentId];

	enabledInst.m_BBOEnabled		= true;
	enabledInst.m_InstrumentId		= instrument.InstrumentId;
	enabledInst.m_InstrumentType	= instrument.Type;

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::SubscribeNBBO(const ExtPF::Instrument& instrument)
{
	::EnterCriticalSection( &_CriticalSection );

	EnabledInstrument& enabledInst = enabledInstruments[instrument.InstrumentId];

	enabledInst.m_NBBOEnabled		= true;
	enabledInst.m_InstrumentId		= instrument.InstrumentId;
	enabledInst.m_InstrumentType	= instrument.Type;

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::SubscribeSettlement(const ExtPF::Instrument& instrument)
{
	::EnterCriticalSection( &_CriticalSection );

	EnabledInstrument& enabledInst = enabledInstruments[instrument.InstrumentId];

	enabledInst.m_SettlementEnabled	= true;
	enabledInst.m_InstrumentId		= instrument.InstrumentId;
	enabledInst.m_InstrumentType	= instrument.Type;

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::Unsubscribe(const ExtPF::Instrument& instrument)
{
	::EnterCriticalSection( &_CriticalSection );

	EnabledInstruments::iterator iter = enabledInstruments.find( instrument.InstrumentId);
	if ( iter != enabledInstruments.end() )
	{
		enabledInstruments.erase( iter );
	}

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::SubscribeStrategyBBO(const ExtPF::Strategy& strategy)
{
	::EnterCriticalSection( &_CriticalSection );

	EnabledInstrument& enabledInst = enabledInstruments[strategy.InstrumentId];

	enabledInst.m_BBOEnabled		= true;
	enabledInst.m_InstrumentId		= strategy.InstrumentId;
	enabledInst.m_InstrumentType	= ExtPF::ITStrategy;

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::UnsubscribeStrategy(const ExtPF::Strategy& strategy)
{
	::EnterCriticalSection( &_CriticalSection );

	EnabledInstruments::iterator iter = enabledInstruments.find( strategy.InstrumentId);
	if ( iter != enabledInstruments.end() )
	{
		enabledInstruments.erase( iter );
	}

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::SubscribeMarketStatus(const ExtPF::Instrument& instrument)
{
	::EnterCriticalSection( &_CriticalSection );

	marketStatusSubscribers.insert(instrument.InstrumentId);

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::UnsubscribeMarketStatus(const ExtPF::Instrument& instrument)
{
	::EnterCriticalSection( &_CriticalSection );

	marketStatusSubscribers.erase(instrument.InstrumentId);

	::LeaveCriticalSection( &_CriticalSection );
	return 0;
}

int ClientModule::SubscribeStrategyMarketStatus(const ExtPF::Strategy& strategy)
{
	::EnterCriticalSection( &_CriticalSection );

	marketStatusSubscribers.insert(strategy.InstrumentId);

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}

int ClientModule::UnsubscribeStrategyMarketStatus(const ExtPF::Strategy& strategy)
{
	::EnterCriticalSection( &_CriticalSection );

	marketStatusSubscribers.erase(strategy.InstrumentId);

	::LeaveCriticalSection( &_CriticalSection );

	return 0;
}