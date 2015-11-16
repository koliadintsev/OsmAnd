package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class VehicleInspectionItemDao extends Dao<VehicleInspectionItem> {

	public VehicleInspectionItemDao() {
		super("sb_vehicle_inspection_items");
	}
	
	@Override
	public void insert(VehicleInspectionItem dto) {
		insertDto(dto);
	}

	@Override
	public void replace(VehicleInspectionItem dto) {
		replaceDto(dto);
	}

	@Override
	protected VehicleInspectionItem buildDto(Cursor cursor) {
		VehicleInspectionItem dto = new VehicleInspectionItem();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public VehicleInspectionItem buildDto(JSONObject json) throws JSONException {
		VehicleInspectionItem dto = new VehicleInspectionItem();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setType(jsonParser.parseString(json, "type"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
