package com.operasoft.snowboard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractServices;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.Divisions;
import com.operasoft.snowboard.database.DivisionsDao;
import com.operasoft.snowboard.database.DropEmployees;
import com.operasoft.snowboard.database.DropEmployeesDao;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.PunchDao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.WorksheetEmployeeLogs;
import com.operasoft.snowboard.database.WorksheetMaintenance;
import com.operasoft.snowboard.database.WorksheetMaintenancesDao;
import com.operasoft.snowboard.dbsync.push.DropEmployeesPushSync;
import com.operasoft.snowboard.dbsync.push.MaintenancesPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceActivityPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;

public class TaskListCustomDialog implements OnClickListener {

	private static final String CONSTRUCTION = "Construction";
	private static final String MAINTENANCE = "Maintenance";

	private Context mContext;
	private dialogStatus status = dialogStatus.CONTRACT_LIST;

	private static Dialog dialog;
	private View layout1;
	private String titleText;
	private List<String> lineItems;
	private LinearLayout llList, llList1;
	private Chronometer chronometer;
	private Button startButton, nextLocationButton;
	private boolean clicked = false;

	private UsersDao userDao;
	private PunchDao punchDao;
	private ServiceActivityDao saDao;
	private DropEmployeesDao dropEmployeeDao;

	private String chronoText = "00:00:00";
	private int stoppedMilliseconds = 0;
	private String cDate;
	private String arrivedTime;
	private SimpleDateFormat dateFormat, dateTimeFormat;

	private PointOfInterest mPoi;
	private Contract contract;
	private ArrayList<Contract> contractList = new ArrayList<Contract>();
	private ServiceActivity serviceActivity;
	private String timeOnSite;
	private ServiceActivityPushSync saPush = ServiceActivityPushSync.getInstance();
	private String contractType = "Construction";
	private ArrayList<User> dropUserList = new ArrayList<User>();
	private String serviceLocationId;
	private ContractsDao contractDao = new ContractsDao();
	final ArrayList<String> list = new ArrayList<String>();

	// List of all Users dropped on any service locations
	private ArrayList<User> droppedEmployees = new ArrayList<User>();

	private ArrayList<User> leftEmployees = new ArrayList<User>();

	// List of Employees Punched In Today
	private List<Punch> punchList = new ArrayList<Punch>();

	// Employees already dropped
	private ArrayList<DropEmployees> droppedEmployeeList = new ArrayList<DropEmployees>();

	// employee left to drop (show in list)
	// private ArrayList<Punch> showEmployeeLIst = new ArrayList<Punch>();

	// Employees dropped on this service location
	ArrayList<DropEmployees> dropServiceLocationList = new ArrayList<DropEmployees>();

	private enum dialogStatus {
		TASKLIST, EMPLOYEE_DROP_LIST, EMPLOYEE_PICK_LIST, CONTRACT_LIST
	}

	@SuppressLint("SimpleDateFormat")
	public TaskListCustomDialog(Context context, PointOfInterest poi) {

		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		userDao = new UsersDao();
		punchDao = new PunchDao();
		dropEmployeeDao = new DropEmployeesDao();
		saDao = new ServiceActivityDao();
		mContext = context;
		mPoi = poi;

		arrivedTime = dateTimeFormat.format(new Date());

		serviceActivity = saDao.getLastEnroute(Session.getVehicle().getId());
		if (serviceActivity == null)
			serviceActivity = poi.getCurrentServiceActivity();

		serviceLocationId = serviceActivity.getServiceLocation().getId();
		cDate = dateFormat.format(new Date());

		dropServiceLocationList = dropEmployeeDao.getDropEmployeesOnSL(serviceLocationId, cDate);

		for (DropEmployees drop : dropServiceLocationList) {
			dropUserList.add(userDao.getById(drop.getEmployeeId()));
		}

		droppedEmployeeList = dropEmployeeDao.getdropEmployee(cDate);
		for (DropEmployees dropEmployee : droppedEmployeeList) {
			droppedEmployees.add(userDao.getById(dropEmployee.getEmployeeId()));
		}

		Utils utils = Utils.getInstance(context);
		String imeiNum = utils.getIMEI();
		punchList = punchDao.listEmployees(cDate, imeiNum);
		for (Punch punch : punchList) {
			if (!droppedEmployees.contains(userDao.getById(punch.getUserId()))) {
				leftEmployees.add(userDao.getById(punch.getUserId()));
			}
		}

		// If no employees dropped on this service location show task List
		if (dropServiceLocationList.size() != 0) {
			status = dialogStatus.TASKLIST;
			chronoText = getWorkTime(dropServiceLocationList.get(0).getDropTime(), arrivedTime);
		} else {
			Thread arrivedThread = new Thread(new Runnable() {

				@Override
				public void run() {
					serviceActivity.setArrivedTime(arrivedTime);
					saPush.pushData(mContext, serviceActivity);
				}
			});
			arrivedThread.start();
		}

		// Initial time to show on chronometer
		String array[] = chronoText.split(":");
		if (array.length == 2) {
			stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 + Integer.parseInt(array[1]) * 1000;
		} else if (array.length == 3) {
			stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 + Integer.parseInt(array[1]) * 60 * 1000 + Integer.parseInt(array[2]) * 1000;
		}
	}

	/**
	 * @param timeIn
	 * @param timeOut
	 * @return time difference string
	 */
	protected String getWorkTime(String timeIn, String timeOut) {
		try {
			long diff = dateTimeFormat.parse(timeOut).getTime() - dateTimeFormat.parse(timeIn).getTime();
			long diffSeconds = diff / (1000) % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000);

			return diffHours + ":" + diffMinutes + ":" + diffSeconds;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "NAN";
	}

	public void createDialog() {
		lineItems = new ArrayList<String>();
		if (status.equals(dialogStatus.EMPLOYEE_PICK_LIST) || status.equals(dialogStatus.EMPLOYEE_DROP_LIST))
			titleText = "Employee Punch List";
		else
			titleText = "Line Items";

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
		tvTitleText.setText(titleText);
		iMg_cancel_dialog.setOnClickListener(this);
		closeText.setOnClickListener(this);
		builder.setCustomTitle(layout);

		// Content of dialog
		switch (status) {
		case CONTRACT_LIST:
			contractList = contractDao.getAllContractForServiceLocationId(serviceActivity.getServiceLocationId());
			Divisions division = null;
			for (Contract contract : contractList) {
				DivisionsDao divisionsDao = new DivisionsDao();
				if (contract.getDivisionId() == null) {
					Toast.makeText(mContext, "No division found.", Toast.LENGTH_LONG).show();
					return;
				}
				division = divisionsDao.getById(contract.getDivisionId());
				if (division != null)
					list.add(division.getName() + "  Job Number   " + contract.getJobNumber());
			}

			// if list is empty
			if (list.size() < 1) {
				Toast.makeText(mContext, "No contract attached to this SL", Toast.LENGTH_SHORT).show();
				return;
			} else if (list.size() == 1) {

				// Skip this step if only one item in list and show next dialog
				contract = contractList.get(0);
				contractType = list.get(0).substring(0, list.get(0).indexOf(" "));

				if (contractType.equals(CONSTRUCTION))
					status = dialogStatus.EMPLOYEE_DROP_LIST;
				else
					status = dialogStatus.TASKLIST;

				if (dialog != null)
					if (dialog.isShowing())
						dialog.dismiss();

				createDialog();
				return;
			}

			layout1 = inflater.inflate(R.layout.dialog_many_polygons, null);
			TextView spinnerTitle = (TextView) layout1.findViewById(R.id.textView1);

			spinnerTitle.setText("Select Contract");

			ListView listView = (ListView) layout1.findViewById(R.id.lv_dmp);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, list);
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					contract = contractList.get(pos);
					contractType = list.get(pos).substring(0, list.get(pos).indexOf(" "));

					if (contractType.equals(CONSTRUCTION))
						status = dialogStatus.EMPLOYEE_DROP_LIST;
					else
						status = dialogStatus.TASKLIST;

					dialog.dismiss();
					createDialog();
				}
			});

			break;

		case TASKLIST:
			if (contract != null) {
				List<ContractServices> contractServices = contract.listServices();
				for (ContractServices service : contractServices) {
					if (!lineItems.contains(service.getId())) {
						lineItems.add(service.getProductName());
					}
				}
			}
			layout1 = inflater.inflate(R.layout.dialog_task_list, null);
			chronometer = (Chronometer) layout1.findViewById(R.id.chronometer);
			startButton = (Button) layout1.findViewById(R.id.btn_chron_start);
			nextLocationButton = (Button) layout1.findViewById(R.id.btn_return_map);
			if (contractType.equalsIgnoreCase(MAINTENANCE))
				nextLocationButton.setVisibility(View.GONE);
			nextLocationButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.hide();
				}
			});
			startButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!clicked) {
						clicked = true;
						startButton.setText("End Now");
						chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
							@Override
							public void onChronometerTick(Chronometer cArg) {
								long t = SystemClock.elapsedRealtime() - cArg.getBase();
								int h = (int) (t / 3600000);
								int m = (int) (t - h * 3600000) / 60000;
								int s = (int) (t - h * 3600000 - m * 60000) / 1000;
								String hh = h < 10 ? "0" + h : h + "";
								String mm = m < 10 ? "0" + m : m + "";
								String ss = s < 10 ? "0" + s : s + "";
								cArg.setText(hh + ":" + mm + ":" + ss);
							}
						});
						chronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
						chronometer.start();
						startButton.setTextColor(Color.WHITE);
						startButton.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.off));
					} else {
						chronometer.stop();
						timeOnSite = chronometer.getText().toString();

						long basetime = SystemClock.elapsedRealtime() - chronometer.getBase();
						basetime = basetime * dropServiceLocationList.size();
						int h = (int) (basetime / 3600000);
						int m = (int) (basetime - h * 3600000) / 60000;
						int s = (int) (basetime - h * 3600000 - m * 60000) / 1000;
						String hh = h < 10 ? "0" + h : h + "";
						String mm = m < 10 ? "0" + m : m + "";
						String ss = s < 10 ? "0" + s : s + "";
						timeOnSite = hh + ":" + mm + ":" + ss;

						dialog.dismiss();
						// serviceActivity.setTimeOnSite(chronometer.getText().toString());
						// saPush.pushData(mContext, serviceActivity);
						if (contractType.equals(MAINTENANCE)) {
							dialog.dismiss();

							// creating new worksheet
							updateWorksheet();

							MaintenanceDialogHandler maintenanceHandler = new MaintenanceDialogHandler(mContext, timeOnSite, serviceLocationId, serviceActivity, dropUserList);
							maintenanceHandler.createDialog();

						} else {
							status = dialogStatus.EMPLOYEE_PICK_LIST;
							dialog.dismiss();
							createDialog();
						}
					}
				}
			});

			llList = (LinearLayout) layout1.findViewById(R.id.ll_sal_list);
			if (lineItems.size() == 0) {
				lineItems.add("No line item for this location");
			}
			for (int i = 0; i < lineItems.size(); i++) {
				inflater.inflate(R.layout.contract_line_item_row, llList);
			}
			for (int i = 0; i < llList.getChildCount(); i++) {
				TextView productName = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_add_service_activity);
				productName.setText(lineItems.get(i));
			}
			if (dropServiceLocationList.size() != 0) {
				startButton.performClick();
				clicked = true;
				// nextLocationButton.setVisibility(View.GONE);
			}
			break;

		case EMPLOYEE_DROP_LIST:
			layout1 = inflater.inflate(R.layout.dialog_employee_list, null);
			Button dropEmployees = (Button) layout1.findViewById(R.id.btn_drop_employees);
			dropEmployees.setOnClickListener(this);
			llList1 = (LinearLayout) layout1.findViewById(R.id.ll_employee_list);
			for (int i = 0; i < leftEmployees.size(); i++) {
				inflater.inflate(R.layout.row_employee_punch, llList1);
			}
			for (int i = 0; i < llList1.getChildCount(); i++) {
				TextView productName = (TextView) ((LinearLayout) llList1.getChildAt(i)).findViewById(R.id.tv_employee_name);
				try {
					productName.setText(leftEmployees.get(i).getFirstName());
				} catch (NullPointerException e) {
					productName.setText("name not available");
				}
			}
			break;

		case EMPLOYEE_PICK_LIST:
			layout1 = inflater.inflate(R.layout.dialog_employee_list, null);
			Button pickEmployees = (Button) layout1.findViewById(R.id.btn_drop_employees);
			pickEmployees.setText("Pick Employees");
			pickEmployees.setTextColor(Color.WHITE);
			pickEmployees.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.off));
			pickEmployees.setOnClickListener(this);
			llList1 = (LinearLayout) layout1.findViewById(R.id.ll_employee_list);
			for (int i = 0; i < dropServiceLocationList.size(); i++) {
				inflater.inflate(R.layout.row_employee_punch, llList1);
			}
			for (int i = 0; i < llList1.getChildCount(); i++) {
				TextView productName = (TextView) ((LinearLayout) llList1.getChildAt(i)).findViewById(R.id.tv_employee_name);
				CheckBox checkBox = (CheckBox) ((LinearLayout) llList1.getChildAt(i)).findViewById(R.id.checkbox_drop);
				checkBox.setVisibility(View.GONE);
				DropEmployees dropEmployee = dropServiceLocationList.get(i);
				try {
					productName.setText(userDao.getById(dropEmployee.getEmployeeId()).getFirstName() + " " + userDao.getById(dropEmployee.getEmployeeId()).getLastName());
				} catch (NullPointerException e) {
					productName.setText("Name not avaialble");
				}
			}
			break;

		default:
			break;
		}

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.show();
		if (!status.equals(dialogStatus.CONTRACT_LIST))
			dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drop_employees:
			switch (status) {
			case EMPLOYEE_DROP_LIST:
				if (llList1.getChildCount() == 0) {
					dialog.dismiss();
					Toast.makeText(mContext, "No employees to drop", Toast.LENGTH_LONG).show();
					return;
				}

				// push selected Employees to SM in background thread
				Thread dropEmployeesThread = new Thread(new Runnable() {

					@Override
					public void run() {
						// creating new worksheet
						updateWorksheet();

						for (int i = 0; i < llList1.getChildCount(); i++) {
							CheckBox c = (CheckBox) ((LinearLayout) llList1.getChildAt(i)).findViewById(R.id.checkbox_drop);
							if (c.isChecked()) {
								DropEmployees dropEmployees = new DropEmployees();
								Date today = Calendar.getInstance().getTime();
								String dropTime = dateTimeFormat.format(today);
								dropEmployees.setDropTime(dropTime);
								dropEmployees.setCompanyId(Session.getCompanyId());
								dropEmployees.setEmployeeId(leftEmployees.get(i).getId());
								dropEmployees.setOperation("drop");
								dropEmployees.setContractId(contract.getId());
								dropEmployees.setServiceLocationId(serviceActivity.getServiceLocationId());
								dropEmployees.setLongitude(Session.clocation == null ? "0.0" : "" + Session.clocation.getLongitude());
								dropEmployees.setLatitude(Session.clocation == null ? "0.0" : "" + Session.clocation.getLatitude());

								// adding worksheet Id to dao
								WorksheetMaintenancesDao worksheetMaintenancesDao = new WorksheetMaintenancesDao();
								String worksheetMaintenanceId = worksheetMaintenancesDao.getWorksheetForServiceLocation(serviceLocationId).getId();
								dropEmployees.setWorksheetMaintenanceId(worksheetMaintenanceId);

								DropEmployeesPushSync dropEmployeesPushSync = new DropEmployeesPushSync();
								dropEmployeesPushSync.pushData(mContext, dropEmployees);
							}
						}

						// Refresh Drop Employees List again after Employees
						// Dropped
						dropServiceLocationList = dropEmployeeDao.getDropEmployeesOnSL(serviceActivity.getServiceLocation().getId(), dateFormat.format(new Date()));

						for (DropEmployees drop : dropServiceLocationList) {
							dropUserList.add(userDao.getById(drop.getEmployeeId()));
						}
					}

				});
				dropEmployeesThread.start();

				status = dialogStatus.TASKLIST;
				dialog.dismiss();
				createDialog();
				break;
			case EMPLOYEE_PICK_LIST:
				// Pushing pushed employees to SM in background thread
				Thread pickEmployeesThread = new Thread(new Runnable() {

					@Override
					public void run() {
						for (DropEmployees dropEmployees : dropServiceLocationList) {
							Date today = Calendar.getInstance().getTime();
							String pickTime = dateTimeFormat.format(today);
							dropEmployees.setPickTime(pickTime);
							dropEmployees.setOperation("pick");
							dropEmployees.setLongitude(Session.clocation == null ? "0.0" : "" + Session.clocation.getLongitude());
							dropEmployees.setLatitude(Session.clocation == null ? "0.0" : "" + Session.clocation.getLatitude());

							// adding worksheet Id to dao
							WorksheetMaintenancesDao worksheetMaintenancesDao = new WorksheetMaintenancesDao();
							String worksheetMaintenanceId = worksheetMaintenancesDao.getWorksheetForServiceLocation(serviceLocationId).getId();
							dropEmployees.setWorksheetMaintenanceId(worksheetMaintenanceId);

							DropEmployeesPushSync dropEmployeesPushSync = new DropEmployeesPushSync();
							dropEmployeesPushSync.pushData(mContext, dropEmployees);
						}
					}
				});
				pickEmployeesThread.start();
				dialog.dismiss();
				MaintenanceDialogHandler maintenanceHandler = new MaintenanceDialogHandler(mContext, timeOnSite, serviceLocationId, serviceActivity, dropUserList);
				maintenanceHandler.createDialog();
				break;
			default:
				break;
			}

			break;
		case R.id.iMg_cancel_dialog:
		case R.id.textView2:
			dialog.dismiss();
			break;
		default:
			break;
		}

	}

	public static void dismissDialog() {
		if (dialog != null)
			dialog.dismiss();
	}

	/**
	 * create new worksheet and push it onto the server
	 */
	@SuppressLint("NewApi")
	private void updateWorksheet() {
		WorksheetMaintenance worksheetMaintenance = new WorksheetMaintenance();
		worksheetMaintenance.setWorksheetType(contractType);
		worksheetMaintenance.setArrived(arrivedTime);
		worksheetMaintenance.setCompanyId(Session.getCompanyId());
		worksheetMaintenance.setDate(cDate);
		try {
			worksheetMaintenance.setEmployeeId(userDao.getUserIdForPin(Session.getUserPin()));
		} catch (Exception e) {
			// worksheetMaintenance.setEmployeeId(userDao.getUserIdForPin(Session.getUserPin()));
		}
		worksheetMaintenance.setEnRoute(serviceActivity.getEnrouteTime());
		worksheetMaintenance.setJobNumber(contractDao.getById(serviceActivity.getContractId()).getJobNumber());
		worksheetMaintenance.setServiceLocationId(serviceLocationId);
		try {
			worksheetMaintenance.setTrailer(Session.getTrailer().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (serviceActivity.getEnrouteLatitude() != null && !serviceActivity.getEnrouteLatitude().isEmpty()) {
			try {
				double enrouteLat = Double.parseDouble(serviceActivity.getEnrouteLatitude());
				double enrouteLOn = Double.parseDouble(serviceActivity.getEnrouteLongitude());
				Location enRouteLOcation = new Location("");
				enRouteLOcation.setLatitude(enrouteLat);
				enRouteLOcation.setLongitude(enrouteLOn);
				String employeeTravelTime = getWorkTime(serviceActivity.getEnrouteTime(), arrivedTime);
				if (Session.clocation != null) {
					float travelDistance = enRouteLOcation.distanceTo(Session.clocation);
					travelDistance = round(travelDistance, 2);
					worksheetMaintenance.setTravelDistance(travelDistance + "");
				}
				worksheetMaintenance.setTravelTime(employeeTravelTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ArrayList<WorksheetEmployeeLogs> worksheetEmployeeLogs = new ArrayList<WorksheetEmployeeLogs>();
		if (droppedEmployeeList.size() == 0) {
			for (int employeeCount = 0; employeeCount < punchList.size(); employeeCount++) {
				WorksheetEmployeeLogs temp = new WorksheetEmployeeLogs();
				temp.setCompany_id(Session.getCompanyId());
				temp.setEmp_id(punchList.get(employeeCount).getUserId());
				try {
					temp.setEmp_name(userDao.getById(punchList.get(employeeCount).getUserId()).getFirstName());
				} catch (Exception e) {

				}
				temp.setPunch_in(punchDao.getPunchFromUserId(punchList.get(employeeCount).getUserId()).getDateTime());
				worksheetEmployeeLogs.add(temp);
			}
			worksheetMaintenance.setWorksheetEmployeeList(worksheetEmployeeLogs);
			MaintenancesPushSync maintenancePushSync = new MaintenancesPushSync();
			maintenancePushSync.pushData(mContext, worksheetMaintenance);

		} else {
			for (int employeeCount = 0; employeeCount < dropServiceLocationList.size(); employeeCount++) {
				WorksheetEmployeeLogs temp = new WorksheetEmployeeLogs();
				temp.setCompany_id(Session.getCompanyId());
				temp.setEmp_id(dropServiceLocationList.get(employeeCount).getEmployeeId());
				temp.setEmp_name(userDao.getById(dropServiceLocationList.get(employeeCount).getEmployeeId()).getFirstName());
				temp.setPunch_in(punchDao.getPunchFromUserId(dropServiceLocationList.get(employeeCount).getEmployeeId()).getDateTime());
				worksheetEmployeeLogs.add(temp);
			}
			worksheetMaintenance.setWorksheetEmployeeList(worksheetEmployeeLogs);
			MaintenancesPushSync maintenancePushSync = new MaintenancesPushSync();
			maintenancePushSync.pushData(mContext, worksheetMaintenance);
		}
	}

	public static float round(float value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (float) tmp / factor;
	}
}