package com.operasoft.snowboard.util;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UserWorkStatusLogs;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.Vehicle;
import com.operasoft.snowboard.database.VehiclesDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.PunchPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;

/**
 * This class will create a new dialog to drop/pick employees
 * 
 * @author Enabke
 */
public class PickDropEmployeesCustomDialog implements OnClickListener {

	public enum dialogStatus {
		EMPLOYEE_DROP_LIST, EMPLOYEE_PICK_LIST
	}

	private Context mContext;

	private Dialog dialog;
	private View subLayout;
	private LinearLayout llList, llOtherList;

	private UsersDao userDao;
	private ServiceActivityDao saDao;

	private PointOfInterest mPoi;
	private dialogStatus mDialogStatus;
	private ServiceActivity serviceActivity;
	private String serviceLocationId;

	// List of all Users with status inVehicle
	private ArrayList<User> slInVehicleEmpList;

	// List of all Users dropped current service location
	private ArrayList<User> slDroppedEmpList;

	// List of all Users that are not inactive
	private ArrayList<User> slOtherDroppedEmpList;

	private ToggleButton btn_drop_emp, btn_other_emp;

	@SuppressLint("SimpleDateFormat")
	public PickDropEmployeesCustomDialog(Context context, PointOfInterest poi, dialogStatus dialogStatus) {

		userDao = new UsersDao();
		saDao = new ServiceActivityDao();
		mContext = context;
		mPoi = poi;
		mDialogStatus = dialogStatus;

		serviceActivity = saDao.getLastEnroute(Session.getVehicle().getId());
		if (serviceActivity == null)
			serviceActivity = poi.getCurrentServiceActivity();

		serviceLocationId = mPoi.getSlId();

		slInVehicleEmpList = userDao.getUsersInVehicle(Session.getVehicle().getId());
		slDroppedEmpList = userDao.getDropEmployeesOnSL(serviceLocationId);
		slOtherDroppedEmpList = userDao.getOtherDropEmployeesOnSL(Session.getVehicle().getId(), serviceLocationId);

	}

	public void createDialog() {

		dialog = new Dialog(mContext);

		// CREATE DIALOG
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock, (ViewGroup) dialog.findViewById(R.id.root_punch));
		// Dialog Title with close button
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
		iMg_title_dialog.setImageResource(R.drawable.add);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		TextView tvTitleText = (TextView) layout.findViewById(R.id.textView1);
		tvTitleText.setText("Employee Punch List");
		iMg_cancel_dialog.setOnClickListener(this);
		closeText.setOnClickListener(this);
		builder.setCustomTitle(layout);
		subLayout = inflater.inflate(R.layout.dialog_employee_list, null);

		btn_drop_emp = (ToggleButton) subLayout.findViewById(R.id.btn_drop_emp);
		btn_other_emp = (ToggleButton) subLayout.findViewById(R.id.btn_other_drop);
		btn_drop_emp.setOnClickListener(this);
		btn_other_emp.setOnClickListener(this);
		// Content of dialog
		switch (mDialogStatus) {

		case EMPLOYEE_DROP_LIST:
			btn_drop_emp.setVisibility(View.GONE);
			btn_other_emp.setVisibility(View.GONE);
			Button dropEmployees = (Button) subLayout.findViewById(R.id.btn_drop_employees);
			dropEmployees.setOnClickListener(this);

			llList = (LinearLayout) subLayout.findViewById(R.id.ll_employee_list);
			for (int i = 0; i < slInVehicleEmpList.size(); i++) {
				inflater.inflate(R.layout.row_employee_punch, llList);
			}

			for (int i = 0; i < llList.getChildCount(); i++) {
				TextView productName = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_employee_name);
				try {
					productName.setText(slInVehicleEmpList.get(i).getFirstName() + " " + slInVehicleEmpList.get(i).getLastName());
				} catch (NullPointerException e) {
					productName.setText("name not available");
				}
			}
			break;

		case EMPLOYEE_PICK_LIST:
			getPickEmpList();

			break;

		default:
			break;
		}

		builder.setView(subLayout);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drop_employees:
			switch (mDialogStatus) {
			case EMPLOYEE_DROP_LIST:

				if (llList.getChildCount() == 0) {
					dismissDialog();
					Toast.makeText(mContext, "No employees to drop", Toast.LENGTH_LONG).show();
					return;
				}

				// push selected Employees to SM in background thread
				Thread dropEmployeesThread = new Thread(new Runnable() {
					@Override
					public void run() {
						((Activity) mContext).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								for (int i = 0; i < llList.getChildCount(); i++) {
									CheckBox c = (CheckBox) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.checkbox_drop);
									if (c.isChecked()) {
										// Refreshing user data
										User user = userDao.getById(slInVehicleEmpList.get(i).getId());
										if (user != null)
											pickUser(user);
									}
								}
							}
						});

					}

				});
				dropEmployeesThread.start();
				dismissDialog();

				break;
			case EMPLOYEE_PICK_LIST:
				// Pushing pushed employees to SM in background thread
				new Thread(new Runnable() {

					@Override
					public void run() {
						((Activity) mContext).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								for (int i = 0; i < slDroppedEmpList.size(); i++) {
									CheckBox c = (CheckBox) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.checkbox_drop);
									if (c.isChecked())
										dropUser(slDroppedEmpList.get(i));
								}
								if (llOtherList != null) {
									for (int i = 0; i < slOtherDroppedEmpList.size(); i++) {
										CheckBox c = (CheckBox) ((LinearLayout) llOtherList.getChildAt(i)).findViewById(R.id.checkbox_drop);
										if (c.isChecked())
											dropUser(slOtherDroppedEmpList.get(i));
									}
								}
							}
						});

					}
				}).start();
				dismissDialog();
				break;
			default:
				break;
			}

			break;
		case R.id.iMg_cancel_dialog:
		case R.id.textView2:
			dismissDialog();
			break;
		case R.id.btn_other_drop:
			if (btn_other_emp.isChecked()) {
				if (llOtherList == null)
					getOtherDropEmpList();
				else {
					llList.setVisibility(View.GONE);
					llOtherList.setVisibility(View.VISIBLE);
				}
			}
			btn_other_emp.setClickable(false);
			btn_drop_emp.setChecked(false);
			btn_drop_emp.setClickable(true);
			break;
		case R.id.btn_drop_emp:
			if (btn_drop_emp.isChecked()) {
				if (llList == null)
					getPickEmpList();
				else {
					llOtherList.setVisibility(View.GONE);
					llList.setVisibility(View.VISIBLE);
				}
			}
			btn_drop_emp.setClickable(false);
			btn_other_emp.setChecked(false);
			btn_other_emp.setClickable(true);
			break;
		default:
			break;
		}

	}

	private void dropUser(final User user) {
		if (user.getWorkStatus().equals(User.STATUS_INACTIVE)) {
			String msg = user.getFirstName() + " " + user.getLastName() + " is currently marked as inactive \n Do you want to continue and override this status?";
			AlertDialog.Builder alert_box = new AlertDialog.Builder(mContext);
			alert_box.setIcon(R.drawable.icon);
			alert_box.setMessage(msg);
			alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					user.setWorkStatus(User.STATUS_IN_VEHICLE);
					updateDetails(user, UserWorkStatusLogs.PUNCH_IN);

					dialog.dismiss();
				}
			});
			alert_box.setNeutralButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			AlertDialog dialog = alert_box.create();
			dialog.show();

		} else if (user.getWorkStatus().equals(User.STATUS_IN_VEHICLE)) {

			VehiclesDao dao = new VehiclesDao();
			Vehicle vehicle = dao.getById(user.getCurrentVehicleId());
			String locAt = "<vehicle>";
			if (vehicle != null)
				locAt = vehicle.getName();

			String msg = user.getFirstName() + " " + user.getLastName() + " is currently marked as in Vehicle in " + locAt + " since " + user.getWorkStatusDate() + "\n"
					+ "Do you want to continue and override this status?";
			AlertDialog.Builder alert_box = new AlertDialog.Builder(mContext);
			alert_box.setIcon(R.drawable.icon);
			alert_box.setMessage(msg);
			alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					user.setWorkStatus(User.STATUS_INACTIVE);
					updateDetails(user, UserWorkStatusLogs.PUNCH_OUT);

					user.setWorkStatus(User.STATUS_IN_VEHICLE);
					updateDetails(user, UserWorkStatusLogs.PUNCH_IN);

					dialog.dismiss();
				}
			});
			alert_box.setNeutralButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			AlertDialog dialog = alert_box.create();
			dialog.show();

		} else {

			user.setWorkStatus(User.STATUS_IN_VEHICLE);
			updateDetails(user, UserWorkStatusLogs.PICK_UP);
		}

	}

	private void pickUser(final User user) {
		if (user.getWorkStatus().equals(User.STATUS_INACTIVE)) {

			String msg = user.getFirstName() + " " + user.getLastName() + " is currently marked as inactive \n Do you want to continue and override this status?";
			AlertDialog.Builder alert_box = new AlertDialog.Builder(mContext);
			alert_box.setIcon(R.drawable.icon);
			alert_box.setMessage(msg);
			alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					user.setWorkStatus(User.STATUS_IN_VEHICLE);
					updateDetails(user, UserWorkStatusLogs.PUNCH_IN);

					user.setWorkStatus(User.STATUS_ON_SITE);
					updateDetails(user, UserWorkStatusLogs.DROP_OFF);

					dialog.dismiss();
				}
			});
			alert_box.setNeutralButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			AlertDialog dialog = alert_box.create();
			dialog.show();

		} else if (user.getWorkStatus().equals(User.STATUS_ON_SITE)) {

			ServiceLocationDao dao = new ServiceLocationDao();
			String locAt = dao.getById(user.getCurrentServiceLocationId()).getName();

			String msg = user.getFirstName() + " " + user.getLastName() + " is currently marked as On Site at " + locAt + " since " + user.getWorkStatusDate() + "\n"
					+ "Do you want to continue and override this status?";
			AlertDialog.Builder alert_box = new AlertDialog.Builder(mContext);
			alert_box.setIcon(R.drawable.icon);
			alert_box.setMessage(msg);
			alert_box.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					user.setWorkStatus(User.STATUS_IN_VEHICLE);
					updateDetails(user, UserWorkStatusLogs.PICK_UP);

					user.setWorkStatus(User.STATUS_ON_SITE);
					updateDetails(user, UserWorkStatusLogs.DROP_OFF);

					dialog.dismiss();
				}
			});
			alert_box.setNeutralButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			AlertDialog dialog = alert_box.create();
			dialog.show();

		} else {

			user.setWorkStatus(User.STATUS_ON_SITE);
			updateDetails(user, UserWorkStatusLogs.DROP_OFF);
		}
	}

	/**
	 * Update user work status details to server.
	 * 
	 * @param user
	 * @param Operation
	 */
	private void updateDetails(User user, String Operation) {
		Utils utils = new Utils(mContext);
		UserWorkStatusLogs statusLogs = new UserWorkStatusLogs();

		statusLogs.setUserId(user.getId());
		statusLogs.setCompanyId(Session.getCompanyId());
		statusLogs.setWorkStatus(user.getWorkStatus());
		statusLogs.setOperation(Operation);
		statusLogs.setImei(utils.getIMEI());
		statusLogs.setDateTime(CommonUtils.UtcDateNow());
		statusLogs.setVehicleId(Session.getVehicle().getId());
		statusLogs.setServiceLocationId(serviceLocationId);
		statusLogs.setLongitude(Session.clocation == null ? 0.0 : Session.clocation.getLongitude());
		statusLogs.setLatitude(Session.clocation == null ? 0.0 : Session.clocation.getLatitude());
		statusLogs.setCreatorId(Session.getDriver().getId());

		PunchPushSync pushSync = PunchPushSync.getInstance();
		pushSync.pushData(mContext, statusLogs);

	}

	private void dismissDialog() {
		if (dialog != null)
			dialog.dismiss();
	}

	private void getPickEmpList() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Button pickEmployees = (Button) subLayout.findViewById(R.id.btn_drop_employees);
		pickEmployees.setText("Pick Employees");
		pickEmployees.setTextColor(Color.WHITE);
		pickEmployees.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.off));
		pickEmployees.setOnClickListener(this);
		llList = (LinearLayout) subLayout.findViewById(R.id.ll_employee_list);
		llList.setVisibility(View.VISIBLE);

		for (int i = 0; i < slDroppedEmpList.size(); i++) {
			inflater.inflate(R.layout.row_employee_punch, llList);
		}
		for (int i = 0; i < llList.getChildCount(); i++) {
			TextView productName = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_employee_name);
			User dropEmployee = slDroppedEmpList.get(i);
			try {
				productName.setText(dropEmployee.getFirstName() + " " + dropEmployee.getLastName());
			} catch (NullPointerException e) {
				productName.setText("Name not avaialble");
			}
		}
	}

	private void getOtherDropEmpList() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Button pickEmployees = (Button) subLayout.findViewById(R.id.btn_drop_employees);
		pickEmployees.setText("Pick Employees");
		pickEmployees.setTextColor(Color.WHITE);
		pickEmployees.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.off));
		pickEmployees.setOnClickListener(this);
		llOtherList = (LinearLayout) subLayout.findViewById(R.id.ll_other_employee_list);
		llOtherList.setVisibility(View.VISIBLE);

		for (int i = 0; i < slOtherDroppedEmpList.size(); i++) {
			inflater.inflate(R.layout.row_employee_punch, llOtherList);
		}
		for (int i = 0; i < llOtherList.getChildCount(); i++) {
			TextView productName = (TextView) ((LinearLayout) llOtherList.getChildAt(i)).findViewById(R.id.tv_employee_name);
			User dropEmployee = slOtherDroppedEmpList.get(i);
			try {
				productName.setText(dropEmployee.getFirstName() + " " + dropEmployee.getLastName());
			} catch (NullPointerException e) {
				productName.setText("Name not avaialble");
			}
		}
	}

}
