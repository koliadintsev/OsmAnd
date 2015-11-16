package com.operasoft.snowboard.database;

public class VehicleRefuelLog extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String vehicleId;
	private String userId;
	private String date;
	private double volume;
	private double amount;
	private String volumeUnit;
	private double engineHours;
	private double latitude;
	private double longitude;
	private String odometer;

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicle_id) {
		this.vehicleId = vehicle_id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String user_id) {
		this.userId = user_id;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(String volume_unit) {
		this.volumeUnit = volume_unit;
	}

	public double getEngineHours() {
		return engineHours;
	}

	public void setEngineHours(double engine_hours) {
		this.engineHours = engine_hours;
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

	public String getOdometer() {
		return odometer;
	}

	public void setOdometer(String odometer) {
		this.odometer = odometer;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
