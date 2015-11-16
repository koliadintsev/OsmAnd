package com.operasoft.snowboard.database;

public class EndRoute extends Dto {

	/**
	 * 
	 */
	@Column(name = "driver_name")
	private String driverName;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "route_id")
	private String routeId;

	@Column(name = "date_time")
	private String dateTime;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "time_spent")
	private int timeSpent;
	
	@Column(name = "vehicle_id")
	private String vehicleId;
	
	@Column(name = "route_selection_id")
	private String route_selection_id;
	
	@Column(name = "user_id")
	private String user_id;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getRoute_selection_id() {
		return route_selection_id;
	}

	public void setRoute_selection_id(String route_selection_id) {
		this.route_selection_id = route_selection_id;
	}

	private static final long serialVersionUID = 1L;

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public int getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(int timeSpent) {
		this.timeSpent = timeSpent;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	transient public Vehicle vehicle;
	transient public Company company;
}
