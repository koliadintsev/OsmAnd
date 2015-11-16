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
import com.operasoft.snowboard.database.MarkerInstallation;
import com.operasoft.snowboard.database.MarkerInstallationDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class MarkerInstallationPushSync extends AbstractPushSync<MarkerInstallation> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private MarkerInstallationDao dao = null;

	// Singleton pattern
	static private MarkerInstallationPushSync instance_s = new MarkerInstallationPushSync();

	static public MarkerInstallationPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new MarkerInstallationPushSync();
		}

		return instance_s;
	}

	private MarkerInstallationPushSync() {
		super("MarkerInstallation");
		actionParam = new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "update");
	}

	@Override
	protected Dao<MarkerInstallation> getDao() {
		if (dao == null) {
			dao = new MarkerInstallationDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, MarkerInstallation dto) {
		params.add(new BasicNameValuePair("MarkerInstallation" + "[" + "id" + "]", dto.getId()));
		params.add(new BasicNameValuePair("MarkerInstallation" + "[" + "status_id" + "]", dto.getStatus()));
		params.add(new BasicNameValuePair("MarkerInstallation" + "[" + "user_id" + "]", dto.getUserId()));
		params.add(new BasicNameValuePair("MarkerInstallation" + "[" + "date_time" + "]", dto.getDateTime()));
	}

	@Override
	protected boolean processServerResponse(String value, MarkerInstallation dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("MI Push", "No details received from server: " + value);
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
			return true;
		} 

		Log.e("MI Push", "No object found in response: " + value);
		return false;
	}

}
