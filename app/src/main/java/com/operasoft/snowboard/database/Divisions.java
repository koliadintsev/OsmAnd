package com.operasoft.snowboard.database;

public class Divisions extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String MAINTENANCE = "Maintenance";
	public static final String CONSTRUCTION = "Construction";

	@Column(name = "name")
	private String Name;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "type")
	private String type;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
