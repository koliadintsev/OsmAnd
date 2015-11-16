package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

/**
 * @author dounaka
 *
 */
public class WorksheetTravelTimeDao extends WorksheetBaseDao<WorksheetTravelTime> {

	public WorksheetTravelTimeDao() {
		super("sb_worksheet_travel_times");
	}

	@Override
	public WorksheetTravelTime buildDto(JSONObject json) throws JSONException {
		WorksheetTravelTime wTravelTime = new WorksheetTravelTime();
		wTravelTime.setId(jsonParser.parseString(json, "id"));
		wTravelTime.setCompanyId(jsonParser.parseString(json, "company_id"));
		wTravelTime.setWorksheetId(jsonParser.parseString(json, "worksheet_id"));
		wTravelTime.setUserId(jsonParser.parseString(json, "user_id"));
		wTravelTime.setTravelDate(jsonParser.parseString(json, "date"));
		wTravelTime.setHours(jsonParser.parseInt(json, "hours"));
		wTravelTime.setCreatorId(jsonParser.parseString(json, "creator_id"));
		wTravelTime.setCreated(jsonParser.parseDate(json, "created"));
		wTravelTime.setModified(jsonParser.parseDate(json, "modified"));

		return wTravelTime;
	}

	@Override
	protected WorksheetTravelTime buildDto(Cursor cursor) {
		WorksheetTravelTime wMaterial = new WorksheetTravelTime();
		wMaterial.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		wMaterial.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		wMaterial.setWorksheetId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		wMaterial.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		wMaterial.setTravelDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		wMaterial.setHours(cursor.getInt(cursor.getColumnIndexOrThrow("hours")));
		wMaterial.setCreatorId((cursor.getString(cursor.getColumnIndexOrThrow("creator_id"))));
		wMaterial.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		wMaterial.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return wMaterial;
	}

}
