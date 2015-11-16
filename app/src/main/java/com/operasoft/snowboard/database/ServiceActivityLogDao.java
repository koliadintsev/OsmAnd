package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class ServiceActivityLogDao extends Dao<ServiceActivityLog> {

	public ServiceActivityLogDao() {
		super("sb_service_activity_logs");
	}

	@Override
	public void insert(ServiceActivityLog dto) {
		String sql = "INSERT INTO "
				+ table
				+ "('id', 'company_id', 'service_activity_id', 'status_code_id', 'contact_id', 'contract_id', 'vehicle_id', 'user_id', 'date_time', 'gps_coordinates', 'created', 'modified', 'sync_flag') VALUES ('"
				+ dto.getId() + "', '" + dto.getCompanyId() + "','" + dto.getServiceActivityId() + "','" + dto.getStatus() + "','"
				+ dto.getContactId() + "','" + dto.getContractId() + "','" + dto.getVehicleId() + "','" + dto.getUserId() + "','" + dto.getDateTime()
				+ "','" + dto.getLatLong() + "','" + dto.getCreated() + "','" + dto.getModified() + "','" + dto.getSyncFlag() + "' )";

		Log.v("SQL INSERT SA Log", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	public void replace(ServiceActivityLog dto) {
		String sql = "UPDATE " + table + " SET company_id = '" + dto.getCompanyId() + "',service_activity_id= '" + dto.getServiceActivityId()
				+ "',status_code_id= '" + dto.getStatus() + "',contact_id= '" + dto.getContactId() + "',contract_id= '" + dto.getContractId()
				+ "',vehicle_id= '" + dto.getVehicleId() + "',user_id= '" + dto.getUserId() + "',date_time= '" + dto.getDateTime()
				+ "',gps_coordinates= '" + dto.getLatLong() + "',created= '" + dto.getCreated() + "',modified= '" + dto.getModified()
				+ "',sync_flag= '" + dto.getSyncFlag() + "' WHERE id =  '" + dto.getId() + "'";
		Log.v("SQL UPDATE SA Log", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	public List<ServiceActivityLog> listAllForServiceActivity(String serviceActivityId) {
		List<ServiceActivityLog> list = new ArrayList<ServiceActivityLog>();

		String sql = "SELECT * FROM " + table + " WHERE service_activity_id = '" + serviceActivityId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				ServiceActivityLog dto = buildDto(cursor);
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

	public void replaceServiceActivityId(String id, String newId) {
		String sql = "UPDATE " + table + " SET service_activity_id = '" + newId + "' WHERE service_activity_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	protected ServiceActivityLog buildDto(Cursor cursor) {
		ServiceActivityLog dto = new ServiceActivityLog();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setServiceActivityId(cursor.getString(cursor.getColumnIndexOrThrow("service_activity_id")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setContactId(cursor.getString(cursor.getColumnIndexOrThrow("contact_id")));
		dto.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setLatLong(cursor.getString(cursor.getColumnIndexOrThrow("gps_coordinates")));
		dto.setCreated(cursor.getString((cursor.getColumnIndexOrThrow("created"))));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

	@Override
	public ServiceActivityLog buildDto(JSONObject json) throws JSONException {
		ServiceActivityLog dto = new ServiceActivityLog();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setServiceActivityId(jsonParser.parseString(json, "service_activity_id"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setContactId(jsonParser.parseString(json, "contact_id"));
		dto.setContractId(jsonParser.parseString(json, "contract_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setDateTime(jsonParser.parseString(json, "date_time"));
		dto.setLatLong(jsonParser.parseString(json, "gps_coordinates"));
		dto.setCreated(jsonParser.parseString(json, "created"));
		dto.setModified(jsonParser.parseString(json, "modified"));
		
		return dto;
	}


}
