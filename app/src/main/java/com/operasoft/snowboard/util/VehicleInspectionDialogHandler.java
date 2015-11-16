package com.operasoft.snowboard.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.InspectionJournal;
import com.operasoft.snowboard.database.InspectionJournalDefect;
import com.operasoft.snowboard.database.VehicleInspectionItem;
import com.operasoft.snowboard.database.VehicleInspectionItemDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.InspectionJournalPushSync;

public class VehicleInspectionDialogHandler {

	private Context context;
	private View view;
	private LinearLayout llList;
	private Dialog dialog;
	private LayoutInflater inflater;
	private View layout1;
	private List<VehicleInspectionItem> preDepartureItems = new ArrayList<VehicleInspectionItem>();
	private List<VehicleInspectionItem> endOfDayItems = new ArrayList<VehicleInspectionItem>();

	private ToggleButton butonPre, buttonEndOfDay;
	private Button buttonSubmit;
	private boolean eodInspection = false;
	ArrayList<VehicleInspectionItem> lineItems = new ArrayList<VehicleInspectionItem>();
	AlertDialog.Builder builder;

	public VehicleInspectionDialogHandler(Context context, View view) {
		this.context = context;
		this.view = view;
	}

	private void submitInspection() {
		// 1. Push the inspection journal to the server
		InspectionJournal inspection = new InspectionJournal();
		inspection.setDate(CommonUtils.UtcDateNow());
		inspection.setUserId(Session.getDriver().getId());
		inspection.setVehicleId(Session.getVehicle().getId());
		inspection.setCompanyId(Session.getCompanyId());

		if (eodInspection) {
			inspection.setType(InspectionJournal.END_OF_DAY_TYPE);
		} else {
			inspection.setType(InspectionJournal.PRE_DEPARTURE_TYPE);
		}
		
		String notes = ((TextView) layout1.findViewById(R.id.et_inspection_notes)).getText().toString();
		for (int i = 0; i < llList.getChildCount(); i++) {

			ToggleButton button = (ToggleButton) ((LinearLayout) llList.getChildAt(i))
					.findViewById(R.id.btn_inspection_status);

			if (!button.isChecked()) {
				VehicleInspectionItem item = lineItems.get(i);
				InspectionJournalDefect defect = new InspectionJournalDefect();
				defect.setItemId(item.getId());
				defect.setNotes(notes);

				inspection.addDefect(defect);
			}
		}

		InspectionJournalPushSync.getInstance().pushData(context, inspection);
		dialog.dismiss();
	}

	private void updateInspectionItems() {
		lineItems.clear();
		if (eodInspection) {
			for (VehicleInspectionItem item : endOfDayItems) {
				lineItems.add(item);
			}
		} else {
			for (VehicleInspectionItem item : preDepartureItems) {
				lineItems.add(item);
			}
		}
		llList.removeAllViews();

		for (int i = 0; i < lineItems.size(); i++) {
			inflater.inflate(R.layout.vehicle_inspection_item_row, llList);
		}
		for (int i = 0; i < llList.getChildCount(); i++) {

			TextView InspectionItem = (TextView) ((LinearLayout) llList.getChildAt(i))
					.findViewById(R.id.tv_add_service_activity);

			InspectionItem.setText(lineItems.get(i).getName());
		}
		llList.refreshDrawableState();
	}

	private void createDialog() {
		VehicleInspectionItemDao dao = new VehicleInspectionItemDao();
		List<VehicleInspectionItem> items = dao.listAll();
		preDepartureItems.clear();
		endOfDayItems.clear();
		for (VehicleInspectionItem item : items) {
			if (item.isPreDepartureItem()) {
				preDepartureItems.add(item);
			}
			if (item.isEndOfDayItem()) {
				endOfDayItems.add(item);
			}
		}

		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		dialog = new Dialog(context);
		builder = new AlertDialog.Builder(context);

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_vehicle_inspection, (ViewGroup) view.findViewById(R.id.root));

		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
		iMg_title_dialog.setImageResource(R.drawable.camera);

		// Configure the "close" button
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
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
		builder.setCustomTitle(layout);
		layout1 = inflater.inflate(R.layout.vehicle_inspection_item_rows, null);
		llList = (LinearLayout) layout1.findViewById(R.id.inspectvehicle);

		// Configure the "Pre-Departure" button
		butonPre = ((ToggleButton) layout1.findViewById(R.id.btn_pre));
		butonPre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonEndOfDay.setChecked(false);
				butonPre.setChecked(true);
				if (eodInspection) {
					eodInspection = false;
					updateInspectionItems();
				}
			}
		});

		// Configure the "End of Day" button
		buttonEndOfDay = ((ToggleButton) layout1.findViewById(R.id.btn_eod));
		buttonEndOfDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonEndOfDay.setChecked(true);
				butonPre.setChecked(false);
				if (!eodInspection) {
					eodInspection = true;
					updateInspectionItems();
				}
			}

		});

		// Configure the "Submit" button
		buttonSubmit = (Button) layout1.findViewById(R.id.btn_inspection_submit);
		buttonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitInspection();
			}

		});

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);

		if ((cal.get(Calendar.AM_PM) == Calendar.AM) && eodInspection) {
			butonPre.setChecked(true);
			buttonEndOfDay.setChecked(false);
			eodInspection = false;
			updateInspectionItems();
		} else {
			butonPre.setChecked(false);
			buttonEndOfDay.setChecked(true);
			eodInspection = true;
			updateInspectionItems();
		}
		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	}

	public OnClickListener makeInspectListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.vehicle_inspection:
				createDialog();
				break;
			default:
				break;
			}
		}
	};

}
