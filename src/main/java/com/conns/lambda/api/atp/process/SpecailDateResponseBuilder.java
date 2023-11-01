package com.conns.lambda.api.atp.process;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.conns.lambda.api.atp.response.ATPDetailsResponse;
import com.conns.lambda.api.atp.response.AvailableToPromiseResponse;
import com.conns.lambda.api.atp.response.DeliveryATPResponse;
import com.conns.lambda.api.atp.response.PickupATPResponse;
import com.conns.lambda.common.exception.InvalidRequestException;
import com.conns.lambda.common.http.ResponseBody;
import com.conns.lambda.common.http.ResponseErrorBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SpecailDateResponseBuilder {


	private static final String _SPECIALDATECONFIG = System.getenv("SPECIALDATECONFIG") != null
			? System.getenv("SPECIALDATECONFIG").trim()
			: "{}"; 
	private static final Logger logger = LogManager.getLogger(SpecailDateResponseBuilder.class);

	public AvailableToPromiseResponse buildResponseObject(AvailableToPromiseResponse atpResponse) {
			
			try {
				String adjustedPickupDate = getAdjustedDateInCST();
				
				for (PickupATPResponse pickupAtp : atpResponse.getPickupAtp()) {
					pickupAtp.setAtpDate(adjustedPickupDate);
				}
				
			} catch (Exception e) {
				logger.debug("Error in applying adjustedPickupDate, Igniring the adjustment!!! Provided config:"+_SPECIALDATECONFIG);
				e.printStackTrace();
			} 
		


		return atpResponse;
	}
	
	//added for https://conns.atlassian.net/browse/CIW-16812
	private String getAdjustedDateInCST() throws JsonMappingException, JsonProcessingException, ParseException {
		Date currentDate = new Date();
		ObjectMapper mapper = new ObjectMapper();
		List<DateConfig> dateConfigs = mapper.readValue(_SPECIALDATECONFIG, new TypeReference<List<DateConfig>>(){});
		for(DateConfig dateConfig: dateConfigs) {
			Date startDate = parse(dateConfig.getStartDate());
			Date endTime = parse(dateConfig.getEndDate());
			Integer numberDays = dateConfig.getPikupDaysOffset();
			if(startDate != null &&  endTime != null &&  numberDays > 0) {
				if(currentDate.after(startDate) && currentDate.before(endTime) ) {
					currentDate = addDays(currentDate, numberDays);
				}
			}
		}
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone obj = TimeZone.getTimeZone("CST");
		formatter.setTimeZone(obj);
		String today = formatter.format(currentDate);
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
	
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DateConfig {
		
		@JsonProperty("pikup_days_offset")
	    public Integer pikupDaysOffset;
		
		@JsonProperty("name")
	    public String name;
		
		@JsonProperty("start_date")
	    public String startDate;
		
		@JsonProperty("end_date")
	    public String endDate;

		public Integer getPikupDaysOffset() {
			return pikupDaysOffset;
		}

		public void setPikupDaysOffset(Integer pikupDaysOffset) {
			this.pikupDaysOffset = pikupDaysOffset;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		
	}
	
	
	
	

}