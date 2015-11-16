package com.operasoft.snowboard.database;

public class EquipmentTypes extends Dto {

	private static final long serialVersionUID = 1L;

	@Column(name="equipment_number")
	private String equipmentNumber;
	
	@Column(name="equipment_name")
	private String equipmentName;

	@Column(name="company_id")
	private String companyId;
	
	public String getEquipmentNumber() {
		return equipmentNumber;
	}

	public void setEquipmentNumber(String equipmentNumber) {
		this.equipmentNumber = equipmentNumber;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

}