package com.operasoft.snowboard.database;

public class Contact extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="company_id")
	private String company_id;
	
	@Column(name="CompanyName")
	private String CompanyName;
	
	@Column(name="Notes")
	private String Notes;
	
	@Column(name="FirstName")
	private String FirstName;
	
	@Column(name="FullName")
	private String FullName;
	
	@Column(name="LastName")
	private String LastName;
	
	@Column(name="Telephone1")
	private String Telephone1;
	
	@Column(name="Telephone2")
	private String Telephone2;

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getTelephone1() {
		return Telephone1;
	}

	public void setTelephone1(String telephone1) {
		Telephone1 = telephone1;
	}

	public String getTelephone2() {
		return Telephone2;
	}

	public void setTelephone2(String telephone2) {
		Telephone2 = telephone2;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

}
