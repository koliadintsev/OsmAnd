package com.operasoft.snowboard.database;

@Deprecated
public class WorksheetEquipmentsOld extends Dto {

	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "worksheet_id")
	private String worksheetId;

	@Column(name = "equipment_name")
	private String equipmentName;

	@Column(name = "equipment_number")
	private String equipmentNumber;

	@Column(name = "hours_used")
	private String hoursUsed;

	@Column(name = "equipment_id")
	private String equipmentId;

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

	public String getWorksheetId() {
		return worksheetId;
	}

	public void setWorksheetId(String worksheetId) {
		this.worksheetId = worksheetId;
	}

	public String getHoursUsed() {
		return hoursUsed;
	}

	public void setHoursUsed(String hoursUsed) {
		this.hoursUsed = hoursUsed;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

}