package net.osmand.plus.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.osmand.GPXUtilities.GPXFile;
import net.osmand.LogUtil;
import net.osmand.OsmAndFormatter;
import net.osmand.access.AccessibleToast;
import net.osmand.osm.LatLon;
import net.osmand.osm.MapUtils;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.OsmandSettings.MetricsConstants;
import net.osmand.plus.activities.ApplicationMode;
import net.osmand.plus.routing.RouteCalculationResult.NextDirectionInfo;
import net.osmand.plus.routing.RouteProvider.GPXRouteParams;
import net.osmand.plus.routing.RouteProvider.RouteService;
import net.osmand.plus.voice.CommandPlayer;
import net.osmand.router.Interruptable;
import net.osmand.router.RouteSegmentResult;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.widget.Toast;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.util.Session;

public class RoutingHelper {

	private static final org.apache.commons.logging.Log log = LogUtil.getLog(RoutingHelper.class);

	public static interface IRouteInformationListener {

		public void newRouteIsCalculated(boolean newRoute);

		public void routeWasCancelled();
	}

	private final float POSITION_TOLERANCE = 60;

	private List<IRouteInformationListener> listeners = new ArrayList<IRouteInformationListener>();

	private Context context;

	private boolean isFollowingMode = false;

	private GPXRouteParams currentGPXRoute = null;

	private RouteCalculationResult route = new RouteCalculationResult("");

	private LatLon finalLocation;
	private Location lastProjection;
	private Location lastFixedLocation;

	private RouteRecalculationThread currentRunningJob;
	private long lastTimeEvaluatedRoute = 0;
	private int evalWaitInterval = 3000;

	private ApplicationMode mode;
	private OsmandSettings settings;

	private RouteProvider provider = new RouteProvider();
	private VoiceRouter voiceRouter;

	private Handler uiHandler;
	private boolean makeUturnWhenPossible = false;
	private long makeUTwpDetected = 0;

	public boolean makeUturnWhenPossible() {
		return makeUturnWhenPossible;
	}

	public RoutingHelper(OsmandSettings settings, Context context, CommandPlayer player) {
		this.settings = settings;
		this.context = context;
		Session.context = context;
		voiceRouter = new VoiceRouter(this, player);
		uiHandler = new Handler();
	}

	public boolean isFollowingMode() {
		return isFollowingMode;
	}

	public void setFollowingMode(boolean isFollowingMode) {
		this.isFollowingMode = isFollowingMode;
	}

	public synchronized void setFinalAndCurrentLocation(LatLon finalLocation, Location currentLocation, GPXRouteParams gpxRoute) {
		clearCurrentRoute(finalLocation);
		currentGPXRoute = gpxRoute;
		// to update route
		setCurrentLocation(currentLocation, false);

	}

	public synchronized void setRoute(RouteCalculationResult route, Location currentLocation, LatLon finalLocation) {
		clearCurrentRoute(finalLocation);
		setNewRoute(route, currentLocation);
	}

	public synchronized void clearCurrentRoute(LatLon newFinalLocation) {
		route = new RouteCalculationResult("");
		makeUturnWhenPossible = false;
		evalWaitInterval = 3000;
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				for (IRouteInformationListener l : listeners) {
					l.routeWasCancelled();
				}
			}
		});
		this.finalLocation = newFinalLocation;
		if (currentRunningJob != null) {
			currentRunningJob.stopCalculation();
		}
		if (newFinalLocation == null) {
			settings.FOLLOW_THE_ROUTE.set(false);
			settings.FOLLOW_THE_GPX_ROUTE.set(null);
			// clear last fixed location
			this.lastProjection = null;
			this.isFollowingMode = false;
		}
	}

	public GPXRouteParams getCurrentGPXRoute() {
		return currentGPXRoute;
	}

	public List<Location> getCurrentRoute() {
		return currentGPXRoute == null || currentGPXRoute.points.isEmpty() ? route.getImmutableLocations() : Collections.unmodifiableList(currentGPXRoute.points);
	}

	public void setAppMode(ApplicationMode mode) {
		this.mode = mode;
		voiceRouter.updateAppMode();
	}

	public ApplicationMode getAppMode() {
		return mode;
	}

	public LatLon getFinalLocation() {
		return finalLocation;
	}

	public boolean isRouteCalculated() {
		return route.isCalculated();
	}

	public VoiceRouter getVoiceRouter() {
		return voiceRouter;
	}

	public Location getLastProjection() {
		return lastProjection;
	}

	public void addListener(IRouteInformationListener l) {
		listeners.add(l);
	}

	public boolean removeListener(IRouteInformationListener l) {
		return listeners.remove(l);
	}

	public Location setCurrentLocation(Location currentLocation, boolean returnUpdatedLocation) {
		Location locationProjection = currentLocation;

		if (finalLocation == null || currentLocation == null) {
			makeUturnWhenPossible = false;
			return locationProjection;
		}
		float posTolerance = POSITION_TOLERANCE;
		if (currentLocation.hasAccuracy()) {
			posTolerance = POSITION_TOLERANCE / 2 + currentLocation.getAccuracy();
		}
		boolean calculateRoute = false;
		synchronized (this) {
			// 0. Route empty or needs to be extended? Then re-calculate route.
			if (route.isEmpty()) {
				calculateRoute = true;
			} else {
				// 1. Update current route position status according to latest received location
				boolean finished = updateCurrentRouteStatus(currentLocation, posTolerance);
				if (finished) {
					return null;
				}
				List<Location> routeNodes = route.getImmutableLocations();
				int currentRoute = route.currentRoute;

				// 2. Analyze if we need to recalculate route
				// >100m off current route (sideways)
				if (currentRoute > 0) {
					double dist = getOrthogonalDistance(currentLocation, routeNodes.get(currentRoute - 1), routeNodes.get(currentRoute));
					if (dist > 1.7 * posTolerance) {
						log.info("Recalculate route, because correlation  : " + dist); //$NON-NLS-1$
						calculateRoute = true;
					}
				}
				// 3. Identify wrong movement direction (very similar to 2?)
				Location next = route.getNextRouteLocation();
				boolean wrongMovementDirection = checkWrongMovementDirection(currentLocation, next);
				if (wrongMovementDirection && currentLocation.distanceTo(routeNodes.get(currentRoute)) > 2 * posTolerance) {
					log.info("Recalculate route, because wrong movement direction: " + currentLocation.distanceTo(routeNodes.get(currentRoute))); //$NON-NLS-1$
					calculateRoute = true;
				}
				// 4. Identify if UTurn is needed
				boolean uTurnIsNeeded = identifyUTurnIsNeeded(currentLocation, posTolerance);
				// 5. Update Voice router
				boolean inRecalc = calculateRoute || isRouteBeingCalculated();
				if (!inRecalc && !uTurnIsNeeded) {
					voiceRouter.updateStatus(currentLocation);
				} else if (uTurnIsNeeded) {
					voiceRouter.makeUTStatus();
				}

				// calculate projection of current location
				if (currentRoute > 0) {
					locationProjection = new Location(currentLocation);
					Location nextLocation = routeNodes.get(currentRoute);
					LatLon project = getProject(currentLocation, routeNodes.get(currentRoute - 1), routeNodes.get(currentRoute));
					//TODO 00 confirm with Narendra - remove the adjustment on the road of current location 
					//locationProjection.setLatitude(project.getLatitude());
					//locationProjection.setLongitude(project.getLongitude());
					// we need to update bearing too
					float bearingTo = locationProjection.bearingTo(nextLocation);
					locationProjection.setBearing(bearingTo);
				}
			}
			lastFixedLocation = currentLocation;
			lastProjection = locationProjection;
		}

		if (calculateRoute) {
			recalculateRouteInBackground(currentLocation, finalLocation, currentGPXRoute, route.isCalculated() ? route : null);
		}

		double projectDist = mode == ApplicationMode.CAR ? posTolerance : posTolerance / 2;
		if (returnUpdatedLocation && locationProjection != null && currentLocation.distanceTo(locationProjection) < projectDist) {
			return locationProjection;
		} else {
			return currentLocation;
		}
	}

	private static double getOrthogonalDistance(Location loc, Location from, Location to) {
		return MapUtils.getOrthogonalDistance(loc.getLatitude(), loc.getLongitude(), from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
	}

	private static LatLon getProject(Location loc, Location from, Location to) {
		return MapUtils.getProjection(loc.getLatitude(), loc.getLongitude(), from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
	}

	private static int lookAheadFindMinOrthogonalDistance(Location currentLocation, List<Location> routeNodes, int currentRoute, int iterations) {
		double newDist;
		double dist = Double.POSITIVE_INFINITY;
		int index = currentRoute;
		while (iterations > 0 && currentRoute + 1 < routeNodes.size()) {
			newDist = getOrthogonalDistance(currentLocation, routeNodes.get(currentRoute), routeNodes.get(currentRoute + 1));
			if (newDist < dist) {
				index = currentRoute;
				dist = newDist;
			}
			currentRoute++;
			iterations--;
		}
		return index;
	}

	private boolean updateCurrentRouteStatus(Location currentLocation, float posTolerance) {
		List<Location> routeNodes = route.getImmutableLocations();
		int currentRoute = route.currentRoute;
		// 1. Try to proceed to next point using orthogonal distance (finding minimum orthogonal dist)
		while (currentRoute + 1 < routeNodes.size()) {
			double dist = currentLocation.distanceTo(routeNodes.get(currentRoute));
			if (currentRoute > 0) {
				dist = getOrthogonalDistance(currentLocation, routeNodes.get(currentRoute - 1), routeNodes.get(currentRoute));
			}
			boolean processed = false;
			// if we are still too far try to proceed many points
			// if not then look ahead only 3 in order to catch sharp turns
			boolean longDistance = dist >= 250;
			int newCurrentRoute = lookAheadFindMinOrthogonalDistance(currentLocation, routeNodes, currentRoute, longDistance ? 15 : 8);
			double newDist = getOrthogonalDistance(currentLocation, routeNodes.get(newCurrentRoute), routeNodes.get(newCurrentRoute + 1));
			if (longDistance) {
				if (newDist < dist) {
					if (log.isDebugEnabled()) {
						log.debug("Processed by distance : (new) " + newDist + " (old) " + dist); //$NON-NLS-1$//$NON-NLS-2$
					}
					processed = true;
				}
			} else if (newDist < dist || newDist < 10) {
				// newDist < 10 (avoid distance 0 till next turn)
				if (dist > posTolerance) {
					processed = true;
					if (log.isDebugEnabled()) {
						log.debug("Processed by distance : " + newDist + " " + dist); //$NON-NLS-1$//$NON-NLS-2$
					}
				} else {
					// case if you are getting close to the next point after turn
					// but you have not yet turned (could be checked bearing)
					if (currentLocation.hasBearing() || lastFixedLocation != null) {
						float bearingToRoute = currentLocation.bearingTo(routeNodes.get(currentRoute));
						float bearingRouteNext = routeNodes.get(newCurrentRoute).bearingTo(routeNodes.get(newCurrentRoute + 1));
						float bearingMotion = currentLocation.hasBearing() ? currentLocation.getBearing() : lastFixedLocation.bearingTo(currentLocation);
						double diff = Math.abs(MapUtils.degreesDiff(bearingMotion, bearingToRoute));
						double diffToNext = Math.abs(MapUtils.degreesDiff(bearingMotion, bearingRouteNext));
						if (diff > diffToNext) {
							if (log.isDebugEnabled()) {
								log.debug("Processed point bearing deltas : " + diff + " " + diffToNext);
							}
							processed = true;
						}
					}
				}
			}
			if (processed) {
				// that node already passed
				route.updateCurrentRoute(newCurrentRoute + 1);
				currentRoute = newCurrentRoute + 1;
			} else {
				break;
			}
		}

		// 2. check if destination found
		Location lastPoint = routeNodes.get(routeNodes.size() - 1);
		if (currentRoute > routeNodes.size() - 3 && currentLocation.distanceTo(lastPoint) < posTolerance * 1.5) {
			showMessage(context.getString(R.string.arrived_at_destination));
			voiceRouter.arrivedDestinationPoint();
			clearCurrentRoute(null);
			Session.navRoutePointsCount = 0;
			return true;
		}
		return false;
	}

	public boolean identifyUTurnIsNeeded(Location currentLocation, float posTolerance) {
		if (finalLocation == null || currentLocation == null || !route.isCalculated()) {
			this.makeUturnWhenPossible = false;
			return makeUturnWhenPossible;
		}
		boolean makeUturnWhenPossible = false;
		if (currentLocation.hasBearing()) {
			float bearingMotion = currentLocation.getBearing();
			Location nextRoutePosition = route.getNextRouteLocation();
			float bearingToRoute = currentLocation.bearingTo(nextRoutePosition);
			double diff = MapUtils.degreesDiff(bearingMotion, bearingToRoute);
			// 7. Check necessity for unscheduled U-turn, Issue 863
			if (Math.abs(diff) > 135f) {
				float d = currentLocation.distanceTo(nextRoutePosition);
				// 60m tolerance to allow for GPS inaccuracy
				if (d > posTolerance) {
					if (makeUTwpDetected == 0) {
						makeUTwpDetected = System.currentTimeMillis();
						// require 5 sec since first detection, to avoid false positive announcements
					} else if ((System.currentTimeMillis() - makeUTwpDetected > 5000)) {
						makeUturnWhenPossible = true;
						//log.info("bearingMotion is opposite to bearingRoute"); //$NON-NLS-1$
					}
				}
			} else {
				makeUTwpDetected = 0;
			}
		}
		this.makeUturnWhenPossible = makeUturnWhenPossible;
		return makeUturnWhenPossible;
	}

	/**
	 * Wrong movement direction is considered when between 
	 * current location bearing (determines by 2 last fixed position or provided)
	 * and bearing from currentLocation to next (current) point
	 * the difference is more than 90 degrees
	 */
	public boolean checkWrongMovementDirection(Location currentLocation, Location nextRouteLocation) {
		// measuring without bearing could be really error prone (with last fixed location)
		// this code has an effect on route recalculation which should be detected without mistakes
		if (currentLocation.hasBearing() && nextRouteLocation != null) {
			float bearingMotion = currentLocation.getBearing();
			float bearingToRoute = currentLocation.bearingTo(nextRouteLocation);
			double diff = MapUtils.degreesDiff(bearingMotion, bearingToRoute);
			if (Math.abs(diff) > 60f) {
				return true;
			}
		}
		return false;
	}

	private synchronized void setNewRoute(RouteCalculationResult res, Location start) {
		final boolean newRoute = !this.route.isCalculated();
		route = res;
		if (isFollowingMode) {
			if (lastFixedLocation != null) {
				start = lastFixedLocation;
			}
			// try remove false route-recalculated prompts by checking direction to second route node
			boolean wrongMovementDirection = false;
			List<Location> routeNodes = res.getImmutableLocations();
			if (routeNodes != null && !routeNodes.isEmpty()) {
				int newCurrentRoute = lookAheadFindMinOrthogonalDistance(start, routeNodes, res.currentRoute, 15);
				if (newCurrentRoute + 1 < routeNodes.size()) {
					// This check is valid for Online/GPX services (offline routing is aware of route direction)
					wrongMovementDirection = checkWrongMovementDirection(start, routeNodes.get(newCurrentRoute + 1));
					// set/reset evalWaitInterval only if new route is in forward direction
					if (!wrongMovementDirection) {
						evalWaitInterval = 3000;
					} else {
						evalWaitInterval = evalWaitInterval * 3 / 2;
						evalWaitInterval = Math.min(evalWaitInterval, 120000);
					}
				}
			}

			// trigger voice prompt only if new route is in forward direction (but see also additional 60sec timer for this message in
			// voiceRouter)
			// If route is in wrong direction after one more setLocation it will be recalculated
			if (!wrongMovementDirection || newRoute) {
				voiceRouter.newRouteIsCalculated(newRoute);
			}
		}

		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				for (IRouteInformationListener l : listeners) {
					l.newRouteIsCalculated(newRoute);
				}
			}
		});

	}

	public synchronized int getLeftDistance() {
		return route.getDistanceToFinish(lastFixedLocation);
	}

	public synchronized int getLeftTime() {
		return route.getLeftTime(lastFixedLocation);
	}

	public String getGeneralRouteInformation() {
		int dist = getLeftDistance();
		int hours = getLeftTime() / (60 * 60);
		int minutes = (getLeftTime() / 60) % 60;
		return context.getString(R.string.route_general_information, OsmAndFormatter.getFormattedDistance(dist, context), hours, minutes);
	}

	public Location getLocationFromRouteDirection(RouteDirectionInfo i) {
		return route.getLocationFromRouteDirection(i);
	}

	public synchronized NextDirectionInfo getNextRouteDirectionInfo(NextDirectionInfo info, boolean toSpeak) {
		NextDirectionInfo i = route.getNextRouteDirectionInfo(info, lastProjection, toSpeak);
		if (i != null) {
			i.imminent = voiceRouter.calculateImminent(i.distanceTo, lastProjection);
		}
		return i;
	}

	public synchronized AlarmInfo getMostImportantAlarm(MetricsConstants mc, boolean showCameras) {
		float mxspeed = route.getCurrentMaxSpeed();
		AlarmInfo speedAlarm = null;
		if (mxspeed != 0 && lastProjection != null && lastProjection.hasSpeed()) {
			float delta = 5f / 3.6f;
			if (lastProjection.getSpeed() > mxspeed + delta) {
				int speed;
				if (mc == MetricsConstants.KILOMETERS_AND_METERS) {
					speed = Math.round(mxspeed * 3.6f);
				} else {
					speed = Math.round(mxspeed * 3.6f / 1.6f);
				}
				speedAlarm = AlarmInfo.createSpeedLimit(speed);
			}
		}
		return route.getMostImportantAlarm(lastProjection, speedAlarm, showCameras);
	}

	public String formatStreetName(String name, String ref) {
		if (name != null && name.length() > 0) {
			if (ref != null && ref.length() > 0) {
				name = ref + " " + name;
			}
			return name;
		} else {
			return ref;
		}
	}

	public synchronized String getCurrentName() {
		NextDirectionInfo n = getNextRouteDirectionInfo(new NextDirectionInfo(), false);
		if ((n.imminent == 0 || n.imminent == 1) && (n.directionInfo != null)) {
			String nm = n.directionInfo.getStreetName();
			String rf = n.directionInfo.getRef();
			return formatStreetName(nm, rf);
		}
		RouteSegmentResult rs = route.getCurrentSegmentResult();
		if (rs != null) {
			String nm = rs.getObject().getName();
			String rf = rs.getObject().getRef();
			return formatStreetName(nm, rf);
		}
		return null;
	}

	public synchronized NextDirectionInfo getNextRouteDirectionInfoAfter(NextDirectionInfo previous, NextDirectionInfo to, boolean toSpeak) {
		NextDirectionInfo i = route.getNextRouteDirectionInfoAfter(previous, to, toSpeak);
		if (i != null) {
			i.imminent = voiceRouter.calculateImminent(i.distanceTo, null);
		}
		return i;
	}

	public List<RouteDirectionInfo> getRouteDirections() {
		return route.getRouteDirections();
	}

	private class RouteRecalculationThread extends Thread implements Interruptable {

		private final Location start;
		private final LatLon end;
		private final GPXRouteParams gpxRoute;
		private final RouteCalculationResult previousRoute;
		private RouteService service;
		private boolean interrupted = false;

		public RouteRecalculationThread(String name, Location start, LatLon end, GPXRouteParams gpxRoute, RouteCalculationResult previousRoute) {
			super(name);
			this.start = start;
			this.end = end;
			this.gpxRoute = gpxRoute;
			this.previousRoute = previousRoute;
			service = settings.ROUTER_SERVICE.get();

		}

		public void stopCalculation() {
			interrupted = true;
		}

		@Override
		public boolean isCancelled() {
			return interrupted;
		}

		@Override
		public void run() {
			boolean leftSide = settings.LEFT_SIDE_NAVIGATION.get();
			boolean fastRoute = settings.FAST_ROUTE_MODE.get();
			RouteCalculationResult res = provider.calculateRouteImpl(start, end, mode, service, context, gpxRoute, previousRoute, fastRoute, leftSide, this);
			if (interrupted) {
				currentRunningJob = null;
				return;
			}

			synchronized (RoutingHelper.this) {
				if (res.isCalculated()) {
					setNewRoute(res, start);
				} else {
					evalWaitInterval = evalWaitInterval * 3 / 2;
					evalWaitInterval = Math.min(evalWaitInterval, 120000);
				}
				currentRunningJob = null;
			}

			if (res.isCalculated()) {
				showMessage(context.getString(R.string.new_route_calculated_dist) + ": " + OsmAndFormatter.getFormattedDistance(res.getWholeDistance(), context)); //$NON-NLS-1$
			} else if (service != RouteService.OSMAND && !settings.isInternetConnectionAvailable()) {
				showMessage(context.getString(R.string.error_calculating_route) + ":\n" + context.getString(R.string.internet_connection_required_for_online_route), Toast.LENGTH_LONG); //$NON-NLS-1$
			} else {
				if (res.getErrorMessage() != null) {
					showMessage(context.getString(R.string.error_calculating_route) + ":\n" + res.getErrorMessage(), Toast.LENGTH_LONG); //$NON-NLS-1$
				} else {
					showMessage(context.getString(R.string.empty_route_calculated), Toast.LENGTH_LONG);
				}
			}
			lastTimeEvaluatedRoute = System.currentTimeMillis();
		}

	}

	private void recalculateRouteInBackground(final Location start, final LatLon end, final GPXRouteParams gpxRoute, final RouteCalculationResult previousRoute) {
		if (start == null || end == null) {
			return;
		}
		if (currentRunningJob == null) {
			// do not evaluate very often
			if (System.currentTimeMillis() - lastTimeEvaluatedRoute > evalWaitInterval) {
				synchronized (this) {
					currentRunningJob = new RouteRecalculationThread("Calculating route", start, end, gpxRoute, previousRoute); //$NON-NLS-1$
					currentRunningJob.start();
				}
			}
		}
	}

	public boolean isRouteBeingCalculated() {
		return currentRunningJob != null;
	}

	private void showMessage(final String msg, final int length) {
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				AccessibleToast.makeText(context, msg, length).show();
			}
		});
	}

	private void showMessage(final String msg) {
		showMessage(msg, Toast.LENGTH_SHORT);
	}

	// NEVER returns null
	public RouteCalculationResult getRoute() {
		return route;
	}

	protected Context getContext() {
		return context;
	}

	public GPXFile generateGPXFileWithRoute() {
		return provider.createOsmandRouterGPX(route);
	}

}
