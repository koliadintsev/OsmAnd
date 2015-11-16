package com.operasoft.snowboard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.activities.SettingsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lazydroid.autoupdateapk.AutoUpdateApk;
import com.operasoft.android.gps.services.GPSService;
import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.DataBaseHelper;
import com.operasoft.snowboard.database.ImeiCompany;
import com.operasoft.snowboard.database.ImeiCompanyDao;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.Site;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UserWorkStatusLogs;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.DbSyncManager;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.events.PunchInEvent;
import com.operasoft.snowboard.events.PunchOutEvent;
import com.operasoft.snowboard.services.MyAlarmService;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.Session;
import com.operasoft.snowboard.util.Session.SessionType;
import com.operasoft.snowboard.util.VehicleLoginController;

public class Sw_LoginScreenActivity extends Activity implements OnClickListener {
	EditText et_password, serverUrl;
	private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b0;
	private ToggleButton btnTabTruck;// , btnTabSite;
	ImageButton ok, btn_connect;
	private TextView pass;
	Vehicle vehicle = null;
	Site site = null;
	List<Vehicle> vehiclesArray;
	private List<Site> sitesArray;
	SharedPreferences mSP;
	Editor editor;
	String mIMEI, mUserPin;

	ScrollView scrollView;
	private ListView truckList;
	float mTopIndex = 0, mFinalIndex = 100;
	int mPrevY;
	boolean mFirstScroll = true;
	String TAG = "Sw_LoginScreenActivity";
	private ArrayList<String> vehiclesNameArray;
	private ArrayList<String> sitesNameArray;
	private AutoUpdateApk aua;
	ProgressDialog mProgressDialog;
	private VehiclesDao vehiclesDao = new VehiclesDao();
	private UsersDao userDao = new UsersDao();
	boolean mBound = false;
	String vimei;
	protected boolean inTruckMode = true;
	private Dialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Configure the application based on its build type
		Config.init();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sw_main_login);

		if (Config.isAutoUpdateEnabled()) {
			aua = new AutoUpdateApk(getApplicationContext());
			aua.checkUpdatesManually();
		}

		// Start the service Monitor if he hasnt started yet
		// Intent monitorIntent = new Intent(getApplicationContext(), ServiceMonitor.class);
		// startService(monitorIntent);

		// Configure the session
		mSP = PreferenceManager.getDefaultSharedPreferences(this);
		Session.init(getApplicationContext(), mSP);

		editor = mSP.edit();
		if (mSP.getBoolean(Config.IS_RESET_KEY, false)) {
			AlertDialog.Builder altDialog = new AlertDialog.Builder(this);
			altDialog.setMessage("Application Reset Successfully.");
			altDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					initDB();
					editor.putBoolean(Config.IS_RESET_KEY, false);
					editor.commit();
					Session.inResetMode = false;
					dialog.dismiss();
				}
			});
			altDialog.show();
		}

		if (Session.logoutFromServer) {
			AlertDialog.Builder altDialog = new AlertDialog.Builder(this);
			altDialog
					.setMessage("You have been logged out by your Company Administrator. Reasons: Punched into incorrect vehicle ID or you have failed to log out correctly. Please try again or contact your company administrator for assistance");
			altDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Session.logoutFromServer = false;
					dialog.dismiss();
				}
			});
			altDialog.show();
		}

		if (!hasStorage(true)) { // if SDCard exists
			noStorageAlert();
		} else {
			// Initializing the database.
			if (initDB())
				showSuspendedDialog();

		}

	}

	private void startupSync() {
		DbSyncManager.getInstance().runStartUpSync(this);
		Session.getCompany(true);
	}

	/**
	 * Initializes the UI of login page
	 * 
	 * @return false if user is already logged in and opens MapActivity, else true and initialize UI
	 */
	public boolean initUI() {
		// get the user pin from SharedPreferences manager.
		String userPin = mSP.getString(Config.USER_PIN_KEY, "");

		setAction();

		btnTabTruck.setChecked(true);
		// btnTabSite.setChecked(false);
		inTruckMode = true;

		populateList();

		// To check the user already login or not.
		// if login it redirect to the Map activity.
		User driver = null;
		if (!userPin.equals("")) {
			driver = userDao.getByPin(userPin);
		}		
		
		if (driver != null) {
			startupSync();
			// User already logged in, launch the map
			Session.setDriver(driver);
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("vehicle", vehicle);
			this.startActivity(intent);
			finish();
			return false;
		} else {

			// The user must log in before launching the map
			truckList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					Parcelable state = truckList.onSaveInstanceState();

					if (inTruckMode) {
						vehicle = vehiclesArray.get(pos);
						truckList.setAdapter(new TruckListAdapter(Sw_LoginScreenActivity.this, vehiclesNameArray, pos));
					} else {
						site = sitesArray.get(pos);
						truckList.setAdapter(new TruckListAdapter(Sw_LoginScreenActivity.this, sitesNameArray, pos));
					}

					truckList.onRestoreInstanceState(state);
					setLoginMarker();
				}
			});

			Log.i(TAG, "page load");
			if (hasStorage(true)) {
				TextView companyName, imeiTV, lastSync, deviceName, configInfo, tabletName, versionName/*, businessType*/;

				companyName = (TextView) findViewById(R.id.comp_name_text);
				imeiTV = (TextView) findViewById(R.id.imei_text_view);
				lastSync = (TextView) findViewById(R.id.textView4);
				deviceName = (TextView) findViewById(R.id.textView3);
				configInfo = (TextView) findViewById(R.id.textView2);
				tabletName = (TextView) findViewById(R.id.tablet_name);
				versionName = (TextView) findViewById(R.id.tv_version_name);
				//businessType = (TextView) findViewById(R.id.businessType);
				//tvWclogin = (TextView) findViewById(R.id.tV_wc_login);
				
				ImageView imLogo = (ImageView) findViewById(R.id.imgView_logo);
				
				int versionCode = -1;
				try {
					versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
					Config.setVersion(versionCode);
				} catch (Exception e) {
				}

				configInfo.setText("Version: " + versionCode + "\nConfig: " + Config.CURRENT_CONFIG + "\nURL: " + Config.getBaseUrl());

				// Update the company and tablet name in the title bar
				Company company = Session.getCompany(true);
				if (company != null) {
					if (company.isSimplicity()) {
						imLogo.setImageResource(R.drawable.simplicity);
					}
					companyName.setText(company.getCompanyName());
				} else {
					companyName.setText("Company Name");
				}

				ImeiCompany imei = Session.getImeiCompany();
				if (imei != null) {
					tabletName.setText(imei.getName());
					imeiTV.setText(imei.getImeiNo());
					Config.setIMEI(imei.getImeiNo());
				} else {
					tabletName.setText("");
					Utils cU = new Utils(getApplicationContext());
					imeiTV.setText(cU.getIMEI());
					Config.setIMEI(cU.getIMEI());
				}
				
				

				try {
					versionName.setText(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				DataBaseHelper Db = new DataBaseHelper(Sw_LoginScreenActivity.this);
				lastSync.setText(new ContractsDao().getLastModified());

				deviceName.setText(Db.selectDeviceName());
			}

		}
		return true;
	}

	/**
	 * Login user into SB
	 */
	private void doLogin() {
		SharedPreferences.Editor prefEditor = mSP.edit();
		Session.FirstLogin = true;
		mUserPin = et_password.getText().toString();
		// Time to update the session
		if (inTruckMode) {
			Session.setVehicle(vehicle);
			Session.setDriver(userDao.getByPin(mUserPin));

			prefEditor.putString(Config.VEHICLE_ID_KEY, vehicle.getId());
			prefEditor.putString(Config.LAST_VEHICLE_KEY, vehicle.getId());
			prefEditor.putString(Config.SITE_ID_KEY, null);

			Session.setType(SessionType.VEHICLE_SESSION);
		} else {
			Session.setSite(site);
			Session.setDriver(userDao.getByPin(mUserPin));

			prefEditor.putString(Config.VEHICLE_ID_KEY, null);
			prefEditor.putString(Config.LAST_VEHICLE_KEY, null);
			prefEditor.putString(Config.SITE_ID_KEY, site.getId());

			Session.setType(SessionType.SITE_SESSION);
		}

		startupSync();
		startSync();

		prefEditor.putString(Config.USER_PIN_KEY, mUserPin);
		prefEditor.putString(Config.LAST_USER_PIN_KEY, mUserPin);
		prefEditor.putString(Config.SYNC_SERVICE_KEY, "yes");
		prefEditor.commit();

		// if no VIMEI associated with truck, starting GPSService on SB device
		if (!vehiclesDao.xergoEsn(getApplicationContext())) {
			vimei = vehiclesDao.getVimei(this);

			Intent intent = new Intent();
			intent.setAction(GPSService.UPDATE_IMEI_EVENT);
			intent.putExtra("Value", vimei);
			sendBroadcast(intent);

			intent = new Intent();
			intent.setAction(GPSService.SEND_IGNITION_ON_EVENT);
			sendBroadcast(intent);
		}

		startSession(mUserPin);
		Intent intent = new Intent(this, MapActivity.class);
		this.startActivity(intent);
		finish();
	}

	/**
	 * Will starts the new session and Punch in the user by sending details to the server
	 * 
	 * @param userPin
	 */
	private void startSession(String userPin) {
		PunchInEvent punchIn = new PunchInEvent(this, CommonUtils.selectUserId(userPin));

		punchIn.sendSessionStartEvent();
		punchIn.doPunch(UserWorkStatusLogs.PUNCH_IN);

	}

	/**
	 * Will Punch out the user by sending details to the server
	 * 
	 * @param userPin
	 */
	private void doPunchOut(String userPin) {
		PunchOutEvent punchOut = new PunchOutEvent(this, CommonUtils.selectUserId(userPin));

		punchOut.doPunchOut(false);

		// TODO: Aman add user to active user's list
	}

	/**
	 * show alert message dialog to user
	 * 
	 * @param msg
	 */
	private void showAlert(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void startSync() {
		MyAlarmService.setAlarm(getApplicationContext());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_connect:

			Utils utils = Utils.getInstance(Sw_LoginScreenActivity.this);
			// check if IMEI is suspended or not
			ImeiCompanyDao companyDao = new ImeiCompanyDao();
			if (companyDao.isTabSuspended(utils.getIMEI())) {
				showSuspendedDialog();

			} else {

				// Reset App if User Enters 987654 at Login time
				String userPin = et_password.getText().toString();

				if (!Config.RESET_PASSWORD.equals(userPin)) {

					CommonUtils cU = new CommonUtils(Sw_LoginScreenActivity.this);
					if ((cU.getAthenticateUser(et_password.getText().toString()))) {
						VehicleLoginController vehicleListController = new VehicleLoginController();

						LoginSession vehicleDriver = vehicleListController.listVehicles(Sw_LoginScreenActivity.this, vehicle.getId(),
								et_password.getText().toString());
						if (vehicleDriver != null) {

							if (!vehicleDriver.getSession_status().equals(LoginSession.END_STATUS)) {
								DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								DateFormat formatterTime = new SimpleDateFormat("hh:mm aa");
								DateFormat timeFormat = new SimpleDateFormat("EEEE");
								Date date = new Date();

								try {
									date = formatter.parse(vehicleDriver.getStart_datetime());
								} catch (ParseException e) {
									e.printStackTrace();
								}

								User user = userDao.getById(vehicleDriver.getUserId());
								String msg = "";
								if (user != null) {
									msg = "This vehicle " + vehicle.getName() + " has been selected by " + user.getFirstName() + " "
											+ user.getLastName() + " at " + formatterTime.format(date) + " on " + timeFormat.format(date)
											+ ". Please contact your Office Administrator for assistants.";
								} else {
									msg = "This vehicle " + vehicle.getName() + " has been selected by another user at " + formatterTime.format(date) + " on " + timeFormat.format(date)
											+ ". Please contact your Office Administrator for assistants.";
								}
								AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
								alert_box.setIcon(R.drawable.icon);
								alert_box.setMessage(msg);
								alert_box.setPositiveButton("OK", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});

								alert_box.show();
							} else
								validateLoginUser();
						} else {
							validateLoginUser();
						}

						break;

					} else {
						et_password.setText("");
						showAlert(getResources().getString(R.string.wrong_pass_enter_correct));
					}
				} else {
					AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
					alert_box.setIcon(R.drawable.icon);
					alert_box.setMessage(R.string.reset_app_db);
					alert_box.setPositiveButton("Reset Database", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							resetApp();
							dialog.dismiss();
						}
					});
					alert_box.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							resetText();
							dialog.dismiss();
						}
					});

					alert_box.show();
				}
			}
			break;
		case R.id.login_screen_button0:
			pass.setText("" + pass.getText() + b0.getText());
			break;
		case R.id.login_screen_button1:
			pass.setText("" + pass.getText() + b1.getText());
			break;
		case R.id.login_screen_button2:
			pass.setText("" + pass.getText() + b2.getText());
			break;
		case R.id.login_screen_button3:
			pass.setText("" + pass.getText() + b3.getText());
			break;
		case R.id.login_screen_button4:
			pass.setText("" + pass.getText() + b4.getText());
			break;
		case R.id.login_screen_button5:
			pass.setText("" + pass.getText() + b5.getText());
			break;
		case R.id.login_screen_button6:
			pass.setText("" + pass.getText() + b6.getText());
			break;
		case R.id.login_screen_button7:
			pass.setText("" + pass.getText() + b7.getText());
			break;
		case R.id.login_screen_button8:
			pass.setText("" + pass.getText() + b8.getText());
			break;
		case R.id.login_screen_button9:
			pass.setText("" + pass.getText() + b9.getText());
			break;
		case R.id.login_screen_buttonOK:
			if (pass.getText().length() > 0) {
				String temp = pass.getText().toString();
				temp = temp.substring(0, temp.length() - 1);
				pass.setText(temp);
			}
			break;
		}
		setLoginMarker();
	}

	/**
	 * Validate if user can login or not
	 */
	private void validateLoginUser() {
		User user = userDao.getByPin(et_password.getText().toString());
		Session.setDriver(user);

		if (user.getWorkStatus().equals(User.STATUS_INACTIVE)) {

			doLogin();
		} else {
			String locAt = "";
			if (user.getWorkStatus().equals(User.STATUS_IN_VEHICLE)) {
				VehiclesDao dao = new VehiclesDao();
				locAt = dao.getById(user.getCurrentVehicleId()).getName();
			} else {
				ServiceLocationDao dao = new ServiceLocationDao();
				locAt = dao.getById(user.getCurrentServiceLocationId()).getName();
			}
			String msg = "You are currently marked as " + user.getWorkStatus() + " at " + locAt + " since " + user.getWorkStatusDate()
					+ "\n" + "Do you want to continue and override this status?";
			AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
			alert_box.setIcon(R.drawable.icon);
			alert_box.setMessage(msg);
			alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					doPunchOut(et_password.getText().toString());
					doLogin();
					dialog.dismiss();
				}
			});
			alert_box.setNeutralButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alert_box.show();
		}
	}

	/**
	 * Enable and disable the Connect(Login) button.
	 */
	private void setLoginMarker() {
		if (pass.getText().toString().equals(Config.RESET_PASSWORD)) {
			updateStatusText();
			scrollView.setVisibility(View.VISIBLE);
			sendScroll();
		} else
			scrollView.setVisibility(View.GONE);

		if (((pass.getText().length() == 4) && ((inTruckMode && (vehicle != null)) || (!inTruckMode && (site != null))))
				|| (pass.getText().toString().equals(Config.RESET_PASSWORD))) {
			btn_connect.setImageResource(R.drawable.green_mark);
			btn_connect.setClickable(true);
		} else {
			btn_connect.setImageResource(R.drawable.white_mark);
			btn_connect.setClickable(false);
		}
	}

	private void updateStatusText() {
		TextView lastSync = (TextView) findViewById(R.id.textView4);
		DbSyncManager dbSync = DbSyncManager.getInstance();

		lastSync.setText(dbSync.getStatus());
	}

	/**
	 * Check if DB exists and validate the DB
	 * 
	 * @return true if DB is valid and User is not already logged in
	 */
	private boolean initDB() {
		DataBaseHelper myDbHelper = new DataBaseHelper(this);
		if (!myDbHelper.checkDataBase()) {
			myDbHelper.dbDelete();
			downloadDB();
			return false;
		} else {
			try {
				if (!myDbHelper.openDataBase()) {
					Log.e(TAG, "Unable to open database");
					stopService();
					myDbHelper.dbDelete();
					downloadDB();
					return false;
				}
			} catch (Exception e) {
				Log.e(TAG, "Unable to open database", e);
				stopService();
				myDbHelper.dbDelete();
				downloadDB();
				return false;
			}
			try {
				startSync();
				return initUI();
			} catch (Exception ioe) {
				Log.e(TAG, "Unable to launch activity");
			}

		}

		return false;
	}

	/**
	 * Stop the MyAlarmService
	 */
	private void stopService() {
		// Stopping alarm service
		if (Sw_LoginScreenActivity.this.stopService(new Intent(Sw_LoginScreenActivity.this, MyAlarmService.class))) {
			MyAlarmService.running = false;
			Log.w("Snowboard", "MyAlarmService stopped due to application reset.");
		} else {
			Log.e("Snowboard", "Failed to stop MyAlarmService in application reset.");
		}
	}

	private void downloadDB() {

		stopService();

		// Clear preferences
		SharedPreferences.Editor prefEditor = mSP.edit();
		prefEditor.putString("user_pin", "");
		prefEditor.commit();

		if (!NetworkUtilities.isOnline(this)) {
			noConnectivityAlert();
		} else {
			Utils utils = Utils.getInstance(Sw_LoginScreenActivity.this);
			String imeiNum = utils.getIMEI();
			if (imeiNum == null)
				imeiNum = "000000000000000";
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading initial database");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.show();

			DownloadFile downloadFile = new DownloadFile();
			downloadFile.execute(NetworkUtilities.BASE_URL + "initdb/" + imeiNum);
		}
	}

	/**
	 * This will delete the existing DB from SD card, clear the preferences and restart the
	 * activity.
	 */
	private void resetApp() {
		Session.inResetMode = true;
		// Stopping alarm and GPS services at reset.
		// MyAlarmService.cancelMyAlarmService(this.getApplicationContext());
		if (Sw_LoginScreenActivity.this.stopService(new Intent(Sw_LoginScreenActivity.this, MyAlarmService.class))) {
			MyAlarmService.running = false;
			Log.w("Snowboard", "MyAlarmService stopped due to application reset.");
		} else {
			Log.e("Snowboard", "Failed to stop MyAlarmService in application reset.");
		}

		if (Sw_LoginScreenActivity.this.stopService(new Intent(Sw_LoginScreenActivity.this, GPSService.class))) {
			Log.w("Snowboard", "GPSService stopped due to application reset.");
		} else {
			Log.e("Snowboard", "Failed to stop GPSService in application reset.");
		}

		try {
			DataBaseHelper myDbHelper = new DataBaseHelper(Sw_LoginScreenActivity.this);
			try {
				// Deleting Previous Database.
				myDbHelper.dbDelete();
			} catch (Exception ioe) {
				Log.e(TAG, "Unable to delete database");
			}
			Log.i(TAG, "DB Deleted");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// clear preferences
		editor.clear();
		editor.commit();

		editor.putBoolean(Config.IS_RESET_KEY, true);
		editor.putString(Config.SERVER_URL_KEY, NetworkUtilities.BASE_URL);
		editor.commit();

		// Recalling this activity
		startActivity(new Intent(Sw_LoginScreenActivity.this, Sw_LoginScreenActivity.class));
		finish();
	}

	private void setAction() {
		b0 = (Button) findViewById(R.id.login_screen_button0);
		b1 = (Button) findViewById(R.id.login_screen_button1);
		b2 = (Button) findViewById(R.id.login_screen_button2);
		b3 = (Button) findViewById(R.id.login_screen_button3);
		b4 = (Button) findViewById(R.id.login_screen_button4);
		b5 = (Button) findViewById(R.id.login_screen_button5);
		b6 = (Button) findViewById(R.id.login_screen_button6);
		b7 = (Button) findViewById(R.id.login_screen_button7);
		b8 = (Button) findViewById(R.id.login_screen_button8);
		b9 = (Button) findViewById(R.id.login_screen_button9);

		pass = (TextView) findViewById(R.id.et_password);

		et_password = (EditText) findViewById(R.id.et_password);
		btn_connect = (ImageButton) findViewById(R.id.btn_connect);
		truckList = (ListView) findViewById(R.id.lv_sml_truck_names);

		scrollView = (ScrollView) findViewById(R.id.scrView);

		ok = (ImageButton) findViewById(R.id.login_screen_buttonOK);

		btnTabTruck = (ToggleButton) findViewById(R.id.btn_truck_tab);
		// btnTabSite = (ToggleButton) findViewById(R.id.btn_sites_tab);

		btnTabTruck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// btnTabSite.setChecked(false);
				btnTabTruck.setChecked(true);
				if (!inTruckMode) {
					inTruckMode = true;
					populateList();
				}
			}
		});

		// Configure the "End of Day" button
		/*
		 * btnTabSite.setOnClickListener(new OnClickListener() {
		 * @Override public void onClick(View v) { btnTabSite.setChecked(true);
		 * btnTabTruck.setChecked(false); if (inTruckMode) { inTruckMode = false; populateList(); }
		 * } });
		 */

		b0.setOnClickListener(this);
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		b5.setOnClickListener(this);
		b6.setOnClickListener(this);
		b7.setOnClickListener(this);
		b8.setOnClickListener(this);
		b9.setOnClickListener(this);
		ok.setOnClickListener(this);

		btn_connect.setOnClickListener(this);

		btn_connect.setClickable(false);

	}

	/**
	 * This method will populate the list of Trucks/Sites, as selected by user
	 */
	protected void populateList() {

		if (inTruckMode) {
			// issue #37 fix(block 2 users from punch-in)
			// Doing force sync for Vehicles table
			// if (com.operasoft.snowboard.dbsync.Utils.isOnline(this)) {
			// System.out.println("Sw_LoginScreenActivity.populateList():vehicle sync start");
			// AbstractPeriodicSync vehiclesPeriodicSync = new AnonymousPeriodicSync("Vehicle", new
			// VehiclesDao());
			// vehiclesPeriodicSync.fetchData(this);
			// }

			String vehicleId = mSP.getString(Config.VEHICLE_ID_KEY, "");
			String lastVehicleId = mSP.getString(Config.LAST_VEHICLE_KEY, "");
			vehiclesDao = new VehiclesDao();

			// issue #37 fix(block 2 users from punch-in)
			// VehicleListController vehicleListController = new VehicleListController();
			// vehiclesArray = vehicleListController.listVehicles(this);//vehiclesDao.listSorted();
			vehiclesArray = vehiclesDao.listSorted();
			vehiclesNameArray = new ArrayList<String>();
			int selectedIndex = -1;

			for (int i = 0; i < vehiclesArray.size(); i++) {
				Vehicle vehicles = vehiclesArray.get(i);
				vehiclesNameArray.add(vehicles.getName());
				// Check if the user is already connected to a vehicle
				if (vehicles.getId().equals(vehicleId)) {
					vehicle = vehicles;
					selectedIndex = i;
				}
				// Else check if it is his preferred one
				if ((selectedIndex == -1) && (vehicles.getId().equals(lastVehicleId))) {
					vehicle = vehicles;
					selectedIndex = i;
				}
			}

			if ((selectedIndex == -1) && (!vehiclesArray.isEmpty())) {
				// The user is not currently connected to any vehicle and has no
				// preferred one
				vehicle = vehiclesArray.get(0);
				selectedIndex = 0;
			}

			// Update the session with the vehicle currently selected
			Session.setVehicle(vehicle);

			truckList.setAdapter(new TruckListAdapter(this, vehiclesNameArray, selectedIndex));
		} else {

			// Sites are not supported yet. Enable this code once they become
			// supported
			// SiteDao siteDao = new SiteDao();
			// sitesArray = siteDao.listAllValid();
			// sitesNameArray = new ArrayList<String>();
			//
			// for (Site site : sitesArray)
			// sitesNameArray.add(site.getName());

			truckList.setAdapter(new TruckListAdapter(this, sitesNameArray, 0));
		}

		setLoginMarker();
	}

	/**
	 * Check if writable SDcard present or not
	 * 
	 * @param requireWriteAccess
	 * @return boolean
	 */
	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * Check the SDcard present or not dialog box. Sep 19, 2012 - 11:06:15 AM
	 */
	public void noStorageAlert() {
		AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
		alert_box.setIcon(R.drawable.icon);
		alert_box.setMessage("No SD Card found, Please insert SD Card to proceed.");
		alert_box.setPositiveButton("Quit", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert_box.setNegativeButton("Retry", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!hasStorage(true))
					noStorageAlert();
			}
		});
		alert_box.show();
	}

	/**
	 * Alert the user that there is not network connectivity available
	 */
	public void noConnectivityAlert() {
		AlertDialog.Builder alert_box = new AlertDialog.Builder(this);
		alert_box.setIcon(R.drawable.icon);
		alert_box.setMessage("No network available, Please make sure you have Internet access to proceed.");
		alert_box.setPositiveButton("Quit", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				return;
			}
		});
		alert_box.setNegativeButton("Retry", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!NetworkUtilities.isOnline(Sw_LoginScreenActivity.this)) {
					noConnectivityAlert();
				}
			}
		});
		alert_box.show();
	}

	// Reset the value of text box
	private void resetText() {
		pass.setText("");
	}

	/**
	 * show punch out dialog with message you are already punched in
	 * 
	 * @param punchDto
	 */
	private void showSuspendedDialog() {

		Utils utils = Utils.getInstance(Sw_LoginScreenActivity.this);
		String imei = utils.getIMEI();

		ImeiCompanyDao companyDao = new ImeiCompanyDao();

		// if Tab not suspended not showing the dialog
		if (!companyDao.isTabSuspended(imei))
			return;

		dialog = new Dialog(this);
		Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout1 = inflater.inflate(R.layout.dialog_suspended_imei, null);

		((Button) layout1.findViewById(R.id.btn_dsi_retry)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDismiss(dialog);
				showSuspendedDialog();
			}
		});

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();

	}

	/**
	 * Scrolling the right bottom view. Sep 19, 2012 - 10:52:35 AM
	 */
	private void sendScroll() {
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mTopIndex <= mFinalIndex) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							mTopIndex += 0.1;
							scrollView.scrollBy(0, (int) mTopIndex);
							if (mTopIndex >= mFinalIndex) {
								mTopIndex = 0;
								scrollView.fullScroll(ScrollView.FOCUS_UP);
							}

							// Check if scroll completed or not
							if (mFirstScroll && (mPrevY == scrollView.getScrollY())) {
								mFinalIndex = mTopIndex + 5;
								mFirstScroll = false;
							}
							mPrevY = scrollView.getScrollY();
						}
					});
				}
			}
		}).start();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// if (mBound) {
		// unbindService(mConnection);
		// mBound = false;
		// }
		unbindDrawables(findViewById(R.id.login_screen_linearLayout1));
		System.gc();
	}

	private class DownloadFile extends AsyncTask<String, Integer, Long> {
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setProgress(progress[0]);
			if (progress[0] > 99) {
				mProgressDialog.setMessage("Unpacking database. Please wait...");
			}
		}

		@Override
		protected Long doInBackground(String... sUrl) {
			long total = 0;
			try {

				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				// this will be useful so that you can show a typical 0-100%
				// progress bar
				int fileLength = connection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream("/sdcard/snowman.db.gz");

				byte data[] = new byte[1024];
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				// mProgressDialog.setMessage("Unpacking database...");
				File infile = new File(Environment.getExternalStorageDirectory().getPath() + "/snowman.db.gz");
				// File infile = new File("/sdcard/snowman.db.gz");
				GZIPInputStream gin = new GZIPInputStream(new FileInputStream(infile));
				File outFile = new File(DataBaseHelper.DB_PATH + DataBaseHelper.DB_NAME);
				if (outFile.exists()) {
					outFile.delete();
				}
				FileOutputStream fos = new FileOutputStream(outFile);
				byte[] buf = new byte[100000]; // Buffer size is a matter of
												// taste and application...
				int len;
				while ((len = gin.read(buf)) > 0)
					fos.write(buf, 0, len);
				gin.close();
				fos.flush();
				fos.close();
				infile.delete();
				// mProgressDialog.setMessage("Done!");

			} catch (Exception e) {
				Log.e(TAG, "Failed to download initial database" + e.getMessage(), e);
				return -1L;
			}
			dialogDismiss(mProgressDialog);
			return total;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			Log.i("Download", "Downloaded " + result + " Bytes");

			if (result == -1L) {
				mProgressDialog
						.setMessage("ERROR: Failed to download the initial database.\nMake sure you are connected to the Internet.\nIfo, please contact Operasoft Technical Support.");

				dialogDismiss(mProgressDialog);

				finish();

			} else {
				if (!mSP.getBoolean("isReset", false)) {
					startActivity(new Intent(Sw_LoginScreenActivity.this, Sw_LoginScreenActivity.class));
					finish();
				}
			}
		}
	}

	/**
	 * Dismiss the dialog
	 * 
	 * @param dialog
	 */
	private void dialogDismiss(Dialog dialog) {
		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
