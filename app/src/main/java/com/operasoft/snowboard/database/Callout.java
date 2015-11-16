package com.operasoft.snowboard.database;

import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.util.Session;

/**
 * This DTO carries the information related to a Callout event created by the driver
 * that must be pushed towards the dispatcher user on Snowman.
 * @author Christian
 *
 */
public class Callout extends Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PENDING = "212a7a5e-3078-11e2-943f-002421b54c78";
	public static final String COMPLETE = "212c6862-3078-11e2-943f-002421b54c78";

	public Callout() {
	}
	
	public Callout(String serviceLocationId, String userId) {
		this.serviceLocationId = serviceLocationId;
		status = PENDING;
		dateTime = CommonUtils.UtcDateNow();
		this.userId = userId;
		this.companyId = Session.getCompanyId();
	}
	
	@Column(name="service_location_id")
	private String serviceLocationId;

	@Column(name="date_time")
	private String dateTime;
	
	@Column(name="user_id")
	private String userId;

	@Column(name="status_code_id")
	private String status;

	@Column(name="company_id")
	private String companyId;
	
	@Column(name="callout_type_id")
	private String callOutTypeId;

	public String getServiceLocationId() {
		return serviceLocationId;
	}

	public void setServiceLocationId(String serviceLocationId) {
		this.serviceLocationId = serviceLocationId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String statusCodeId) {
		this.status = statusCodeId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCallOutTypeId() {
		return callOutTypeId;
	}

	public void setCallOutTypeId(String callOutTypeId) {
		this.callOutTypeId = callOutTypeId;
	}

}
