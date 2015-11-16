package com.operasoft.snowboard.util;

import org.acra.ACRAConfiguration;

import android.util.Log;

import com.lazydroid.autoupdateapk.AutoUpdateApk;
import com.operasoft.android.gps.ConfigurationProfile;
import com.operasoft.android.gps.ConfigurationProfile.DefaultProfile;
import com.operasoft.snowboard.database.GpsConfig;
import com.operasoft.snowboard.database.GpsConfigDao;
import com.operasoft.snowboard.database.ImeiCompany;
import com.operasoft.snowboard.database.ImeiCompanyDao;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class Config extends com.operasoft.android.config.Config {

	//--------------------------------------------------------------------------------
	// CONFIGURATION KEYS
	//--------------------------------------------------------------------------------
	// Constants used to defines configuration attributes defined in the 
	// SharedPreferences instance linked to this application
	final public static String LAST_VEHICLE_KEY = "last_vehicle"; // Last vehicle selected by the user
	final public static String VEHICLE_ID_KEY = "vehicleID"; // Vehicle currently logged in
	final public static String SITE_ID_KEY = "siteID"; // Site currently logged in
	final public static String USER_PIN_KEY = "user_pin"; // Driver currently logged in
	final public static String LAST_USER_PIN_KEY = "last_user_pin"; // Last user who successfully logged in. Used to complete synchronization on restart
	final public static String SYNC_SERVICE_KEY = "sync_service";
	final public static String IS_RESET_KEY = "isReset";
	final public static String SERVER_URL_KEY = "serverUrl";
	final public static String PIP_POSITION_KEY = "outside";
	final public static String TIME_ROUTE_KEY = "select_route_time";
	final public static String IMEI_NUM_KEY = "imei_num";
	final public static String TRAILER_ID_KEY = "trailer_id";
	final public static String VIEW_DEFAULT_SEASON = "view_defualt_season";
	final public static String RESET_PASSWORD = "987654";
	final public static String DISABLE_ONROUTE_AUTOPOPUP = "disableOnRouteAutoPopup";
	final public static String STAFF_LIST = "staffList";

	//--------------------------------------------------------------------------------
	// BUILD TYPE ENUM
	//--------------------------------------------------------------------------------
	// Constants used to configure the how application should behave based on the build
	// type.
	//
	// 		- DESIGNER_BUILD: Use this configuration to build developer's APK that is 
	//            targeted for the development environment for early testing purposes.
	//            To build this kind of APK:
	//					- KEY used to sign it: N/A 
	//					- Manifest "Debuggable" attribute: TRUE
	//					- Update server: N/A
	//                  - Tablet used for tests: TEST tablet
	//
	//      - STAGIN_BUILD: use this configuration to build a staging APK
	//            for staging tests.
	//					- KEY used to sign it: android-dev 
	//					- Manifest "Debuggable" attribute: TRUE
	//					- Update server: staging.snowman.operasoft.ca/androidapps/
	//                  - Tablet used for tests: BETA tablet
	//
	//      - BETA_BUILD: use this configuration to build a stable APK
	//            for final acceptance tests.
	//					- KEY used to sign it: android-dev 
	//					- Manifest "Debuggable" attribute: TRUE
	//					- Update server: dev.snowman.operasoft.ca/androidapps/
	//                  - Tablet used for tests: BETA tablet
	//
	//      - FINAL_BUILD: use this configuration to build an APK that is destined
	//            to customers.
	//					- KEY used to sign it: operasoft 
	//					- Manifest "Debuggable" attribute: FALSE
	//					- Update server: snowman.operasoft.ca/androidapps/
	//                  - Tablet used for tests: PROD tablet
	//--------------------------------------------------------------------------------
	public enum BuildType {
		LOCAL_BUILD, DESIGNER_BUILD, STAGING_BUILD, BETA_BUILD, FINAL_BUILD
	}

	//--------------------------------------------------------------------------------
	// Set this value to the proper environment you want to build the environment for
	//--------------------------------------------------------------------------------
	public static final BuildType CURRENT_CONFIG = BuildType.FINAL_BUILD;

	//--------------------------------------------------------------------------------

	/**
	 * This method must be invoked to configure the application based on the proper type
	 * of build.
	 */
	synchronized public static void init() {

		//TODO do a read database if there is a config associated
		// if true return it 
		// else return the default

		if (initialized) {
			// Already initialized...
			return;
		}

		switch (CURRENT_CONFIG) {
		case FINAL_BUILD:
			// If you want to force a DB download on the next update, 
			// simply change the DB name
			DB_NAME = "snowboard-" + DB_VERSION;
			BASE_URL = "http://snowman.operasoft.ca";
			AUTO_UPDATE_ENABLED = true;
			COLLECTOR_PORT = 32191;
			ACRA_FORM_URL = "http://crash.snowman.operasoft.ca/submit.php";
			break;
		case BETA_BUILD:
			// Auto-update: ENABLED
			BASE_URL = "http://beta.snowman.operasoft.ca";
			DB_NAME = "snowboard-beta-" + DB_VERSION;
			AUTO_UPDATE_ENABLED = true;
			COLLECTOR_PORT = 35191;
			ACRA_FORM_URL = "http://crash.beta.snowman.operasoft.ca/submit.php";
			break;
		case STAGING_BUILD:
			// Auto-update: ENABLED
			BASE_URL = "http://staging.snowman.operasoft.ca";
			DB_NAME = "snowboard-stg" + DB_VERSION;
			AUTO_UPDATE_ENABLED = true;
			COLLECTOR_PORT = 34191;
			ACRA_FORM_URL = "http://crash.staging.snowman.operasoft.ca/submit.php";
			break;
		case LOCAL_BUILD:
			BASE_URL = "http://192.168.25.133:8080";
			DB_NAME = "snowboard-beta-" + DB_VERSION;
			AUTO_UPDATE_ENABLED = false;
			COLLECTOR_PORT = 34191;
			ACRA_FORM_URL = "http://crash.beta.snowman.operasoft.ca/submit.php";
			break;
		default:
			// Auto-update: DISABLED
			BASE_URL = "http://dev.snowman.operasoft.ca";
			DB_NAME = "snowboard-dev-" + DB_VERSION;
			AUTO_UPDATE_ENABLED = false;
			COLLECTOR_PORT = 33191;
			ACRA_FORM_URL = "http://crash.dev.snowman.operasoft.ca/submit.php";
		}
		NetworkUtilities.setBASE_URL(BASE_URL + "/api/");
		AutoUpdateApk.setApiUrl(BASE_URL + "/androidapps/check");
		initialized = true;

		gpsProfile.setCollector(COLLECTOR_ADDRESS);
		gpsProfile.setPort(COLLECTOR_PORT);
		gpsProfile.setMetricSystem(true);

		Log.w("Config", "CURRENT_CONFIG: " + CURRENT_CONFIG);
		Log.w("Config", "DB_NAME: " + DB_NAME);
		Log.w("Config", "BASE_URL: " + BASE_URL);
		Log.w("Config", "AUTO_UPDATE_ENABLED: " + AUTO_UPDATE_ENABLED);
		Log.w("Config", "COLLECTOR - Address: " + COLLECTOR_ADDRESS + ", Port: " + COLLECTOR_PORT);
	}

	/**
	 * Whether the init method has been invoked yet.
	 */
	static private boolean initialized = false;

	/**
	 * Whether or not the application should check for an update.
	 */
	private static boolean AUTO_UPDATE_ENABLED = false;

	static public boolean isAutoUpdateEnabled() {
		Config.init();
		return AUTO_UPDATE_ENABLED;
	}

	/**
	 * The base URL to which all server requests must be sent.
	 */
	private static String BASE_URL = "http://stg.snowman.operasoft.ca";

	static public String getBaseUrl() {
		Config.init();
		return BASE_URL;
	}

	/**
	 * The IP address/host name of the collector to use.
	 */
	private static String COLLECTOR_ADDRESS = "collector.operasoft.ca";

	static public String getCollectorAddress() {
		Config.init();
		return COLLECTOR_ADDRESS;
	}

	/**
	 * The port to connect to on the collector server
	 */
	private static int COLLECTOR_PORT = 33191;

	static public int getCollectorPort() {
		Config.init();
		return COLLECTOR_PORT;
	}

	/**
	 * The database version associated with this release. Increase this value
	 * to force a database reset.
	 */
	private static String DB_VERSION = "v2.7.4";

	/**
	 * The database to use for this release.
	 */
	private static String DB_NAME = "snowboard";

	static public String getDbName() {
		Config.init();
		return DB_NAME;
	}

	private static int version = -1;

	static public int getVersion() {
		return version;
	}

	static public void setVersion(int versionCode) {
		version = versionCode;
	}

	static public void updateCrashConfig(ACRAConfiguration defaultConfig) {
		Config.init();
		defaultConfig.setFormUri(ACRA_FORM_URL);
	}

	public static String APP_NAME = "Snowman";
	public static int CLIENT_LISTENING_PORT = 5555;
	public static int ACK_RESPONSE_WAITING_TIME = 60 * 1000; // 1 min default
	public static int SEQUENCE_NO_START = 100;

	public static String VIMEI = "";
	public static String IMEI = "";
	//TODO 00 confirm compass map state
	//public static int compassState = 0;
	public static int compassState = 3;
	public static boolean MARKER_INSTALLATION = false;

	public static void setVIMEI(String vIMEI) {
		VIMEI = vIMEI;
	}

	public static void setIMEI(String imei) {
		IMEI = imei;
	}

	static private ConfigurationProfile gpsProfile = new ConfigurationProfile(DefaultProfile.SNOW_DEFAULT_PROFILE);

	static private ConfigurationProfile getGpsProfile() {
		Config.init();
		return gpsProfile;
	}

	static public ConfigurationProfile getGpsProfile(GpsConfig gpsConf) {
		Config.init();
		if (gpsConf != null)
			gpsConf.fetch(gpsProfile);
		return gpsProfile;
	}

	static public ConfigurationProfile getGpsProfile(String imei) {
		GpsConfig gpsConf = null;
		if (imei != null) {
			ImeiCompany imeiComp = (new ImeiCompanyDao()).getByImei(imei);
			if (imeiComp != null && imeiComp.getGpsConfigId() != null)
				gpsConf = (new GpsConfigDao()).getById(imeiComp.getGpsConfigId());
		}
		return getGpsProfile(gpsConf);
	}
}
