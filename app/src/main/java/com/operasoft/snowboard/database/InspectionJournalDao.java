package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class InspectionJournalDao extends Dao<InspectionJournal> {

	public InspectionJournalDao() {
		super("sb_inspection_journals");
	}
	
	@Override
	public void insert(InspectionJournal dto) {
		insertDto(dto);
	}

	@Override
	public void replace(InspectionJournal dto) {
		replaceDto(dto);
	}

	@Override
	protected InspectionJournal buildDto(Cursor cursor) {
		InspectionJournal dto = new InspectionJournal();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public InspectionJournal buildDto(JSONObject json) throws JSONException {
		InspectionJournal dto = new InspectionJournal();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setDate(jsonParser.parseDate(json, "date"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setType(jsonParser.parseString(json, "type"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
