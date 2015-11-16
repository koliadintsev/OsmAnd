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
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.LoginSessionDao;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class LoginSessionPushSync extends AbstractPushSync<LoginSession> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private LoginSessionDao dao = null;
	
	static private LoginSessionPushSync instance_sm = new LoginSessionPushSync();
	
	static public LoginSessionPushSync getInstance() {
		if (instance_sm == null) {
			instance_sm = new LoginSessionPushSync();
		}
		return instance_sm;
	}
	
	private LoginSessionPushSync() {
		super("LoginSession");
	}

	@Override
	protected Dao<LoginSession> getDao() {
		if (dao == null) {
			dao = new LoginSessionDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params,
			LoginSession dto) {
		params.add(new BasicNameValuePair(model + "[imei_companies_id]", dto.getImei()));
		params.add(new BasicNameValuePair(model + "[start_datetime]", dto.getStart_datetime()));
		params.add(new BasicNameValuePair(model + "[end_datetime]", dto.getEnd_datetime()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[latitude]", Double.toString(dto.getLatitude())));
		params.add(new BasicNameValuePair(model + "[longitude]", Double.toString(dto.getLongitude())));
		params.add(new BasicNameValuePair(model + "[session_status]", dto.getSession_status()));
	}

	@Override
	protected boolean processServerResponse(String value, LoginSession dto)
			throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("LoginSessionPushSync", "empty JSON response received: " + value);
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
			LoginSession snowmanDto = dtoParser.parseSnowmanLoginSession(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			return true;
		} 

		Log.e("LoginSessionPushSync", "No LoginSession object found in response: " + value);
		return false;
	}

}
