package com.operasoft.snowboard.engine;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehicleLastReport;
import com.operasoft.snowboard.database.VehicleLastReportDao;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.dbsync.CommonUtils;

public class VehicleLocationManager {

	/**
	 * The singleton instance
	 */
	static private VehicleLocationManager instance_s;

	private Map<String, VehicleLocation> vehiclesMap = new HashMap<String, VehicleLocation>();
	private VehicleLocationListener listener = null;
	private VehiclesDao vehicleDao = new VehiclesDao();
	private VehicleLastReportDao lastReportDao = new VehicleLastReportDao();
	
	/**
	 * Singleton pattern. This makes sure we have only one instance of this class instantiated in the entire application. >>>>>>> .r1695
	 */
	synchronized static public VehicleLocationManager getInstance() {
		if (instance_s == null) {
			instance_s = new VehicleLocationManager();
		}
		return instance_s;
	}

	/**
	 * Private constructor. Users of this class must call VehicleLocationManager.getInstance()
	 */
	private VehicleLocationManager() {
		init();
	}
	
	
	private void init() {
		List<VehicleLastReport> reports = lastReportDao.listAll();
		
		for (VehicleLastReport report : reports) {
			Vehicle vehicle = vehicleDao.getById(report.getId());
			// !!!! TODO Make sure the vehicle has not been deleted yet
			if (vehicle != null) {
				VehicleLocation location = new VehicleLocation(vehicle);
				location.setLastReport(report);
				vehiclesMap.put(report.getId(), location);
			} else if (vehicle == null) {
				Log.e("VehicleLocationManager", "Vehicle " + report.getId() + " not found!");
			}
		}
	}
	
	public void updateLocation(VehicleLastReport lastReport) {
		VehicleLocation location = vehiclesMap.get(lastReport.getId());
		if (location != null) {
			location.setLastReport(lastReport);
			
			if (listener != null) {
				listener.vehicleModified(location);
			}
		} else {
			Vehicle vehicle = vehicleDao.getById(lastReport.getId());
			// !!!! TODO Make sure the vehicle has not been deleted yet
			if (vehicle != null) {
				location = new VehicleLocation(vehicle);
				location.setLastReport(lastReport);
				vehiclesMap.put(lastReport.getId(), location);

				if (listener != null) {
					listener.vehicleAdded(location);
				}
			} else if (vehicle == null) {
				Log.e("VehicleLocationManager", "Vehicle " + lastReport.getId() + " not found!");
			}
		}
	}

	public void updateDelays() {
		Date now = CommonUtils.Now();
		for (VehicleLocation location : vehiclesMap.values()) {
			location.updateStatus(now);
		}
	}
	
	public VehicleLocationListener getListener() {
		return listener;
	}

	public void setListener(VehicleLocationListener listener) {
		this.listener = listener;
		vehicleListReloaded();
	}
	
	private void vehicleListReloaded() {
		Collection<VehicleLocation> activeVehicles = listActiveVehicles();
		listener.vehicleListReloaded(activeVehicles);
	}
		
		/**
		 * Return the entire list of Vehicles that must be displayed by the Osmand layer
		 * 
		 * @return
		 */
		public Collection<VehicleLocation> listActiveVehicles() {
			return vehiclesMap.values();
		}
}
