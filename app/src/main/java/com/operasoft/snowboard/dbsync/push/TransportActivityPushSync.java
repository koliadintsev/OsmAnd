package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Callout;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.TransportActivityDao;
import com.operasoft.snowboard.database.TransportActivity;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class TransportActivityPushSync extends AbstractPushSync<TransportActivity> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private TransportActivityDao taDao = null;

	// Singleton pattern
	static private TransportActivityPushSync instance_s = new TransportActivityPushSync();

	static public TransportActivityPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new TransportActivityPushSync();
		}

		return instance_s;
	}

	private TransportActivityPushSync() {
		super("TransportActivity");
		getDao();
	}

	@Override
	protected Dao<TransportActivity> getDao() {
		if (taDao == null) {
			taDao = new TransportActivityDao();
		}
		return taDao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params,
			TransportActivity dto) {
		if (dto.isNew()) {
			// We are creating a brand new transport activity
			params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
			params.add(new BasicNameValuePair(model + "[start_datetime]", dto.getStartDateTime()));
			params.add(new BasicNameValuePair(model + "[start_datetime]", dto.getStartDateTime()));
			params.add(new BasicNameValuePair(model + "[start_latitude]", String.valueOf(dto.getStartLatitude())));
			params.add(new BasicNameValuePair(model + "[start_longitude]", String.valueOf(dto.getStartLongitude())));
			params.add(new BasicNameValuePair(model + "[start_user_id]", dto.getStartUserId()));
			params.add(new BasicNameValuePair(model + "[geofence_id]", dto.getGeofenceId()));
			params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
			params.add(new BasicNameValuePair(model + "[transport_vehicle_id]", dto.getTransportVehicleId()));
			params.add(new BasicNameValuePair(model + "[status_code_id]", dto.getStatus()));
		} else {
			// We are closing an existing transport activity
			params.remove(actionParam);
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "update"));

			params.add(new BasicNameValuePair(model + "[id]", dto.getId()));
			params.add(new BasicNameValuePair(model + "[end_datetime]", dto.getEndDateTime()));
			params.add(new BasicNameValuePair(model + "[end_datetime]", dto.getEndDateTime()));
			params.add(new BasicNameValuePair(model + "[end_latitude]", String.valueOf(dto.getEndLatitude())));
			params.add(new BasicNameValuePair(model + "[end_longitude]", String.valueOf(dto.getEndLongitude())));
			params.add(new BasicNameValuePair(model + "[end_user_id]", dto.getEndUserId()));
			params.add(new BasicNameValuePair(model + "[site_id]", dto.getEndUserId()));
		}
		
	}

	@Override
	protected boolean processServerResponse(String value, TransportActivity dto)
			throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("TA Push", "No details received from server: " + value);
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}

			return true;
		}

		// Let's make sure the DTO has been properly created on the server...
		// Parse the JSON data received
		JSONArray jsArray = new JSONArray(value);
		if (jsArray.length() > 0) {
			JSONObject jsonObject = jsArray.getJSONObject(0);
			TransportActivity snowmanDto = dtoParser.parseSnowmanTransportActivity(jsonObject);
			if (dto.isNew() && dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
				dto.setCreated(snowmanDto.getCreated());
			} else if (dto.isNew()) {
				// The DTO has not been inserted in our DB yet
				dto.setId(snowmanDto.getId());
				dto.setCreated(snowmanDto.getCreated());
			}
			return true;
		} 

		Log.e("TA Push", "No JSON object found in response: " + value);
		return false;
	}

}
