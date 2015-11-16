package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.operasoft.snowboard.util.Session;

public class ImeiCompanyDao extends Dao<ImeiCompany> {

	public ImeiCompanyDao() {
		super("sb_imei_companies");
	}

	@Override
	public void insert(ImeiCompany dto) {
		insertDto(dto);
	}

	@Override
	public void replace(ImeiCompany dto) {
		replaceDto(dto);

		// If tab suspended on the server end by admin, forcefully logout the user
		if (isTabSuspended(Session.getImeiCompany().getImeiNo()))
			if (Session.MapAct != null)
				Session.MapAct.doLogout();

	}

	public ImeiCompany getByImei(String imeiNo) {
		final String sql = "SELECT * FROM " + table + " WHERE imei_no= '" + imeiNo + "'";
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				ImeiCompany dto = buildDto(cursor);

				return dto;
			}
		} catch (Exception e) {
			Log.e(sql, "field not found", e);
		} finally {
			if (cursor != null)
				cursor.close();

		}
		return null;

	}

	@Override
	protected ImeiCompany buildDto(Cursor cursor) {
		ImeiCompany dto = new ImeiCompany();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setImeiNo(cursor.getString(cursor.getColumnIndexOrThrow("imei_no")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setConfig(cursor.getString(cursor.getColumnIndexOrThrow("config")));
		dto.setVersion(cursor.getInt(cursor.getColumnIndexOrThrow("version")));
		dto.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		dto.setGpsConfigId(cursor.getString(cursor.getColumnIndexOrThrow("gps_config_id")));
		return dto;
	}

	@Override
	public ImeiCompany buildDto(JSONObject json) throws JSONException {
		ImeiCompany dto = new ImeiCompany();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setImeiNo(jsonParser.parseString(json, "imei_no"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setType(jsonParser.parseString(json, "type"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setConfig(jsonParser.parseString(json, "config"));
		dto.setVersion(jsonParser.parseInt(json, "version"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		dto.setStatus(jsonParser.parseInt(json, "status"));
		dto.setGpsConfigId(jsonParser.parseString(json, "gps_config_id"));

		return dto;
	}

	/**
	 * it will check if this device is suspended by admin or not
	 * 
	 * @param imei
	 * @return
	 */
	public boolean isTabSuspended(String imei) {

		// logAvailableTablet();
		String sql = "SELECT * FROM " + table + " WHERE imei_no= '" + imei + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			try {
				cursor.moveToFirst();
				ImeiCompany dto = buildDto(cursor);
				cursor.close();
				return dto.getStatus() == ImeiCompany.SUSPENDED ? true : false;
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		return true;
	}

	public void logAvailableTablet() {
		String sql = "SELECT * FROM " + table;
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				try {
					ImeiCompany dto = buildDto(cursor);
					Log.d("IMEI", dto.toString());
				} catch (Exception e) {
					Log.e(sql, "field not found", e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
}
