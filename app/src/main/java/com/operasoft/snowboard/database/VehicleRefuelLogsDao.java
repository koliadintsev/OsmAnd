package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class VehicleRefuelLogsDao extends Dao<VehicleRefuelLog> {

	public VehicleRefuelLogsDao() {
		super("sb_vehicle_refuel_logs");
	}

	@Override
	public void insert(VehicleRefuelLog dto) {
		String sql = "INSERT INTO "
				+ table
				+ " (id, vehicle_id, user_id, date, volume, amount, volume_unit, engine_hours, latitude, longitude, odometer, created, modified, sync_flag) "
				+ "VALUES ('" + dto.getId() + "', '" + dto.getVehicleId() + "','" + dto.getUserId() + "','"
				+ dto.getDate() + "'," + dto.getVolume() + "," + dto.getAmount() + ",'" + dto.getVolumeUnit() + "'," 
				+ dto.getEngineHours() + "," + dto.getLatitude() + "," + dto.getLongitude() + ",'" + dto.getOdometer() + "','" 
				+ dto.getCreated() + "','" + dto.getModified() + "'," + dto.getSyncFlag() + ")";

		Log.v("SQL INSERT Refuel", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	public void replace(VehicleRefuelLog dto) {
		String sql = "UPDATE " + table + " SET vehicle_id =  '" + dto.getVehicleId() + "', user_id = '" + dto.getUserId() + "' ,"
				+ "date =  '" + dto.getDate() + "', volume = " + dto.getVolume() + ", amount = " + dto.getAmount() + ", volume_unit = '" + dto.getVolumeUnit() + "',"
				+ "engine_hours = " + dto.getEngineHours() + ", latitude = " + dto.getLatitude() + ", longitude =  " + dto.getLongitude() + ","
				+ "odometer = '" + dto.getOdometer() + "', created = '" + dto.getCreated() + "', modified = '" + dto.getModified() + "', sync_flag =  " + dto.getSyncFlag()
				+ " WHERE id =  '" + dto.getId() + "'";
		Log.v("SQL UPDATE Refuel", sql);
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	protected VehicleRefuelLog buildDto(Cursor cursor) {
		VehicleRefuelLog vehicleRefuelLogs = new VehicleRefuelLog();

		vehicleRefuelLogs.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
		vehicleRefuelLogs.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		vehicleRefuelLogs.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		vehicleRefuelLogs.setEngineHours(cursor.getDouble(cursor.getColumnIndexOrThrow("engine_hours")));
		vehicleRefuelLogs.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		vehicleRefuelLogs.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
		vehicleRefuelLogs.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
		vehicleRefuelLogs.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		vehicleRefuelLogs.setOdometer(cursor.getString(cursor.getColumnIndexOrThrow("odometer")));
		vehicleRefuelLogs.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));
		vehicleRefuelLogs.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		vehicleRefuelLogs.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		vehicleRefuelLogs.setVolume(cursor.getFloat(cursor.getColumnIndexOrThrow("volume")));
		vehicleRefuelLogs.setVolumeUnit(cursor.getString(cursor.getColumnIndexOrThrow("volume_unit")));

		return vehicleRefuelLogs;
	}

	@Override
	public VehicleRefuelLog buildDto(JSONObject json) throws JSONException {
		VehicleRefuelLog dto = new VehicleRefuelLog();
		
		dto.setAmount(jsonParser.parseDouble(json, "amount"));
		dto.setDate(jsonParser.parseDate(json, "date"));
		dto.setEngineHours(jsonParser.parseDouble(json, "engine_hours"));
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setLatitude(jsonParser.parseDouble(json, "latitude"));
		dto.setLongitude(jsonParser.parseDouble(json, "longitude"));
		dto.setOdometer(jsonParser.parseString(json, "odometer"));
		dto.setUserId(jsonParser.parseString(json, "user_id"));
		dto.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		dto.setVolume(jsonParser.parseFloat(json, "volume"));
		dto.setVolumeUnit(jsonParser.parseString(json, "volume_unit"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}
}