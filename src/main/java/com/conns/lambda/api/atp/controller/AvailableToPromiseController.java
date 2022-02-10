package com.conns.lambda.api.atp.controller;



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
		
		 LocationDTO locationDTO = dao.getLocations(atpRequest.getLatitude(), atpRequest.getLongitude());
		
		 
		try {
		InventoryAvailableRequest invRequest = new InventoryAvailableRequest();
		invRequest.setReqID(atpRequest.getReqID());
		invRequest.setDL_Location(locationDTO.getDLLocation());
		invRequest.setPU_Location(locationDTO.getPULocations());
		invRequest.setSKU(atpRequest.getProducts());
		invRequest.setZipcode(atpRequest.getZip());
		InventoryAvailableResponse invRes;

			invRes = dao.getInventoryLambda(invRequest);

		DeliveryDateRequest ddRequest = new DeliveryDateRequest();
		ddRequest.setReqID(atpRequest.getReqID());
		ddRequest.setDL_Location(locationDTO.getDLLocation());
		ddRequest.setZipcode(atpRequest.getZip());
		ddRequest.setSKU(atpRequest.getProducts());
		DeliveryDateResponse ddRes = dao.getDDambda(ddRequest);
		

		Performance p2 = new Performance("Total build response from retrived inventory.", logger);
		p2.start();
		ResponseBody response = responseBuilder.buildResponseObject(atpRequest, invRes, ddRes);
		p2.end();
		logger.debug("After Calling responseBuilder.buildResponseObject:" + response);
		
		return response;
		
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throwInvalidRequestException("");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throwInvalidRequestException("");
		}
		
		return null;
	}
	private void throwInvalidRequestException(String message) throws InvalidRequestException {
		throw new InvalidRequestException(message + " Expected - {\r\n" + 
				"\"products\": \"AEE24DT,AEE18DT,PM08X10008,PM28X329,QN85Q60AAFXZA,2W7P3UAABA\",\r\n" + 
				"\"latitude\": \"29.567719260470312\",\r\n" + 
				"\"longitude\": \"-95.68109502268624\",\r\n" + 
				"\"zip\": 77479,\r\n" + 
				"\"locale\": \"CDT\"\r\n" + 
				"}");
	}
	
	private void validateRequest(AvailableToPromiseRequest atpRequest ) throws InvalidRequestException {
		
		if(atpRequest.getLatitude() == null || atpRequest.getLatitude().length() == 0) {
			throwInvalidRequestException("Latitude is required.");
		}
		if(atpRequest.getLongitude()== null || atpRequest.getLongitude().length() == 0) {
			throwInvalidRequestException("Longitude is required.");
		}
		
	}
	
	/*
	private void callMySQL() throws ClassNotFoundException, SQLException {
		
		System.out.println( "Step 1:");
		
		Class.forName( "com.mysql.cj.jdbc.Driver" );
		System.out.println( "Step 2:");
		Connection conn = DriverManager.getConnection(
		 "jdbc:mysql://uat-db-cleanup-updated-7-20-21.c5rheudkn7b1.us-east-1.rds.amazonaws.com/magento",
		 "dbadmin",
		 "MTqrJtFEZZF3CyaG" );
		System.out.println( "Step 3:");
		try {
		     Statement stmt = conn.createStatement();
		     System.out.println( "Step 4:");
		try {
			String statement = "SELECT *, (3959 * acos(cos(radians(35.5306820000)) * cos(radians(latitude)) * cos(radians(longitude) - radians(-97.5758190000)) + sin(radians(35.5306820000)) * sin(radians(latitude)))) as distance, type= FROM conns_location HAVING (distance < 200) and (pickup = 1)  and (type in (‘Warehouse’, ‘Stores’ )) ORDER BY distance ASC limit 1";
			System.out.println( "Step 5:");
			System.out.println( "statement 1:" + statement );
		    ResultSet rs = stmt.executeQuery(statement);
		    System.out.println( "Step 6:");
		    System.out.println( "statement 2:" + statement );
		    try {
		        while ( rs.next() ) {
		            int numColumns = rs.getMetaData().getColumnCount();
		            for ( int i = 1 ; i <= numColumns ; i++ ) {
		               // Column numbers start at 1.
		               // Also there are many methods on the result set to return
		               //  the column as a particular type. Refer to the Sun documentation
		               //  for the list of valid conversions.
		               System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
		            }
		        }
	}
	
	*/

}
