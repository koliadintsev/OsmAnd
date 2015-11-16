package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class WorksheetMaintenanceTasksDao extends Dao<WorksheetMaintenanceTasks> {

	public WorksheetMaintenanceTasksDao() {
		super("sb_worksheet_maintenance_tasks");
	}

	@Override
	public void insert(WorksheetMaintenanceTasks dto) {
		insertDto(dto);

	}

	@Override
	public void replace(WorksheetMaintenanceTasks dto) {
		replaceDto(dto);

	}

	@Override
	public WorksheetMaintenanceTasks buildDto(JSONObject json) throws JSONException {
		WorksheetMaintenanceTasks dto = new WorksheetMaintenanceTasks();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setTaskcode(jsonParser.parseString(json, "task_code"));
		dto.setTaskname(jsonParser.parseString(json, "task_name"));
		dto.setWorksheet_maintenance_id(jsonParser.parseString(json, "worksheet_maintenance_id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setProduct_id(jsonParser.parseString(json, "product_id"));
		dto.setContract_service_id(jsonParser.parseString(json, "contract_service_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	@Override
	protected WorksheetMaintenanceTasks buildDto(Cursor cursor) {
		WorksheetMaintenanceTasks worksheetMaintenanceTasks = new WorksheetMaintenanceTasks();
		worksheetMaintenanceTasks.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		worksheetMaintenanceTasks.setTaskcode(cursor.getString(cursor.getColumnIndexOrThrow("task_code")));
		worksheetMaintenanceTasks.setTaskname(cursor.getString(cursor.getColumnIndexOrThrow("task_name")));
		worksheetMaintenanceTasks.setProduct_id(cursor.getString(cursor.getColumnIndexOrThrow("product_id")));
		worksheetMaintenanceTasks.setContract_service_id(cursor.getString(cursor.getColumnIndexOrThrow("contract_service_id")));
		worksheetMaintenanceTasks.setWorksheet_maintenance_id(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_maintenance_id")));
		worksheetMaintenanceTasks.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		worksheetMaintenanceTasks.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		worksheetMaintenanceTasks.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return worksheetMaintenanceTasks;
	}

	public void replaceWorksheetId(String id, String newId) {
		String sql = "UPDATE " + table + " SET worksheet_maintenance_id = '" + newId + "' WHERE worksheet_maintenance_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
		
	}

	public ArrayList<WorksheetMaintenanceTasks> getListAttachedWithWorksheet(
			String worsheetId) {
		ArrayList<WorksheetMaintenanceTasks> list = new ArrayList<WorksheetMaintenanceTasks>();

		String sql = "SELECT * FROM " + table + " where worksheet_maintenance_id = '"
				+ worsheetId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				WorksheetMaintenanceTasks dto = buildDto(cursor);
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
