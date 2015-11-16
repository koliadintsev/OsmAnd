package com.operasoft.snowboard.database;

public class VehicleLastReport extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="company_id")
	private String companyId;
	
	@Column(name="location")
	private String location;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
