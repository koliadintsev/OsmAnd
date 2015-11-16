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

public class TransportVehicleDao extends Dao<TransportVehicle> {
	public TransportVehicleDao() {
		super("sb_transport_vehicles");
	}

	public List<TransportVehicle> listSorted() {
		List<TransportVehicle> list = new ArrayList<TransportVehicle>();

		String sql = "SELECT * FROM " + table +" WHERE deleted = 0 ORDER BY LOWER(name) ASC;";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				TransportVehicle dto = buildDto(cursor);
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
	
	@Override
	public void insert(TransportVehicle dto) {
		insertDto(dto);
	}

	@Override
	public void replace(TransportVehicle dto) {
		replaceDto(dto);
	}

	@Override
	protected TransportVehicle buildDto(Cursor cursor) {
		TransportVehicle dto = new TransportVehicle();
		
		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setSupplierId(cursor.getString(cursor.getColumnIndexOrThrow("supplier_id")));
		dto.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		dto.setVehicleNumber(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_number")));
		dto.setLicensePlate(cursor.getString(cursor.getColumnIndexOrThrow("license_plate")));
		dto.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		dto.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow("deleted")));
		dto.setCreated(cursor.getString((cursor.getColumnIndexOrThrow("created"))));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		
		return dto;
	}

	@Override
	public TransportVehicle buildDto(JSONObject json) throws JSONException {
		TransportVehicle dto = new TransportVehicle();
		
		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setSupplierId(jsonParser.parseString(json, "supplier_id"));
		dto.setName(jsonParser.parseString(json, "name"));
		dto.setVehicleNumber(jsonParser.parseString(json, "vehicle_number"));
		dto.setLicensePlate(jsonParser.parseString(json, "license_plate"));
		dto.setStatus(jsonParser.parseString(json, "status_code_id"));
		dto.setDeleted(jsonParser.parseInt(json, "deleted"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
