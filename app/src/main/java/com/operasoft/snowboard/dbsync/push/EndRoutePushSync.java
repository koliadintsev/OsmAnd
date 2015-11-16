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
import com.operasoft.snowboard.database.EndRoute;
import com.operasoft.snowboard.database.EndRoutesDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class EndRoutePushSync extends AbstractPushSync<EndRoute> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private EndRoutesDao dao = new EndRoutesDao();

	// Singleton pattern
	static private EndRoutePushSync instance_s = new EndRoutePushSync();

	static public EndRoutePushSync getInstance() {
		if (instance_s == null) {
			instance_s = new EndRoutePushSync();
		}

		return instance_s;
	}

	public EndRoutePushSync() {
		super("EndRoute");
	}

	@Override
	protected Dao<EndRoute> getDao() {
		if (dao == null) {
			dao = new EndRoutesDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, EndRoute dto) {
		params.add(new BasicNameValuePair(model + "[driver_name]", dto.getDriverName()));
		params.add(new BasicNameValuePair(model + "[latitude]", dto.getLatitude()));
		params.add(new BasicNameValuePair(model + "[longitude]", dto.getLongitude()));
		params.add(new BasicNameValuePair(model + "[route_id]", dto.getRouteId()));
		params.add(new BasicNameValuePair(model + "[date_time]", dto.getDateTime()));
		params.add(new BasicNameValuePair(model + "[time_spent]", String.valueOf(dto.getTimeSpent())));
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[route_selection_id]", dto.getRoute_selection_id()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUser_id()));
	}

	@Override
	protected boolean processServerResponse(String value, EndRoute dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("EndRoutePushSync", "empty JSON response received: " + value);
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
			EndRoute endRoutesDto = dtoParser.parseEndRoutes(jsonObject, model);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(endRoutesDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(endRoutesDto.getId());
			}
			return true;
		}

		Log.e("EndRoutePushSync", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveDirtyDto(EndRoute dto) {
		dao.markAsDirty(dto);
	}

	@Override
	protected void saveClearDto(EndRoute dto) {
		dao.insertOrReplace(dto);
	}

	@Override
	protected void clearDirtyDto(EndRoute dto) {
		dao.clearDirtyDto(dto);
	}

}
