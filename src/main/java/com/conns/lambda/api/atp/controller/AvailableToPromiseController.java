package com.conns.lambda.api.atp.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.conns.lambda.api.atp.dao.AvailableToPromiseDao;
import com.conns.lambda.api.atp.dao.LocationDTO;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableRequest;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableResponse;
import com.conns.lambda.api.atp.model.dd.DeliveryDateRequest;
import com.conns.lambda.api.atp.model.dd.DeliveryDateResponse;
import com.conns.lambda.api.atp.process.ResponseBuilder;
import com.conns.lambda.api.atp.request.AvailableToPromiseRequest;
import com.conns.lambda.common.controller.RequestController;
import com.conns.lambda.common.exception.ExceptionHandler;
import com.conns.lambda.common.exception.InternalServerError;
import com.conns.lambda.common.exception.InvalidRequestException;
import com.conns.lambda.common.http.ApiResponseHeader;
import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.logging.Performance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AvailableToPromiseController extends RequestController {

	private static final Logger logger = LogManager.getLogger(AvailableToPromiseController.class);
	private static final AvailableToPromiseDao dao = new AvailableToPromiseDao();
	private static final ResponseBuilder responseBuilder = new ResponseBuilder();

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
		try {
				dao.loadLocations(true);
				logger.debug("location cache loaded.");
		} catch (InternalServerError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.conns.lambda.common.controller.RequestController#handleRequest(com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent, com.conns.lambda.common.http.ApiResponseHeader)
	 */
	@Override
	public ResponseBody handleRequest(APIGatewayProxyRequestEvent apiRequest, ApiResponseHeader headers)
			throws InternalServerError, InvalidRequestException {
		setRequestID(null);
		String requestBody = apiRequest.getBody();
		logger.debug("Request Body Received:{}", requestBody);

		AvailableToPromiseRequest atpRequest = null;

		Performance p1 = new Performance("Request Parsing", logger);
		p1.start();
		if (!requestBody.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			try {
				atpRequest = mapper.readValue(requestBody, AvailableToPromiseRequest.class);
				validateRequest(atpRequest);
				setRequestID(atpRequest.getReqID());
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

		
		LocationDTO locationDTO = dao.getLocationsUsingLambda(atpRequest.getReqID(), atpRequest.getLatitude(), atpRequest.getLongitude());
		
		//LocationDTO locationDTO = dao.getLocations(atpRequest.getLatitude(), atpRequest.getLongitude());

		final AvailableToPromiseRequest atpReq = atpRequest;
		
		
		CompletableFuture<InventoryAvailableResponse> inventoryLambdaFuture = CompletableFuture.supplyAsync(() -> getInventoryAvailable(locationDTO,atpReq));
		if(locationDTO.getDLLocation()== null || locationDTO.getDLLocation() == "") {
			throw new InvalidRequestException("No warehouse found within the delivery radius.");
		}
		CompletableFuture<DeliveryDateResponse> ddLambdaFuture = CompletableFuture.supplyAsync(() -> getDeliveryDate(locationDTO,atpReq));
		InventoryAvailableResponse invRes = null;
		DeliveryDateResponse ddRes = null;
		try {
			invRes = inventoryLambdaFuture.get();
			ddRes = ddLambdaFuture.get();
		} catch (InterruptedException e) {
			logger.debug(ExceptionHandler.getStackDetails(e));
			throwInvalidRequestException(e.getMessage());
		} catch (ExecutionException e) {
			logger.debug(ExceptionHandler.getStackDetails(e));
			throwInvalidRequestException(e.getMessage());
		}

		Performance p2 = new Performance("Total build response from retrived inventory.", logger);
		p2.start();
		ResponseBody response = responseBuilder.buildResponseObject(atpRequest, invRes, ddRes, locationDTO);
		p2.end();
		logger.debug("After Calling responseBuilder.buildResponseObject:" + response);

		return response;
	}

	private void throwInvalidRequestException(String message) throws InvalidRequestException {
		throw new InvalidRequestException(message + " Expected - {\r\n" + 
				"\"reqID\": \"265325gsfdgs\",\r\n" + 
				"\"products\": [\"AEE24DT\",\"AEE18DT\"],\r\n" + 
				"\"latitude\": \"29.567719260470312\",\r\n" + 
				"\"longitude\": \"-95.68109502268624\",\r\n" + 
				"\"zip\": 77479,\r\n" + 
				"\"locale\": \"CDT\"\r\n" + 
				"}");
	}

	private void validateRequest(AvailableToPromiseRequest atpRequest) throws InvalidRequestException {

		if (atpRequest.getLatitude() == null || atpRequest.getLatitude().length() == 0) {
			throwInvalidRequestException("Latitude is required.");
		}
		if (atpRequest.getLongitude() == null || atpRequest.getLongitude().length() == 0) {
			throwInvalidRequestException("Longitude is required.");
		}

	}

	private InventoryAvailableResponse getInventoryAvailable(LocationDTO locationDTO, AvailableToPromiseRequest atpRequest){
		InventoryAvailableRequest invRequest = new InventoryAvailableRequest();
		invRequest.setReqID(atpRequest.getReqID());
		invRequest.setDL_Location(locationDTO.getDLLocation());
		invRequest.setPU_Location(locationDTO.getPULocations());
		invRequest.setSKU(atpRequest.getProducts());
		invRequest.setZipcode(atpRequest.getZip());
		InventoryAvailableResponse invRes;
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
		}
		return invRes;
	}
	
	private DeliveryDateResponse getDeliveryDate(LocationDTO locationDTO, AvailableToPromiseRequest atpRequest) {
		DeliveryDateRequest ddRequest = new DeliveryDateRequest();
		ddRequest.setReqID(atpRequest.getReqID());
		ddRequest.setDL_Location(locationDTO.getDLLocation());
		ddRequest.setZipcode(locationDTO.getDLZip()); ///Request zip or location Zip
		ddRequest.setSKU(atpRequest.getProducts());
		DeliveryDateResponse ddRes;
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
		}
		return ddRes;
	}


}
