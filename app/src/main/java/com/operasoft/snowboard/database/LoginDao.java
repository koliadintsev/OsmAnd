package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class LoginDao extends Dao<Login> {

	public LoginDao() {
		super("sb_sm_logins");
	}

	@Override
	public void insert(Login dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Login dto) {
		replaceDto(dto);
	}

	@Override
	public Login buildDto(JSONObject json) throws JSONException {
		Login dto = new Login();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setBrowserType(jsonParser.parseString(json, "browserType"));
		dto.setBrowserVersion(jsonParser.parseString(json, "browser_version"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setIpAddress(jsonParser.parseString(json, "ip_address"));
		dto.setLogin(jsonParser.parseString(json, "login"));
		dto.setLogout(jsonParser.parseString(json, "logout"));
		dto.setSessionEndType(jsonParser.parseString(json, "session_end_type"));
		dto.setSessionId(jsonParser.parseString(json, "session_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	@Override
	protected Login buildDto(Cursor cursor) {
		Login dto = new Login();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setBrowserType(cursor.getString(cursor.getColumnIndexOrThrow("browser_type")));
		dto.setBrowserVersion(cursor.getString(cursor.getColumnIndexOrThrow("browser_version")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setIpAddress(cursor.getString(cursor.getColumnIndexOrThrow("ip_address")));
		dto.setLogin(cursor.getString(cursor.getColumnIndexOrThrow("login")));
		dto.setLogout(cursor.getString(cursor.getColumnIndexOrThrow("logout")));
		dto.setSessionEndType(cursor.getString(cursor.getColumnIndexOrThrow("session_end_type")));
		dto.setSessionId(cursor.getString(cursor.getColumnIndexOrThrow("session_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

}
