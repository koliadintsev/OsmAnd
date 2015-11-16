package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class ServiceLocationPeriodicSync extends DefaultPeriodicSync {

	/**
	 * This member is used to convert JSON -> DTO objects
	 */
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private ServiceLocationDao dao;
	private PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();

	public ServiceLocationPeriodicSync() {
		super("ServiceLocation", new ServiceLocationDao());
		this.dao = (ServiceLocationDao) super.dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {

		// This method builds SL objects from the JSON array received, updates
		// the database and pass it up to the POI manager when needed
		List<ServiceLocation> list = dtoParser.parseServiceLocations(jsArray);

		for (ServiceLocation sl : list) {
			ServiceLocation dbObj = dao.getById(sl.getId());
			if (dbObj != null) {
				// We already have this SL in our database
				// Let's update our database
				dao.replace(sl);
			} else {
				// This SA is for me, we need to:
				// 1. Add it to our database
				dao.insert(sl);
				// 2. Add it to the POI manager to bring it to the driver's
				// attention
				poiMgr.attachServiceLocation(sl);
			}
		}
	}
}