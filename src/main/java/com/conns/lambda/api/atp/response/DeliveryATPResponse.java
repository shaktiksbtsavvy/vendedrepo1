package com.conns.lambda.api.atp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeliveryATPResponse {
	
	/**
	 * @param sku
	 * @param zip
	 * @param availableQty
	 */
	public DeliveryATPResponse(String sku, String zip, Double availableQty, String atpDate) {
		super();
		this.sku = sku;
		this.zip = zip;
		this.availableQty = availableQty;
		this.atpDetails = new ATPDetailsResponse(atpDate);
	}
	
	/**
	 * @param sku
	 * @param zip
	 * @param availableQty
	 */
	public DeliveryATPResponse(String sku, String zip, String availableQty, String atpDate, String onHand) {
		super();
		this.sku = sku;
		this.zip = zip;
		this.availableQty = availableQty != null ? Double.parseDouble(availableQty) : 0;
		this.atpDetails = new ATPDetailsResponse(atpDate);
		this.onhandFlag = onHand;
	}


	/**
	 * 
	 */
	public DeliveryATPResponse() {
		super();
	}

	@JsonProperty("sku")
	private String sku;
	
	@JsonProperty("zip")
	private String zip;
	
	@JsonProperty("servicing_location")
	private String servicingLocation;
	
	@JsonProperty("servicing_location_type")
	private String servicingLocationType;
	
	@JsonProperty("inventory_location")
	private String inventoryLocation;
	
	@JsonProperty("inventory_location_type")
	private String inventoryLocationType;
	
	@JsonProperty("available_qty")
	private Double availableQty;
	
	@JsonProperty("delivery_method")
	private String deliveryMethod;
	
	@JsonProperty("onhand_flag")
	private String onhandFlag;
	
	@JsonProperty("lead_time")
	private Double leadTime;
	
	@JsonProperty("calculated_delivery_date")
	private String calculatedDeliveryDate;
	
	@JsonProperty("available_to_promise_details")
	private ATPDetailsResponse atpDetails;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getServicingLocation() {
		return servicingLocation;
	}

	public void setServicingLocation(String servicingLocation) {
		this.servicingLocation = servicingLocation;
	}

	public String getServicingLocationType() {
		return servicingLocationType;
	}

	public void setServicingLocationType(String servicingLocationType) {
		this.servicingLocationType = servicingLocationType;
	}

	public String getInventoryLocation() {
		return inventoryLocation;
	}

	public void setInventoryLocation(String inventoryLocation) {
		this.inventoryLocation = inventoryLocation;
	}

	public String getInventoryLocationType() {
		return inventoryLocationType;
	}

	public void setInventoryLocationType(String inventoryLocationType) {
		this.inventoryLocationType = inventoryLocationType;
	}

	public Double getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(Double availableQty) {
		this.availableQty = availableQty;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public Double getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Double leadTime) {
		this.leadTime = leadTime;
	}

	public String getCalculatedDeliveryDate() {
		return calculatedDeliveryDate;
	}

	public void setCalculatedDeliveryDate(String calculatedDeliveryDate) {
		this.calculatedDeliveryDate = calculatedDeliveryDate;
	}

	public ATPDetailsResponse getAtpDetails() {
		return atpDetails;
	}

	public void setAtpDetails(ATPDetailsResponse atpDetails) {
		this.atpDetails = atpDetails;
	}

	public String getOnhandFlag() {
		return onhandFlag;
	}

	public void setOnhandFlag(String onhandFlag) {
		this.onhandFlag = onhandFlag;
	}

}
