package com.conns.lambda.api.atp.model.dd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;


@JsonPropertyOrder({ "SKU", "Locations"})
public class NextDeliveryDateResponse {
	
	/**
	 * 
	 */
	public NextDeliveryDateResponse() {
		super();
	}
	/**
	 * @param nextDeliveryDate
	 */
	public NextDeliveryDateResponse(String nextDeliveryDate) {
		super();
		this.nextDeliveryDate = nextDeliveryDate;
	}
	private String nextDeliveryDate;
	private List<PurchaseOrder> purchaseOrder;
	
	@JsonGetter("NextDeliveryDate")
	public String getNextDeliveryDate() {
		return nextDeliveryDate;
	}
	@JsonSetter("NextDeliveryDate")
	public void setNextDeliveryDate(String nextDeliveryDate) {
		this.nextDeliveryDate = nextDeliveryDate;
	}
	
	@JsonGetter("PurchaseOrder")
	public List<PurchaseOrder> getPurchaseOrder() {
		return purchaseOrder;
	}
	@JsonSetter("PurchaseOrder")
	public void setPurchaseOrder(List<PurchaseOrder> purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	@Override
	public String toString() {
		return "NextDeliveryDateResponse [nextDeliveryDate=" + nextDeliveryDate + ", purchaseOrder=" + purchaseOrder
				+ "]";
	}
	
	

}
