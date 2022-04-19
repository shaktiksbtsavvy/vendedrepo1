package com.conns.lambda.api.atp.dao;

import com.conns.lambda.api.atp.dao.AvailableToPromiseDao.LocationMaster;
import com.conns.lambda.api.atp.model.geo.StoreResponse;

public  class Location implements Comparable<Location>{
	/**
	 * @param longitude
	 * @param latitude
	 * @param type
	 * @param locationNumber
	 * @param distance
	 * @param zip
	 */
	public Location(Double longitude, Double latitude, String type, String locationNumber, Double distance,
			String zip, Double pickup, StoreResponse sr) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.type = type;
		this.locationNumber = locationNumber;
		this.distance = distance;
		this.zip = zip;
		this.pickup = pickup;
		this.storeResponse = sr;
	}

	
	public Location(LocationMaster lm, Double distance) {
		this(lm.getLatitude(), lm.getLongitude(), lm.getType(),lm.getLocationNumber(), distance, lm.getZip(), lm.getPickup(), null);
	}
	
	public Location(StoreResponse sr) {
		this(sr.getLatitude(), sr.getLongitude(), sr.getType(),sr.getStoreId(), sr.getStoreDistance(), sr.getStoreZip(),sr.getPickup(), sr);
	}
	
	/**
	 * 
	 */
	public Location() {
		super();
	}
	
	
	private Double longitude;
	private Double latitude;
	private String type;
	private String locationNumber;
	private Double distance;
	private String zip;
	private Double pickup;
	private StoreResponse storeResponse;

	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLocationNumber() {
		return locationNumber;
	}
	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}
	
	public int compareTo(Location location) {
		Double compareDistance = location.getDistance(); 
		if( this.distance >  compareDistance) {
			return 1;
		}
		else if( this.distance <  compareDistance) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}


	@Override
	public String toString() {
		return "Location [longitude=" + longitude + ", latitude=" + latitude + ", type=" + type + ", locationNumber="
				+ locationNumber + ", distance=" + distance + ", zip=" + zip + ", StoreResponse=" + storeResponse + "]";
	}


	public Double getPickup() {
		return pickup;
	}


	public void setPickup(Double pickup) {
		this.pickup = pickup;
	}

	public StoreResponse getStoreResponse() {
		return storeResponse;
	}


	public void setStoreResponse(StoreResponse storeResponse) {
		this.storeResponse = storeResponse;
	}
}