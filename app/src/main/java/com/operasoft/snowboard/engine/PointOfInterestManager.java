package com.operasoft.snowboard.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.operasoft.geom.Polygon;
import com.operasoft.geom.PolygonDetector;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.Divisions;
import com.operasoft.snowboard.database.DivisionsDao;
import com.operasoft.snowboard.database.EndRoute;
import com.operasoft.snowboard.database.EndRoutesDao;
import com.operasoft.snowboard.database.MarkerInstallation;
import com.operasoft.snowboard.database.MarkerInstallationDao;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.engine.PointOfInterest.PoiStatus;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.util.Session;

/**
 * This class is used to manage the list of POIs to display on Snowboard. It provides several APIs that need to be invoked when a
 * significant changes occurs, either through user interaction or as the result of a database synchronization
 * 
 * It implements the singleton pattern to make sure we only have a single instance across the entire application
 * 
 * @author Christian
 * 
 */
public class PointOfInterestManager {
	public enum Counters {
		SL_TOTAL, // Total number of service locations
		SL_COMPLETED, // Number of service locations that have been completed
		SL_GO_BACK, // Number of service locations that have been marked for revisit.
		SA_ASSIGNED, // Number of service activities that have not been accepted yet.
		SA_ACCEPTED, // Number of service activities that have been accepted
		SA_IN_DIRECTION, // Number of service activities that are being handled
		MISSION_ACTIVE, // Number of mission are active.
		MISSION_ENABLED, // Number of mission are enabled.
		MARKER_TOTAL // Number of marker installation left to complete.
	}

	private static final String CONSTRUCTION = "Construction";
	private static final String MAINTENANCE = "Maintenance";

	public enum Mode {
		NONE, CONSTRUCTION, MARKER, ROUTE, MAINTENANCE,
	}

	private Mode mMode = null;

	public void setModeNone() {
		setMode(Mode.NONE);
	}

	public void setModeConstruction() {
		setMode(Mode.CONSTRUCTION);
	}

	public void setModeMarker() {
		setMode(Mode.MARKER);
	}

	public void setModeRoute() {
		setMode(Mode.ROUTE);
	}

	public void setModeMaintenance() {
		setMode(Mode.MAINTENANCE);
	}

	public boolean isModeNone() {
		return isMode(Mode.NONE);
	}

	public boolean isModeConstruction() {
		return isMode(Mode.CONSTRUCTION);
	}

	public boolean isModeMarker() {
		return isMode(Mode.MARKER);
	}

	public boolean isModeRoute() {
		return isMode(Mode.ROUTE);
	}

	public boolean isModeMaintenance() {
		return isMode(Mode.MAINTENANCE);
	}

	private boolean isMode(Mode m) {
		if (mMode != null && mMode == m)
			return true;
		else
			return false;
	}

	private void setMode(Mode m) {
		onModeChange(mMode, m);
		mMode = m;
	}

	/**
	 * 
	 * @return
	 */
	public Route getCurrentRoute() {
		return Session.route;
	}

	public String getContractType() {
		String contractType = null;
		if (isModeConstruction())
			contractType = Divisions.CONSTRUCTION;
		else if (isModeMaintenance())
			contractType = Divisions.MAINTENANCE;
		return contractType;
	}
	
	private final ArrayList<EndRoute> mEndRoutes = new ArrayList<EndRoute>();

	public void removeEndRoutes() {
		for (EndRoute endroute : mEndRoutes)
			poiMap.remove(endroute.getId());
	}

	/**
	 * This method must be invoked when the driver selects a given route on the menu. 
	 * ServiceLocations associated
	 * @param route
	 * The route selected by the driver.
	 */

	public void selectRoute(Route route, int pastDays) {
		route.pastDaysServiced = pastDays;

		setModeRoute();

	}

	private void onModeChange(Mode oldMode, Mode newMode) {

		if (oldMode != null) {
			switch (oldMode) {

			case MAINTENANCE:
				detachAllServiceLocations();
				break;

			case CONSTRUCTION:
				detachAllServiceLocations();
				break;

			case MARKER:
				detachAllMarkerInstallation();
				break;

			case NONE:
				// on none mode, the POI can be added when enter service locations
				detachAllServiceLocations();
				break;

			case ROUTE:
				removeEndRoutes();
				detachAllServiceLocations();
				//				detachAllOthersServiceActivities();
				//				detachAllMarkerInstallation();
				if (newMode != Mode.ROUTE) {
					Session.route = null;
				}
				break;
			}
		}
		switch (newMode) {
		case MAINTENANCE:
			attachMaintenanceLocations();
			break;
		case CONSTRUCTION:
			attachConstructionLocations();
			break;
		case MARKER:
			attachAllMarkerInstallation();
			break;
		case NONE:

			Session.route = null;
			break;
		case ROUTE:
			attachRouteServiceLocations();
			break;
		}
		onPoiListReloaded();
		mMode = newMode;
		if (statuslistener != null) {
			if (newMode == Mode.NONE) {
				statuslistener.onNoneMode();
			} else {
				statuslistener.onAssistedMode();

			}
		}
	}

	public boolean isInBreadCrumbMode() {
		return inBreadCrumbMode;
	}

	public void setInBreadCrumbMode(boolean inBreadCrumbMode) {
		this.inBreadCrumbMode = inBreadCrumbMode;
	}

	/**
	 * The singleton instance
	 */
	static private PointOfInterestManager instance_s;

	/**
	 * The list of active POIs to present to the driver as a map. The key used is the ServiceLocation.id
	 */
	private final Map<String, PointOfInterest> poiMap = new LinkedHashMap<String, PointOfInterest>();

	/**
	 * The listener to invoke when a significant change is made to a POI. the Osmand layer must implement this listener and register with
	 * the PoiManager
	 */
	private final List<PointOfInterestEventListener> poiEventListeners = new ArrayList<PointOfInterestEventListener>();

	private final ServiceLocationDao slDao = new ServiceLocationDao();
	private final ServiceActivityDao saDao = new ServiceActivityDao();
	private final MarkerInstallationDao miDao = new MarkerInstallationDao();
	private final ContractsDao contractDao = new ContractsDao();

	/**
	 * Flag to remember that the driver is interested in MarkerInstallation updates
	 */
	private boolean inBreadCrumbMode = false;

	private ArrayList<TIT_RoutePoint> breadCrumbPoints = new ArrayList<TIT_RoutePoint>();

	private int activeMissionSA = Integer.MAX_VALUE;
	private PointOfInterest activeMissionPoi = null;

	/**
	 * List of Service Locations on selected route
	 */
	public List<ServiceLocation> routeSlList = new ArrayList<ServiceLocation>();

	/**
	 * Singleton pattern. This makes sure we have only one instance of this class instanciated in the entire application. >>>>>>> .r1695
	 */
	static public PointOfInterestManager getInstance() {
		if (instance_s != null) {
			return instance_s;
		}

		synchronized (PointOfInterestManager.class) {
			if (instance_s == null) {
				instance_s = new PointOfInterestManager();
			}
		}
		return instance_s;
	}

	/**
	 * Private constructor. Users of this class must call PointOfInterestManager.getInstance()
	 */
	private PointOfInterestManager() {
	}

	public int getActiveMissionSA() {
		return activeMissionSA;
	}

	/**
	 * Adds a new listener to receive notifications when a POI status changes
	 */
	public void addPoiEventListener(PointOfInterestEventListener listener) {
		if (!poiEventListeners.contains(listener)) {
			poiEventListeners.add(listener);
		}
	}

	public void removePoiEventListener(PointOfInterestEventListener listener) {
		poiEventListeners.remove(listener);
	}

	/**
	 * Return the entire list of POIs that must be displayed by the Osmand layer
	 * 
	 * @return
	 */
	public Collection<PointOfInterest> listActivePois() {
		return poiMap.values();
	}

	public void clear() {
		poiMap.clear();
		clearBreadCrumb();
		setModeNone();
		activeMissionSA = Integer.MAX_VALUE;
		activeMissionPoi = null;
	}

	/**
	 * This method is invoked when the DbSync layer receives a new service location for the route currently selected by the driver.
	 * 
	 * @param sl
	 */
	public void attachServiceLocation(ServiceLocation sl) {
		PointOfInterest poi = poiMap.get(sl.getId());
		if (poi == null) {
			poi = new PointOfInterest(sl.getId());
			poi.attachServiceLocation(sl);
			poiMap.put(poi.getId(), poi);
			notifyListener(poi, true);
		} else if (poi.attachServiceLocation(sl)) {
			notifyListener(poi);
		}
		// if state is none -> appel listener (none)

		if (statuslistener != null /* and status ==none */)
			statuslistener.onNewServiceLocationUpdate(sl);
	}

	public StatusListener statuslistener;

	public interface StatusListener {

		void onAssistedMode();

		void onNoneMode();

		void onNewServiceLocationUpdate(ServiceLocation sl);
	}

	public void markServiceLocationAsCompleted(PointOfInterest poi) {
		PointOfInterest dbPoi = poiMap.get(poi.getId());
		if (dbPoi != null) {
			if (dbPoi.markSLAsCompleted()) {
				notifyListener(dbPoi);
			}
		}
	}

	public void markServiceLocationAsCompletedNow(PointOfInterest poi) {
		PointOfInterest dbPoi = poiMap.get(poi.getId());
		if (dbPoi != null) {
			if (dbPoi.markSLAsCompletedNow()) {
				notifyListener(dbPoi);
			}
		}
	}

	public void markServiceLocationAsCompletedNow(ServiceLocation sl) {
		PointOfInterest dbPoi = poiMap.get(sl.getId());
		if (dbPoi != null) {
			if (dbPoi.markSLAsCompletedNow()) {
				notifyListener(dbPoi);
			}
		}
	}

	public void markServiceLocationAsCompleted(ServiceLocation sl) {
		PointOfInterest dbPoi = poiMap.get(sl.getId());
		if (dbPoi != null) {
			if (dbPoi.markSLAsCompleted()) {
				notifyListener(dbPoi);
			}
		}
	}

	public void markServiceLocationAsGoBack(PointOfInterest poi) {
		PointOfInterest dbPoi = poiMap.get(poi.getId());
		if (dbPoi != null) {
			if (dbPoi.markSLAsGoBack()) {
				notifyListener(dbPoi);
			}
		}
	}

	public void markServiceLocationAsGoBack(ServiceLocation sl) {
		PointOfInterest dbPoi = poiMap.get(sl.getId());
		if (dbPoi != null) {
			if (dbPoi.markSLAsGoBack()) {
				notifyListener(dbPoi);
			}
		}
	}

	/**
	 * This method must be invoked when the driver selects the "All Service Locations" from the route menu.
	 */
	public void attachAllServiceLocations() {
		List<ServiceLocation> list = slDao.listAll();
		attachServiceLocations(list);
		//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
	}

	/**
	 * This method must be invoked to detach all service locations from the list of POIs currently active. This must be invoked when the
	 * following events are triggered: - The driver selects a new route; before we attach the service locations of the new route - The
	 * driver selects "None" in the route menu.
	 */
	public void detachAllServiceLocations() {
		List<String> cleanupList = new ArrayList<String>();

		for (PointOfInterest poi : poiMap.values()) {
			poi.detachServiceLocation();

			// If the POI is no longer needed, get rid of it
			if (poi.getStatus() == PointOfInterest.PoiStatus.INACTIVE) {
				cleanupList.add(poi.getId());
			}
		}

		for (String key : cleanupList) {
			poiMap.remove(key);
		}
	}

	/**
	 * Returns the number of POIs that have a service location attached to them
	 */
	public Map<Counters, Integer> countPois() {
		Map<Counters, Integer> result = new HashMap<Counters, Integer>();
		result.put(Counters.SL_TOTAL, 0);
		result.put(Counters.SL_COMPLETED, 0);
		result.put(Counters.SL_GO_BACK, 0);
		result.put(Counters.SA_ASSIGNED, 0);
		result.put(Counters.SA_ACCEPTED, 0);
		result.put(Counters.SA_IN_DIRECTION, 0);
		result.put(Counters.MISSION_ACTIVE, 0);
		result.put(Counters.MISSION_ENABLED, 0);
		result.put(Counters.MARKER_TOTAL, 0);

		Integer value;

		for (PointOfInterest poi : poiMap.values()) {
			if (poi.isServiceLocationAttached()) {
				switch (poi.getSlStatus()) {
				case COMPLETED:
					value = result.get(Counters.SL_COMPLETED);
					result.put(Counters.SL_COMPLETED, ++value);
					break;
				case GO_BACK:
					value = result.get(Counters.SL_GO_BACK);
					result.put(Counters.SL_GO_BACK, ++value);
					break;
				default:
					value = result.get(Counters.SL_TOTAL);
					result.put(Counters.SL_TOTAL, ++value);
				}
			}

			if (poi.getStatus() == PoiStatus.MISSION_ACTIVE) {
				value = result.get(Counters.MISSION_ACTIVE);
				result.put(Counters.MISSION_ACTIVE, ++value);

				value = result.get(Counters.MISSION_ENABLED);
				result.put(Counters.MISSION_ENABLED, value + poi.missionListSize() - 1);
			} else {
				value = result.get(Counters.MISSION_ENABLED);
				result.put(Counters.MISSION_ENABLED, value + poi.missionListSize());
			}

			value = result.get(Counters.MARKER_TOTAL);
			result.put(Counters.MARKER_TOTAL, value + poi.markerListSize());

			value = result.get(Counters.SA_ASSIGNED);
			result.put(Counters.SA_ASSIGNED, value + poi.saAssignedSize());

			value = result.get(Counters.SA_ACCEPTED);
			result.put(Counters.SA_ACCEPTED, value + poi.saAcceptedSize());

			value = result.get(Counters.SA_IN_DIRECTION);
			result.put(Counters.SA_IN_DIRECTION, value + poi.saInDirectionSize());
		}

		return result;
	}

	/**
	 * This method must be invoked when a new service activity is received from the database synchronization process.
	 * 
	 * @param sa
	 */
	public void addServiceActivity(ServiceActivity sa) {
		String id = sa.getServiceLocationId();

		if (id == null) {
			Log.e("PoiManager", "ServiceActivity " + sa.getId() + " is not linked to a ServiceLocation");
			return;
		}

		// Whether or not we need to update the currently active mission
		boolean activeMissionUpdated = false;
		if (sa.isPrioritizedMissionSA()) {
			if (sa.getSequenceNumber() < activeMissionSA) {
				activeMissionUpdated = true;
				activeMissionSA = sa.getSequenceNumber();
				if (activeMissionPoi != null) {
					// The active mission POI has changed, tell the old POI to update its status
					if (activeMissionPoi.updatePoiStatus()) {
						notifyListener(activeMissionPoi);
					}
				}
			}
		}

		PointOfInterest poi = poiMap.get(id);
		if (poi == null) {
			poi = new PointOfInterest(id);
			poiMap.put(poi.getId(), poi);
			poi.attachServiceActivity(sa);
			notifyListener(poi, true);
		} else if (poi.attachServiceActivity(sa)) {
			notifyListener(poi);
		}

		if (activeMissionUpdated) {
			activeMissionPoi = poi;
		}
	}

	/**
	 * This method updates an existing SA currently in the POI list
	 * 
	 * @param sa
	 */
	public void updateServiceActivity(ServiceActivity sa) {
		String id = sa.getServiceLocationId();

		if (id == null) {
			throw new RuntimeException("ServiceActivity " + sa.getId() + " is not linked to a ServiceLocation");
		}
		PointOfInterest poi = poiMap.get(id);
		if (poi != null) {
			if (sa.isPrioritizedMissionSA() && (sa.getSequenceNumber() < activeMissionSA)) {
				activeMissionSA = sa.getSequenceNumber();
				// The active mission POI has changed, tell the old POI to update its status
				if (activeMissionPoi.updatePoiStatus()) {
					notifyListener(activeMissionPoi);
				}
				activeMissionPoi = poi;
			}
			if (poi.updateServiceActivity(sa)) {
				notifyListener(poi);
			} else {
				Log.w("PoiManager", "Failed to update ServiceActivity " + sa.getId());
			}
		} else {
			Log.w("PoiManager", "ServiceActivity " + sa.getId() + " is not attached to a POI... creating a new one");
			addServiceActivity(sa);
		}
	}

	/**
	 * This method must be invoked when a service activity has been completed or when it has been removed by the database synchronization
	 * process.
	 */
	public void removeServiceActivity(ServiceActivity sa) {
		String id = sa.getServiceLocationId();

		if (id == null) {
			throw new RuntimeException("ServiceActivity " + sa.getId() + " is not linked to a ServiceLocation");
		}
		PointOfInterest poi = poiMap.get(id);

		if (poi != null) {
			if (poi.detachServiceActivity(sa)) {
				notifyListener(poi);
			}
			if (sa.isPrioritizedMissionSA()) {
				if (activeMissionSA == sa.getSequenceNumber()) {
					// We need to find the next activeMissionSA
					updateNextMissionSA();
				}
			}
		}
	}

	public void attachAllServiceActivities() {
		// Whether or not we need to update the currently active mission
		boolean activeMissionUpdated = false;
		PointOfInterest nextActivePoi = null;

		if ((Session.getDriver() != null) && (Session.getVehicle() != null)) {
			List<ServiceActivity> activities = saDao.listActive(Session.getDriver().getId(), Session.getVehicle().getId());
			for (ServiceActivity sa : activities) {
				PointOfInterest poi = poiMap.get(sa.getServiceLocationId());
				if (poi == null) {
					poi = new PointOfInterest(sa.getServiceLocationId());
					poiMap.put(poi.getId(), poi);
				}
				poi.attachServiceActivity(sa);

				if (sa.isPrioritizedMissionSA()) {
					if (sa.getSequenceNumber() < activeMissionSA) {
						activeMissionSA = sa.getSequenceNumber();
						nextActivePoi = poi;
						activeMissionUpdated = true;
					}
				}
			}
			if (activeMissionUpdated) {
				if (activeMissionPoi != null) {
					// The active mission POI has changed, tell the old POI to update its status
					activeMissionPoi.updatePoiStatus();
				}
				activeMissionPoi = nextActivePoi;
				activeMissionPoi.updatePoiStatus();
			}

			//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
		} else {
			Log.w("PoiManager", "Session.driver = " + Session.getDriver() + ", Session.vehicle = " + Session.getVehicle());
		}
	}

	public void attachAllOthersServiceActivities() {
		if (Session.getVehicle() != null) {
			List<ServiceActivity> activities = saDao.listOthers(Session.getVehicle().getId());
			for (ServiceActivity sa : activities) {
				PointOfInterest poi = poiMap.get(sa.getServiceLocationId());
				if (poi == null) {
					poi = new PointOfInterest(sa.getServiceLocationId());
					poiMap.put(poi.getId(), poi);
				}
				poi.attachServiceActivity(sa);
			}

			//TODO 00 POI-RELOAD removed ! 	 poiListReloaded();
		} else {
			Log.w("PoiManager", "Session.vehicle = " + Session.getVehicle());
		}
	}

	public void detachAllOthersServiceActivities() {
		List<String> cleanupList = new ArrayList<String>();

		for (PointOfInterest poi : poiMap.values()) {
			poi.detachAllOthersServiceActivities();

			// If the POI is no longer needed, get rid of it
			if (poi.getStatus() == PointOfInterest.PoiStatus.INACTIVE) {
				cleanupList.add(poi.getId());
			}
		}

		for (String key : cleanupList) {
			poiMap.remove(key);
		}

	}

	/**
	 * This method must be invoked when the driver selects the "Marker Installation" from the route menu.
	 */
	public void attachAllMarkerInstallation() {
		List<MarkerInstallation> list = miDao.listActive();

		attachMarkerInstallations(list);
		//TODO 00 POI-RELOAD removed ! 		poiListReloaded();
	}

	/**
	 * This method must be invoked to detach all Marker installation from the list of POIs currently active. This must be invoked when the
	 * following events are triggered: - The driver selects "None" in the route menu.
	 */
	public void detachAllMarkerInstallation() {
		List<String> cleanupList = new ArrayList<String>();

		for (PointOfInterest poi : poiMap.values()) {
			poi.detachAllMarkerInstallations();

			// If the POI is no longer needed, get rid of it
			if (poi.getStatus() == PointOfInterest.PoiStatus.INACTIVE) {
				cleanupList.add(poi.getId());
			}
		}

		for (String key : cleanupList) {
			poiMap.remove(key);
		}

		//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
	}

	/**
	 * This method must be invoked when a marker installation needs to be added to the POI list
	 * 
	 * @param mi
	 */
	public void attachMarkerInstallation(MarkerInstallation mi) {
		if (this.isModeMarker()) {
			String id = contractDao.getServiceLocationIdForContract(mi.getContractId());
			if (id == null) {
				Log.e("PoiManager", "MarkerInstallation " + mi.getId() + " is not linked to a ServiceLocation");
				return;
			}
			ServiceLocation sl = slDao.getById(id);
			if (sl == null) {
				Log.e("PoiManager", "Cannot find ServiceLocation " + id + " in database");
			}

			PointOfInterest poi = poiMap.get(id);
			if (poi == null) {
				poi = new PointOfInterest(id);
				poi.attachMarkerInstallation(mi, sl);
				poiMap.put(poi.getId(), poi);
				notifyListener(poi, true);
			} else if (poi.attachMarkerInstallation(mi, sl)) {
				notifyListener(poi);
			}
		}
	}

	private void attachMarkerInstallations(List<MarkerInstallation> list) {
		for (MarkerInstallation mi : list) {
			String id = contractDao.getServiceLocationIdForContract(mi.getContractId());
			if (id == null) {
				Log.e("PoiManager", "MarkerInstallation " + mi.getId() + " is not linked to a ServiceLocation");
				continue;
			}
			ServiceLocation sl = slDao.getById(id);
			if (sl == null) {
				Log.e("PoiManager", "Cannot find ServiceLocation " + id + " in database");
			}

			PointOfInterest poi = poiMap.get(id);
			if (poi == null) {
				poi = new PointOfInterest(id);
				poiMap.put(poi.getId(), poi);
			}
			poi.attachMarkerInstallation(mi, sl);
		}
	}

	private void attachRouteServiceLocations() {
		// 1. Detach the service locations tied to the currently selected route.
		// 2. Update the route currently selected.
		// 3. Attach the service locations for the newly selected route
		if (Session.route != null) {
			if (Session.route.pastDaysServiced == -1) {
				routeSlList = slDao.findByRoute(Session.route.getId());
				attachServiceLocations(routeSlList);
			} else if (Session.route.pastDaysServiced == 0) {
				routeSlList = slDao.findByRoute(Session.route.getId());
				attachServiceLocationsResume(routeSlList);
			} else {
				routeSlList = slDao.findByRoute(Session.route.getId(), Session.route.pastDaysServiced);
				attachServiceLocationsResume(routeSlList);
			}
			// set poi and sl status
			// put a stop sign on the map if end route happened
			PointOfInterest endroutepoi = null;
			mEndRoutes.clear();
			mEndRoutes.addAll((new EndRoutesDao()).getLastVehicleEndRoutes(Session.route.getId()));
			for (EndRoute endRoute : mEndRoutes) {
				endroutepoi = new PointOfInterest(endRoute);
				poiMap.put(endroutepoi.getId(), endroutepoi);
			}
		}
	}

	/**
	 * This method must be invoked when a service activity has been completed or when it has been removed by the database synchronization
	 * process.
	 */
	public void detachMarkerInstallation(MarkerInstallation mi) {
		if (this.isModeMarker()) {
			ServiceLocation sl = slDao.findServiceLocationByContractId(mi.getContractId());

			if (sl == null) {
				Log.e("PoiManager", "MarkerInstallation " + mi.getId() + " is not linked to a ServiceLocation");
				return;
			}
			PointOfInterest poi = poiMap.get(sl.getId());
			if (poi != null) {
				if (poi.detachMarkerInstallation(mi)) {
					notifyListener(poi);
				}
			}
		}
	}

	public ArrayList<TIT_RoutePoint> getBreadCrumbPoints() {
		return breadCrumbPoints;
	}

	public void addpointToBreadCrumb(TIT_RoutePoint point) {
		if (breadCrumbPoints == null) {
			breadCrumbPoints = new ArrayList<TIT_RoutePoint>();
		}
		breadCrumbPoints.add(point);
	}

	public void clearBreadCrumb() {
		breadCrumbPoints = new ArrayList<TIT_RoutePoint>();
	}

	private void updateNextMissionSA() {
		int nextMissionSa = Integer.MAX_VALUE;
		PointOfInterest nextMission = null;

		for (PointOfInterest poi : poiMap.values()) {
			if ((poi.getStatus() == PoiStatus.MISSION_ENABLED) || (poi.getStatus() == PoiStatus.MISSION_ACTIVE)) {
				int value = poi.getCurrentServiceActivity().getSequenceNumber();
				if (value < nextMissionSa) {
					nextMissionSa = value;
					nextMission = poi;
				}
			}
		}

		activeMissionSA = nextMissionSa;
		activeMissionPoi = nextMission;
		if (activeMissionPoi != null) {
			activeMissionPoi.updatePoiStatus();
		}
	}

	private void notifyListener(PointOfInterest poi) {
		notifyListener(poi, false);
	}

	private void notifyListener(PointOfInterest poi, boolean added) {
		// If the POI is no longer active, it is time to get rid of it...
		if (poi.getStatus() == PointOfInterest.PoiStatus.INACTIVE) {
			poiMap.remove(poi.getId());
			for (PointOfInterestEventListener poiEventListener : poiEventListeners) {
				poiEventListener.poiRemoved(poi);
			}
		} else if (added) {
			for (PointOfInterestEventListener poiEventListener : poiEventListeners) {
				poiEventListener.poiAdded(poi);
			}
		} else {
			for (PointOfInterestEventListener poiEventListener : poiEventListeners) {
				poiEventListener.poiModified(poi);
			}
		}
	}

	public void onPoiListReloaded() {
		Log.d("POIMANAGER", "poiListReloaded");
		Collection<PointOfInterest> activePois = listActivePois();
		for (PointOfInterestEventListener poiEventListener : poiEventListeners) {
			poiEventListener.onPoiListReloaded(activePois);
		}
	}

	public void onPoiEnter(PointOfInterest pointOfInterest) {
		if (poiMap.containsKey(pointOfInterest.getId())) {
			notifyListener(pointOfInterest, false);
		} else if (isModeNone()) {
			poiMap.put(pointOfInterest.getId(), pointOfInterest);
			notifyListener(pointOfInterest, true);
		}
	}

	public void onPoiExit(PointOfInterest pointOfInterest) {
		if (isModeNone() && poiMap.containsKey(pointOfInterest.getId())) {
			poiMap.remove(pointOfInterest.getId());
			notifyListener(pointOfInterest, false);
		}
	}

	/**
	 * This method must be invoked when the driver selects the
	 * "Maintenances Locations" from the route menu.
	 * pickup only service location with active contracts 
	 */
	public void attachMaintenanceLocations() {
		ArrayList<ServiceLocation> listMaintenance = new ArrayList<ServiceLocation>();
		listMaintenance.addAll(slDao.findAllServiceLocationByDivisionType(Divisions.MAINTENANCE));
		attachServiceLocations(listMaintenance);
		//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
	}

	/**
	 * This method must be invoked when the driver selects the
	 * "Construction Locations" from the route menu.
	 */
	public void attachConstructionLocations() {
		ContractsDao contractsDao = new ContractsDao();

		/*
		 * List<ServiceLocation> listMaintenance = new ArrayList<ServiceLocation>();
				List<Contract> contractList = contractsDao.listAll();
				for (Contract contract : contractList) {
					if (contract == null)
						continue;
					DivisionsDao divisionDao = new DivisionsDao();
					Divisions divisions = divisionDao.getById(contract.getDivisionId());
					if (divisions == null)
						continue;
					if (divisions.getType().equals(CONSTRUCTION))
						try {
							listMaintenance.add(slDao.getById(contract.getService_location_id()));
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
				*/
		final List<ServiceLocation> listMaintenance = slDao.findAllServiceLocationByDivisionType(CONSTRUCTION);

		attachConstructionLocations(listMaintenance);
	}

	public PointOfInterest getPOI(String id) {
		PointOfInterest poi = null;
		poi = poiMap.get(id);
		return poi;

	}

	private PoiPolygonConnection mConnection;
	private PolygonDetector mPolyDetector;
	private static final String TAG = "poiManager";

	public void connect(PolygonDetector detector, StatusListener statusListener) {
		if (mConnection != null) {
			Log.e(TAG, "PoiPolygonConnection already connected!");
			return;
		}
		mPolyDetector = detector;
		mConnection = new PoiPolygonConnection(this);
		mConnection.connect(mPolyDetector, statusListener);
		mPolyDetector.detectorListener = detectionListener;
		mPolyDetector.start();
		// by defaut set the none mode 
		if (mMode == null) {
			setModeNone();
		}

	}

	public EnterExitPoiListener enterExitPoiListener;

	public interface EnterExitPoiListener {

		void onEnter(Polygon polygon, PointOfInterest poi);

		void onDelayExceeded(Polygon polygon, PointOfInterest poi);

		void onExit(Polygon polygon, PointOfInterest poi);

	}

	/**
	 * @param serviceLocationId
	 * @return PointOfInterest from serviceLocationId
	 */
	private PointOfInterest findServiceLocationPoi(String serviceLocationId) {
		PointOfInterest poi = null;
		final ServiceLocation serviceLocation = (new ServiceLocationDao()).getById(serviceLocationId);
		if (serviceLocation != null) {
			poi = new PointOfInterest(serviceLocationId);
			poi.attachServiceLocation(serviceLocation);
		}
		return poi;
	}

	PointOfInterest insidePolygonPoi = null;

	public PointOfInterest getInsidePolygonPoi() {
		return insidePolygonPoi;
	}

	PolygonDetector.PolygonDetectorListener detectionListener = new PolygonDetector.PolygonDetectorListener() {

		long insidePolygonTime = 0l;

		@Override
		public void onEnter(Polygon polygon) {
			Log.d(TAG, "Enter polygon : " + polygon.getDisplayName());
			// onPoiEnter should be called in onDetect method
			// to cover the scenario when inside a polygon, the user switch to another mode
		}

		/* more than 10 seconds the POI becomes completed
		 * @see com.operasoft.geom.PolygonDetector.PolygonDetectorListener#onDetect(com.operasoft.geom.Polygon)
		 */
		@Override
		public void onDetect(Polygon p) {
			if (insidePolygonPoi == null) {
				insidePolygonPoi = poiMap.get(p.getId());
				if (insidePolygonPoi == null) {
					insidePolygonPoi = findServiceLocationPoi(p.getId());
					// we have to add manually the POI in order to change status
				}
				if (insidePolygonPoi != null) {
					onPoiEnter(insidePolygonPoi);
					insidePolygonTime = System.currentTimeMillis();
					if (enterExitPoiListener != null) {
						enterExitPoiListener.onEnter(p, insidePolygonPoi);
					}
				}
			}

			final long delayToCompleSL = 10000l;
			if (insidePolygonTime != 0 && (System.currentTimeMillis() - insidePolygonTime > delayToCompleSL)) {
				insidePolygonTime = 0;
				markServiceLocationAsCompleted(insidePolygonPoi);
				enterExitPoiListener.onDelayExceeded(p, insidePolygonPoi);
				//notify the new status
				notifyListener(insidePolygonPoi, false);
			}

		}

		@Override
		public void onExit(Polygon polygon) {
			Log.d(TAG, "Exit polygon : " + polygon.getDisplayName());
			if (enterExitPoiListener != null && insidePolygonPoi != null)
				enterExitPoiListener.onExit(polygon, insidePolygonPoi);

			insidePolygonPoi = null;
			insidePolygonTime = 0l;
		}

	};

	public void disconnectPolygonDetector() {
		if (mConnection == null) {
			Log.e(TAG, "PoiPolygonConnection not connected!");
			return;
		}
		mConnection.disconnect();
		mPolyDetector.stop();
		mConnection = null;
		mPolyDetector = null;
	}

	/*
	 * 
	 * 
	IF Route has NO Service Locations Assigned, 
	OR the none of the SL's have been serviced in the past 7 days or less, 
	then bypass this step, and just show the route.
	IF the route has one or more service locations, AND, 
	one of those SL's has been serviced in the past 7 days or less, then:
	This new prompt indicates:
	Please select from which day of the past week you want serviced locations to indicate as 
	<show green circle with white check mark> the SL that have been serviced since day of the week selected.. 
	then below, put a button containing each day of the week: 
	Monday - Tuesday - Wednesday - Thursday - Friday - Saturday - Sunday - None. 
	(See attached Selection_of_last_serviced_date_for_Resume_route.png as a reference idea, but use text herein.)


	Regardless of whether or not the route has SL or not, or if they were serviced in the past 7 days or not:

	- IF the End_Route table indicates that the selected route was ended within the past 72 hours, then display that End_Route as a POI indicated as the 'Stop Sign' on the right of the (stop-sign.jpg) image included below, with two lines of text showing at the same zoom level that make SL's addresses show. 
	Line1 Date & Time of the 'End_Route' 
	Line 2: User First Name, Last Name from the 'driver_name' of the 'End_Route' table.

	** Please ensure that if a user is currently in a route, and uses the route selector button in the menu to select another route, force an 'End_Route' entry as though they clicked on the 'End Route' button.

	** Please ensure that if a user Logs Off from a tablet while they are currently in a route, also ensure that you force an 'End_Route' entry as though they clicked on the 'End Route' button.
	 * 
	 */

	/**
	 * Private method used to attach a list of service locations to a POIs
	 */
	private void attachServiceLocations(List<ServiceLocation> list) {
		for (ServiceLocation sl : list) {
			try {
				PointOfInterest poi = poiMap.get(sl.getId());
				if (poi == null) {
					poi = new PointOfInterest(sl.getId());
					poiMap.put(poi.getId(), poi);
				}
				poi.attachServiceLocation(sl);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
	}

	/**
	 * Private method used to attach a list of construction service locations to a POIs
	 */
	private void attachConstructionLocations(List<ServiceLocation> list) {
		for (ServiceLocation sl : list) {
			try {
				PointOfInterest poi = poiMap.get(sl.getId());
				if (poi == null) {
					poi = new PointOfInterest(sl.getId());
					poiMap.put(poi.getId(), poi);
				}
				poi.attachConstructionLocation(sl);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Private method used to attach a list of service locations to a POIs
	 */
	private void attachServiceLocationsResume(List<ServiceLocation> list) {
		for (ServiceLocation sl : list) {
			try {
				PointOfInterest poi = poiMap.get(sl.getId());
				if (poi == null) {
					poi = new PointOfInterest(sl.getId());
					poiMap.put(poi.getId(), poi);
				}
				poi.attachServiceLocationWithStatus(sl);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
	}

	/**
	 * This method must be invoked when the driver selects the
	 * "Construction Locations" from the route menu.
	 */
	public void attachConstructionLocationsTemp() {
		ContractsDao contractsDao = new ContractsDao();
		ArrayList<ServiceLocation> listMaintenance = new ArrayList<ServiceLocation>();
		List<Contract> contractList = contractsDao.listAll();
		for (Contract contract : contractList) {
			if (contract == null)
				continue;
			DivisionsDao divisionDao = new DivisionsDao();
			Divisions divisions = divisionDao.getById(contract.getDivisionId());
			if (divisions == null)
				continue;
			if (divisions.getType().equals(CONSTRUCTION))
				try {
					listMaintenance.add(slDao.getById(contract.getService_location_id()));
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		attachServiceLocations(listMaintenance);
		//TODO 00 POI-RELOAD removed ! 	poiListReloaded();
	}
}
