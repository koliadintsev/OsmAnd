package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorksheetSubContractorsDao extends Dao<WorksheetSubContractors> {

	public WorksheetSubContractorsDao() {
		super("sb_worksheet_sub_contractors");
	}

	@Override
	public void insert(WorksheetSubContractors dto) {
		insertDto(dto);
	}

	@Override
	public void replace(WorksheetSubContractors dto) {
		replaceDto(dto);
	}

	@Override
	protected WorksheetSubContractors buildDto(Cursor cursor) {
		WorksheetSubContractors WorksheetSubContractors = new WorksheetSubContractors();
		WorksheetSubContractors.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));

		WorksheetSubContractors.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		WorksheetSubContractors.setWork_performence(cursor.getString(cursor.getColumnIndexOrThrow("work_performence")));
		WorksheetSubContractors.setWorksheet_id(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		WorksheetSubContractors.setSub_contractor(cursor.getString(cursor.getColumnIndexOrThrow("sub_contractor")));

		WorksheetSubContractors.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		WorksheetSubContractors.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return WorksheetSubContractors;
	}

	@Override
	public WorksheetSubContractors buildDto(JSONObject json) throws JSONException {
		WorksheetSubContractors dto = new WorksheetSubContractors();

		dto.setId(jsonParser.parseString(json, "id"));

		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setWork_performence(jsonParser.parseString(json, "work_performence"));
		dto.setWorksheet_id(jsonParser.parseString(json, "worksheet_id"));
		dto.setSub_contractor(jsonParser.parseString(json, "sub_contractor"));

		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public void replaceWorksheetId(String id, String newId) {
		String sql = "UPDATE " + table + " SET worksheet_id = '" + newId + "' WHERE worksheet_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public ArrayList<WorksheetSubContractors> getListAttachedWithWorksheet(String worsheetId) {
		ArrayList<WorksheetSubContractors> list = new ArrayList<WorksheetSubContractors>();

		String sql = "SELECT * FROM " + table + " where worksheet_id = '" + worsheetId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				WorksheetSubContractors dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return list;
	}

}