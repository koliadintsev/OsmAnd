package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class CompanyDao extends Dao<Company> {

	public CompanyDao() {
		super("sb_companies");
	}

	public String getCompanyId() {
		Company company = getFirst();
		if (company != null) {
			return company.getId();
		}

		return "";
	}

	public String getDefaultSeasonId() {
		Company company = getFirst();
		if (company != null) {
			return company.getDefaultSeasonId();
		}

		return "";
	}

	@Override
	public void insert(Company dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Company dto) {
		replaceDto(dto);
	}

	@Override
	protected Company buildDto(Cursor cursor) {
		Company dto = new Company();
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setLanguage(cursor.getString(cursor.getColumnIndexOrThrow("language")));
		dto.setCompanyName(cursor.getString(cursor.getColumnIndexOrThrow("company_name")));
		dto.setDefaultSeasonId(cursor.getString(cursor.getColumnIndexOrThrow("default_season_id")));
		dto.setImperialUnits(cursor.getInt(cursor.getColumnIndexOrThrow("miles")));
		dto.setBlocked(cursor.getString(cursor.getColumnIndexOrThrow("blocked")));
		dto.setBusinessTypeId(cursor.getString(cursor.getColumnIndexOrThrow("business_type_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return dto;
	}

	@Override
	public Company buildDto(JSONObject json) throws JSONException {
		Company dto = new Company();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setLanguage(jsonParser.parseString(json, "language"));
		dto.setCompanyName(jsonParser.parseString(json, "company_name"));
		dto.setDefaultSeasonId(jsonParser.parseString(json, "default_season_id"));
		dto.setImperialUnits(jsonParser.parseInt(json, "miles"));
		dto.setBlocked(jsonParser.parseString(json, "blocked"));
		dto.setBusinessTypeId(jsonParser.parseString(json, "business_type_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}
}