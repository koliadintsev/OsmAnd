package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.RouteDao;
import com.operasoft.snowboard.database.RouteSequence;
import com.operasoft.snowboard.database.RouteSequenceDao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.dbsync.DbSyncManager;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.onetime.RouteSequenceOneTimeSync;

/**
 * This class implements the route periodic sync logic. When an existing route is being updated on Snowboard,
 * we need to clean all of its route sequence entries in our local database and request a one-time update
 * to retrieve the latest route sequences for this route on Snowman.
 * @author Christian
 *
 */
public class RoutePeriodicSync extends DefaultPeriodicSync {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private RouteDao dao;
	private RouteSequenceDao routeSequenceDao;
	
	public RoutePeriodicSync() {
		super("Route", new RouteDao());
		this.dao = (RouteDao) super.dao;
		routeSequenceDao = new RouteSequenceDao();
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		// This method builds SA objects from the JSON array received, updates
		// the database and pass it up to the POI manager when needed
		List<Route> list = dtoParser.parseRoutes(jsArray);
		
		for (Route route : list) {
			Log.w("Route Sync", "Received " + route.getId() + " - " + route.getName());
			if (dao.exists(route.getId())) {
				// We are replacing an existing route in our local DB, we need to flush the route sequence
				// DTOs that belongs to this route in order for it to display properly.
				routeSequenceDao.removeAllForRoute(route.getId());
				
				if (route.isActive()) {
					// The route is still active...
					// Schedule a one-time update from the Snowman server to retrieve the updated route sequences
					Log.i("Route Sync", "Scheduling one-time sync for route " + route.getId() + " - " + route.getName());
					RouteSequenceOneTimeSync oneTimeSync = new RouteSequenceOneTimeSync(route.getId());
					DbSyncManager.getInstance().addOneTimeSync(oneTimeSync);
				}
				
				dao.replace(route);
			} else {
				dao.insert(route);
			}
		}
	}
	
	
}
