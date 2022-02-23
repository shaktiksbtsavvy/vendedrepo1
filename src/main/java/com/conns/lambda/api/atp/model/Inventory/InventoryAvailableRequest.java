package com.conns.lambda.api.atp.model.Inventory;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties
public class InventoryAvailableRequest {
	/**
	 * 
	 */
	public InventoryAvailableRequest() {
		super();
	}

	private String reqID;
	private List<String> SKU;
	private String zipcode;
	private String DL_Location;
	private List<String> PU_Location;
	
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
	
	@JsonGetter("PU_Location")
	public List<String> getPU_Location() {
		return PU_Location;
	}
	
	@JsonSetter("PU_Location")
	public void setPU_Location(List<String> pU_Location) {
		PU_Location = pU_Location;
	}
	
	public boolean locationRequested(String locationNumber) {
		if(locationNumber == null || locationNumber.isEmpty()) return false;
		if((locationNumber.equals(DL_Location) || (PU_Location != null && PU_Location.contains(locationNumber)) )) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean skuRequested(String sku) {
		if(sku == null || sku.isEmpty() || SKU == null) return false;
		if((SKU.contains(sku))) {
			return true;
		} else {
			return false;
		}
	}

}
