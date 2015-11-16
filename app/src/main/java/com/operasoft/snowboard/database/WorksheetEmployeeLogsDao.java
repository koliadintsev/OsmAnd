package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorksheetEmployeeLogsDao extends Dao<WorksheetEmployeeLogs> {

	public WorksheetEmployeeLogsDao() {
		super("sb_worksheet_employee_logs");
	}

	@Override
	public void insert(WorksheetEmployeeLogs dto) {
		insertDto(dto);
	}

	@Override
	public void replace(WorksheetEmployeeLogs dto) {
		replaceDto(dto);
	}

	@Override
	protected WorksheetEmployeeLogs buildDto(Cursor cursor) {
		WorksheetEmployeeLogs WorksheetEmployeeLogs = new WorksheetEmployeeLogs();

		WorksheetEmployeeLogs.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		WorksheetEmployeeLogs.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		WorksheetEmployeeLogs.setWorksheet_id(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		WorksheetEmployeeLogs.setEmp_id(cursor.getString(cursor.getColumnIndexOrThrow("emp_id")));
		WorksheetEmployeeLogs.setEmp_name(cursor.getString(cursor.getColumnIndexOrThrow("emp_name")));
		WorksheetEmployeeLogs.setPunch_in(cursor.getString(cursor.getColumnIndexOrThrow("punch_in")));
		WorksheetEmployeeLogs.setPunch_out(cursor.getString(cursor.getColumnIndexOrThrow("punch_out")));
		WorksheetEmployeeLogs.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		return WorksheetEmployeeLogs;
	}

	@Override
	public WorksheetEmployeeLogs buildDto(JSONObject json) throws JSONException {
		WorksheetEmployeeLogs dto = new WorksheetEmployeeLogs();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setWorksheet_id(jsonParser.parseString(json, "worksheet_id"));
		dto.setEmp_id(jsonParser.parseString(json, "emp_id"));
		dto.setEmp_name(jsonParser.parseString(json, "emp_name"));
		dto.setPunch_in(jsonParser.parseString(json, "punch_in"));
		dto.setPunch_out(jsonParser.parseString(json, "punch_out"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		return dto;
	}

	public void replaceWorksheetId(String id, String newId) {
		String sql = "UPDATE " + table + " SET worksheet_id = '" + newId + "' WHERE worksheet_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	// public void replaceWorksheetMaintenenceId(String id, String newId) {
	// String sql = "UPDATE " + table + " SET worksheet_id = '" + newId +
	// "' WHERE worksheet_maintenance_id = '" + id
	// + "';";
	// DataBaseHelper.getDataBase().rawQuery(sql, null);
	// }

	public ArrayList<WorksheetEmployeeLogs> getListAttachedWithWorksheet(String worsheetId) {
		ArrayList<WorksheetEmployeeLogs> list = new ArrayList<WorksheetEmployeeLogs>();

		String sql = "SELECT * FROM " + table + " where worksheet_id = '" + worsheetId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				WorksheetEmployeeLogs dto = buildDto(cursor);
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
