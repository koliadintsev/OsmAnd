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
import com.operasoft.snowboard.database.Login;
import com.operasoft.snowboard.database.LoginDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class LoginPushSync extends AbstractPushSync<Login> {

	private JsonDtoParser			dtoParser	= new JsonDtoParser();
	private LoginDao				dao			= new LoginDao();

	// Singleton pattern
	static private LoginPushSync	instance_s	= new LoginPushSync();

	static public LoginPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new LoginPushSync();
		}

		return instance_s;
	}

	public LoginPushSync() {
		super("SmLogin");
	}

	@Override
	protected Dao<Login> getDao() {
		if (dao == null) {
			dao = new LoginDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Login dto) {
		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[session_id]", dto.getSessionId()));
		params.add(new BasicNameValuePair(model + "[login]", dto.getLogin()));
		params.add(new BasicNameValuePair(model + "[logout]", dto.getLogout()));
		params.add(new BasicNameValuePair(model + "[session_end_type]", dto.getSessionEndType()));
		params.add(new BasicNameValuePair(model + "[browser_type]", dto.getBrowserType()));
		params.add(new BasicNameValuePair(model + "[browser_version]", dto.getBrowserVersion()));
		params.add(new BasicNameValuePair(model + "[ip_address]", dto.getIpAddress()));

	}

	@Override
	protected boolean processServerResponse(String value, Login dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.i("LoginPushSync", "empty JSON response received: " + value);
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
			Login loginDto = dtoParser.parseLoginPushSync(jsonObject, model);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(loginDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(loginDto.getId());
			}
			return true;
		}

		Log.e("LoginPushSync", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveDirtyDto(Login dto) {
		dao.markAsDirty(dto);
	}

	@Override
	protected void saveClearDto(Login dto) {
		dao.insertOrReplace(dto);
	}

	@Override
	protected void clearDirtyDto(Login dto) {
		//dao.clearDirtyDto(dto);
	}

}
