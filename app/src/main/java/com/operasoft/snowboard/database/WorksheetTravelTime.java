package com.operasoft.snowboard.database;

public class WorksheetTravelTime extends Dto implements WorksheetCompanyable {
	private static final long serialVersionUID = 1L;

	@Column(name = "worksheet_id")
	private String worksheetId;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "date")
	private String travelDate;

	@Column(name = "hours")
	private int hours;

	@Column(name = "creator_id")
	private String creatorId;

	public String getCompanyId() {
		return companyId;
	}

	@Override
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getWorksheetId() {
		return worksheetId;
	}

	@Override
	public void setWorksheetId(String worksheetId) {
		this.worksheetId = worksheetId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

}
