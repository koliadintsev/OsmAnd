package com.operasoft.snowboard.database;

public class UserWorkStatusLogs extends Dto {

	private static final long serialVersionUID = 1L;

	public static final String PUNCH_IN = "punchIn";
	public static final String PUNCH_OUT = "punchOut";
	public static final String PICK_UP = "pickUp";
	public static final String DROP_OFF = "dropOff";

	@Column(name = "user_id")
	private String userId;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "operation")
	private String operation;

	@Column(name = "work_status")
	private String workStatus;

	@Column(name = "imei")
	private String imei;

	@Column(name = "date_time")
	private String dateTime;

	@Column(name = "vehicle_id")
	private String vehicleId;

	@Column(name = "service_location_id")
	private String serviceLocationId;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "longitude")
	private double longitude;

	@Column(name = "accepted")
	private int accepted;

	@Column(name = "creator_id")
	private String creatorId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
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

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getServiceLocationId() {
		return serviceLocationId;
	}

	public void setServiceLocationId(String serviceLocationId) {
		this.serviceLocationId = serviceLocationId;
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

	public int getAccepted() {
		return accepted;
	}

	public void setAccepted(int accepted) {
		this.accepted = accepted;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

}