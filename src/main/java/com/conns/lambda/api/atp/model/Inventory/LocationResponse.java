package com.conns.lambda.api.atp.model.Inventory;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "LocationType", "LocationNumber", "QtyAvailable","OnhandFlag"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {
	/**
	 * 
	 */
	public LocationResponse() {
		super();
	}

	/**
	 * @param locationType
	 * @param locationNumber
	 * @param qtyAvailable
	 */
	public LocationResponse(String locationType, String locationNumber, String qtyAvailable) {
		super();
		this.locationType = locationType;
		this.locationNumber = locationNumber;
		this.qtyAvailable = qtyAvailable;
	}
	
	public LocationResponse(Location location) {
		super();
		locationType = location.getLocationType();
		locationNumber = location.getLocationNumber();
		qtyAvailable = String.valueOf(location.getQtyAvailable());
		if (location.getLocationType().equals("WH")) {
			OnhandFlag = location.getOnHand();
		}
	}
	
	private String locationType;
	private String locationNumber;
	private String qtyAvailable;
	private String OnhandFlag;
	
	@JsonGetter("LocationType")
	public String getLocationType() {
		return locationType;
	}
	
	@JsonSetter("LocationType")
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
	
	@JsonGetter("LocationNumber")
	public String getLocationNumber() {
		return locationNumber;
	}
	
	@JsonSetter("LocationNumber")
	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}
	
	@JsonGetter("QtyAvailable")
	public String getQtyAvailable() {
		return qtyAvailable;
	}
	
	@JsonSetter("QtyAvailable")
	public void setQtyAvailable(String qtyAvailable) {
		this.qtyAvailable = qtyAvailable;
	}

	@JsonGetter("OnhandFlag")
	public String getOnhandFlag() {
		return OnhandFlag;
	}

	@JsonSetter("OnhandFlag")
	public void setOnhandFlag(String onhandFlag) {
		OnhandFlag = onhandFlag;
	}

	@Override
	public String toString() {
		return "LocationResponse [locationType=" + locationType + ", locationNumber=" + locationNumber
				+ ", qtyAvailable=" + qtyAvailable + ", OnhandFlag=" + OnhandFlag + "]";
	}	
}
