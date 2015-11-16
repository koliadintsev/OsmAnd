package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class SeasonDao extends Dao<Season> {

	public SeasonDao() {
		super("sb_seasons");
	}

	/**
	 * Inserts a new SA in our database
	 * 
	 * @param sa
	 */
	@Override
	public void insert(Season sa) {
		insertDto(sa);
	}

	/**
	 * Updates an existing SA in our database
	 * 
	 * @param sa
	 */
	@Override
	public void replace(Season sa) {
		replaceDto(sa);
	}

	/**
	 * This method builds the DTO object based on a database cursor.
	 */
	@Override
	protected Season buildDto(Cursor cursor) {
		Season season = new Season();

		season.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		season.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		season.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
		season.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
		season.setStatusCodeId(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		season.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		season.setContractPrefix(cursor.getString(cursor.getColumnIndexOrThrow("contract_prefix")));
		season.setSeasonCode(cursor.getString(cursor.getColumnIndexOrThrow("season_code")));
		season.setEnglishContractDescription(cursor.getString(cursor.getColumnIndexOrThrow("english_contract_description")));
		season.setFrenchContractDescription(cursor.getString(cursor.getColumnIndexOrThrow("french_contract_description")));
		season.setDefaultVal(cursor.getString(cursor.getColumnIndexOrThrow("default")));
		season.setImportId(cursor.getString(cursor.getColumnIndexOrThrow("import_id")));
		season.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		season.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		season.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return season;
	}

	@Override
	public Season buildDto(JSONObject jsonObject) throws JSONException {
		Season season = new Season();

		season.setId(jsonParser.parseString(jsonObject, "id"));
		season.setCompanyId(jsonParser.parseString(jsonObject, "company_id"));
		season.setName(jsonParser.parseString(jsonObject, "name"));
		season.setEndDate(jsonParser.parseString(jsonObject, "end_date"));
		season.setStartDate(jsonParser.parseString(jsonObject, "start_date"));
		season.setStatusCodeId(jsonParser.parseString(jsonObject, "status_code_id"));
		season.setCompanyId(jsonParser.parseString(jsonObject, "company_id"));
		season.setContractPrefix(jsonParser.parseString(jsonObject, "contract_prefix"));
		season.setSeasonCode(jsonParser.parseString(jsonObject, "season_code"));
		season.setEnglishContractDescription(jsonParser.parseString(jsonObject, "english_contract_description"));
		season.setFrenchContractDescription(jsonParser.parseString(jsonObject, "french_contract_description"));
		season.setDefaultVal(jsonParser.parseString(jsonObject, "default"));
		season.setImportId(jsonParser.parseString(jsonObject, "import_id"));
		season.setCreated(jsonParser.parseDate(jsonObject, "created"));
		season.setModified(jsonParser.parseDate(jsonObject, "modified"));

		return season;
	}

}