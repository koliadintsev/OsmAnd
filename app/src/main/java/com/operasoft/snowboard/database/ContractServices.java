package com.operasoft.snowboard.database;

public class ContractServices extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="company_id")
	private String companyId;

	@Column(name="product_id")
	private String productId;

	@Column(name="product_name")
	private String productName;

	@Column(name="uom_name")
	private String unitOfMeasure;

	@Column(name="product_qty")
	private float quantity;

	@Column(name="contract_id")
	private String contractId;

	@Column(name="route_id")
	private String routeId;

	@Column(name="description")
	private String description;

	@Column(name="trigger_level")
	private int triggerLevel;
	
	private Products product;
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String company_id) {
		this.companyId = company_id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String product_id) {
		this.productId = product_id;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contract_id) {
		this.contractId = contract_id;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String route_id) {
		this.routeId = route_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTriggerLevel() {
		return triggerLevel;
	}

	public void setTriggerLevel(int trigger_level) {
		this.triggerLevel = trigger_level;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Products getProduct() {
		if (product == null) {
			ProductsDao dao = new ProductsDao();
			product = dao.getById(productId);
		}
		return product;
	}
	
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
}
