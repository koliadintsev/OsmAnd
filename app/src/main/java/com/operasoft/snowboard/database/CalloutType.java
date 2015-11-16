package com.operasoft.snowboard.database;

public class CalloutType extends Dto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CalloutType() {
	}

	public CalloutType(String name, String companyId) {
		this.name = name;
		this.companyId = companyId;
	}

	@Column(name = "name")
	private String name;

	@Column(name = "company_id")
	private String companyId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
}
