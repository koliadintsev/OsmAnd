package com.operasoft.snowboard.database;

import java.util.List;

public class Contract extends Dto {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	@Column(name = "company_id")
	private String					company_id;

	@Column(name = "service_location_id")
	private String					service_location_id;

	@Column(name = "date_to")
	private String					date_to_date;

	@Column(name = "status_code_id")
	private String					status_code_id;

	@Column(name = "season_id")
	private String					season_id;

	@Column(name = "latitude")
	private double					latitude;

	@Column(name = "longitude")
	private double					longitude;

	@Column(name = "contract_number ")
	private String					contract_number;

	@Column(name = "contract_name ")
	private String					contract_name;
	
	@Column(name = "job_number")
	private String					jobNumber;
	
	@Column(name = "division_id")
	private String					divisionId;

	private List<ContractServices>	services			= null;

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getService_location_id() {
		return service_location_id;
	}

	public void setService_location_id(String service_location_id) {
		this.service_location_id = service_location_id;
	}

	public String getDate_to_date() {
		return date_to_date;
	}

	public void setDate_to_date(String date_to_date) {
		this.date_to_date = date_to_date;
	}

	public String getStatus_code_id() {
		return status_code_id;
	}

	public void setStatus_code_id(String status_code_id) {
		this.status_code_id = status_code_id;
	}

	public String getSeason_id() {
		return season_id;
	}

	public void setSeason_id(String season_id) {
		this.season_id = season_id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getContract_number() {
		return contract_number;
	}

	public void setContract_number(String contract_number) {
		this.contract_number = contract_number;
	}

	public String getContract_name() {
		return contract_name;
	}

	public void setContract_name(String contract_name) {
		this.contract_name = contract_name;
	}

	/**
	 * List the Contract Services available for this contract
	 */
	public List<ContractServices> listServices() {
		// We use a late binding approach for this API to only load DTOs when
		// needed.
		if (services == null) {
			ContractServicesDao dao = new ContractServicesDao();
			services = dao.listAllForContractId(id);
		}
		return services;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}
}
