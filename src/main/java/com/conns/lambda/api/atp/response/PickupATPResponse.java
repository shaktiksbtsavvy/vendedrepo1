package com.conns.lambda.api.atp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PickupATPResponse {
	
	/**
	 * @param sku
	 * @param locationType
	 * @param locationLong
	 * @param locationLat
	 * @param location
	 * @param distance
	 * @param availableQty
	 */
	public PickupATPResponse(String sku, String locationType, Double locationLong, Double locationLat, String location,
			Double distance, Double availableQty) {
		super();
		this.sku = sku;
		this.locationType = locationType;
		this.locationLong = locationLong;
		this.locationLat = locationLat;
		this.location = location;
		this.distance = distance;
		this.availableQty = availableQty;
	}
	
	public PickupATPResponse(String sku, String locationType, Double locationLong, Double locationLat, String location,
			Double distance, String availableQty, String atpDate) {
		super();
		this.sku = sku;
		this.locationType = locationType;
		this.locationLong = locationLong;
		this.locationLat = locationLat;
		this.location = location;
		this.distance = distance;
		this.availableQty = availableQty != null ? Double.parseDouble(availableQty) : 0;
		this.atpDate = atpDate;
	}

	/**
	 * @param sku
	 * @param atpDate
	 * @param availableQty
	 */
	public PickupATPResponse(String sku, String atpDate, Double availableQty) {
		super();
		this.sku = sku;
		this.atpDate = atpDate;
		this.availableQty = availableQty;
	}
	
	public PickupATPResponse(String sku, String atpDate, String availableQty) {
		super();
		this.sku = sku;
		this.atpDate = atpDate;
		this.availableQty = availableQty != null ? Double.parseDouble(availableQty) : 0;
	}

	@JsonProperty("sku")
	private String sku;
	
	@JsonProperty("pick_method")
	private String pickMethod;
	
	@JsonProperty("location_website_url")
	private String locationWebsiteUrl;
	
	@JsonProperty("location_type")
	private String locationType;
	
	@JsonProperty("location_name")
	private String locationName;
	
	@JsonProperty("location_long")
	private Double locationLong;
	
	@JsonProperty("location_lat")
	private Double locationLat;
	
	@JsonProperty("location_contact_number")
	private String locationContactNumber;
	
	@JsonProperty("location")
	private String location;
	
	@JsonProperty("distance")
	private Double distance;
	
	@JsonProperty("available_to_promise_date")
	private String atpDate;
	
	@JsonProperty("available_qty")
	private Double availableQty;
	
	@JsonProperty("address_zip")
	private String addressZip;
	
	@JsonProperty("address_state")
	private String addressState;
	
	@JsonProperty("address_ln_2")
	private String addressLn_2;
	
	@JsonProperty("address_ln_1")
	private String addressLn_1;
	
	@JsonProperty("address_city")
	private String addressCity;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getPickMethod() {
		return pickMethod;
	}

	public void setPickMethod(String pickMethod) {
		this.pickMethod = pickMethod;
	}

	public String getLocationWebsiteUrl() {
		return locationWebsiteUrl;
	}

	public void setLocationWebsiteUrl(String locationWebsiteUrl) {
		this.locationWebsiteUrl = locationWebsiteUrl;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Double getLocationLong() {
		return locationLong;
	}

	public void setLocationLong(Double locationLong) {
		this.locationLong = locationLong;
	}

	public Double getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(Double locationLat) {
		this.locationLat = locationLat;
	}

	public String getLocationContactNumber() {
		return locationContactNumber;
	}

	public void setLocationContactNumber(String locationContactNumber) {
		this.locationContactNumber = locationContactNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getAtpDate() {
		return atpDate;
	}

	public void setAtpDate(String atpDate) {
		this.atpDate = atpDate;
	}

	public Double getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(Double availableQty) {
		this.availableQty = availableQty;
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddressZip(String addressZip) {
		this.addressZip = addressZip;
	}

	public String getAddressState() {
		return addressState;
	}

	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}

	public String getAddressLn_2() {
		return addressLn_2;
	}

	public void setAddressLn_2(String addressLn_2) {
		this.addressLn_2 = addressLn_2;
	}

	public String getAddressLn_1() {
		return addressLn_1;
	}

	public void setAddressLn_1(String addressLn_1) {
		this.addressLn_1 = addressLn_1;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	

}
