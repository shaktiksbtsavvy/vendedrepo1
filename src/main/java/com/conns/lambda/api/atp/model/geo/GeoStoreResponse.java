package com.conns.lambda.api.atp.model.geo;

import java.util.List;

import com.conns.lambda.common.http.ResponseBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoStoreResponse extends ResponseBody{

	public GeoStoreResponse(List<StoreResponse> stores, int code, String message) {
		super();
		this.stores = stores;
		this.code = code;
		this.message = message;
	}

	public GeoStoreResponse(List<StoreResponse> stores) {
		super();
		this.stores = stores;
	}

	/**
	 * 
	 */
	public GeoStoreResponse() {
		super();
	}

	List<StoreResponse> stores;
	int code;
	String message;
	private String errorDetails;

	public List<StoreResponse> getStores() {
		return stores;
	}

	public void setStores(List<StoreResponse> stores) {
		this.stores = stores;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}
	
	
}
