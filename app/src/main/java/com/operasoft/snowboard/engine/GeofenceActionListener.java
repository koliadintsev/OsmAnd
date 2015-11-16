package com.operasoft.snowboard.engine;

import com.operasoft.snowboard.database.Geofence;

/**
 * This interface defines the list of actions that are related to Geofences. 
 * Classes wishing to be informed when an action occurs on a Geofence must 
 * implement this interface and register with the GeofenceManager singleton.
 * 
 * @author Christian
 *
 */
public interface GeofenceActionListener {

	/**
	 * This API is invoked when the vehicle has just entered a Geofence
	 * 
	 * @param geofence
	 */
	public void geofenceEntered(Geofence geofence);
	
	/**
	 * This API is invoked when the vehicle has just left a Geofence it was
	 * previously in.
	 * 
	 * @param geofence
	 */
	public void geofenceExited(Geofence geofence);

	/**
	 * This API is invoked when the current geofence has been updated.
	 * @param geofence
	 */
	public void geofenceStatusUpdated(Geofence geofence);
}
