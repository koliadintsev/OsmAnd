package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

/**
 * @author dounaka
 *
 */
public class WorksheetLabourDao extends WorksheetBaseDao<WorksheetLabour> {

	public WorksheetLabourDao() {
		super("sb_worksheet_labours");
	}

	@Override
	public WorksheetLabour buildDto(JSONObject json) throws JSONException {
		WorksheetLabour dto = new WorksheetLabour();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setWorksheetId(jsonParser.parseString(json, "worksheet_id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setProductId(jsonParser.parseString(json, "product_id"));
		dto.setLabourDate(jsonParser.parseString(json, "date"));
		dto.setHours(jsonParser.parseFloat(json, "hours"));
		dto.setCreatorId(jsonParser.parseString(json, "creator_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	@Override
	protected WorksheetLabour buildDto(Cursor cursor) {
		WorksheetLabour worksheetLabour = new WorksheetLabour();
		worksheetLabour.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		worksheetLabour.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		worksheetLabour.setWorksheetId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		worksheetLabour.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		worksheetLabour.setProductId(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		worksheetLabour.setLabourDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		worksheetLabour.setHours(cursor.getFloat(cursor.getColumnIndexOrThrow("hours")));
		worksheetLabour.setCreatorId((cursor.getString(cursor.getColumnIndexOrThrow("creator_id"))));
		worksheetLabour.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		worksheetLabour.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return worksheetLabour;
	}

}
