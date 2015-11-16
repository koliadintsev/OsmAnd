package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author dounaka
 * 
 * 
 * 
 * 10h45 : git issue #124
 * 11h45
 * 
 * 12h45 
 *
 *  
 *  Worksheets improvements

 Only Key pad to display when entering hours on SB
 Must see entry field when when entering hours or text
 Please add SL name and SL Address at top menu next to Job & Contract #'s

 * 3- finish the sync class
 * 
 * 4 - need to sort all list
 * travel = dropoff-Punch 
 * evenement : sur dropoff associe un evenement travel time 
 * dans user work status log (tous les employés 
 * UserWorkStatusLog.date_time >= Worksheet.startDate AND
 * UserWorkStatusLog.service_location_id  = Worksheet.Contract.service_location_id
 * 
 * 
 * voir pour un autre clavier : juste numero
 * 
 * changer l'entete du rapport (job, contract, SL name, address)
 * 
 */
public class Worksheets extends Dto {

	private static final long serialVersionUID = 1L;

	public static final String STATUS_OPEN = "open";
	public static final String STATUS_APPROVED = "approved";
	public static final String STATUS_LOCK = "lock";

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "start_date")
	private String startDate;

	@Column(name = "visitors")
	private String visitors;

	@Column(name = "notes")
	private String notes;

	@Column(name = "desc_work_performed")
	private String workPerformed;

	@Column(name = "weather")
	private String weather;

	@Column(name = "comments")
	private String comments;

	@Column(name = "notes_acident_incident")
	private String accidentNotes;

	@Column(name = "temperature")
	private String temperature;

	@Column(name = "status")
	private String status = STATUS_OPEN;

	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "creator_id")
	private String creatorId;

	private Contract contract = null;
	
	private ArrayList<WorksheetLabour> worksheetLabourList;
	private ArrayList<WorksheetEquipment> worksheetEquipmentList;
	private ArrayList<WorksheetMaterial> worksheetMaterialList;
	private ArrayList<WorksheetTravelTime> worksheetTravelTimeList;

	// --------------------------------------------	

	private String desc_work_performed;

	public String getWorkPerformed() {
		return workPerformed;
	}

	public void setWorkPerformed(String workPerformed) {
		this.workPerformed = workPerformed;
	}

	private ArrayList<WorksheetEmployeeLogs> employeeLogsList;
	// private ArrayList<WorksheetEmployeeDetails> employeeDetailsList;
	private ArrayList<WorksheetEquipmentsOld> equipmentsList;
	private ArrayList<WorksheetMaintenanceProducts> maintenanceProductList;
	private ArrayList<WorksheetSubContractors> subContractorsList;
	private ArrayList<WorksheetMaintenance> WorksheetMaintenancesList;
	private ArrayList<WorksheetMaintenanceTasks> WorksheetMaintenanceTaskList;

	// private ArrayList<WorksheetEmployeeDetailsLogTimes>
	// employeeDetailsLogTimes;

	// setEmployeeMaintenanceList

	public void setEmployeeLogsList(ArrayList<WorksheetEmployeeLogs> employeeLogsList) {
		this.employeeLogsList = employeeLogsList;
	}

	public ArrayList<WorksheetEmployeeLogs> getEmployeeLogsList() {
		if (employeeLogsList == null) {
			WorksheetEmployeeLogsDao worksheetEmployeeLogsDao = new WorksheetEmployeeLogsDao();
			employeeLogsList = worksheetEmployeeLogsDao.getListAttachedWithWorksheet(id);
		}
		return employeeLogsList;
	}

	public ArrayList<WorksheetEquipmentsOld> getEquipmentsList() {
		if (equipmentsList == null) {
			WorksheetEquipmentsOldDao worksheetEquipmentsOldDao = new WorksheetEquipmentsOldDao();
			equipmentsList = worksheetEquipmentsOldDao.getListAttachedWithWorksheet(id);
		}
		return equipmentsList;
	}

	public void setEquipmentsList(ArrayList<WorksheetEquipmentsOld> equipmentsList) {
		this.equipmentsList = equipmentsList;
	}

	public ArrayList<WorksheetMaintenanceProducts> getMaintenanceProductList() {
		if (maintenanceProductList == null) {
			WorksheetMaintenanceProductDao maintenanceProductDao = new WorksheetMaintenanceProductDao();
			maintenanceProductList = maintenanceProductDao.getListAttachedWithWorksheet(id);
		}
		return maintenanceProductList;
	}

	public void setMaintenanceProductList(ArrayList<WorksheetMaintenanceProducts> maintenanceProductList) {
		this.maintenanceProductList = maintenanceProductList;
	}

	public ArrayList<WorksheetSubContractors> getSubContractorsList() {
		if (subContractorsList == null) {
			WorksheetSubContractorsDao subContractorsDao = new WorksheetSubContractorsDao();
			subContractorsList = subContractorsDao.getListAttachedWithWorksheet(id);
		}
		return subContractorsList;
	}

	public void setSubContractorsList(ArrayList<WorksheetSubContractors> subContractorsList) {
		this.subContractorsList = subContractorsList;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String id) {
		this.companyId = id;
	}

	public String getVisitors() {
		return visitors;
	}

	public void setVisitors(String visitors) {
		this.visitors = visitors;
	}

	public String getDesc_work_performed() {
		return desc_work_performed;
	}

	public void setDesc_work_performed(String desc_work_performed) {
		this.desc_work_performed = desc_work_performed;
	}

	// public void setEmployeeDetailsList(ArrayList<WorksheetEmployeeDetails>
	// employeeDetailsList) {
	// this.employeeDetailsList = employeeDetailsList;
	// }

	// public ArrayList<WorksheetEmployeeDetails> getEmployeeDetailsList() {
	// if (employeeDetailsList == null) {
	// WorksheetEmployeeDetailsDao worksheetEmployeeDetailsDao = new
	// WorksheetEmployeeDetailsDao();
	// employeeDetailsList =
	// worksheetEmployeeDetailsDao.getListAttachedWithWorksheet(id);
	// }
	// return employeeDetailsList;
	// }

	// public ArrayList<WorksheetEmployeeDetailsLogTimes>
	// getEmployeeDetailsLogTimesList() {
	// if (employeeDetailsLogTimes == null) {
	// WorksheetEmployeeDetailsLogTimesDao worksheetEmployeeDetailsDao = new
	// WorksheetEmployeeDetailsLogTimesDao();
	// employeeDetailsLogTimes =
	// worksheetEmployeeDetailsDao.getListAttachedWithWorksheet(id);
	// }
	// return employeeDetailsLogTimes;
	// }

	public ArrayList<WorksheetMaintenance> getWorksheetMaintenancesList() {
		if (WorksheetMaintenancesList == null) {
			WorksheetMaintenancesDao worksheetMaintenancesDao = new WorksheetMaintenancesDao();
			// WorksheetMaintenancesList =
			// worksheetMaintenancesDao.getListAttachedWithWorksheet(id);
		}
		return WorksheetMaintenancesList;
	}

	public void setWorksheetMaintenancesList(ArrayList<WorksheetMaintenance> worksheetMaintenancesList) {
		this.WorksheetMaintenancesList = worksheetMaintenancesList;

	}

	public List<WorksheetMaintenanceTasks> getWorksheetMaintenanceTaskList() {
		if (WorksheetMaintenanceTaskList == null) {
			WorksheetMaintenanceTasksDao worksheetMaintenanceTaskDao = new WorksheetMaintenanceTasksDao();
			WorksheetMaintenanceTaskList = worksheetMaintenanceTaskDao.getListAttachedWithWorksheet(id);
		}
		return WorksheetMaintenanceTaskList;
	}

	public List<WorksheetLabour> getWorksheetLabourList() {
		if (worksheetLabourList == null) {
			WorksheetLabourDao worksheetListDao = new WorksheetLabourDao();
			Log.d("wid", "id:" + id);
			worksheetLabourList = worksheetListDao.getListAttachedWithWorksheet(id);
		}
		return worksheetLabourList;
	}

	public List<WorksheetEquipment> getWorksheetEquipmentList() {
		if (worksheetEquipmentList == null) {
			WorksheetEquipmentDao worksheetListDao = new WorksheetEquipmentDao();
			worksheetEquipmentList = worksheetListDao.getListAttachedWithWorksheet(id);
		}
		return worksheetEquipmentList;
	}

	public List<WorksheetMaterial> getWorksheetMaterialList() {
		if (worksheetMaterialList == null) {
			WorksheetMaterialDao worksheetListDao = new WorksheetMaterialDao();
			Log.d("wid", "id:" + id);
			worksheetMaterialList = worksheetListDao.getListAttachedWithWorksheet(id);
		}
		return worksheetMaterialList;
	}

	public List<WorksheetTravelTime> getWorksheetTravelTimeList() {
		if (worksheetTravelTimeList == null) {
			WorksheetTravelTimeDao worksheetListDao = new WorksheetTravelTimeDao();
			Log.d("wid", "id:" + id);
			worksheetTravelTimeList = worksheetListDao.getListAttachedWithWorksheet(id);
		}
		return worksheetTravelTimeList;
	}

	public void setWorksheetMaintenanceTaskList(ArrayList<WorksheetMaintenanceTasks> worksheetMaintenanceTask) {
		this.WorksheetMaintenanceTaskList = worksheetMaintenanceTask;
	}

	public void setWorksheetMaterialList(ArrayList<WorksheetMaterial> worksheetMaterials) {
		this.worksheetMaterialList = worksheetMaterials;
	}

	public void setWorksheetTravelTimeList(ArrayList<WorksheetTravelTime> worksheetTravelTimes) {
		this.worksheetTravelTimeList = worksheetTravelTimes;
	}

	public Contract getContract() {
		if ( (contract == null) && (contractId != null)) {
			ContractsDao dao = new ContractsDao();
			contract = dao.getById(contractId);
		}
		return contract;
	}
	
	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAccidentNotes() {
		return accidentNotes;
	}

	public void setAccidentNotes(String accidentNotes) {
		this.accidentNotes = accidentNotes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setOpenStatus() {
		this.status = STATUS_OPEN;
	}

	public boolean isOpen() {
		return (status == null) || (status.equalsIgnoreCase(STATUS_OPEN));
	}
	
	public void setLockStatus() {
		this.status = STATUS_LOCK;
	}

	public void setApprovedStatus() {
		this.status = STATUS_APPROVED;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public void setWorksheetLabourList(ArrayList<WorksheetLabour> worksheetLabourList) {
		this.worksheetLabourList = worksheetLabourList;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	///////////////

	// @Column(name = "division_id")
	private String DivisionId;

	//@Column(name = "job_number")
	private String job_number;

	//@Column(name = "job_name")
	private String job_name;

	//@Column(name = "date")
	private String date;

	//@Column(name = "service_location_id")

	private String service_location_id;

	//@Column(name = "daily_photos")
	private String daily_photos;

	//@Column(name = "job_meeting_notes")
	private String job_meeting_notes;

	// @Column(name = "accident_incident_notes")
	private String accident_incident_notes;

	//@Column(name = "temp_am")
	private String tempAm;

	//@Column(name = "temp_pm")
	private String tempPm;

	//@Column(name = "temp_eod")
	private String tempEod;

	//@Column(name = "weather_type")
	private String weatherType;

	//@Column(name = "completed_by")
	private String completedBy;

	//@Column(name = "worksheet_maintenance_id")
	private String worksheetMaintenanceId;

	private String maintenance_sheet_id;

	public String getWorksheetMaintenanceId() {
		return worksheetMaintenanceId;
	}

	public String getJob_number() {
		return job_number;
	}

	public String getTempAm() {
		return tempAm;
	}

	public void setTempAm(String tempAm) {
		this.tempAm = tempAm;
	}

	public String getTempPm() {
		return tempPm;
	}

	public void setTempPm(String tempPm) {
		this.tempPm = tempPm;
	}

	public String getTempEod() {
		return tempEod;
	}

	public void setTempEod(String tempEod) {
		this.tempEod = tempEod;
	}

	public String getWeatherType() {
		return weatherType;
	}

	public void setWeatherType(String weatherType) {
		this.weatherType = weatherType;
	}

	public String getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}

	public void setWorksheetMaintenanceId(String worksheetMaintenanceId) {
		this.worksheetMaintenanceId = worksheetMaintenanceId;
	}

	public String getAccident_incident_notes() {
		return accident_incident_notes;
	}

	public void setAccident_incident_notes(String accident_incident_notes) {
		this.accident_incident_notes = accident_incident_notes;
	}

	public void setJob_number(String job_number) {
		this.job_number = job_number;
	}

	public String getJob_name() {
		return job_name;
	}

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getService_location_id() {
		return service_location_id;
	}

	public void setService_location_id(String service_location_id) {
		this.service_location_id = service_location_id;
	}

	public String getDaily_photos() {
		return daily_photos;
	}

	public void setDaily_photos(String daily_photos) {
		this.daily_photos = daily_photos;
	}

	public String getJob_meeting_notes() {
		return job_meeting_notes;
	}

	public void setJob_meeting_notes(String job_meeting_notes) {
		this.job_meeting_notes = job_meeting_notes;
	}

	public String getMaintenance_sheet_id() {
		return maintenance_sheet_id;
	}

	public void setMaintenance_sheet_id(String maintenance_sheet_id) {
		this.maintenance_sheet_id = maintenance_sheet_id;
	}

	public String getDivisionId() {
		return DivisionId;
	}

	public void setDivisionId(String divisionId) {
		DivisionId = divisionId;
	}

	public void add(WorksheetLabour labour) {
		if (worksheetLabourList == null) {
			worksheetLabourList = new ArrayList<WorksheetLabour>();			
		}
		labour.setWorksheetId(id);
		worksheetLabourList.add(labour);
	}

	public void add(WorksheetEquipment equipment) {
		if (worksheetEquipmentList == null) {
			worksheetEquipmentList = new ArrayList<WorksheetEquipment>();			
		}
		equipment.setWorksheetId(id);
		worksheetEquipmentList.add(equipment);
	}

	public void add(WorksheetMaterial material) {
		if (worksheetMaterialList == null) {
			worksheetMaterialList = new ArrayList<WorksheetMaterial>();			
		}
		material.setWorksheetId(id);
		worksheetMaterialList.add(material);
	}

	public void add(WorksheetTravelTime travelTime) {
		if (worksheetTravelTimeList == null) {
			worksheetTravelTimeList = new ArrayList<WorksheetTravelTime>();			
		}
		travelTime.setWorksheetId(id);
		worksheetTravelTimeList.add(travelTime);
	}
}
