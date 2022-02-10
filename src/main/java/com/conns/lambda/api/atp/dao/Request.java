package com.conns.lambda.api.atp.dao;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {
	
	@JsonProperty("headers")
	HashMap<String,String> headers;
	
	@JsonProperty("pathParameters")
	HashMap<String,String> pathParameters;
	
	@JsonProperty("path")
	String path;
	
	@JsonProperty("httpMethod")
	String method;
	
	@JsonProperty("body")
	String body;

	public HashMap<String,String> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String,String> headers) {
		this.headers = headers;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HashMap<String, String> getPathParameters() {
		return pathParameters;
	}

	public void setPathParameters(HashMap<String, String> pathParameters) {
		this.pathParameters = pathParameters;
	}


}
