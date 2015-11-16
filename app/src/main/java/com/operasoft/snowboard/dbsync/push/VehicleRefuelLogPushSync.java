package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.VehicleRefuelLog;
import com.operasoft.snowboard.database.VehicleRefuelLogsDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class VehicleRefuelLogPushSync extends AbstractPushSync<VehicleRefuelLog> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private VehicleRefuelLogsDao dao = null;

	// Singleton pattern
	static private VehicleRefuelLogPushSync instance_s = new VehicleRefuelLogPushSync();
	
	static public VehicleRefuelLogPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new VehicleRefuelLogPushSync();
		}
		
		return instance_s;
	}
	
	private VehicleRefuelLogPushSync() {
		super("VehicleRefuelLog");
	}
	
	@Override
	protected Dao<VehicleRefuelLog> getDao() {
		if (dao == null) {
			dao = new VehicleRefuelLogsDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params,
			VehicleRefuelLog dto) {
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[date]", dto.getDate()));
		params.add(new BasicNameValuePair(model + "[amount]", String.valueOf(dto.getAmount())));
		params.add(new BasicNameValuePair(model + "[volume]", String.valueOf(dto.getVolume())));
		params.add(new BasicNameValuePair(model + "[volume_unit]", dto.getVolumeUnit()));
		params.add(new BasicNameValuePair(model + "[engine_hours]", String.valueOf(dto.getEngineHours())));
		params.add(new BasicNameValuePair(model + "[latitude]", String.valueOf(dto.getLatitude())));
		params.add(new BasicNameValuePair(model + "[longitude]", String.valueOf(dto.getLongitude())));
		params.add(new BasicNameValuePair(model + "[odometer]", dto.getOdometer()));
	}

	@Override
	protected boolean processServerResponse(String value, VehicleRefuelLog dto)
			throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("VehicleRefuelLogPushSync", "No details received from server: " + value);
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}

			return true;
		}

		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		JSONArray jsArray = new JSONArray(value);
		if (jsArray.length() > 0) {
			JSONObject jsonObject = jsArray.getJSONObject(0);
			VehicleRefuelLog snowmanDto = dtoParser.parseSnowmanRefuel(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			return true;
		}

		Log.e("VehicleRefuelLogPushSync", "No object found in response: " + value);
		return false;
	}

}
