package com.conns.lambda.api.atp.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LocationDTO {

	/**
	 * @param storeLocations
	 * @param whLocations
	 */
	public LocationDTO(HashMap<String, Location> storeLocations, HashMap<String, Location> whLocations) {
		super();
		this.storeLocations = storeLocations;
		this.whLocations = whLocations;
	}

	private HashMap<String, Location> storeLocations;
	private HashMap<String, Location> whLocations;
	
	

	
	public String getDLLocation() {
		if(whLocations!= null && whLocations.values() != null && whLocations.values().size() > 0) {
	        // Getting Collection of values from HashMap
	        Collection<Location> values = whLocations.values();
	        // Creating an ArrayList of values
	        ArrayList<Location> listOfValues
	            = new ArrayList<>(values);
	        
			Collections.sort(listOfValues);
			Location location = listOfValues.get(0); //get closet location
			return location.getLocationNumber();
		} else {
			return "";
		}
	}
	
	public List<String> getPULocations() {
		if(storeLocations != null && storeLocations.values().size() > 0) {
	        Collection<Location> values = storeLocations.values();
	        // Creating an ArrayList of values
	        ArrayList<Location> listOfValues
	            = new ArrayList<>(values);
	        
			Collections.sort(listOfValues);
			List<String> puLocations = new ArrayList<String>();
			for(Location l: listOfValues) {
				puLocations.add(l.getLocationNumber());
			}
			return puLocations;
		} else {
			return null;
		}
	}

	public HashMap<String, Location> getStoreLocations() {
		return storeLocations;
	}

	public void setStoreLocations(HashMap<String, Location> storeLocations) {
		this.storeLocations = storeLocations;
	}

	public HashMap<String, Location> getWhLocations() {
		return whLocations;
	}

	public void setWhLocations(HashMap<String, Location> whLocations) {
		this.whLocations = whLocations;
	}

}
