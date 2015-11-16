package com.operasoft.snowboard.database;

public class Punch extends Dto {

	static final public String IN = "In";
	static final public String OUT = "Out";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String imei;
	private String dateTime;
	private String userId;
	private String vehicleId;
	private double latitude;
	private double longitude;
	private String operation;
	private String serviceLocationId;

	public String getServiceLocationId() {
		return serviceLocationId;
	}

	public void setServiceLocationId(String serviceLocationId) {
		this.serviceLocationId = serviceLocationId;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}