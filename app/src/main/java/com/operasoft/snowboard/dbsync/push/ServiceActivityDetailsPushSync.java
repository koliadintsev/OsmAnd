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
import com.operasoft.snowboard.database.ServiceActivityDetails;
import com.operasoft.snowboard.database.ServiceActivityDetailsDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class ServiceActivityDetailsPushSync extends AbstractPushSync<ServiceActivityDetails> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private ServiceActivityDetailsDao dao = null;

	// Singleton pattern
	static private ServiceActivityDetailsPushSync instance_s = new ServiceActivityDetailsPushSync();

	static public ServiceActivityDetailsPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new ServiceActivityDetailsPushSync();
		}

		return instance_s;
	}

	private ServiceActivityDetailsPushSync() {
		super("ServiceActivity");
	}

	@Override
	protected Dao<ServiceActivityDetails> getDao() {
		if (dao == null) {
			dao = new ServiceActivityDetailsDao();
		}
		return dao;
	}

	protected ServiceActivityDetailsPushSync(String model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, ServiceActivityDetails dto) {
		params.add(new BasicNameValuePair(model + "[id]", dto.getService_activity_id()));
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompany_id()));

		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[company_id]", dto.getCompany_id()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[accumulation_depth]", String.valueOf(dto.getAccumulation_depth())));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[accumulation_type]", dto.getAccumulation_type()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[precipitation]", dto.getPrecipitation()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[conditions]", dto.getConditions()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[outdoor_temp]", dto.getOutdoor_temp()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[plowing_roads]", dto.getPlowing_roads()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[deice_roads]", dto.getDeice_roads()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[plowing_walkways]", dto.getPlowing_walkways()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[deice_walkways]", dto.getDeice_walkways()));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[inspection_only]", String.valueOf(dto.getInspection_only())));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[time_on_site]", String.valueOf(dto.getTime_on_site())));
		params.add(new BasicNameValuePair("ServiceActivityDetail" + "[notes]", dto.getNotes()));
	}

	@Override
	protected boolean processServerResponse(String value, ServiceActivityDetails dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("ServiceActivityDetailsSync", "No details received from server: " + value);
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
			ServiceActivityDetails snowmanDto = dtoParser.parseSnowmanSADetailsForm(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			return true;
		} 

		Log.e("ServiceActivityDetailsSync", "No ServiceActivityDetails object found in response: " + value);
		return false;
	}
}