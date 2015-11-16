package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.operasoft.snowboard.database.RouteSelected;
import com.operasoft.snowboard.database.RouteSelectedDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class RouteSelectionPeriodicSync extends DefaultPeriodicSync {

	/**
	 * This member is used to convert JSON -> DTO objects
	 */
	private JsonDtoParser		dtoParser	= new JsonDtoParser();
	private RouteSelectedDao	dao;

	public RouteSelectionPeriodicSync() {
		super("RouteSelection", new RouteSelectedDao());
		this.dao = (RouteSelectedDao) super.dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		List<RouteSelected> list = dtoParser.parseRouteSelections(jsArray);

		for (RouteSelected routeselection : list) {
			Log.w("Routeselection Sync", "Received " + routeselection.getId());
			if (dao.exists(routeselection.getId())) {
				continue;
			} else {
				dao.insert(routeselection);
			}
		}
	}

}
