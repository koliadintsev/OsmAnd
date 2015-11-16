package com.operasoft.snowboard.database;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AssetsDao extends Dao<Assets> {
	
	public AssetsDao() {
		super("sb_assets");
	}

	@Override
	public void insert(Assets dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Assets dto) {
		replaceDto(dto);
	}

	@Override
	public Assets buildDto(JSONObject json) throws JSONException {
		Assets dto = new Assets();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));		
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		
		dto.setAsset_type_id(jsonParser.parseString(json, "asset_type_id"));
		dto.setNumber(jsonParser.parseString(json, "number"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setDescription(jsonParser.parseString(json, "description"));
		
		dto.setLatitude(jsonParser.parseDouble(json, "latitude"));
		dto.setLongitude(jsonParser.parseDouble(json, "longitude"));
		
		dto.setStatus_code_id(jsonParser.parseString(json, "status_code_id"));

		try {
			dto.setLast_inspection_date(jsonParser.parseDate(json, "last_inspection_date"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		dto.setAsset_inspection_sheet_count(jsonParser.parseString(json, "asset_inspection_sheet_count"));
		
		return dto;
	}

	@Override
	protected Assets buildDto(Cursor cursor) {
		Assets dto = new Assets();
		
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
			
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		
		dto.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		
		dto.setAsset_type_id(cursor.getString(cursor.getColumnIndexOrThrow("asset_type_id")));
		dto.setNumber(cursor.getString(cursor.getColumnIndexOrThrow("number")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		dto.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		dto.setStatus_code_id(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setLast_inspection_date(cursor.getString(cursor.getColumnIndexOrThrow("last_inspection_date")));
		dto.setAsset_inspection_sheet_count(cursor.getString(cursor.getColumnIndexOrThrow("asset_inspection_sheet_count")));
		
		
		return dto;
	}
	
	public List<Assets> getAllAssets() {
		final String sql = "SELECT * FROM " + table;
		return listDtos(sql);
	}

}
