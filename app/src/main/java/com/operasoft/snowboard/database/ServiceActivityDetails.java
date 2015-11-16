package com.operasoft.snowboard.database;

public class ServiceActivityDetails extends Dto {

	/**
	 * Narendra Singh
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String company_id;
	
	@Column(name="service_activity_id")
	private String service_activity_id;
	
	@Column(name="accumulation_depth")
	private int accumulation_depth;
	
	@Column(name="precipitation")
	private String precipitation;
	
	@Column(name="conditions")
	private String conditions;
	
	@Column(name="outdoor_temp")
	private String outdoor_temp;
	
	@Column(name="plowing_roads")
	private String plowing_roads;
	
	@Column(name="deice_roads")
	private String deice_roads;
	
	@Column(name="plowing_walkways")
	private String plowing_walkways;
	
	@Column(name="deice_walkways")
	private String deice_walkways;
	
	@Column(name="time_on_site")
	private double time_on_site;
	
	@Column(name="inspection_only")
	private int inspection_only;
	
	@Column(name="notes")
	private String notes;
	
	@Column(name="accumulation_type")
	private String accumulation_type;

	
	public String getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getOutdoor_temp() {
		return outdoor_temp;
	}

	public void setOutdoor_temp(String outdoor_temp) {
		this.outdoor_temp = outdoor_temp;
	}

	public String getPlowing_roads() {
		return plowing_roads;
	}

	public void setPlowing_roads(String plowing_roads) {
		this.plowing_roads = plowing_roads;
	}

	public String getPlowing_walkways() {
		return plowing_walkways;
	}

	public void setPlowing_walkways(String plowing_walkways) {
		this.plowing_walkways = plowing_walkways;
	}

	public String getDeice_walkways() {
		return deice_walkways;
	}

	public void setDeice_walkways(String deice_walkways) {
		this.deice_walkways = deice_walkways;
	}

	public double getTime_on_site() {
		return time_on_site;
	}

	public void setTime_on_site(double time_on_site) {
		this.time_on_site = time_on_site;
	}

	public int getInspection_only() {
		return inspection_only;
	}

	public void setInspection_only(int inspection_only) {
		this.inspection_only = inspection_only;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAccumulation_type() {
		return accumulation_type;
	}

	public void setAccumulation_type(String accumulation_type) {
		this.accumulation_type = accumulation_type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setService_activity_id(String service_activity_id) {
		this.service_activity_id = service_activity_id;
	}

	public String getService_activity_id() {
		return service_activity_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setAccumulation_depth(int accumulation_depth) {
		this.accumulation_depth = accumulation_depth;
	}

	public int getAccumulation_depth() {
		return accumulation_depth;
	}

	public void setDeice_roads(String deice_roads) {
		this.deice_roads = deice_roads;
	}

	public String getDeice_roads() {
		return deice_roads;
	}
}