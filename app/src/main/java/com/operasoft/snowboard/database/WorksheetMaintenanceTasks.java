package com.operasoft.snowboard.database;

public class WorksheetMaintenanceTasks extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "worksheet_maintenance_id")
	private String worksheet_maintenance_id;

	@Column(name = "task_code")
	private String Taskcode;

	@Column(name = "task_name")
	private String Taskname;

	@Column(name = "duration")
	private String duration;

	@Column(name = "product_id")
	private String product_id;

	@Column(name = "contract_service_id")
	private String contract_service_id;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getWorksheet_maintenance_id() {
		return worksheet_maintenance_id;
	}

	public void setWorksheet_maintenance_id(String worksheet_maintenance_id) {
		this.worksheet_maintenance_id = worksheet_maintenance_id;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getTaskname() {
		return Taskname;
	}

	public void setTaskname(String taskname) {
		Taskname = taskname;
	}

	public String getTaskcode() {
		return Taskcode;
	}

	public void setTaskcode(String taskcode) {
		Taskcode = taskcode;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getContract_service_id() {
		return contract_service_id;
	}

	public void setContract_service_id(String contract_service_id) {
		this.contract_service_id = contract_service_id;
	}
}
