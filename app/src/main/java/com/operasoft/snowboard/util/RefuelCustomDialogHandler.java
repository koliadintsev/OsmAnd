package com.operasoft.snowboard.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.VehicleRefuelLog;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.VehicleRefuelLogPushSync;

public class RefuelCustomDialogHandler {

	private Context context;
	private View view;
	private EditText quantity, amount, engHours, Odometer;
	private Button btnConfirm;
	private SharedPreferences preferences;
	private Dialog dialog;

	private static int cSelect = 1;
	private String unit = "";

	public RefuelCustomDialogHandler(Context context, View view) {
		this.context = context;
		this.view = view;
	}

	private void createDialog() {

		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		dialog = new Dialog(context);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_refuel_header, (ViewGroup) view.findViewById(R.id.root_refuel));

		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		iMg_cancel_dialog.setOnClickListener(makeListener);
		closeText.setOnClickListener(makeListener);

		builder.setCustomTitle(layout);
		View layout1 = inflater.inflate(R.layout.dialog_refuel, null);

		((Button) layout1.findViewById(R.id.btn_drefuel_0)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_1)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_2)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_3)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_4)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_5)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_6)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_7)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_8)).setOnClickListener(makeListener);
		((Button) layout1.findViewById(R.id.btn_drefuel_9)).setOnClickListener(makeListener);

		((Button) layout1.findViewById(R.id.btn_drefuel_Reset)).setOnClickListener(makeListener);
		((ImageButton) layout1.findViewById(R.id.btn_drefuel_OK)).setOnClickListener(makeListener);

		quantity = (EditText) layout1.findViewById(R.id.et_drefuel_quantity);
		amount = (EditText) layout1.findViewById(R.id.et_drefuel_amount);
		engHours = (EditText) layout1.findViewById(R.id.et_drefuel_engine_hours);
		Odometer = (EditText) layout1.findViewById(R.id.et_drefuel_odometer);
		btnConfirm = (Button) layout1.findViewById(R.id.Btn_drefuel_confirm);

		unit = Session.getVolumeUnit();
		((TextView) layout1.findViewById(R.id.tv_drefuel_unit)).setText("(" + unit + ") :");

		btnConfirm.setOnClickListener(makeListener);
		quantity.setOnClickListener(makeListener);
		amount.setOnClickListener(makeListener);
		engHours.setOnClickListener(makeListener);
		Odometer.setOnClickListener(makeListener);

		btnConfirm.setBackgroundResource(R.drawable.login_button);
		setTextBg();

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	public OnClickListener makeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.button_refuel:
				cSelect = 1;
				createDialog();
				break;

			case R.id.btn_drefuel_Reset:
				if ((cSelect == 1) && !(quantity.getText().toString()).contains(".") && quantity.getText().length() > 0)
					quantity.setText("" + quantity.getText() + ".");
				else if ((cSelect == 2) && !(amount.getText().toString()).contains(".")
						&& amount.getText().length() > 0)
					amount.setText("" + amount.getText() + ".");
				else if ((cSelect == 4) && !(Odometer.getText().toString()).contains(".")
						&& Odometer.getText().length() > 0)
					Odometer.setText("" + Odometer.getText() + ".");
				break;

			case R.id.btn_drefuel_OK:
				if ((cSelect == 1) && (quantity.getText().length()) > 0)
					quantity.setText("" + quantity.getText().toString().substring(0, quantity.getText().length() - 1));
				else if ((cSelect == 2) && (amount.getText().length()) > 0)
					amount.setText("" + amount.getText().toString().substring(0, amount.getText().length() - 1));
				else if ((cSelect == 3) && (engHours.getText().length()) > 0)
					engHours.setText("" + engHours.getText().toString().substring(0, engHours.getText().length() - 1));
				else if ((cSelect == 4) && (Odometer.getText().length()) > 0)
					Odometer.setText("" + Odometer.getText().toString().substring(0, Odometer.getText().length() - 1));
				break;

			case R.id.et_drefuel_quantity:
				quantity.setBackgroundResource(R.drawable.login_presses_button);
				cSelect = 1;
				btnConfirm.setText("Next");
				setTextBg();
				break;

			case R.id.et_drefuel_amount:
				quantity.setBackgroundResource(R.drawable.login_presses_button);
				cSelect = 2;
				btnConfirm.setText("Next");
				setTextBg();
				break;

			case R.id.et_drefuel_engine_hours:
				engHours.setBackgroundResource(R.drawable.login_presses_button);
				btnConfirm.setText("Next");
				cSelect = 3;
				setTextBg();
				break;

			case R.id.et_drefuel_odometer:
				Odometer.setBackgroundResource(R.drawable.login_presses_button);
				btnConfirm.setText("Submit");
				cSelect = 4;
				setTextBg();
				break;

			case R.id.Btn_drefuel_confirm:
				if (cSelect == 1) {
					cSelect++;
					amount.setBackgroundResource(R.drawable.login_presses_button);
					amount.requestFocus();
					setTextBg();
				} else if (cSelect == 2) {
					cSelect++;
					engHours.setBackgroundResource(R.drawable.login_presses_button);
					engHours.requestFocus();
					setTextBg();
				} else if (cSelect == 3) {
					cSelect++;
					Odometer.setBackgroundResource(R.drawable.login_presses_button);
					btnConfirm.setText("Submit");
					Odometer.requestFocus();
					setTextBg();
				} else if (cSelect == 4) {
					if (quantity.getText().length() > 0 && engHours.getText().length() > 0
							&& Odometer.getText().length() > 0) {
						btnConfirm.setOnClickListener(null);
						btnConfirm.setClickable(false);
						btnConfirm.setText("Updating...");
						preferences = PreferenceManager.getDefaultSharedPreferences(context);

						insertRefuelLog();
						dialog.dismiss();
					} else {
						AlertDialog.Builder alert_box = new AlertDialog.Builder(context);
						final Dialog dialog1 = new Dialog(context);
						alert_box.setIcon(R.drawable.icon);
						alert_box.setMessage(R.string.all_field_mandatory);
						alert_box.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog1.dismiss();
								RefuelCustomDialogHandler.this.dialog.show();
							}
						});
						alert_box.show();
					}
				}
				break;

			case R.id.btn_drefuel_0:
			case R.id.btn_drefuel_1:
			case R.id.btn_drefuel_2:
			case R.id.btn_drefuel_3:
			case R.id.btn_drefuel_4:
			case R.id.btn_drefuel_5:
			case R.id.btn_drefuel_6:
			case R.id.btn_drefuel_7:
			case R.id.btn_drefuel_8:
			case R.id.btn_drefuel_9:
				switch (cSelect) {
				case 1:
					quantity.setText("" + quantity.getText() + ((Button) v.findViewById(v.getId())).getText());
					break;

				case 2:
					amount.setText("" + amount.getText() + ((Button) v.findViewById(v.getId())).getText());
					break;

				case 3:
					engHours.setText("" + engHours.getText() + ((Button) v.findViewById(v.getId())).getText());
					break;

				case 4:
					Odometer.setText("" + Odometer.getText() + ((Button) v.findViewById(v.getId())).getText());
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
	};

	public void insertRefuelLog() {

		VehicleRefuelLog dto = new VehicleRefuelLog();
		dto.setVehicleId(preferences.getString(Config.VEHICLE_ID_KEY, ""));
		dto.setUserId(CommonUtils.selectUserId(preferences.getString(Config.USER_PIN_KEY, "")));
		dto.setDate(CommonUtils.UtcDateNow());
		// Set the volume
		double volume = 0;
		try {
			volume = Double.valueOf(quantity.getText().toString());
		} catch (NumberFormatException nfe) {}
		dto.setVolume(volume);		
		dto.setVolumeUnit(unit);
		// Set the amount
		double value = 0;
		try {
			value = Double.valueOf(amount.getText().toString()); 
		} catch (NumberFormatException nfe) {}
		dto.setAmount(value);
		// Set the hours
		double hours = 0;
		try {
			hours = Double.valueOf(engHours.getText().toString()); 
		} catch (NumberFormatException nfe) {}
		dto.setEngineHours(hours);		
		if (Session.clocation != null) {
			dto.setLatitude(Session.clocation.getLatitude());
			dto.setLongitude(Session.clocation.getLongitude());
		}
		dto.setOdometer(Odometer.getText().toString());

		VehicleRefuelLogPushSync.getInstance().pushData(context, dto);
	}

	public void setTextBg() {
		quantity.setBackgroundResource(R.drawable.login_header);
		amount.setBackgroundResource(R.drawable.login_header);
		engHours.setBackgroundResource(R.drawable.login_header);
		Odometer.setBackgroundResource(R.drawable.login_header);

		switch (cSelect) {
		case 1:
			quantity.setBackgroundResource(R.drawable.login_presses_button);
			break;
		case 2:
			amount.setBackgroundResource(R.drawable.login_presses_button);
			break;
		case 3:
			engHours.setBackgroundResource(R.drawable.login_presses_button);
			break;
		case 4:
			Odometer.setBackgroundResource(R.drawable.login_presses_button);
			break;
		}

	}
}
