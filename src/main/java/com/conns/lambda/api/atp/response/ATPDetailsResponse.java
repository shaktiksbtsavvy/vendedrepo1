package com.conns.lambda.api.atp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ATPDetailsResponse {

	
	/**
	 * @param atpDate
	 */
	public ATPDetailsResponse(String atpDate) {
		super();
		this.atpDate = atpDate;
	}

	/**
	 * 
	 */
	public ATPDetailsResponse() {
		super();
	}

	/**
	 * @param atpDate
	 * @param deliveryZone
	 * @param dayOfWeek
	 */
	public ATPDetailsResponse(String atpDate, String deliveryZone, String dayOfWeek) {
		super();
		this.atpDate = atpDate;
		this.deliveryZone = deliveryZone;
		this.dayOfWeek = dayOfWeek;
	}

	@JsonProperty("available_to_promise_date")
	private String atpDate;
	
	@JsonProperty("delivery_zone")
	private String deliveryZone;
	
	@JsonProperty("delivery_day_of_week")
	private String dayOfWeek;
	
}
