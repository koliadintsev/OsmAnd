package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class DeficiencyDao extends Dao<Deficiency>{
	
	DeficiencyPictureDao deficiencyPictureDao = new DeficiencyPictureDao();
	
	public DeficiencyDao() {
		super("sb_route_deficiencies");
	}

	@Override
	public void insert(Deficiency deficiency) {
		insertDto(deficiency);
		deficiencyPictureDao.insert(deficiency.getPictures());
	}

	@Override
	public void replace(Deficiency deficiency) {
		replaceDto(deficiency);
		deficiencyPictureDao.replace(deficiency.getPictures());
	}


	@Override
	public Deficiency buildDto(JSONObject json) throws JSONException {
		Deficiency dto = new Deficiency();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setRoute_selection_id(jsonParser.parseString(json, "route_selection_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setDate(jsonParser.parseDate(json, "date_time"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setDeficiencyTypeId(jsonParser.parseString(json, "deficiency_type_id"));
		dto.setRouteId(jsonParser.parseString(json, "route_id"));
		dto.setAirT(jsonParser.parseString(json, "air_T"));
		dto.setGroundT(jsonParser.parseString(json, "ground_T"));
		dto.setNotes(jsonParser.parseString(json, "notes"));
		dto.setGpsCoordinates(jsonParser.parseString(json, "gps_coordinates"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));
				
		return dto;
	}

	@Override
	protected Deficiency buildDto(Cursor cursor) {
		Deficiency dto = new Deficiency();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setRoute_selection_id(cursor.getString(cursor.getColumnIndexOrThrow("route_selection_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setDeficiencyTypeId(cursor.getString(cursor.getColumnIndexOrThrow("deficiency_type_id")));
		dto.setRouteId(cursor.getString(cursor.getColumnIndexOrThrow("route_id")));
		dto.setAirT(cursor.getString(cursor.getColumnIndexOrThrow("air_T")));
		dto.setGroundT(cursor.getString(cursor.getColumnIndexOrThrow("ground_T")));
		dto.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
		dto.setGpsCoordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_coordinates")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		dto.setPictures(deficiencyPictureDao.getListAttachedWithDeficiency(dto.getId()));

		return dto;
	}
	
	

}
