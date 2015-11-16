package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

@Deprecated
public class WorksheetEquipmentsOldDao extends Dao<WorksheetEquipmentsOld> {

	public WorksheetEquipmentsOldDao() {
		super("sb_worksheet_equipments");
	}

	@Override
	public void insert(WorksheetEquipmentsOld dto) {
		insertDto(dto);
	}

	@Override
	public void replace(WorksheetEquipmentsOld dto) {
		replaceDto(dto);
	}

	@Override
	protected WorksheetEquipmentsOld buildDto(Cursor cursor) {
		WorksheetEquipmentsOld WorksheetEquipmentsOld = new WorksheetEquipmentsOld();
		WorksheetEquipmentsOld.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		WorksheetEquipmentsOld.setEquipmentName(cursor.getString(cursor.getColumnIndexOrThrow("equipment_name")));
		WorksheetEquipmentsOld.setEquipmentNumber(cursor.getString(cursor.getColumnIndexOrThrow("equipment_number")));

		WorksheetEquipmentsOld.setWorksheetId(cursor.getString(cursor.getColumnIndexOrThrow("worksheet_id")));
		WorksheetEquipmentsOld.setHoursUsed(cursor.getString(cursor.getColumnIndexOrThrow("hours_used")));
		WorksheetEquipmentsOld.setEquipmentId(cursor.getString(cursor.getColumnIndexOrThrow("equipment_id")));

		WorksheetEquipmentsOld.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		WorksheetEquipmentsOld.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		WorksheetEquipmentsOld.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return WorksheetEquipmentsOld;
	}

	@Override
	public WorksheetEquipmentsOld buildDto(JSONObject json) throws JSONException {
		WorksheetEquipmentsOld dto = new WorksheetEquipmentsOld();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setEquipmentName(jsonParser.parseString(json, "equipment_name"));
		dto.setEquipmentNumber(jsonParser.parseString(json, "equipment_number"));
		dto.setWorksheetId(jsonParser.parseString(json, "worksheet_id"));
		dto.setHoursUsed(jsonParser.parseString(json, "hours_used"));
		dto.setEquipmentId(jsonParser.parseString(json, "equipment_id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public void replaceWorksheetId(String id, String newId) {
		String sql = "UPDATE " + table + " SET worksheet_id = '" + newId + "' WHERE worksheet_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public ArrayList<WorksheetEquipmentsOld> getListAttachedWithWorksheet(String worsheetId) {
		ArrayList<WorksheetEquipmentsOld> list = new ArrayList<WorksheetEquipmentsOld>();

		String sql = "SELECT * FROM " + table + " where worksheet_id = '" + worsheetId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				WorksheetEquipmentsOld dto = buildDto(cursor);
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