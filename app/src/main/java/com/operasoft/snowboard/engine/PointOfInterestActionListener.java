package com.operasoft.snowboard.engine;

import com.operasoft.snowboard.database.Callout;
import com.operasoft.snowboard.database.Damage;
import com.operasoft.snowboard.database.Incident;
import com.operasoft.snowboard.database.ServiceActivity;

/**
 * This interface defines the list of actions that can be triggered against a POI by 
 * the driver on the Osmand layer.
 * 
 * Classes implementing this interface will typically update the database (local and remote)
 * as well as the POI manager accordingly.
 * @author Christian
 *
 */
public interface PointOfInterestActionListener {

	/**
	 * This method must be invoked when the driver accepts a service activity assigned to him.
	 * 
	 * @param poi The POI on which the action occurred.
	 * @param sa The service activity accepted by the driver
	 */
	public void serviceActivityAccepted(PointOfInterest poi, ServiceActivity sa);

	/**
	 * This method must be invoked when the driver refuses a service activity assigned to him.
	 * 
	 * @param poi The POI on which the action occurred.
	 * @param sa The service activity refused by the driver
	 */
	public void serviceActivityRefused(PointOfInterest poi, ServiceActivity sa);
	
	/**
	 * This method must be invoked when the driver selects a service activity where he is
	 * going at the moment.
	 * 
	 * @param poi The POI on which the action occurred.
	 * @param sa The service activity selected by the driver
	 */
	public void serviceActivityInDirection(PointOfInterest poi, ServiceActivity sa);
	
	/**
	 * This method must be invoked when the driver completes a service activity assigned to him.
	 * 
	 * @param poi The POI on which the action occurred.
	 * @param sa The service activity completed by the driver
	 */
	public void serviceActivityCompleted(PointOfInterest poi, ServiceActivity sa);
	
	/**
	 * This method must be invoked when the driver creates and completes a new service activity
	 * while on a service location.
	 * 
	 * @param poi The POI on which the action occurred.
	 * @param sa The service activity created by the driver
	 */
	public void serviceActivityCreated(PointOfInterest poi, ServiceActivity sa);
	
	/**
	 * This method must be invoked when the driver triggers a callout event for a given POI
	 * @param poi
	 */
	public void calloutCreated(PointOfInterest poi, Callout callout);
	
	/**
	 * This method must be invoked when the driver triggers an incident for a given POI
	 * @param poi
	 */
	public void incidentCreated(PointOfInterest poi);
	
	/**
	 * This method must be invoked when the driver triggers a "go back" event for a given POI
	 * @param poi
	 */
	public void goBackTriggered(PointOfInterest poi);
	
	/**
	 * This method must be invoked when the driver triggers a "cancel" event for a given POI that is in "Go Back" state
	 * @param poi
	 */
	public void goBackCancelTriggered(PointOfInterest poi);

	/**
	 * This method must be invoked when the driver triggeres the "go to" event for a given POI.
	 * @param poi
	 */
	public void goToTriggered(PointOfInterest poi);
	
	/**
	 * This method must be invoked when a vehicle is inside a SL for more than 10 seconds.
	 * @param poi
	 */
	public void serviceLocationCompleted(PointOfInterest poi);
	
	/**
	 * This method must be invoked when driver install a Marker.
	 * @param poi
	 */
	public void markerInstalled(PointOfInterest poi);
	
	/**
	 * This method must be invoked when driver Enroutes a SL
	 * @param poi
	 */
	public void serviceLocationToserviceActivityEnroute(PointOfInterest poi);
	
	/**
	 * This method must be invoked  when the driver triggeres the "Foreman" event for a given POI.
	 * @param poi
	 */
	public void ForemanDaily(PointOfInterest poi);
}
