package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class DamageDao extends Dao<Damage> {

	public DamageDao() {
		super("sb_damages");
	}

	@Override
	public void insert(Damage damage) {
		insertDto(damage);
	}

	@Override
	public void replace(Damage damage) {
		replaceDto(damage);
	}

	@Override
	protected Damage buildDto(Cursor cursor) {
		Damage dto = new Damage();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		dto.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
		dto.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		dto.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		dto.setGpsCoordinates(cursor.getString(cursor.getColumnIndexOrThrow("gps_coordinates")));
		dto.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
		dto.setForeignKey(cursor.getString(cursor.getColumnIndexOrThrow("foreign_key")));
		dto.setForeignValue(cursor.getString(cursor.getColumnIndexOrThrow("foreign_value")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setDamageTypeId(cursor.getString(cursor.getColumnIndexOrThrow("damage_type_id")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public Damage buildDto(JSONObject json) throws JSONException {
		Damage dto = new Damage();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setDescription(jsonParser.parseString(json, "description"));
		dto.setContractId(jsonParser.parseString(json, "contract_id"));
		dto.setDate(jsonParser.parseDate(json, "date_time"));
		dto.setGpsCoordinates(jsonParser.parseString(json, "gps_coordinates"));
		dto.setComments(jsonParser.parseString(json, "comments"));
		dto.setForeignKey(jsonParser.parseString(json, "foreign_key"));
		dto.setForeignValue(jsonParser.parseString(json, "foreign_value"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setDamageTypeId(jsonParser.parseString(json, "damage_type_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}