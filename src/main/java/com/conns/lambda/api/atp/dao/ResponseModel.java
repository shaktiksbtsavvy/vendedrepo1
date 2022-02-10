package com.conns.lambda.api.atp.dao;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class ResponseModel {
	
	@JsonProperty("statusCode")
	private  int statusCode;
	
	@JsonProperty("body")
	private String body;
	
	@JsonProperty("headers")
	private HashMap<String, String> headers;
	
	@JsonProperty("isBase64Encoded")
	private Boolean isBase64Encoded;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	public Boolean getIsBase64Encoded() {
		return isBase64Encoded;
	}

	public void setIsBase64Encoded(Boolean isBase64Encoded) {
		this.isBase64Encoded = isBase64Encoded;
	}
	

	
}
