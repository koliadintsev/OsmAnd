package com.operasoft.snowboard.database;

/**
 * This is the DTO for the marker_installations model in Snowman
 * 
 * @author Christian
 */
public class MarkerInstallation extends Dto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final static String INSTALLED_CASE = "e5162310-060f-11e2-8f45-0025900e9333";
	public final static String OPEN_PENDING_CASE = "e51611ea-060f-11e2-8f45-0025900e9333";

	@Column(name="company_id")
	private String companyId;
	
	@Column(name="contract_id")
	private String contractId;

	@Column(name="status_id")
	private String status;
	
	@Column(name="user_id")
	private String userId;

	@Column(name="date_time")
	private String dateTime;

	@Column(name="comments")
	private String comments;

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public boolean isInstalled() {
		return status.equals(INSTALLED_CASE);
	}
}
