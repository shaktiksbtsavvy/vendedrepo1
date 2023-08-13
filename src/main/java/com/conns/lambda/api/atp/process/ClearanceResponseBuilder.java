package com.conns.lambda.api.atp.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.conns.lambda.api.atp.dao.Location;
import com.conns.lambda.api.atp.dao.LocationDTO;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableResponse;
import com.conns.lambda.api.atp.model.Inventory.LocationResponse;
import com.conns.lambda.api.atp.model.Inventory.ProductResponse;
import com.conns.lambda.api.atp.model.dd.DeliveryDateResponse;
import com.conns.lambda.api.atp.model.dd.NextDeliveryDateResponse;
import com.conns.lambda.api.atp.model.dd.PurchaseOrder;
import com.conns.lambda.api.atp.request.AvailableToPromiseRequest;
import com.conns.lambda.api.atp.response.ATPDetailsResponse;
import com.conns.lambda.api.atp.response.AvailableToPromiseResponse;
import com.conns.lambda.api.atp.response.DeliveryATPResponse;
import com.conns.lambda.api.atp.response.PickupATPResponse;
import com.conns.lambda.common.exception.InvalidRequestException;
import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.http.ResponseErrorBody;

public class ClearanceResponseBuilder {
	protected static final int HTTP_200 = 200;
	protected static final int HTTP_500 = 500;
	protected static final String SUCCESS = "Success";
	protected static final String _WH = "WH";
	protected static final String _STR = "STR";
	protected static final String _ONHANDFLAG_Y = "Y";
	protected static final String _ONHANDFLAG_N = "N";
	protected static final String _ONHANDFLAG_RY = "RY";
	protected static final String _ONHANDFLAG_RF = "RF"; //sending ‘RF’ for PO?
	
	private static final String _CLEARANCEBUFFERDAYS= System.getenv("CLEARANCEBUFFERDAYS") != null
			? System.getenv("CLEARANCEBUFFERDAYS").trim()
			: "5"; // _LOCATIONTABLE
	
	private static final Logger logger = LogManager.getLogger(ClearanceResponseBuilder.class);

	public ResponseBody buildErrorResponseObject(int code, String message, String errorDetails) {
		return new ResponseErrorBody(code, message, errorDetails);
	}

	public ResponseBody buildErrorResponseObject(String message) {
		return new ResponseErrorBody(HTTP_500, message, "");
	}

	/**
	 * Build Response Object
	 * 
	 * @param code
	 * @param message
	 * @param reqID
	 * @return
	 * @throws InvalidRequestException
	 */
	public AvailableToPromiseResponse buildResponseObject(String clearanceStoreId, String clearanceStoreWearhouse, AvailableToPromiseResponse atpResponse, LocationDTO locationDTO) {
		
		List<PickupATPResponse> pickupAtpClearanceList = new ArrayList<PickupATPResponse>();
		
		String closestWarehouse = locationDTO.getDLLocation();
		
		if(closestWarehouse != null && closestWarehouse.equalsIgnoreCase(clearanceStoreWearhouse)) {
			for( PickupATPResponse pickupAtp:  atpResponse.getPickupAtp()) {
				if(pickupAtp.getLocation().equalsIgnoreCase(clearanceStoreId)) {
					pickupAtpClearanceList.add(pickupAtp);
				}
			}
		} else {
			logger.info("Clearance Store Wearhouse :{} does not match with returnend warehouse: {}",clearanceStoreWearhouse,  closestWarehouse);
		}
		
		AvailableToPromiseResponse atpClearance = new AvailableToPromiseResponse();
	
		
		List<DeliveryATPResponse> deliveryAtpClearanceList = new ArrayList<DeliveryATPResponse>();
		
		for(PickupATPResponse pickupClearance: pickupAtpClearanceList) {
			
			DeliveryATPResponse delClearance = new DeliveryATPResponse();
			delClearance.setSku(pickupClearance.getSku());
			delClearance.setZip(pickupClearance.getAddressZip());
			delClearance.setInventoryLocation(pickupClearance.getLocation());
			delClearance.setInventoryLocationType("WH");
			delClearance.setAvailableQty(pickupClearance.getAvailableQty());
			delClearance.setOnhandFlag(pickupClearance.getAvailableQty() > 0 ? "Y": "N");
			delClearance.setAtpDetails(new ATPDetailsResponse(pickupClearance.getAvailableQty() > 0 ? getClearanceInCST(): null, null,  null));
			
			deliveryAtpClearanceList.add(delClearance);
			
		}
		
		atpClearance.setDeliveryAtp(deliveryAtpClearanceList);
		atpClearance.setPickupAtp(pickupAtpClearanceList);
		
		return atpClearance;
	}
	
	
	private String getClearanceInCST() {
		Integer days = Integer.parseInt(_CLEARANCEBUFFERDAYS);
		
		Calendar clerancedate = Calendar.getInstance();
		clerancedate.add(Calendar.DAY_OF_MONTH, days);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone obj = TimeZone.getTimeZone("CST");
		formatter.setTimeZone(obj);

		String day = formatter.format(clerancedate.getTime());
		return day;
	}

}