package com.operasoft.snowboard.database;

public class Damage extends Dto {

	private static final long serialVersionUID = 1L;
	public final static String DAMAGE_PENDING = "6f163f1e-fcb0-11e1-aa27-0025900e9333";

	@Column(name="user_id")
	private String userId;

	@Column(name="vehicle_id")
	private String vehicleId;

	@Column(name="company_id")
	private String companyId;

	@Column(name="description")
	private String description;

	@Column(name="contract_id")
	private String contractId;

	@Column(name="date_time")
	private String date;

	@Column(name="gps_coordinates")
	private String gpsCoordinates;

	@Column(name="comments")
	private String comments;

	@Column(name="foreign_key")
	private String foreignKey;
	
	@Column(name="foreign_value")
	private String foreignValue;
	
	@Column(name="status_code_id")
	private String status;

	@Column(name="damage_type_id")
	private String damageTypeId;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contract_id) {
		this.contractId = contract_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status_code_id) {
		this.status = status_code_id;
	}

	public String getDamageTypeId() {
		return damageTypeId;
	}

	public void setDamageTypeId(String damage_type_id) {
		this.damageTypeId = damage_type_id;
	}

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getGpsCoordinates() {
		return gpsCoordinates;
	}

	public void setGpsCoordinates(String gpsCoordinates) {
		this.gpsCoordinates = gpsCoordinates;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	public String getForeignValue() {
		return foreignValue;
	}

	public void setForeignValue(String foreignValue) {
		this.foreignValue = foreignValue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}