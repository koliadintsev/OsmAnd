package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class ActivityDao extends Dao<Activity> {

	public ActivityDao() {
		super("sb_activities");
	}

	@Override
	public void insert(Activity dto) {
		String sql = "INSERT INTO " + table
				+ " (id, company_id, service_activity_id, contract_service_id, quantity, created, modified, sync_flag) " + "VALUES ('"
				+ dto.getId() + "', '" + dto.getCompanyId() + "','" + dto.getServiceActivityId() + "','" + dto.getContractServiceId() + "'"
				+ "," + dto.getQuantity() + ",'" + dto.getCreated() + "','" + dto.getModified() + "','" + dto.getSyncFlag() + "' )";

		Log.v("SQL INSERT Activity", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	public void replace(Activity dto) {
		String sql = "UPDATE " + table + " SET service_activity_id =  '" + dto.getServiceActivityId() + "', contract_service_id = '"
				+ dto.getContractServiceId() + "', " + "quantity = '" + dto.getQuantity() + "', created =  '" + dto.getCreated()
				+ "', modified = '" + dto.getModified() + "', company_id = '" + dto.getCompanyId() + "', " + " sync_flag =  "
				+ dto.getSyncFlag() + " WHERE id =  '" + dto.getId() + "'";
		Log.v("SQL UPDATE Activities", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
		// throw new RuntimeException("Not implemented yet.");
	}

	public List<Activity> listAllForServiceActivity(String serviceActivityId) {
		List<Activity> list = new ArrayList<Activity>();

		String sql = "SELECT * FROM " + table + " WHERE service_activity_id = '" + serviceActivityId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				Activity dto = buildDto(cursor);
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
	protected Activity buildDto(Cursor cursor) {
		Activity dto = new Activity();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setServiceActivityId(cursor.getString(cursor.getColumnIndexOrThrow("service_activity_id")));
		dto.setContractServiceId(cursor.getString(cursor.getColumnIndexOrThrow("contract_service_id")));
		dto.setQuantity(cursor.getFloat(cursor.getColumnIndexOrThrow("quantity")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	
	@Override
	public Activity buildDto(JSONObject json) throws JSONException {
		Activity dto = new Activity();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setServiceActivityId(jsonParser.parseString(json, "service_activity_id"));
		dto.setContractServiceId(jsonParser.parseString(json, "contract_service_id"));
		dto.setQuantity(jsonParser.parseFloat(json, "quantity"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public void replaceServiceActivityId(String id, String newId) {
		String sql = "UPDATE " + table + " SET service_activity_id = '" + newId + "' WHERE service_activity_id = '" + id + "';";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

}
