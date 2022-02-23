package com.conns.lambda.api.atp.model.Inventory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
@DynamoDBDocument
public class Location {
	
	/**
	 * 
	 */
	public Location() {
		super();
	}

	/**
	 * @param locationType
	 * @param locationNumber
	 * @param qtyAvailable
	 */
	public Location(String locationType, String locationNumber, Integer qtyAvailable) {
		super();
		this.locationType = locationType;
		this.locationNumber = locationNumber;
		this.qtyAvailable = qtyAvailable;
	}

	private String locationType;
	private String locationNumber;
	private Integer qtyAvailable;
	private String onHand;

	@DynamoDBIgnore
	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	@JsonGetter("Location")
	@DynamoDBAttribute(attributeName = "Location")
	public String getLocationNumber() {
		return locationNumber;
	}

	@JsonSetter("Location")
	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}

	@JsonGetter("QtyAvailable")
	@DynamoDBAttribute(attributeName = "QtyAvailable")
	public Integer getQtyAvailable() {
		return qtyAvailable;
	}

	@JsonSetter("QtyAvailable")
	public void setQtyAvailable(Integer qtyAvailable) {
		this.qtyAvailable = qtyAvailable;
	}

	@JsonGetter("Onhand")
	@DynamoDBAttribute(attributeName = "Onhand")
	public String getOnHand() {
		return onHand;
	}

	@JsonSetter("Onhand")
	public void setOnHand(String onHand) {
		this.onHand = onHand;
	}
}
