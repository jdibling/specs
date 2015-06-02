package com.theice.mdf.client.domain;

public class SpotTradeInfo
{
	public long DeliveryBeginDateTime;

	public long DeliveryEndDateTime;

	public SpotTradeInfo(long deliveryBeginDateTime, long deliveryEndDateTime)
	{
		this.DeliveryBeginDateTime = deliveryBeginDateTime;
		this.DeliveryEndDateTime = deliveryEndDateTime;
	}

	public long getDeliveryBeginDateTime()
	{
		return DeliveryBeginDateTime;
	}

	public long getDeliveryEndDateTime()
	{
		return DeliveryEndDateTime;
	}
}
