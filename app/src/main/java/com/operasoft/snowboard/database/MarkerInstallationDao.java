package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class MarkerInstallationDao extends Dao<MarkerInstallation> {

	public MarkerInstallationDao() {
		super("sb_marker_installations");
	}

	@Override
	public void insert(MarkerInstallation dto) {
		insertDto(dto);

	}

	@Override
	public void replace(MarkerInstallation dto) {
		replaceDto(dto);
	}

	@Override
	protected MarkerInstallation buildDto(Cursor cursor) {
		MarkerInstallation markerInstallation = new MarkerInstallation();

		markerInstallation.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		markerInstallation.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
		markerInstallation.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		markerInstallation.setContractId(cursor.getString(cursor.getColumnIndexOrThrow("contract_id")));
		markerInstallation.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		markerInstallation.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		markerInstallation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_id")));
		markerInstallation.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		markerInstallation.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		markerInstallation.setSyncFlag(cursor.getInt(cursor.getColumnIndexOrThrow("sync_flag")));

		return markerInstallation;
	}

	
	@Override
	public MarkerInstallation buildDto(JSONObject json) throws JSONException {
		MarkerInstallation mi = new MarkerInstallation();

		mi.setId(jsonParser.parseString(json, "id"));
		mi.setCompanyId(jsonParser.parseString(json, "company_id"));
		mi.setComments(jsonParser.parseString(json, "comments"));
		mi.setContractId(jsonParser.parseString(json, "contract_id"));
		mi.setStatus(jsonParser.parseString(json, "status_id"));
		mi.setUserId(jsonParser.parseString(json, "user_id"));
		mi.setDateTime(jsonParser.parseDate(json, "date_time"));
		mi.setCreated(jsonParser.parseDate(json, "created"));
		mi.setModified(jsonParser.parseDate(json, "modified"));

		return mi;
	}

	/**
	 * @deprecated This is not working yet
	 * @param contractId
	 * @return
	 */
	public MarkerInstallation getByContractId(String contractId) {
		String miId = "";
		String sql = "SELECT mi.id FROM sb_marker_installations mi, sb_contracts con WHERE con.service_location_id = mi.id and con.id='"
				+ contractId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			miId = cursor.getString(0);
		}
		cursor.close();
		return getById(miId);
	}

	public List<MarkerInstallation> listActive() {
		List<MarkerInstallation> list = new ArrayList<MarkerInstallation>();

		String sql = "SELECT * FROM " + table +" WHERE status_id = '" + MarkerInstallation.OPEN_PENDING_CASE + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			try {
				MarkerInstallation dto = buildDto(cursor);
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

}
