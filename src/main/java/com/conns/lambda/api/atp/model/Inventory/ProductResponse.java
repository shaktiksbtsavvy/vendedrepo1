package com.conns.lambda.api.atp.model.Inventory;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;


@JsonPropertyOrder({ "SKU", "Locations"})
public class ProductResponse {
	
	/**
	 * 
	 */
	public ProductResponse() {
		super();
	}

	private String SKU;
	private List<LocationResponse> locations;
	
	/**
	 * @param sKU
	 */
	public ProductResponse(String sKU) {
		super();
		this.SKU = sKU;
		this.locations = new ArrayList<LocationResponse>();
	}

	@JsonGetter("SKU")
	public String getSKU() {
		return SKU;
	}
	
	@JsonSetter("SKU")
	public void setSKU(String sKU) {
		this.SKU = sKU;
	}
	
	@JsonGetter("Locations")
	public List<LocationResponse> getLocations() {
		return locations;
	}
	
	@JsonSetter("Locations")
	public void setLocations(List<LocationResponse> locations) {
		this.locations = locations;
	}
	
	public void addLocation(Location location) {
		this.locations.add(new LocationResponse(location));
	}

}
