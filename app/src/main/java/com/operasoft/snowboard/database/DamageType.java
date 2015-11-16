package com.operasoft.snowboard.database;

public class DamageType extends Dto {

	/**
         * 
         */
	private static final long serialVersionUID = 1L;

	@Column(name="name")
	private String name;

	@Column(name="company_id")
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