package com.operasoft.snowboard.dbsync;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.operasoft.snowboard.database.ActivityDao;
import com.operasoft.snowboard.database.CalloutTypeDao;
import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.database.ContactDao;
import com.operasoft.snowboard.database.ContractServicesDao;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.DamageTypeDao;
import com.operasoft.snowboard.database.EndRoutesDao;
import com.operasoft.snowboard.database.GpsConfig;
import com.operasoft.snowboard.database.GpsConfigDao;
import com.operasoft.snowboard.database.ImeiCompany;
import com.operasoft.snowboard.database.ImeiCompanyDao;
import com.operasoft.snowboard.database.RouteSequenceDao;
import com.operasoft.snowboard.database.TabletConfigsDao;
import com.operasoft.snowboard.database.VehicleInspectionItemDao;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.database.WorksheetsDao;
import com.operasoft.snowboard.dbsync.onetime.AbstractOneTimeSync;
import com.operasoft.snowboard.dbsync.periodic.AbstractPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.AnonymousPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.CompanyPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.DefaultPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.GpsConfigPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.MarkerInstallationPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.RoutePeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.ServiceActivityPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.ServiceLocationPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.UserPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.WorkOrderPeriodicSync;
import com.operasoft.snowboard.dbsync.periodic.WorksheetPeriodicSync;
import com.operasoft.snowboard.dbsync.push.AbstractPushSync;
import com.operasoft.snowboard.dbsync.push.CalloutPushSync;
import com.operasoft.snowboard.dbsync.push.DamagePushSync;
import com.operasoft.snowboard.dbsync.push.DeficiencyPushSync;
import com.operasoft.snowboard.dbsync.push.InspectionJournalPushSync;
import com.operasoft.snowboard.dbsync.push.LoginSessionPushSync;
import com.operasoft.snowboard.dbsync.push.MarkerInstallationPushSync;
import com.operasoft.snowboard.dbsync.push.PunchPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceActivityDetailsPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceActivityPushSync;
import com.operasoft.snowboard.dbsync.push.UploadsPushSync;
import com.operasoft.snowboard.dbsync.push.VehicleRefuelLogPushSync;
import com.operasoft.snowboard.dbsync.push.WorksheetPushSync;
import com.operasoft.snowboard.dbsync.push.WorkOrderPushSync;
import com.operasoft.snowboard.util.Session;

/**
 * This class is meant to handle all database synchronization activities with the Snowman database.
 * It implements the singleton pattern to make sure we only have one instance running for the entire
 * application.
 * 
 * @author Christian
 * 
 */

public class DbSyncManager {

	/**
	 * The timer delay (in seconds) to use between 2 sync calls
	 */
	static final public int SLEEP_DELAY = 60;
	/**
	 * The minimum delay to enforce between 2 sync requests in normal mode
	 */
	static final private int NORMAL_SYNC_DELAY = 120;
	/**
	 * The minimum delay to enforce between 2 priority sync requests in storm mode
	 */
	static final private int STORM_PRIORITY_SYNC_DELAY = 60;
	/**
	 * The minimum delay to enforce between 2 sync requests in storm mode
	 */
	static final private int STORM_SYNC_DELAY = 120;
	/**
	 * The minimum delay to enforce between 2 sync requests for vehicle last reports
	 */
	static final private int VEHICLE_REPORT_SYNC_DELAY = 60;

	/**
	 * The singleton instance
	 */
	static private DbSyncManager instance_s;

	private boolean initialized = false;
	private Context context = null;


	/**
	 * This field stores the list of all periodic synchronization processes that need to be done prior to a user login. Synchronization will
	 * be done in the order in which elements are located in this array
	 */
	private final List<AbstractPeriodicSync> preLoginSyncList = new Vector<AbstractPeriodicSync>();

	/**
	 * This field stores the list of all periodic synchronization processes that need to be done. Synchronization will be done in the order
	 * in which elements are located in this array
	 */
	private final List<AbstractPeriodicSync> periodicSyncList = new Vector<AbstractPeriodicSync>();
	private final List<AbstractPeriodicSync> periodicStormPrioritySyncList = new Vector<AbstractPeriodicSync>();
	private final List<AbstractPeriodicSync> periodicStormSyncList = new Vector<AbstractPeriodicSync>();
	private final List<AbstractPeriodicSync> periodicSuperDriverSyncList = new Vector<AbstractPeriodicSync>();

	/**
	 * This field stores one-time synchronization requests that need to be performed once in a while (e.g. refresh a route) to update the database.
	 * Once this synchronization request has been performed, it is removed from the list. 
	 */
	private final List<AbstractOneTimeSync> oneTimeSyncList = new Vector<AbstractOneTimeSync>();

	/**
	 * This field sores the list of all push synchronization processes that need to run at periodic intervals.
	 */
	private final List<AbstractPushSync> pushSyncList = new Vector<AbstractPushSync>();

	private int timerDelay = 0;

	private Date lastPeriodicSync;
	private Date lastPushSync;
	private Date lastStormPrioritySync;
	private Date lastOneTimeSync;

	/**
	 * Keep track of the failures observed.
	 */
	private final Map<String, DbSyncFailure> pushSyncFailures = new HashMap<String, DbSyncFailure>();
	private final Map<String, DbSyncFailure> periodicSyncFailures = new HashMap<String, DbSyncFailure>();
	private final Map<String, DbSyncFailure> oneTimeSyncFailures = new HashMap<String, DbSyncFailure>();

	/**
	 * Singleton pattern. This makes sure we have only one instance of this class instantiated in the entire application. >>>>>>> .r1695
	 */
	synchronized static public DbSyncManager getInstance() {
		if (instance_s == null) {
			instance_s = new DbSyncManager();
		}
		return instance_s;
	}

	private DbSyncManager() {

	}

	private void init(Context ctx) {
		if (!initialized) {
			deviceImei = com.operasoft.android.util.Utils.getInstance(ctx).getIMEI();
			initPreLoginSyncList();
			initPeriodicSyncList();
			initPushSyncList();
			initPeriodicStormSyncList();
			initPeriodicStormPrioritySyncList();
			initialized = true;
		}
		
		context = ctx;
	}
	
	public String getStatus() {
		StringBuilder text = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd KK:mm:ss");
		Date date = getLastPeriodicSync();
		if (date != null) {
			text.append("Periodic: " + dateFormat.format(date) + "\n");
		}
		date = getLastStormPrioritySync();
		if (date != null) {
			text.append("Storm: " + dateFormat.format(date) + "\n");
		}
		date = getLastPushSync();
		if (date != null) {
			text.append("Push: " + dateFormat.format(date) + "\n");
		}
		date = getLastOneTimeSync();
		if (date != null) {
			text.append("One-Time: " + dateFormat.format(date) + "\n");
		}
		if (!periodicSyncFailures.isEmpty()) {
			int count = 0;
			Date last = null;
			for (DbSyncFailure failure : periodicSyncFailures.values()) {
				count += failure.getCount();
				if ((last == null) || (failure.getLastFailure().after(last))) {
					last = failure.getLastFailure();
				}
			}
			text.append("Periodic: " + count + " failures on " + periodicSyncFailures.size() + " models. Last: " + dateFormat.format(last) + "\n");
		}
		if (!pushSyncFailures.isEmpty()) {
			int count = 0;
			Date last = null;
			for (DbSyncFailure failure : pushSyncFailures.values()) {
				count += failure.getCount();
				if ((last == null) || (failure.getLastFailure().after(last))) {
					last = failure.getLastFailure();
				}
			}
			text.append("Push: " + count + " failures on " + pushSyncFailures.size() + " models. Last: " + dateFormat.format(last) + "\n");
		}
		if (!oneTimeSyncFailures.isEmpty()) {
			int count = 0;
			Date last = null;
			for (DbSyncFailure failure : oneTimeSyncFailures.values()) {
				count += failure.getCount();
				if ((last == null) || (failure.getLastFailure().after(last))) {
					last = failure.getLastFailure();
				}
			}
			text.append("One-Time: " + count + " failures on " + oneTimeSyncFailures.size() + " models. Last: " + dateFormat.format(last) + "\n");
		}

		return text.toString();
	}

	public Date getLastPeriodicSync() {
		return lastPeriodicSync;
	}

	public Date getLastPushSync() {
		return lastPushSync;
	}

	public Date getLastStormPrioritySync() {
		return lastStormPrioritySync;
	}

	public Date getLastOneTimeSync() {
		return lastOneTimeSync;
	}

	/**
	 * 
	 * GpsConfigSync has to be runned before ImeiCompany Sync
	 * and both have to notify in case of change
	 * @param syncList
	 * 
	 */
	private void addImeiCompanySync(List<AbstractPeriodicSync> syncList) {
		
		syncList.add(new GpsConfigPeriodicSync() {
			@Override
			public void onUpdate(GpsConfig gpsConfig) {
				if (dbDevice != null && dbDevice.getGpsConfigId() != null && dbDevice.getGpsConfigId().equals(gpsConfig.getId()))
					uptValuesGpsConfig = gpsConfig;
			}
		});
		syncList.add(new AnonymousPeriodicSync("ImeiCompany", new ImeiCompanyDao() {
			@Override
			public void insertOrReplace(ImeiCompany device) {
				if (deviceImei != null && device.getImeiNo() != null && device.getImeiNo().equals(deviceImei)) {
					if (dbDevice != null && !dbDevice.equalsGpsConfig(device))
						newLinkGpsConfigId = device.getGpsConfigId();
				}
				super.insertOrReplace(device);
			}
		}));

	}

	public GpsConfigListener gpsConfListener = null;

	public interface GpsConfigListener {
		void onChange(GpsConfig newGpsConfig);
	}

	private void onPreSync() {

		dbDevice = (new ImeiCompanyDao()).getByImei(deviceImei);
		uptValuesGpsConfig = null;
		newLinkGpsConfigId = null;
	}

	private void onPostSync() {

		GpsConfig newGpsConfig = null;
		// if both changes the new link is considered 
		if (newLinkGpsConfigId != null) {
			newGpsConfig = (new GpsConfigDao()).getById(newLinkGpsConfigId);
		} else if (uptValuesGpsConfig != null) {
			newGpsConfig = uptValuesGpsConfig;
		}
		uptValuesGpsConfig = null;
		newLinkGpsConfigId = null;
		dbDevice = null;
		if (newGpsConfig != null && gpsConfListener != null)
			gpsConfListener.onChange(newGpsConfig);
	}

	// keep a different variable to imei because it doesnt change
	private String deviceImei = null;
	// ImeiCompany can receive updates (attributes) so we need to read it at each sync
	private ImeiCompany dbDevice;
	// Same reference  & updates attribute values in same Object
	// before :ImeiCompany -> GpsConfig#123
	// after : ImeiCompany -> GpsConfig#123(*) 
	private GpsConfig uptValuesGpsConfig = null;
	// New reference to GpsConfig 
	// before :ImeiCompany -> GpsConfig#123
	// after : ImeiCompany -> GpsConfig#456 
	private String newLinkGpsConfigId = null;

	private void initPreLoginSyncList() {

		// Before a user is logged in, we must be able to always retrieve
		// the latest set of users and vehicles available
		preLoginSyncList.add(new UserPeriodicSync());
		preLoginSyncList.add(new AnonymousPeriodicSync("Vehicle", new VehiclesDao()));

		addImeiCompanySync(preLoginSyncList);

	}

	private void initPeriodicSyncList() {

		// Start by generic (i.e. not company-related) tables
		// periodicSyncList.add(new DefaultPeriodicSync("AndroidConfig", new GpsConfigDao(), false));
		// Then, proceed with the initial set of tables that do not require
		// user authentication
		periodicSyncList.add(new UserPeriodicSync());
		periodicSyncList.add(new AnonymousPeriodicSync("Vehicle", new VehiclesDao()));

		addImeiCompanySync(periodicSyncList);

		// Then, proceed with company-specific tables
		periodicSyncList.add(new CompanyPeriodicSync());
		//		periodicSyncList.add(new RouteSelectionPeriodicSync());
		periodicSyncList.add(new DefaultPeriodicSync("Contact", new ContactDao()));
		periodicSyncList.add(new DefaultPeriodicSync("Contract", new ContractsDao()));
		periodicSyncList.add(new ContractPeriodicSync());
		periodicSyncList.add(new ServiceLocationPeriodicSync()); // ServiceLocation model
		periodicSyncList.add(new MarkerInstallationPeriodicSync());
		periodicSyncList.add(new RoutePeriodicSync()); // Route model
		periodicSyncList.add(new DefaultPeriodicSync("RouteSequence", new RouteSequenceDao()));
		periodicSyncList.add(new DefaultPeriodicSync("Activity", new ActivityDao()));
		periodicSyncList.add(new ServiceActivityPeriodicSync()); // ServiceActivity model
		periodicSyncList.add(new DefaultPeriodicSync("VehicleInspectionItem", new VehicleInspectionItemDao()));
		periodicSyncList.add(new DefaultPeriodicSync("CalloutType", new CalloutTypeDao()));
		periodicSyncList.add(new DefaultPeriodicSync("DamageType", new DamageTypeDao()));
		periodicSyncList.add(new DefaultPeriodicSync("EndRoute", new EndRoutesDao()));
		periodicSyncList.add(new DefaultPeriodicSync("TabletConfig", new TabletConfigsDao()));
		periodicSyncList.add(new WorksheetPeriodicSync());
		periodicSyncList.add(new WorkOrderPeriodicSync());

		/*		periodicSyncList.add(new GeofencePeriodicSync());
				periodicSyncList.add(new DefaultPeriodicSync("Site", new SiteDao()));
				periodicSyncList.add(new DefaultPeriodicSync("TransportSupplier", new TransportSupplierDao()));
				periodicSyncList.add(new DefaultPeriodicSync("TransportVehicle", new TransportVehicleDao()));
				periodicSyncList.add(new DefaultPeriodicSync("TransportActivity", new TransportActivityDao()));
		*/
		// Initialize the list of tables that are need by "super users"
		//		periodicSuperDriverSyncList.add(new DefaultPeriodicSync("VehicleLastReport", new VehicleLastReportDao()));

	}

	class ContractPeriodicSync extends DefaultPeriodicSync {

		HashSet<String> contractids = new HashSet<String>();

		ContractPeriodicSync() {
			super("ContractService", new ContractServicesDao());
		}

		@Override
		protected ArrayList<NameValuePair> buildRequestParams() {
			contractids.clear();
			return super.buildRequestParams();
		}

		private void detectContractIds(JSONArray jsonArray) throws JSONException {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonContractService = jsonArray.getJSONObject(i).getJSONObject("SbContractService");
				contractids.add(jsonContractService.getString("contract_id"));
			}
		}

		private void clearContractLines() {
			if (this.dao instanceof ContractServicesDao) {
				ContractServicesDao contractSvcDao = (ContractServicesDao) this.dao;
				for (String contractid : contractids)
					contractSvcDao.clearAllContractLines(contractid);
			}

		}

		@Override
		protected void processServerResponse(JSONArray jsArray) throws Exception {

			try {
				detectContractIds(jsArray);
				clearContractLines();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			super.processServerResponse(jsArray);
		}
	}

	private void initPeriodicStormPrioritySyncList() {
		// While operating in snow storm mode, we must sync SA and MI at a faster pace in order to let
		// drivers see their workload.
		// In order to do so, we must also synchronize SL and contracts to make sure we have all the information at
		// hand to complete the work.
		periodicStormPrioritySyncList.add(new DefaultPeriodicSync("Contract", new ContractsDao()));
		periodicStormPrioritySyncList.add(new ContractPeriodicSync());
		periodicStormPrioritySyncList.add(new ServiceLocationPeriodicSync()); // ServiceLocation model
		periodicStormPrioritySyncList.add(new MarkerInstallationPeriodicSync());
		periodicStormPrioritySyncList.add(new DefaultPeriodicSync("Activity", new ActivityDao()));
		periodicStormPrioritySyncList.add(new ServiceActivityPeriodicSync()); // ServiceActivity model
		//		periodicStormPrioritySyncList.add(new VehicleLastReportPeriodicSync()); // ServiceLocation model
	}

	private void initPeriodicStormSyncList() {
		//		periodicStormSyncList.add(new RouteSelectionPeriodicSync()); 
		periodicStormSyncList.add(new UserPeriodicSync());
		periodicStormSyncList.add(new AnonymousPeriodicSync("Vehicle", new VehiclesDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("Company", new CompanyDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("ImeiCompany", new ImeiCompanyDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("Contact", new ContactDao()));
		periodicStormSyncList.add(new RoutePeriodicSync()); // Route model
		periodicStormSyncList.add(new DefaultPeriodicSync("RouteSequence", new RouteSequenceDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("VehicleInspectionItem", new VehicleInspectionItemDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("CalloutType", new CalloutTypeDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("DamageType", new DamageTypeDao()));
		periodicStormSyncList.add(new DefaultPeriodicSync("TabletConfig", new TabletConfigsDao()));
		periodicStormSyncList.add(new WorksheetPeriodicSync());
		periodicStormSyncList.add(new WorkOrderPeriodicSync());

		/*		periodicStormSyncList.add(new GeofencePeriodicSync());
				periodicStormSyncList.add(new DefaultPeriodicSync("Site", new SiteDao()));
				periodicStormSyncList.add(new DefaultPeriodicSync("TransportSupplier", new TransportSupplierDao()));
				periodicStormSyncList.add(new DefaultPeriodicSync("TransportVehicle", new TransportVehicleDao()));
				periodicStormSyncList.add(new DefaultPeriodicSync("TransportActivity", new TransportVehicleDao()));
		*/
	}

	// All classes that requires dirty sync
	private void initPushSyncList() {

		pushSyncList.add(MarkerInstallationPushSync.getInstance());
		pushSyncList.add(PunchPushSync.getInstance());
		pushSyncList.add(ServiceActivityPushSync.getInstance());
		pushSyncList.add(VehicleRefuelLogPushSync.getInstance());
		pushSyncList.add(LoginSessionPushSync.getInstance());
		pushSyncList.add(CalloutPushSync.getInstance());

		pushSyncList.add(ServiceActivityDetailsPushSync.getInstance());
		pushSyncList.add(InspectionJournalPushSync.getInstance());
		pushSyncList.add(DamagePushSync.getInstance());
		
		pushSyncList.add(UploadsPushSync.getInstance());

		pushSyncList.add(WorksheetPushSync.getInstance());
		pushSyncList.add(WorkOrderPushSync.getInstance());
		pushSyncList.add(DeficiencyPushSync.getInstance());
		//        pushSyncList.add(TransportActivityPushSync.getInstance());
	}

	/**
	 * Adds a one-time operation to perform in the next periodic sync interval 
	 * @param oneTimeSync
	 */
	synchronized public void addOneTimeSync(AbstractOneTimeSync oneTimeSync) {
		oneTimeSyncList.add(oneTimeSync);
	}

	/**
	 * This method must be called at regular interval before the user is logged in to perform a minimal database synchronization required to
	 * let the user authenticate the tablet for the first time.
	 */
	public void runPreLoginSync(Context c) {
		if (!initialized) {
			init(c);
		}
		timerDelay += SLEEP_DELAY;

		if (timerDelay >= NORMAL_SYNC_DELAY) {
			sync(c, preLoginSyncList);
			timerDelay = 0;
		}
	}

	/**
	 * This method must be called at regular interval to query the Snowman database to retrieve the latest updates and store them in our
	 * local database.
	 */
	public void runPeriodicSync(Context c) {
		if (!initialized) {
			init(c);
		}
		timerDelay += SLEEP_DELAY;
		boolean resetDelay = false;

		if (Session.inStormMode) {
			// We are operating in storm mode
			if (timerDelay >= STORM_PRIORITY_SYNC_DELAY) {
				sync(c, periodicStormPrioritySyncList);
				lastStormPrioritySync = new Date();
			}

			if (timerDelay >= STORM_SYNC_DELAY) {
				sync(c, periodicStormSyncList);
				lastPeriodicSync = new Date();

				if (!oneTimeSyncList.isEmpty()) {
					syncOneTimeList(c);
				}

				resetDelay = true;
			}
		} else {
			// We are operating in normal mode
			if (timerDelay >= NORMAL_SYNC_DELAY) {
				sync(c, periodicSyncList);
				lastPeriodicSync = new Date();

				if (!oneTimeSyncList.isEmpty()) {
					syncOneTimeList(c);
				}

				resetDelay = true;
			}
		}

		if (Session.viewVehicles && (timerDelay >= VEHICLE_REPORT_SYNC_DELAY)) {
			// We need to synchronize the vehicle last reports
			sync(c, periodicSuperDriverSyncList);
		}

		if (resetDelay) {
			timerDelay = 0;
		}
	}

	synchronized private void syncOneTimeList(Context c) {
		if (Utils.isOnline(c)) {
			Log.i("DbSyncManager", "Online - " + oneTimeSyncList.size() + " one-time sync to perform");
			lastOneTimeSync = new Date();
			List<AbstractOneTimeSync> cleanupList = new ArrayList<AbstractOneTimeSync>();
			for (AbstractOneTimeSync oneTimeSync : oneTimeSyncList) {
				if (oneTimeSync.fetchData(c) == false) {
					Log.e("DbSyncManager", "OneTime sync failed for model " + oneTimeSync.getModel());
					// Record the failure and keep going
					recordOneTimeSyncFailure(oneTimeSync.getModel());
				} else {
					// Sync has been successful, let's remember to remove it from our one-time sync list
					cleanupList.add(oneTimeSync);
				}
			}

			for (AbstractOneTimeSync oneTimeSync : cleanupList) {
				oneTimeSyncList.remove(oneTimeSync);
			}

		} else {
			Log.w("DbSyncManager", "Offline - skipping one-time sync");
		}
	}

	private void sync(Context c, List<AbstractPeriodicSync> syncList) {
		if (Utils.isOnline(c)) {
			context = c;			
			onPreSync();
			for (AbstractPeriodicSync periodicSync : syncList) {
				if (periodicSync.fetchData(c) == false) {
					Log.e("DbSyncManager", "Periodic sync failed for model " + periodicSync.getModel());
					// Record the failure and keep going
					recordPeriodicSyncFailure(periodicSync.getModel());
				}
			}
			onPostSync();
		} else {
			Log.w("DbSyncManager", "Offline - skipping periodic sync");
		}
	}

	/**
	 * This method must be called at regular interval to send any pending data in our local database that has not been sent to the master
	 * database on Snowman yet.
	 */
	public void sendUpdates(Context c) {  

		if (!initialized) {
			init(c);
		}
		
		if (Utils.isOnline(c)) {
			for (AbstractPushSync pushSync : pushSyncList) {
				if (pushSync.syncData() == false) {
					Log.e("DbSyncManager", "Push sync failed for " + pushSync.getModel());
					// Record the failure and keep going
					recordPushSyncFailure(pushSync.getModel());
				}
			}
			lastPushSync = new Date();
		}
	}

	public void recordPushSyncFailure(String model) {
		recordPushSyncFailure(model, null, false);
	}
	
	public void recordPushSyncFailure(String model, String message, boolean alertUser) {
		DbSyncFailure failure = pushSyncFailures.get(model);
		if (failure == null) {
			failure = new DbSyncFailure(model);
			pushSyncFailures.put(model, failure);
		} else {
			failure.increment();
		}
		
		if (alertUser)
			sendMessageToMainActivity(context, "DB SYNC Alert: " + message);		
	}

	private void recordPeriodicSyncFailure(String model) {
		DbSyncFailure failure = periodicSyncFailures.get(model);
		if (failure == null) {
			failure = new DbSyncFailure(model);
			periodicSyncFailures.put(model, failure);
		} else {
			failure.increment();
		}
	}

	private void recordOneTimeSyncFailure(String model) {
		DbSyncFailure failure = oneTimeSyncFailures.get(model);
		if (failure == null) {
			failure = new DbSyncFailure(model);
			oneTimeSyncFailures.put(model, failure);
		} else {
			failure.increment();
		}
	}

	/**
	 * This field stores the list of sync that is run on every app startup
	 */
	private final List<AbstractPeriodicSync> startupSyncList = new Vector<AbstractPeriodicSync>();

	public void runStartUpSync(Context c) {
		initStartupSyncList();
		for (AbstractPeriodicSync periodicSync : startupSyncList) {
			if (periodicSync.fetchData(c) == false) {
				Log.e("DbSyncManager", "Periodic sync failed for model " + periodicSync.getModel());
				// Record the failure and keep going
				recordPeriodicSyncFailure(periodicSync.getModel());
			}
		}
		startupSyncList.clear();
	}

	//TODO replace by android-core/AbstractMainActivity.onInitialSync()
	private void initStartupSyncList() {
		startupSyncList.add(new DefaultPeriodicSync("Company", new CompanyDao()));
		startupSyncList.add(new DefaultPeriodicSync("TabletConfig", new TabletConfigsDao()));
	}
	
	private void sendMessageToMainActivity(Context context, String message) {
		if (Session.MapAct == null)
			return;
		
		Intent intent = new Intent("DbSyncManager");
		// add data
		intent.putExtra("message", message);
		
		LocalBroadcastManager.getInstance(Session.MapAct).sendBroadcast(intent);
	}
		
}