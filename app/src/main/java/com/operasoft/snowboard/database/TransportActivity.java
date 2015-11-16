package com.operasoft.snowboard.database;

public class TransportActivity extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="company_id")
	private String companyId;

	@Column(name="start_datetime")
	private String startDateTime;

	@Column(name="start_latitude")
	private double startLatitude;

	@Column(name="start_longitude")
	private double startLongitude;

	@Column(name="start_user_id")
	private String startUserId;

	@Column(name="geofence_id")
	private String geofenceId;

	@Column(name="vehicle_id")
	private String vehicleId;

	@Column(name="transport_vehicle_id")
	private String transportVehicleId;

	@Column(name="site_id")
	private String siteId;

	@Column(name="end_datetime")
	private String endDateTime;

	@Column(name="end_latitude")
	private double endLatitude;

	@Column(name="end_longitude")
	private double endLongitude;

	@Column(name="end_user_id")
	private String endUserId;

	@Column(name="status_code_id")
	private String status;

	@Column(name="deleted")
	private int deleted;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public double getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(double startLatitude) {
		this.startLatitude = startLatitude;
	}

	public double getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(double startLongitude) {
		this.startLongitude = startLongitude;
	}

	public String getStartUserId() {
		return startUserId;
	}

	public void setStartUserId(String startUserId) {
		this.startUserId = startUserId;
	}

	public String getGeofenceId() {
		return geofenceId;
	}

	public void setGeofenceId(String geofenceId) {
		this.geofenceId = geofenceId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getTransportVehicleId() {
		return transportVehicleId;
	}

	public void setTransportVehicleId(String transportVehicleId) {
		this.transportVehicleId = transportVehicleId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public double getEndLatitude() {
		return endLatitude;
	}

	public void setEndLatitude(double endLatitude) {
		this.endLatitude = endLatitude;
	}

	public double getEndLongitude() {
		return endLongitude;
	}

	public void setEndLongitude(double endLongitude) {
		this.endLongitude = endLongitude;
	}

	public String getEndUserId() {
		return endUserId;
	}

	public void setEndUserId(String endUserId) {
		this.endUserId = endUserId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}	
}
