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

	public List<StoreResponse> getStores() {
		return stores;
	}

	public void setStores(List<StoreResponse> stores) {
		this.stores = stores;
	}
	
	
}
