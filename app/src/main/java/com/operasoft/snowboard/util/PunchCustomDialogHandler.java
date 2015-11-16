package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Dto;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UserWorkStatusLogs;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.events.PunchInEvent;
import com.operasoft.snowboard.events.PunchOutEvent;
import com.operasoft.snowboard.maplayers.TIT_RoutePoint;
import com.operasoft.snowboard.view.StaffListView;

public class PunchCustomDialogHandler {

	private final Context context;
	private final View view;
	private TextView pass;
	private Dialog dialog;
	private boolean showSelectorDialog = false;
	private ArrayList<PointOfInterest> polygonList;
	private String serviceLocationAddress;
	private Punch punch;

	// staff list view
	TextView txtDialogTitle;
	private StaffListView mStaffListView;

	public PunchCustomDialogHandler(Context context, View view) {
		this.context = context;
		this.view = view;

	}

	private void setStatusLabel(User user) {
		if (user.getWorkStatus() == null)
			return;
		if (user.getWorkStatus().equals("onsite")) {
			user.workStatusLabel = (StaffListView.STATUS_ONSITE);
		} else if (user.getWorkStatus().equals("invehicle")) {
			user.workStatusLabel = (StaffListView.STATUS_INVEHICLE);
		} else if (user.getWorkStatus().equals("inactive")) {
			user.workStatusLabel = (StaffListView.STATUS_INACTIVE);
		}
	}

	private void showStaffListView() {

		final HashMap<String, Dto> dtoByIds = new HashMap<String, Dto>();

		AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {

			@Override
			protected List<User> doInBackground(Void... params) {
				ServiceLocationDao slDao = new ServiceLocationDao();
				VehiclesDao vehicleDao = new VehiclesDao();

				final ArrayList<User> lstUsers = new ArrayList<User>();
				for (User user : (new UsersDao()).listAll()) {
					lstUsers.add(user);

					// replace service location id by the name 
					if (user.getCurrentServiceLocationId() != null && dtoByIds.get(user.getCurrentServiceLocationId()) == null) {
						ServiceLocation serviceLocation = slDao.getById(user.getCurrentServiceLocationId());
						if (serviceLocation != null) {
							dtoByIds.put(user.getCurrentServiceLocationId(), serviceLocation);
							user.serviceLocation = serviceLocation;
						}
					} else {
						user.serviceLocation = (ServiceLocation) dtoByIds.get(user.getCurrentServiceLocationId());
					}

					// replace vehicle id by the name 
					if (user.getCurrentVehicleId() != null && dtoByIds.get(user.getCurrentVehicleId()) == null) {
						Vehicle vehicle = vehicleDao.getById(user.getCurrentVehicleId());
						if (vehicle != null) {
							dtoByIds.put(user.getCurrentVehicleId(), vehicle);
							user.vehicle = vehicle;
						}
					} else {
						user.vehicle = (Vehicle) dtoByIds.get(user.getCurrentVehicleId());
					}

					setStatusLabel(user);

				}
				return lstUsers;
			}

			@Override
			protected void onPostExecute(List<User> lstUsers) {
				String vehicleId = null;
				if (Session.getVehicle() != null && Session.getVehicle().getName() != null)
					vehicleId = Session.getVehicle().getId();
				mStaffListView.initUserList(lstUsers, vehicleId);
				mStaffListView.show();
				mStaffListView.setVisibility(View.VISIBLE);
			}

		};
		task.execute();

	}

	private void createDialog() {

		dialog = new Dialog(context);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogHeader = inflater.inflate(R.layout.dialoag_listview_punch_clock, (ViewGroup) view.findViewById(R.id.root_punch));
		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) dialogHeader.findViewById(R.id.iMg_cancel_dialog);
		TextView closeText = (TextView) dialogHeader.findViewById(R.id.textView2);
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mStaffListView.getVisibility() == View.VISIBLE) {
					mStaffListView.setVisibility(View.GONE);
					txtDialogTitle.setText(R.string.dialog_punch_clock);
				} else
					dialog.dismiss();
			}
		});
		txtDialogTitle = (TextView) dialogHeader.findViewById(R.id.textView1);
		builder.setCustomTitle(dialogHeader);
		View layout1;
		// if (!showSelectorDialog) {

		/**
		 * TODO Enabled this when we integrate Schumacher in production if (alreadyPunched) {
		 * System.out.println("punched in at login dialog show"); layout1 =
		 * inflater.inflate(R.layout.dialog_punched_already, null); UsersDao usersDao = new
		 * UsersDao(); User user = usersDao.getById(punch.getUserId()); ((TextView)
		 * layout1.findViewById(R.id.tv_dpa_message)).setText(user.getFirstName() + " " +
		 * user.getLastName() + ", You are currently punched in on " + punch.getDateTime() +
		 * ", and cannot punch in again. Click on below button if you would like to punch out");
		 * ((Button)
		 * layout1.findViewById(R.id.btn_dpa_punch_out)).setOnClickListener(punchListener);
		 * System.out.println("showing dialog"); } else {
		 */
		layout1 = inflater.inflate(R.layout.dialog_punch_clock, null);
		// staff list view controles
		mStaffListView = (StaffListView) layout1.findViewById(R.id.panelstafflist);

		((Button) layout1.findViewById(R.id.login_screen_button0)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button1)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button2)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button3)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button4)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button5)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button6)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button7)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button8)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_button9)).setOnClickListener(punchListener);

		((Button) layout1.findViewById(R.id.login_screen_punch_in)).setOnClickListener(punchListener);
		((Button) layout1.findViewById(R.id.login_screen_punch_out)).setOnClickListener(punchListener);
		Button staffListButton = ((Button) layout1.findViewById(R.id.login_view_staff_list));
		staffListButton.setOnClickListener(punchListener);
		staffListButton.setVisibility(Session.isStaffListEnabled() ? View.VISIBLE : View.GONE);

		((ImageButton) layout1.findViewById(R.id.login_screen_buttonReset)).setOnClickListener(punchListener);
		((ImageButton) layout1.findViewById(R.id.login_screen_buttonOK)).setOnClickListener(punchListener);

		/**
		 * TODO Enabled this when we integrate Schumacher in production Button todayTimesheet =
		 * (Button) layout1.findViewById(R.id.login_screen_timesheet); if
		 * (Session.getDriver().isForeman()) todayTimesheet.setVisibility(View.VISIBLE); else
		 * todayTimesheet.setVisibility(View.GONE); ((Button)
		 * layout1.findViewById(R.id.login_screen_timesheet)).setOnClickListener(punchListener);
		 * ((TextView)
		 * layout1.findViewById(R.id.login_screen_address)).setText(serviceLocationAddress);
		 */
		pass = (TextView) layout1.findViewById(R.id.et_password);

		pass.setGravity(Gravity.CENTER);
		// }
		/**
		 * TODO Enabled this when we integrate Schumacher in production } else { layout1 =
		 * inflater.inflate(R.layout.dialog_many_polygons, null); Spinner polygonSelector =
		 * (Spinner) layout1.findViewById(R.id.spinner_polygons); final ArrayList<String> list = new
		 * ArrayList<String>(); list.add("none"); for (PointOfInterest poi : polygonList) {
		 * list.add(poi.getAddress()); } ArrayAdapter<String> dataAdapter = new
		 * ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
		 * dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 * polygonSelector.setAdapter(dataAdapter); polygonSelector.setOnItemSelectedListener(new
		 * OnItemSelectedListener() {
		 * 
		 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3)
		 *           { if (pos != 0) { dialog.dismiss(); serviceLocationAddress = list.get(pos);
		 *           showSelectorDialog = false; createDialog(); } }
		 * @Override public void onNothingSelected(AdapterView<?> arg0) { // TODO Auto-generated
		 *           method stub } }); }
		 */

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		// if (!showSelectorDialog && !alreadyPunched)
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

	}

	public OnClickListener punchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_punch_clock:

				if (dialog != null)
					if (dialog.isShowing())
						dialog.dismiss();

				polygonList = getPolygonAdrslist();
				if (polygonList.size() == 0) {
					// showAlert();
					createDialog();
				} else if (polygonList.size() == 1) {
					showSelectorDialog = false;
					PointOfInterest poi = polygonList.get(0);
					serviceLocationAddress = poi.getAddress();
					createDialog();
				} else if (polygonList.size() > 1) {
					showSelectorDialog = true;
					createDialog();
				}
				break;

			case R.id.login_screen_button0:
			case R.id.login_screen_button1:
			case R.id.login_screen_button2:
			case R.id.login_screen_button3:
			case R.id.login_screen_button4:
			case R.id.login_screen_button5:
			case R.id.login_screen_button6:
			case R.id.login_screen_button7:
			case R.id.login_screen_button8:
			case R.id.login_screen_button9:
				pass.setText("" + pass.getText() + ((Button) v.findViewById(v.getId())).getText());
				break;

			case R.id.login_screen_buttonReset:
				pass.setText("");
				break;

			case R.id.login_screen_buttonOK:
				if (pass.getText().length() > 0) {
					String temp = pass.getText().toString();
					temp = temp.substring(0, temp.length() - 1);
					pass.setText(temp);
				}
				break;

			case R.id.login_screen_punch_in:
				String uId = CommonUtils.selectUserId(pass.getText().toString());
				doPunch(true, uId);
				break;

			case R.id.login_screen_punch_out:
				String userId = CommonUtils.selectUserId(pass.getText().toString());
				doPunch(false, userId);
				break;

			case R.id.login_view_staff_list:
				mStaffListView.setVisibility(View.VISIBLE);
				txtDialogTitle.setText("STAFF LIST");
				showStaffListView();
				break;

			/**
			 * TODO Enabled this when we integrate Schumacher in production case
			 * R.id.login_screen_timesheet: dialog.dismiss(); TimesheetTodayCustomDialog
			 * timesheetToday = new TimesheetTodayCustomDialog(context);
			 * timesheetToday.createDialog(); break; case R.id.btn_dpa_punch_out: doPunch(false,
			 * punch.getUserId()); break;
			 */
			}
		}
	};

	/**
	 * Perform Punch In/Out operation
	 * 
	 * @param isIn
	 */
	private void doPunch(boolean isIn, final String userId) {
		if (userId.equals("")) {
			AlertDialog.Builder alert_box = new AlertDialog.Builder(context);
			final Dialog dialog1 = new Dialog(context);
			alert_box.setIcon(R.drawable.icon);
			alert_box.setMessage(R.string.wrong_user_pin_enter_correct_in);
			alert_box.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					pass.setText("");
					dialog1.dismiss();
					PunchCustomDialogHandler.this.dialog.show();
				}
			});
			alert_box.show();
		} else {

			UsersDao usersDao = new UsersDao();
			User user = usersDao.getById(userId);

			if (!isIn) {
				if (!user.getWorkStatus().equals(User.STATUS_INACTIVE)) {
					PunchOutEvent event = new PunchOutEvent(context, userId);
					event.doPunchOut(false);
				}
			} else {

				if (user.getWorkStatus().equals(User.STATUS_IN_VEHICLE)) {

					String msg = "You are currently marked as in Vehicle in " + (new VehiclesDao()).getById(user.getCurrentVehicleId()).getName() + " since " + user.getWorkStatusDate() + "\n"
							+ "Do you want to continue and override this status?";
					AlertDialog.Builder alert_box = new AlertDialog.Builder(context);
					alert_box.setIcon(R.drawable.icon);
					alert_box.setMessage(msg);
					alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							PunchOutEvent outEvent = new PunchOutEvent(context, userId);
							outEvent.doPunchOut(false);

							PunchInEvent inEvent = new PunchInEvent(context, userId);
							inEvent.doPunch(UserWorkStatusLogs.PUNCH_IN);
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
				} else if (user.getWorkStatus().equals(User.STATUS_ON_SITE)) {

					String msg = "You are currently marked as On Site at " + (new ServiceLocationDao()).getById(user.getCurrentServiceLocationId()).getName() + " Location since "
							+ user.getWorkStatusDate() + "\n" + "Are you being picked up from this location?";
					AlertDialog.Builder alert_box = new AlertDialog.Builder(context);
					alert_box.setIcon(R.drawable.icon);
					alert_box.setMessage(msg);
					alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							PunchInEvent inEvent = new PunchInEvent(context, userId);
							inEvent.doPunch(UserWorkStatusLogs.PICK_UP);
							dialog.dismiss();
						}
					});
					alert_box.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					alert_box.setNegativeButton("NO", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PunchOutEvent outEvent = new PunchOutEvent(context, userId);
							outEvent.doPunchOut(false);

							PunchInEvent inEvent = new PunchInEvent(context, userId);
							inEvent.doPunch(UserWorkStatusLogs.PUNCH_IN);
							dialog.dismiss();
						}
					});

					alert_box.show();
				} else if (user.getWorkStatus().equals(User.STATUS_INACTIVE)) {
					PunchInEvent punchIn = new PunchInEvent(context, userId);
					punchIn.doPunch(UserWorkStatusLogs.PUNCH_IN);
				}

			}
			dialog.dismiss();
		}
	}

	/**
	 * Returns the polygons list
	 * 
	 * @return activePoisList
	 */
	private ArrayList<PointOfInterest> getPolygonAdrslist() {

		PointOfInterestManager poiManager = PointOfInterestManager.getInstance();
		ArrayList<PointOfInterest> activePoisList = new ArrayList<PointOfInterest>();
		
		boolean done = true;
		do {
			done = true;
			activePoisList.clear();
			Collection<PointOfInterest> activePois = poiManager.listActivePois();
			
			try {
				for (PointOfInterest poi : activePois) {

					List<TIT_RoutePoint> nodes = CommonUtils.getPolyNodes(poi.getPolygon());
					double[] nlat = new double[nodes.size()];
					double[] nlon = new double[nodes.size()];

					for (int i = 0; i < nodes.size(); i++) {
						nlat[i] = nodes.get(i).getLatitude();
						nlon[i] = nodes.get(i).getLongitude();
					}

					if (Session.clocation != null) {
						if (CommonUtils.isPointInPolygon(nlon, nlat, nodes.size(),

						Session.clocation == null ? 0.0f : (float) Session.clocation.getLongitude(),

						Session.clocation == null ? 0.0f : (float) Session.clocation.getLatitude())) {
							activePoisList.add(poi);
						}

					}
				}
			} catch (ConcurrentModificationException e) {
				// The list of active POIs has been modified while we parsed it... Try again
				done = false;
			}
		} while (!done);
		return activePoisList;
	}
}