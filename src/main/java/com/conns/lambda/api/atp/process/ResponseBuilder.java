package com.conns.lambda.api.atp.process;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
	protected static final String _WH = "WH";
	protected static final String _STR = "STR";
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
			DeliveryDateResponse ddRes, LocationDTO locationDTO) throws InvalidRequestException {
		return buildResponseObject(HTTP_200, SUCCESS, request, invRes, ddRes, locationDTO);
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
			InventoryAvailableResponse invRes, DeliveryDateResponse ddRes, LocationDTO locationDTO) {

		logger.debug("1-Starting build response.");
		AvailableToPromiseResponse tiwResponse = new AvailableToPromiseResponse();
		tiwResponse.setReqID(request.getReqID());
		tiwResponse.setCode(code);
		tiwResponse.setMessage(message);
		HashMap<String, Location> storeLocations = locationDTO.getStoreLocations();
		HashMap<String, Location> whLocations = locationDTO.getWhLocations();

		logger.debug("storeLocations size:{}" + storeLocations != null ? storeLocations.size() : storeLocations);
		logger.debug("whLocations size:{}" + whLocations != null ? whLocations.size() : whLocations);

		debugList("2-Store Locations", storeLocations.values());
		debugList("3-Warehouse Locations", whLocations.values());

		List<PickupATPResponse> pickupAtp = new ArrayList<PickupATPResponse>();
		List<DeliveryATPResponse> deliveryAtp = new ArrayList<DeliveryATPResponse>();

		tiwResponse.setPickupAtp(pickupAtp);
		tiwResponse.setDeliveryAtp(deliveryAtp);
		NextDeliveryDateResponse nddr = ddRes.getData() != null && ddRes.getData().size() > 0 ? ddRes.getData().get(0)
				: null;

		logger.debug("4-NextDeliveryDateResponse {}", nddr != null ? nddr.toString() : "");

		List<ProductResponse> productResponseList = invRes.getData();
		debugList("5-productResponseList", productResponseList);

		HashMap<String, String> nextDDDate = getPObySKU(ddRes);
		if (productResponseList.size() > 0) {
			for (ProductResponse pr : productResponseList) {
				String skuName = pr.getSKU();
				logger.debug("6-skuName: {}.", skuName);
				for (LocationResponse lr : pr.getLocations()) {
					logger.debug("7-LocationResponse: {}.", lr != null ? lr.toString() : "");

					Location loc = storeLocations.get(lr.getLocationNumber());
					// The below WH data is for Pickup from different WH

					if (loc == null) {
						logger.debug("7.1-inventory store location is null.");
						loc = whLocations.get(lr.getLocationNumber());
					}
					if ((lr != null && lr.getLocationType().equalsIgnoreCase(_STR) && loc.getPickup() == 1.0)
							|| (lr != null && loc != null && lr.getLocationType().equalsIgnoreCase(_WH) && loc.getPickup() == 1.0
									&& lr.getOnhandFlag().equalsIgnoreCase("Y"))) {
						logger.debug("8-Selected store location: {}.", loc != null ? loc.toString() : "");
						if (loc != null) {
							// pickupAtp.add(new PickupATPResponse(skuName, getTodayInCST(),
							// lr.getQtyAvailable()));
							pickupAtp.add(new PickupATPResponse(skuName, lr.getLocationType(), loc.getLongitude(),
									loc.getLatitude(), lr.getLocationNumber(), loc.getDistance(), lr.getQtyAvailable(),
									getTodayInCST()));
						}

					}
					if (lr != null && lr.getLocationType().equalsIgnoreCase(_WH)) {
						String dateAvailble = null;
						// Location loc = whLocations.get(lr.getLocationNumber());
						// logger.debug("8-Selected warehouse location: {}.", loc != null ?
						// loc.toString() : "");
						// if (loc != null) { //Waehouse lookup is based on zipcode, not on Geo
						if (lr.getOnhandFlag().equalsIgnoreCase("Y")) {
							dateAvailble = nddr != null ? nddr.getNextDeliveryDate() : null;
						} else {
							dateAvailble = nextDDDate.get(skuName);
						}
						logger.debug("9-dateAvailble: {}.", dateAvailble);
						deliveryAtp.add(new DeliveryATPResponse(skuName, request.getZip(), lr.getQtyAvailable(),
								dateAvailble));
						// }

					}
				}

			}
		}
		tiwResponse.sort();
		return tiwResponse;
	}

	private void debugList(String message, Collection coll) {
		List objList = new ArrayList(coll);
		debugList(message, objList);
	}

	private void debugList(String message, List objList) {
		logger.debug("Starting printing:" + message);
		if (objList != null) {
			for (Object o : objList) {
				logger.debug(o.toString());
			}
		}
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