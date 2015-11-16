package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Activity;
import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.database.Contract;
import com.operasoft.snowboard.database.ContractServices;
import com.operasoft.snowboard.database.ProductsDao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDetails;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.ServiceActivityDetailsPushSync;
import com.operasoft.snowboard.dbsync.push.ServiceActivityPushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionHandler;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;

public class SaCompletedCustomDialog implements OnClickListener {

	private static final String TAG = "SaCompletedCustomDialog";
	
	private Context mContext;
	private PointOfInterest mPoi;
	private Dialog dialog;
	private View addServiceActivityView;
	private final boolean newSA;
	private final PointOfInterestActionListener actionHandler;
	private LinearLayout llList;
	private int cSelect = 0;
	private Button btnNext, btnAddDetails;
	private EditText cEditText, jobNotes;
	private final ArrayList<EditText> editTextArray = new ArrayList<EditText>();
	private String titleText;
	private ServiceActivityDetails saDF = null;
	private SaDetailsFormCustomDialogHandler customDialogHandler;
	private List<String> lineItems;
	private final PointOfInterestActionHandler actionListener;

	public SaCompletedCustomDialog(Context context, PointOfInterest poi, PointOfInterestActionListener actionHandler, boolean newSA) {
		mContext = context;
		mPoi = poi;
		this.actionHandler = actionHandler;
		this.newSA = newSA;
		this.actionListener = new PointOfInterestActionHandler(context);
	}

	public void createDialog() {
		customDialogHandler = new SaDetailsFormCustomDialogHandler(mContext, mPoi);
		lineItems = new ArrayList<String>();
		ArrayList<String> pName = new ArrayList<String>();
		ArrayList<String> pUnit = new ArrayList<String>();
		ArrayList<Float> pQty  = new ArrayList<Float>();

		int dispatchCount = 0;
		titleText = "Add Service Activity";

		switch (mPoi.getStatus()) {

		case SERVICE_ACTIVITY_ACCEPTED:
		case SERVICE_ACTIVITY_IN_DIRECTION:
		case SERVICE_ACTIVITY_RECEIVED:
			titleText = "Complete Service Performed";
			List<Activity> saServicesList = mPoi.getCurrentServiceActivity().listRequestedServices();

			for (Activity saService : saServicesList) {
				if (saService.getService() != null) {
					pName.add(saService.getService().getProductName());
					pUnit.add(saService.getService().getUnitOfMeasure());
					pQty.add(saService.getService().getQuantity());
					lineItems.add(saService.getService().getId());
					dispatchCount++;
				} else {
					Log.e("SA Dialog", "Could not find service " + saService.getContractServiceId() + " in DB for activity " + saService.getId() + ", SA: " + mPoi.getCurrentServiceActivity().getId());
				}
			}
			break;

		default:
			break;
		}

		// while creating an SA on Snowboard, we need to display the list of contract_services
		// available for a given SL.
		Contract contract = mPoi.getContract();

		if (contract != null) {
			final List<ContractServices> contractServices = contract.listServices();

			ProductsDao.sortBySeasonEvent(contractServices);
			for (ContractServices service : contractServices) {
				if (!lineItems.contains(service.getId())) {

					service.getProductId();
					pName.add(service.getProductName());
					pUnit.add(service.getUnitOfMeasure());
					pQty.add(service.getQuantity());
					lineItems.add(service.getId());
				}
			}
		} else {
			showNoContractErrorMsg();
			return;
		}

		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock, (ViewGroup) dialog.findViewById(R.id.root_punch));
		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
		iMg_title_dialog.setImageResource(R.drawable.add);
		btnAddDetails = (Button) layout.findViewById(R.id.btn_add_details);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		TextView tvTitleText = (TextView) layout.findViewById(R.id.textView1);
		tvTitleText.setText(titleText);
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		btnAddDetails.setVisibility(View.VISIBLE);
		btnAddDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialogHandler.createDialog();
			}
		});
		builder.setCustomTitle(layout);

		addServiceActivityView = inflater.inflate(R.layout.add_serviceactivity_list, null);

		((Button) addServiceActivityView.findViewById(R.id.btn_sal_submit)).setOnClickListener(this);
		btnNext = (Button) addServiceActivityView.findViewById(R.id.btn_sal_next);
		jobNotes = (EditText) addServiceActivityView.findViewById(R.id.et_sal_jobnotes);

		llList = (LinearLayout) addServiceActivityView.findViewById(R.id.ll_sal_list);
		btnNext.setOnClickListener(this);

		((Button) addServiceActivityView.findViewById(R.id.asa_button0)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button1)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button2)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button3)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button4)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button5)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button6)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button7)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button8)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_button9)).setOnClickListener(this);
		((Button) addServiceActivityView.findViewById(R.id.asa_buttonPoint)).setOnClickListener(this);
		((ImageButton) addServiceActivityView.findViewById(R.id.asa_buttonClear)).setOnClickListener(this);

		for (int i = 0; i < pUnit.size(); i++) {
			inflater.inflate(R.layout.add_serviceactivity_row, llList);
		}

		for (int i = 0; i < llList.getChildCount(); i++) {
			final int pos = i;

			TextView productName = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_add_service_activity);
			TextView format = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_format);
			TextView qty = (TextView) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.tv_quantity);

			if (dispatchCount > i) {
				productName.setTextColor(Color.GREEN);
				format.setTextColor(Color.GREEN);
			}

			productName.setText(pName.get(i));
			format.setText(pUnit.get(i));
			float fQty = pQty.get(i);
		    if(fQty == (int) fQty) {
		    	qty.setText(String.format("%d",(int)fQty));
		    } else {
		    	qty.setText(String.format("%s",fQty));
		    }

			EditText ed = (EditText) ((LinearLayout) llList.getChildAt(i)).findViewById(R.id.et_quantity);
			ed.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cEditText.setBackgroundColor(Color.WHITE);
					cEditText = editTextArray.get(pos);
					cEditText.setBackgroundColor(Color.YELLOW);

				}
			});
			editTextArray.add(ed);
		}

		if (!editTextArray.isEmpty()) {
			cEditText = editTextArray.get(0);
			cEditText.setBackgroundColor(Color.YELLOW);
			btnNext.setVisibility(View.VISIBLE);
		} else
			btnNext.setVisibility(View.GONE);

		builder.setView(addServiceActivityView);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_sal_submit:
			((Button) addServiceActivityView.findViewById(R.id.btn_sal_submit)).setClickable(false);
			saDF = customDialogHandler.getSaDF();
			if (newSA) {
				createNewServiceActivity();
			} else {
				updateCurrentServiceActivity();
			}
			actionListener.serviceLocationCompletedNow(mPoi);
			dialog.dismiss();
			break;

		case R.id.btn_sal_next:
			if (cEditText != null) {
				/**
				 * Round Robin condition for current EditText selection.
				 */
				cEditText.setBackgroundColor(Color.WHITE);
			}
	
			if ((editTextArray.size() - 1) == cSelect)
				cSelect = 0;
			else
				cSelect++;

			cEditText = editTextArray.get(cSelect);
			if (cEditText != null) {
				cEditText.setBackgroundColor(Color.YELLOW);
			}
			break;

		case R.id.asa_button0:
		case R.id.asa_button1:
		case R.id.asa_button2:
		case R.id.asa_button3:
		case R.id.asa_button4:
		case R.id.asa_button5:
		case R.id.asa_button6:
		case R.id.asa_button7:
		case R.id.asa_button8:
		case R.id.asa_button9:
			if (cEditText != null)
				cEditText.setText(cEditText.getText().toString() + ((Button) v.findViewById(v.getId())).getText());
			break;

		case R.id.asa_buttonPoint:
			if (cEditText != null) {
				if (!(cEditText.getText().toString()).contains(".")) {
					if (cEditText.getText().length() > 0)
						cEditText.setText(cEditText.getText() + ".");
					else
						cEditText.setText(cEditText.getText() + "0.");
				}
			}
			break;

		case R.id.asa_buttonClear:
			if (cEditText != null) {
				if ((cEditText.getText().length()) > 0)
					cEditText.setText(cEditText.getText().toString().substring(0, cEditText.getText().length() - 1));
			}
			break;

		default:
			break;
		}
	}

	private void createNewServiceActivity() {
		ServiceActivity serviceActivity = new ServiceActivity();
		CompanyDao dao = new CompanyDao();
		serviceActivity.setCompanyId(dao.getCompanyId());

		Contract contract = mPoi.getContract();
		if (contract != null) {
			serviceActivity.setContractId(mPoi.getContract().getId());
		} else {
			Log.e("New SA", "No contract found for POI " + mPoi.getId());
		}
		serviceActivity.setDateTime(CommonUtils.UtcDateNow());

		serviceActivity.setJobNotes(jobNotes.getText().toString());

		if (Session.getDriver() != null) {
			serviceActivity.setUserId(Session.getDriver().getId());
		} else {
			Log.w("New SA", "Session.driver is NULL");
		}
		if (Session.getVehicle() != null) {
			serviceActivity.setVehicleId(Session.getVehicle().getId());
		} else {
			Log.w("New SA", "Session.vehicle is NULL");
		}
		serviceActivity.setStatus(ServiceActivity.SA_COMPLETED, true);

		// Add the list of activities that have been filled by the driver.
		List<Activity> activitiesList = new ArrayList<Activity>();

		for (int i = 0; i < llList.getChildCount(); i++) {
			LinearLayout layout = (LinearLayout) llList.getChildAt(i);
			EditText quantity = (EditText) layout.findViewById(R.id.et_quantity);
			String text = quantity.getText().toString();

			if (!text.equals("")) {
				Activity activity = new Activity();
				activity.setCompanyId(mPoi.getContract().getCompany_id());
				activity.setQuantity(Float.valueOf(text));
				activity.setContractServiceId(mPoi.getContract().listServices().get(i).getId());
				activitiesList.add(activity);
			}
		}

		serviceActivity.setServiceLocationId(mPoi.getSlId());
		serviceActivity.setServices(activitiesList);
		serviceActivity.setSaDetailsForm(saDF);

		ServiceActivityPushSync saPushSync = ServiceActivityPushSync.getInstance();
		saPushSync.pushData(mContext, serviceActivity);
		// Sending SA details form.
		if (saDF != null) {
			ServiceActivityDetailsPushSync saDetailsPushSync = ServiceActivityDetailsPushSync.getInstance();
			saDF.setService_activity_id(serviceActivity.getId());
			saDetailsPushSync.pushData(Session.MapAct, saDF);
		}

		try {
			ServiceLocationDao slDao = new ServiceLocationDao();
			ServiceLocation location = (slDao).getById(mPoi.getSlId());
			if (location != null) {
				location.setTimeLastSA(serviceActivity.getDateTime());
				slDao.insertOrReplace(location);
			} else {
				Log.e(TAG, "No service location found for POI " + mPoi.getId() + " in statu " + mPoi.getStatus());
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception caught", e);
		}
	}

	private void updateCurrentServiceActivity() {
		ServiceActivity sa = mPoi.getCurrentServiceActivity();

		List<Activity> saActivities = sa.listRequestedServices();

		// Updating the quantity for the SA that were requested and add any
		// new SA that was not requested but still completed by the driver.

		for (int i = 0; i < llList.getChildCount(); i++) {
			LinearLayout layout = (LinearLayout) llList.getChildAt(i);
			EditText quantity = (EditText) layout.findViewById(R.id.et_quantity);
			String text = quantity.getText().toString();

			if (text.equals("") && (i < saActivities.size())) {
				// Assigned activity... Fill out with 0 if nothing in it.
				text = "0";
			}
			if (!text.equals("")) {
				if (i < saActivities.size()) {
					// Existing activity requested by the dispatcher
					saActivities.get(i).setQuantity(Float.valueOf(text));
				} else if (i < lineItems.size()) {
					// New activity created on the spot by the driver
					String serviceId = lineItems.get(i);
					Activity activity = new Activity();

					activity.setCompanyId(mPoi.getContract().getCompany_id());
					activity.setQuantity(Float.valueOf(text));
					activity.setContractServiceId(serviceId);
					activity.setServiceActivityId(mPoi.getCurrentServiceActivity().getId());

					saActivities.add(activity);
				} else {
					Log.e("Update SA", "Could not find lineItem for new SA at index " + i + ", size = " + lineItems.size());
				}
			}
		}

		// List<Activity> activities = saActivities;
		// // Removing blank SA's
		// for (int i = 0; i < saActivities.size(); i++) {
		// if (saActivities.get(i).getQuantity() == 0.0f) {
		// activities.remove(i);
		// }
		// }

		// saActivities = activities;

		sa.setJobNotes(jobNotes.getText().toString());
		sa.setSaDetailsForm(saDF);

		if (actionHandler != null) {
			// It is time to pass the update to Snowman and al.
			actionHandler.serviceActivityCompleted(mPoi, sa);
			// Sending SA details form.
			if (saDF != null) {
				ServiceActivityDetailsPushSync saDetailsPushSync = ServiceActivityDetailsPushSync.getInstance();
				saDF.setService_activity_id(sa.getId());
				saDetailsPushSync.pushData(Session.MapAct, saDF);
			}
		}

	}

	public void showNoContractErrorMsg() {
		AlertDialog.Builder alert_box = new AlertDialog.Builder(mContext);
		final Dialog dialog2 = new Dialog(mContext);
		alert_box.setIcon(R.drawable.icon);
		alert_box.setMessage("No contract found for this Service Location for the current season.");
		alert_box.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog2.dismiss();
			}
		});
		alert_box.show();
	}

	public void showErrorMsg() {
		AlertDialog.Builder alert_box = new AlertDialog.Builder(mContext);
		final Dialog dialog1 = new Dialog(mContext);
		alert_box.setIcon(R.drawable.icon);
		alert_box.setMessage("All fields are mandatory");
		alert_box.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog1.dismiss();
				SaCompletedCustomDialog.this.dialog.show();
			}
		});
		alert_box.show();
	}
}