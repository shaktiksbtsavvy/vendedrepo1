package com.conns.lambda.api.atp.model.geo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreResponse {

	

	/**
	 * @param storeId
	 * @param storeName
	 * @param storeAddressln1
	 * @param storeAddressln2
	 * @param storeCity
	 * @param storeState
	 * @param storeZip
	 * @param storePhone
	 * @param storeUrl
	 * @param storeDistance
	 * @param storeClosingTime
	 * @param storeHours
	 */
	public StoreResponse(String storeId, String storeName, String storeAddressln1, String storeAddressln2,
			String storeCity, String storeState, String storeZip, String storePhone, String storeUrl,
			Double storeDistance, String storeClosingTime, String storeHours) {
		super();
		this.storeId = storeId;
		this.storeName = storeName;
		this.storeAddressln1 = storeAddressln1;
		this.storeAddressln2 = storeAddressln2;
		this.storeCity = storeCity;
		this.storeState = storeState;
		this.storeZip = storeZip;
		this.storePhone = storePhone;
		this.storeUrl = storeUrl;
		this.storeDistance = storeDistance;
		this.storeClosingTime = storeClosingTime;
		this.storeHours = storeHours;
	}
	

	/**
	 * 
	 */
	public StoreResponse() {
		super();
	}

	@JsonProperty("store_id")
	private String storeId;
	
	@JsonProperty("store_name")
	private String storeName;
	
	@JsonProperty("store_address_ln_1")
	private String storeAddressln1;
	
	@JsonProperty("store_address_ln_2")
	private String storeAddressln2;
	
	@JsonProperty("store_city")
	private String storeCity;
	
	@JsonProperty("store_state")
	private String storeState;
	
	@JsonProperty("store_zip")
	private String storeZip;
	
	@JsonProperty("store_phone")
	private String storePhone;
	
	@JsonProperty("store_url")
	private String storeUrl;
	
	@JsonProperty("store_distance")
	private Double storeDistance;
	
	@JsonProperty("store_closing_time")
	private String storeClosingTime;
	
	@JsonProperty("store_hours")
	private String storeHours;
	
	@JsonProperty("type")
	private String type;

	@JsonProperty("latitude")
	private Double latitude;
	
	@JsonProperty("longitude")
	private Double longitude;
	
	public String getStoreId() {
		return storeId;
	}


	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}


	public String getStoreName() {
		return storeName;
	}


	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}


	public String getStoreAddressln1() {
		return storeAddressln1;
	}


	public void setStoreAddressln1(String storeAddressln1) {
		this.storeAddressln1 = storeAddressln1;
	}


	public String getStoreAddressln2() {
		return storeAddressln2;
	}


	public void setStoreAddressln2(String storeAddressln2) {
		this.storeAddressln2 = storeAddressln2;
	}


	public String getStoreCity() {
		return storeCity;
	}


	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}


	public String getStoreState() {
		return storeState;
	}


	public void setStoreState(String storeState) {
		this.storeState = storeState;
	}


	public String getStoreZip() {
		return storeZip;
	}


	public void setStoreZip(String storeZip) {
		this.storeZip = storeZip;
	}


	public String getStorePhone() {
		return storePhone;
	}


	public void setStorePhone(String storePhone) {
		this.storePhone = storePhone;
	}


	public String getStoreUrl() {
		return storeUrl;
	}


	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}


	public Double getStoreDistance() {
		return storeDistance;
	}


	public void setStoreDistance(Double storeDistance) {
		this.storeDistance = storeDistance;
	}


	public String getStoreClosingTime() {
		return storeClosingTime;
	}


	public void setStoreClosingTime(String storeClosingTime) {
		this.storeClosingTime = storeClosingTime;
	}


	public String getStoreHours() {
		return storeHours;
	}


	public void setStoreHours(String storeHours) {
		this.storeHours = storeHours;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Double getLatitude() {
		return latitude;
	}


	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
}
