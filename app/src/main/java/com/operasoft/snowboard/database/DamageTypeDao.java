package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class DamageTypeDao extends Dao<DamageType> {

	public DamageTypeDao() {
		super("sb_damage_types");
	}

	@Override
	public void insert(DamageType dto) {
		insertDto(dto);
	}

	@Override
	public void replace(DamageType dto) {
		replaceDto(dto);
	}

	@Override
	protected DamageType buildDto(Cursor cursor) {
		DamageType damagetypes = new DamageType();
		damagetypes.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		damagetypes.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		damagetypes.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		damagetypes.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		damagetypes.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		return damagetypes;
	}

	@Override
	public DamageType buildDto(JSONObject json) throws JSONException {
		DamageType dto = new DamageType();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		
		return dto;
	}

}