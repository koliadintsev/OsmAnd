package com.operasoft.snowboard.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.MapActivity;

import org.acra.ACRA;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.database.ImeiCompany;
import com.operasoft.snowboard.database.ImeiCompanyDao;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.RouteSequence;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.Site;
import com.operasoft.snowboard.database.SiteDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class Session {

	/**
	 * This enum defines the type of session currently active on Snowboard
	 */
	public enum SessionType {
		VEHICLE_SESSION, // The user is logged against a Vehicle
		SITE_SESSION, // The user is logged against a Site
	}

	public static Context context;
	private static SharedPreferences prefs;
	private static SessionType type = SessionType.VEHICLE_SESSION;

	public static void init(Context context, SharedPreferences prefs) {
		Session.context = context;
		Session.prefs = prefs;
	}

	public static boolean isInit() {
		return (context != null) && (prefs != null);
	}

	public static void setType(SessionType type) {
		Session.type = type;
	}

	public static SessionType getType() {
		return type;
	}

	/**
	 * The company associated with this application
	 */
	private static Company company = null;

	public static Company getCompany() {
		return getCompany(false);
	}

	public static Company getCompany(boolean force) {
		if (force || company == null || company.getId() == null) {
			try {
				CompanyDao dao = new CompanyDao();
				company = dao.getFirst();
				if (company != null) {
					ACRA.getErrorReporter().putCustomData("companyId", company.getId());
					ACRA.getErrorReporter().putCustomData("company", company.getCompanyName());
				} else {
					ACRA.getErrorReporter().putCustomData("companyId", "null");
					ACRA.getErrorReporter().putCustomData("company", "null");
				}
				updatePreferences();
			} catch (Exception e) {
				e.printStackTrace();
				company = new Company();
			}
		}

		return company;
	}

	public static String getCompanyId() {
		String value = "";
		if (company == null) {
			getCompany();
		}

		if (company != null) {
			if (company.getId() == null) {
				getCompany();
			}
			if (company != null) {
				value = company.getId();
			}
		}

		return value;
	}

	public static boolean isSimplicity() {
		boolean result = false;
		
		Company company = getCompany();
		if  ( (company != null) && company.isSimplicity() ) {
			result = true;
		}
		
		return result;
	}
	
	public static String getDistanceUnit() {
		String value = Company.KILOMETERS;
		if (company == null) {
			getCompany();
		}

		if (company != null) {
			value = company.getDistanceUnit();
		}

		return value;
	}

	public static String getVolumeUnit() {
		String value = Company.LITRES;
		if (company == null) {
			getCompany();
		}

		if (company != null) {
			value = company.getVolumeUnit();
		}

		return value;
	}

	public static String getTemperatureUnit() {
		String value = Company.CELSIUS;
		if (company == null) {
			getCompany();
		}

		if (company != null) {
			value = company.getTemperatureUnit();
		}

		return value;
	}

	public static String getSpeedUnit() {
		String value = Company.KILOMETERS_PER_HOUR;
		if (company == null) {
			getCompany();
		}

		if (company != null) {
			value = company.getVolumeUnit();
		}

		return value;
	}

	/**
	 * The driver currently logged in the application
	 */
	private static User driver = null;

	public static String getDriverFullname() {
		String fullname = "";

		try {
			if (Session.getDriver() != null && Session.getDriver().getFullName() != null)
				fullname = Session.getDriver().getFullName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fullname;
	}

	public static User getDriver() {
		if ((driver == null) && (prefs != null)) {
			String pin = prefs.getString(Config.USER_PIN_KEY, null);
			if (pin != null) {
				UsersDao dao = new UsersDao();
				setDriver(dao.getByPin(pin));
			}
		}
		return driver;
	}

	public static void setDriver(User driver) {
		Session.driver = driver;
		if (driver != null) {
			ACRA.getErrorReporter().putCustomData("driverId", driver.getId());
		} else {
			ACRA.getErrorReporter().putCustomData("driverId", "null");
		}
	}

	public static boolean isSuperDriver() {
		if (driver != null) {
			return driver.isSuperDriver();
		}

		return false;
	}

	public static String getUserPin() {
		if (driver != null) {
			return driver.getPin();
		}

		if (prefs != null) {
			return prefs.getString(Config.USER_PIN_KEY, null);
		}
		return null;
	}

	public static String getLastUserPin() {
		if (driver != null) {
			return driver.getPin();
		}

		if (prefs != null) {
			return prefs.getString(Config.LAST_USER_PIN_KEY, "");
		}
		return "";
	}

	/**
	 * The vehicle currently linked to this application
	 */
	private static Vehicle vehicle;

	public static Vehicle getVehicle() {
		if ((vehicle == null) && (prefs != null)) {
			String id = prefs.getString(Config.VEHICLE_ID_KEY, null);
			if (id != null) {
				VehiclesDao dao = new VehiclesDao();
				setVehicle(dao.getById(id));
			}
		}
		return vehicle;
	}

	public static void setVehicle(Vehicle vehicle) {
		Session.vehicle = vehicle;
		if (vehicle != null) {
			ACRA.getErrorReporter().putCustomData("vehicleId", vehicle.getId());
		} else {
			ACRA.getErrorReporter().putCustomData("vehicleId", "null");
		}
	}

	public static boolean isVehicleSet() {
		return Session.vehicle != null;
	}

	/**
	 * The vehicle currently linked to this application
	 */
	private static Site site;

	public static Site getSite() {
		if ((site == null) && (prefs != null)) {
			String id = prefs.getString(Config.SITE_ID_KEY, null);
			if (id != null) {
				SiteDao dao = new SiteDao();
				setSite(dao.getById(id));
			}
		}
		return site;
	}

	public static void setSite(Site site) {
		Session.site = site;
		if (site != null) {
			ACRA.getErrorReporter().putCustomData("siteId", site.getId());
		} else {
			ACRA.getErrorReporter().putCustomData("siteId", "null");
		}
	}

	/**
	 * The IMEI Company associated with this tablet
	 */
	private static ImeiCompany imeiCompany;

	public static ImeiCompany getImeiCompany() {
		if (imeiCompany == null) {
			ImeiCompanyDao dao = new ImeiCompanyDao();
			Utils utils = new Utils(context);
			String imeiNo = utils.getIMEI();
			imeiCompany = dao.getByImei(imeiNo);
			if (imeiCompany != null) {
				ACRA.getErrorReporter().putCustomData("deviceId", imeiCompany.getId());
				ACRA.getErrorReporter().putCustomData("imei", imeiCompany.getImeiNo());
			} else {
				ACRA.getErrorReporter().putCustomData("deviceId", "null");
				ACRA.getErrorReporter().putCustomData("imei", "null");
			}
		}

		return imeiCompany;
	}

	public static boolean viewVehicles = false;
	public static boolean viewAllSAs = false;

	public static boolean disableOnRouteAutoPopup = false;
	public static Route route;
	public static ServiceActivity serviceActivity;
	public static List<RouteSequence> routeSequences;
	public static boolean FirstLogin;
	public static Location clocation;
	public static int userZoom = 18;
	public static List<ServiceLocation> serLoc;
	public static Set<String> serLocCrossed = new HashSet<String>();
	public static MapActivity MapAct;
	public static int compassState;
	public static boolean moving;
	public static boolean inStormMode = false;
	public static boolean inlookAheadMode = false;
	public static int navRoutePointsCount = 0;
	public static boolean inResetMode = false;
	public static String networkState = "disconnected";

	public static void close() {
		setDriver(null);
		setVehicle(null);
		route = null;
		routeSequences = null;
		FirstLogin = false;
		inStormMode = false;
		viewVehicles = false;
		if (viewAllSAs) {
			viewAllSAs = false;
			PointOfInterestManager.getInstance().detachAllOthersServiceActivities();
		}
	}

	public static void setCompany(Company company) {
		Session.company = company;
		updatePreferences();
	}

	private static void updatePreferences() {
		if (company != null) {
			((OsmandApplication) context).onUpdateCompany(company);
		}
	}

	/**
	 * The trailer currently linked to this application
	 */
	private static Vehicle equipmentTrailer;

	public static Vehicle getTrailer() {
		return equipmentTrailer;
	}

	public static void setTrailer(Vehicle equipmnt) {
		Session.equipmentTrailer = equipmnt;
	}

	public static String getCurrentSeason() {
		if (company == null) {
			company = getCompany();
			if (company == null) {
				return null;
			}
		}

		return company.getDefaultSeasonId();
	}

	public static int routeSlPosition = 0;
	public static boolean logoutFromServer = false;

	public static int getRouteSlPosition() {
		return routeSlPosition;
	}

	public static void setRouteSlPosition(int routeSlPosition) {
		Session.routeSlPosition = routeSlPosition;
	}

	public static boolean isViewDefautSeason() {
		if (prefs != null)
			return prefs.getBoolean(Config.VIEW_DEFAULT_SEASON, false);
		return false;
	}

	public static void setViewDefautSeason(boolean viewDefautHandler) {
		prefs.edit().putBoolean(Config.VIEW_DEFAULT_SEASON, viewDefautHandler).commit();
	}

	public static boolean getDisableOnRouteAutoPopup() {
		boolean disable = prefs.getBoolean(Config.DISABLE_ONROUTE_AUTOPOPUP, false);
		return disable;
	}

	public static void changeDisableOnRouteAutoPopup() {
		boolean disable = prefs.getBoolean(Config.DISABLE_ONROUTE_AUTOPOPUP, false);
		prefs.edit().putBoolean(Config.DISABLE_ONROUTE_AUTOPOPUP, !disable).commit();

	}

	public static void changeDisableStaffList() {
		boolean disable = prefs.getBoolean(Config.STAFF_LIST, false);
		prefs.edit().putBoolean(Config.STAFF_LIST, !disable).commit();

	}

	public static boolean isStaffListEnabled() {
		boolean enabled = prefs.getBoolean(Config.STAFF_LIST, false);
		return enabled;
	}

}
