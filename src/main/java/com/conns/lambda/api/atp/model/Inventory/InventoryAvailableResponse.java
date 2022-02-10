package com.conns.lambda.api.atp.model.Inventory;

import java.util.ArrayList;
import java.util.List;

import com.conns.lambda.common.http.ResponseBody;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({ "code", "message", "reqID", "data"})
public class InventoryAvailableResponse extends ResponseBody{
	
	/**
	 * 
	 */
	public InventoryAvailableResponse() {
		super();
	}

	/**
	 * @param code
	 * @param message
	 * @param reqID
	 */
	public InventoryAvailableResponse(int code, String message, String reqID) {
		super();
		this.code = code;
		this.message = message;
		this.reqID = reqID;
		this.data = new ArrayList<ProductResponse>();
	}
	
	private int code;
	private String message;
	private String reqID;
	private List<ProductResponse> data;
	
	@JsonGetter("code")
	public int getCode() {
		return code;
	}
	
	@JsonSetter("code")
	public void setCode(int code) {
		this.code = code;
	}
	
	@JsonGetter("message")
	public String getMessage() {
		return message;
	}
	
	@JsonSetter("message")
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonGetter("reqID")
	public String getReqID() {
		return reqID;
	}
	
	@JsonSetter("reqID")
	public void setReqID(String reqID) {
		this.reqID = reqID;
	}
	
	@JsonGetter("data")
	public List<ProductResponse> getData() {
		return data;
	}
	
	@JsonSetter("data")
	public void setData(List<ProductResponse> data) {
		this.data = data;
	}
	
}
