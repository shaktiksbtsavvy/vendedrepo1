package com.conns.lambda.api.atp.model.dd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonPropertyOrder({ "SKU", "Locations"})
@JsonIgnoreProperties(ignoreUnknown = true)
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
	
	@JsonInclude(Include.NON_NULL)
	private String rdc_nextDeliveryDate;
	
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
	
	@JsonGetter("RDC_NextDeliveryDate")
	public String getRdc_nextDeliveryDate() {
		return rdc_nextDeliveryDate;
	}
	
	@JsonSetter("RDC_NextDeliveryDate")
	public void setRdc_nextDeliveryDate(String rdc_nextDeliveryDate) {
		this.rdc_nextDeliveryDate = rdc_nextDeliveryDate;
	}
	
	@Override
	public String toString() {
		return "NextDeliveryDateResponse [nextDeliveryDate=" + nextDeliveryDate + ", purchaseOrder=" + purchaseOrder
				+ "]";
	}
	
	

}
