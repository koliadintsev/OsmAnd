package com.operasoft.snowboard.database;

public class RouteSelected extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "route_id")
	private String routeId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "date_time")
	private String dateTime;

	@Column(name = "vehicle_id")
	private String vehicleId;

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

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
}
