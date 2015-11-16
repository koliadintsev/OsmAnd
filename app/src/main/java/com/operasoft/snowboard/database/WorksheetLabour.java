package com.operasoft.snowboard.database;

public class WorksheetLabour extends Dto implements WorksheetCompanyable {
	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "worksheet_id")
	private String worksheetId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "date")
	private String labourDate;

	@Column(name = "hours")
	private float hours;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getLabourDate() {
		return labourDate;
	}

	public void setLabourDate(String labourDate) {
		this.labourDate = labourDate;
	}

	public float getHours() {
		return hours;
	}

	public void setHours(float hours) {
		this.hours = hours;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

}
