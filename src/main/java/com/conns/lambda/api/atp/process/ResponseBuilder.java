package com.conns.lambda.api.atp.process;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import com.conns.lambda.api.atp.response.AvailableToPromiseResponse;
import com.conns.lambda.api.atp.response.DeliveryATPResponse;
import com.conns.lambda.api.atp.response.PickupATPResponse;
import com.conns.lambda.common.exception.InvalidRequestException;
import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.http.ResponseErrorBody;

public class ResponseBuilder {
	protected static final int HTTP_200 = 200;
	protected static final int HTTP_500 = 500;
	protected static final String SUCCESS = "Success";
	protected static final String _WH = "WH";
	protected static final String _STR = "STR";
	protected static final String _ONHANDFLAG_Y = "Y";
	protected static final String _ONHANDFLAG_N = "N";
	protected static final String _ONHANDFLAG_RY = "RY";
	protected static final String _ONHANDFLAG_RF = "RF"; //sending ‘RF’ for PO?
	private static final Logger logger = LogManager.getLogger(ResponseBuilder.class);
	
	private static final String _ADJUSTEDSTART = System.getenv("ADJUSTEDSTART") != null ? System.getenv("ADJUSTEDSTART").trim() : null; // INVTABLENAME
	private static final String _ADJUSTEDEND = System.getenv("ADJUSTEDEND") != null ? System.getenv("ADJUSTEDEND").trim() : null; // INVTABLENAME
	private static final String _ADJUSTEDDAYS = System.getenv("ADJUSTEDDAYS") != null ? System.getenv("ADJUSTEDDAYS").trim() : "0"; // INVTABLENAME
	private static final Integer _ADJUSTEDDAYSLONG = Integer.parseInt(_ADJUSTEDDAYS);
	
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
	public AvailableToPromiseResponse buildResponseObject(AvailableToPromiseRequest request, InventoryAvailableResponse invRes,
			DeliveryDateResponse ddRes, LocationDTO locationDTO) {
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
	/**
	 * @param code
	 * @param message
	 * @param request
	 * @param invRes
	 * @param ddRes
	 * @param locationDTO
	 * @return
	 */
	protected AvailableToPromiseResponse buildResponseObject(int code, String message, AvailableToPromiseRequest request,
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
					if ((lr != null && lr.getLocationType().equalsIgnoreCase(_STR) && loc.getPickup() == 1.0) // WH are
																												// already
																												// included
																												// in
																												// STRs
//							|| (lr != null && loc != null && lr.getLocationType().equalsIgnoreCase(_WH) && loc.getPickup() == 1.0
//									&& lr.getOnhandFlag().equalsIgnoreCase("Y"))
					) {
						logger.debug("8-Selected store location: {}.", loc != null ? loc.toString() : "");
						if (loc != null) {
							// pickupAtp.add(new PickupATPResponse(skuName, getTodayInCST(),
							// lr.getQtyAvailable()));
//							pickupAtp.add(new PickupATPResponse(skuName, lr.getLocationType(), loc.getLongitude(),
//									loc.getLatitude(), lr.getLocationNumber(), loc.getDistance(), lr.getQtyAvailable(),
//									getTodayInCST()));

							String qtyAvailable = lr.getQtyAvailable();
							String qtyStr = qtyAvailable;

							// https://conns.atlassian.net/browse/CIW-10195
//							try {
//								double qty = Double.parseDouble(qtyAvailable);
//								if (qty == 1) {
//									qty = 0;
//								}
//								qtyStr = String.valueOf(qty);
//							} catch (NumberFormatException nfe) {
//								qtyStr = qtyAvailable;
//							}
							// https://conns.atlassian.net/browse/CIW-10195

							pickupAtp.add(new PickupATPResponse(skuName, loc.getStoreResponse().getStoreUrl(),
									lr.getLocationType(), loc.getStoreResponse().getStoreName(), loc.getLongitude(),
									loc.getLatitude(), loc.getStoreResponse().getStorePhone(), lr.getLocationNumber(),
									loc.getDistance(), getDateInCST(), qtyStr, loc.getStoreResponse().getStoreZip(),
									loc.getStoreResponse().getStoreState(), loc.getStoreResponse().getStoreAddressln2(),
									loc.getStoreResponse().getStoreAddressln1(), loc.getStoreResponse().getStoreCity(),
									loc.getStoreResponse().getStoreClosingTime(),
									loc.getStoreResponse().getStoreHours()));
						}

					}
					if (lr != null && lr.getLocationType().equalsIgnoreCase(_WH)) {
						String dateAvailble = null;
						// Location loc = whLocations.get(lr.getLocationNumber());
						// logger.debug("8-Selected warehouse location: {}.", loc != null ?
						// loc.toString() : "");
						// if (loc != null) { //Waehouse lookup is based on zipcode, not on Geo
						if (lr.getOnhandFlag().equalsIgnoreCase(_ONHANDFLAG_Y)) {
							dateAvailble = nddr != null ? nddr.getNextDeliveryDate() : null;
						} else if (lr.getOnhandFlag().equalsIgnoreCase(_ONHANDFLAG_RY)) {
							// CIW-9941
							dateAvailble = nddr != null ? nddr.getRdc_nextDeliveryDate() : null;
						} else {
							dateAvailble = nextDDDate.get(skuName);
						}
						logger.debug("9-dateAvailble: {}.", dateAvailble);

						// https://conns.atlassian.net/browse/CIW-11856
//						deliveryAtp.add(new DeliveryATPResponse(skuName, request.getZip(), lr.getQtyAvailable(),
//								dateAvailble, lr.getOnhandFlag()));

//						Location whLoc = loc != null ? loc : whLocations.get(lr.getLocationNumber());
//						logger.debug("Warehouse location :{} is :{} in GeoLocation Table", lr.getLocationNumber(), whLoc);
//
//						String dcName = whLoc != null ? (whLoc.getStoreResponse() != null ? whLoc.getStoreResponse().getStoreName() : "") : "";

						// https://conns.atlassian.net/browse/CIW-12617
						if(applyDeliveryDateRuleCIW12617(dateAvailble)) {
						    logger.debug("sku:{}  location: {}", skuName, lr.getLocationNumber());
							deliveryAtp.add(new DeliveryATPResponse(skuName, request.getZip(), lr.getQtyAvailable(),
									dateAvailble, changeRDC_RYtoY_EAI_91(lr.getOnhandFlag()), lr.getLocationType(), lr.getLocationNumber()));
						}
						
//						deliveryAtp.add(new DeliveryATPResponse(skuName, request.getZip(), lr.getQtyAvailable(),
//								dateAvailble, lr.getOnhandFlag(), lr.getLocationType(), lr.getLocationNumber()));

					}
				}

			}
		}
		tiwResponse.sort();
		return tiwResponse;
	}

	
	//https://conns.atlassian.net/browse/EAI-91
	//onhand_flag as ‘RY’ for RDC and RF for PO, Front end team is asking if that can be sent as ‘Y’ itself,
	private String changeRDC_RYtoY_EAI_91(String onHandFlag) {
	    logger.debug("changeRDC_RYtoY_EAI_91 onHandFlag:{}", onHandFlag);
	    if(onHandFlag != null && onHandFlag.equalsIgnoreCase(_ONHANDFLAG_RY)) {
	        return _ONHANDFLAG_Y;
	    } else if(onHandFlag != null && onHandFlag.equalsIgnoreCase(_ONHANDFLAG_RF)) {
            return _ONHANDFLAG_N;
        } else {
            return onHandFlag;
        }
	}
	
	
	// https://conns.atlassian.net/browse/CIW-12617
	// We will have to ignore the delivery_atp if the zipcode is not available or
	// the delivery date is in the past. Please make this change in ATP
	private Boolean applyDeliveryDateRuleCIW12617(String ddDateStr) {
		logger.debug("Delivery Date: {}", ddDateStr);
		if (ddDateStr != null) {
			LocalDate ddDate = LocalDate.parse(ddDateStr);
			LocalDate today = LocalDate.now();
			logger.debug("Delivery Date: {}", ddDate);
			logger.debug("Today Date: {}", today);
			int compareValue = ddDate.compareTo(today);
			logger.debug("compareValue: {}", compareValue);
			if (compareValue >= 0) {
				return true;
			}
		}
		return false;
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
	
	//added for https://conns.atlassian.net/browse/CIW-16812
	private String getDateInCST() {
		logger.debug("Inside getDateInCST::");
		Date currentDate = new Date();
		
		Date startDate = null;
		Date endTime = null;
		if(_ADJUSTEDSTART != null && _ADJUSTEDEND != null ) {
			logger.debug("getDateInCST:: start and end date from env");
			try {
				startDate = parse(_ADJUSTEDSTART);
				endTime = parse(_ADJUSTEDEND);
			} catch (Exception e) {
				logger.error("Setup The Pickup Date For Holidays Failed!!!! for " + _ADJUSTEDSTART +" "+ _ADJUSTEDEND);
				e.printStackTrace();
			}
		}
		Integer numberDays = _ADJUSTEDDAYSLONG;
		if(startDate != null &&  endTime != null &&  numberDays > 0) {
			logger.debug("getDateInCST:: setting current PU date");
			if(currentDate.after(startDate) && currentDate.before(endTime) ) {
				logger.debug("getDateInCST:: setting current PU date 2");
				currentDate = addDays(currentDate, numberDays);
			}
		}
		
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone obj = TimeZone.getTimeZone("CST");
		formatter.setTimeZone(obj);
		String today = formatter.format(currentDate);
		logger.debug("getDateInCST:: returning current PU date: "+today);
		return today;
	}
	
	private Date parse(String strDate) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-dd-yyyy hh:mm a z");
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(strDate, formatter);
		//return LocalDateTime.from(zonedDateTime.toInstant());
		return Date.from(zonedDateTime.toInstant());
	}
	
	
	private Date addDays(Date currentDate, Integer days){

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, days); 
        Date currentDatePlusOne = c.getTime();
        return currentDatePlusOne;
    }


}