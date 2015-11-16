package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class UploadsDao extends Dao<Uploads>{
	
	public UploadsDao() {
		super("sb_uploads");
	}

	@Override
	public void insert(Uploads dto) {
		insertDto(dto);

	}

	@Override
	public void replace(Uploads dto) {
		replaceDto(dto);
	}
	
	@Override
	public Uploads buildDto(JSONObject json) throws JSONException {
		Uploads uploads = new Uploads();
		
		uploads.setId(jsonParser.parseString(json, "id"));
		
		uploads.setCompanyId(jsonParser.parseString(json, "company_id"));
		uploads.setModel(jsonParser.parseString(json, "model"));
		uploads.setFilename(jsonParser.parseString(json, "filename"));
		uploads.setOriginal_filename(jsonParser.parseString(json, "original_filename"));
		uploads.setGps_cordinates(jsonParser.parseString(json, "gps_cordinates"));
		uploads.setUrl(jsonParser.parseString(json, "url"));
		uploads.setImei_no(jsonParser.parseString(json, "imei_no"));
		uploads.setCreator_id(jsonParser.parseString(json, "creator_id"));

		uploads.setCreated(jsonParser.parseDate(json, "created"));
		uploads.setModified(jsonParser.parseDate(json, "modified"));
		
		return uploads;
	}

	@Override
	public Uploads buildDto(Cursor cursor) {
		Uploads uploads = new Uploads();

		uploads.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		
		uploads.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		uploads.setModel(cursor.getString(cursor.getColumnIndexOrThrow("model")));
		uploads.setFilename(cursor.getString(cursor.getColumnIndexOrThrow("filename")));
		uploads.setOriginal_filename(cursor.getString(cursor.getColumnIndexOrThrow("original_filename")));
		uploads.setGps_cordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_cordinates")));
		uploads.setUrl(cursor.getString(cursor.getColumnIndexOrThrow("url")));
		uploads.setImei_no(cursor.getString(cursor.getColumnIndexOrThrow("imei_no")));
		uploads.setCreator_id(cursor.getString(cursor.getColumnIndexOrThrow("creator_id")));
		
		uploads.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		uploads.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		uploads.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return uploads;
	}
}
