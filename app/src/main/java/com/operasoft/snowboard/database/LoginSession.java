package com.operasoft.snowboard.database;

public class LoginSession extends Dto {

	static final public String START_STATUS = "start";
	static final public String END_STATUS = "end";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String imei;
	private String start_datetime;
	private String end_datetime;
	private String userId;
	private String vehicleId;
	private String session_status;
	private double latitude;
	private double longitude;

	public String getImei() {
		return imei;
	}

	public void setImei(String imei_companies_id) {
		this.imei = imei_companies_id;
	}

	public String getStart_datetime() {
		return start_datetime;
	}

	public void setStart_datetime(String start_datetime) {
		this.start_datetime = start_datetime;
	}

	public String getEnd_datetime() {
		return end_datetime;
	}

	public void setEnd_datetime(String end_datetime) {
		this.end_datetime = end_datetime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String user_id) {
		this.userId = user_id;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicle_id) {
		this.vehicleId = vehicle_id;
	}

	public String getSession_status() {
		if (session_status == null)
			return END_STATUS;

		return session_status;
	}

	public void setSession_status(String session_status) {
		this.session_status = session_status;
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

	public boolean isOpened() {
		return end_datetime == null;
	}
}
