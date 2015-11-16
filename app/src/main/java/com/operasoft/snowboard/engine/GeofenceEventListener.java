package com.operasoft.snowboard.engine;

import com.operasoft.snowboard.database.Geofence;

/**
 * This interface defines the list of events that are related to Geofences. 
 * Classes wishing to take actions when a Geofence-related event takes place
 * must implement this class and register with the GeofenceManager singleton
 * 
 * @author Christian
 *
 */
public interface GeofenceEventListener {

	/**
	 * This API is invoked when a new Geofence object has been added to the
	 * list being managed.
	 * 
	 * @param geofence The Geofence added
	 */
	public void geofenceAdded(Geofence geofence);
	
	/**
	 * This API is invoked when an existing Geofence object has been updated.
	 * 
	 * @param geofence The Geofence updated
	 */
	public void geofenceUpdated(Geofence geofence);

	/**
	 * This API is invoked when an existing Geofence object has been removed from
	 * the list being managed.
	 * 
	 * @param geofence The Geofence removed
	 */
	public void geofenceRemoved(Geofence geofence);
}
