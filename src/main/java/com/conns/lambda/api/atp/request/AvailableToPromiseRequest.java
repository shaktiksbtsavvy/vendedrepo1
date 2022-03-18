package com.conns.lambda.api.atp.request;

import java.util.ArrayList;
import java.util.List;

import com.conns.lambda.common.http.ResponseBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
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
	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
	@JsonProperty("zip")
	private String zip;
	
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
		this.products = trim(products);
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
}
