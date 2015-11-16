package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.osmand.plus.OsmandSettings;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.views.MapInfoControls;
import net.osmand.plus.views.MapInfoControls.MapInfoControlRegInfo;
import net.osmand.plus.views.OsmandMapTileView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.RouteListAdapter;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.RouteDao;
import com.operasoft.snowboard.database.RouteSelected;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.TabletConfigs;
import com.operasoft.snowboard.database.TabletConfigsDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.dbsync.push.RouteSelectedPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.engine.RouteBuilder;
import com.operasoft.snowboard.maplayers.PointOfInterestMenu;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.view.ResumeRouteView;
import com.operasoft.snowboard.view.ResumeRouteView.ResumeRouteListener;
import com.operasoft.snowboard.view.RouteResumedView;
import com.operasoft.snowboard.view.RouteResumedView.RouteResumedListener;

/**
 * @author show Route menu : icon route display route menu : to choose none or day in past for
 *         serviced location
 */
public class RouteCustomDialogHandler implements OnClickListener {
	private Context mContext;
	private View dialogView;
	private Dialog dialog;
	private MapActivity mapActivity;
	private PointOfInterestManager mPoiMgr;
	private List<Route> routes;
	private RouteSelected routeSelected = new RouteSelected();

	public RouteCustomDialogHandler(Context context, View view, MapActivity mapActivity) {
		this.mContext = context;
		this.dialogView = view;
		this.mapActivity = mapActivity;
	}

	private void refreshMap() {
		mapActivity.getRoutingHelper().clearCurrentRoute(null);
		OsmandSettings settings = mapActivity.getMyApplication().getSettings();
		if (Session.clocation != null) {
			settings.setMapLocationToShow(Session.clocation.getLatitude(), Session.clocation.getLongitude(), 14, null);
		}
		Config.compassState = 3;
		// TODO 000 should cancel the zoom change github Fixes #20 Utils.setSLZoom();
		Intent intent = new Intent(mContext, MapActivity.class);
		mapActivity.startActivity(intent);
	}

	private void setConstructionMode() {
		mPoiMgr.setModeConstruction();
	}

	// TODO 00 confirm allocationMode == maintenanceMode == service location Mode
	private void setAllocationMode() {
		mPoiMgr.setModeMaintenance();

	}

	private void setMarkerMode() {
		mPoiMgr.setModeMarker();

	}

	LinearLayout mIconMarkerInst, mIconConstructionLocations, mIconAllLocations;
	private OnClickListener mHeaderClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.dismiss();
			mapActivity.updateTracer(false);
			if (v == mIconAllLocations) {
				setAllocationMode();
			} else if (v == mIconMarkerInst) {
				setMarkerMode();
			} else if (v == mIconConstructionLocations) {
				setConstructionMode();
			}
			refreshMap();
		}
	};

	private void showMainMenu() {
		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialogHeader = inflater.inflate(R.layout.dialoag_routelistview_header, (ViewGroup) dialogView.findViewById(R.id.root));
		// Calling PoiManager class
		mPoiMgr = PointOfInterestManager.getInstance();

		// Dialog close button and Text on click listener.
		ImageView imgCancel = (ImageView) dialogHeader.findViewById(R.id.iMg_cancel_dialog);
		TextView txtClose = (TextView) dialogHeader.findViewById(R.id.textView2);
		TextView txtNone = (TextView) dialogHeader.findViewById(R.id.textView3);
		ImageView imgNone = (ImageView) dialogHeader.findViewById(R.id.iMg_none);
		mIconMarkerInst = (LinearLayout) dialogHeader.findViewById(R.id.drh_btn_marker_installation);
		mIconConstructionLocations = (LinearLayout) dialogHeader.findViewById(R.id.drh_btn_construction_locations);
		mIconAllLocations = (LinearLayout) dialogHeader.findViewById(R.id.allLocations);

		mIconAllLocations.setOnClickListener(mHeaderClickListener);
		mIconMarkerInst.setOnClickListener(mHeaderClickListener);
		mIconConstructionLocations.setOnClickListener(mHeaderClickListener);

		imgCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		txtNone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNone();
			}

		});
		imgNone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNone();
			}
		});
		txtClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		builder.setCustomTitle(dialogHeader);
		final ListView routeListView = new ListView(mContext);
		builder.setView(routeListView);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		final RouteDao routesDao = new RouteDao();
		routes = routesDao.listSortedRoutes();
		RouteListAdapter sortedAdapter = new RouteListAdapter(mContext, R.layout.route_item_row, routes, dialogView);
		routeListView.setAdapter(sortedAdapter);

		routeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog.dismiss();
				// String routeId = routes.get(position).getId();
				// onSelectRoute(position, routeId);
				mapActivity.updateTracer(true);
				Session.inlookAheadMode = false;
				new MyAsyncTask(position).execute(position);
			}

		});
	}

	/**
	 * the method is used to show a route if route has location serviced in the last 7 days, then a
	 * dialog prompts the user to choose : - show serviced location - none
	 * 
	 * @param position
	 * @param routeId
	 */
	private void onSelectRoute(int position, String routeId) {
		// if route has service location serviced then prompt user
		Session.route = routes.get(position);
		boolean hasCompletedSl = false;
		final List<ServiceLocation> routeServiceLocations = (new ServiceLocationDao()).findByRoute(Session.route.getId(), 7);
		for (ServiceLocation sl : routeServiceLocations)
			if (sl.isCompleted() || sl.isVisited())
				hasCompletedSl = true;
		if (hasCompletedSl) {

			if (dgResumeRte != null)
				return;

			final ResumeRouteListener resumeRouteListener = new ResumeRouteListener() {
				@Override
				public void onStartAsNew() {
					if (dgResumeRte != null) {
						dgResumeRte.dismiss();
						dgResumeRte = null;
					}
					mPoiMgr.selectRoute(Session.route, -1);
				}

				@Override
				public void onResumeRoute() {
					if (dgResumeRte != null) {
						dgResumeRte.dismiss();
						dgResumeRte = null;
					}

					showResumedRteDg();
				}
			};
			showDgResumeRte(resumeRouteListener);
		} else
			mPoiMgr.selectRoute(Session.route, -1);
	}

	private Dialog dgResumeRte = null;
	private TabletConfigs configs;

	private void showDgResumeRte(ResumeRouteListener resumeRouteListener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		ResumeRouteView dialogView = new ResumeRouteView(mContext);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Resume route ?");
		dgResumeRte = dialogBuilder.create();
		dgResumeRte.setCancelable(false);
		dgResumeRte.show();
		dialogView.listener = resumeRouteListener;
	}

	private void showResumedRteDg() {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		RouteResumedView dialogView = new RouteResumedView(mContext);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setTitle("Indicate location as serviced since last");
		final Dialog dgRteResumed = dialogBuilder.create();
		dgRteResumed.setCancelable(false);
		dgRteResumed.show();

		final RouteResumedListener routeResumedListener = new RouteResumedListener() {
			@Override
			public void onSelectServicedNone() {
				mPoiMgr.selectRoute(Session.route, 0);
				if (dgRteResumed != null) {
					dgRteResumed.dismiss();
				}
			}

			@Override
			public void onSelectServiceCompleted(int dayInPast) {
				mPoiMgr.selectRoute(Session.route, dayInPast);
				if (dgRteResumed != null) {
					dgRteResumed.dismiss();
				}
			}
		};
		dialogView.listener = routeResumedListener;
	}

	/**
	 * Display the route on the map
	 */
	private void showRoute() {

		// Initializing new list and Clearing Previous route
		// data of crossed locations.
		Config.MARKER_INSTALLATION = false;
		OsmandSettings.setRouteTime(mContext, System.nanoTime());
		Session.serLocCrossed = new HashSet<String>();
		Thread routeSelector = new Thread(new Runnable() {
			@Override
			public void run() {
				routeSelected.setRouteId(Session.route.getId());
				routeSelected.setUserId(Session.getDriver().getId());
				routeSelected.setCompanyId(Session.getCompanyId());
				routeSelected.setDateTime(CommonUtils.UtcDateNow());
				routeSelected.setVehicleId(Session.getVehicle().getId());
				RouteSelectedPushSync routePushSync = new RouteSelectedPushSync();
				routePushSync.pushData(mapActivity, routeSelected);
			}
		});
		routeSelector.start();
		final List<TIT_RoutePoint> points = Utils.getGeoPolygon(Session.route.getLinePath());
		OsmandSettings settings = mapActivity.getMyApplication().getSettings();
		settings = mapActivity.getMyApplication().getSettings();
		try {
			if (points.size() > 0) {
				// Setting route start point in center of map
				settings.setMapLocationToShow(points.get(0).getLatitude(), points.get(0).getLongitude(), 15, null);
			} else {
				if (Session.clocation != null)
					settings.setMapLocationToShow(Session.clocation.getLatitude(), Session.clocation.getLongitude(), 15, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		createOsmAndRoute(Session.route, points);

		// dialog.dismiss();
		Config.compassState = 3;
		Intent intent = new Intent(mContext, MapActivity.class);
		mapActivity.startActivity(intent);
		Session.setRouteSlPosition(0);
		try {
			final PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
			if (poiMgr.routeSlList != null && !poiMgr.routeSlList.isEmpty()) {
				PointOfInterest poi = new PointOfInterest(poiMgr.routeSlList.get(0).getId());
				poi.attachServiceLocation(poiMgr.routeSlList.get(0));
				// Fix2.11 #61
				if (Session.route != null && Session.route.getPopUp() != null && Session.route.getPopUp().equals(Route.SHOW_POPUP)) {
					PointOfInterestActionListener actionListener = new PointOfInterestActionHandler(mContext);
					OsmandMapTileView tileView = new OsmandMapTileView(mContext);
					PointOfInterestMenu menu = new PointOfInterestMenu(actionListener, tileView);
					menu.createDialog(poi);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * start a thread to draw route on the map
	 * 
	 * @param route
	 * @param points
	 */
	private void createOsmAndRoute(Route route, List<TIT_RoutePoint> points) {
		final OsmandSettings settings = mapActivity.getMyApplication().getSettings();
		settings.clearPointToNavigate();

		TabletConfigsDao configsDao = new TabletConfigsDao();
		configs = configsDao.getByCompanyId(Session.getCompanyId());

		if (configs.getTurnByTurn().equals(TabletConfigs.TBT_NAV_WITH_VOICE)) {
			Company company = Session.getCompany();
			if ( (company != null) && company.getLanguage().equals("F"))
				settings.VOICE_PROVIDER.set("fr-tts");
			else
				settings.VOICE_PROVIDER.set("en-tts");
		}

		final MapInfoControls mapController = mapActivity.getMapLayers().getMapInfoLayer().getMapInfoControls();
		final Iterator<MapInfoControlRegInfo> iterator = mapController.getLeft().iterator();
		final ArrayList<MapInfoControlRegInfo> mapRegInfos = new ArrayList<MapInfoControls.MapInfoControlRegInfo>();
		mapRegInfos.addAll(mapController.getLeft());
		mapActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				while (iterator.hasNext())
					mapController.changeNavigation(iterator.next(), false);

				if (!configs.getTurnByTurn().equals(TabletConfigs.TBT_NAV_DISABLED)) {
					mapController.changeNavigation(mapRegInfos.get(0), true);

					if (configs.getTurnCount() == 2)
						mapController.changeNavigation(mapRegInfos.get(2), true);
				}
			}
		});

		RouteBuilder builder = new RouteBuilder(route, mapActivity);
		builder.run();
	}

	@Override
	public void onClick(View v) {
		showMainMenu();
	}

	public void showNone() {
		mapActivity.updateTracer(false);
		Config.MARKER_INSTALLATION = false;
		mPoiMgr = PointOfInterestManager.getInstance();
		mPoiMgr.setModeNone();
		// mPoiMgr.routeUnSelected();
		mapActivity.getRoutingHelper().clearCurrentRoute(null);

		Intent intent = new Intent(mContext, MapActivity.class);
		if (dialog != null)
			dialog.dismiss();
		OsmandSettings.setNoneOnMap(mContext, "none_name");
		(mContext).startActivity(intent);

	}

	private class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		private ProgressDialog pDialog;
		int pos;

		public MyAsyncTask(int position) {
			this.pos = position;
		}

		@Override
		protected void onPreExecute() {
			pDialog = ProgressDialog.show(mContext, "", "Please wait calculating route.");
			String routeId = routes.get(pos).getId();
			onSelectRoute(pos, routeId);
		}

		@Override
		protected Integer doInBackground(final Integer... params) {
			showRoute();
			return params[0];

		}

		@Override
		protected void onPostExecute(Integer result) {

			if (pDialog != null)
				if (pDialog.isShowing())
					pDialog.dismiss();
		}
	}
}
