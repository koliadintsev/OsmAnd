package com.operasoft.snowboard.database;

public class Season extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;

	@Column(name = "start_date")
	private String startDate;

	@Column(name = "end_date")
	private String endDate;

	@Column(name = "status_code_id")
	private String statusCodeId;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "contract_prefix")
	private String contractPrefix;

	@Column(name = "season_code")
	private String seasonCode;

	@Column(name = "english_contract_description")
	private String englishContractDescription;

	@Column(name = "french_contract_description")
	private String frenchContractDescription;

	@Column(name = "default")
	private String defaultVal;

	@Column(name = "import_id")
	private String importId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStatusCodeId() {
		return statusCodeId;
	}

	public void setStatusCodeId(String statusCodeId) {
		this.statusCodeId = statusCodeId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getContractPrefix() {
		return contractPrefix;
	}

	public void setContractPrefix(String contractPrefix) {
		this.contractPrefix = contractPrefix;
	}

	public String getSeasonCode() {
		return seasonCode;
	}

	public void setSeasonCode(String seasonCode) {
		this.seasonCode = seasonCode;
	}

	public String getEnglishContractDescription() {
		return englishContractDescription;
	}

	public void setEnglishContractDescription(String englishContractDescription) {
		this.englishContractDescription = englishContractDescription;
	}

	public String getFrenchContractDescription() {
		return frenchContractDescription;
	}

	public void setFrenchContractDescription(String frenchContractDescription) {
		this.frenchContractDescription = frenchContractDescription;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

}
