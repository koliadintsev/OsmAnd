package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class LoginSessionDao extends Dao<LoginSession> {

	public LoginSessionDao() {
		super("sb_login_sessions");
	}
	
	@Override
	public void insert(LoginSession dto) {
		String sql = "INSERT INTO " + table + " (id, imei_companies_id, start_datetime, end_datetime, user_id, vehicle_id, latitude, longitude, sync_flag) "
				    + "VALUES ('" + dto.getId() + "','" + dto.getImei() + "','" + dto.getStart_datetime() + "','" + dto.getEnd_datetime()
    				+ "','" + dto.getUserId() + "','" + dto.getVehicleId() + "'," + dto.getLatitude() + "," + dto.getLongitude() + "," + dto.getSyncFlag() + ")";
				
		Log.v("SQL INSERT LoginSession", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	public void replace(LoginSession dto) {
		String sql = "UPDATE " + table + " SET imei_companies_id =  '" + dto.getImei() + "', start_datetime = '" + dto.getStart_datetime() + "' ,"
				+ "end_datetime = '" + dto.getEnd_datetime() + "', user_id =  '" + dto.getUserId() + "', vehicle_id = '" + dto.getVehicleId() + "', latitude = " + dto.getLatitude() + ", "
				+ "longitude =  " + dto.getLongitude() + ", sync_flag =  " + dto.getSyncFlag()
				+ " WHERE id =  '" + dto.getId() + "'";
		Log.v("SQL UPDATE LoginSession", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	/**
	 * Returns the latest session opened in our database
	 * @return
	 */
	public LoginSession getLatestSession() {
		String sql = "SELECT * FROM " + table + " ORDER BY start_datetime DESC LIMIT 1;";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			try {
				LoginSession dto = buildDto(cursor);
				cursor.close();
				return dto;
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		return new LoginSession();
	}

	@Override
	protected LoginSession buildDto(Cursor cursor) {
		LoginSession ls = new LoginSession();
		ls.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		ls.setImei(cursor.getString(cursor.getColumnIndexOrThrow("imei_companies_id")));
		ls.setStart_datetime(cursor.getString(cursor.getColumnIndexOrThrow("start_datetime")));
		ls.setEnd_datetime(cursor.getString(cursor.getColumnIndexOrThrow("end_datetime")));
		ls.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		ls.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		ls.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		ls.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		ls.setSession_status(cursor.getString(cursor.getColumnIndexOrThrow("session_status")));
		ls.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return ls;
	}

	@Override
	public LoginSession buildDto(JSONObject json) throws JSONException {
		LoginSession dto = new LoginSession();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setImei(jsonParser.parseString(json, "imei_companies_id"));
		dto.setStart_datetime(jsonParser.parseDate(json, "start_datetime"));
		dto.setEnd_datetime(jsonParser.parseDate(json, "end_datetime"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setLatitude(jsonParser.parseDouble(json, "latitude"));
		dto.setLongitude(jsonParser.parseDouble(json, "longitude"));
		dto.setSession_status(jsonParser.parseString(json, "session_status"));
		
		return dto;
	}
}
