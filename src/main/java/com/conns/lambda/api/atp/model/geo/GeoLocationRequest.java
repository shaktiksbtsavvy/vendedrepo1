package com.conns.lambda.api.atp.model.geo;


import com.conns.lambda.common.http.ResponseBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocationRequest extends ResponseBody{
	
	/**
	 * @param reqID
	 * @param latitude
	 * @param longitude
	 * @param zip
	 * @param distance
	 */
	public GeoLocationRequest(String reqID, String latitude, String longitude, String zip, Double distance) {
		super();
		this.reqID = reqID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.zip = zip;
		this.distance = distance;
	}


	/**
	 * 
	 */
	public GeoLocationRequest() {
		super();
	}


	@JsonProperty("reqID")
	private String reqID;
	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
	@JsonProperty("zip")
	private String zip;
	
	@JsonProperty("distance")
	private Double distance;

	public String getReqID() {
		return reqID;
	}

	public void setReqID(String reqID) {
		this.reqID = reqID;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	
}
