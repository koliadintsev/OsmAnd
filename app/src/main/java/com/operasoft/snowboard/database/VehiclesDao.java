package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.operasoft.snowboard.util.Config;

public class VehiclesDao extends Dao<Vehicle> {
	public VehiclesDao() {
		super("sb_vehicles");
		this.orderByFields = "name, vehicle_number ";
	}

	public String getVimei(Context context) {
		SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(context);
		String viemi = "";
		String sql = "select esn_id from " + table + " where id = '" + mSP.getString(Config.VEHICLE_ID_KEY, "") + "' limit 1";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			viemi = cursor.getString(cursor.getColumnIndexOrThrow("esn_id")) + "";
		}
		cursor.close();
		Config.setVIMEI(viemi);
		return viemi;
	}

	public boolean xergoEsn(Context context) {
		SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(context);
		String sql = "select esn_id from " + table + " where id = '" + mSP.getString(Config.VEHICLE_ID_KEY, "") + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			String esn_id = cursor.getString(cursor.getColumnIndexOrThrow("esn_id"));
			if ( (esn_id != null) && esn_id.startsWith("999999")) {
				cursor.close();
				return false;
			} else {
				getVimei(context);
			}
		}
		cursor.close();
		return true;
	}

	public List<Vehicle> listSorted() {
		String sql = "SELECT * FROM " + table + " WHERE status_code_id = '" + Vehicle.ACTIVE_STATUS + "' ORDER BY LOWER(name) ASC;";
		return listDtos(sql);
	}

	@Override
	public void insert(Vehicle dto) {
		insertDto(dto);
	}

	@Override
	public void replace(Vehicle dto) {
		replaceDto(dto);
	}

	@Override
	public Vehicle buildDto(Cursor cursor) {
		Vehicle vehicles = new Vehicle();
		vehicles.setCreated(cursor.getString((cursor.getColumnIndexOrThrow("created"))));
		vehicles.setCompany_id(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		vehicles.setEsn_id(cursor.getString(cursor.getColumnIndexOrThrow("esn_id")));
		vehicles.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		vehicles.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		vehicles.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		vehicles.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		vehicles.setVehicleNumber(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_number")));
		vehicles.setTrailer(cursor.getString(cursor.getColumnIndexOrThrow("trailer")));
		vehicles.setEquipmentTypeId(cursor.getString(cursor.getColumnIndexOrThrow("equipment_type_id")));
		vehicles.setTrailerId(cursor.getString(cursor.getColumnIndexOrThrow("trailer_id")));
		vehicles.setImeiNumber(cursor.getString(cursor.getColumnIndexOrThrow("imei_number")));
		vehicles.setStatusCodeId(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		return vehicles;
	}

	@Override
	public Vehicle buildDto(JSONObject json) throws JSONException {
		Vehicle dto = new Vehicle();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setEsn_id(jsonParser.parseString(json, "esn_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setTrailer(jsonParser.parseString(json, "trailer"));
		dto.setVehicleNumber(jsonParser.parseString(json, "vehicle_number"));
		dto.setEquipmentTypeId(jsonParser.parseString(json, "equipment_type_id"));
		dto.setTrailerId(jsonParser.parseString(json, "trailer_id"));
		dto.setCreated(jsonParser.parseString(json, "created"));
		dto.setModified(jsonParser.parseString(json, "modified"));
		dto.setImeiNumber(jsonParser.parseString(json, "imei_number"));
		dto.setStatusCodeId(jsonParser.parseString(json, "status_code_id"));

		return dto;
	}

	public List<Vehicle> listTrailers(String[] equipmentTypeidList) {
		List<Vehicle> list = new ArrayList<Vehicle>();

		String sql = "SELECT * FROM " + table + " WHERE equipment_type_id IN (" + makePlaceholders(equipmentTypeidList.length) + ")";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, equipmentTypeidList);
		while (cursor.moveToNext()) {
			try {
				Vehicle dto = buildDto(cursor);
				if (dto != null) {
					list.add(dto);
				}
			} catch (Exception e) {
				Log.e(sql, "field not found", e);
			}
		}
		cursor.close();

		return list;
	}

	public List<Vehicle> findByEquipmentType(String equipmentTypeId) {
		String sql = "SELECT * FROM " + table + " WHERE equipment_type_id = '" + equipmentTypeId +"' ORDER BY LOWER(name) ASC";
		return listDtos(sql);
	}
	
	String makePlaceholders(int len) {
		if (len < 1) {
			// It will lead to an invalid query anyway
			throw new RuntimeException("No placeholders");
		} else {
			StringBuilder sb = new StringBuilder(len * 2 - 1);
			sb.append("?");
			for (int i = 1; i < len; i++) {
				sb.append(",?");
			}
			return sb.toString();
		}
	}

}
