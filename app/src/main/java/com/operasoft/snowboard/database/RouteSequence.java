package com.operasoft.snowboard.database;

public class RouteSequence extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="route_id")
	private String routeId;

	@Column(name="contract_id")
	private String contract_id;

	@Column(name="service_location_id")
	private String service_location_id;

	@Column(name="company_id")
	private String company_id;

	@Column(name="sequence_order")
	private int sequenceOrder;

	private ServiceLocation serviceLocation = new ServiceLocation();

	public String getContract_id() {
		return contract_id;
	}

	public void setContract_id(String contract_id) {
		this.contract_id = contract_id;
	}

	public String getService_location_id() {
		return service_location_id;
	}

	public void setService_location_id(String service_location_id) {
		this.service_location_id = service_location_id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public ServiceLocation getServiceLocation() {
		return serviceLocation;
	}

	public void setServiceLocation(ServiceLocation serviceLocation) {
		this.serviceLocation = serviceLocation;
	}

	public int getSequenceOrder() {
		return sequenceOrder;
	}

	public void setSequenceOrder(int sequencOrder) {
		this.sequenceOrder = sequencOrder;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
}
