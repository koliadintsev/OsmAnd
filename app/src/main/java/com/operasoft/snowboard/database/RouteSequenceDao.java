package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class RouteSequenceDao extends Dao<RouteSequence> {

	public RouteSequenceDao() {
		super("sb_route_sequences");
	}

	@Override
	public void insert(RouteSequence dto) {
		insertDto(dto);
	}

	@Override
	public void replace(RouteSequence dto) {
		replaceDto(dto);
	}

	/**
	 * Removes all route sequences that are tied to a given route ID from our local DB.
	 * 
	 * @param routeId
	 */
	public void removeAllForRoute(String routeId) {
		String sql = "DELETE FROM " + table + " WHERE route_id = '" + routeId + "'";
		DataBaseHelper.getDataBase().execSQL(sql);
	}

	@Override
	protected RouteSequence buildDto(Cursor cursor) {
		RouteSequence routeSequence = new RouteSequence();
		routeSequence.setCompany_id(cursor.getString(cursor
				.getColumnIndexOrThrow("company_id")));
		routeSequence.setContract_id(cursor.getString(cursor
				.getColumnIndexOrThrow("contract_id")));
		routeSequence.setCreated(cursor.getString(cursor
				.getColumnIndexOrThrow("created")));
		routeSequence
				.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		routeSequence.setModified(cursor.getString(cursor
				.getColumnIndexOrThrow("modified")));
		routeSequence.setRouteId(cursor.getString(cursor
				.getColumnIndexOrThrow("route_id")));
		routeSequence.setSequenceOrder(cursor.getInt(cursor
				.getColumnIndexOrThrow("sequence_order")));
		routeSequence.setService_location_id(cursor.getString(cursor
				.getColumnIndexOrThrow("service_location_id")));
		routeSequence.setSyncFlag(cursor.getShort(cursor
				.getColumnIndexOrThrow("sync_flag")));

		return routeSequence;
	}

	
	@Override
	public RouteSequence buildDto(JSONObject json) throws JSONException {
		RouteSequence dto = new RouteSequence();

		dto.setId(jsonParser.parseString(json, "id"));
		dto.setCompany_id(jsonParser.parseString(json, "company_id"));
		dto.setContract_id(jsonParser.parseString(json, "contract_id"));
		dto.setRouteId(jsonParser.parseString(json, "route_id"));
		dto.setSequenceOrder(jsonParser.parseInt(json, "sequence_order"));
		dto.setService_location_id(jsonParser.parseString(json, "service_location_id"));
		dto.setCreated(jsonParser.parseDate(json, "created"));
		dto.setModified(jsonParser.parseDate(json, "modified"));

		return dto;
	}

	public List<RouteSequence> listAllForRoute(String routeId) {
		List<RouteSequence> list = new ArrayList<RouteSequence>();
		String sql = "SELECT * FROM " + table + " WHERE route_id = '" + routeId + "'";
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			RouteSequence sa = buildDto(cursor);
			if (sa != null) {
				list.add(sa);
			}
		}
		cursor.close();
		return list;
	}

}
