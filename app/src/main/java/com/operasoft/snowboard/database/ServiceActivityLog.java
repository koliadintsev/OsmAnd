package com.operasoft.snowboard.database;

import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.util.Session;

public class ServiceActivityLog extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "service_activity_id")
	private String serviceActivityId;

	@Column(name = "status_code_id")
	private String status;

	@Column(name = "contact_id")
	private String contactId;

	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "vehicle_id")
	private String vehicleId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "date_time")
	private String dateTime;

	@Column(name = "gps_coordinates")
	private String latLong;

	public ServiceActivityLog() {

	}

	public ServiceActivityLog(ServiceActivity sa) {
		this.serviceActivityId = sa.getId();
		this.companyId = sa.getCompanyId();
		this.status = sa.getStatus();
		this.contractId = sa.getContractId();
		this.dateTime = CommonUtils.UtcDateNow();
		if (Session.getDriver() != null) {
			this.userId = Session.getDriver().getId();
		} else {
			this.userId = sa.getUserId();
		}
		if (Session.getVehicle() != null) {
			this.vehicleId = Session.getVehicle().getId();
		} else {
			this.vehicleId = sa.getVehicleId();
		}
		if (Session.clocation != null) {
			latLong = Session.clocation.getLatitude() + " " + Session.clocation.getLongitude();
		}
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getServiceActivityId() {
		return serviceActivityId;
	}

	public void setServiceActivityId(String serviceActivityId) {
		this.serviceActivityId = serviceActivityId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getLatLong() {
		return latLong;
	}

	public void setLatLong(String latLong) {
		this.latLong = latLong;
	}
}
