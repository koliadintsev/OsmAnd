package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class SiteDao extends Dao<Site> {

	public SiteDao() {
		super("sb_sites");
	}
	
	@Override
	public void insert(Site dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Site dto) {
		replaceDto(dto);
	}

	@Override
	public Site buildDto(JSONObject json) throws JSONException {
		Site dto = new Site();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setDeleted(jsonParser.parseInt(json, "deleted"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		
		return dto;
	}

	@Override
	protected Site buildDto(Cursor cursor) {
		Site dto = new Site();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

}
