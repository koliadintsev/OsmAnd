package com.operasoft.snowboard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.osmand.plus.views.OsmandMapTileView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractServices;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.Divisions;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.ProductsDao;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.PunchDao;
import com.operasoft.snowboard.database.Route;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.WorksheetMaintenance;
import com.operasoft.snowboard.database.WorksheetMaintenanceProducts;
import com.operasoft.snowboard.database.WorksheetMaintenanceTasks;
import com.operasoft.snowboard.database.WorksheetMaintenancesDao;
import com.operasoft.snowboard.dbsync.push.MaintenancesPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceActivityPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.maplayers.PointOfInterestMenu;

public class MaintenanceDialogHandler {

	private static final String PER_EVENT = "Per Event";
	private Context context;
	private Dialog dialog;
	private LayoutInflater inflater;
	private View layout1;
	private int viewNumber = 1;
	private AlertDialog.Builder builder;
	private int count = 0;
	private String jobNum, SlAdrs, SlId, cDate, companyId;
	private Date date;
	private SimpleDateFormat dateFormat;
	private List<Products> materialList;
	private View[] maintainanceViewOne, maintainanceViewTwo;

	private ArrayList<WorksheetMaintenanceTasks> worksheetMaintenanceTask = new ArrayList<WorksheetMaintenanceTasks>();
	private ArrayList<WorksheetMaintenanceProducts> worksheetMaintenancesProducts;
	private String employeeWorkTime;
	private String enrouteTime = "not available";
	private String arrivedTime = "";
	private String completeTime;
	private ServiceActivity serviceActivity;
	private static String imgUriMaintenence = null;
	private List<ContractServices> taskList = new ArrayList<ContractServices>();
	private ArrayList<String> maintenanceEmployee = new ArrayList<String>();
	private String employeeTravelTime = "not available";
	private ArrayList<Divisions> allDivisionsList = new ArrayList<Divisions>();
	private String trailerName;
	private ServiceLocationDao slDao = new ServiceLocationDao();
	private ServiceLocation Slocation;
	private Contract contract;
	private String travelDistance;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private int topLimit;
	private int workTime;
	private int workTimeinQuarters;
	private TextView tvLeftToDistribute;
	private ArrayList<User> employeeList = new ArrayList<User>();
	private ServiceActivityPushSync saPush = ServiceActivityPushSync.getInstance();
	private PunchDao punchDao = new PunchDao();
	private UsersDao userDao = new UsersDao();
	private List<Punch> punchList = new ArrayList<Punch>();
	private String serviceLocationID;
	private WorksheetMaintenance worksheetMaintenance = new WorksheetMaintenance();
	private String tempAm = "";
	private String tempPm = "";
	private String tempEod = "";
	private int weatherPosition = 0;

	@SuppressLint("SimpleDateFormat")
	public MaintenanceDialogHandler(Context context, String employeeWorkTime, String serviceLocationId, ServiceActivity serviceActivity, ArrayList<User> userList) {
		ContractsDao contractDao = new ContractsDao();
		// getting worksheet id for this SL
		WorksheetMaintenancesDao worksheetMaintenancesDao = new WorksheetMaintenancesDao();
		worksheetMaintenance = worksheetMaintenancesDao.getWorksheetForServiceLocation(serviceLocationId);

		this.context = context;
		this.serviceLocationID = serviceLocationId;
		this.contract = contractDao.getById(serviceActivity.getContractId());
		this.employeeList = userList;
		this.employeeWorkTime = employeeWorkTime;
		this.serviceActivity = serviceActivity;

		Slocation = slDao.getById(serviceLocationID);
		SlAdrs = Slocation.getAddress();

		try {
			date = new Date();
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			arrivedTime = worksheetMaintenance.getArrived();
			travelDistance = worksheetMaintenance.getTravelDistance();
			employeeTravelTime = worksheetMaintenance.getTravelTime();
			enrouteTime = worksheetMaintenance.getEnRoute();
			jobNum = worksheetMaintenance.getJobNumber();

			if (Session.getTrailer() != null)
				trailerName = Session.getTrailer().getName();
			else
				trailerName = "none";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createDialog() {
		cDate = dateFormat.format(date);
		if (employeeList.size() == 0) {
			Utils cU = new Utils(context);
			String imeiNum = cU.getIMEI();
			punchList = punchDao.listEmployees(cDate, imeiNum);
			for (Punch punch : punchList) {
				employeeList.add(userDao.getById(punch.getUserId()));
			}
		}
		dialog = new Dialog(context);
		builder = new AlertDialog.Builder(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		companyId = Session.getCompanyId();
		try {
			SlId = serviceActivity.getServiceLocationId();
			String array[] = employeeWorkTime.split(":");
			int workTimeHours = Integer.parseInt(array[0]);
			int workTimeMinutes = Integer.parseInt(array[1]);
			workTime = workTimeHours * 60 + workTimeMinutes;
			workTimeinQuarters = workTime * employeeList.size();
			topLimit = workTimeinQuarters;
			if (serviceLocationID != null && serviceLocationID.trim().length() != 0) {

			} else {
				viewNumber = 1;
			}
		} catch (Exception e) {

		}
		View layout = inflater.inflate(R.layout.dialog_header, (ViewGroup) dialog.findViewById(R.id.root));

		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.img_dh_cancel);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.img_dh_title);
		TextView closeText = (TextView) layout.findViewById(R.id.tv_dh_cancel);
		TextView titleText = (TextView) layout.findViewById(R.id.tv_dh_title);

		iMg_title_dialog.setImageResource(R.drawable.map_action_transport);
		titleText.setText("Foreman Daily Worksheet");

		// Configure the "close" button
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewNumber = 1;
				dialog.dismiss();
			}
		});

		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				viewNumber = 1;
				dialog.dismiss();
			}
		});

		builder.setCustomTitle(layout);

		switch (viewNumber) {
		case 0:
			layout1 = inflater.inflate(R.layout.dialog_many_polygons, null);
			TextView spinnerTitle = (TextView) layout1.findViewById(R.id.textView1);
			Spinner polygonSelector = (Spinner) layout1.findViewById(R.id.spinner_polygons);
			spinnerTitle.setText("Select Contract");
			final ArrayList<String> list = new ArrayList<String>();
			list.add("none");
			for (Divisions division : allDivisionsList) {
				list.add(division.getName());
			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			polygonSelector.setAdapter(dataAdapter);
			polygonSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					if (pos != 0) {
						viewNumber++;
						dialog.dismiss();
						createDialog();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case 1:
			imgUriMaintenence = null;

			if (!SlId.equals(""))
				layout1 = inflater.inflate(R.layout.dialog_maintenance_1, null);

			((Button) layout1.findViewById(R.id.btn_maintenance_next_first)).setOnClickListener(maintenanceListener);
			((Button) layout1.findViewById(R.id.btn_maintenance_back_first)).setOnClickListener(maintenanceListener);
			((Button) layout1.findViewById(R.id.btn_maintenance_add_material)).setOnClickListener(maintenanceListener);
			((Button) layout1.findViewById(R.id.btn_upload_maintenance)).setOnClickListener(maintenanceListener);
			EditText tempAM = (EditText) layout1.findViewById(R.id.temp_am);
			EditText tempPM = (EditText) layout1.findViewById(R.id.temp_pm);
			EditText tempEOD = (EditText) layout1.findViewById(R.id.temp_eod);
			(((EditText) layout1.findViewById(R.id.temp_am))).setText(tempAm);
			(((EditText) layout1.findViewById(R.id.temp_pm))).setText(tempPm);
			(((EditText) layout1.findViewById(R.id.temp_eod))).setText(tempEod);
			((Spinner) layout1.findViewById(R.id.weather_type)).setSelection(weatherPosition);
			String tempUnit = Session.getTemperatureUnit();
			if (tempUnit == Company.FARENHEIT) {
				tempAM.setHint("in farenheit");
				tempPM.setHint("in farenheit");
				tempEOD.setHint("in farenheit");
			} else {
				tempAM.setHint("in celsius");
				tempPM.setHint("in celsius");
				tempEOD.setHint("in celsius");
			}

			LoadMaintenanceOne();
			break;
		case 2:
			layout1 = inflater.inflate(R.layout.dialog_maintenance_2, null);
			((Button) layout1.findViewById(R.id.btn_maintenance_next_second)).setOnClickListener(maintenanceListener);
			((Button) layout1.findViewById(R.id.btn_maintenance_back_second)).setOnClickListener(maintenanceListener);
			LoadMaintenanceTwo();
			break;

		default:
			dialog.dismiss();
			break;
		}

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		if (viewNumber != 0)
			dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	}

	protected String getWorkTime(String timeIn, String timeOut) {
		try {
			long diff = df.parse(timeOut).getTime() - df.parse(timeIn).getTime();
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000);

			return diffHours + ":" + diffMinutes;
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return "NAN";
	}

	/**
	 * Populate data to maintenance main form
	 */
	private void LoadMaintenanceOne() {
		tvLeftToDistribute = (TextView) layout1.findViewById(R.id.tv_left_to_assign);
		tvLeftToDistribute.setText(employeeWorkTime);
		loadMaintenanceDetails();

		if (contract != null) {
			List<ContractServices> contractServices = contract.listServices();
			for (ContractServices service : contractServices) {
				if (!taskList.contains(service)) {
					taskList.add(service);
				}
			}
			/**
			 * ScrollView Inside another ScrollView is not allowed in Android below code handles the
			 * scrolling of child and parent Scroll
			 */
			ScrollView parentScrollView = (ScrollView) layout1.findViewById(R.id.sv_maintenance_1);
			ScrollView childScrollView = (ScrollView) layout1.findViewById(R.id.child_maintenance_one);
			parentScrollView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					layout1.findViewById(R.id.child_maintenance_one).getParent().requestDisallowInterceptTouchEvent(false);
					return false;
				}
			});
			childScrollView.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// Disallow the touch request for parent scroll on touch of
					// child view
					v.getParent().requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
			Spinner employeePicker = (Spinner) layout1.findViewById(R.id.employee_picker);
			final ArrayList<String> list = new ArrayList<String>();
			list.add("All");
			for (User user : employeeList) {
				list.add(user.getFirstName());

			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			employeePicker.setAdapter(dataAdapter);
			employeePicker.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
					if (pos != 0) {
						maintenanceEmployee.clear();
						maintenanceEmployee.add(employeeList.get(pos - 1).getId());
					} else {
						maintenanceEmployee.clear();
						for (User user : employeeList) {
							maintenanceEmployee.add(user.getId());
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
				}
			});

			maintainanceViewOne = new View[taskList.size()];

			ViewGroup group = (ViewGroup) layout1.findViewById(R.id.sv_employee_detail);

			for (count = 0; count < taskList.size(); count++) {
				maintainanceViewOne[count] = inflater.inflate(R.layout.foreman_maintenance_list, null);

				final TextView tvVal = ((TextView) maintainanceViewOne[count].findViewById(R.id.tv_feq_value));
				final Button btnInc = ((Button) maintainanceViewOne[count].findViewById(R.id.btn_feq_inc));
				final Button btnDec = ((Button) maintainanceViewOne[count].findViewById(R.id.btn_feq_dec));

				((TextView) maintainanceViewOne[count].findViewById(R.id.tv_feq_eqp_num)).setText(taskList.get(count).getProductName());
				((TextView) maintainanceViewOne[count].findViewById(R.id.tv_feq_eqp_name)).setText(PER_EVENT);

				btnInc.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (workTimeinQuarters > 15) {
							workTimeinQuarters = workTimeinQuarters - 15;
						} else {
							tvLeftToDistribute.setText(getTimefromMinutes(0));
							int val = Integer.parseInt(getMinutesFromTime(tvVal.getText().toString()));
							tvVal.setText(getTimefromMinutes((workTimeinQuarters + val)));
							workTimeinQuarters = 0;
							return;
						}
						if (0 <= workTimeinQuarters && workTimeinQuarters <= topLimit) {
							tvLeftToDistribute.setText(getTimefromMinutes(workTimeinQuarters));
							int val = Integer.parseInt(getMinutesFromTime(tvVal.getText().toString()));
							tvVal.setText(getTimefromMinutes((15 + val)));
						} else {
							Toast.makeText(context, "Left to Distribute less than 15 minutes", Toast.LENGTH_LONG).show();
						}
					}
				});

				btnDec.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int val = Integer.parseInt(getMinutesFromTime(tvVal.getText().toString()));
						if (val >= 15) {
							tvVal.setText(getTimefromMinutes((val - 15)));
							workTimeinQuarters = workTimeinQuarters + 15;
							if (0 <= workTimeinQuarters && workTimeinQuarters <= topLimit)
								tvLeftToDistribute.setText(getTimefromMinutes(workTimeinQuarters));
						} else if (val > 0 && val < 15) {
							tvVal.setText(getTimefromMinutes((0)));
							workTimeinQuarters = workTimeinQuarters + val;
							if (0 <= workTimeinQuarters && workTimeinQuarters <= topLimit)
								tvLeftToDistribute.setText(getTimefromMinutes(workTimeinQuarters));
						}
					}

				});

				if (worksheetMaintenanceTask.size() > 0) {
					if (worksheetMaintenanceTask.get(count) != null)
						tvVal.setText(worksheetMaintenanceTask.get(count).getDuration());
				}

				group.addView(maintainanceViewOne[count]);
			}

		}

	}

	private String getMinutesFromTime(String string) {
		String array[] = string.split(":");
		int minutes = Integer.parseInt(array[0]) * 60 + Integer.parseInt(array[1]);
		String temp = minutes + "";
		return temp;
	}

	private void LoadMaintenanceTwo() {
		loadMaintenanceDetails();
		final EditText searchProductName = (EditText) layout1.findViewById(R.id.et_search);

		final ScrollView listScroll = (ScrollView) layout1.findViewById(R.id.sv_search);

		ProductsDao productsDao = new ProductsDao();
		materialList = productsDao.listProducts();
		maintainanceViewTwo = new View[materialList.size()];

		final ViewGroup group = (ViewGroup) layout1.findViewById(R.id.sv_equipment);

		for (count = 0; count < materialList.size(); count++) {
			maintainanceViewTwo[count] = inflater.inflate(R.layout.foreman_product_list, null);

			final TextView tvVal = ((TextView) maintainanceViewTwo[count].findViewById(R.id.tv_feq_value));
			final Button btnInc = ((Button) maintainanceViewTwo[count].findViewById(R.id.btn_feq_inc));
			final Button btnDec = ((Button) maintainanceViewTwo[count].findViewById(R.id.btn_feq_dec));
			String productCode = materialList.get(count).getProduct_code();
			if (productCode.equals(""))
				productCode = "not available";

			((TextView) maintainanceViewTwo[count].findViewById(R.id.field_one)).setText(productCode);
			((TextView) maintainanceViewTwo[count].findViewById(R.id.field_two)).setText(materialList.get(count).getName());
			String uom_id = materialList.get(count).getUom_id();

			// unit of measure table not available for SB using static values
			// temporarily
			if (uom_id.equals("5065e5ed-3f68-4fb9-89a3-6a90ae8ed672")) {
				uom_id = "Tonne";
			} else if (uom_id.equals("50ad0a6d-a8ac-4907-9e8a-0bd6ae8ed672")) {
				uom_id = "Per Bag";
			} else if (uom_id.equals("5065e5da-2d80-402a-80a5-6a90ae8ed672")) {
				uom_id = "Km";
			} else if (uom_id.equals("50ad0a96-4950-4250-aeb8-0c8dae8ed672")) {
				uom_id = "Per Hour";
			} else if (uom_id.equals("5065e5e4-ea20-433d-a152-6a90ae8ed672")) {
				uom_id = "Par Occurence";
			} else if (uom_id.equals("50b6379f-0084-49c0-aa26-2b3cae8ed672")) {
				uom_id = "Événement";
			} else {
				uom_id = "NA";
			}
			((TextView) maintainanceViewTwo[count].findViewById(R.id.field_three)).setText(uom_id);

			btnInc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int val = Integer.parseInt(tvVal.getText().toString());
					tvVal.setText((++val) + "");
				}
			});

			btnDec.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int val = Integer.parseInt(tvVal.getText().toString());
					if (val > 0)
						tvVal.setText((--val) + "");
				}
			});
			maintainanceViewTwo[count].setFocusable(true);
			maintainanceViewTwo[count].setFocusableInTouchMode(true);
			group.addView(maintainanceViewTwo[count]);

		}

		searchProductName.addTextChangedListener((new TextWatcher() {
			int searchPos = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchWord = searchProductName.getText().toString();
				searchPos = getPositionFromText(searchWord);
				final int x;
				final int y;
				x = maintainanceViewTwo[searchPos].getLeft();
				y = maintainanceViewTwo[searchPos].getTop();

				listScroll.post(new Runnable() {
					@Override
					public void run() {
						listScroll.scrollTo(x, y);
					}
				});
			}

			private int getPositionFromText(String searchWord) {
				for (int i = 0; i < materialList.size(); i++) {
					Locale loc = Locale.getDefault();
					if (materialList.get(i).getName().toLowerCase(loc).startsWith(searchWord)) {
						return i;
					}
				}
				return searchPos;
			}

		}));

	}

	private void loadMaintenanceDetails() {
		(((TextView) layout1.findViewById(R.id.tv_service_location_adrs))).setText(SlAdrs);
		(((TextView) layout1.findViewById(R.id.tv_job_number))).setText(jobNum);
		(((TextView) layout1.findViewById(R.id.tv_enroute_time))).setText(enrouteTime);
		(((TextView) layout1.findViewById(R.id.tv_arrived_time))).setText(arrivedTime);
		(((TextView) layout1.findViewById(R.id.tv_completed_time))).setText(completeTime);
		(((TextView) layout1.findViewById(R.id.tv_dfd_date))).setText(cDate);
		(((TextView) layout1.findViewById(R.id.tv_trailer))).setText(trailerName);
		(((TextView) layout1.findViewById(R.id.tv_travel_distance))).setText(travelDistance + "");
		(((TextView) layout1.findViewById(R.id.tv_travel_time))).setText(employeeTravelTime);
		(((TextView) layout1.findViewById(R.id.tv_on_site_time))).setText(employeeWorkTime);
	}

	public static void setImageUriMaintenence(String selectedImage) {
		imgUriMaintenence = selectedImage;

	}

	private String getText(int txtid) {
		TextView txt = (TextView) layout1.findViewById(txtid);
		if (txt == null || txt.getText() == null)
			return null;
		return txt.getText().toString();
	}

	private void saveDetails() {
		switch (viewNumber) {
		case 1:

			try {
				worksheetMaintenance.setCompleted(((TextView) layout1.findViewById(R.id.tv_completed_time)).getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				worksheetMaintenance.setEmployeeId(userDao.getUserIdForPin(Session.getUserPin()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Fixe2.11 #44
			/*
						worksheetMaintenance.setNotes(((EditText) layout1.findViewById(R.id.et_dfd_descwork)).getText().toString());
						worksheetMaintenance.setOnSiteTime(((TextView) layout1.findViewById(R.id.tv_on_site_time)).getText().toString());
						worksheetMaintenance.setTempAm(((EditText) layout1.findViewById(R.id.temp_am)).getText().toString());
						worksheetMaintenance.setTempPm(((EditText) layout1.findViewById(R.id.temp_pm)).getText().toString());
						worksheetMaintenance.setTempEod(((EditText) layout1.findViewById(R.id.temp_eod)).getText().toString());
						worksheetMaintenance.setWeatherTypes(((Spinner) layout1.findViewById(R.id.weather_type)).getSelectedItem().toString());
						worksheetMaintenance.setJobPhotos(imgUriMaintenence);
			*/
			try {
				worksheetMaintenance.setNotes(getText(R.id.et_dfd_descwork));
				worksheetMaintenance.setOnSiteTime(getText(R.id.tv_on_site_time));
				worksheetMaintenance.setTempAm(getText(R.id.temp_am));
				worksheetMaintenance.setTempPm(getText(R.id.temp_pm));
				worksheetMaintenance.setTempEod(getText(R.id.temp_eod));
				Object selectedItem = null;
				Spinner spinner = ((Spinner) layout1.findViewById(R.id.weather_type));
				if (spinner != null) {
					selectedItem = spinner.getSelectedItem();
				}
				worksheetMaintenance.setWeatherTypes(selectedItem == null ? null : selectedItem.toString());
				worksheetMaintenance.setJobPhotos(imgUriMaintenence);

			} catch (Exception e) {
				e.printStackTrace();
			}

			worksheetMaintenanceTask = new ArrayList<WorksheetMaintenanceTasks>();
			for (count = 0; count < taskList.size(); count++) {
				WorksheetMaintenanceTasks temp = new WorksheetMaintenanceTasks();
				temp.setCompanyId(companyId);
				temp.setTaskcode(PER_EVENT);
				temp.setDuration((((TextView) maintainanceViewOne[count].findViewById(R.id.tv_feq_value)).getText().toString()));
				temp.setTaskname(taskList.get(count).getProductName());
				temp.setProduct_id(taskList.get(count).getProductId());
				temp.setContract_service_id(taskList.get(count).getId());
				worksheetMaintenanceTask.add(temp);
			}

			worksheetMaintenance.setWorksheetMaintenanceTask(worksheetMaintenanceTask);
			worksheetMaintenance.setWorksheetProductList(worksheetMaintenancesProducts);
			MaintenancesPushSync maintenancePushSync = new MaintenancesPushSync();
			maintenancePushSync.pushData(context, worksheetMaintenance);

			updateServiceActivity(serviceActivity, ServiceActivity.SA_COMPLETED);
			PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();
			poiMgr.markServiceLocationAsCompletedNow(serviceActivity.getServiceLocation());
			int routeSlPosition = Session.getRouteSlPosition();
			routeSlPosition++;
			if (Session.route != null) {
				Session.setRouteSlPosition(routeSlPosition);
				ServiceLocation sl = poiMgr.routeSlList.get(routeSlPosition);
				PointOfInterestActionListener actionListener = new PointOfInterestActionHandler(context);
				OsmandMapTileView view = new OsmandMapTileView(context);
				PointOfInterestMenu menu = new PointOfInterestMenu(actionListener, view);
				PointOfInterest poi = new PointOfInterest(sl.getId());
				poi.attachServiceLocation(sl);
				if (Session.route.getPopUp().equals(Route.SHOW_POPUP)) {
					menu.createDialog(poi);
				}
			}

			dialog.dismiss();
			break;
		case 2:
			worksheetMaintenancesProducts = new ArrayList<WorksheetMaintenanceProducts>();
			for (count = 0; count < maintainanceViewTwo.length; count++) {
				String quantity = ((TextView) maintainanceViewTwo[count].findViewById(R.id.tv_feq_value)).getText().toString();
				if (!quantity.equals("0")) {
					WorksheetMaintenanceProducts temp = new WorksheetMaintenanceProducts();
					temp.setCode(materialList.get(count).getProduct_code());
					temp.setName(materialList.get(count).getName());
					temp.setProductId(materialList.get(count).getId());
					temp.setQuantity(quantity);
					temp.setCompany_id(companyId);
					worksheetMaintenancesProducts.add(temp);
				}
			}
			break;
		default:
			dialog.dismiss();
			break;
		}
	}

	public void updateServiceActivity(ServiceActivity sa, String statusCodeId) {
		if (Session.getDriver() != null) {
			sa.setUserId(Session.getDriver().getId());
		} else {
			Log.e("PoiActionHandler", "Session.driver is NULL");
		}
		if (Session.getVehicle() != null) {
			sa.setVehicleId(Session.getVehicle().getId());
		} else {
			Log.e("PoiActionHandler", "Session.vehicle is NULL");
		}
		sa.setStatus(statusCodeId, true);

		// Push the update to the server (and our local database)
		saPush.pushData(context, sa);
		PointOfInterestManager poiMgr = PointOfInterestManager.getInstance();

		try {
			poiMgr.removeServiceActivity(sa);
		} catch (RuntimeException re) {
			Log.e("PoiActionHandler", "Failed to update SA " + sa.getId() + " to status " + statusCodeId, re);
		}
	}

	private String getTimefromMinutes(int t) {
		String time;
		int hours = t / 60;
		int minutes = t % 60;
		if (minutes == 0)
			time = hours + ":" + "00";
		else
			time = hours + ":" + minutes + "";
		return time;
	}

	public OnClickListener maintenanceListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_maintenance_add_material:
				tempAm = ((EditText) layout1.findViewById(R.id.temp_am)).getText().toString();
				tempPm = ((EditText) layout1.findViewById(R.id.temp_pm)).getText().toString();
				tempEod = ((EditText) layout1.findViewById(R.id.temp_eod)).getText().toString();
				weatherPosition = ((Spinner) layout1.findViewById(R.id.weather_type)).getSelectedItemPosition();
				employeeWorkTime = ((TextView) layout1.findViewById(R.id.tv_left_to_assign)).getText().toString();

				worksheetMaintenanceTask = new ArrayList<WorksheetMaintenanceTasks>();
				for (count = 0; count < taskList.size(); count++) {
					WorksheetMaintenanceTasks temp = new WorksheetMaintenanceTasks();
					temp.setCompanyId(companyId);
					temp.setTaskcode(PER_EVENT);
					temp.setDuration((((TextView) maintainanceViewOne[count].findViewById(R.id.tv_feq_value)).getText().toString()));
					temp.setTaskname(taskList.get(count).getProductName());
					temp.setProduct_id(taskList.get(count).getProductId());
					temp.setContract_service_id(taskList.get(count).getId());
					worksheetMaintenanceTask.add(temp);
				}

				viewNumber++;
				dialog.dismiss();
				if (viewNumber < 3)
					createDialog();
				else {
					viewNumber = 1;
				}
				break;
			case R.id.btn_maintenance_next_first:
				saveDetails();
				break;
			case R.id.btn_maintenance_next_second:
				saveDetails();
			case R.id.btn_maintenance_back_second:
				viewNumber = 1;
				dialog.dismiss();
				createDialog();
				break;
			case R.id.btn_upload_maintenance:
				Session.MapAct.startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 2);
				break;

			default:
				break;
			}
		}
	};
}
