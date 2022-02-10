package com.conns.lambda.api.atp;

import com.conns.lambda.api.atp.controller.AvailableToPromiseController;
import com.conns.lambda.common.http.APIRequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AvailableToPromiseHandler extends APIRequestHandler {

	// private static final ObjectMapper objectMapper = geObjectMapper();
	private static final Logger logger = LogManager.getLogger(AvailableToPromiseHandler.class);
	//@SuppressWarnings("unused")
	//private static final ResponseBuilder responseBuilder = new ResponseBuilder(); //this is for caching the handle
	//@SuppressWarnings("unused")
	//private static final InventoryDao inventoryDao = new InventoryDaoImpl(); //this is for caching the handle

	// Grant permission to API Gateway
	// aws lambda add-permission --function-name
	// "arn:aws:lambda:us-west-1:203315843709:function:${stageVariables.function}"
	// --source-arn
	// "arn:aws:execute-api:us-west-1:203315843709:c4czlvou82/*/GET/order/details/list"
	// --principal apigateway.amazonaws.com --statement-id
	// a52562d0-ae75-49e8-b4b5-2c7e845dba27 --action lambda:InvokeFunction"

	public AvailableToPromiseHandler() {
		logger.debug("registring controllers");
		controllers.register("POST",".*/atp", AvailableToPromiseController.getInstance());
	}

	
}