package net.osmand.plus.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.osmand.GPXUtilities;
import net.osmand.GPXUtilities.GPXFile;
import net.osmand.IProgress;
import net.osmand.LogUtil;
import net.osmand.Version;
import net.osmand.access.AccessibilityPlugin;
import net.osmand.access.AccessibleActivity;
import net.osmand.access.AccessibleAlertBuilder;
import net.osmand.access.AccessibleToast;
import net.osmand.access.NavigationInfo;
import net.osmand.data.MapTileDownloader.DownloadRequest;
import net.osmand.data.MapTileDownloader.IMapDownloaderCallback;
import net.osmand.map.IMapLocationListener;
import net.osmand.osm.LatLon;
import net.osmand.osm.MapUtils;
import net.osmand.plus.BusyIndicator;
import net.osmand.plus.FavouritesDbHelper;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.ResourceManager;
import net.osmand.plus.activities.search.SearchActivity;
import net.osmand.plus.routing.RouteProvider.GPXRouteParams;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.views.AnimateDraggingMapThread;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.PointLocationLayer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.operasoft.android.gps.XirgoForwarder;
import com.operasoft.android.gps.services.GPSService;
import com.operasoft.android.util.Utils;
import com.operasoft.geom.Point;
import com.operasoft.geom.Polygon;
import com.operasoft.geom.PolygonDetector;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.Sw_LoginScreenActivity;
import com.operasoft.snowboard.database.DropEmployeesDao;
import com.operasoft.snowboard.database.EndRoute;
import com.operasoft.snowboard.database.ImeiCompanyDao;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.RouteSelectedDao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.EndRoutePushSync;
import com.operasoft.snowboard.engine.GeofenceManager;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.engine.PointOfInterestManager.EnterExitPoiListener;
import com.operasoft.snowboard.events.PunchOutEvent;
import com.operasoft.snowboard.map.actions.GeofenceComponentUpdateAction;
import com.operasoft.snowboard.map.actions.PointOfInterestMapUpdateAction;
import com.operasoft.snowboard.maplayers.PointOfInterestMenu;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.DamageCustomDialogHandler;
import com.operasoft.snowboard.util.DeficiencyCustomDialogHandler;
import com.operasoft.snowboard.util.HelpHandler;
import com.operasoft.snowboard.util.PIPMediaPlayer;
import com.operasoft.snowboard.util.PoiAlarmManager;
import com.operasoft.snowboard.util.PunchCustomDialogHandler;
import com.operasoft.snowboard.util.RefuelCustomDialogHandler;
import com.operasoft.snowboard.util.RouteCustomDialogHandler;
import com.operasoft.snowboard.util.Session;
import com.operasoft.snowboard.util.Session.SessionType;
import com.operasoft.snowboard.util.TaskListCustomDialog;
import com.operasoft.snowboard.util.TransportDialogHandler;
import com.operasoft.snowboard.util.VehicleInspectionDialogHandler;
import com.operasoft.snowboard.util.WorkOrderCustomDialogHandler;

public class MapActivity extends AccessibleActivity implements IMapLocationListener, SensorEventListener {

	// stupid error but anyway hero 2.1 : always lost gps signal (temporarily
	// unavailable) for timeout = 2000
	private static final int GPS_TIMEOUT_REQUEST = 0;
	private static final int GPS_DIST_REQUEST = 0;
	// use only gps (not network) for 12 seconds
	private static final int USE_ONLY_GPS_INTERVAL = 12000;

	private static final int SHOW_POSITION_MSG_ID = 7;
	private static final int SHOW_POSITION_DELAY = 2500;
	public static final float ACCURACY_FOR_GPX_AND_ROUTING = 50;

	private static final int AUTO_FOLLOW_MSG_ID = 8;
	private static final int LOST_LOCATION_MSG_ID = 10;
	private static final long LOST_LOCATION_CHECK_DELAY = 18000;

	private static final int LONG_KEYPRESS_MSG_ID = 28;
	private static final int LONG_KEYPRESS_DELAY = 500;

	// 5kph = 1.4m/s
	private static final float FIVE_KM_PER_HOUR = 1.4f;
	private static final int COMPASS_CENTER = 0;
	private static final int COMPASS_LOCK = 1;

	private static final String TAG = "MapActivity";

	private long lastTimeAutoZooming = 0;
	private long lastTimeSensorRotation = 0;
	private long lastTimeGPSLocationFixed = 0;

	/** Called when the activity is first created. */
	public static OsmandMapTileView mapView;
	private MapActivityActions mapActions;
	private MapActivityLayers mapLayers;
	private NavigationInfo navigationInfo;

	private LiveMonitoringHelper liveMonitoringHelper;
	private RoutingHelper routingHelper;

	private SavingTrackHelper savingTrackHelper;
	// Notification status
	private NotificationManager mNotificationManager;
	private int APP_NOTIFICATION_ID;
	private boolean sensorRegistered = false;
	private float previousSensorValue = 0;

	// handler to show/hide trackball position and to link map with delay
	private final Handler uiHandler = new Handler();
	// Current screen orientation
	private int currentScreenOrientation;
	//
	private Dialog progressDlg = null;
	// App settings
	private OsmandSettings settings;

	//TODO 001 confirm useless private RouteAnimation routeAnimation = new RouteAnimation();

	private boolean isMapLinkedToLocation = false;
	private ProgressDialog startProgressDialog;
	private final List<DialogProvider> dialogProviders = new ArrayList<DialogProvider>(2);

	private PolygonDetector mPolygonDetector;

	private PointOfInterestManager PoiManager;

	private GeofenceComponentUpdateAction geofenceHandler;
	/**
	 * This is the Handler used to post POI Header update requests
	 */
	private PointOfInterestMapUpdateAction mMapMenuHeader;
	private String userPin;
	private Location location;
	List<Vehicle> trailerList = new ArrayList<Vehicle>();

	private Button mBtnEndRoute, mBtnCompleteRoute;
	private ImageButton mBtnLogout,mBtnDeficiency;
	private String timeSpent = "";

	private TextView mTxtRouteName, mTxtRouteTimer, mTxtDriverComments, mTxtLogs;
	// private TextView routeCrossed;
	private ImageView mImgCompassState;
	private ToggleButton mBreadcrumb;
	private int secondsSpent = 0;
	
	DamageCustomDialogHandler damangeCustomDialogHandler;
	DeficiencyCustomDialogHandler deficiencyCustomDialogHandler;
	WorkOrderCustomDialogHandler woCustomDialogHandler;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	

	private Notification getNotification() {
		Intent notificationIndent = new Intent(this, OsmandIntents.getMapActivity());
		notificationIndent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Notification notification = new Notification(R.drawable.icon, "", //$NON-NLS-1$ 
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(this, Version.getAppName(this), getString(R.string.go_back_to_osmand),
				PendingIntent.getActivity(this, 0, notificationIndent, PendingIntent.FLAG_UPDATE_CURRENT));
		return notification;
	}

	/**
	 * Snowboard code.
	 */
	static Location mLocation;

	/**
	 * Snowboard code.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = getMyApplication().getSettings();
		mapActions = new MapActivityActions(this);
		mapLayers = new MapActivityLayers(this);
		navigationInfo = new NavigationInfo(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Full screen is not used here
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		if (Session.getType() == SessionType.SITE_SESSION) {
			((ImageButton) findViewById(R.id.button_route)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.button_refuel)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.vehicle_inspection)).setVisibility(View.GONE);
		}
		
		ImageView imLogo = (ImageView) findViewById(R.id.imgView_wc_login_text);
		if (Session.isSimplicity())
			imLogo.setImageResource(R.drawable.simplicity);
		
		PoiManager = PointOfInterestManager.getInstance();
		PoiManager.enterExitPoiListener = mapInOutPoiListener;

		startProgressDialog = new ProgressDialog(this);
		startProgressDialog.setCancelable(true);
		((OsmandApplication) getApplication()).checkApplicationIsBeingInitialized(this, startProgressDialog);
		parseLaunchIntentLocation();

		settings.setLastKnownMapZoom(Session.userZoom != -1 ? Session.userZoom : 18);
		mapView = (OsmandMapTileView) findViewById(R.id.MapView);
		mapView.setTrackBallDelegate(new OsmandMapTileView.OnTrackBallListener() {
			@Override
			public boolean onTrackBallEvent(MotionEvent e) {
				showAndHideMapPosition();
				return MapActivity.this.onTrackballEvent(e);
			}
		});
		mImgCompassState = (ImageView) findViewById(R.id.Iv_MA_Compass_State);

		// Do some action on close
		startProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				getMyApplication().getResourceManager().getRenderer().clearCache();
				mapView.refreshMap(true);
			}
		});

		getMyApplication().getResourceManager().getMapTileDownloader().addDownloaderCallback(new IMapDownloaderCallback() {
			@Override
			public void tileDownloaded(DownloadRequest request) {
				if (request != null && !request.error && request.fileToSave != null) {
					ResourceManager mgr = getMyApplication().getResourceManager();
					mgr.tileDownloaded(request);
				}
				if (request == null || !request.error) {
					mapView.tileDownloaded(request);
				}
			}
		});

		savingTrackHelper = getMyApplication().getSavingTrackHelper();
		liveMonitoringHelper = getMyApplication().getLiveMonitoringHelper();
		LatLon pointToNavigate = settings.getPointToNavigate();

		routingHelper = getMyApplication().getRoutingHelper();
		// This situtation could be when navigation suddenly crashed and after
		// restarting
		// it tries to continue the last route
		if (settings.FOLLOW_THE_ROUTE.get() && !routingHelper.isRouteCalculated()) {
			restoreRoutingMode(pointToNavigate);
		}

		mapView.setMapLocationListener(this);
		mapLayers.createLayers(mapView);

		if (!settings.isLastKnownMapLocation()) {
			// show first time when application ran
			LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
			location = null;
			for (String provider : service.getAllProviders()) {
				try {
					Location loc = service.getLastKnownLocation(provider);
					if (location == null) {
						location = loc;
					} else if (loc != null && location.getTime() < loc.getTime()) {
						location = loc;
					}
				} catch (IllegalArgumentException e) {
					Log.d(LogUtil.TAG, "Location provider not available"); //$NON-NLS-1$ 
				}
			}
			if (location != null) {
				mapView.setLatLon(location.getLatitude(), location.getLongitude());
			}
		}
		/**
		 * Snowboard code.
		 */
		// Reloading the offline map file from SD card.
		Session.MapAct = MapActivity.this;
		try {
			getMyApplication().getResourceManager().reloadIndexes(IProgress.EMPTY_PROGRESS);
		} catch (Exception e) {
			Log.e("MapActivity", "Exception caught - skipping reloadIndexes", e);
		}

		// ImageButton mChat = (ImageButton) findViewById(R.id.button_chat);

		ImageButton mRoute = (ImageButton) findViewById(R.id.button_route);
		RouteCustomDialogHandler routeHandler = new RouteCustomDialogHandler(this, getWindow().getDecorView().getRootView(), MapActivity.this);
		mRoute.setOnClickListener(routeHandler);

		// ImageButton mService = (ImageButton)
		// findViewById(R.id.button_service_activity);

		ImageButton button_punch_clock = (ImageButton) findViewById(R.id.button_punch_clock);
		PunchCustomDialogHandler punchDialogHandler = new PunchCustomDialogHandler(this, getWindow().getDecorView().getRootView());
		button_punch_clock.setOnClickListener(punchDialogHandler.punchListener);

		ImageButton mRefuel = (ImageButton) findViewById(R.id.button_refuel);
		RefuelCustomDialogHandler handler = new RefuelCustomDialogHandler(this, getWindow().getDecorView().getRootView());
		mRefuel.setOnClickListener(handler.makeListener);

		ImageButton mVehicleInspection = (ImageButton) findViewById(R.id.vehicle_inspection);
		VehicleInspectionDialogHandler vehiCleInspectionHandler = new VehicleInspectionDialogHandler(this, getWindow().getDecorView().getRootView());
		mVehicleInspection.setOnClickListener(vehiCleInspectionHandler.makeInspectListener);

		ImageButton mtransport = (ImageButton) findViewById(R.id.button_transport);
		TransportDialogHandler transportHandler = new TransportDialogHandler(this, getWindow().getDecorView().getRootView());
		mtransport.setOnClickListener(transportHandler.makeTransportListener);

		ImageButton mIncident = (ImageButton) findViewById(R.id.button_incident);
		if(Session.isSimplicity()){
			woCustomDialogHandler = new WorkOrderCustomDialogHandler(this, null);
			mIncident.setOnClickListener(woCustomDialogHandler.woListener);
		}else{
			damangeCustomDialogHandler = new DamageCustomDialogHandler(this, null);
			mIncident.setOnClickListener(damangeCustomDialogHandler.damageListener);
		}
		
		
		
		

		ImageButton mDeficiency = (ImageButton) findViewById(R.id.btn_add_deficiency);
		deficiencyCustomDialogHandler = new DeficiencyCustomDialogHandler(this,getWindow().getDecorView().getRootView());
		mDeficiency.setOnClickListener(deficiencyCustomDialogHandler.deficiencyListener);
		
		// ImageButton mCreate = (ImageButton) findViewById(R.id.button_create);

		mBtnLogout = (ImageButton) findViewById(R.id.button_logout);

		//		LogoutHandler logoutHandler = new LogoutHandler(this, MapActivity.this);
		//	    mBtnLogout.setOnClickListener(logoutHandler.logout);

		/** TODO Enabled this when we integrate Schumacher in production */
		mBtnLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processLogout();
			}
		});

		ImageButton mHelp = (ImageButton) findViewById(R.id.button_help);
		HelpHandler helpHandler = new HelpHandler(this, getWindow().getDecorView().getRootView(), MapActivity.this);
		mHelp.setOnClickListener(helpHandler.helpClick);

		mBreadcrumb = (ToggleButton) findViewById(R.id.btn_breadcrumb);

		mBreadcrumb.setChecked(PoiManager.isInBreadCrumbMode());
		mBreadcrumb.setText(" ");

		mBreadcrumb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PoiManager.setInBreadCrumbMode(!PoiManager.isInBreadCrumbMode());
				updateTracer(PoiManager.isInBreadCrumbMode());
			}
		});

		// set the user last name.
		TextView lastName = (TextView) findViewById(R.id.driverlast);
		SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(this);
		userPin = mSP.getString(Config.USER_PIN_KEY, "");
		lastName.setText("Driver Name:" + CommonUtils.selectUserLastName(userPin));
		// set the vehicle id.
		TextView vehicleId = (TextView) findViewById(R.id.vehicleid);
		String vehicle = mSP.getString(Config.VEHICLE_ID_KEY, "");
		vehicleId.setText("Vehicle No:" + CommonUtils.selectVehicleNumber(vehicle).toString());

		mTxtRouteName = (TextView) findViewById(R.id.routename);
		mTxtRouteTimer = (TextView) findViewById(R.id.routetimer);
		mBtnEndRoute = (Button) findViewById(R.id.btn_end_route);
		mBtnCompleteRoute = (Button) findViewById(R.id.btn_complete_route);
		mBtnDeficiency = (ImageButton) findViewById(R.id.btn_add_deficiency);
		mTxtDriverComments = (TextView) findViewById(R.id.drivercomment);
		// routeCrossed = (TextView) findViewById(R.id.routeCrossed);

		/*
		 * End of the snowboard code.
		 */
		addDialogProvider(mapActions);
		OsmandPlugin.onMapActivityCreate(this);

		mPolygonDetector = new PolygonDetector();
		//PoiManager.connect(mPolygonDetector, statusListener);
		geofenceHandler = new GeofenceComponentUpdateAction(this);
		mMapMenuHeader = new PointOfInterestMapUpdateAction(this);
		PoiManager.addPoiEventListener(mMapMenuHeader);
		// Setting compass view
		setToNavigationMode();
	}
	

	/**
	 * Enable/Disable Tracer
	 * 
	 * @param enableTracer
	 */
	public void updateTracer(boolean enableTracer) {

		mBreadcrumb.setChecked(enableTracer);
		mBreadcrumb.setText("");

		if (enableTracer) {
			if (PoiManager.isInBreadCrumbMode()) {
				PoiManager.clearBreadCrumb();
			}

			PoiManager.setInBreadCrumbMode(true);

			if (Session.clocation != null) {
				TIT_RoutePoint point = new TIT_RoutePoint();
				point.setLatitude(Session.clocation.getLatitude());
				point.setLongitude(Session.clocation.getLongitude());
				PoiManager.addpointToBreadCrumb(point);
			}

		} else {
			PoiManager.setInBreadCrumbMode(false);
			PoiManager.clearBreadCrumb();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		PoiManager.connect(mPolygonDetector, statusListener);
		if (PoiManager.isModeNone()) {
			//force to reload the POI list
			PoiManager.setModeNone();
		}
	}

	public void doLogout() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				processLogout();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode != RESULT_CANCELED){
			/**
			 * We check if the Result is from the DamageCustomDialogHandler//WorkOrderCustomDialogHandler(Taking a picture). 
			 * If so, we send the information to the class so it can add the picture to the ImageView
			 */
			if (requestCode == ACTION_TAKE_PHOTO_B && resultCode == Activity.RESULT_OK) {
				if(Session.isSimplicity()){
					woCustomDialogHandler.addImage(getApplicationContext(),woCustomDialogHandler.getFullPathFilename());
				}
			}else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
				if(Session.isSimplicity()){
					deficiencyCustomDialogHandler.addImage(getApplicationContext(),deficiencyCustomDialogHandler.getFullPathFilename());
				}
				
			}else if (resultCode == Activity.RESULT_OK) {
				// Getting real path of image from uri.
				Uri selectedImage = data.getData();
				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(selectedImage, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				// Storing real path in object
				/** TODO Enabled this when we integrate Schumacher in production
			if (requestCode == 1)
				ForemanDailySheetDialogHandler.setImageUri(cursor.getString(column_index));
			else
				ForemanDailySheetDialogHandler.setImageUriMaintenence(cursor.getString(column_index));
				 */
			}
			
		}		
	}

	private void displayVerionName() {
		try {

			final String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			final TextView txtView = (TextView) this.findViewById(R.id.txtversion);
			txtView.setText(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayVerionName();
		updateConfigInfo();

		// If tab suspended on the server end by admin, forcefully logout the user
		ImeiCompanyDao imeiCompanyDao = new ImeiCompanyDao();
		SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(this);

		if (mSP.getString("user_pin", "").equals(""))
			doLogout();
		else if (Session.getImeiCompany() == null)
			doLogout();
		else if (imeiCompanyDao.isTabSuspended(Session.getImeiCompany().getImeiNo()))
			doLogout();

		// Create the POI Alarm Manager instance
		PoiAlarmManager.getInstance(this);

		// When the map is displayed, we need to display all active Service
		// Activities assigned to this truck/driver
		PoiManager.attachAllServiceActivities();

		cancelNotification();
		if (settings.MAP_SCREEN_ORIENTATION.get() != getRequestedOrientation()) {
			// setRequestedOrientation(settings.MAP_SCREEN_ORIENTATION.get());
			// can't return from this method we are not sure if activity will be
			// recreated or not
		}
		mapLayers.getNavigationLayer().setPointToNavigate(settings.getPointToNavigate());
		Location loc = getLastKnownLocation();
		if (loc != null && (System.currentTimeMillis() - loc.getTime()) > 30 * 1000) {
			setLocation(null);
		}

		currentScreenOrientation = getWindow().getWindowManager().getDefaultDisplay().getOrientation();

		// for voice navigation
		if (settings.AUDIO_STREAM_GUIDANCE.get() != null) {
			setVolumeControlStream(settings.AUDIO_STREAM_GUIDANCE.get());
		} else {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
		}

		updateApplicationModeSettings();

		mapLayers.getPoiMapLayer().setFilter(settings.getPoiFilterForMap((OsmandApplication) getApplication()));

		mapLayers.getMapInfoLayer().getBackToLocation().setEnabled(false);
		// by default turn off causing unexpected movements due to network
		// establishing
		// best to show previous location
		setMapLinkedToLocation(false);

		// !!!! CL TO DO: Find a proper way to recalculate the route on resume
		// because the following screws everything up
		// if destination point was changed try to recalculate route
		// if (routingHelper.isFollowingMode() &&
		// !Algoritms.objectEquals(settings.getPointToNavigate(),
		// routingHelper.getFinalLocation())) {
		// routingHelper.setFinalAndCurrentLocation(settings.getPointToNavigate(),
		// getLastKnownLocation(), routingHelper.getCurrentGPXRoute());
		// }

		startLocationRequests();

		if (settings != null && settings.isLastKnownMapLocation()) {
			LatLon l = settings.getLastKnownMapLocation();
			mapView.setLatLon(l.getLatitude(), l.getLongitude());
			mapView.setZoom(settings.getLastKnownMapZoom());
		}

		settings.MAP_ACTIVITY_ENABLED.set(true);
		checkExternalStorage();
		showAndHideMapPosition();

		LatLon cur = new LatLon(mapView.getLatitude(), mapView.getLongitude());
		LatLon latLonToShow = settings.getAndClearMapLocationToShow();
		String mapLabelToShow = settings.getAndClearMapLabelToShow();
		Object toShow = settings.getAndClearObjectToShow();
		if (settings.isRouteToPointNavigateAndClear()) {
			// always enable and follow and let calculate it (GPS is not
			// accessible in garage)
			mapActions.getDirections(getLastKnownLocation(), true);
		}
		if (mapLabelToShow != null && latLonToShow != null) {
			mapLayers.getContextMenuLayer().setSelectedObject(toShow);
			mapLayers.getContextMenuLayer().setLocation(latLonToShow, mapLabelToShow);
		}
		if (latLonToShow != null && !latLonToShow.equals(cur)) {
			mapView.getAnimatedDraggingThread().startMoving(latLonToShow.getLatitude(), latLonToShow.getLongitude(), settings.getMapZoomToShow(), true);

		}

		View progress = mapLayers.getMapInfoLayer().getProgressBar();
		if (progress != null) {
			getMyApplication().getResourceManager().setBusyIndicator(new BusyIndicator(this, progress));
		}

		OsmandPlugin.onMapActivityResume(this);
		getMyApplication().getDaynightHelper().onMapResume();
		mapView.refreshMap(true);

		// set the map for enroute
		if (Session.route != null && OsmandSettings.getNoneOnMap(getApplicationContext()) == "") {
			setEnrouteMode();
		}

		// Removing elements of route view.
		if (Config.MARKER_INSTALLATION) {
			Session.route = null;
			mTxtRouteName.setVisibility(View.GONE);
			mTxtRouteTimer.setVisibility(View.GONE);
			mBtnEndRoute.setVisibility(View.GONE);
			mBtnCompleteRoute.setVisibility(View.GONE);
			mBtnDeficiency.setVisibility(View.GONE);
		}
		if (Session.route == null) {
			mTxtRouteName.setVisibility(View.GONE);
			mTxtRouteTimer.setVisibility(View.GONE);
			mBtnEndRoute.setVisibility(View.GONE);
			mBtnCompleteRoute.setVisibility(View.GONE);
			mBtnDeficiency.setVisibility(View.GONE);
		}

		// Register with the POI Manager in order to know when to refresh the
		// POI header...
		mMapMenuHeader.updatePoiHeader();
		PoiManager.addPoiEventListener(mMapMenuHeader);

		// Register with the Geofence Manager in order to know when to refresh
		// the
		// geofence component...
		geofenceHandler.postComponentUpdate();
		GeofenceManager.getInstance().addGeofenceActionListener(geofenceHandler);

		//TODO 000 confirm, not clean but should cancel all previous zoom update
		if (Session.userZoom != -1)
			mapView.setZoom(Session.userZoom);

		// switchRotateMapMode();
		
		// Register mMessageReceiver to receive messages from DbSyncManager.
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("DbSyncManager"));
	}
	
	// handler for received Intents for the "my-event" event 
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(final Context context, Intent intent) {
	    // Extract data included in the Intent
	    final String message = intent.getStringExtra("message");
	    Log.d("MapActivity receiver", "Got message: " + message);
	    runOnUiThread(new Runnable() {
	    	@Override
	    	public void run() {
	    		Toast.makeText(context, "DB SYNC Alert: " + message, Toast.LENGTH_LONG).show();
	    	}
	    });
	  }
	};

	private void shumacherFeature() {
		//Moved to login activity
		try {
			/** TODO Enabled this when we integrate Schumacher in production
			Login login = new Login();
			login.setBrowserType("Snowboard");
			login.setBrowserVersion("android");
			login.setCompanyId(Session.getCompanyId());
			WifiManager wim = (WifiManager) getSystemService(WIFI_SERVICE);
			login.setIpAddress(Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress()));
			login.setLogin(new Date() + "");
			login.setUserId(userDao.getByPin(Session.getUserPin()).getId());
			LoginPushSync loginPushSync = new LoginPushSync();
			loginPushSync.pushData(getApplicationContext(), login);
			 */

			// Moved to login activity
			// PunchInEvent punchIn = new PunchInEvent(this, CommonUtils.selectUserId(userPin));
			// punchIn.send();
			/** TODO Enabled this when we integrate Schumacher in production
			if (Session.FirstLogin) {
				Session.FirstLogin = false;
				setLocation(location);

				PunchDao punchDao = new PunchDao();

				String uId = CommonUtils.selectUserId(userPin);
				Punch punchDto = punchDao.userLastPunch(uId, MapActivity.this);

				if (punchDto != null) {
					if (punchDto.getOperation().equals("In"))
						showPunchOutDialog(punchDto);
					else {
						PunchInEvent punchIn = new PunchInEvent(this, CommonUtils.selectUserId(userPin));
						punchIn.send();
					}
				} else {
					PunchInEvent punchIn = new PunchInEvent(this, CommonUtils.selectUserId(userPin));
					punchIn.send();
				}

				if (Session.getVehicle().getTrailer().equals("1"))
					showTrailerSelector();
			}
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private long chronoVersion = 0;

	private void setEnrouteMode() {
		secondsSpent = 0;
		chronoVersion = System.currentTimeMillis();
		PoiManager.setModeRoute();
		mTxtRouteTimer.setText("00:00");
		mTxtRouteName.setText(PoiManager.getCurrentRoute().getName());
		mTxtRouteTimer.setVisibility(View.VISIBLE);
		mTxtRouteName.setVisibility(View.VISIBLE);
		mBtnEndRoute.setVisibility(View.VISIBLE);
		mBtnCompleteRoute.setVisibility(View.VISIBLE);
		
		if(Session.isSimplicity())
			mBtnDeficiency.setVisibility(View.VISIBLE);

		mBtnCompleteRoute.setBackgroundResource(Session.inlookAheadMode ? R.drawable.blue_bg : R.drawable.blue_light_bg);

		mBtnEndRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chronoVersion = 0;
				processEndRoute(PoiManager.getCurrentRoute());
			}
		});
		mBtnCompleteRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Session.inlookAheadMode = !Session.inlookAheadMode;
				mBtnCompleteRoute.setBackgroundResource(Session.inlookAheadMode ? R.drawable.blue_bg : R.drawable.blue_light_bg);
				mapView.refreshMap(true);
			}
		});

		final long startedTime = OsmandSettings.getRouteTime(MapActivity.this);

		Runnable chrono = new Runnable() {
			long timestamp = chronoVersion;

			@Override
			public void run() {
				final long time = System.nanoTime();
				long spentTime = time - startedTime;
				int seconds = (int) (spentTime / 1000000000.0);
				secondsSpent = seconds;
				int minutes = (seconds / 60);
				int hours = (minutes / 60);
				minutes %= 60;
				seconds %= 60;
				timeSpent = (hours > 9 ? hours : "0" + hours) + ":" + (minutes > 9 ? minutes : "0" + minutes) + ":" + (seconds > 9 ? seconds : "0" + seconds);

				mTxtRouteTimer.setText(timeSpent);
				if (chronoVersion == timestamp)
					mTxtRouteName.postDelayed(this, 1000);
			}
		};
		mTxtRouteName.postDelayed(chrono, 1000);
		/*
				Thread timer = new Thread(new Runnable() {
					@Override
					public void run() {
						while (Session.route != null && OsmandSettings.getNoneOnMap(getApplicationContext()) == "" && PoiManager.getCurrentRoute() != null) {


						}
					}
				});

		 */
	}

	private void processLogout() {
		/** TODO Enabled this when we integrate Schumacher in production
		Login login = new Login();
		login.setBrowserType("Snowboard");
		login.setBrowserVersion("android");
		login.setCompanyId(Session.getCompanyId());
		login.setLogout(new Date() + "");
		WifiManager wim = (WifiManager) getSystemService(WIFI_SERVICE);
		login.setIpAddress(Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress()));
		login.setUserId(userDao.getByPin(Session.getUserPin()).getId());
		login.setSessionEndType("Logout");
		LoginPushSync loginPushSync = new LoginPushSync();
		loginPushSync.pushData(getApplicationContext(), login);

		VehiclePushSync vPush = new VehiclePushSync();
		VehiclesDao vehicleDao = new VehiclesDao();
		Vehicle vehicle = vehicleDao.getById(Session.getVehicle().getId());
		vehicle.setTrailerId("");
		vPush.pushData(MapActivity.this, vehicle);
		 */

		SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefEditor = mSP.edit();
		prefEditor.putString("user_pin", "");
		prefEditor.commit();

		if (Session.route != null)
			processEndRoute(Session.route);

		settings.clearPointToNavigate();

		// OsmandSettings.setPIP(mContext, "");

		Session.route = null;

		// Punch out.
		String userId = "";
		if (Session.getDriver() != null) {
			userId = Session.getDriver().getId();
		}
		PunchOutEvent event = new PunchOutEvent(getApplicationContext(), userId);
		event.doPunchOut(true);
		PointOfInterestManager.getInstance().clear();

		OsmandSettings.setLogoutMap(this, "logout_user");

		Intent intent = new Intent();
		intent.setAction("com.operasoft.messages.IgnitionOFF");
		sendBroadcast(intent);

		// mContext.stopService(new Intent(mContext, GPSService.class));

		// Stopping alarm services after logout.
		// MyAlarmService.cancelMyAlarmService(mContext.getApplicationContext());
		// MyAlarmService2.cancelMyAlarmService2(mContext.getApplicationContext());

		// if(mBound)
		// appContext.unbindService(mConnection);
		// appContext.stopService(intGPS);

		intent = new Intent(this, Sw_LoginScreenActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

		finish();

	}

	private void processEndRoute(final Route route) {
		getRoutingHelper().clearCurrentRoute(null);
		RouteCustomDialogHandler routeHandler = new RouteCustomDialogHandler(MapActivity.this, getWindow().getDecorView().getRootView(), MapActivity.this);
		routeHandler.showNone();
		Toast.makeText(this, "Ending route", Toast.LENGTH_LONG).show();
		Session.route = null;
		if (route == null)
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				EndRoute endRoute = new EndRoute();
				endRoute.setDriverName(Session.getDriver().getFirstName());
				endRoute.setDateTime(CommonUtils.UtcDateNow());
				if (Session.clocation != null) {
					endRoute.setLatitude(String.valueOf(Session.clocation.getLatitude()));
					endRoute.setLongitude(String.valueOf(Session.clocation.getLongitude()));
				}
				endRoute.setRouteId(route.getId());
				endRoute.setCompanyId(Session.getCompanyId());
				endRoute.setTimeSpent(secondsSpent);
				endRoute.setVehicleId(Session.getVehicle().getId());
				endRoute.setUser_id(Session.getDriver().getId());
				RouteSelectedDao routeSelectedDao = new RouteSelectedDao();
				endRoute.setRoute_selection_id(routeSelectedDao.getRouteSelectionId(route.getId(), Session.getDriver().getId()));
				
				EndRoutePushSync endRoutesPushSync = new EndRoutePushSync();
				endRoutesPushSync.pushData(getApplicationContext(), endRoute);
			}
		}).start();
		
	}

	/**
	 * show punch out dialog with message you are already punched in
	 * 
	 * @param punchDto
	 */
	/** TODO Enabled this when we integrate Schumacher in production
	private void showPunchOutDialog(Punch punchDto) {
		dialogPunch = new Dialog(MapActivity.this);
		Builder builder = new AlertDialog.Builder(MapActivity.this);
		LayoutInflater inflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock, null);
		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogPunch.dismiss();
			}
		});
		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialogPunch.dismiss();
			}
		});

		builder.setCustomTitle(layout);
		View layout1;
		layout1 = inflater.inflate(R.layout.dialog_punched_already, null);

		final User user = userDao.getById(punchDto.getUserId());
		((TextView) layout1.findViewById(R.id.tv_dpa_message)).setText(user.getFirstName() + " " + user.getLastName() + ", You are currently punched in on " + punchDto.getDateTime()
				+ ", and cannot punch in again. Click on below button if you would like to punch out");

		((Button) layout1.findViewById(R.id.btn_dpa_punch_out)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PunchOutEvent event = new PunchOutEvent(MapActivity.this, user.getId());
				event.send(false);
				dialogPunch.dismiss();
			}
		});

		builder.setView(layout1);
		dialogPunch = builder.create();
		dialogPunch.setCancelable(false);
		dialogPunch.show();

	}
	 */

	/** TODO Enabled this when we integrate Schumacher in production
	private void showTrailerSelector() {
		dialog = new Dialog(MapActivity.this);
		Builder builder = new AlertDialog.Builder(MapActivity.this);
		LayoutInflater inflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout1 = inflater.inflate(R.layout.dialog_many_polygons, null);
		TextView spinnerTitle = (TextView) layout1.findViewById(R.id.textView1);
		Spinner trailerSelector = (Spinner) layout1.findViewById(R.id.spinner_polygons);
		spinnerTitle.setText("Select trailer");
		final ArrayList<String> list = new ArrayList<String>();
		list.add("select trailer from list");
		list.add("none");
		EquipmentTypesDao equipemntTypesDao = new EquipmentTypesDao();
		final List<EquipmentTypes> equipmentTypesList = equipemntTypesDao.listTrailers();

		String[] equipmentTypeidList = new String[equipmentTypesList.size()];
		int i = 0;
		for (EquipmentTypes equiptment : equipmentTypesList) {
			equipmentTypeidList[i] = equiptment.getId();
			i++;
		}
		final VehiclesDao vehicleDao = new VehiclesDao();
		if (equipmentTypeidList.length != 0) {
			trailerList = vehicleDao.listTrailers(equipmentTypeidList);
			for (Vehicle vehicle : trailerList) {
				list.add(vehicle.getName());

			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trailerSelector.setAdapter(dataAdapter);
		trailerSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (pos == 1) {
					dialog.dismiss();
				} else if (pos > 1) {
					dialog.dismiss();
					VehiclePushSync vPush = new VehiclePushSync();
					Vehicle vehicle = vehicleDao.getById(Session.getVehicle().getId());
					vehicle.setTrailerId(trailerList.get(pos - 2).getId());
					vPush.pushData(MapActivity.this, vehicle);
					Session.setTrailer(trailerList.get(pos - 2));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();

	} */

	private void updateConfigInfo() {
		int versionCode = -1;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			Config.setVersion(versionCode);
		} catch (Exception e) {
		}
		Utils cU = new Utils(getApplicationContext());
		Config.setIMEI(cU.getIMEI());
	}

	public void startLocationRequests() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		try {
			service.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIMEOUT_REQUEST, GPS_DIST_REQUEST, gpsListener);
		} catch (IllegalArgumentException e) {
			Log.d(LogUtil.TAG, "GPS location provider not available"); //$NON-NLS-1$ 
		}
		// try to always ask for network provide : it is faster way to find
		// location
		try {
			service.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_TIMEOUT_REQUEST, GPS_DIST_REQUEST, networkListener);
		} catch (IllegalArgumentException e) {
			Log.d(LogUtil.TAG, "Network location provider not available"); //$NON-NLS-1$ 
		}
	}

	private void notRestoreRoutingMode() {
		boolean changed = settings.APPLICATION_MODE.set(settings.PREV_APPLICATION_MODE.get());
		updateApplicationModeSettings();
		routingHelper.clearCurrentRoute(null);
		mapView.refreshMap(changed);
	}

	private void restoreRoutingMode(final LatLon pointToNavigate) {
		final String gpxPath = settings.FOLLOW_THE_GPX_ROUTE.get();
		if (pointToNavigate == null && gpxPath == null) {
			notRestoreRoutingMode();
		} else {
			Runnable encapsulate = new Runnable() {
				int delay = 7;
				boolean quit = false;
				Runnable delayDisplay = null;

				@Override
				public void run() {
					Builder builder = new AccessibleAlertBuilder(MapActivity.this);
					final TextView tv = new TextView(MapActivity.this);
					tv.setText(getString(R.string.continue_follow_previous_route_auto, delay + ""));
					tv.setPadding(7, 5, 7, 5);
					builder.setView(tv);
					builder.setPositiveButton(R.string.default_buttons_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							restoreRoutingMode();

						}
					});
					builder.setNegativeButton(R.string.default_buttons_no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							notRestoreRoutingMode();
							quit = true;
						}
					});
					final AlertDialog dlg = builder.show();
					dlg.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							quit = true;
						}
					});
					dlg.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							quit = true;
						}
					});
					delayDisplay = new Runnable() {
						@Override
						public void run() {
							if (!quit) {
								delay--;
								tv.setText(getString(R.string.continue_follow_previous_route_auto, delay + ""));
								if (delay <= 0) {
									dlg.dismiss();
									restoreRoutingMode();
								} else {
									uiHandler.postDelayed(delayDisplay, 1000);
								}
							}
						}
					};
					delayDisplay.run();
				}

				private void restoreRoutingMode() {
					quit = true;
					AsyncTask<String, Void, GPXFile> task = new AsyncTask<String, Void, GPXFile>() {
						@Override
						protected GPXFile doInBackground(String... params) {
							if (gpxPath != null) {
								// Reverse also should be stored ?
								GPXFile f = GPXUtilities.loadGPXFile(MapActivity.this, new File(gpxPath), false);
								if (f.warning != null) {
									return null;
								}
								return f;
							} else {
								return null;
							}
						}

						@Override
						protected void onPostExecute(GPXFile result) {
							final GPXRouteParams gpxRoute = result == null ? null : new GPXRouteParams(result, false, settings);
							LatLon endPoint = pointToNavigate != null ? pointToNavigate : gpxRoute.getLastPoint();
							Location startPoint = gpxRoute == null ? null : gpxRoute.getStartPointForRoute();
							if (endPoint == null) {
								notRestoreRoutingMode();
							} else {
								routingHelper.setFollowingMode(true);
								routingHelper.setFinalAndCurrentLocation(endPoint, startPoint, gpxRoute);
								getMyApplication().showDialogInitializingCommandPlayer(MapActivity.this);
							}
						}
					};
					task.execute(gpxPath);

				}
			};
			encapsulate.run();
		}

	}

	public OsmandApplication getMyApplication() {
		return ((OsmandApplication) getApplication());
	}

	public void addDialogProvider(DialogProvider dp) {
		dialogProviders.add(dp);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		for (DialogProvider dp : dialogProviders) {
			dialog = dp.onCreateDialog(id);
			if (dialog != null) {
				return dialog;
			}
		}
		if (id == OsmandApplication.PROGRESS_DIALOG) {
			return startProgressDialog;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		for (DialogProvider dp : dialogProviders) {
			dp.onPrepareDialog(id, dialog);
		}
	}

	public void changeZoom(float newZoom) {
		newZoom = Math.round(newZoom * OsmandMapTileView.ZOOM_DELTA) * OsmandMapTileView.ZOOM_DELTA_1;
		boolean changeLocation = settings.AUTO_ZOOM_MAP.get();
		mapView.getAnimatedDraggingThread().startZooming(newZoom, changeLocation);
		if (getMyApplication().accessibilityEnabled())
			AccessibleToast.makeText(this, getString(R.string.zoomIs) + " " + String.valueOf(newZoom), Toast.LENGTH_SHORT).show(); //$NON-NLS-1$ 
		showAndHideMapPosition();
	}

	public void backToMainMenu() {
		final Dialog dlg = new Dialog(this, R.style.Dialog_Fullscreen);
		final View menuView = getLayoutInflater().inflate(R.layout.menu, null);
		menuView.setBackgroundColor(Color.argb(200, 150, 150, 150));
		dlg.setContentView(menuView);
		MainMenuActivity.onCreateMainMenu(dlg.getWindow(), this);
		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				ColorDrawable colorDraw = ((ColorDrawable) menuView.getBackground());
				colorDraw.setAlpha((int) (interpolatedTime * 200));
			}
		};
		anim.setDuration(700);
		anim.setInterpolator(new AccelerateInterpolator());
		menuView.setAnimation(anim);

		View showMap = dlg.findViewById(R.id.MapButton);
		showMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		View settingsButton = dlg.findViewById(R.id.SettingsButton);
		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent settings = new Intent(MapActivity.this, OsmandIntents.getSettingsActivity());
				MapActivity.this.startActivity(settings);
				dlg.dismiss();
			}
		});

		View favouritesButton = dlg.findViewById(R.id.FavoritesButton);
		favouritesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent favorites = new Intent(MapActivity.this, OsmandIntents.getFavoritesActivity());
				favorites.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				MapActivity.this.startActivity(favorites);
				dlg.dismiss();
			}
		});

		View closeButton = dlg.findViewById(R.id.CloseButton);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();

				getMyApplication().closeApplication();
				// 1. Work for almost all cases when user open apps from main
				// menu
				Intent newIntent = new Intent(MapActivity.this, OsmandIntents.getMainMenuActivity());
				newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				newIntent.putExtra(MainMenuActivity.APP_EXIT_KEY, MainMenuActivity.APP_EXIT_CODE);
				startActivity(newIntent);
				// 2. good analogue but user will come back to the current
				// activity onResume()
				// so application is not reloaded !!!
				// moveTaskToBack(true);
				// 3. bad results if user comes from favorites
				// MapActivity.this.setResult(MainMenuActivity.APP_EXIT_CODE);
				// MapActivity.this.finish();
			}
		});

		View searchButton = dlg.findViewById(R.id.SearchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent search = new Intent(MapActivity.this, OsmandIntents.getSearchActivity());
				LatLon loc = getMapLocation();
				search.putExtra(SearchActivity.SEARCH_LAT, loc.getLatitude());
				search.putExtra(SearchActivity.SEARCH_LON, loc.getLongitude());
				// causes wrong position caching:
				// search.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				search.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MapActivity.this.startActivity(search);
				dlg.dismiss();
			}
		});
		menuView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});

		dlg.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// some application/hardware needs that back button reacts on key
			// up, so
			// that they could do some key combinations with it...
			// Victor : doing in that way doesn't close dialog properly!
			return true;
		} else if (getMyApplication().accessibilityEnabled() && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
			if (!uiHandler.hasMessages(LONG_KEYPRESS_MSG_ID)) {
				Message msg = Message.obtain(uiHandler, new Runnable() {
					@Override
					public void run() {
						emitNavigationHint();
					}
				});
				msg.what = LONG_KEYPRESS_MSG_ID;
				uiHandler.sendMessageDelayed(msg, LONG_KEYPRESS_DELAY);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
			Intent newIntent = new Intent(MapActivity.this, OsmandIntents.getSearchActivity());
			// causes wrong position caching:
			// newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			LatLon loc = getMapLocation();
			newIntent.putExtra(SearchActivity.SEARCH_LAT, loc.getLatitude());
			newIntent.putExtra(SearchActivity.SEARCH_LON, loc.getLongitude());
			startActivity(newIntent);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			return true;
		} else if (!routingHelper.isFollowingMode() && OsmandPlugin.getEnabledPlugin(AccessibilityPlugin.class) != null) {
			// Find more appropriate plugin for it?
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getRepeatCount() == 0) {
				if (mapView.isZooming()) {
					changeZoom(mapView.getZoom() + 2);
				} else {
					changeZoom(mapView.getZoom() + 1);
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getRepeatCount() == 0) {
				changeZoom(mapView.getZoom() - 1);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public String getNavigationHint(LatLon point) {
		String hint = navigationInfo.getDirectionString(point, mapLayers.getLocationLayer().getHeading());
		if (hint == null)
			hint = getString(R.string.no_info);
		return hint;
	}

	private void emitNavigationHint() {
		final LatLon point = settings.getPointToNavigate();
		if (point != null) {
			if (routingHelper.isRouteCalculated()) {
				routingHelper.getVoiceRouter().announceCurrentDirection(getLastKnownLocation());
			} else {
				AccessibleToast.makeText(this, getNavigationHint(point), Toast.LENGTH_LONG).show();
			}
		} else {
			AccessibleToast.makeText(this, R.string.mark_final_location_first, Toast.LENGTH_SHORT).show();
		}
	}

	public void setMapLocation(double lat, double lon, float zoom) {
		mapView.setLatLon(lat, lon);
		locationChanged(lat, lon, this);

		if (zoom > 0)
			mapView.setZoom(zoom);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE && settings.USE_TRACKBALL_FOR_MOVEMENTS.get()) {
			float x = event.getX();
			float y = event.getY();
			LatLon l = mapView.getLatLonFromScreenPoint(mapView.getCenterPointX() + x * 15, mapView.getCenterPointY() + y * 15);
			setMapLocation(l.getLatitude(), l.getLongitude(), 0);
			return true;
		}
		return super.onTrackballEvent(event);
	}

	protected void setProgressDlg(Dialog progressDlg) {
		this.progressDlg = progressDlg;
	}

	protected Dialog getProgressDlg() {
		return progressDlg;
	}

	@Override
	protected void onStop() {
		if (routingHelper.isFollowingMode()) {
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNotificationManager.notify(APP_NOTIFICATION_ID, getNotification());
		}
		if (progressDlg != null) {
			progressDlg.dismiss();
			progressDlg = null;
		}
		PoiManager.disconnectPolygonDetector();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OsmandPlugin.onMapActivityDestroy(this);
		savingTrackHelper.close();

		//TODO 001 confirm useless  routeAnimation.close();
		cancelNotification();
		getMyApplication().getResourceManager().getMapTileDownloader().removeDownloaderCallback(mapView);
	}

	private void cancelNotification() {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		mNotificationManager.cancel(APP_NOTIFICATION_ID);
	}

	private void registerUnregisterSensor(Location location, boolean overruleRegister) {
		boolean currentShowingAngle = settings.SHOW_VIEW_ANGLE.get();
		int currentMapRotation = settings.ROTATE_MAP.get();
		boolean show = overruleRegister || (currentShowingAngle && location != null) || currentMapRotation == OsmandSettings.ROTATE_MAP_COMPASS;
		// show point view only if gps enabled
		if (sensorRegistered && !show) {
			Log.d(LogUtil.TAG, "Disable sensor"); //$NON-NLS-1$ 
			((SensorManager) getSystemService(SENSOR_SERVICE)).unregisterListener(this);
			sensorRegistered = false;
			previousSensorValue = 0;
			mapLayers.getLocationLayer().setHeading(null);
		} else if (!sensorRegistered && show) {
			Log.d(LogUtil.TAG, "Enable sensor"); //$NON-NLS-1$ 
			SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
			Sensor s = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			if (s != null) {
				if (!sensorMgr.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL)) {
					Log.e(LogUtil.TAG, "Sensor could not be enabled");
				}
			}
			sensorRegistered = true;
		}
	}

	public void backToLocationImpl() {
		mapLayers.getMapInfoLayer().getBackToLocation().setEnabled(false);
		PointLocationLayer locationLayer = mapLayers.getLocationLayer();
		if (!isMapLinkedToLocation()) {
			setMapLinkedToLocation(true);
			if (locationLayer.getLastKnownLocation() != null) {
				Location lastKnownLocation = locationLayer.getLastKnownLocation();
				AnimateDraggingMapThread thread = mapView.getAnimatedDraggingThread();
				//TODO 00 confirm remove zoom float fZoom = mapView.getFloatZoom() < 13 ? 13 : mapView.getFloatZoom();
				thread.startMoving(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), Session.userZoom, false);
			}
		}
		if (locationLayer.getLastKnownLocation() == null) {
			AccessibleToast.makeText(this, R.string.unknown_location, Toast.LENGTH_LONG).show();
		}
	}

	// location not null!
	private void updateSpeedBearingEmulator(Location location) {
		// For network/gps it's bad way (not accurate). It's widely used for
		// testing purposes
		// possibly keep using only for emulator case
		PointLocationLayer locationLayer = mapLayers.getLocationLayer();
		if (locationLayer.getLastKnownLocation() != null) {
			if (locationLayer.getLastKnownLocation().distanceTo(location) > 3) {
				float d = location.distanceTo(locationLayer.getLastKnownLocation());
				long time = location.getTime() - locationLayer.getLastKnownLocation().getTime();
				float speed;
				if (time == 0) {
					speed = 0;
				} else {
					speed = (d * 1000) / time;
				}
				// Be aware only for emulator ! code is incorrect in case of
				// airplane
				if (speed > 100) {
					speed = 100;
				}
				location.setSpeed(speed);
			}
		}
		if (locationLayer.getLastKnownLocation() != null && !location.hasBearing()) {
			if (locationLayer.getLastKnownLocation().distanceTo(location) > 10 && !isRunningOnEmulator()) {
				// very innacurate
				// location.setBearing(locationLayer.getLastKnownLocation().bearingTo(location));
			}
		}
	}

	public boolean isPointAccurateForRouting(Location loc) {
		return loc != null && loc.getAccuracy() < ACCURACY_FOR_GPX_AND_ROUTING * 3 / 2;
	}

	int speedCounter = 0;

	/* DEBUG only 
	GpxSimulator simulator = new GpxSimulator();
	 */
	public void setLocation(final Location location) {
		if (Log.isLoggable(LogUtil.TAG, Log.DEBUG)) {
			Log.d(LogUtil.TAG, "Location changed " + location.getProvider()); //$NON-NLS-1$ 
		}
		/* DEBUG only 
		simulator.setLocation(location);
		mapView.postDelayed(new Runnable() {
			@Override
			public void run() {
				setLocation(location);
			}
		}, 600);
		 */
		// 1. Logging services
		if (location != null) {
			Session.clocation = location;
			final Point newPoint = new Point(location.getLatitude(), location.getLongitude());
			if (mPolygonDetector != null) {
				Log.d(LogUtil.TAG, " sending position " + newPoint.getLatitude() + ":" + newPoint.getLongitude());
				mPolygonDetector.setLocation(newPoint);
			} else {
				Log.d(LogUtil.TAG, " ----- position not sent ");
			}
			/**
			 * Points in Polygons
			 */
			//			if (false && (mapView.getZoom() >= 15) && (!Session.compassState.equals("lock"))) {
			//				backToLocationImpl();
			//			}

			if (Session.compassState != COMPASS_LOCK) {
				mapView.setLatLon(location.getLatitude(), location.getLongitude());

			} else if (location.getSpeed() > FIVE_KM_PER_HOUR) {
				// if move at speed > FIVE_KM_PER_HOUR for next 5 seconds
				if (++speedCounter > 4) {
					speedCounter = 0;
					setToNavigationMode();
				}

			} else {
				speedCounter = 0;
			}
			// use because there is a bug on some devices with
			// location.getTime()
			long locationTime = System.currentTimeMillis();
			// write only with 50 meters accuracy
			if (!location.hasAccuracy() || location.getAccuracy() < ACCURACY_FOR_GPX_AND_ROUTING) {
				if (settings.SAVE_TRACK_TO_GPX.get()) {
					savingTrackHelper.insertData(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed(), location.getAccuracy(), locationTime, settings);
				}
				// live monitoring is aware of accuracy (it would be good to
				// create an option)
				if (settings.LIVE_MONITORING.get()) {
					liveMonitoringHelper.insertData(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed(), location.getAccuracy(), location.getTime(), settings);
				}

			}

		}

		if (location != null && isRunningOnEmulator()) {
			// only for emulator
			updateSpeedBearingEmulator(location);
		}
		// 2. accessibility routing
		navigationInfo.setLocation(location);

		// 3. routing
		boolean enableSensorNavigation = routingHelper.isFollowingMode() && settings.USE_COMPASS_IN_NAVIGATION.get() ? location == null || !location.hasBearing() : false;
		registerUnregisterSensor(location, enableSensorNavigation);
		Location updatedLocation = location;
		if (routingHelper.isFollowingMode()) {
			if (location == null || !location.hasAccuracy() || location.getAccuracy() < ACCURACY_FOR_GPX_AND_ROUTING) {
				// Update routing position and get location for sticking mode
				updatedLocation = routingHelper.setCurrentLocation(location, settings.SNAP_TO_ROAD.get());
				if (!routingHelper.isFollowingMode()) {
					// finished
					Message msg = Message.obtain(uiHandler, new Runnable() {
						@Override
						public void run() {
							settings.APPLICATION_MODE.set(settings.PREV_APPLICATION_MODE.get());
							updateApplicationModeSettings();
						}
					});
					uiHandler.sendMessage(msg);
				}
				// Check with delay that gps location is not lost
				if (location != null && routingHelper.getLeftDistance() > 0) {
					final long fixTime = location.getTime();
					Message msg = Message.obtain(uiHandler, new Runnable() {
						@Override
						public void run() {
							Location lastKnown = getLastKnownLocation();
							if (lastKnown != null && lastKnown.getTime() - fixTime < LOST_LOCATION_CHECK_DELAY / 2) {
								// false positive case, still strange how we got
								// here with removeMessages
								return;
							}
							if (routingHelper.getLeftDistance() > 0 && settings.MAP_ACTIVITY_ENABLED.get()) {
								routingHelper.getVoiceRouter().gpsLocationLost();
							}
						}
					});
					msg.what = LOST_LOCATION_MSG_ID;
					uiHandler.removeMessages(LOST_LOCATION_MSG_ID);
					uiHandler.sendMessageDelayed(msg, LOST_LOCATION_CHECK_DELAY);
				}
			}
		}

		// Update information
		mapLayers.getLocationLayer().setLastKnownLocation(updatedLocation);
		if (updatedLocation != null) {
			// updateAutoMapViewConfiguration(updatedLocation);
		} else {
			if (mapLayers.getMapInfoLayer().getBackToLocation().isEnabled()) {
				mapLayers.getMapInfoLayer().getBackToLocation().setEnabled(false);
			}
		}

		// When location is changed we need to refresh map in order to show
		// movement!
		mapView.refreshMap();
	}

	private void updateAutoMapViewConfiguration(Location location) {
		long now = System.currentTimeMillis();
		if (isMapLinkedToLocation()) {
			if (settings.AUTO_ZOOM_MAP.get() && location.hasSpeed()) {
				float zdelta = defineZoomFromSpeed(location.getSpeed());
				if (Math.abs(zdelta) >= OsmandMapTileView.ZOOM_DELTA_1) {
					// prevent ui hysteresis (check time interval for autozoom)
					if (zdelta >= 2) {
						// decrease a bit
						zdelta -= 3 * OsmandMapTileView.ZOOM_DELTA_1;
					} else if (zdelta <= -2) {
						// decrease a bit
						zdelta += 3 * OsmandMapTileView.ZOOM_DELTA_1;
					}
					if (now - lastTimeAutoZooming > 4500) {
						lastTimeAutoZooming = now;
						mapView.setZoom(mapView.getFloatZoom() + zdelta);
						// mapView.getAnimatedDraggingThread().startZooming(mapView.getFloatZoom()
						// + zdelta, false);
					}
				}
			}
			int currentMapRotation = settings.ROTATE_MAP.get();
			if (currentMapRotation == OsmandSettings.ROTATE_MAP_BEARING) {
				if (location.hasBearing()) {
					mapView.setRotate(-location.getBearing());
				} else if (routingHelper.isFollowingMode() && settings.USE_COMPASS_IN_NAVIGATION.get()) {
					if (previousSensorValue != 0 && Math.abs(MapUtils.degreesDiff(mapView.getRotate(), -previousSensorValue)) > 15) {
						if (now - lastTimeSensorRotation > 1500 && now - lastTimeSensorRotation < 15000) {
							lastTimeSensorRotation = now;
							mapView.setRotate(-previousSensorValue);
						}
					}
				}
			}
			mapView.setLatLon(location.getLatitude(), location.getLongitude());
		} else {
			if (!mapLayers.getMapInfoLayer().getBackToLocation().isEnabled()) {
				mapLayers.getMapInfoLayer().getBackToLocation().setEnabled(true);
			}
			if (settings.AUTO_FOLLOW_ROUTE.get() > 0 && routingHelper.isFollowingMode() && !uiHandler.hasMessages(AUTO_FOLLOW_MSG_ID)) {
				backToLocationWithDelay(1);
			}
		}
	}

	public float defineZoomFromSpeed(float speed) {
		//TODO 00 confirm remove Zoom
		if (true)
			return Session.userZoom;
		if (speed < 7f / 3.6) {
			return 0;
		}
		double topLat = mapView.calcLatitude(-mapView.getCenterPointY());
		double cLat = mapView.calcLatitude(0);
		double visibleDist = MapUtils.getDistance(cLat, mapView.getLongitude(), topLat, mapView.getLongitude());
		float time = 75f;
		if (speed < 83f / 3.6) {
			time = 60f;
		}
		double distToSee = speed * time;
		float zoomDelta = (float) (Math.log(visibleDist / distToSee) / Math.log(2.0f));
		zoomDelta = Math.round(zoomDelta * OsmandMapTileView.ZOOM_DELTA) * OsmandMapTileView.ZOOM_DELTA_1;
		// check if 17, 18 is correct?
		if (zoomDelta + mapView.getFloatZoom() > 18 - OsmandMapTileView.ZOOM_DELTA_1) {
			return 18 - OsmandMapTileView.ZOOM_DELTA_1 - mapView.getFloatZoom();
		}
		return zoomDelta;
	}

	public void navigateToPoint(LatLon point, boolean updateRoute) {
		if (point != null) {
			settings.setPointToNavigate(point.getLatitude(), point.getLongitude(), null);
		} else {
			settings.clearPointToNavigate();
		}
		if (updateRoute && (routingHelper.isRouteBeingCalculated() || routingHelper.isRouteCalculated() || routingHelper.isFollowingMode())) {
			routingHelper.setFinalAndCurrentLocation(point, getLastKnownLocation(), routingHelper.getCurrentGPXRoute());
		}
		mapLayers.getNavigationLayer().setPointToNavigate(point);
	}

	public Location getLastKnownLocation() {
		if ((mapLayers != null) && (mapLayers.getLocationLayer() != null)) {
			return mapLayers.getLocationLayer().getLastKnownLocation();
		}

		return null;
	}

	public LatLon getMapLocation() {
		return new LatLon(mapView.getLatitude(), mapView.getLongitude());
	}

	public LatLon getPointToNavigate() {
		return mapLayers.getNavigationLayer().getPointToNavigate();
	}

	public RoutingHelper getRoutingHelper() {
		return routingHelper;
	}

	private boolean isRunningOnEmulator() {
		if (Build.DEVICE.equals("generic")) { //$NON-NLS-1$  
			return true;
		}
		return false;
	}

	private boolean useOnlyGPS() {
		return (routingHelper != null && routingHelper.isFollowingMode()) || (System.currentTimeMillis() - lastTimeGPSLocationFixed) < USE_ONLY_GPS_INTERVAL || isRunningOnEmulator();
	}

	// Working with location listeners
	private final LocationListener networkListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// double check about use only gps that strange situation but it could happen?
			// if (Session.compassState.equals("lock") && Session.moving) {
			// switchRotateMapMode();
			// }
			if (!useOnlyGPS()) {
				setLocation(location);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if (!useOnlyGPS()) {
				setLocation(null);
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (LocationProvider.OUT_OF_SERVICE == status && !useOnlyGPS()) {
				setLocation(null);
			}
		}
	};

	private final LocationListener gpsListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

			if (location != null) {
				if (location.getSpeed() > 1.4) { // Speed
					// check
					// set
					// to
					// 5
					// km/h
					// or
					// 1.4
					// m/s.
					TIT_RoutePoint point = new TIT_RoutePoint();
					point.setLatitude(location.getLatitude());
					point.setLongitude(location.getLongitude());
					PoiManager.addpointToBreadCrumb(point);
				}

				lastTimeGPSLocationFixed = location.getTime();

			}
			setLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (!useOnlyGPS() && service.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				Location loc = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (loc != null && (System.currentTimeMillis() - loc.getTime()) < USE_ONLY_GPS_INTERVAL) {
					setLocation(loc);
				}
			} else {
				setLocation(null);
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (LocationProvider.TEMPORARILY_UNAVAILABLE == status) {
				if (routingHelper.isFollowingMode() && routingHelper.getLeftDistance() > 0) {
					// Suppress
					// gpsLocationLost()
					// prompt here
					// for now, as
					// it
					// causes
					// duplicate
					// announcement
					// and then also
					// prompts when
					// signal is
					// found again
					// routingHelper.getVoiceRouter().gpsLocationLost();
				}
			} else if (LocationProvider.OUT_OF_SERVICE == status) {
				if (routingHelper.isFollowingMode() && routingHelper.getLeftDistance() > 0) {
					routingHelper.getVoiceRouter().gpsLocationLost();
				}
			} else if (LocationProvider.AVAILABLE == status) {
				// Do not remove
				// right now network
				// listener
				// service.removeUpdates(networkListener);
			}

		}
	};

	public LocationListener getGpsListener() {
		return gpsListener;
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationRequests();

		SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.unregisterListener(this);
		sensorRegistered = false;

		getMyApplication().getDaynightHelper().onMapPause();

		settings.setLastKnownMapLocation((float) mapView.getLatitude(), (float) mapView.getLongitude());
		AnimateDraggingMapThread animatedThread = mapView.getAnimatedDraggingThread();
		if (animatedThread.isAnimating() && animatedThread.getTargetZoom() != 0) {
			settings.setMapLocationToShow(animatedThread.getTargetLatitude(), animatedThread.getTargetLongitude(), (int) animatedThread.getTargetZoom());
		}

		settings.setLastKnownMapZoom(mapView.getZoom());
		Session.userZoom = mapView.getZoom();
		settings.MAP_ACTIVITY_ENABLED.set(false);
		getMyApplication().getResourceManager().interruptRendering();
		getMyApplication().getResourceManager().setBusyIndicator(null);
		OsmandPlugin.onMapActivityPause(this);
		/**
		 * Snowboard code
		 */
		if (OsmandSettings.getNoneOnMap(getApplicationContext()) != "") {
			OsmandSettings.setNoneOnMap(getApplicationContext(), "");
			// manager with the new API
			LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
			Location location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (location != null) {
				Session.clocation = location;
				PointLocationLayer pointLocation = new PointLocationLayer();
				pointLocation.setLastKnownLocation(Session.clocation);
				settings.setMapLocationToShow(location != null ? Session.clocation.getLatitude() : 0.0, location != null ? Session.clocation.getLongitude() : 0.0, 15, null);
			}

			Session.route = null;
			mTxtRouteName.setVisibility(View.GONE);
			mTxtRouteTimer.setVisibility(View.GONE);
			mBtnEndRoute.setVisibility(View.GONE);
			mBtnCompleteRoute.setVisibility(View.GONE);
			// routeCrossed.setVisibility(View.GONE);
			OsmandSettings.setRouteTime(this, -1);
			// Clearing Service Location from session if any
			Session.serLoc = null;
			hideDriverComments();
			startActivity(new Intent(this, MapActivity.class));

		}

		// Unregister with the Geofence Manager as we have nothing to update
		GeofenceManager.getInstance().removeGeofenceActionListener(geofenceHandler);
		PoiManager.removePoiEventListener(mMapMenuHeader);
		
		// de-register DbSyncManager events
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

		/**
		 * End of the Snowboard code.
		 */
	}

	public void stopLocationRequests() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		service.removeUpdates(gpsListener);
		service.removeUpdates(networkListener);
	}

	public void updateApplicationModeSettings() {
		// int currentMapRotation = settings.ROTATE_MAP.get();
		// if (currentMapRotation == OsmandSettings.ROTATE_MAP_NONE) {
		mapView.setRotate(OsmandSettings.ROTATE_MAP_NONE);
		// }
		// routingHelper.setAppMode(settings.getApplicationMode());

		settings.ROTATE_MAP.set(OsmandSettings.ROTATE_MAP_NONE);
		// mapView.setMapPosition(settings.POSITION_ON_MAP.get());

		if (Session.route != null)
			mapView.setMapPosition(OsmandSettings.BOTTOM_CONSTANT);
		else
			mapView.setMapPosition(OsmandSettings.CENTER_CONSTANT);

		// mapView.setMapPosition(settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_BEARING ?
		// OsmandSettings.BOTTOM_CONSTANT : OsmandSettings.CENTER_CONSTANT);
		registerUnregisterSensor(getLastKnownLocation(), false);
		try {
			mapLayers.getMapInfoLayer().recreateControls();
			mapLayers.updateLayers(mapView);
			getMyApplication().getDaynightHelper().setDayNightMode(settings.DAYNIGHT_MODE.get());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * To toggle between different map view states 1. north up 2. rotate 3. lock
	 */
	//	public void switchRotateMapMode() {
	//		System.out.println("MapActivity.switchRotateMapMode()");
	//		boolean done = true;
	//		int resId = R.string.rotate_map_none_opt;
	//		int imgId = R.drawable.north;
	//
	//		int vl = OsmandSettings.ROTATE_MAP_NONE; // (settings.ROTATE_MAP.get() + 1) % 3;
	//		
	//		System.out.println("Value of v1 rotate:"+vl);
	//		settings.ROTATE_MAP.set(vl);
	//
	//		registerUnregisterSensor(getLastKnownLocation(), false);
	//
	//		if (Config.compassState == 3) {
	//			imgId = R.drawable.lock;
	//			resId = R.string.rotate_map_bearing_opt;
	//			Config.compassState = 0;
	//			mapView.setRotate(0);
	//		} else {
	//
	//			if (settings.ROTATE_MAP.get() != OsmandSettings.ROTATE_MAP_COMPASS) {
	//				mapView.setRotate(0);
	//			}
	//
	//			if (settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_COMPASS) {
	//				imgId = R.drawable.transparent;
	//				resId = R.string.rotate_map_compass_opt;
	//			} else if (settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_BEARING) {
	//				done = false;
	//				switchRotateMapMode();
	//			}
	//		}
	//
	//		if (done) {
	//
	//			switch (imgId) {
	//			case R.drawable.transparent:
	//				Session.compassState = "compass";
	//				break;
	//			case R.drawable.north:
	//				Session.compassState = "north";
	//				break;
	//			case R.drawable.lock:
	//				Session.compassState = "lock";
	//				break;
	//			}
	//
	//			mapView.setMapPosition(settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_BEARING ? OsmandSettings.BOTTOM_CONSTANT : OsmandSettings.CENTER_CONSTANT);
	//
	//			mImgCompassState.setImageResource(imgId);
	//			AccessibleToast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	//			mapView.refreshMap();
	//		}
	//	}

	/**
	 * set map view to the navigation mode
	 */
	public void setToNavigationMode() {
		int imgId = R.drawable.transparent;
		Session.compassState = COMPASS_CENTER;

		// 0, 1 or 2
		settings.ROTATE_MAP.set(OsmandSettings.ROTATE_MAP_COMPASS);

		mapView.setRotate(OsmandSettings.ROTATE_MAP_NONE);

		mapView.setMapPosition(OsmandSettings.CENTER_CONSTANT);

		mImgCompassState.setImageResource(imgId);
		// mapView.refreshMap();
	}

	// End SB code
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// some application/hardware needs that back button reacts on key
			// up, so
			// that they could do some key combinations with it...
			// Android 1.6 doesn't have onBackPressed() method it should be
			// finish instead!
			// onBackPressed();
			// return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (!getMyApplication().accessibilityEnabled()) {
				mapActions.contextMenuPoint(mapView.getLatitude(), mapView.getLongitude());
			} else if (uiHandler.hasMessages(LONG_KEYPRESS_MSG_ID)) {
				uiHandler.removeMessages(LONG_KEYPRESS_MSG_ID);
				mapActions.contextMenuPoint(mapView.getLatitude(), mapView.getLongitude());
			}
			return true;
		} else if (settings.ZOOM_BY_TRACKBALL.get()) {
			// Parrot device has only dpad left and right
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				changeZoom(mapView.getZoom() - 1);
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				changeZoom(mapView.getZoom() + 1);
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			int dx = keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 15 : (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ? -15 : 0);
			int dy = keyCode == KeyEvent.KEYCODE_DPAD_DOWN ? 15 : (keyCode == KeyEvent.KEYCODE_DPAD_UP ? -15 : 0);
			LatLon l = mapView.getLatLonFromScreenPoint(mapView.getCenterPointX() + dx, mapView.getCenterPointY() + dy);
			setMapLocation(l.getLatitude(), l.getLongitude(), 0);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			AccessibleToast.makeText(this, R.string.sd_mounted_ro, Toast.LENGTH_LONG).show();
		} else {
			AccessibleToast.makeText(this, R.string.sd_unmounted, Toast.LENGTH_LONG).show();
		}
	}

	public void showAndHideMapPosition() {
		mapView.setShowMapPosition(true);
		Message msg = Message.obtain(uiHandler, new Runnable() {
			@Override
			public void run() {
				if (mapView.isShowMapPosition()) {
					mapView.setShowMapPosition(false);
					mapView.refreshMap();
				}
			}

		});
		msg.what = SHOW_POSITION_MSG_ID;
		uiHandler.removeMessages(SHOW_POSITION_MSG_ID);
		uiHandler.sendMessageDelayed(msg, SHOW_POSITION_DELAY);
	}

	@Override
	public void locationChanged(double newLatitude, double newLongitude, Object source) {

		//if(Session.route == null)
		setMapLockState();

		// when user start dragging
		if (mapLayers.getLocationLayer().getLastKnownLocation() != null) {
			setMapLinkedToLocation(false);
			if (!mapLayers.getMapInfoLayer().getBackToLocation().isEnabled()) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mapLayers.getMapInfoLayer().getBackToLocation().setEnabled(true);
					}
				});
			}
		}
	}

	public OsmandMapTileView getMapView() {
		return mapView;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (true)
			return;
		// Attention : sensor produces a lot of events & can hang the system
		float val = event.values[0];
		if (currentScreenOrientation == 1) {
			// val += 90;
		}
		val += 90;
		previousSensorValue = val;
		if (settings.ROTATE_MAP.get() == OsmandSettings.ROTATE_MAP_COMPASS) {
			if (Math.abs(MapUtils.degreesDiff(mapView.getRotate(), -val)) > 15) {
				mapView.setRotate(-val);
			}
		}
		if (settings.SHOW_VIEW_ANGLE.get().booleanValue()) {
			if (mapLayers.getLocationLayer().getHeading() == null || Math.abs(mapLayers.getLocationLayer().getHeading() - val) > 10) {
				mapLayers.getLocationLayer().setHeading(val);
			}
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// return mapActions.onCreateOptionsMenu(menu);
	// }

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// boolean val = super.onPrepareOptionsMenu(menu);
	// mapActions.onPrepareOptionsMenu(menu);
	// return val;
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// return mapActions.onOptionsItemSelected(item) == true ? true :
	// super.onOptionsItemSelected(item);
	// }

	protected void parseLaunchIntentLocation() {
		Intent intent = getIntent();
		if (intent != null && intent.getData() != null) {
			Uri data = intent.getData();
			if ("http".equalsIgnoreCase(data.getScheme()) && "download.osmand.net".equals(data.getHost()) && "/go".equals(data.getPath())) {
				String lat = data.getQueryParameter("lat");
				String lon = data.getQueryParameter("lon");
				if (lat != null && lon != null) {
					try {
						double lt = Double.parseDouble(lat);
						double ln = Double.parseDouble(lon);
						String zoom = data.getQueryParameter("z");
						int z = settings.getLastKnownMapZoom();
						if (zoom != null) {
							z = Integer.parseInt(zoom);
						}
						settings.setMapLocationToShow(lt, ln, z, getString(R.string.shared_location));
					} catch (NumberFormatException e) {
					}
				}
			}
		}
	}

	public FavouritesDbHelper getFavoritesHelper() {
		return getMyApplication().getFavorites();
	}

	public MapActivityActions getMapActions() {
		return mapActions;
	}

	public MapActivityLayers getMapLayers() {
		return mapLayers;
	}

	public SavingTrackHelper getSavingTrackHelper() {
		return savingTrackHelper;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public static void launchMapActivityMoveToTop(Activity activity) {
		Intent newIntent = new Intent(activity, OsmandIntents.getMapActivity());
		newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(newIntent);
	}

	private boolean isMapLinkedToLocation() {
		return isMapLinkedToLocation;
	}

	public void setMapLinkedToLocation(boolean isMapLinkedToLocation) {
		if (!isMapLinkedToLocation) {
			int autoFollow = settings.AUTO_FOLLOW_ROUTE.get();
			if (autoFollow > 0 && routingHelper.isFollowingMode()) {
				backToLocationWithDelay(autoFollow);
			}
		}
		this.isMapLinkedToLocation = isMapLinkedToLocation;
	}

	private void backToLocationWithDelay(int delay) {

		if (true)
			return;
		uiHandler.removeMessages(AUTO_FOLLOW_MSG_ID);
		Message msg = Message.obtain(uiHandler, new Runnable() {
			@Override
			public void run() {
				if (settings.MAP_ACTIVITY_ENABLED.get()) {
					AccessibleToast.makeText(MapActivity.this, R.string.auto_follow_location_enabled, Toast.LENGTH_SHORT).show();
					backToLocationImpl();
				}
			}
		});
		msg.what = AUTO_FOLLOW_MSG_ID;
		uiHandler.sendMessageDelayed(msg, delay * 1000);
	}

	public NavigationInfo getNavigationInfo() {
		return navigationInfo;
	}

	public void setMapLockState() {
		Session.compassState = COMPASS_LOCK;
		try {
			mImgCompassState.setImageResource(R.drawable.lock);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ringTheBell() {

		try {
			PIPMediaPlayer pipMedia = new PIPMediaPlayer(MapActivity.this);
			// pipMedia.player();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	EnterExitPoiListener mapInOutPoiListener = new EnterExitPoiListener() {
		@Override
		public void onEnter(Polygon polygon, PointOfInterest poi) {
			if (poi == null)
				return;

			final boolean showPopup = Session.route != null && !Session.getDisableOnRouteAutoPopup();
			ringTheBell();
			showDriverComments(poi.getDriverComments());

			try {
				final OsmandMapTileView view = new OsmandMapTileView(MapActivity.this);
				final PointOfInterestMenu menu = new PointOfInterestMenu(new PointOfInterestActionHandler(MapActivity.this), view);
				if (poi.getCurrentServiceActivity() != null && poi.getCurrentServiceActivity().getStatus().equals(ServiceActivity.SA_IN_DIRECTION)) {
					DropEmployeesDao dropEmployeesDao = new DropEmployeesDao();
					if (dropEmployeesDao.isDropped(poi.getCurrentServiceActivity().getServiceLocationId())) {
						TaskListCustomDialog dialog = new TaskListCustomDialog(MapActivity.this, poi);
						dialog.createDialog();
					} else {
						poi.attachServiceLocation(PoiManager.routeSlList.get(0));
						if (showPopup && Session.route != null && Session.route.getPopUp().equals(Route.SHOW_POPUP)) {
							menu.createDialog(poi);
						}
					}
				} else {
					if (showPopup)
						menu.createDialog(poi, 8000);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onExit(Polygon polygon, PointOfInterest poi) {
			hideDriverComments();
			Toast.makeText(MapActivity.this, "(i) Leaving sevice location " + polygon.getDisplayName(), Toast.LENGTH_LONG).show();

		}

		@Override
		public void onDelayExceeded(Polygon polygon, PointOfInterest poi) {
			// tricky way to refresh the map and refresh the POI status
			int tempZoom = mapView.getZoom();
			mapView.setZoom(tempZoom + 1);

			ringTheBell();
			(new PointOfInterestActionHandler(MapActivity.this)).serviceLocationCompleted(poi);

			// Let's ask the GPS service to report its position immediately with a heading change
			Intent intent = new Intent();
			intent.setAction(GPSService.TRIGGER_MESSAGE_EVENT);
			intent.putExtra(GPSService.EVENT_CODE_EVENT_PARAM, XirgoForwarder.HEADING_CHANGE_EVENT_CODE);
			sendBroadcast(intent);

			mapView.setZoom(tempZoom);
		}

	};

	PointOfInterestManager.StatusListener statusListener = new PointOfInterestManager.StatusListener() {

		@Override
		public void onAssistedMode() {

		}

		@Override
		public void onNoneMode() {
			if (mMapMenuHeader != null) {
				mMapMenuHeader.updatePoiHeader();
			}

		}

		@Override
		public void onNewServiceLocationUpdate(ServiceLocation sl) {
			// TODO Auto-generated method stub

		}
	};

	private void hideDriverComments() {
		mTxtDriverComments.setVisibility(View.INVISIBLE);
	}

	private void showDriverComments(String comments) {

		if (comments != null && comments.trim().length() > 0) {
			mTxtDriverComments.setVisibility(View.VISIBLE);
			mTxtDriverComments.setText(comments);
		}
	}




}
