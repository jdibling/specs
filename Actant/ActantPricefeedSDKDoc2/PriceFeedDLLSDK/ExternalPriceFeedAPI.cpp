

///////////////////////////////////////////////////////////////////////////////
//
// ExternalPriceFeedAPI:
//
// Provides the basis for the implementation of a ClientModule.
//	- extern DLL entry points called by the IIO which loads the Client Module DLL
//	- an API version checking system
//	- delivers callback functions for the delivery of prices and connection status back to IIO
//	- passes on the IIO calls to the ClientModule functions
//	- wraps the IIO's callback functions allowing the ClientModule to call typed functions
//
// When writing a ClientModule DLL this file should be compiled and linked in.
// While the ClientModule DLL could implement the external functions and handle the
// callback functions directly this code allows the ClientModule deleloper to use
// a fully typed interface reducing the changes of mismatched DLL interfaces.
// Once this file is linked into the ClientModule DLL all the entry points and
// declarations required by the implementation may be accessed by incuding 
// the file "ExternalPriceFeedAPI.h".
//
///////////////////////////////////////////////////////////////////////////////

#include "ExternalPriceFeedAPI.h"

using namespace ExtPfAPIInternal;

CallbackFunctions fns;

extern "C" __declspec(dllexport) int ExPfGetVersion()
{
	return EX_PF_API_VERSION;
}

extern "C" __declspec(dllexport) int ExPfStartup(CallbackFunctions* callbackFns, char* pPluginPriceFeedConfiguration, char* pRegistryLocation)
{
	fns = *callbackFns;

	return ClientModule::Startup( pPluginPriceFeedConfiguration, pRegistryLocation );
}

extern "C" __declspec(dllexport) int ExPfShutdown()
{
	return ClientModule::Shutdown();
}

extern "C" __declspec(dllexport) int ExPfConnect()
{
	return ClientModule::Connect();
}

extern "C" __declspec(dllexport) int ExPfDisconnect()
{
	return ClientModule::Disconnect();
}

extern "C" __declspec(dllexport) int ExPfSubscribeBBO(ExtPF::Instrument* instrument)
{
	return ClientModule::SubscribeBBO(*instrument);
}

extern "C" __declspec(dllexport) int ExPfSubscribeNBBO(ExtPF::Instrument* instrument)
{
	return ClientModule::SubscribeNBBO(*instrument);
}

extern "C" __declspec(dllexport) int ExPfSubscribeSettlement(ExtPF::Instrument* instrument)
{
	return ClientModule::SubscribeSettlement(*instrument);
}

extern "C" __declspec(dllexport) int ExPfUnsubscribe(ExtPF::Instrument* instrument)
{
	return ClientModule::Unsubscribe(*instrument);
}

extern "C" __declspec(dllexport) int ExPfSubscribeStrategyBBO(ExtPF::Strategy* strategy)
{
	return ClientModule::SubscribeStrategyBBO(*strategy);
}

extern "C" __declspec(dllexport) int ExPfUnsubscribeStrategy(ExtPF::Strategy* strategy)
{
	return ClientModule::UnsubscribeStrategy(*strategy);
}

extern "C" __declspec(dllexport) int ExPfSubscribeMarketStatus(ExtPF::Instrument* instrument)
{
	return ClientModule::SubscribeMarketStatus(*instrument);
}

extern "C" __declspec(dllexport) int ExPfUnsubscribeMarketStatus(ExtPF::Instrument* instrument)
{
	return ClientModule::UnsubscribeMarketStatus(*instrument);
}

extern "C" __declspec(dllexport) int ExPfSubscribeStrategyMarketStatus(ExtPF::Strategy* strategy)
{
	return ClientModule::SubscribeStrategyMarketStatus(*strategy);
}

extern "C" __declspec(dllexport) int ExPfUnsubscribeStrategyMarketStatus(ExtPF::Strategy* strategy)
{
	return ClientModule::UnsubscribeStrategyMarketStatus(*strategy);
}

void ClientCallback::LogMsg( const ExtPF::LogCategory logCategory, const char* pBaseId, const char* pMessage)
{
	fns.Log( logCategory, const_cast<char*>(pBaseId), const_cast<char*>(pMessage) );
}

void ClientCallback::PriceFeed ( int instrumentId, short iNbrPU, ExtPF::PriceUpdateEntry* pPriceUpdates, short iNbrQU, ExtPF::QuantityUpdateEntry* pQuantityUpdates, short iNbrPQU, ExtPF::PriceQuoteUpdateEntry* pPriceQuoteUpdates, float latency )
{
	fns.PriceFeed( fns.InstanceHandle, instrumentId, iNbrPU, pPriceUpdates, iNbrQU, pQuantityUpdates, iNbrPQU, pPriceQuoteUpdates, latency );
}

void ClientCallback::MarketStatus ( int instrumentId, ExtPF::MarketStatus status )
{
	fns.MarketStatus( fns.InstanceHandle, instrumentId, status );
}

void ClientCallback::ConnectionLost()
{
	fns.ConnectionLost( fns.InstanceHandle );
}

void ClientCallback::ConnectionUnreliable()
{
	fns.ConnectionUnreliable( fns.InstanceHandle );
}



