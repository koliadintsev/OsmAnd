package com.operasoft.snowboard.database;

public class WorksheetMaintenanceProducts extends Dto {

	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String company_id;

	@Column(name = "worksheet_maintenance_id")
	private String worksheet_maintenance_id;

	@Column(name = "code")
	private String code;

	@Column(name = "product_name")
	private String name;

	@Column(name = "quantity")
	private String quantity;

	@Column(name = "unit_of_measure")
	private String unitOfMeasure;

	@Column(name = "product_id")
	private String productId;

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getWorksheet_maintenance_id() {
		return worksheet_maintenance_id;
	}

	public void setWorksheet_maintenance_id(String worksheet_maintenance_id) {
		this.worksheet_maintenance_id = worksheet_maintenance_id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
