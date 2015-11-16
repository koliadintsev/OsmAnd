package com.operasoft.snowboard.engine;

import java.util.List;

import net.osmand.GPXUtilities;
import net.osmand.GPXUtilities.GPXFile;
import net.osmand.GPXUtilities.Track;
import net.osmand.GPXUtilities.TrkSegment;
import net.osmand.osm.LatLon;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.activities.ApplicationMode;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.routing.RouteCalculationResult;
import net.osmand.plus.routing.RouteProvider;
import net.osmand.plus.routing.RouteProvider.GPXRouteParams;
import net.osmand.plus.routing.RouteProvider.RouteService;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.router.Interruptable;
import android.location.Location;
import android.widget.Toast;

import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.TabletConfigs;
import com.operasoft.snowboard.database.TabletConfigsDao;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.util.Session;

public class RouteBuilder implements Runnable, Interruptable {

	private Route route;
	private RoutingHelper helper;
	private MapActivity activity;
	private OsmandSettings settings;

	public RouteBuilder(Route route, final MapActivity activity) {
		this.route = route;
		this.activity = activity;
		this.helper = activity.getRoutingHelper();
		settings = activity.getMyApplication().getSettings();
		boolean changed = settings.APPLICATION_MODE.set(ApplicationMode.CAR);
		if (changed) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					activity.updateApplicationModeSettings();
				}
			});
		}
		settings.FAST_ROUTE_MODE.set(true);
		helper.setAppMode(ApplicationMode.CAR);

	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void run() {
		settings.clearPointToNavigate();

		GPXFile gpx = createFile();

		if (gpx == null) {
			if (Session.clocation == null)
				showToast("Unknown current position");
			return;
		}

		GPXRouteParams gpxRoute = new GPXRouteParams(gpx, false, settings);

		// Reference to
		// https://code.google.com/p/osmand/wiki/FAQ#What_do_the_'Use_current_destination'_and_'Pass_a
		// working as 'Pass along entire track' mode of Osmand
		Location loc = activity.getLastKnownLocation();
		if (loc != null) {
			System.out.println("RouteBuilder.run()" + loc.getLatitude());
			gpxRoute.setStartPoint(loc);
		}

		LatLon endPoint = gpxRoute.getLastPoint();
		Location startPoint = gpxRoute.getStartPointForRoute();
		helper.setFollowingMode(true);
		helper.setFinalAndCurrentLocation(endPoint, startPoint, gpxRoute);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TabletConfigsDao configsDao = new TabletConfigsDao();
				TabletConfigs configs = configsDao.getByCompanyId(Session.getCompanyId());

				if (configs.getTurnByTurn().equals(TabletConfigs.TBT_NAV_WITH_VOICE))
					activity.getMyApplication().showDialogInitializingCommandPlayer(activity);
			}
		});
	}

	/**
	 * Create new GPXFile including seleted route points.
	 * 
	 * @return
	 */
	private GPXFile createFile() {
		if (Session.clocation == null)
			return null;

		// Extract the list of points that are part of that route.
		List<TIT_RoutePoint> points = Utils.getGeoPolygon(route.getLinePath());
		if ((points == null) || (points.isEmpty())) {
			return null;
		}
		LatLon routeStart = new LatLon(points.get(0).getLatitude(), points.get(0).getLongitude());
		// RouteCalculationResult res = new RouteCalculationResult(null, null, Session.clocation,
		// end, null, activity, true, true);
		//
		RouteProvider provider = new RouteProvider();
		// GPXFile gpx = provider.createOsmandRouterGPX(res);
		// Build a route to get to our starting point
		// LatLon routeStart = new LatLon(points.get(0).getLatitude(),
		// points.get(0).getLongitude());

		// TODO check if logic is still ok
		GPXFile gpx = null;
		Location location = new Location("start");
		location.setLatitude(points.get(0).getLatitude());
		location.setLongitude(points.get(0).getLongitude());

		if (location.distanceTo(Session.clocation) > (200 * 1000)) {
			showToast("Start point is more then 100 km far");
			gpx = new GPXFile();
			Track track = new Track();
			gpx.tracks.add(track);
			TrkSegment trkSegment = new TrkSegment();
			track.segments.add(trkSegment);
		} else {
			RouteCalculationResult firstPath = provider.calculateRouteImpl(Session.clocation, routeStart, ApplicationMode.CAR, RouteService.OSMAND, activity, null, null, true, true, this);
			gpx = provider.createOsmandRouterGPX(firstPath);
		}

		// Build the entire route
		// gpx = new GPXFile();
		GPXUtilities.WptPt pt = new GPXUtilities.WptPt();
		// pt.lat = Session.clocation.getLatitude();
		// pt.lon = Session.clocation.getLongitude();
		// gpx.points.add(pt);
		//
		// pt = new GPXUtilities.WptPt();
		// pt.lat = points.get(points.size() - 1).getLatitude();
		// pt.lon = points.get(points.size() - 1).getLongitude();
		// gpx.points.add(pt);

		// Append our route to it

		GPXUtilities.Route rte;
		if (gpx.routes.isEmpty()) {
			rte = new GPXUtilities.Route();
			gpx.routes.add(rte);
		} else {
			rte = gpx.routes.get(0);
		}
		rte.name = route.getName();

		for (TIT_RoutePoint point : points) {
			pt = new GPXUtilities.WptPt();
			pt.lat = point.getLatitude();
			pt.lon = point.getLongitude();
			gpx.tracks.get(0).segments.get(0).points.add(pt);
		}

		return gpx;
	}

	private void showToast(final String msg) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
