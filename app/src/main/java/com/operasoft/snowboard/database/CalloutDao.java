package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class CalloutDao extends Dao<Callout> {

	public CalloutDao() {
		super("sb_callouts");
	}

	@Override
	public void insert(Callout dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Callout dto) {
		replaceDto(dto);
	}

	@Override
	protected Callout buildDto(Cursor cursor) {
		Callout dto = new Callout();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setServiceLocationId(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		dto.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setCallOutTypeId(cursor.getString(cursor.getColumnIndexOrThrow("callout_type_id")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

	@Override
	public Callout buildDto(JSONObject json) throws JSONException {
		Callout dto = new Callout();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setServiceLocationId(jsonParser.parseString(json, "service_location_id"));
		dto.setDateTime(jsonParser.parseDate(json, "date_time"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setCallOutTypeId(jsonParser.parseString(json, "callout_type_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		
		return dto;
	}

}
