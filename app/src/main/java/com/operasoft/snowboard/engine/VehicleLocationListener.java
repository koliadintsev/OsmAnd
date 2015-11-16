package com.operasoft.snowboard.engine;

import java.util.Collection;

public interface VehicleLocationListener {

	public void vehicleAdded(VehicleLocation vehicle);

	public void vehicleModified(VehicleLocation vehicle);

	public void vehicleRemoved(VehicleLocation vehicle);

	//To refresh list of vehicles to be shown on map
	public void vehicleListReloaded(Collection<VehicleLocation> activeVehicles);
}
