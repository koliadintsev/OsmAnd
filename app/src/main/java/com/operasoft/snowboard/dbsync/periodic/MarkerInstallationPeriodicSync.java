package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import com.operasoft.snowboard.database.MarkerInstallation;
import com.operasoft.snowboard.database.MarkerInstallationDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class MarkerInstallationPeriodicSync extends DefaultPeriodicSync {

	/**
	 * This member is used to convert JSON -> DTO objects
	 */
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private MarkerInstallationDao dao;
	private PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();

	public MarkerInstallationPeriodicSync() {
		super("MarkerInstallation", new MarkerInstallationDao());
		this.dao = (MarkerInstallationDao) super.dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {

		// This method builds mi objects from the JSON array received, updates
		// the database and pass it up to the POI manager when needed
		List<MarkerInstallation> list = dtoParser.parseMarkerInstallation(jsArray);

		for (MarkerInstallation mi : list) {
			MarkerInstallation dbObj = dao.getById(mi.getId());
			if (dbObj != null) {
				// We already have this mi in our database
				
				// 2. Update the POI manager
				if (mi.isInstalled()) {
					poiMgr.detachMarkerInstallation(mi);
					dao.remove(mi.getId());
				} else {
					dao.replace(mi);
				}
			} else {
				if (!mi.isInstalled()) {
					// This SA is for me, we need to:
					// 1. Add it to our database
					dao.insert(mi);
					// 2. Add it to the POI manager to bring it to the driver's
					// attention
					poiMgr.attachMarkerInstallation(mi);
				}
			}
		}
	}
}