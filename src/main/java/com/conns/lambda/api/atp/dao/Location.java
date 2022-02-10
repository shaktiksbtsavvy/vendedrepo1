package com.conns.lambda.api.atp.dao;

import com.conns.lambda.api.atp.dao.AvailableToPromiseDao.LocationMaster;

public  class Location implements Comparable<Location>{
	/**
	 * @param longitude
	 * @param latitude
	 * @param type
	 * @param locationNumber
	 * @param distance
	 */
	public Location(Double longitude, Double latitude, String type, String locationNumber, Double distance) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.type = type;
		this.locationNumber = locationNumber;
		this.distance = distance;
	}
	
	public Location(LocationMaster lm, Double distance) {
		this(lm.getLatitude(), lm.getLongitude(), lm.getType(),lm.getLocationNumber(), distance);
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
}