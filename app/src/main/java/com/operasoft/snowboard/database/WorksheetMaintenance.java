package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

public class WorksheetMaintenance extends Dto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "company_id")
	private String CompanyId;

	@Column(name = "service_location_id")
	private String serviceLocationId;

	@Column(name = "date")
	private String date;

	@Column(name = "job_number")
	private String jobNumber;

	@Column(name = "trailer")
	private String trailer;

	@Column(name = "enroute")
	private String enRoute;

	@Column(name = "travel_distance")
	private String travelDistance;

	@Column(name = "arrived")
	private String arrived;

	@Column(name = "travel_time")
	private String travelTime;

	@Column(name = "completed")
	private String completed;

	@Column(name = "on_site_time")
	private String onSiteTime;

	@Column(name = "employee_id")
	private String employeeId;

	@Column(name = "job_photos")
	private String jobPhotos;

	@Column(name = "time_left")
	private String timeLeft;

	@Column(name = "notes")
	private String notes;

	@Column(name = "contract_id")
	private String contractId;

	@Column(name = "temp_am")
	private String tempAm;

	@Column(name = "temp_pm")
	private String tempPm;

	@Column(name = "temp_eod")
	private String tempEod;

	@Column(name = "weather_type")
	private String weatherTypes;

	@Column(name = "worksheet_type")
	private String worksheetType;

	@Column(name = "status")
	private String status;

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

	public String getWeatherTypes() {
		return weatherTypes;
	}

	public void setWeatherTypes(String weatherTypes) {
		this.weatherTypes = weatherTypes;
	}

	private ArrayList<WorksheetMaintenanceTasks> worksheetMaintenanceTask;

	private ArrayList<WorksheetMaintenanceProducts> worksheetProductList;

	private ArrayList<WorksheetEmployeeLogs> worksheetEmployeeList;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

	public String getServiceLocationId() {
		return serviceLocationId;
	}

	public void setServiceLocationId(String serviceLocationId) {
		this.serviceLocationId = serviceLocationId;
	}

	public String getJobNumber() {
		return jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	public String getEnRoute() {
		return enRoute;
	}

	public void setEnRoute(String enRoute) {
		this.enRoute = enRoute;
	}

	public String getArrived() {
		return arrived;
	}

	public void setArrived(String arrived) {
		this.arrived = arrived;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public String getOnSiteTime() {
		return onSiteTime;
	}

	public void setOnSiteTime(String onSiteTime) {
		this.onSiteTime = onSiteTime;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(String timeLeft) {
		this.timeLeft = timeLeft;
	}

	public String getTravelDistance() {
		return travelDistance;
	}

	public void setTravelDistance(String travelDistance) {
		this.travelDistance = travelDistance;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}

	public ArrayList<WorksheetMaintenanceTasks> getWorksheetMaintenanceTask() {
		return worksheetMaintenanceTask;
	}

	public void setWorksheetMaintenanceTask(ArrayList<WorksheetMaintenanceTasks> worksheetMaintenanceTask) {
		this.worksheetMaintenanceTask = worksheetMaintenanceTask;
	}

	public ArrayList<WorksheetMaintenanceProducts> getWorksheetProductList() {
		return worksheetProductList;
	}

	public void setWorksheetProductList(ArrayList<WorksheetMaintenanceProducts> worksheetProductList) {
		this.worksheetProductList = worksheetProductList;
	}

	public String getJobPhotos() {
		return jobPhotos;
	}

	public void setJobPhotos(String jobPhotos) {
		this.jobPhotos = jobPhotos;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<WorksheetMaintenanceTasks> getWorksheetMaintenanceTaskList() {
		if (worksheetMaintenanceTask == null) {
			WorksheetMaintenanceTasksDao worksheetMaintenanceTaskDao = new WorksheetMaintenanceTasksDao();
			worksheetMaintenanceTask = worksheetMaintenanceTaskDao.getListAttachedWithWorksheet(id);
		}
		return worksheetMaintenanceTask;
	}

	public ArrayList<WorksheetMaintenanceProducts> getMaintenanceProductList() {
		if (worksheetProductList == null) {
			WorksheetMaintenanceProductDao maintenanceProductDao = new WorksheetMaintenanceProductDao();
			worksheetProductList = maintenanceProductDao.getListAttachedWithWorksheet(id);
		}
		return worksheetProductList;
	}

	public String getWorksheetType() {
		return worksheetType;
	}

	public void setWorksheetType(String worksheetType) {
		this.worksheetType = worksheetType;
	}

	public ArrayList<WorksheetEmployeeLogs> getWorksheetEmployeeList() {
		return worksheetEmployeeList;
	}

	public void setWorksheetEmployeeList(ArrayList<WorksheetEmployeeLogs> worksheetEmployeeList) {
		this.worksheetEmployeeList = worksheetEmployeeList;
	}
}
