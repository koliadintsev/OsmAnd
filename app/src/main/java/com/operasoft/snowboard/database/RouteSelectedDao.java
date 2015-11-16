package com.operasoft.snowboard.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class RouteSelectedDao extends Dao<RouteSelected> {

	public RouteSelectedDao() {
		super("sb_route_selections");
	}

	@Override
	public void insert(RouteSelected dto) {
		insertDto(dto);
	}

	@Override
	public void replace(RouteSelected dto) {
		replaceDto(dto);
	}

	@Override
	public RouteSelected buildDto(JSONObject json) throws JSONException {
		RouteSelected routeselected = new RouteSelected();

		routeselected.setId(jsonParser.parseString(json, "id"));
		routeselected.setCompanyId(jsonParser.parseString(json, "company_id"));
		routeselected.setUserId(jsonParser.parseString(json, "user_id"));
		routeselected.setRouteId(jsonParser.parseString(json, "route_id"));
		routeselected.setVehicleId(jsonParser.parseString(json, "vehicle_id"));
		routeselected.setDateTime(jsonParser.parseString(json, "date_time"));
		routeselected.setCreated(jsonParser.parseDate(json, "created"));
		routeselected.setModified(jsonParser.parseDate(json, "modified"));
		
		return routeselected;
	}

	@Override
	protected RouteSelected buildDto(Cursor cursor) {
		RouteSelected routeSelected = new RouteSelected();
		routeSelected.setRouteId(cursor.getString(cursor.getColumnIndexOrThrow("route_id")));
		routeSelected.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
		routeSelected.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		routeSelected.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("date_time")));
		routeSelected.setVehicleId(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_id")));
		routeSelected.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		routeSelected.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		routeSelected.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		routeSelected.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));

		return routeSelected;
	}

	public String getRouteSelectionId(String routeId, String userId) {
		final String sql = "SELECT * FROM " + table + " where route_id = '" + routeId + "' and user_id='" + userId +"' order by date_time desc limit 1;";
		Log.d("RouteSelectedDao", sql);
		String routeSelectionId = "";
		Cursor cursor = null;
		RouteSelected routeSelected = null;
		try {
			cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
			while (cursor.moveToNext()) {
				routeSelected = buildDto(cursor);
				if (routeSelected != null) {
					routeSelectionId = routeSelected.getId();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("getRouteSelectionId", e.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();
		}
		Log.d("RouteSelectedDao", "result: " + routeSelectionId);
		return routeSelectionId;
	}
}
