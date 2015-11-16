package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.operasoft.snowboard.database.Geofence;
import com.operasoft.snowboard.database.GeofenceDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.engine.GeofenceManager;

public class GeofencePeriodicSync extends DefaultPeriodicSync {

	/**
	 * This member is used to convert JSON -> DTO objects
	 */
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private GeofenceManager geofenceManager = GeofenceManager.getInstance();

	public GeofencePeriodicSync() {
		super("Geofence", new GeofenceDao());
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		// This method builds SA objects from the JSON array received, updates
		// the database and pass it up to the POI manager when needed
		List<Geofence> list = dtoParser.parseGeofences(jsArray);
		
		for (Geofence geofence : list) {
			Log.w("Geofence Sync", "Received " + geofence.getId() + ", Deleted? " + geofence.isDeleted());
			if (geofence.isDeleted()) {
				geofenceManager.removeGeofence(geofence);
			} else {
				geofenceManager.addOrUpdateGeofence(geofence);
			}
		}
	}


}
