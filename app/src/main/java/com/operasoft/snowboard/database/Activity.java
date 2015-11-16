package com.operasoft.snowboard.database;

public class Activity extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String companyId;
	private String serviceActivityId;
	private String contractServiceId;
	private float quantity;
	private String time;
	
	private ContractServices service;
	
	public Activity() {
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getServiceActivityId() {
		return serviceActivityId;
	}

	public void setServiceActivityId(String serviceActivityId) {
		this.serviceActivityId = serviceActivityId;
	}

	public String getContractServiceId() {
		return contractServiceId;
	}

	public void setContractServiceId(String contractServiceId) {
		this.contractServiceId = contractServiceId;
	}
	
	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
	
	public ContractServices getService() {
		// We use a late binding approach for this
		if (service == null) {
			ContractServicesDao dao = new ContractServicesDao();
			service = dao.getById(contractServiceId);
		}
		return service;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
