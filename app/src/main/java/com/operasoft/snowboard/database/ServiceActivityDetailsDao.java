package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class ServiceActivityDetailsDao extends Dao<ServiceActivityDetails> {

	public ServiceActivityDetailsDao() {
		super("sb_service_activity_details");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void insert(ServiceActivityDetails dto) {
		insertDto(dto);
	}

	@Override
	public void replace(ServiceActivityDetails dto) {
		replaceDto(dto);
	}

	public ServiceActivityDetails getSaDetailsFormDB(String serviceActivityId) {
		ServiceActivityDetails dto = null;

		String sql = "SELECT * FROM " + table + " WHERE service_activity_id = '" + serviceActivityId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				dto = buildDto(cursor);
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();
		return dto;
	}
	
	public void replaceServiceActivityId(String id, String newId) {
		String sql = "UPDATE " + table + " SET service_activity_id = '" + newId + "' WHERE service_activity_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}
	
	@Override
	protected ServiceActivityDetails buildDto(Cursor cursor) {
		ServiceActivityDetails dto = new ServiceActivityDetails();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setAccumulation_depth(cursor.getInt(cursor.getColumnIndexOrThrow("accumulation_depth")));
		dto.setAccumulation_type(cursor.getString(cursor.getColumnIndexOrThrow("accumulation_type")));
		dto.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setConditions(cursor.getString(cursor.getColumnIndexOrThrow("conditions")));
		dto.setDeice_roads(cursor.getString(cursor.getColumnIndexOrThrow("deice_roads")));
		dto.setDeice_walkways(cursor.getString(cursor.getColumnIndexOrThrow("deice_walkways")));
		dto.setInspection_only(cursor.getInt(cursor.getColumnIndexOrThrow("inspection_only")));
		dto.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		dto.setOutdoor_temp(cursor.getString(cursor.getColumnIndexOrThrow("outdoor_temp")));
		dto.setPlowing_roads(cursor.getString(cursor.getColumnIndexOrThrow("plowing_roads")));
		dto.setPlowing_walkways(cursor.getString(cursor.getColumnIndexOrThrow("plowing_walkways")));
		dto.setPrecipitation(cursor.getString(cursor.getColumnIndexOrThrow("precipitation")));
		dto.setService_activity_id(cursor.getString(cursor.getColumnIndexOrThrow("service_activity_id")));
		dto.setTime_on_site(cursor.getDouble(cursor.getColumnIndexOrThrow("time_on_site")));

		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public ServiceActivityDetails buildDto(JSONObject json)
			throws JSONException {
		ServiceActivityDetails dto = new ServiceActivityDetails();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setAccumulation_depth(jsonParser.parseInt(json, "accumulation_depth"));
		dto.setAccumulation_type(jsonParser.parseString(json, "accumulation_type"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setConditions(jsonParser.parseString(json, "conditions"));
		dto.setDeice_roads(jsonParser.parseString(json, "deice_roads"));
		dto.setDeice_walkways(jsonParser.parseString(json, "deice_walkways"));
		dto.setInspection_only(jsonParser.parseInt(json, "inspection_only"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setOutdoor_temp(jsonParser.parseString(json, "outdoor_temp"));
		dto.setPlowing_roads(jsonParser.parseString(json, "plowing_roads"));
		dto.setPlowing_walkways(jsonParser.parseString(json, "plowing_walkways"));
		dto.setPrecipitation(jsonParser.parseString(json, "precipitation"));
		dto.setService_activity_id(jsonParser.parseString(json, "service_activity_id"));
		dto.setTime_on_site(jsonParser.parseDouble(json, "time_on_site"));

		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}
}
