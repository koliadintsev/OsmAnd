package com.operasoft.snowboard.dbsync.periodic;

import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.util.Session;

/**
 * This class is used to customize how we handle the periodic synchronization of 
 * service activities received from the Snowman server.
 * 
 * @author Christian
 *
 */
public class ServiceActivityPeriodicSync extends DefaultPeriodicSync {

	/**
	 * This member is used to convert JSON -> DTO objects
	 */
	private JsonDtoParser dtoParser = new JsonDtoParser();
	private ServiceActivityDao dao;
	private PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
	
	public ServiceActivityPeriodicSync() {
		super("ServiceActivity", new ServiceActivityDao());
		this.dao = (ServiceActivityDao) super.dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		
		// This method builds SA objects from the JSON array received, updates
		// the database and pass it up to the POI manager when needed
		List<ServiceActivity> list = dtoParser.parseServiceActivities(jsArray);

		for (ServiceActivity sa : list) {
			Log.w("SA Sync", "Received " + sa.getId());
			ServiceActivity dbObj = dao.getById(sa.getId());
			if (dbObj != null) {
				boolean wasMine = dbObj.isMine();
				boolean isMine = sa.isMine();
				
				// We already have this SA in our database
				dao.replace(sa);
				// Check if we need to pass it on to the POI manager for processing
				if (Session.viewAllSAs) {
					if (dbObj.isWorkRequired() && sa.isWorkRequired()) {
						poiMgr.updateServiceActivity(sa);
					} else if (sa.isWorkRequired()) {
						poiMgr.addServiceActivity(sa);
					} else if (dbObj.isWorkRequired()) {
						poiMgr.removeServiceActivity(sa);
					}
				} else if (wasMine && !isMine) {
					// This SA is no longer assigned to me, let's remove it from the POI manager
					poiMgr.removeServiceActivity(sa);
				} else if (wasMine && isMine) {
					// This one is still mine, let's tell the POI manager about it
					if (dbObj.isWorkRequired() && sa.isWorkRequired()) {
						poiMgr.updateServiceActivity(sa);
					} else if (sa.isWorkRequired()) {
						poiMgr.addServiceActivity(sa);
					} else if (dbObj.isWorkRequired()) {
						poiMgr.removeServiceActivity(sa);
					}
				} else if (!wasMine && isMine) {
					// This one has just been assigned to me
					if (sa.isWorkRequired()) {
						poiMgr.addServiceActivity(sa);
					}
				}
			} else {
				// This is a brand new SA, add it to our database
				dao.insert(sa);
				if ( Session.viewAllSAs && sa.isWorkRequired() ) {
					// We want to see all SAs:
					// Add it to the POI manager to bring it to the driver's attention
					poiMgr.addServiceActivity(sa);
				} else if ( sa.isMine() && sa.isWorkRequired() ) {
					// This SA is for me, we need to:
					// Add it to the POI manager to bring it to the driver's attention
					poiMgr.addServiceActivity(sa);
				}
			}
		}
	}
}
