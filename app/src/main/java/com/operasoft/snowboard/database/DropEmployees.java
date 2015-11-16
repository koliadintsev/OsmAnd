package com.operasoft.snowboard.database;

public class DropEmployees extends Dto {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Column(name = "employee_id")
	private String				employeeId;

	@Column(name = "company_id")
	private String				companyId;

	@Column(name = "service_location_id")
	private String				serviceLocationId;

	@Column(name = "drop_time")
	private String				dropTime;

	@Column(name = "pick_time")
	private String				pickTime;

	@Column(name = "operation")
	private String				operation;

	@Column(name = "contract_id")
	private String				contractId;
	
	@Column(name = "worksheet_maintenance_id")
	private String				worksheetMaintenanceId;

	@Column(name = "latitude")
	private String				latitude;

	@Column(name = "longitude")
	private String				longitude;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getServiceLocationId() {
		return serviceLocationId;
	}

	public void setServiceLocationId(String serviceLocationId) {
		this.serviceLocationId = serviceLocationId;
	}

	public String getDropTime() {
		return dropTime;
	}

	public void setDropTime(String dropTime) {
		this.dropTime = dropTime;
	}

	public String getPickTime() {
		return pickTime;
	}

	public void setPickTime(String pickTime) {
		this.pickTime = pickTime;
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

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getWorksheetMaintenanceId() {
		return worksheetMaintenanceId;
	}

	public void setWorksheetMaintenanceId(String worksheetMaintenanceId) {
		this.worksheetMaintenanceId = worksheetMaintenanceId;
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
}
