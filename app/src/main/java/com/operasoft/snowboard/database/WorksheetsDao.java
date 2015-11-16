package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorksheetsDao extends Dao<Worksheets> {

	public WorksheetsDao() {
		super("sb_worksheets");
	}

	@Override
	public void insert(Worksheets dto) {
		dto.setOpenStatus();
		insertDto(dto);
	}

	@Override
	public void replace(Worksheets dto) {
		replaceDto(dto);
	}

	@Override
	protected Worksheets buildDto(Cursor cursor) {
		Worksheets worksheet = new Worksheets();

		worksheet.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		worksheet.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		worksheet.setVisitors(cursor.getString(cursor.getColumnIndexOrThrow("visitors")));
		worksheet.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));

		worksheet.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		worksheet.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		worksheet.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		worksheet.setWorkPerformed(cursor.getString(cursor.getColumnIndexOrThrow("desc_work_performed")));
		worksheet.setTemperature(cursor.getString(cursor.getColumnIndexOrThrow("temperature")));
		worksheet.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
		worksheet.setWeather(cursor.getString(cursor.getColumnIndexOrThrow("weather")));
		worksheet.setAccidentNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes_acident_incident")));
		worksheet.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
		worksheet.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		worksheet.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		worksheet.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
		worksheet.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		worksheet.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		/*
		worksheet.setJob_number(cursor.getString(cursor.getColumnIndexOrThrow("job_number")));
		worksheet.setJob_name(cursor.getString(cursor.getColumnIndexOrThrow("job_name")));
		worksheet.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		worksheet.setService_location_id(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		worksheet.setDaily_photos(cursor.getString(cursor.getColumnIndexOrThrow("daily_photos")));
		worksheet.setJob_meeting_notes(cursor.getString(cursor.getColumnIndexOrThrow("job_meeting_notes")));
		worksheet.setDesc_work_performed(cursor.getString(cursor.getColumnIndexOrThrow("desc_work_performed")));
		worksheet.setAccident_incident_notes(cursor.getString(cursor.getColumnIndexOrThrow("accident_incident_notes")));
		worksheet.setTempAm(cursor.getString(cursor.getColumnIndexOrThrow("temp_am")));
		worksheet.setTempPm(cursor.getString(cursor.getColumnIndexOrThrow("temp_pm")));
		worksheet.setTempEod(cursor.getString(cursor.getColumnIndexOrThrow("temp_eod")));
		worksheet.setWeatherType(cursor.getString(cursor.getColumnIndexOrThrow("weather_type")));
		worksheet.setCompletedBy(cursor.getString(cursor.getColumnIndexOrThrow("completed_by")));
		worksheet.setWorksheetMaintenanceId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_maintenance_id")));
		*/
		return worksheet;
	}

	@Override
	public Worksheets buildDto(JSONObject json) throws JSONException {
		Worksheets dto = new Worksheets();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setVisitors(jsonParser.parseString(json, "visitors"));
		dto.setContractId(jsonParser.parseString(json, "contract_id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setWorkPerformed(jsonParser.parseString(json, "desc_work_performed"));
		dto.setTemperature(jsonParser.parseString(json, "temperature"));
		dto.setComments(jsonParser.parseString(json, "comments"));
		dto.setWeather(jsonParser.parseString(json, "weather"));
		dto.setAccidentNotes(jsonParser.parseString(json, "notes_acident_incident"));
		dto.setStatus(jsonParser.parseString(json, "status"));
		dto.setContractId(jsonParser.parseString(json, "contract_id"));
		dto.setCreatorId(jsonParser.parseString(json, "creator_id"));
		dto.setStartDate(jsonParser.parseString(json, "start_date"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public Worksheets findOpenWorksheet(String companyid, String contractid) {
		final String sql = "SELECT * FROM " + table + " WHERE company_id = '" + companyid + "' AND contract_id='" + contractid + "' AND status='" + Worksheets.STATUS_OPEN + "' AND (created IS NOT NULL OR sync_flag = 1) ORDER BY modified DESC LIMIT 1";
		return getDto(sql);
	}
}
