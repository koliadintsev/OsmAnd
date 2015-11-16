package com.operasoft.snowboard.database;

public class TransportVehicle extends Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="company_id")
	private String companyId;

	@Column(name="supplier_id")
	private String supplierId;

	@Column(name="name")
	private String name;

	@Column(name="vehicle_number")
	private String vehicleNumber;

	@Column(name="license_plate")
	private String licensePlate;

	@Column(name="status_code_id")
	private String status;

	@Column(name="deleted")
	private int deleted;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicle_number) {
		this.vehicleNumber = vehicle_number;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}		
}
