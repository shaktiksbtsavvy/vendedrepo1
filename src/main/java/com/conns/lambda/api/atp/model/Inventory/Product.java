package com.conns.lambda.api.atp.model.Inventory;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class Product {
	
	private String sku;
	private Integer quantity;
	/**
	 * 
	 */
	public Product() {
		super();
	}

	
	@JsonGetter("sku")
	public String getSku() {
		return sku;
	}
	@JsonSetter("sku")
	public void setSku(String sku) {
		this.sku = sku;
	}

	@JsonGetter("qty")
	public Integer getQuantity() {
		return quantity;
	}
	@JsonSetter("qty")
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
