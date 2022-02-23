package com.conns.lambda.api.atp.model.dd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties
public class DeliveryDateRequest {
	/**
	 * 
	 */
	public DeliveryDateRequest() {
		super();
	}

	private String reqID;
	private List<String> SKU;
	private String zipcode;
	private String DL_Location;
	
	@JsonGetter("reqID")
	public String getReqID() {
		return reqID;
	}
	
	@JsonSetter("reqID")
	public void setReqID(String reqID) {
		this.reqID = reqID;
	}
	
	@JsonGetter("SKU")
	public List<String> getSKU() {
		return SKU;
	}
	
	@JsonSetter("SKU")
	public void setSKU(List<String> sKU) {
		SKU = sKU;
	}
	
	@JsonGetter("Zipcode")
	public String getZipcode() {
		return zipcode;
	}
	
	@JsonSetter("Zipcode")
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	@JsonGetter("DL_Location")
	public String getDL_Location() {
		return DL_Location;
	}
	
	@JsonSetter("DL_Location")
	public void setDL_Location(String dL_Location) {
		DL_Location = dL_Location;
	}
	

}
