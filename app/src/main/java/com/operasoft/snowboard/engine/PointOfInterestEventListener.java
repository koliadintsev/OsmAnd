package com.operasoft.snowboard.engine;

import java.util.Collection;

/**
 * This interface defines a listener that the Osmand Layer can hook on to
 * to be notified when a POI change needs to be brought to the driver's attention.
 * @author Christian
 *
 */
public interface PointOfInterestEventListener {
	
	/**
	 * This method is invoked when a new POI has been added to the active POI list
	 */
	public void poiAdded(PointOfInterest poi);
	
	/**
	 * This method is invoked when an existing POI on the active POI list has been modified
	 * @param poi
	 */
	public void poiModified(PointOfInterest poi);

	/**
	 * This method is invoked when an existing POI has been removed from the active POI list
	 * @param poi
	 */
	public void poiRemoved(PointOfInterest poi);
	
	/**
	 * This method is invoked when the entire list of active POIs has been updated
	 * @param activePois The list of POIs currently active
	 */
	public void onPoiListReloaded(Collection<PointOfInterest> activePois);
}
