package com.conns.lambda.api.atp.response;

import java.util.Collections;
import java.util.List;

import com.conns.lambda.common.http.ResponseBody;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AvailableToPromiseResponse extends ResponseBody{
	
	/**
	 * 
	 */
	public AvailableToPromiseResponse() {
		super();
	}

	@JsonProperty("code")
	private Integer code;
	
	@JsonProperty("message")
	private String message;
	
	@JsonIgnore
	private String reqID;
	
	@JsonProperty("delivery_atp")
	private List<DeliveryATPResponse> deliveryAtp;
	
	@JsonProperty("pickup_atp")
	private List<PickupATPResponse> pickupAtp;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReqID() {
		return reqID;
	}

	public void setReqID(String reqID) {
		this.reqID = reqID;
	}

	public List<DeliveryATPResponse> getDeliveryAtp() {
		return deliveryAtp;
	}

	public void setDeliveryAtp(List<DeliveryATPResponse> deliveryAtp) {
		this.deliveryAtp = deliveryAtp;
	}

	public List<PickupATPResponse> getPickupAtp() {
		return pickupAtp;
	}

	public void setPickupAtp(List<PickupATPResponse> pickupAtp) {
		this.pickupAtp = pickupAtp;
	}

	
	public void sort() {
		Collections.sort(pickupAtp);
	}
	
}
