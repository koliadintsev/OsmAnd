package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class StatusCodeDao extends Dao<StatusCode> {

	public StatusCodeDao() {
		super("sb_status_codes");
	}

	@Override
	public void insert(StatusCode dto) {
		insertDto(dto);
	}

	@Override
	public void replace(StatusCode dto) {
		replaceDto(dto);
	}

	@Override
	protected StatusCode buildDto(Cursor cursor) {
		StatusCode dto = new StatusCode();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setModel(cursor.getString(cursor.getColumnIndexOrThrow("model")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public StatusCode buildDto(JSONObject json) throws JSONException {
		StatusCode dto = new StatusCode();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setModel(jsonParser.parseString(json, "model"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
