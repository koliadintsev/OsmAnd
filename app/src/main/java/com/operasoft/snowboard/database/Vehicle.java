package com.operasoft.snowboard.database;

public class Vehicle extends Dto {

	/**
         * 
         */
	private static final long serialVersionUID = 1L;

	public static final String ACTIVE_STATUS = "4fb77932-1c54-4331-8e7f-6ebbae8ed672";
	public static final String INACTIVE_STATUS = "4fb77941-a070-408b-aaa5-6ebbae8ed672";

	@Column(name = "company_id")
	private String company_id;

	@Column(name = "esn_id")
	private String esn_id;

	@Column(name = "name")
	private String name;

	@Column(name = "vehicle_number")
	private String vehicleNumber;

	@Column(name = "trailer")
	private String trailer;

	@Column(name = "equipment_type_id")
	private String equipmentTypeId;

	@Column(name = "trailer_id")
	private String trailerId;

	@Column(name = "imei_number")
	private String imeiNumber;

	@Column(name = "status_code_id")
	private String statusCodeId;

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getEsn_id() {
		return esn_id;
	}

	public void setEsn_id(String esn_id) {
		this.esn_id = esn_id;
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

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	public String getEquipmentTypeId() {
		return equipmentTypeId;
	}

	public void setEquipmentTypeId(String equipmentTypeId) {
		this.equipmentTypeId = equipmentTypeId;
	}

	public String getTrailerId() {
		return trailerId;
	}

	public void setTrailerId(String trailerId) {
		this.trailerId = trailerId;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getStatusCodeId() {
		return statusCodeId;
	}

	public void setStatusCodeId(String statusCodeId) {
		this.statusCodeId = statusCodeId;
	}

	public boolean isSnowflakeInstalled() {
		if ( (esn_id != null) && (esn_id.startsWith("999999")) ) {
			return false;
		}
		return true;
	}
}
