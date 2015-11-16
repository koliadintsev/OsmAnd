package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class CalloutTypeDao extends Dao<CalloutType> {

	public CalloutTypeDao() {
		super("sb_callout_types");
	}

	@Override
	public void insert(CalloutType dto) {
		insertDto(dto);

	}

	@Override
	public void replace(CalloutType dto) {
		replaceDto(dto);

	}

	@Override
	public CalloutType buildDto(JSONObject json) throws JSONException {
		CalloutType dto = new CalloutType();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));

		return dto;
	}

	@Override
	protected CalloutType buildDto(Cursor cursor) {
		CalloutType dto = new CalloutType();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	public ArrayList<CalloutType> listCalloutTypes() {
		ArrayList<CalloutType> list = new ArrayList<CalloutType>();

		String sql = "SELECT * FROM " + table;
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			CalloutType dto = buildDto(cursor);
			if (dto != null) {
				list.add(dto);
			}
		}
		cursor.close();
		return list;
	}

}
