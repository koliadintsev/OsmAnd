package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.operasoft.snowboard.util.Config;

public class TransportSupplierDao extends Dao<TransportSupplier> {
	public TransportSupplierDao() {
		super("sb_transport_suppliers");
	}

	public List<TransportSupplier> listSorted() {
		List<TransportSupplier> list = new ArrayList<TransportSupplier>();

		String sql = "SELECT * FROM " + table +" WHERE deleted = 0 ORDER BY LOWER(name) ASC;";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				TransportSupplier dto = buildDto(cursor);
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
	
	@Override
	public void insert(TransportSupplier dto) {
		insertDto(dto);
	}

	@Override
	public void replace(TransportSupplier dto) {
		replaceDto(dto);
	}

	@Override
	protected TransportSupplier buildDto(Cursor cursor) {
		TransportSupplier dto = new TransportSupplier();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
		dto.setCreated(cursor.getString((cursor.getColumnIndexOrThrow("created"))));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

	@Override
	public TransportSupplier buildDto(JSONObject json) throws JSONException {
		TransportSupplier dto = new TransportSupplier();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setDeleted(jsonParser.parseInt(json, "deleted"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
