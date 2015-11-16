package com.operasoft.snowboard.dbsync.periodic;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.operasoft.snowboard.database.VehicleLastReport;
import com.operasoft.snowboard.database.VehicleLastReportDao;
import com.operasoft.snowboard.engine.VehicleLocationManager;
import com.operasoft.snowboard.util.Session;

public class VehicleLastReportPeriodicSync extends DefaultPeriodicSync {

	private VehicleLocationManager manager;
	private VehicleLastReportDao reportDao;
	
	public VehicleLastReportPeriodicSync() {
		super("VehicleLastReport", new VehicleLastReportDao());
		manager = VehicleLocationManager.getInstance();
		reportDao = (VehicleLastReportDao) dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			JSONArray recordKey = jsonObject.names();
			for (int j = 0; j < recordKey.length(); j++) {
				String jsonModel = recordKey.getString(j);
				JSONObject json = jsonObject.getJSONObject(jsonModel);
				
				VehicleLastReport report = reportDao.buildDto(json);
				if (Session.viewVehicles) {
					manager.updateLocation(report);
				}
				reportDao.insertOrReplace(report);
			}
		}
	}

	@Override
	public boolean fetchData(Context context) {
		if (Session.viewVehicles) {
			manager.updateDelays();
		}
		return super.fetchData(context);
	}
	
	
}
