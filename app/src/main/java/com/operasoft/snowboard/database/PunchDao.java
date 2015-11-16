package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.util.Session;

public class PunchDao extends Dao<Punch> {

	public PunchDao() {
		super("sb_punch");
	}

	@Override
	public void insert(Punch dto) {
		String sql = "INSERT INTO " + table + " (id, imei_companies_id, date_time, user_id, vehicle_id, latitude, longitude, operation, service_location_id, sync_flag) " + "VALUES ('" + dto.getId()
				+ "', '" + dto.getImei() + "','" + dto.getDateTime() + "','" + dto.getUserId() + "','" + dto.getVehicleId() + "'" + "," + dto.getLatitude() + "," + dto.getLongitude() + ",'"
				+ dto.getOperation() + "','" + dto.getServiceLocationId() + "'," + dto.getSyncFlag() + ")";

		Log.v("SQL INSERT Punch", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	public void replace(Punch dto) {
		String sql = "UPDATE " + table + " SET imei_companies_id =  '" + dto.getImei() + "', date_time = '" + dto.getDateTime() + "' ," + "user_id =  '" + dto.getUserId() + "', vehicle_id = '"
				+ dto.getVehicleId() + "', latitude = " + dto.getLatitude() + ", " + "longitude =  " + dto.getLongitude() + ", operation =  '" + dto.getOperation() + ", service_location_id =  '"
				+ dto.getServiceLocationId() + "', sync_flag =  " + dto.getSyncFlag() + " WHERE id =  '" + dto.getId() + "'";
		Log.v("SQL UPDATE Punch", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	protected Punch buildDto(Cursor cursor) {
		Punch punch = new Punch();
		punch.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		punch.setImei(cursor.getString(cursor.getColumnIndexOrThrow("imei_companies_id")));
		punch.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		punch.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		punch.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		punch.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		punch.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		punch.setOperation(cursor.getString(cursor.getColumnIndexOrThrow("operation")));
		punch.setServiceLocationId(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		punch.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		return punch;
	}

	@Override
	public Punch buildDto(JSONObject json) throws JSONException {
		Punch dto = new Punch();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setImei(jsonParser.parseString(json, "imei_companies_id"));
		dto.setDateTime(jsonParser.parseDate(json, "date_time"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setLatitude(jsonParser.parseDouble(json, "latitude"));
		dto.setLongitude(jsonParser.parseDouble(json, "longitude"));
		dto.setOperation(jsonParser.parseString(json, "operation"));
		dto.setServiceLocationId(jsonParser.parseString(json, "service_location_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	/**
	 * return SLId of location from where user punched last time
	 * 
	 * @param id
	 * @return SLId
	 */
	public String getLastSLIdFromUserId(String usedId) {
		String slId = "";
		String sql = "SELECT * FROM " + table + " where user_id = '" + usedId + "' ORDER BY date_time DESC limit 1";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			try {
				Punch dto = buildDto(cursor);
				if (dto != null) {
					slId = dto.getServiceLocationId();
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		return slId;
	}

	/**
	 * get list of all the employees for specific date in same service location.
	 * 
	 * @param date
	 *            (date)
	 * @param SlId
	 *            (Service Location ID)
	 * @return users list
	 */
	public List<Punch> listTeamEmployees(String date, String SlId) {
		List<Punch> list = new ArrayList<Punch>();
		ArrayList<String> userIdList = new ArrayList<String>();

		String sql = "SELECT * FROM " + table + " where operation = 'In' AND date_time BETWEEN '" + date + " 00:00:00.00' AND '" + date + " 23:59:59.999' AND service_location_id = '" + SlId
				+ "' order by date_time desc";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				Punch dto = buildDto(cursor);
				if (dto != null) {
					if (!userIdList.contains(dto.getUserId())) {
						userIdList.add(dto.getUserId());
						list.add(dto);
					}
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return list;
	}

	/**
	 * return punch out time for user who punched in same service location.
	 * 
	 * @param date
	 * @param slId
	 * @param userId
	 * @param inTime
	 * @return punch time
	 */

	public String getOutTimefromUserId(String date, String slId, String userId, String inTime) {
		String dateTime = "";
		String sql = "SELECT * FROM " + table + " where user_id = '" + userId + "' and operation = 'Out' and date_time BETWEEN '" + date + " 00:00:00.00' AND '" + date
				+ " 23:59:59.999' AND date_time > '" + inTime + "' AND service_location_id = '" + slId + "' order by date_time desc limit 1";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			try {
				Punch dto = buildDto(cursor);
				if (dto != null) {
					dateTime = dto.getDateTime();
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return dateTime;
	}

	/**
	 * return Punch dto for a user
	 * 
	 * @param userId
	 * @return punch dto
	 */
	public Punch getPunchFromUserId(String userId) {
		Punch punch = null;

		String sql = "SELECT * FROM " + table + " where operation = 'In' and user_id = '" + userId + "' order by date_time DESC limit 1";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			try {
				Punch dto = buildDto(cursor);
				if (dto != null) {
					punch = dto;
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return punch;
	}

	/**
	 * get list of all the employees for specific date
	 * 
	 * @param date
	 *            (date)
	 * @return users list
	 */
	public List<Punch> listEmployees(String date, String imei) {
		List<Punch> list = new ArrayList<Punch>();
		ArrayList<String> userIdList = new ArrayList<String>();

		String sql = "SELECT * FROM " + table + " where operation = 'In' and date_time BETWEEN '" + date + " 00:00:00.00' AND '" + date + " 23:59:59.999'AND imei_companies_id = '" + imei + "'"
				+ "order by date_time desc";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		while (cursor.moveToNext()) {
			try {
				Punch dto = buildDto(cursor);
				if (dto != null) {
					if (!userIdList.contains(dto.getUserId())) {
						userIdList.add(dto.getUserId());
						list.add(dto);
					}
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return list;
	}

	/**
	 * return punch out time for user who punched in same service location.
	 * 
	 * @param date
	 * @param slId
	 * @param userId
	 * @param inTime
	 * @return punch time
	 */

	public String getOutTimefromUserId(String date, String userId, String inTime) {
		String dateTime = "";
		String sql = "SELECT * FROM " + table + " where user_id = '" + userId + "' and operation = 'Out' AND date_time BETWEEN '" + date + " 00:00:00.00' AND '" + date
				+ " 23:59:59.999' AND date_time > '" + inTime + "' order by date_time desc limit 1";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			try {
				Punch dto = buildDto(cursor);
				if (dto != null) {
					dateTime = dto.getDateTime();
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return dateTime;
	}

}
