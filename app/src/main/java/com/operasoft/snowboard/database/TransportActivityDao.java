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

public class TransportActivityDao extends Dao<TransportActivity> {
	public TransportActivityDao() {
		super("sb_transport_activities");
	}

	@Override
	public void insert(TransportActivity dto) {
		insertDto(dto);
	}

	@Override
	public void replace(TransportActivity dto) {
		replaceDto(dto);
	}

	@Override
	protected TransportActivity buildDto(Cursor cursor) {
		TransportActivity dto = new TransportActivity();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setStartDateTime(cursor.getString(cursor.getColumnIndexOrThrow("start_datetime")));
		dto.setStartLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("start_latitude")));
		dto.setStartLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("start_longitude")));
		dto.setStartUserId(cursor.getString(cursor.getColumnIndexOrThrow("start_user_id")));
		dto.setGeofenceId(cursor.getString(cursor.getColumnIndexOrThrow("geofence_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setTransportVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("transport_vehicle_id")));
		dto.setSiteId(cursor.getString(cursor.getColumnIndexOrThrow("side_id")));
		dto.setEndDateTime(cursor.getString(cursor.getColumnIndexOrThrow("end_datetime")));
		dto.setEndLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("end_latitude")));
		dto.setEndLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("end_longitude")));
		dto.setEndUserId(cursor.getString(cursor.getColumnIndexOrThrow("end_user_id")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
		dto.setCreated(cursor.getString((cursor.getColumnIndexOrThrow("created"))));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

	@Override
	public TransportActivity buildDto(JSONObject json) throws JSONException {
		TransportActivity dto = new TransportActivity();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setStartDateTime(jsonParser.parseDate(json, "start_datetime"));
		dto.setStartLatitude(jsonParser.parseDouble(json, "start_latitude"));
		dto.setStartLongitude(jsonParser.parseDouble(json, "start_longitude"));
		dto.setStartUserId(jsonParser.parseString(json, "start_user_id"));
		dto.setGeofenceId(jsonParser.parseString(json, "geofence_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setTransportVehicleId(jsonParser.parseString(json, "transport_vehicle_id"));
		dto.setSiteId(jsonParser.parseString(json, "side_id"));
		dto.setEndDateTime(jsonParser.parseDate(json, "end_datetime"));
		dto.setEndLatitude(jsonParser.parseDouble(json, "end_latitude"));
		dto.setEndLongitude(jsonParser.parseDouble(json, "end_longitude"));
		dto.setEndUserId(jsonParser.parseString(json, "end_user_id"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setDeleted(jsonParser.parseInt(json, "deleted"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
