package com.conns.lambda.api.atp.request;

import java.util.ArrayList;
import java.util.List;

import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.util.RequestUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableToPromiseRequest extends ResponseBody{
	
	/**
	 * 
	 */
	public AvailableToPromiseRequest() {
		super();
	}

	@JsonProperty("reqID")
	private String reqID;
	
	@JsonProperty("products")
	private List<String> products;
	
	@JsonProperty("upcs")
	private List<String> upcs;
	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
	@JsonProperty("zip")
	private String zip;
	
	@JsonProperty("clearance_store_id")
	private String clearanceStoreId;
	
	@JsonProperty("clearance_store_nearest_wearhouse")
	private String clearanceStoreWearhouse;
	
	@JsonProperty("locale")
	private String locale;
	
	@JsonProperty("distance")
	private Double distance;

	public String getReqID() {
		return reqID;
	}

	public void setReqID(String reqID) {
		this.reqID = trim(reqID);
	}

	public List<String> getProducts() {
		return products;
	}

	public void setProducts(List<String> products) {
		this.products = RequestUtil.removeDuplicateSkus(trim(products));
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = trim(latitude);
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = trim(longitude);
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = trim(zip);
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = trim(locale);
	}
	
	public String trim(String value) {
		return value != null ? value.trim() : null;
	}
	
	public List<String> trim(List<String> values) {
		List<String> valuesTrim = new ArrayList<String>();
		for(String v: values) {
			String tv = trim(v);
			if(tv != null) valuesTrim.add(tv);
		}
		return valuesTrim;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public List<String> getUpcs() {
		return upcs;
	}

	public void setUpcs(List<String> upcs) {
		this.upcs = upcs;
	}

	public String getClearanceStoreId() {
		return clearanceStoreId;
	}

	public void setClearanceStoreId(String clearanceStoreId) {
		this.clearanceStoreId = clearanceStoreId;
	}

	public String getClearanceStoreWearhouse() {
		return clearanceStoreWearhouse;
	}

	public void setClearanceStoreWearhouse(String clearanceStoreWearhouse) {
		this.clearanceStoreWearhouse = clearanceStoreWearhouse;
	}
}
