package net.osmand.plus;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.osmand.Algoritms;
import net.osmand.FavouritePoint;
import net.osmand.GPXUtilities;
import net.osmand.GPXUtilities.GPXFile;
import net.osmand.GPXUtilities.WptPt;
import net.osmand.LogUtil;
import net.osmand.Version;
import net.osmand.access.AccessibilityMode;
import net.osmand.access.AccessibleToast;
import net.osmand.plus.activities.DayNightHelper;
import net.osmand.plus.activities.LiveMonitoringHelper;
import net.osmand.plus.activities.OsmandIntents;
import net.osmand.plus.activities.SavingTrackHelper;
import net.osmand.plus.activities.SettingsActivity;
import net.osmand.plus.render.NativeOsmandLibrary;
import net.osmand.plus.render.RendererRegistry;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.voice.CommandPlayer;
import net.osmand.plus.voice.CommandPlayerException;
import net.osmand.plus.voice.CommandPlayerFactory;
import net.osmand.render.RenderingRulesStorage;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.bidforfix.andorid.BidForFixHelper;
import com.operasoft.android.gps.services.GPSService;
import com.operasoft.android.gps.services.GPSService.LocalBinder;
import com.operasoft.android.service.ReverseGeocodingService;
import com.operasoft.android.service.ReverseGeocodingService.ReverseGeocodingBinder;
import com.operasoft.android.util.Utils;
import com.operasoft.geom.Point;
import com.operasoft.geom.StreetPoint;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.WorksheetActivity;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.database.WorksheetsDao;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.Session;
import com.operasoft.snowboard.voice.TTSCommandStreetNamePlayerImpl;
import com.operasoft.snowboard.voice.TTSCommandStreetNamePlayerImpl.StreetFinder;

@ReportsCrashes(formKey = "", // will not be used
formUri = "http://crash.dev.snowman.operasoft.ca/submit.php", formUriBasicAuthLogin = "crash", // optional
formUriBasicAuthPassword = "Cr@sh", // optional
mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class OsmandApplication extends Application {

	private static OsmandApplication mInstance;

	public OsmandApplication() {
		mInstance = this;
	}

	public static OsmandApplication getInstance() {
		return mInstance == null ? (mInstance = new OsmandApplication()) : mInstance;
	}

	public static final String EXCEPTION_PATH = ResourceManager.APP_DIR + "exception.log"; //$NON-NLS-1$
	private static final org.apache.commons.logging.Log LOG = LogUtil.getLog(OsmandApplication.class);

	ResourceManager manager = null;
	PoiFiltersHelper poiFilters = null;
	RoutingHelper routingHelper = null;
	FavouritesDbHelper favorites = null;
	CommandPlayer player = null;

	OsmandSettings osmandSettings = null;

	DayNightHelper daynightHelper;
	NavigationService navigationService;
	RendererRegistry rendererRegistry;
	BidForFixHelper bidforfix;

	// start variables
	private ProgressDialogImplementation startDialog;
	private List<String> startingWarnings;
	private Handler uiHandler;
	private GPXFile gpxFileToDisplay;
	private SavingTrackHelper savingTrackHelper;
	private LiveMonitoringHelper liveMonitoringHelper;

	private boolean applicationInitializing = false;
	private Locale prefferedLocale = null;

	private GPSService gpsService;
	private final GPSServiceConnection gpsConnection = new GPSServiceConnection();

	@Override
	public void onCreate() {
		super.onCreate();

		// Configure the application based on its build type
		Config.init();

		// Initialize the crash reporting engine
		ACRAConfiguration crashConfig = ACRA.getNewDefaultConfig(this);
		Config.updateCrashConfig(crashConfig);
		ACRA.setConfig(crashConfig);
		ACRA.init(this);

		// Start the GPS Service
		launchGPSService();

		long timeToStart = System.currentTimeMillis();
		osmandSettings = createOsmandSettingsInstance();
		routingHelper = new RoutingHelper(osmandSettings, this, player)

		/* Play the selected route - DEBUG ONLY !!
		{

			// Play the selected route - DEBUG ONLY !!
			@Override
			public synchronized void setFinalAndCurrentLocation(LatLon finalLocation, Location currentLocation, GPXRouteParams gpxRoute) {
				super.setFinalAndCurrentLocation(finalLocation, currentLocation, gpxRoute);
				GpxSimulator.initRoad(gpxRoute.getAllPoints());
			}

		}
		*/
		;
		manager = new ResourceManager(this);
		daynightHelper = new DayNightHelper(this);
		bidforfix = new BidForFixHelper("osmand.net", getString(R.string.default_buttons_support), getString(R.string.default_buttons_cancel));
		savingTrackHelper = new SavingTrackHelper(this);
		liveMonitoringHelper = new LiveMonitoringHelper(this);
		uiHandler = new Handler();
		rendererRegistry = new RendererRegistry();
		checkPrefferedLocale();
		startApplication();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Time to start application " + (System.currentTimeMillis() - timeToStart) + " ms. Should be less < 800 ms");
		}
		timeToStart = System.currentTimeMillis();
		OsmandPlugin.initPlugins(this);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Time to init plugins " + (System.currentTimeMillis() - timeToStart) + " ms. Should be less < 800 ms");
		}
		startRgcService();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		if (routingHelper != null) {
			routingHelper.getVoiceRouter().onApplicationTerminate(getApplicationContext());
		}
		if (bidforfix != null) {
			bidforfix.onDestroy();
		}
		stopRgcService();
	}

	public void onUpdateCompany(Company company) {
		osmandSettings.update(company);
	}

	public RendererRegistry getRendererRegistry() {
		return rendererRegistry;
	}

	/**
	 * Creates instance of OsmandSettings
	 * 
	 * @return Reference to instance of OsmandSettings
	 */
	protected OsmandSettings createOsmandSettingsInstance() {
		return new OsmandSettings(this);
	}

	/**
	 * Application settings
	 * 
	 * @return Reference to instance of OsmandSettings
	 */
	public OsmandSettings getSettings() {
		if (osmandSettings == null) {
			LOG.error("Trying to access settings before they were created");
		}
		return osmandSettings;
	}

	public SavingTrackHelper getSavingTrackHelper() {
		return savingTrackHelper;
	}

	public LiveMonitoringHelper getLiveMonitoringHelper() {
		return liveMonitoringHelper;
	}

	public PoiFiltersHelper getPoiFilters() {
		if (poiFilters == null) {
			poiFilters = new PoiFiltersHelper(this);
		}
		return poiFilters;
	}

	public void setGpxFileToDisplay(GPXFile gpxFileToDisplay, boolean showCurrentGpxFile) {
		this.gpxFileToDisplay = gpxFileToDisplay;
		osmandSettings.SHOW_CURRENT_GPX_TRACK.set(showCurrentGpxFile);
		if (gpxFileToDisplay == null) {
			getFavorites().setFavoritePointsFromGPXFile(null);
		} else {
			List<FavouritePoint> pts = new ArrayList<FavouritePoint>();
			for (WptPt p : gpxFileToDisplay.points) {
				FavouritePoint pt = new FavouritePoint();
				pt.setLatitude(p.lat);
				pt.setLongitude(p.lon);
				if (p.name == null) {
					p.name = "";
				}
				pt.setName(p.name);
				pts.add(pt);
			}
			gpxFileToDisplay.proccessPoints();
			getFavorites().setFavoritePointsFromGPXFile(pts);
		}
	}

	public GPXFile getGpxFileToDisplay() {
		return gpxFileToDisplay;
	}

	public FavouritesDbHelper getFavorites() {
		if (favorites == null) {
			favorites = new FavouritesDbHelper(this);
		}
		return favorites;
	}

	public ResourceManager getResourceManager() {
		return manager;
	}

	public DayNightHelper getDaynightHelper() {
		return daynightHelper;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		manager.onLowMemory();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (prefferedLocale != null && !newConfig.locale.getLanguage().equals(prefferedLocale.getLanguage())) {
			super.onConfigurationChanged(newConfig);
			// ugly fix ! On devices after 4.0 screen is blinking when you rotate device!
			if (Build.VERSION.SDK_INT < 14) {
				newConfig.locale = prefferedLocale;
			}
			getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
			Locale.setDefault(prefferedLocale);
		} else {
			super.onConfigurationChanged(newConfig);
		}
	}

	public void checkPrefferedLocale() {
		Configuration config = getBaseContext().getResources().getConfiguration();
		String lang = osmandSettings.PREFERRED_LOCALE.get();
		if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
			prefferedLocale = new Locale(lang);
			Locale.setDefault(prefferedLocale);
			config.locale = prefferedLocale;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
		}

	}

	public static final int PROGRESS_DIALOG = 5;

	/**
	 * @param activity
	 *            that supports onCreateDialog({@link #PROGRESS_DIALOG}) and returns @param
	 *            progressdialog
	 * @param progressDialog
	 *            - it should be exactly the same as onCreateDialog
	 * @return
	 */
	public void checkApplicationIsBeingInitialized(Activity activity, ProgressDialog progressDialog) {
		// start application if it was previously closed
		startApplication();
		synchronized (OsmandApplication.this) {
			if (startDialog != null) {
				try {
					SpecialPhrases.setLanguage(this, osmandSettings);
				} catch (IOException e) {
					LOG.error("I/O exception", e);
					Toast error = Toast.makeText(this, "Error while reading the special phrases. Restart OsmAnd if possible", Toast.LENGTH_LONG);
					error.show();
				}

				progressDialog.setTitle(getString(R.string.loading_data));
				progressDialog.setMessage(getString(R.string.reading_indexes));
				activity.showDialog(PROGRESS_DIALOG);
				startDialog.setDialog(progressDialog);
			} else if (startingWarnings != null) {
				showWarnings(startingWarnings, activity);
			}
		}
	}

	public boolean isApplicationInitializing() {
		return startDialog != null;
	}

	public RoutingHelper getRoutingHelper() {
		return routingHelper;
	}

	public CommandPlayer getPlayer() {
		return player;
	}

	public void showDialogInitializingCommandPlayer(final Activity uiContext) {
		showDialogInitializingCommandPlayer(uiContext, true);
	}

	public void showDialogInitializingCommandPlayer(final Activity uiContext, boolean warningNoneProvider) {
		showDialogInitializingCommandPlayer(uiContext, warningNoneProvider, null);
	}

	public void showDialogInitializingCommandPlayer(final Activity uiContext, boolean warningNoneProvider, Runnable run) {
		String voiceProvider = osmandSettings.VOICE_PROVIDER.get();
		if (voiceProvider == null || OsmandSettings.VOICE_PROVIDER_NOT_USE.equals(voiceProvider)) {
			if (warningNoneProvider && voiceProvider == null) {
				Builder builder = new AlertDialog.Builder(uiContext);
				builder.setCancelable(true);
				builder.setNegativeButton(R.string.default_buttons_cancel, null);
				builder.setPositiveButton(R.string.default_buttons_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(uiContext, SettingsActivity.class);
						intent.putExtra(SettingsActivity.INTENT_KEY_SETTINGS_SCREEN, SettingsActivity.SCREEN_NAVIGATION_SETTINGS);
						uiContext.startActivity(intent);
					}
				});
				builder.setTitle(R.string.voice_is_not_available_title);
				builder.setMessage(R.string.voice_is_not_available_msg);
				builder.show();
			}

		} else {
			if (player == null || !Algoritms.objectEquals(voiceProvider, player.getCurrentVoice())) {
				initVoiceDataInDifferentThread(uiContext, voiceProvider, run);
			}
		}

	}

	private static final String DEV_GPS_SERVICES_MARKERS = "525dd78a-c04c-45c9-aee6-642fae8ed672";

	private static final String STREET_NAME_FEATURES_AUTHORIZED_COMPANIES = DEV_GPS_SERVICES_MARKERS
			+ " 51a78b90-62a0-45b9-a0b4-3c90ae8ed672 526fd0bb-e230-42b9-8d06-0a44ae8ed672 526960e0-6f90-4069-a0d9-0401ae8ed672 500d653c-505c-4b2a-8fa1-7b68ae8ed672";

	private String getServiceStreetName(Location location) {

		Company company = Session.getCompany();

		if (company == null || !STREET_NAME_FEATURES_AUTHORIZED_COMPANIES.contains(company.getId()))
			return null;

		StreetPoint streetpoint = null;
		if (location != null && mBoundRgc)
			streetpoint = mRgcService.getStreetName(new Point(location.getLatitude(), location.getLongitude()));
		if (streetpoint == null || streetpoint.street == null)
			return null;
		else
			return streetpoint.street;
	}

	private void initVoiceDataInDifferentThread(final Activity uiContext, final String voiceProvider, final Runnable run) {
		final ProgressDialog dlg = ProgressDialog.show(uiContext, getString(R.string.loading_data), getString(R.string.voice_data_initializing));
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (player != null) {
						player.clear();
					}
					player = CommandPlayerFactory.createCommandPlayer(voiceProvider, OsmandApplication.this, uiContext);

					if (player instanceof TTSCommandStreetNamePlayerImpl) {

						// inject the street finder 
						TTSCommandStreetNamePlayerImpl streetPlayer = (TTSCommandStreetNamePlayerImpl) player;

						streetPlayer.streetFinder = new StreetFinder() {
							@Override
							public String getStreetName(Location location) {
								return getServiceStreetName(location);
							}
						};
					}

					routingHelper.getVoiceRouter().setPlayer(player);
					dlg.dismiss();
					if (run != null && uiContext != null) {
						uiContext.runOnUiThread(run);
					}
				} catch (CommandPlayerException e) {
					dlg.dismiss();
					showWarning(uiContext, e.getError());
				}
			}
		}).start();
	}

	// Rgc means ReverseGeoCoding
	ReverseGeocodingService mRgcService;
	boolean mBoundRgc = false;

	protected void startRgcService() {
		Intent intent = new Intent(this, ReverseGeocodingService.class);
		bindService(intent, mRgcConnection, Context.BIND_AUTO_CREATE);
	}

	protected void stopRgcService() {
		if (mBoundRgc) {
			unbindService(mRgcConnection);
			mBoundRgc = false;
		}
	}

	private final ServiceConnection mRgcConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			ReverseGeocodingBinder binder = (ReverseGeocodingBinder) service;
			mRgcService = binder.getService();
			mBoundRgc = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBoundRgc = false;
		}
	};

	public NavigationService getNavigationService() {
		return navigationService;
	}

	public void setNavigationService(NavigationService navigationService) {
		this.navigationService = navigationService;
	}

	public BidForFixHelper getBidForFix() {
		return bidforfix;
	}

	public synchronized void closeApplication() {
		if (applicationInitializing) {
			manager.close();
		}
		applicationInitializing = false;
	}

	public synchronized void startApplication() {
		if (applicationInitializing) {
			return;
		}
		applicationInitializing = true;
		startDialog = new ProgressDialogImplementation(this, null, false);

		startDialog.setRunnable("Initializing app", new Runnable() { //$NON-NLS-1$
					@Override
					public void run() {
						startApplicationBackground();
					}
				});
		startDialog.run();

		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

	}

	public String exportFavorites(File f) {
		GPXFile gpx = new GPXFile();
		for (FavouritePoint p : getFavorites().getFavouritePoints()) {
			if (p.isStored()) {
				WptPt pt = new WptPt();
				pt.lat = p.getLatitude();
				pt.lon = p.getLongitude();
				pt.name = p.getName() + "_" + p.getCategory();
				gpx.points.add(pt);
			}
		}
		return GPXUtilities.writeGpxFile(f, gpx, this);
	}

	private void startApplicationBackground() {
		List<String> warnings = new ArrayList<String>();
		try {
			if (!Version.isBlackberry(this)) {
				if (osmandSettings.NATIVE_RENDERING_FAILED.get()) {
					osmandSettings.NATIVE_RENDERING.set(false);
					osmandSettings.NATIVE_RENDERING_FAILED.set(false);
					warnings.add(getString(R.string.native_library_not_supported));
				} else if (osmandSettings.NATIVE_RENDERING.get()) {
					osmandSettings.NATIVE_RENDERING_FAILED.set(true);
					startDialog.startTask(getString(R.string.init_native_library), -1);
					RenderingRulesStorage storage = rendererRegistry.getCurrentSelectedRenderer();
					boolean initialized = NativeOsmandLibrary.getLibrary(storage) != null;
					osmandSettings.NATIVE_RENDERING_FAILED.set(false);
					if (!initialized) {
						LOG.info("Native library could not loaded!");
						osmandSettings.NATIVE_RENDERING.set(false);
					}
				}
			}
			warnings.addAll(manager.reloadIndexes(startDialog));
			player = null;
			if (savingTrackHelper.hasDataToSave()) {
				startDialog.startTask(getString(R.string.saving_gpx_tracks), -1);
				warnings.addAll(savingTrackHelper.saveDataToGpx());
			}
			savingTrackHelper.close();

			// restore backuped favorites to normal file
			final File appDir = getSettings().extendOsmandPath(ResourceManager.APP_DIR);
			File save = new File(appDir, FavouritesDbHelper.FILE_TO_SAVE);
			File bak = new File(appDir, FavouritesDbHelper.FILE_TO_BACKUP);
			if (bak.exists() && (!save.exists() || bak.lastModified() > save.lastModified())) {
				if (save.exists()) {
					save.delete();
				}
				bak.renameTo(save);
			}
		} finally {
			synchronized (OsmandApplication.this) {
				final ProgressDialog toDismiss;
				if (startDialog != null) {
					toDismiss = startDialog.getDialog();
				} else {
					toDismiss = null;
				}
				startDialog = null;

				if (toDismiss != null) {
					uiHandler.post(new Runnable() {
						@Override
						public void run() {
							if (toDismiss != null) {
								// TODO handling this dialog is bad, we need a better standard way
								toDismiss.dismiss();
								// toDismiss.getOwnerActivity().dismissDialog(PROGRESS_DIALOG);
							}
						}
					});
					showWarnings(warnings, toDismiss.getContext());
				} else {
					startingWarnings = warnings;
				}
			}
		}
	}

	protected void showWarnings(List<String> warnings, final Context uiContext) {
		if (warnings != null && !warnings.isEmpty()) {
			final StringBuilder b = new StringBuilder();
			boolean f = true;
			for (String w : warnings) {
				if (f) {
					f = false;
				} else {
					b.append('\n');
				}
				b.append(w);
			}
			showWarning(uiContext, b.toString());
		}
	}

	private void showWarning(final Context uiContext, final String b) {
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				AccessibleToast.makeText(uiContext, b.toString(), Toast.LENGTH_LONG).show();
			}
		});
	}

	private class DefaultExceptionHandler implements UncaughtExceptionHandler {

		private final UncaughtExceptionHandler defaultHandler;
		private final PendingIntent intent;

		public DefaultExceptionHandler() {
			defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			intent = PendingIntent.getActivity(OsmandApplication.this.getBaseContext(), 0, new Intent(OsmandApplication.this.getBaseContext(), OsmandIntents.getMainMenuActivity()), 0);
		}

		@Override
		public void uncaughtException(final Thread thread, final Throwable ex) {
			File file = osmandSettings.extendOsmandPath(EXCEPTION_PATH);
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(out);
				ex.printStackTrace(printStream);
				StringBuilder msg = new StringBuilder();
				msg.append("Version  " + Version.getFullVersion(OsmandApplication.this)). //$NON-NLS-1$ 
						append("Exception occured in thread " + thread.toString() + " : "). //$NON-NLS-1$ //$NON-NLS-2$
						append(DateFormat.format("MMMM dd, yyyy h:mm:ss", System.currentTimeMillis())).append("\n"). //$NON-NLS-1$//$NON-NLS-2$
						append(new String(out.toByteArray()));

				if (file.getParentFile().canWrite()) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
					writer.write(msg.toString());
					writer.close();
				}
				if (routingHelper.isFollowingMode()) {
					AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, intent);
					System.exit(2);
				}
				defaultHandler.uncaughtException(thread, ex);
			} catch (Exception e) {
				// swallow all exceptions
				android.util.Log.e(LogUtil.TAG, "Exception while handle other exception", e); //$NON-NLS-1$
			}

		}
	}

	public boolean accessibilityExtensions() {
		return osmandSettings.ACCESSIBILITY_EXTENSIONS.get();
	}

	public boolean accessibilityEnabled() {
		final AccessibilityMode mode = getSettings().ACCESSIBILITY_MODE.get();
		if (mode == AccessibilityMode.ON)
			return true;
		else if (mode == AccessibilityMode.OFF)
			return false;
		return ((AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE)).isEnabled();
	}

	private void launchGPSService() {
		LOG.info("Connecting to GPS Service");

		Intent locInt = new Intent(this, GPSService.class);

		if (!bindService(locInt, gpsConnection, BIND_AUTO_CREATE)) {
			LOG.error("Failed to connect to GPS service");
		}
	}

	private class GPSServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			gpsService = binder.getService();
			// Make sure the GPS service is configured with the proper profile...
			gpsService.setProfile(Config.getGpsProfile(Utils.getInstance(OsmandApplication.this).getIMEI()));

			if (Session.isVehicleSet()) {
				Vehicle vehicle = Session.getVehicle();
				if ( (vehicle != null) && (!vehicle.isSnowflakeInstalled()) ) {
					Intent intent = new Intent();
					intent.setAction(GPSService.UPDATE_IMEI_EVENT);
					intent.putExtra("Value", vehicle.getEsn_id());
					sendBroadcast(intent);
				}
			}

			LOG.info("GPS Service is connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			LOG.error("GPS Service is disconnected");
			launchGPSService();
		}
	}

	public void startWorksheetActivity(String worksheetid, String companyid, String contractid) {
		Intent worksheetIntent = new Intent();
		worksheetIntent.setClass(this, WorksheetActivity.class);
		worksheetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (worksheetid != null) {
			worksheetIntent.putExtra("worksheetid", worksheetid);
		} else {
			Worksheets worksheet = new Worksheets();
			worksheet.setCompanyId(companyid);
			worksheet.setContractId(contractid);
			final WorksheetsDao wDao = new WorksheetsDao();
			wDao.insertOrReplace(worksheet);
			worksheetIntent.putExtra("worksheetid", worksheet.getId());
		}
		startActivity(worksheetIntent);
	}

}
