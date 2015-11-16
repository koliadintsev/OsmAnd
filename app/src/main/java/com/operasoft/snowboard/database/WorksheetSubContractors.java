package com.operasoft.snowboard.database;

public class WorksheetSubContractors extends Dto {

	private static final long	serialVersionUID	= 1L;

	@Column(name = "company_id")
	private String				company_id;

	@Column(name = "worksheet_id")
	private String				worksheet_id;

	@Column(name = "sub_contractor")
	private String				sub_contractor;

	@Column(name = "work_performence")
	private String				work_performence;

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

	public String getWork_performence() {
		return work_performence;
	}

	public void setWork_performence(String work_performence) {
		this.work_performence = work_performence;
	}

	public String getSub_contractor() {
		return sub_contractor;
	}

	public void setSub_contractor(String sub_contractor) {
		this.sub_contractor = sub_contractor;
	}

}
