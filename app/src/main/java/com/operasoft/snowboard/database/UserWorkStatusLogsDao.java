package com.operasoft.snowboard.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class UserWorkStatusLogsDao extends Dao<UserWorkStatusLogs> {

	public UserWorkStatusLogsDao() {
		super("sb_user_work_status_logs");
	}

	@Override
	public void insert(UserWorkStatusLogs dto) {
		insertDto(dto);
	}

	@Override
	public void replace(UserWorkStatusLogs dto) {
		replaceDto(dto);
	}

	@Override
	protected UserWorkStatusLogs buildDto(Cursor cursor) {
		UserWorkStatusLogs dto = new UserWorkStatusLogs();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setOperation(cursor.getString(cursor.getColumnIndexOrThrow("operation")));
		dto.setWorkStatus(cursor.getString(cursor.getColumnIndexOrThrow("work_status")));
		dto.setImei(cursor.getString(cursor.getColumnIndexOrThrow("imei")));
		dto.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setServiceLocationId(cursor.getString(cursor.getColumnIndexOrThrow("service_location_id")));
		dto.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		dto.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		dto.setAccepted(cursor.getInt(cursor.getColumnIndexOrThrow("accepted")));
		dto.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public UserWorkStatusLogs buildDto(JSONObject json) throws JSONException {
		UserWorkStatusLogs dto = new UserWorkStatusLogs();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setOperation(jsonParser.parseString(json, "operation"));
		dto.setWorkStatus(jsonParser.parseString(json, "work_status"));
		dto.setImei(jsonParser.parseString(json, "imei"));
		dto.setDateTime(jsonParser.parseDate(json, "date_time"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setServiceLocationId(jsonParser.parseString(json, "service_location_id"));
		dto.setLatitude(jsonParser.parseDouble(json, "latitude"));
		dto.setLongitude(jsonParser.parseDouble(json, "longitude"));
		dto.setAccepted(jsonParser.parseInt(json, "accepted"));
		dto.setCreatorId(jsonParser.parseDate(json, "creator_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public UserWorkStatusLogs getLastWorkStatusLogs(String userId) {

		UserWorkStatusLogs dto = null;
		String sql = "select * from " + table + " where user_id = '" + userId + "' order by date_time desc limit 1";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				dto = buildDto(cursor);
			}
		}

		cursor.close();
		return dto;
	}

	// SELECT * FROM sb_user_work_status_logs WHERE service_location_id = '52a24b73-8d54-4d1c-9d09-6530ae8ed672'  and created>='2014-03-27';
	public ArrayList<UserWorkStatusLogs> getDropOff(String contractId, String startDate) {
		ArrayList<UserWorkStatusLogs> list = new ArrayList<UserWorkStatusLogs>();
		final Contract contract = (new ContractsDao()).getById(contractId);
		if (contract == null)
			return list;
		final String serviceLocationId = contract.getService_location_id();
		String sql = "SELECT * FROM " + table + " WHERE service_location_id = '" + serviceLocationId + "' and created>='" + startDate + "' and operation ='dropOff' and work_status='onsite' ";
		Cursor cursor = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				UserWorkStatusLogs dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			}
		} catch (Exception e) {
			Log.e(sql, "field not found", e);
		} finally {
			cursor.close();
		}
		return list;
	}
}
