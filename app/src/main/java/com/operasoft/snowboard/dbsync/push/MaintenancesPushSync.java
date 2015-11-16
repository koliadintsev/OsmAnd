package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.WorksheetEmployeeLogs;
import com.operasoft.snowboard.database.WorksheetEmployeeLogsDao;
import com.operasoft.snowboard.database.WorksheetMaintenance;
import com.operasoft.snowboard.database.WorksheetMaintenanceProductDao;
import com.operasoft.snowboard.database.WorksheetMaintenanceProducts;
import com.operasoft.snowboard.database.WorksheetMaintenanceTasks;
import com.operasoft.snowboard.database.WorksheetMaintenanceTasksDao;
import com.operasoft.snowboard.database.WorksheetMaintenancesDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class MaintenancesPushSync extends AbstractPushSync<WorksheetMaintenance> {
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private WorksheetMaintenancesDao maintenancesDao = null;
	private WorksheetMaintenanceProductDao maintenanceProductDao = null;
	private WorksheetMaintenanceTasksDao maintenanceTaskDao = null;
	private WorksheetEmployeeLogsDao employeeLogDao = null;

	// Singleton pattern
	static private MaintenancesPushSync instance_s = new MaintenancesPushSync();

	static public MaintenancesPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new MaintenancesPushSync();
		}

		return instance_s;
	}

	public MaintenancesPushSync() {
		super("WorksheetMaintenance");
		getDao();
	}

	@Override
	protected Dao<WorksheetMaintenance> getDao() {
		if (maintenancesDao == null) {
			maintenancesDao = new WorksheetMaintenancesDao();
			maintenanceProductDao = new WorksheetMaintenanceProductDao();
			maintenanceTaskDao = new WorksheetMaintenanceTasksDao();
			employeeLogDao = new WorksheetEmployeeLogsDao();
		}
		return maintenancesDao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, WorksheetMaintenance dto) {
		List<WorksheetMaintenanceTasks> maintenancTaskList = dto.getWorksheetMaintenanceTaskList();
		List<WorksheetMaintenanceProducts> maintenanceProductsList = dto.getMaintenanceProductList();

		if (dto.getId() != null) {
			params.remove(actionParam);
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "edit"));
			params.add(new BasicNameValuePair("WorksheetMaintenance[id]", dto.getId()));
		}

		params.add(new BasicNameValuePair("WorksheetMaintenance[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[service_location_id]", dto.getServiceLocationId()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[job_number]", dto.getJobNumber()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[enroute]", dto.getEnRoute()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[arrived]", dto.getArrived()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[completed]", dto.getCompleted()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[employee_id]", ""));
		params.add(new BasicNameValuePair("WorksheetMaintenance[date]", dto.getDate()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[trailer]", dto.getTrailer()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[travel_distance]", dto.getTravelDistance()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[travel_time]", dto.getTravelTime()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[on_site_time]", dto.getOnSiteTime()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[time_left]", dto.getTimeLeft()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[notes]", dto.getNotes()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[job_photos]", dto.getJobPhotos()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[temp_am]", dto.getTempAm()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[temp_pm]", dto.getTempPm()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[temp_eod]", dto.getTempEod()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[weather_type]", dto.getWeatherTypes()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[worksheet_type]", dto.getWorksheetType()));
		params.add(new BasicNameValuePair("WorksheetMaintenance[contract_id]", dto.getContractId()));

		if (maintenancTaskList != null) {
			int maintenanceListCounter = 0;
			for (WorksheetMaintenanceTasks worksheetMaintenanceTask : maintenancTaskList) {
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceTask]["
						+ maintenanceListCounter + "][contract_service_id]", worksheetMaintenanceTask
						.getContract_service_id()));
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceTask]["
						+ maintenanceListCounter + "][product_id]", worksheetMaintenanceTask.getProduct_id()));
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceTask]["
						+ maintenanceListCounter + "][duration]", worksheetMaintenanceTask.getDuration()));
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceTask]["
						+ maintenanceListCounter + "][company_id]", worksheetMaintenanceTask.getCompanyId()));

				maintenanceListCounter++;
			}
		}

		if (maintenanceProductsList != null) {
			int maintenanceProductListCounter = 0;
			for (WorksheetMaintenanceProducts worksheetProducts : maintenanceProductsList) {
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceProduct]["
						+ maintenanceProductListCounter + "][company_id]", worksheetProducts.getCompany_id()));
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceProduct]["
						+ maintenanceProductListCounter + "][product_id]", worksheetProducts.getProductId()));
				params.add(new BasicNameValuePair("WorksheetMaintenance[WorksheetMaintenanceProduct]["
						+ maintenanceProductListCounter + "][quantity]", worksheetProducts.getQuantity()));

				maintenanceProductListCounter++;
			}
		}
	}

	@Override
	protected boolean processServerResponse(String value, WorksheetMaintenance dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}

			return true;
		}

		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		JSONArray jsArray = new JSONArray(value);
		if (jsArray.length() > 0) {
			JSONObject jsonObject = jsArray.getJSONObject(0);
			WorksheetMaintenance worksheetsMaintenancesDto = dtoParser.parseWorksheetMaintenance(jsonObject,
					"WorksheetMaintenance");

			if (dto.isNew() && dto.isDirty()) {
				dto.setNewId(worksheetsMaintenancesDto.getId());
				dto.setCreated(worksheetsMaintenancesDto.getCreated());
				// maintenancesDao.replaceWorksheetId(dto.getId(),
				// dto.getNewId());
				// maintenanceProductDao.replaceWorksheetId(dto.getId(),
				// dto.getNewId());
				maintenanceTaskDao.replaceWorksheetId(dto.getId(), dto.getNewId());
				// employeeLogDao.replaceWorksheetMaintenenceId(dto.getId(),
				// dto.getNewId());
			} else if (dto.isNew()) {
				// The DTO has not been inserted in our DB yet
				dto.setId(worksheetsMaintenancesDto.getId());
				dto.setCreated(worksheetsMaintenancesDto.getCreated());
			}

			return true;
		}

		Log.e("Worksheet Maintenance Push", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveDirtyDto(WorksheetMaintenance dto) {
		maintenancesDao.markAsDirty(dto);

		List<WorksheetMaintenanceTasks> worksheetMaintenanceTaskList = dto.getWorksheetMaintenanceTaskList();
		for (WorksheetMaintenanceTasks worksheetMaintenanceTask : worksheetMaintenanceTaskList) {
			worksheetMaintenanceTask.setWorksheet_maintenance_id(dto.getId());
			maintenanceTaskDao.markAsDirty(worksheetMaintenanceTask);
		}

		List<WorksheetMaintenanceProducts> maintenanceProductsList = dto.getMaintenanceProductList();
		for (WorksheetMaintenanceProducts maintenanceProduct : maintenanceProductsList) {
			maintenanceProduct.setWorksheet_maintenance_id(dto.getId());
			maintenanceProductDao.markAsDirty(maintenanceProduct);
		}

		List<WorksheetEmployeeLogs> maintenanceEmployeeList = dto.getWorksheetEmployeeList();
		for (WorksheetEmployeeLogs employeeLog : maintenanceEmployeeList) {
			// employeeLog.setWorksheetMaintenanceId(dto.getId());
			employeeLogDao.markAsDirty(employeeLog);
		}

	}

	@Override
	protected void saveClearDto(WorksheetMaintenance dto) {
		maintenancesDao.insertOrReplace(dto);

		List<WorksheetMaintenanceTasks> worksheetMaintenanceTaskList = dto.getWorksheetMaintenanceTaskList();
		for (WorksheetMaintenanceTasks worksheetMaintenanceTask : worksheetMaintenanceTaskList) {
			if (worksheetMaintenanceTask.isNew()) {
				// Set the created date to make sure they are not sent again
				worksheetMaintenanceTask.setCreated(CommonUtils.UtcDateNow());
				worksheetMaintenanceTask.setId(dto.getId());
				maintenanceTaskDao.insertOrReplace(worksheetMaintenanceTask);
			}
		}

		List<WorksheetEmployeeLogs> worksheetEmployeeList = dto.getWorksheetEmployeeList();
		if (worksheetEmployeeList != null) {
			for (WorksheetEmployeeLogs employeeLogs : worksheetEmployeeList) {
				if (employeeLogs.isNew()) {
					// Set the created date to make sure they are not sent again
					employeeLogs.setCreated(CommonUtils.UtcDateNow());
					employeeLogs.setId(dto.getId());
					employeeLogDao.insertOrReplace(employeeLogs);
				}
			}
		}

		List<WorksheetMaintenanceProducts> maintenanceProductsList = dto.getMaintenanceProductList();
		for (WorksheetMaintenanceProducts maintenanceProduct : maintenanceProductsList) {
			if (maintenanceProduct.isNew()) {
				maintenanceProduct.setCreated(CommonUtils.UtcDateNow());
				maintenanceProduct.setWorksheet_maintenance_id(dto.getId());
				maintenanceProductDao.insertOrReplace(maintenanceProduct);
			}
		}
	}

}
