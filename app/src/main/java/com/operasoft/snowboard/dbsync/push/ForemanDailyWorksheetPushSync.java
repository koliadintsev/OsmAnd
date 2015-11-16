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
import com.operasoft.snowboard.database.WorksheetEquipmentsOld;
import com.operasoft.snowboard.database.WorksheetEquipmentsOldDao;
import com.operasoft.snowboard.database.WorksheetSubContractors;
import com.operasoft.snowboard.database.WorksheetSubContractorsDao;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.database.WorksheetsDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class ForemanDailyWorksheetPushSync extends AbstractPushSync<Worksheets> {

	private final JsonDtoParser dtoParser = new JsonDtoParser();
	private WorksheetsDao worksheetsDao = null;
	private WorksheetEmployeeLogsDao employeeLogsDao = null;
	private WorksheetEquipmentsOldDao equipmentDao = null;
	private WorksheetSubContractorsDao subContractorsDao = null;

	// Singleton pattern
	static private ForemanDailyWorksheetPushSync instance_s = new ForemanDailyWorksheetPushSync();

	static public ForemanDailyWorksheetPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new ForemanDailyWorksheetPushSync();
		}

		return instance_s;
	}

	private ForemanDailyWorksheetPushSync() {
		super("Worksheet");
		getDao();
	}

	@Override
	protected Dao<Worksheets> getDao() {
		if (worksheetsDao == null) {
			worksheetsDao = new WorksheetsDao();
			employeeLogsDao = new WorksheetEmployeeLogsDao();
			equipmentDao = new WorksheetEquipmentsOldDao();
			subContractorsDao = new WorksheetSubContractorsDao();
		}
		return worksheetsDao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Worksheets dto) {

		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[job_number]", dto.getJob_number()));
		params.add(new BasicNameValuePair(model + "[job_name]", dto.getJob_name()));
		params.add(new BasicNameValuePair(model + "[date]", dto.getDate()));
		params.add(new BasicNameValuePair(model + "[service_location_id]", dto.getService_location_id()));
		params.add(new BasicNameValuePair(model + "[visitors]", dto.getVisitors()));
		params.add(new BasicNameValuePair(model + "[job_meeting_notes]", dto.getJob_meeting_notes()));
		params.add(new BasicNameValuePair(model + "[desc_work_performed]", dto.getDesc_work_performed()));
		params.add(new BasicNameValuePair(model + "[accident_incident_notes]", dto.getAccident_incident_notes()));
		params.add(new BasicNameValuePair(model + "[division_id]", dto.getDivisionId()));
		params.add(new BasicNameValuePair(model + "[temp_am]", dto.getTempAm()));
		params.add(new BasicNameValuePair(model + "[temp_pm]", dto.getTempPm()));
		params.add(new BasicNameValuePair(model + "[temp_eod]", dto.getTempEod()));
		params.add(new BasicNameValuePair(model + "[weather_type]", dto.getWeatherType()));
		params.add(new BasicNameValuePair(model + "[completed_by]", dto.getCompletedBy()));
		params.add(new BasicNameValuePair(model + "[worksheet_maintenance_id]", dto.getWorksheetMaintenanceId()));
		params.add(new BasicNameValuePair(model + "[contract_id]", dto.getContractId()));

		// Specify the data to put in the Activity table
		List<WorksheetEquipmentsOld> equipmentsList = dto.getEquipmentsList();
		List<WorksheetEmployeeLogs> employeeLogsList = dto.getEmployeeLogsList();
		List<WorksheetSubContractors> subContractors = dto.getSubContractorsList();

		int counter = 0;

		for (WorksheetEquipmentsOld equipment : equipmentsList) {
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][hours_used]", equipment.getHoursUsed()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][equipment_id]", equipment.getEquipmentId()));
			params.add(new BasicNameValuePair("WorksheetEquipment[" + counter + "][company_id]", dto.getCompanyId()));
			counter++;
		}

		counter = 0;
		for (WorksheetEmployeeLogs employeeLog : employeeLogsList) {
			params.add(new BasicNameValuePair("WorksheetEmployeeLog[" + counter + "][emp_id]", employeeLog.getEmp_id()));
			params.add(new BasicNameValuePair("WorksheetEmployeeLog[" + counter + "][punch_in]", employeeLog.getPunch_in()));
			params.add(new BasicNameValuePair("WorksheetEmployeeLog[" + counter + "][company_id]", dto.getCompanyId()));

			counter++;
		}

		counter = 0;
		for (WorksheetSubContractors subContractor : subContractors) {
			params.add(new BasicNameValuePair("WorksheetSubContractor[" + counter + "][sub_contractor]", subContractor.getSub_contractor()));
			params.add(new BasicNameValuePair("WorksheetSubContractor[" + counter + "][work_performence]", subContractor.getWork_performence()));
			params.add(new BasicNameValuePair("WorksheetSubContractor[" + counter + "][company_id]", dto.getCompanyId()));

			counter++;
		}

		if ((dto.getDaily_photos() != null) && (!dto.getDaily_photos().equals(""))) {
			params.add(new BasicNameValuePair(model + "[daily_photos_url]", dto.getDaily_photos()));
		}

	}

	@Override
	protected boolean processServerResponse(String value, Worksheets dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("SA Push", "No details received from server: " + value);
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
			Worksheets worksheetsDto = dtoParser.parseWorksheet(jsonObject, "Worksheet");

			if (dto.isNew() && dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(worksheetsDto.getId());
				dto.setCreated(worksheetsDto.getCreated());
				// saDao.replaceId(dto);
				employeeLogsDao.replaceWorksheetId(dto.getId(), dto.getNewId());
				equipmentDao.replaceWorksheetId(dto.getId(), dto.getNewId());
				subContractorsDao.replaceWorksheetId(dto.getId(), dto.getNewId());
			} else if (dto.isNew()) {
				// The DTO has not been inserted in our DB yet
				dto.setId(worksheetsDto.getId());
				dto.setCreated(worksheetsDto.getCreated());
			}

			return true;
		}

		Log.e("Worksheet Push", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveDirtyDto(Worksheets dto) {
		worksheetsDao.markAsDirty(dto);

		List<WorksheetEquipmentsOld> equipmentsList = dto.getEquipmentsList();
		for (WorksheetEquipmentsOld equipment : equipmentsList) {
			equipment.setWorksheetId(dto.getId());
			equipmentDao.markAsDirty(equipment);
		}

		List<WorksheetEmployeeLogs> employeeLogsList = dto.getEmployeeLogsList();
		for (WorksheetEmployeeLogs employeeLog : employeeLogsList) {
			employeeLog.setWorksheet_id(dto.getId());
			employeeLogsDao.markAsDirty(employeeLog);
		}

		List<WorksheetSubContractors> subContractorsList = dto.getSubContractorsList();
		for (WorksheetSubContractors subContractor : subContractorsList) {
			subContractor.setWorksheet_id(dto.getId());
			subContractorsDao.markAsDirty(subContractor);
		}
	}

	@Override
	protected void clearDirtyDto(Worksheets dto) {
		worksheetsDao.clearDirtyDto(dto);
		List<WorksheetEquipmentsOld> equipmentsList = dto.getEquipmentsList();
		for (WorksheetEquipmentsOld equipment : equipmentsList) {
			if (equipment.isNew()) {
				equipment.setWorksheetId(dto.getId());
				equipmentDao.clearDirtyDto(equipment);
			}
		}

		List<WorksheetEmployeeLogs> employeeLogsList = dto.getEmployeeLogsList();
		for (WorksheetEmployeeLogs employeeLog : employeeLogsList) {
			if (employeeLog.isNew()) {
				employeeLog.setWorksheet_id(dto.getId());
				employeeLogsDao.clearDirtyDto(employeeLog);
			}
		}

		List<WorksheetSubContractors> subContractorsList = dto.getSubContractorsList();
		for (WorksheetSubContractors subContractor : subContractorsList) {
			if (subContractor.isNew()) {
				subContractor.setWorksheet_id(dto.getId());
				subContractorsDao.clearDirtyDto(subContractor);
			}
		}
	}

	@Override
	protected void saveClearDto(Worksheets dto) {
		worksheetsDao.insertOrReplace(dto);
		List<WorksheetEquipmentsOld> equipmentsList = dto.getEquipmentsList();
		for (WorksheetEquipmentsOld equipment : equipmentsList) {
			if (equipment.isNew()) {
				// Set the created date to make sure they are not sent again
				equipment.setCreated(CommonUtils.UtcDateNow());
				equipment.setWorksheetId(dto.getId());
				equipmentDao.insertOrReplace(equipment);
			}
		}

		List<WorksheetEmployeeLogs> employeeLogsList = dto.getEmployeeLogsList();
		for (WorksheetEmployeeLogs employeeLog : employeeLogsList) {
			if (employeeLog.isNew()) {
				// Set the created date to make sure they are not sent again
				employeeLog.setCreated(CommonUtils.UtcDateNow());
				employeeLog.setWorksheet_id(dto.getId());
				employeeLogsDao.insertOrReplace(employeeLog);
			}
		}

		List<WorksheetSubContractors> subContractorsList = dto.getSubContractorsList();
		for (WorksheetSubContractors subContractor : subContractorsList) {
			if (subContractor.isNew()) {
				subContractor.setCreated(CommonUtils.UtcDateNow());
				subContractor.setWorksheet_id(dto.getId());
				subContractorsDao.insertOrReplace(subContractor);
			}
		}
	}

}
