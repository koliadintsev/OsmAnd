package com.operasoft.snowboard.database;

public class Company extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String GALLONS = "Gallons";
	public static final String LITRES = "Litres";
	public static final String KILOMETERS = "km";
	public static final String MILES = "mi";
	public static final String KILOMETERS_PER_HOUR = "km/h";
	public static final String MILES_PER_HOUR = "mph";
	public static final String CELSIUS = "°C";
	public static final String FARENHEIT = "°F";
	public static final String SIMPLICITY = "5226cb45-1e74-4046-b192-193d292ed4a8";
	

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "status_code_id")
	private String status;

	@Column(name = "language")
	private String language;

	@Column(name = "default_season_id")
	private String defaultSeasonId;

	@Column(name = "miles")
	private int imperialUnits;

	@Column(name = "blocked")
	private String blocked;

	@Column(name = "business_type_id")
	private String businessTypeId;
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setDefaultSeasonId(String defaultSeasonId) {
		this.defaultSeasonId = defaultSeasonId;
	}

	public String getDefaultSeasonId() {
		return defaultSeasonId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public static final int IMPERIAL_UNIT = 1;
	public static final int METRIC_UNIT = -1;

	public boolean isImperialUnits() {
		return (imperialUnits == 1);
	}

	public boolean isMetricUnits() {
		return (imperialUnits == -1);
	}

	public int getImperialUnits() {
		return imperialUnits;
	}

	public void setImperialUnits(int imperialUnits) {
		this.imperialUnits = imperialUnits;
	}

	public String getDistanceUnit() {
		if (isImperialUnits()) {
			return MILES;
		}

		return KILOMETERS;
	}

	public String getVolumeUnit() {
		if (isImperialUnits()) {
			return GALLONS;
		}

		return LITRES;
	}

	public String getSpeedUnit() {
		if (isImperialUnits()) {
			return MILES_PER_HOUR;
		}

		return KILOMETERS_PER_HOUR;
	}

	public String getTemperatureUnit() {
		if (isImperialUnits()) {
			return FARENHEIT;
		}

		return CELSIUS;
	}

	public String isBlocked() {
		return blocked;
	}

	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}

	public String getBusinessTypeId() {
		return businessTypeId;
	}

	public void setBusinessTypeId(String businessTypeId) {
		this.businessTypeId = businessTypeId;
	}

	public boolean isSimplicity(){
		return businessTypeId.equals(SIMPLICITY);
	}
}
