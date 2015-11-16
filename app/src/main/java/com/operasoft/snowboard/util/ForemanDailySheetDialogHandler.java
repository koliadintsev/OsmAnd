package com.operasoft.snowboard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractsDao;
import com.operasoft.snowboard.database.Divisions;
import com.operasoft.snowboard.database.DivisionsDao;
import com.operasoft.snowboard.database.EquipmentTypes;
import com.operasoft.snowboard.database.EquipmentTypesDao;
import com.operasoft.snowboard.database.Products;
import com.operasoft.snowboard.database.ProductsDao;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.PunchDao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.database.WorksheetEmployeeLogs;
import com.operasoft.snowboard.database.WorksheetEquipmentsOld;
import com.operasoft.snowboard.database.WorksheetMaintenance;
import com.operasoft.snowboard.database.WorksheetMaintenanceProducts;
import com.operasoft.snowboard.database.WorksheetMaintenanceTasks;
import com.operasoft.snowboard.database.WorksheetSubContractors;
import com.operasoft.snowboard.database.Worksheets;
import com.operasoft.snowboard.dbsync.push.ForemanDailyWorksheetPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestManager;

/**
 * TODO GET RID OF THIS CLASS 
 * @deprecated Worksheets have been reimplemented using a different approach
 */
public class ForemanDailySheetDialogHandler {

	final static int FORM_DEFAULT = 0;
	final static int FORM_GENERAL_INFORMATION = 1;
	final static int FORM_EQUIPMENT_UTILISATION = 2;
	final static int FORM_EMPLOYEE_TIME_LOG = 3;
	final static int FORM_SUB_CONTRACTOR_INFO = 4;
	final static int FORM_ACCIDENT_REPORT = 5;

	private final Context context;
	private Dialog dialog;
	private LayoutInflater inflater;
	private View layout1;
	private int viewNumber = 1;
	private AlertDialog.Builder builder;
	private int count = 0;
	private String jobNum, jobName, SlAdrs, SlId, cDate, companyId;
	private final Date date;
	private final SimpleDateFormat dateFormat, dateTimeFormat;
	private final PunchDao punchDao;

	private List<EquipmentTypes> equipmentList;
	private List<Punch> punchList;
	private List<Products> materialList;
	private List<Products> productList;
	private View[] equipmentViewRow, employeeViewRow, materialViewRow, productViewRow;

	private Worksheets worksheets;
	private ArrayList<WorksheetEquipmentsOld> worksheetEquipmentsOld;
	private ArrayList<WorksheetEmployeeLogs> worksheetEmployeeLogs;
	private ArrayList<WorksheetMaintenanceProducts> worksheetMaintenanceProducts;
	private ArrayList<WorksheetMaintenance> worksheetMaintenances;
	private final ArrayList<WorksheetMaintenanceTasks> worksheetMaintenanceTask = new ArrayList<WorksheetMaintenanceTasks>();
	private ArrayList<WorksheetSubContractors> worksheetSubContractor;
	// private ArrayList<WorksheetEmployeeDetails> worksheetEmployeeDetails =
	// new ArrayList<WorksheetEmployeeDetails>();
	private ViewGroup contractorViewGroup;
	private static String imgUri = null;
	private String employeeName;
	private String employeeId;
	private String employeeWorkTime;
	private int workTime;
	private int topLimit;
	private String userCompany;
	private final String enrouteTime = "not available";
	private String arrivedTime;
	private String completeTime;
	private ServiceActivity serviceActivity;
	private final ServiceActivityDao saDao;
	private static String imgUriMaintenence = null;
	private final ArrayList<String> taskList = new ArrayList<String>();
	private final ArrayList<String> maintenanceEmployee = new ArrayList<String>();
	private String maintenanceUserCompany;
	private final String employeeTravelTime = "not available";
	private final ArrayList<String> taskCodeList = new ArrayList<String>();
	private ArrayList<Divisions> allDivisionsList = new ArrayList<Divisions>();
	private final ContractsDao contractsDao;
	private final UsersDao usersDao;
	private String trailerName;
	private final ServiceLocationDao slDao;
	private ServiceLocation Slocation;
	private Contract contract;
	private String divisionId = "";
	private SeekBar tempSeekBar;
	private TextView tvTemp;
	private Spinner weatherType;
	private final PointOfInterest mPoi;

	@SuppressLint("SimpleDateFormat")
	public ForemanDailySheetDialogHandler(Context context, PointOfInterest poi) {

		this.context = context;
		this.mPoi = poi;

		date = new Date();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		punchDao = new PunchDao();
		saDao = new ServiceActivityDao();
		contractsDao = new ContractsDao();
		usersDao = new UsersDao();
		slDao = new ServiceLocationDao();

		if (Session.getTrailer() != null)
			trailerName = Session.getTrailer().getName();
		else
			trailerName = "none";
	}

	public void createDialog() {
		dialog = new Dialog(context);
		builder = new AlertDialog.Builder(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String userId = usersDao.getUserIdForPin(Session.getUserPin());
		SlId = mPoi.getSlId();
		if (SlId == null)
			SlId = "";

		if (!SlId.equalsIgnoreCase("")) {
			Slocation = slDao.getById(SlId);
			if (viewNumber == 1) {
				ContractsDao contractDao = new ContractsDao();
				List<Contract> contracts = contractDao.getActiveContractForServiceLocation(SlId, Session.getCurrentSeason(), PointOfInterestManager.getInstance().getContractType());
				contract = contracts.get(0);
				if (contract != null) {
					DivisionsDao divisionsDao = new DivisionsDao();
					String divisionId = contract.getDivisionId();
					allDivisionsList = divisionsDao.getAllDivisionsForContract(divisionId);
					jobNum = contract.getJobNumber();
					if (jobNum == null)
						jobNum = contract.getId();
					jobName = contract.getContract_name();
				}
				SlAdrs = Slocation.getAddress();
				Punch punchIn = punchDao.getPunchFromUserId(userId);
				arrivedTime = punchIn.getDateTime();
				completeTime = punchDao.getOutTimefromUserId(dateFormat.format(date), SlId, userId, arrivedTime);
				employeeWorkTime = getWorkTime(arrivedTime, completeTime);
				cDate = dateFormat.format(date);
				companyId = Session.getCompanyId();
				if (allDivisionsList.size() == 0)
					viewNumber = 1;
			}
		} else {
			if (viewNumber == 0)
				viewNumber = 1;
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
		case FORM_DEFAULT:
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
						divisionId = allDivisionsList.get(pos - 1).getId();
						createDialog();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			break;

		case FORM_GENERAL_INFORMATION:
			// Setting values at once when user open's the form.
			setWorksheetDetails();
			imgUri = null;

			layout1 = inflater.inflate(R.layout.dialog_forman_daily_1, null);
			((Button) layout1.findViewById(R.id.btn_next_first)).setOnClickListener(foremanDailySheetListener);
			((Button) layout1.findViewById(R.id.btn_back_first)).setOnClickListener(foremanDailySheetListener);
			((Button) layout1.findViewById(R.id.btn_upload)).setOnClickListener(foremanDailySheetListener);
			EditText tempAM = (EditText) layout1.findViewById(R.id.temp_am);
			EditText tempPM = (EditText) layout1.findViewById(R.id.temp_pm);
			EditText tempEOD = (EditText) layout1.findViewById(R.id.temp_eod);
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
			LoadWorksheet();
			break;

		case FORM_EQUIPMENT_UTILISATION:
			layout1 = inflater.inflate(R.layout.dialog_forman_daily_2, null);
			((Button) layout1.findViewById(R.id.btn_next_second)).setOnClickListener(foremanDailySheetListener);
			((Button) layout1.findViewById(R.id.btn_back_second)).setOnClickListener(foremanDailySheetListener);
			LoadEquipmentList();
			break;

		case FORM_EMPLOYEE_TIME_LOG:
			layout1 = inflater.inflate(R.layout.dialog_forman_daily_3, null);
			((Button) layout1.findViewById(R.id.btn_next_third)).setOnClickListener(foremanDailySheetListener);
			((Button) layout1.findViewById(R.id.btn_back_third)).setOnClickListener(foremanDailySheetListener);

			punchList = new ArrayList<Punch>();
			if (!SlId.equals(""))
				punchList = punchDao.listTeamEmployees(cDate, SlId);
			LoadEmployeeList();
			break;

		case FORM_SUB_CONTRACTOR_INFO:
			layout1 = inflater.inflate(R.layout.dialog_forman_daily_5, null);
			((Button) layout1.findViewById(R.id.btn_next_fifth)).setOnClickListener(foremanDailySheetListener);
			((Button) layout1.findViewById(R.id.btn_back_fifth)).setOnClickListener(foremanDailySheetListener);
			LoadContractorDetails();

			((Button) layout1.findViewById(R.id.btn_add_more_fifth)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AddContractorDetails();
				}
			});
			break;

		case FORM_ACCIDENT_REPORT:
			layout1 = inflater.inflate(R.layout.dialog_forman_daily_6, null);
			((Button) layout1.findViewById(R.id.btn_next_sixth)).setOnClickListener(foremanDailySheetListener);
			((Button) layout1.findViewById(R.id.btn_back_sixth)).setOnClickListener(foremanDailySheetListener);
			LoadContractorDetails();
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

	private void LoadWorksheet() {
		loadDetails();
	}

	private void LoadEquipmentList() {
		loadDetails();
		ScrollView parentScrollView = (ScrollView) layout1.findViewById(R.id.foreman_parent_2);
		ScrollView childScrollView = (ScrollView) layout1.findViewById(R.id.foreman_child_2);
		parentScrollView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				layout1.findViewById(R.id.foreman_child_2).getParent().requestDisallowInterceptTouchEvent(false);
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
		EquipmentTypesDao equipmentTypesDao = new EquipmentTypesDao();
		equipmentList = equipmentTypesDao.listAll();
		equipmentViewRow = new View[equipmentList.size()];

		ViewGroup group = (ViewGroup) layout1.findViewById(R.id.sv_equipment);

		for (count = 0; count < equipmentList.size(); count++) {
			equipmentViewRow[count] = inflater.inflate(R.layout.foreman_equipment_list, null);

			final TextView tvVal = ((TextView) equipmentViewRow[count].findViewById(R.id.tv_feq_value));
			final Button btnInc = ((Button) equipmentViewRow[count].findViewById(R.id.btn_feq_inc));
			final Button btnDec = ((Button) equipmentViewRow[count].findViewById(R.id.btn_feq_dec));

			((TextView) equipmentViewRow[count].findViewById(R.id.tv_feq_eqp_num)).setText(equipmentList.get(count).getEquipmentNumber());
			((TextView) equipmentViewRow[count].findViewById(R.id.tv_feq_eqp_name)).setText(equipmentList.get(count).getEquipmentName());

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

			group.addView(equipmentViewRow[count]);

		}

	}

	private void LoadEmployeeList() {
		loadDetails();
		employeeViewRow = new View[punchList.size()];

		ViewGroup group = (ViewGroup) layout1.findViewById(R.id.sv_employee);

		for (count = 0; count < employeeViewRow.length; count++) {
			employeeViewRow[count] = inflater.inflate(R.layout.foreman_employee_list, null);

			UsersDao usersDao = new UsersDao();
			String userName = usersDao.getById(punchList.get(count).getUserId()).getFirstName();
			String userId = usersDao.getById(punchList.get(count).getUserId()).getId();
			userCompany = usersDao.getById(punchList.get(count).getUserId()).getCompany_id();

			final TextView tvempName = ((TextView) employeeViewRow[count].findViewById(R.id.tv_femp_name));
			final EditText etIn = ((EditText) employeeViewRow[count].findViewById(R.id.et_femp_in));
			final EditText etOut = ((EditText) employeeViewRow[count].findViewById(R.id.et_femp_out));
			final TextView tvTime = ((TextView) employeeViewRow[count].findViewById(R.id.tv_femp_time));

			etIn.setText(punchList.get(count).getDateTime());
			etOut.setText(punchDao.getOutTimefromUserId(cDate, SlId, punchList.get(count).getUserId(), punchList.get(count).getDateTime()));
			tvempName.setText(userName);
			tvempName.setTag(userId);
			tvTime.setText(getWorkTime(etIn.getText().toString(), etOut.getText().toString()));

			etIn.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					tvTime.setText(getWorkTime(etIn.getText().toString(), etOut.getText().toString()));
				}
			});

			etOut.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					tvTime.setText(getWorkTime(etIn.getText().toString(), etOut.getText().toString()));
				}
			});

			tvempName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					employeeName = tvempName.getText().toString();
					employeeId = tvempName.getTag().toString();
					employeeWorkTime = tvTime.getText().toString();
					// viewNumber = 31;
					// dialog.dismiss();
					// createDialog();
				}
			});

			group.addView(employeeViewRow[count]);
		}
	}

	protected String getWorkTime(String timeIn, String timeOut) {
		try {
			long diff = dateTimeFormat.parse(timeOut).getTime() - dateTimeFormat.parse(timeIn).getTime();
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000);

			return diffHours + ":" + diffMinutes;
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return "NAN";
	}

	private void LoadEmployeeDetails() {
		loadDetails();

		TextView tvEmployeeName = (TextView) layout1.findViewById(R.id.employeename);
		TextView tvEmployeeWorkTime = (TextView) layout1.findViewById(R.id.work_time);
		final TextView tvLeftToAssign = (TextView) layout1.findViewById(R.id.left_to_assign);

		// setting values
		tvEmployeeName.setText(employeeName);
		tvEmployeeWorkTime.setText(employeeWorkTime);
		tvLeftToAssign.setText(employeeWorkTime);

		ProductsDao productsDao = new ProductsDao();
		productList = productsDao.listProducts();
		productViewRow = new View[productList.size()];

		ViewGroup group = (ViewGroup) layout1.findViewById(R.id.sv_employee_detail);

		for (count = 0; count < productList.size(); count++) {
			productViewRow[count] = inflater.inflate(R.layout.foreman_employee_detail_list, null);
			((TextView) productViewRow[count].findViewById(R.id.equipment_name)).setText(productList.get(count).getName());
			final TextView tvVal = ((TextView) productViewRow[count].findViewById(R.id.tv_fed_value));
			final Button btnInc = ((Button) productViewRow[count].findViewById(R.id.btn_fed_inc));
			final Button btnDec = ((Button) productViewRow[count].findViewById(R.id.btn_fed_dec));
			if (!employeeWorkTime.equalsIgnoreCase("NAN")) {
				workTime = Integer.parseInt(tvLeftToAssign.getText().toString());
				topLimit = workTime;
			}
			btnInc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int val = Integer.parseInt(tvVal.getText().toString());
					tvVal.setText((++val) + "");
					if (!employeeWorkTime.equalsIgnoreCase("NAN")) {
						workTime--;
						if (0 <= workTime && workTime <= topLimit)
							tvLeftToAssign.setText(workTime + "");
					}
				}
			});

			btnDec.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int val = Integer.parseInt(tvVal.getText().toString());
					if (val > 0) {
						tvVal.setText((--val) + "");
						if (!employeeWorkTime.equalsIgnoreCase("NAN")) {
							workTime++;
							if (0 <= workTime && workTime <= topLimit)
								tvLeftToAssign.setText(workTime + "");
						}
					}
				}
			});
			group.addView(productViewRow[count]);
		}
	}

	private void LoadContractorDetails() {
		loadDetails();
		contractorViewGroup = (ViewGroup) layout1.findViewById(R.id.sv_contractor_detail);
	}

	private void AddContractorDetails() {

		View tempRow = inflater.inflate(R.layout.foreman_contractor_list, null);
		contractorViewGroup.addView(tempRow);
	}

	/**
	 * initialize the form heaser values.
	 */
	private void setWorksheetDetails() {
		// if ((SlId != null) && (!SlId.equals(""))) {
	}

	private void loadDetails() {
		(((TextView) layout1.findViewById(R.id.tv_dfd_job_number))).setText(jobNum);
		(((TextView) layout1.findViewById(R.id.tv_dfd_date))).setText(cDate);
		(((TextView) layout1.findViewById(R.id.tv_dfd_job_name))).setText(jobName);
		(((TextView) layout1.findViewById(R.id.tv_dfd_job_address))).setText(SlAdrs);
	}

	public static void setImageUri(String selectedImage) {
		imgUri = selectedImage;
	}

	public static void setImageUriMaintenence(String selectedImage) {
		imgUriMaintenence = selectedImage;

	}

	private void saveDetails() {
		switch (viewNumber) {
		case FORM_GENERAL_INFORMATION:
			worksheets = new Worksheets();
			worksheets.setVisitors(((EditText) layout1.findViewById(R.id.et_dfd_visitor)).getText().toString());
			worksheets.setJob_meeting_notes(((EditText) layout1.findViewById(R.id.et_dfd_jobmeetingsnotes)).getText().toString());
			worksheets.setDesc_work_performed(((EditText) layout1.findViewById(R.id.et_dfd_descwork)).getText().toString());
			worksheets.setCompanyId(Session.getCompanyId());
			worksheets.setDate(cDate);
			worksheets.setJob_name(jobName);
			worksheets.setJob_number(jobNum);
			worksheets.setDivisionId(divisionId);
			worksheets.setCompletedBy(usersDao.getUserIdForPin(Session.getUserPin()));
			worksheets.setContractId(contract.getId());

			worksheets.setTempAm(((EditText) layout1.findViewById(R.id.temp_am)).getText().toString());
			worksheets.setTempPm(((EditText) layout1.findViewById(R.id.temp_pm)).getText().toString());
			worksheets.setTempEod(((EditText) layout1.findViewById(R.id.temp_eod)).getText().toString());
			worksheets.setWeatherType(((Spinner) layout1.findViewById(R.id.weather_type)).getSelectedItem().toString());
			if (imgUri != null)
				worksheets.setDaily_photos(imgUri + "");
			worksheets.setService_location_id(SlId);
			break;

		case FORM_EQUIPMENT_UTILISATION:
			worksheetEquipmentsOld = new ArrayList<WorksheetEquipmentsOld>();
			for (count = 0; count < equipmentViewRow.length; count++) {
				int hoursUsed = Integer.parseInt(((TextView) equipmentViewRow[count].findViewById(R.id.tv_feq_value)).getText().toString());

				if (hoursUsed > 0) {
					WorksheetEquipmentsOld temp = new WorksheetEquipmentsOld();
					temp.setHoursUsed(hoursUsed + "");
					temp.setEquipmentName(equipmentList.get(count).getEquipmentName());
					temp.setEquipmentNumber(equipmentList.get(count).getEquipmentNumber());
					temp.setEquipmentId(equipmentList.get(count).getId());
					temp.setCompanyId(companyId);
					worksheetEquipmentsOld.add(temp);
				}
			}
			break;

		case FORM_EMPLOYEE_TIME_LOG:
			worksheetEmployeeLogs = new ArrayList<WorksheetEmployeeLogs>();
			for (count = 0; count < employeeViewRow.length; count++) {
				WorksheetEmployeeLogs temp = new WorksheetEmployeeLogs();
				temp.setPunch_in(((EditText) employeeViewRow[count].findViewById(R.id.et_femp_in)).getText().toString());
				temp.setPunch_out(((EditText) employeeViewRow[count].findViewById(R.id.et_femp_out)).getText().toString());
				temp.setEmp_name(((TextView) employeeViewRow[count].findViewById(R.id.tv_femp_name)).getText().toString());
				temp.setCompany_id(companyId);
				temp.setEmp_id(punchList.get(count).getUserId());

				worksheetEmployeeLogs.add(temp);
			}
			break;

		case FORM_SUB_CONTRACTOR_INFO:
			worksheetSubContractor = new ArrayList<WorksheetSubContractors>();

			String subCont = ((EditText) layout1.findViewById(R.id.et_fcl_main_name)).getText().toString();

			if (!subCont.equals("")) {
				WorksheetSubContractors tempMain = new WorksheetSubContractors();
				tempMain.setSub_contractor(subCont);
				tempMain.setWork_performence(((EditText) layout1.findViewById(R.id.et_fcl_main_work)).getText().toString());
				tempMain.setCompany_id(companyId);

				worksheetSubContractor.add(tempMain);
			}

			for (count = 0; count < contractorViewGroup.getChildCount(); count++) {
				subCont = ((EditText) contractorViewGroup.getChildAt(count).findViewById(R.id.et_fcl_name)).getText().toString();
				if (!subCont.equals("")) {
					WorksheetSubContractors temp = new WorksheetSubContractors();
					temp.setSub_contractor(subCont);
					temp.setWork_performence(((EditText) contractorViewGroup.getChildAt(count).findViewById(R.id.et_fcl_work)).getText().toString());
					temp.setCompany_id(companyId);

					worksheetSubContractor.add(temp);
				}
			}
			break;

		case FORM_ACCIDENT_REPORT:
			worksheets.setAccident_incident_notes(((EditText) layout1.findViewById(R.id.et_accident_report)).getText().toString());
			worksheets.setEquipmentsList(worksheetEquipmentsOld);
			worksheets.setEmployeeLogsList(worksheetEmployeeLogs);
			worksheets.setSubContractorsList(worksheetSubContractor);

			// Pushing data to the server.
			ForemanDailyWorksheetPushSync worksheetPushSync = ForemanDailyWorksheetPushSync.getInstance();
			worksheetPushSync.pushData(context, worksheets);
			PointOfInterestActionHandler actionListener = new PointOfInterestActionHandler(context);
			actionListener.serviceLocationCompletedNow(mPoi);

			dialog.dismiss();
			break;

		default:
			dialog.dismiss();
			break;
		}
	}

	/**
	 * this will create a sub dialog over the main dialog window. private void
	 * createChildDialog() { childDialog = new Dialog(context); childBuilder =
	 * new AlertDialog.Builder(context); childInflator = (LayoutInflater)
	 * context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); View layout =
	 * childInflator.inflate(R.layout.foreman_add_new_employee_list, null);
	 * Spinner spinner = ((Spinner) layout.findViewById(R.id.sp_fan_emp));
	 * List<String> list = new ArrayList<String>(); UsersDao usersDao = new
	 * UsersDao(); final List<User> users = usersDao.listAll(); for (User user :
	 * users) { list.add(user.getFirstName()); } ArrayAdapter<String>
	 * dataAdapter = new ArrayAdapter<String>(context,
	 * android.R.layout.simple_spinner_item, list);
	 * dataAdapter.setDropDownViewResource
	 * (android.R.layout.simple_spinner_dropdown_item);
	 * spinner.setAdapter(dataAdapter); spinner.setOnItemSelectedListener(new
	 * OnItemSelectedListener() {
	 * 
	 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int
	 *           arg2, long arg3) {
	 *           punchList.add(punchDao.getPunchFromUserId(users
	 *           .get(count).getId())); childDialog.dismiss();
	 *           ForemanDailySheetDialogHandler.this.dialog.show();
	 *           LoadEmployeeList(); }
	 * @Override public void onNothingSelected(AdapterView<?> arg0) { // TODO
	 *           Auto-generated method stub } }); // LoadWorksheet();
	 *           childBuilder.setView(layout); childDialog =
	 *           childBuilder.create(); childDialog.setCancelable(true);
	 *           childDialog.show(); }
	 */

	public OnClickListener foremanDailySheetListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.button_foreman_daily_sheet:
				createDialog();
				break;
			case R.id.btn_next_first:
			case R.id.btn_next_second:
			case R.id.btn_next_third:
			case R.id.btn_next_fourth:
			case R.id.btn_next_fifth:
			case R.id.btn_next_sixth:
				saveDetails();
				viewNumber++;
				dialog.dismiss();
				if (viewNumber < 6)
					createDialog();
				else {
					viewNumber = 1;
				}
				break;
			case R.id.btn_back_first:
			case R.id.btn_back_second:
			case R.id.btn_back_third:
			case R.id.btn_back_fourth:
			case R.id.btn_back_fifth:
			case R.id.btn_back_sixth:
				viewNumber--;
				dialog.dismiss();
				if (viewNumber > 0)
					createDialog();
				else
					viewNumber = 1;

				break;
			case R.id.btn_upload:
				Session.MapAct.startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
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
