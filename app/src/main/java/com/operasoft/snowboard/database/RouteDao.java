package com.operasoft.snowboard.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.operasoft.snowboard.util.Session;

import android.database.Cursor;

public class RouteDao extends Dao<Route> {
	public RouteDao() {
		super("sb_routes");
	}

	@Override
	public void insert(Route dto) {
		insertDto(dto);

	}

	@Override
	public void replace(Route dto) {
		replaceDto(dto);
	}

	public List<Route> listActiveRoutes() {
		List<Route> list = new ArrayList<Route>();
		
		String sql;
		if (Session.isViewDefautSeason())
			sql = "SELECT * FROM " + table + " WHERE status_code_id IS NULL OR status_code_id = '" + Route.ROUTE_ACTIVE
					+ "' AND season_id = '" + Session.getCurrentSeason() + "'";
		else
			sql = "SELECT * FROM " + table + " WHERE status_code_id IS NULL OR status_code_id = '" + Route.ROUTE_ACTIVE + "'";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			Route dto = buildDto(cursor);
			if (dto != null) {
				list.add(dto);
			}
		}
		cursor.close();
		return list;
	}

	@Override
	protected Route buildDto(Cursor cursor) {
		Route route = new Route();
		route.setCompanyId(cursor.getString(cursor.getColumnIndexOrThrow("company_id")));
		route.setCreated(cursor.getString(cursor.getColumnIndexOrThrow("created")));
		route.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
		route.setLinePath(cursor.getString(cursor.getColumnIndexOrThrow("linepath")));
		route.setModified(cursor.getString(cursor.getColumnIndexOrThrow("modified")));
		route.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
		route.setRouteGroupId(cursor.getString(cursor.getColumnIndexOrThrow("route_group_id")));
		route.setSeasonId(cursor.getString(cursor.getColumnIndexOrThrow("season_id")));
		route.setPopUp(cursor.getString(cursor.getColumnIndexOrThrow("pop_up_alert_sb")));
		route.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status_code_id")));
		route.setSyncFlag(cursor.getShort(cursor.getColumnIndexOrThrow("sync_flag")));
		return route;
	}

	@Override
	public Route buildDto(JSONObject json) throws JSONException {
		Route route = new Route();

		route.setId(jsonParser.parseString(json, "id"));
		route.setCompanyId(jsonParser.parseString(json, "company_id"));
		route.setName(jsonParser.parseString(json, "name"));
		route.setRouteGroupId(jsonParser.parseString(json, "route_group_id"));
		route.setSeasonId(jsonParser.parseString(json, "season_id"));
		route.setStatus(jsonParser.parseString(json, "status_code_id"));
		route.setPopUp(jsonParser.parseString(json, "pop_up_alert_sb"));
		route.setCreated(jsonParser.parseDate(json, "created"));
		route.setModified(jsonParser.parseDate(json, "modified"));
		route.setLinePath(jsonParser.parseString(json, "linepath"));

		return route;
	}

	public List<Route> listSortedRoutes() {
		List<Route> list = new ArrayList<Route>();

		String sql;
		if (Session.isViewDefautSeason())
			sql = "SELECT * FROM " + table + " WHERE status_code_id IS NULL OR status_code_id = '" + Route.ROUTE_ACTIVE
					+ "' AND season_id = '" + Session.getCurrentSeason() + "'" + "ORDER BY name COLLATE NOCASE";
		else
			sql = "SELECT * FROM " + table + " WHERE status_code_id IS NULL OR status_code_id = '" + Route.ROUTE_ACTIVE + "'"
					+ "ORDER BY name COLLATE NOCASE";

		Cursor cursor = DataBaseHelper.getDataBase().rawQuery(sql, null);
		while (cursor.moveToNext()) {
			Route dto = buildDto(cursor);
			if (dto != null) {
				list.add(dto);
			}
		}
		cursor.close();
		return list;
	}
}
