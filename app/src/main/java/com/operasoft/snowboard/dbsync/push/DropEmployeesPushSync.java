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
import com.operasoft.snowboard.database.DropEmployees;
import com.operasoft.snowboard.database.DropEmployeesDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class DropEmployeesPushSync extends AbstractPushSync<DropEmployees> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private DropEmployeesDao dao = new DropEmployeesDao();

	// Singleton pattern
	static private DropEmployeesPushSync instance_s = new DropEmployeesPushSync();

	static public DropEmployeesPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new DropEmployeesPushSync();
		}

		return instance_s;
	}

	public DropEmployeesPushSync() {
		super("DropEmployee");
	}

	@Override
	protected Dao<DropEmployees> getDao() {
		if (dao == null) {
			dao = new DropEmployeesDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, DropEmployees dto) {
		params.add(new BasicNameValuePair(model + "[0]" + "[employee_id]", dto.getEmployeeId()));
		params.add(new BasicNameValuePair(model + "[0]" + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[0]" + "[pick_time]", dto.getPickTime()));
		params.add(new BasicNameValuePair(model + "[0]" + "[drop_time]", dto.getDropTime()));
		params.add(new BasicNameValuePair(model + "[0]" + "[service_location_id]", dto.getServiceLocationId()));
		params.add(new BasicNameValuePair(model + "[0]" + "[operation]", dto.getOperation()));
		params.add(new BasicNameValuePair(model + "[0]" + "[contract_id]", dto.getContractId()));
		params.add(new BasicNameValuePair(model + "[0]" + "[worksheet_maintenance_id]", dto.getWorksheetMaintenanceId()));
		params.add(new BasicNameValuePair(model + "[0]" + "[latitude]", dto.getLatitude()));
		params.add(new BasicNameValuePair(model + "[0]" + "[longitude]", dto.getLongitude()));
	}

	@Override
	protected boolean processServerResponse(String value, DropEmployees dto) throws JSONException {
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
			DropEmployees dropEmployeesDto = dtoParser.parseDropEmployees(jsonObject, model);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(dropEmployeesDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(dropEmployeesDto.getId());
			}
			return true;
		}

		Log.e("DropEmployeePushSync", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveClearDto(DropEmployees dto) {
		dao.insertOrReplace(dto);
	}

	@Override
	protected void saveDirtyDto(DropEmployees dto) {
		dao.markAsDirty(dto);
	}

	@Override
	protected void clearDirtyDto(DropEmployees dto) {
		dao.clearDirtyDto(dto);
	}

}
