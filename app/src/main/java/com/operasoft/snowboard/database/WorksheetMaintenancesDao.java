package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorksheetMaintenancesDao extends Dao<WorksheetMaintenance> {

	public WorksheetMaintenancesDao() {
		super("sb_worksheet_maintenances");
	}

	@Override
	public void insert(WorksheetMaintenance dto) {
		insertDto(dto);
	}

	@Override
	public void replace(WorksheetMaintenance dto) {
		replaceDto(dto);

	}

	@Override
	public WorksheetMaintenance buildDto(JSONObject json) throws JSONException {
		WorksheetMaintenance dto = new WorksheetMaintenance();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setArrived(jsonParser.parseString(json, "arrived"));
		dto.setCompleted(jsonParser.parseString(json, "completed"));
		dto.setDate(jsonParser.parseString(json, "date"));
		dto.setEmployeeId(jsonParser.parseString(json, "employee_id"));
		dto.setEnRoute(jsonParser.parseString(json, "enroute"));
		dto.setJobNumber(jsonParser.parseString(json, "job_number"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setOnSiteTime(jsonParser.parseString(json, "on_site_time"));
		dto.setServiceLocationId(jsonParser.parseString(json, "service_location_id"));
		dto.setTimeLeft(jsonParser.parseString(json, "time_left"));
		dto.setTrailer(jsonParser.parseString(json, "trailer"));
		dto.setTravelDistance(jsonParser.parseString(json, "travel_distance"));
		dto.setTravelTime(jsonParser.parseString(json, "travel_time"));
		dto.setTempAm(jsonParser.parseString(json, "temp_am"));
		dto.setTempPm(jsonParser.parseString(json, "temp_pm"));
		dto.setTempEod(jsonParser.parseString(json, "temp_eod"));
		dto.setWeatherTypes(jsonParser.parseString(json, "weather_type"));
		dto.setContractId(jsonParser.parseString(json, "contract_id"));
		dto.setStatus(jsonParser.parseString(json, "status"));
		dto.setWorksheetType(jsonParser.parseString(json, "worksheet_type"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	@Override
	protected WorksheetMaintenance buildDto(Cursor cursor) {
		WorksheetMaintenance worksheetMaintenances = new WorksheetMaintenance();
		worksheetMaintenances.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		worksheetMaintenances.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		worksheetMaintenances.setArrived(cursor.getString(cursor.getColumnIndexOrThrow("arrived")));
		worksheetMaintenances.setCompleted(cursor.getString(cursor.getColumnIndexOrThrow("completed")));
		worksheetMaintenances.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		worksheetMaintenances.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow("employee_id")));
		worksheetMaintenances.setEnRoute(cursor.getString(cursor.getColumnIndexOrThrow("enroute")));
		worksheetMaintenances.setJobNumber(cursor.getString(cursor.getColumnIndexOrThrow("job_number")));
		worksheetMaintenances.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		worksheetMaintenances.setOnSiteTime(cursor.getString(cursor.getColumnIndexOrThrow("on_site_time")));
		worksheetMaintenances
				.setServiceLocationId(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		worksheetMaintenances.setTimeLeft(cursor.getString(cursor.getColumnIndexOrThrow("time_left")));
		worksheetMaintenances.setTrailer(cursor.getString(cursor.getColumnIndexOrThrow("trailer")));
		worksheetMaintenances.setTravelDistance(cursor.getString(cursor.getColumnIndexOrThrow("travel_distance")));
		worksheetMaintenances.setTravelTime(cursor.getString(cursor.getColumnIndexOrThrow("travel_time")));
		worksheetMaintenances.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		worksheetMaintenances.setTempAm(cursor.getString(cursor.getColumnIndexOrThrow("temp_am")));
		worksheetMaintenances.setTempPm(cursor.getString(cursor.getColumnIndexOrThrow("temp_pm")));
		worksheetMaintenances.setTempEod(cursor.getString(cursor.getColumnIndexOrThrow("temp_eod")));
		worksheetMaintenances.setWeatherTypes(cursor.getString(cursor.getColumnIndexOrThrow("weather_type")));
		worksheetMaintenances.setWorksheetType(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_type")));
		worksheetMaintenances.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
		worksheetMaintenances.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		worksheetMaintenances.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return worksheetMaintenances;
	}

	// public void replaceWorksheetId(String id, String newId) {
	// String sql = "UPDATE " + table + " SET worksheet_id = '" + newId +
	// "' WHERE worksheet_id = '" + id + "';";
	// DataBaseHelper.getDataBase().rawQuery(sql, null);
	//
	// }

	// public ArrayList<WorksheetMaintenance>
	// getListAttachedWithWorksheet(String worsheetId) {
	// ArrayList<WorksheetMaintenance> list = new
	// ArrayList<WorksheetMaintenance>();
	//
	// String sql = "SELECT * FROM " + table + " where worksheet_id = '" +
	// worsheetId + "'";
	// Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
	//
	// while (cursor.moveToNext()) {
	// try {
	// WorksheetMaintenance dto = buildDto(cursor);
	// if (dto != null) {
	// list.add(dto);
	// }
	// } catch (Exception e) {
	// Log.e(sql, "field not found", e);
	// }
	// }
	// cursor.close();
	//
	// return list;
	// }

	/**
	 * @param serviceLocationId
	 * @return Work-sheet for current Service Location for the day
	 */
	public WorksheetMaintenance getWorksheetForServiceLocation(String serviceLocationId) {
		String sql = "SELECT * FROM " + table + " where service_location_id = '" + serviceLocationId + "'"
				+ "and strftime('%Y-%m-%d', created) = strftime('%Y-%m-%d', 'now')";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			WorksheetMaintenance dto = buildDto(cursor);
			cursor.close();
			return dto;
		}

		return null;
	}
}
