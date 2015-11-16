package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class GeofenceDao extends Dao<Geofence> {

	public GeofenceDao() {
		super("sb_geofences");
	}
	
	@Override
	public void insert(Geofence dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Geofence dto) {
		replaceDto(dto);
	}

	@Override
	public Geofence buildDto(JSONObject json) throws JSONException {
		Geofence dto = new Geofence();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setCenter(jsonParser.parseString(json, "pcenter"));
		dto.setComments(jsonParser.parseString(json, "comments"));
		dto.setGeom(jsonParser.parseString(json, "geom"));
		dto.setColor(jsonParser.parseString(json, "color"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setInputThreshold(jsonParser.parseFloat(json, "input_threshold"));
		dto.setOutputThreshold(jsonParser.parseFloat(json, "output_threshold"));
		dto.setDeleted(jsonParser.parseInt(json, "deleted"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
		
		return dto;
	}

	@Override
	protected Geofence buildDto(Cursor cursor) {
		Geofence dto = new Geofence();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setCenter(cursor.getString(cursor.getColumnIndexOrThrow("pcenter")));
		dto.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
		dto.setGeom(cursor.getString(cursor.getColumnIndexOrThrow("geom")));
		dto.setColor(cursor.getString(cursor.getColumnIndexOrThrow("color")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setInputThreshold(cursor.getFloat(cursor.getColumnIndexOrThrow("input_threshold")));
		dto.setOutputThreshold(cursor.getFloat(cursor.getColumnIndexOrThrow("output_threshold")));
		dto.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

}
