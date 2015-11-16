package com.operasoft.snowboard.database;

public class WorkOrderItem extends Dto {
	private static final long serialVersionUID = 1L;
	
	@Column(name="work_order_id")
	private String workOrderId;
	
	public String getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(String workOrderId) {
		this.workOrderId = workOrderId;
	}

	@Column(name="product_id")
	private String productId;
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Column(name="result")
	private String result;
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Column(name="quantity")
	private float quantity;
	
	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
