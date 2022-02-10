package com.conns.lambda.api.atp.model.dd;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class PurchaseOrder {
	/**
	 * 
	 */
	public PurchaseOrder() {
		super();
	}
	/**
	 * @param sku
	 * @param nextDeliveryDate
	 */
	public PurchaseOrder(String sku, String nextDeliveryDate) {
		super();
		this.sku = sku;
		this.nextDeliveryDate = nextDeliveryDate;
	}
	private String sku;
	private String nextDeliveryDate;
	
	@JsonGetter("SKU")
	public String getSku() {
		return sku;
	}
	@JsonSetter("SKU")
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	@JsonGetter("NextDeliveryDate")
	public String getNextDeliveryDate() {
		return nextDeliveryDate;
	}
	@JsonSetter("NextDeliveryDate")
	public void setNextDeliveryDate(String nextDeliveryDate) {
		this.nextDeliveryDate = nextDeliveryDate;
	}

}
