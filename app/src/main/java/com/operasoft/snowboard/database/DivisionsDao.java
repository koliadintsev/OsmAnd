package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class DivisionsDao extends Dao<Divisions> {

	public DivisionsDao() {
		super("sb_divisions");
	}

	@Override
	public void insert(Divisions divisions) {
		insertDto(divisions);

	}

	@Override
	public void replace(Divisions divisions) {
		replaceDto(divisions);

	}

	@Override
	public Divisions buildDto(JSONObject json) throws JSONException {
		Divisions dto = new Divisions();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setType(jsonParser.parseString(json, "type"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	@Override
	protected Divisions buildDto(Cursor cursor) {
		Divisions dto = new Divisions();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor
				.getColumnIndexOrThrow("company_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor
				.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}
	
	/**
	 * Retrieves all the divisions associated with a Contract
	 */
	public ArrayList<Divisions> getAllDivisionsForContract(String divisionsId) {
		ArrayList<Divisions> DivisionsList = new ArrayList<Divisions>();
		String sql = "SELECT * FROM " + table + " WHERE id = '" + divisionsId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				Divisions dto = buildDto(cursor);
				if (dto != null) {
					DivisionsList.add(dto);
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return DivisionsList;
	}


}
