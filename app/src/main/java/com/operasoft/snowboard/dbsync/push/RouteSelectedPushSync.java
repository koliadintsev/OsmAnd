package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Damage;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.RouteSelected;
import com.operasoft.snowboard.database.RouteSelectedDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class RouteSelectedPushSync extends AbstractPushSync<RouteSelected> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private RouteSelectedDao dao = new  RouteSelectedDao();

	static private RouteSelectedPushSync instance_sm = new RouteSelectedPushSync();

	static public RouteSelectedPushSync getInstance() {
		if (instance_sm == null) {
			instance_sm = new RouteSelectedPushSync();
		}
		return instance_sm;
	}

	public RouteSelectedPushSync() {
		super("RouteSelection");
	}

	@Override
	protected Dao<RouteSelected> getDao() {
		if (dao == null) {
			dao = new RouteSelectedDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params,
			RouteSelected dto) {
		params.add(new BasicNameValuePair(model + "[company_id]", dto
				.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[date_time]", dto
				.getDateTime()));
		params.add(new BasicNameValuePair(model + "[route_id]", dto
				.getRouteId()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto
				.getVehicleId()));
	}

	@Override
	protected boolean processServerResponse(String value, RouteSelected dto) throws JSONException {
		
		if (isEmptyJsonResponse(value)) {
			Log.i("RouteSelectionPushSync", "empty JSON response received: " + value);
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
			RouteSelected route = dtoParser.parseRouteSelection(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(route.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(route.getId());
			}
			return true;
		} 

		Log.e("RouteSelectedPushSync", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveDirtyDto(RouteSelected dto) {
		dao.markAsDirty(dto);
	}

	@Override
	protected void saveClearDto(RouteSelected dto) {
		dao.insertOrReplace(dto);
	}

	@Override
	protected void clearDirtyDto(RouteSelected dto) {
		dao.clearDirtyDto(dto);
	}

}
