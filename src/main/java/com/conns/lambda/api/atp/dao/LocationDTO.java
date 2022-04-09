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
		if(whLocations!= null && whLocations.values() != null && whLocations.values().size() > 0) {
	        // Getting Collection of values from HashMap
	        Collection<Location> values = whLocations.values();
	        // Creating an ArrayList of values
	        ArrayList<Location> listOfValues
	            = new ArrayList<>(values);
	        
			Collections.sort(listOfValues);
			closestWHLocation = listOfValues.get(0); //get closet location
		} else {
			closestWHLocation = null;
		}
	}

	private HashMap<String, Location> storeLocations;
	private HashMap<String, Location> whLocations;
	private Location closestWHLocation;
	
	

	
	public String getDLLocation() {
		if(closestWHLocation != null) {
			return closestWHLocation.getLocationNumber();
		} else {
			return "";
		}
	}
	
	public String getDLZip() {
		if(closestWHLocation != null) {
			return closestWHLocation.getZip();
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
			//return new ArrayList<String>();
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

	public Location getClosestWHLocation() {
		return closestWHLocation;
	}

	public void setClosestWHLocation(Location closestWHLocation) {
		this.closestWHLocation = closestWHLocation;
	}

}
