package com.operasoft.snowboard.database;

public class VehicleInspectionItem extends Dto {

	static final private String PRE_DEPARTURE_TYPE 	= "Pre-Departure";
	static final private String END_OF_DAY_TYPE 	= "End of Day";
	static final private String BOTH_TYPE			= "Both";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="company_id")
	private String companyId;

	@Column(name="name")
	private String name;

	@Column(name="type")
	private String type;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPreDepartureItem() {
		if (type != null) {
			if (type.equalsIgnoreCase(PRE_DEPARTURE_TYPE)) {
				return true;
			}
			if (type.equalsIgnoreCase(BOTH_TYPE)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEndOfDayItem() {
		if (type != null) {
			if (type.equalsIgnoreCase(END_OF_DAY_TYPE)) {
				return true;
			}
			if (type.equalsIgnoreCase(BOTH_TYPE)) {
				return true;
			}
		}
		return false;
	}
	
}
