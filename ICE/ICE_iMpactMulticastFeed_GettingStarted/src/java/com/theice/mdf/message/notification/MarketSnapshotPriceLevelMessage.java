package com.theice.mdf.message.notification;

import com.theice.mdf.message.RawMessageFactory;
import com.theice.mdf.message.pricelevel.AddPriceLevelMessage;

/**
 * MarketSnapshotPriceLevelMessage
 * @author David Chen
 */
public class MarketSnapshotPriceLevelMessage extends AddPriceLevelMessage
{
   public MarketSnapshotPriceLevelMessage()
   {
      super();
      MessageType = RawMessageFactory.MarketSnapshotPriceLevelMessageType;
   }
}
