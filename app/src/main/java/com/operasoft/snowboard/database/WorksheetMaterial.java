package com.operasoft.snowboard.database;

public class WorksheetMaterial extends Dto implements WorksheetCompanyable {
	private static final long serialVersionUID = 1L;

	@Column(name = "worksheet_id")
	private String worksheetId;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "date")
	private String materialDate;

	@Column(name = "quantity")
	private float quantity;

	@Column(name = "unit_of_measure_id")
	private String unitOfMesureId;

	@Column(name = "creator_id")
	private String creatorId;

	public String getCompanyId() {
		return companyId;
	}

	@Override
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getWorksheetId() {
		return worksheetId;
	}

	@Override
	public void setWorksheetId(String worksheetId) {
		this.worksheetId = worksheetId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getMaterialDate() {
		return materialDate;
	}

	public void setMaterialDate(String materialDate) {
		this.materialDate = materialDate;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public String getUnitOfMesureId() {
		return unitOfMesureId;
	}

	public void setUnitOfMesureId(String unitOfMesureId) {
		this.unitOfMesureId = unitOfMesureId;
	}

}
