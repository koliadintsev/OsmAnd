package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class VehicleLastReportDao extends Dao<VehicleLastReport> {

	public VehicleLastReportDao() {
		super("sb_vehicle_last_reports");
	}

	@Override
	public void insert(VehicleLastReport dto) {
		insertDto(dto);
	}

	@Override
	public void replace(VehicleLastReport dto) {

		try {
			StringBuilder builder = new StringBuilder("UPDATE " + table + " SET company_id='" + dto.getCompanyId()
					+ "', location='" + dto.getLocation() + "', modified='" + dto.getModified() + "'");
			builder.append(" WHERE id = '" + dto.getId() + "'");

			String sql = builder.toString();
			Log.v("SQL UPDATE " + table, sql);
			DataBaseHelper.getDataBase().execSQL(sql);
		} catch (Exception e) {
			Log.e(table, "Failed to update DTO", e);
			e.printStackTrace();
		}

	}

	@Override
	protected VehicleLastReport buildDto(Cursor cursor) {
		VehicleLastReport dto = new VehicleLastReport();

		dto.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		dto.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		dto.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
		// dto.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		dto.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		dto.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));

		return dto;
	}

	@Override
	public VehicleLastReport buildDto(JSONObject json) throws JSONException {
		VehicleLastReport dto = new VehicleLastReport();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompanyId(jsonParser.parseString(json, "company_id"));
		dto.setLocation(jsonParser.parseString(json, "location"));
		// dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

}
