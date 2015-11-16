package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class GpsConfigDao extends Dao<GpsConfig> {

	public GpsConfigDao() {
		super("sb_gps_configs");
	}

	@Override
	public void insert(GpsConfig dto) {
		insertDto(dto);
	}

	@Override
	public void replace(GpsConfig dto) {
		replaceDto(dto);
	}

	@Override
	protected GpsConfig buildDto(Cursor cursor) {
		GpsConfig dto = new GpsConfig();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setHeartbeatDelayOn(cursor.getInt(cursor.getColumnIndexOrThrow("heartbeat_delay_on")));
		dto.setHeartbeatDelayOff(cursor.getInt(cursor.getColumnIndexOrThrow("heartbeat_delay_off")));
		dto.setHeadingChange(cursor.getInt(cursor.getColumnIndexOrThrow("heading_change")));
		dto.setMovementStartDelay(cursor.getInt(cursor.getColumnIndexOrThrow("movement_start_delay")));
		dto.setMovementStartSpeed(cursor.getInt(cursor.getColumnIndexOrThrow("movement_start_speed")));
		dto.setMovementStopDelay(cursor.getInt(cursor.getColumnIndexOrThrow("movement_stop_delay")));
		dto.setMovementStopSpeed(cursor.getInt(cursor.getColumnIndexOrThrow("movement_stop_speed")));
		dto.setAcceleration(cursor.getInt(cursor.getColumnIndexOrThrow("acceleration")));
		dto.setBraking(cursor.getInt(cursor.getColumnIndexOrThrow("braking")));
		dto.setLowBattery(cursor.getDouble(cursor.getColumnIndexOrThrow("low_battery")));
		dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return dto;
	}

	@Override
	public GpsConfig buildDto(JSONObject json) throws JSONException {
		GpsConfig dto = new GpsConfig();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setHeartbeatDelayOn(jsonParser.parseInt(json, "heartbeat_delay_on"));
		dto.setHeartbeatDelayOff(jsonParser.parseInt(json, "heartbeat_delay_off"));
		dto.setHeadingChange(jsonParser.parseInt(json, "heading_change"));
		dto.setMovementStartDelay(jsonParser.parseInt(json, "movement_start_delay"));
		dto.setMovementStartSpeed(jsonParser.parseInt(json, "movement_start_speed"));
		dto.setMovementStopDelay(jsonParser.parseInt(json, "movement_stop_delay"));
		dto.setMovementStopSpeed(jsonParser.parseInt(json, "movement_stop_speed"));
		dto.setAcceleration(jsonParser.parseInt(json, "acceleration"));
		dto.setBraking(jsonParser.parseInt(json, "braking"));
		dto.setLowBattery(jsonParser.parseDouble(json, "low_battery"));
		dto.setCreated(jsonParser.parseString(json, "created"));
		dto.setModified(jsonParser.parseString(json, "modified"));

		return dto;
	}
}
