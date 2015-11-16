package com.operasoft.snowboard.database;

public class Site extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="company_id")
	private String companyId;
	
	@Column(name="name")
	private String name;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}		
}
