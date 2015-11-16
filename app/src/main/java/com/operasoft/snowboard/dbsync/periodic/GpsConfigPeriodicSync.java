package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import com.operasoft.snowboard.database.GpsConfig;
import com.operasoft.snowboard.database.GpsConfigDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;

public class GpsConfigPeriodicSync extends AnonymousPeriodicSync {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private GpsConfigDao gpsConfigDao;

	public GpsConfigPeriodicSync() {
		super("GpsConfig", new GpsConfigDao());
		this.gpsConfigDao = (GpsConfigDao) super.dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {

		// This method builds SL objects from the JSON array received, updates
		// the database and pass it up to the POI manager when needed
		List<GpsConfig> list = dtoParser.parseGpsConfigs(jsArray);

		for (GpsConfig config : list) {
			gpsConfigDao.insertOrReplace(config);
			onUpdate(config);
			// Check if this is the config assigned to our tablet...

		}
	}

	public void onUpdate(GpsConfig gpsConfig) {
		// do something
	}

}
