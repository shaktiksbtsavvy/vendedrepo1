package com.conns.lambda.api.atp.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableResponse;
import com.conns.lambda.api.atp.model.Inventory.LocationResponse;
import com.conns.lambda.api.atp.model.Inventory.ProductResponse;
import com.conns.lambda.api.atp.model.dd.DeliveryDateResponse;
import com.conns.lambda.api.atp.model.dd.NextDeliveryDateResponse;
import com.conns.lambda.api.atp.model.dd.PurchaseOrder;
import com.conns.lambda.api.atp.request.AvailableToPromiseRequest;
import com.conns.lambda.api.atp.response.PickupATPResponse;
import com.conns.lambda.api.atp.response.AvailableToPromiseResponse;
import com.conns.lambda.api.atp.response.DeliveryATPResponse;
import com.conns.lambda.common.exception.InvalidRequestException;
import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.http.ResponseErrorBody;

public class ResponseBuilder {
	protected static final int HTTP_200 = 200;
	protected static final int HTTP_500 = 500;
	protected static final String SUCCESS = "Success";
	private static final Logger logger = LogManager.getLogger(ResponseBuilder.class);

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
	public ResponseBody buildResponseObject(AvailableToPromiseRequest request, InventoryAvailableResponse invRes,
			DeliveryDateResponse ddRes)
			throws InvalidRequestException {
		return buildResponseObject(HTTP_200, SUCCESS, request, invRes, ddRes);
	}

	/**
	 * Build Response Object
	 * 
	 * @param code
	 * @param message
	 * @param reqID
	 * @return
	 */
	protected ResponseBody buildResponseObject(int code, String message, AvailableToPromiseRequest request,
			InventoryAvailableResponse invRes, DeliveryDateResponse ddRes) {
		AvailableToPromiseResponse tiwResponse = new AvailableToPromiseResponse();
		tiwResponse.setReqID(request.getReqID());
		tiwResponse.setCode(code);
		tiwResponse.setMessage(message);
		
		List<PickupATPResponse> pickupAtp = new ArrayList<PickupATPResponse>();
		List<DeliveryATPResponse> deliveryAtp = new ArrayList<DeliveryATPResponse>();
		
		tiwResponse.setPickupAtp(pickupAtp);
		tiwResponse.setDeliveryAtp(deliveryAtp);
		NextDeliveryDateResponse nddr = ddRes.getData() != null && ddRes.getData().size() > 0 ? ddRes.getData().get(0)
				: null;
		
		List<ProductResponse> productResponseList = invRes.getData();
		HashMap<String, String> nextDDDate = getPObySKU(ddRes);
		for (ProductResponse pr : productResponseList) {
			String skuName = pr.getSKU();
			for (LocationResponse lr : pr.getLocations()) {
				if (lr != null && lr.getLocationType().equalsIgnoreCase("STR")) {
					pickupAtp.add(new PickupATPResponse(skuName, lr.getQtyAvailable(), getTodayInCST()));
				} else if (lr != null && lr.getLocationType().equalsIgnoreCase("WH")) {
					String dateAvailble = null;
					if (lr.getOnhandFlag().equalsIgnoreCase("Y")) {
						dateAvailble = nddr != null ? nddr.getNextDeliveryDate() : null;
					} else {
						dateAvailble = nextDDDate.get(skuName);
					}
					deliveryAtp.add(new DeliveryATPResponse(skuName,request.getZip(), lr.getQtyAvailable(), dateAvailble));
				}
			}
			
		}

		return tiwResponse;
	}

	private HashMap<String, String> getPObySKU(DeliveryDateResponse ddRes) {
		List<NextDeliveryDateResponse> nddrList = ddRes.getData();
		HashMap<String, String> map = new HashMap<String, String>();
		for (NextDeliveryDateResponse nddr : nddrList) {
			List<PurchaseOrder> pos = nddr.getPurchaseOrder();
			for (PurchaseOrder po : pos) {
				map.put(po.getSku(), po.getNextDeliveryDate());
			}
		}
		return map;
	}


	private String getTodayInCST() {
		Calendar currentdate = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone obj = TimeZone.getTimeZone("CST");
		formatter.setTimeZone(obj);

		String today = formatter.format(currentdate.getTime());
		return today;
	}
}