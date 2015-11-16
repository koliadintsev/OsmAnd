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
import com.operasoft.snowboard.database.CalloutDao;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class CalloutPushSync extends AbstractPushSync<Callout> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private CalloutDao dao = null;
	
	// Singleton pattern
	static private CalloutPushSync instance_s = new CalloutPushSync();
	
	static public CalloutPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new CalloutPushSync();
		}
		
		return instance_s;
	}
	
	private CalloutPushSync() {
		super("Callout");
	}

	@Override
	protected Dao<Callout> getDao() {
		if (dao == null) {
			dao = new CalloutDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Callout dto) {
		params.add(new BasicNameValuePair(model + "[service_location_id]", dto.getServiceLocationId()));
		params.add(new BasicNameValuePair(model + "[date_time]", dto.getDateTime()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[status_code_id]", dto.getStatus()));
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[callout_type_id]", dto.getCallOutTypeId()));
	}

	@Override
	protected boolean processServerResponse(String value, Callout dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("CalloutPushSync", "empty JSON response received: " + value);
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
			Callout snowmanDto = dtoParser.parseSnowmanCallout(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			return true;
		} 

		Log.e("CalloutPushSync", "No Callout object found in response: " + value);
		return false;
	}

}
