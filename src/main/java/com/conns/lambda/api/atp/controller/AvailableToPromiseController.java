package com.conns.lambda.api.atp.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.conns.lambda.api.atp.dao.AvailableToPromiseDao;
import com.conns.lambda.api.atp.dao.LocationDTO;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableRequest;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableResponse;
import com.conns.lambda.api.atp.model.dd.DeliveryDateRequest;
import com.conns.lambda.api.atp.model.dd.DeliveryDateResponse;
import com.conns.lambda.api.atp.process.ClearanceResponseBuilder;
import com.conns.lambda.api.atp.process.ResponseBuilder;
import com.conns.lambda.api.atp.request.AvailableToPromiseRequest;
import com.conns.lambda.api.atp.response.AvailableToPromiseResponse;
import com.conns.lambda.common.controller.RequestController;
import com.conns.lambda.common.exception.ExceptionHandler;
import com.conns.lambda.common.exception.InternalServiceException;
import com.conns.lambda.common.exception.InvalidRequestWarning;
import com.conns.lambda.common.http.ApiResponseHeader;
import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.logging.Performance;
import com.conns.lambda.common.util.RequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author shakt
 *
 */
public class AvailableToPromiseController extends RequestController {

	private static final Logger logger = LogManager.getLogger(AvailableToPromiseController.class);
	private static final AvailableToPromiseDao dao = new AvailableToPromiseDao();
	private static final ResponseBuilder responseBuilder = new ResponseBuilder();
	private static final String regex5zip = "^[0-9]{5}$";
	private static final Pattern pattern5Zip = Pattern.compile(regex5zip);
	private static final String defaultZip = "77022";

	private AvailableToPromiseController() {
	}

	private static AvailableToPromiseController single_instance = null;

	// Static method
	// Static method to create instance of Singleton class
	public static AvailableToPromiseController getInstance() {
		if (single_instance == null)
			single_instance = new AvailableToPromiseController();
		return single_instance;
	}

	@Override
	public void loadCache(List<String> tables) {
//		try {
//				//dao.loadLocations(true);
//				logger.debug("location cache loaded.");
//		} catch (InternalServerError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.conns.lambda.common.controller.RequestController#handleRequest(com.
	 * amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent,
	 * com.conns.lambda.common.http.ApiResponseHeader)
	 */
	@Override
	public ResponseBody handleRequest(APIGatewayProxyRequestEvent apiRequest, ApiResponseHeader headers)
			throws InvalidRequestWarning, InternalServiceException {
		setRequestID(null);
		String requestBody = apiRequest.getBody();
		logger.debug("Request Body Received with upcs:{}", requestBody);

		AvailableToPromiseRequest atpRequest = null;

		Performance p1 = new Performance("Request Parsing", logger);
		p1.start();
		if (requestBody != null && !requestBody.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			try {
				atpRequest = mapper.readValue(requestBody, AvailableToPromiseRequest.class);
				atpRequest.setReqID(setRequestID(atpRequest.getReqID()));
				
				atpRequest = mapUPCs(atpRequest); //BPAD-154

				validateRequest(atpRequest);

				logger.debug("Request id:{}", atpRequest.getReqID());
			} catch (JsonMappingException e) {
				logger.debug(ExceptionHandler.getStackDetails(e));
				throwInvalidRequestException("Invalid Request body.");
			} catch (JsonProcessingException e) {
				logger.debug(ExceptionHandler.getStackDetails(e));
				throwInvalidRequestException("Invalid Request body.");
			}
		} else {
			throwInvalidRequestException("Invalid request:Request body not provided.");
		}
		p1.end();
		logger.debug("Request Body Mapped with sku list {}", atpRequest.getProducts());

		Performance p2 = new Performance("Get Locations Using Lambda", logger);
		p2.start();
		LocationDTO locationDTO = dao.getLocationsUsingLambda(atpRequest.getReqID(), atpRequest.getLatitude(),
				atpRequest.getLongitude(), atpRequest.getZip(), atpRequest.getDistance());
		p2.end();

		final AvailableToPromiseRequest atpReq = atpRequest;

		Performance p3 = new Performance("Send Async inventory Lambda ", logger);
		p3.start();
		CompletableFuture<InventoryAvailableResponse> inventoryLambdaFuture = CompletableFuture.supplyAsync(
				() -> getInventoryAvailable(locationDTO.getDLLocation(), locationDTO.getPULocations(), atpReq));
		p3.end();

		Performance p4 = new Performance("Send Async DD Lambda ", logger);
		p4.start();
		CompletableFuture<DeliveryDateResponse> ddLambdaFuture = CompletableFuture
				.supplyAsync(() -> getDeliveryDate(locationDTO, atpReq));
		p4.end();

		InventoryAvailableResponse invRes = null;
		DeliveryDateResponse ddRes = null;
		Performance p5 = new Performance("Get inventory Lambda ", logger);
		p5.start();
		try {
			invRes = inventoryLambdaFuture.get();
		} catch (InterruptedException e) {
			throw new InternalServiceException("InterruptedException from inventoryLambda.", e);
		} catch (ExecutionException e) {
			throw new InternalServiceException("ExecutionException from inventoryLambda.", e);
		}
		p5.end();

		Performance p6 = new Performance("Get DD Lambda ", logger);
		p6.start();
		try {
			ddRes = ddLambdaFuture.get();
		} catch (InterruptedException e) {
			throw new InternalServiceException("InterruptedException from ddLambda.", e);
		} catch (ExecutionException e) {
			throw new InternalServiceException("ExecutionException from ddLambda", e);
		}
		p6.end();

		Performance p7 = new Performance("Total build response from retrived inventory.", logger);
		p7.start();
		AvailableToPromiseResponse response = responseBuilder.buildResponseObject(atpRequest, invRes, ddRes, locationDTO);
		p7.end();
		logger.debug("After Calling responseBuilder.buildResponseObject:" + response);
		
//		https://conns.atlassian.net/browse/CIW-16118
		if(atpRequest.getClearanceStoreId() != null && atpRequest.getClearanceStoreId().length() > 0) {
			ClearanceResponseBuilder clearanceResponseBuilder = new ClearanceResponseBuilder();
			//int code, String message, AvailableToPromiseRequest request,InventoryAvailableResponse invRes,String clearanceStoreId, String clearanceStoreWearhouse, AvailableToPromiseResponse atpResponse, LocationDTO locationDTO, DeliveryDateResponse ddRes
			response = clearanceResponseBuilder.buildResponseObject(atpRequest.getClearanceStoreId(), atpRequest.getClearanceStoreWearhouse(), response, ddRes,locationDTO );
			logger.debug("After Calling clearanceResponseBuilder.buildResponseObject:" + response);
		} else {
			ClearanceResponseBuilder clearanceResponseBuilder = new ClearanceResponseBuilder();
			response = clearanceResponseBuilder.filterOutClearanceStores(response);
		}
		

		return response;
	}

	private void throwInvalidRequestException(String message) throws InvalidRequestWarning {
		throw new InvalidRequestWarning(message + " Expected - {\r\n" + "\"reqID\": \"265325gsfdgs\",\r\n"
				+ "\"products\": [\"AEE24DT\",\"AEE18DT\"],\r\n" + "\"latitude\": \"29.567719260470312\",\r\n"
				+ "\"longitude\": \"-95.68109502268624\",\r\n" + "\"zip\": 77479,\r\n" + "\"locale\": \"CDT\"\r\n"
				+ "}");
	}

	
	private AvailableToPromiseRequest mapUPCs(AvailableToPromiseRequest atpRequest) throws InvalidRequestWarning, InternalServiceException {
		
		if(atpRequest.getProducts() != null && atpRequest.getProducts().size() > 0) {
			return atpRequest;
		}
		
		if(atpRequest.getUpcs() != null && atpRequest.getUpcs().size() > 0) {
			atpRequest.setProducts(dao.getSkusForUpcs(atpRequest.getUpcs()));
		}
		
		return atpRequest;
	}
	
	private void validateRequest(AvailableToPromiseRequest atpRequest) throws InvalidRequestWarning {

//		RequestValidator.validateLatitude(atpRequest.getLatitude(), true);
//		RequestValidator.validateLongitude(atpRequest.getLongitude(), true);
//      RequestValidator.validateZip(atpRequest.getZip(), true);

		if (atpRequest.getZip() != null) {
			Matcher matcher = pattern5Zip.matcher(atpRequest.getZip());
			if (!matcher.matches()) {
				atpRequest.setZip(defaultZip);
			}
		} else {
			atpRequest.setZip(defaultZip);
		}
		try {
			if (atpRequest.getLatitude() != null) {
				double d = Double.parseDouble(atpRequest.getLatitude());

				if (d > 90 || d < -90) {
					atpRequest.setLatitude(null);
				}
			}
		} catch (NumberFormatException nfe) {
			atpRequest.setLatitude(null);
		}

		try {
			if (atpRequest.getLongitude() != null) {
				double d = Double.parseDouble(atpRequest.getLongitude());
				if (d > 180 || d < -180) {
					atpRequest.setLongitude(null);
				}
			}
		} catch (NumberFormatException nfe) {
			atpRequest.setLongitude(null);
		}

		RequestValidator.validateSkus(atpRequest.getProducts(), true);

	}

	/*
	 * // -----------------temp fix for //
	 * https://conns.atlassian.net/browse/CIW-9378--------------------- private void
	 * tempfixCIW9378(AvailableToPromiseRequest atpRequest) { //
	 * -----------------temp fix for //
	 * https://conns.atlassian.net/browse/CIW-9378--------------------- // --this is
	 * temp fix to hide UI error until BigC UI fix that their front end // code
	 * final String defaultLatitude = "29.8308828"; final String defaultLongitude =
	 * "-95.3858507"; final String defaultZip = "77022"; if (atpRequest.getZip() ==
	 * null || atpRequest.getZip().length() != 5) { logger.info("Invalid zip {}",
	 * atpRequest.getZip()); atpRequest.setZip(defaultZip); } if
	 * (atpRequest.getLatitude() == null || atpRequest.getLatitude().length() == 0)
	 * { logger.info("Invalid Latitude {}", atpRequest.getLatitude());
	 * atpRequest.setLatitude(defaultLatitude);
	 * atpRequest.setLongitude(defaultLongitude); } if (atpRequest.getLongitude() ==
	 * null || atpRequest.getLongitude().length() == 0) {
	 * logger.info("Invalid Longitude {}", atpRequest.getLongitude());
	 * atpRequest.setLatitude(defaultLatitude);
	 * atpRequest.setLongitude(defaultLongitude); } try {
	 * Double.parseDouble(atpRequest.getLatitude()); } catch (NumberFormatException
	 * nfe) { logger.info("Invalid Latitude {}", atpRequest.getLatitude());
	 * atpRequest.setLatitude(defaultLatitude);
	 * atpRequest.setLongitude(defaultLongitude); } try {
	 * Double.parseDouble(atpRequest.getLongitude()); } catch (NumberFormatException
	 * nfe) { logger.info("Invalid Longitude {}", atpRequest.getLongitude());
	 * atpRequest.setLatitude(defaultLatitude);
	 * atpRequest.setLongitude(defaultLongitude); } }
	 */

	private InventoryAvailableResponse getInventoryAvailable(String dlLocation, List<String> puLocations,
			AvailableToPromiseRequest atpRequest) {
		InventoryAvailableRequest invRequest = new InventoryAvailableRequest();
		invRequest.setReqID(atpRequest.getReqID());
		invRequest.setDL_Location(dlLocation);
		invRequest.setPU_Location(puLocations);
		invRequest.setSKU(atpRequest.getProducts());
		invRequest.setZipcode(atpRequest.getZip());
		InventoryAvailableResponse invRes = null;
		logger.debug("InventoryAvailableRequest to Inventory Lambda: {}", invRequest);
		try {
			invRes = dao.getInventoryLambda(invRequest);
			logger.debug("InventoryAvailableResponse from Inventory Lambda:{}", invRes);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CompletionException(e);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CompletionException(e);
		} catch (InternalServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CompletionException(e);
		}
		return invRes;
	}

	private DeliveryDateResponse getDeliveryDate(LocationDTO locationDTO, AvailableToPromiseRequest atpRequest) {
		DeliveryDateRequest ddRequest = new DeliveryDateRequest();
		ddRequest.setReqID(atpRequest.getReqID());
		ddRequest.setDL_Location(locationDTO.getDLLocation());
		ddRequest.setZipcode(atpRequest.getZip()); /// Request zip or location Zip
		ddRequest.setSKU(atpRequest.getProducts());
		DeliveryDateResponse ddRes = null;
		logger.debug("DeliveryDateRequest to DD Lambda: {}", ddRequest);
		try {
			ddRes = dao.getDDambda(ddRequest);
			logger.debug("DeliveryDateResponse from DD Lambda: {}", ddRes);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new CompletionException(e);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new CompletionException(e);
		} catch (InternalServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CompletionException(e);
		}
		return ddRes;
	}

}
