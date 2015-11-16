package com.operasoft.snowboard.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.operasoft.android.gps.ConfigurationProfile;
import com.operasoft.android.gps.services.GPSService;
import com.operasoft.snowboard.database.GpsConfig;
import com.operasoft.snowboard.dbsync.DbSyncManager;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.Session;

public class MyAlarmService extends Service {
	public static boolean running = false;

	String TAG = "My Alarm Services";
	public static String imeiNum = "";
	public static String userPin = "";
	SharedPreferences mSP;

	@Override
	public void onCreate() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		mSP = PreferenceManager.getDefaultSharedPreferences(this);
		if (!Session.isInit()) {
			Session.init(getApplicationContext(), mSP);
		}
		imeiNum = mSP.getString(Config.IMEI_NUM_KEY, "");
		userPin = mSP.getString(Config.USER_PIN_KEY, "");
		if (!Session.inResetMode) {
			new BackgroundLoader().execute();
		} else {
			Log.w(TAG, "Skipping sync while in reset mode...");
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	class BackgroundLoader extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;
		DbSyncManager syncMgr = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// Fetch the latest data from the server
			if (syncMgr == null) {
				syncMgr = DbSyncManager.getInstance();
			}
			if (syncMgr != null && syncMgr.gpsConfListener == null) {
				syncMgr.gpsConfListener = new DbSyncManager.GpsConfigListener() {
					@Override
					public void onChange(GpsConfig gpsConf) {
						final ConfigurationProfile configProfile = Config.getGpsProfile(gpsConf);
						final Intent intent = new Intent();
						intent.setAction(GPSService.UPDATE_CONFIG_EVENT);
						intent.putExtra(GPSService.UPDATE_CONFIG, configProfile);
						sendBroadcast(intent);
					}
				};
			}
			try {
				if (userPin.equals("")) {
					syncMgr.runPreLoginSync(getApplicationContext());
				} else {
					syncMgr.runPeriodicSync(getApplicationContext());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// Forward any pending data to the server
			try {
				syncMgr.sendUpdates(getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

	public static void setAlarm(Context c) {
		if (running == false) {
			running = true;
			AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(c, MyAlarmService.class);
			PendingIntent pi = PendingIntent.getService(c, 0, intent, 0);
			mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), DbSyncManager.SLEEP_DELAY * 1000, pi);
		}
	}

}