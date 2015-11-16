package com.operasoft.snowboard.database;

public class WorksheetEmployeeLogs extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="worksheet_id")
	private String worksheet_id;
	
	@Column(name="company_id")
	private String company_id;

	@Column(name="emp_id")
	private String emp_id;

	@Column(name="emp_name")
	private String emp_name;

	@Column(name="punch_in")
	private String punch_in;

	@Column(name="punch_out")
	private String punch_out;
	
	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getWorksheet_id() {
		return worksheet_id;
	}

	public void setWorksheet_id(String worksheet_id) {
		this.worksheet_id = worksheet_id;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}

	public String getPunch_in() {
		return punch_in;
	}

	public void setPunch_in(String punch_in) {
		this.punch_in = punch_in;
	}

	public String getPunch_out() {
		return punch_out;
	}

	public void setPunch_out(String punch_out) {
		this.punch_out = punch_out;
	}
}
