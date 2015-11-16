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
import com.operasoft.snowboard.database.DamageDao;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class DamagePushSync extends AbstractPushSync<Damage> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private DamageDao dao = null;

	// Singleton pattern
	static private DamagePushSync instance_s = new DamagePushSync();

	static public DamagePushSync getInstance() {
		if (instance_s == null) {
			instance_s = new DamagePushSync();
		}

		return instance_s;
	}

	private DamagePushSync() {
		super("Damage");
	}

	@Override
	protected Dao<Damage> getDao() {
		if (dao == null) {
			dao = new DamageDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Damage dto) {
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[contract_id]", dto.getContractId()));
		params.add(new BasicNameValuePair(model + "[description]", dto.getDescription()));
		params.add(new BasicNameValuePair(model + "[date_time]", dto.getDate()));
		params.add(new BasicNameValuePair(model + "[gps_coordinates]", dto.getGpsCoordinates()));
		params.add(new BasicNameValuePair(model + "[comments]", dto.getComments()));
		params.add(new BasicNameValuePair(model + "[foreign_key]", dto.getForeignKey()));
		params.add(new BasicNameValuePair(model + "[foreign_value]", dto.getForeignValue()));
		params.add(new BasicNameValuePair(model + "[status_code_id]", dto.getStatus()));
		params.add(new BasicNameValuePair(model + "[damage_type_id]", dto.getDamageTypeId()));
	}

	@Override
	protected boolean processServerResponse(String value, Damage dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("DamagePushSync", "empty JSON response received: " + value);
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
			Damage snowmanDto = dtoParser.parseSnowmanDamages(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			return true;
		} 

		Log.e("DamagePushSync", "No object found in response: " + value);
		return false;
	}

}
