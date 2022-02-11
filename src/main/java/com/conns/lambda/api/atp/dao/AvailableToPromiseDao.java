package com.conns.lambda.api.atp.dao;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableRequest;
import com.conns.lambda.api.atp.model.Inventory.InventoryAvailableResponse;
import com.conns.lambda.api.atp.model.dd.DeliveryDateRequest;
import com.conns.lambda.api.atp.model.dd.DeliveryDateResponse;
import com.conns.lambda.common.dao.DaxDataAccessObject;
import com.conns.lambda.common.dao.LambdaDataAccessObject;
import com.conns.lambda.common.exception.DBException;
import com.conns.lambda.common.exception.InternalServerError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AvailableToPromiseDao extends DaxDataAccessObject implements LambdaDataAccessObject {
	private static final ObjectMapper mapper = new ObjectMapper(); // Use single instance of ObjectMapper in your code.
	private static final String _INVENTORYFUN = "INVENTORYFUN";
	private static final String _DDFUN = "DDFUN";
	
	private static final String _LONGITUDE = "longitude";
	private static final String _LATITUDE = "latitude";
	private static final String _TYPE = "type";
	private static final String _LOCATIONNUMBER = "number";
	private static final String _PICKUP = "pickup";
	private static final String _ZIP = "zip";
	
	private static final String _LOCATIONTABLE = "LOCATIONTABLE";
	private static final Logger logger = LogManager.getLogger(AvailableToPromiseDao.class);
	private static final String locationTable = System.getenv(_LOCATIONTABLE) != null ? System.getenv(_LOCATIONTABLE).trim() : null; // _LOCATIONTABLE
	
	private static final String _DISTANCETHRESHOLD = "DISTANCETHRESHOLD";
	private static final String DISTANCETHRESHOLD = System.getenv(_DISTANCETHRESHOLD) != null ? System.getenv(_DISTANCETHRESHOLD).trim() : null; // _LOCATIONTABLE
	
	private static final String _STORETYPE = "Store";
	private static final String _WHTYPE = "Warehouse";
	
	private static Set<LocationMaster> locations =null;
	
	
	private static final String InventoryFunction = System.getenv(_INVENTORYFUN) != null ? System.getenv(_INVENTORYFUN).trim() : null; // INVTABLENAME
	private static final String ddFunction = System.getenv(_DDFUN) != null ? System.getenv(_DDFUN).trim() : null; // INVTABLENAME
	
	private static final AWSLambda awsLambda = AWSLambdaClientBuilder.standard().build();

	public AvailableToPromiseDao() {
	}
	
	
	public LocationDTO getLocations(String lati, String longi) throws InternalServerError {
		
		loadLocations();
		
		 double distanceThresh = Double.parseDouble(DISTANCETHRESHOLD);
		 
		 logger.debug("Distance Thresh is :{}", distanceThresh);
		 
		 if(distanceThresh == 0 ) {
			 throw new InternalServerError("Invalid threshold distance.");
		 }
		 
		 double lat = Double.parseDouble(lati);
		 double lon = Double.parseDouble(longi);
		 HashMap<String, Location> storeLocations = new HashMap<String, Location>();
		 HashMap<String, Location> whLocations = new HashMap<String, Location>();
		 
		 for(LocationMaster lm :locations) {
			 double distance = distance(lm.getLatitude(),lm.getLongitude(), lat, lon);
			 if( distance <= distanceThresh ) {
				 if(lm.getType() != null &&  lm.getType().equalsIgnoreCase(_STORETYPE)) {
					 storeLocations.put(lm.getLocationNumber(), new Location(lm,distance));
				 }
				 if(lm.getPickup() == 0 && lm.getType() != null && lm.getType().equalsIgnoreCase(_WHTYPE)) {
					 whLocations.put(lm.getLocationNumber(), new Location(lm,distance));
				 }
			 }
		 }
		 
		 logger.debug("Number of store locations :{}", storeLocations.size());
		 logger.debug("Number of warehouse locations :{}", whLocations.size());
		 
		 return new LocationDTO(storeLocations, whLocations);
	}
	
	public void loadLocations() throws InternalServerError {
		loadLocations(false);
	}
	
	public void loadLocations(Boolean reload) throws InternalServerError {
		if(locations == null || reload ) {
			locations = new HashSet<LocationMaster>();
			 Iterator<Item> records;
			try {
				records = getAllRecords(locationTable);
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new InternalServerError("error scaning:" + locationTable);
			}
			 if(records != null) {
		         while (records.hasNext()) {
		             Item item = records.next();
		             locations.add(new LocationMaster(item.getDouble(_LONGITUDE),item.getDouble(_LATITUDE), item.getString(_TYPE),item.getString(_LOCATIONNUMBER), item.getDouble(_PICKUP), item.getString(_ZIP)));
		         }
			 }
		}
	}
	
	private static double distance(double lat1, double lon1, double lat2, double lon2) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			return (dist);
		}
	}
	
	
	public InventoryAvailableResponse getInventoryLambda(InventoryAvailableRequest reqInv) throws JsonMappingException, JsonProcessingException {
		LambdaRequest req = new LambdaRequest();
		req.setBody(mapper.writeValueAsString(reqInv));
		req.setMethod("POST");
		req.setPath("/inventory/quantity");
		LambdaResponse resMod = invokeLambda(InventoryFunction,  req);
		logger.debug("getInventoryLambda result {}.", resMod);
		InventoryAvailableResponse inventoryRequest = null;
	    if (resMod != null) {
	    	//LambdaResponse resMod  = mapper.readValue(result, LambdaResponse.class);
	    	inventoryRequest = mapper.readValue(resMod.getBody(), InventoryAvailableResponse.class);
	    }
	    return inventoryRequest;
	}

	
	public  DeliveryDateResponse getDDambda(DeliveryDateRequest reqDD) throws JsonMappingException, JsonProcessingException {
		LambdaRequest req = new LambdaRequest();
		req.setBody(mapper.writeValueAsString(reqDD));
		req.setMethod("POST");
		req.setPath("/inventory/deliverydate");
		LambdaResponse resMod  = invokeLambda(ddFunction,  req);
		logger.debug("getDDambda result {}.", resMod);
		DeliveryDateResponse res = null;
	    if (resMod != null) {
	    	//LambdaResponse resMod  = mapper.readValue(result, LambdaResponse.class);
	    	res = mapper.readValue(resMod.getBody(), DeliveryDateResponse.class);
	    }
	    return res;
	}
	
	public static class LocationMaster {
		/**
		 * @param longitude
		 * @param latitude
		 * @param type
		 * @param locationNumber
		 * @param pickup
		 * @param zip
		 */
		public LocationMaster(Double longitude, Double latitude, String type, String locationNumber, Double pickup,
				String zip) {
			super();
			this.longitude = longitude;
			this.latitude = latitude;
			this.type = type;
			this.locationNumber = String.format("%1$" + 3 + "s", locationNumber).replace(' ', '0'); ;
			this.pickup = pickup;
			this.zip = zip;
		}


		/**
		 * 
		 */
		public LocationMaster() {
			super();
		}
		/**
		 * @param longitude
		 * @param latitude
		 * @param type
		 */
		
		private Double longitude;
		private Double latitude;
		private String type;
		private String locationNumber;
		private Double pickup;
		private String zip;
		
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLocationNumber() {
			return locationNumber;
		}
		public void setLocationNumber(String locationNumber) {
			this.locationNumber = locationNumber;
		}

		public Double getPickup() {
			return pickup;
		}

		public void setPickup(Double pickup) {
			this.pickup = pickup;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}
	}
}