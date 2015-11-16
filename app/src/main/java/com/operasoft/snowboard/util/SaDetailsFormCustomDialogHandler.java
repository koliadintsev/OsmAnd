package com.operasoft.snowboard.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Company;
import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.database.ServiceActivityDetails;
import com.operasoft.snowboard.engine.PointOfInterest;

public class SaDetailsFormCustomDialogHandler {

	private Context mContext;
	PointOfInterest mPoi;
	private Dialog dialogDetails;
	private View layout1;
	private Button btnAddDetails;
	private String titleText;
	int plowingRoad = 0;
	int deiceRoad = 0;
	int plowingWalk = 0;
	int deiceWalk = 0;
	boolean farenheit = false;
	private ServiceActivityDetails saDF = null;

	public ServiceActivityDetails getSaDF() {
		return saDF;
	}

	public SaDetailsFormCustomDialogHandler(Context context, PointOfInterest poi) {
		mContext = context;
		mPoi = poi;
		saDF = null;
	}

	public void createDialog() {
		dialogDetails = new Dialog(mContext);
		saDF = new ServiceActivityDetails();

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock, (ViewGroup) dialogDetails.findViewById(R.id.root_punch));
		// dialogDetails close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
		iMg_title_dialog.setImageResource(R.drawable.add);
		btnAddDetails = (Button) layout.findViewById(R.id.btn_add_details);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		TextView tvTitleText = (TextView) layout.findViewById(R.id.textView1);
		titleText = "Detailed Site Visit Form";
		tvTitleText.setText(titleText);
		iMg_cancel_dialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogDetails.dismiss();
			}
		});

		closeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialogDetails.dismiss();
			}
		});

		btnAddDetails.setVisibility(View.GONE);
		builder.setCustomTitle(layout);

		layout1 = inflater.inflate(R.layout.sa_add_details_dialog, null);

		// Seek bar related code.
		final RadioButton rBtnC = (RadioButton) layout1.findViewById(R.id.radio_temp_c);
		final RadioButton rBtnF = (RadioButton) layout1.findViewById(R.id.radio_temp_f);
		final TextView tvTempC = (TextView) layout1.findViewById(R.id.temp_text_c);
		final TextView tvTempF = (TextView) layout1.findViewById(R.id.temp_text_f);
		final SeekBar sbProgressTemp = (SeekBar) layout1.findViewById(R.id.seekBar_temp);
		final TextView tvAccumulation = (TextView) layout1.findViewById(R.id.accumulation_text);
		
		// TODO: Use a single TV instead of 2 of them...
		String tempUnit = Session.getTemperatureUnit();
		if (tempUnit == Company.FARENHEIT) {
			tvAccumulation.setText("Accumulation (inches)");
			farenheit = true;
			tvTempF.setVisibility(View.VISIBLE);
			tvTempC.setVisibility(View.INVISIBLE);
			tvTempF.setText("32 " + Company.FARENHEIT);
			sbProgressTemp.setMax(110);
			sbProgressTemp.setProgress(30 - 0);
		} else {
			tvAccumulation.setText("Accumulation (cm)");
			farenheit = false;
			tvTempC.setVisibility(View.VISIBLE);
			tvTempF.setVisibility(View.INVISIBLE);
			tvTempC.setText("0 " + Company.CELSIUS);
			sbProgressTemp.setMax(80);
			sbProgressTemp.setProgress(40 - 0);
		}

//		saDF.setOutdoor_temp_type("C");
//		saDF.setOutdoor_temp(0 + "");
		rBtnC.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					tvTempC.setText(sbProgressTemp.getProgress() - 40 + "C");
					// sbProgressTemp.setProgress(40 - 0);
					tvTempC.setVisibility(View.VISIBLE);
					tvTempF.setVisibility(View.INVISIBLE);
					sbProgressTemp.setMax(80);
//					saDF.setOutdoor_temp_type("C");
				}
			}
		});
		rBtnF.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					tvTempF.setText(sbProgressTemp.getProgress() - 30 + "F");
					tvTempC.setVisibility(View.INVISIBLE);
					tvTempF.setVisibility(View.VISIBLE);
					sbProgressTemp.setMax(110);
//					saDF.setOutdoor_temp_type("F");
				}
			}
		});

		sbProgressTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (farenheit) {
					tvTempF.setText("" + (progress - 30) + " " + Company.FARENHEIT);
//					saDF.setOutdoor_temp(progress - 30 + "");
				} else {
					tvTempC.setText("" + (progress - 40) + " " + Company.CELSIUS);
//					saDF.setOutdoor_temp(progress - 40 + "");
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		// End of seek bar related code.

		// Spinner set.
		final Spinner spAccumulationInch = (Spinner) layout1.findViewById(R.id.spinner_acc_inch);
		final Spinner spAccumulationType = (Spinner) layout1.findViewById(R.id.spinner_acc_type);
		final Spinner spPrecipitationType = (Spinner) layout1.findViewById(R.id.spinner_pre_type);
		final Spinner spConditionType = (Spinner) layout1.findViewById(R.id.spinner_con_type);
		final Spinner spTotalTimeOnSite = (Spinner) layout1.findViewById(R.id.spinner_total_time_on_site);

		final ToggleButton tbIOnly = (ToggleButton) layout1.findViewById(R.id.inspection_only);

		ToggleButton tbPlowing_entrance = (ToggleButton) layout1.findViewById(R.id.plowing_entrance);
		ToggleButton tbPlowing_rode = (ToggleButton) layout1.findViewById(R.id.plowing_rode);
		ToggleButton tbPlowing_parking = (ToggleButton) layout1.findViewById(R.id.plowing_parking);
		ToggleButton tbPlowing_loading = (ToggleButton) layout1.findViewById(R.id.plowing_loading);
		ToggleButton tbPlowing_drive = (ToggleButton) layout1.findViewById(R.id.plowing_drive);
		ToggleButton tbPlowing_ramp = (ToggleButton) layout1.findViewById(R.id.plowing_ramp);
		ToggleButton tbPlowing_delivery = (ToggleButton) layout1.findViewById(R.id.plowing_delivery);

		tbPlowing_entrance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 0);
			}
		});

		tbPlowing_rode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 1);
			}
		});

		tbPlowing_parking.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 2);
			}
		});

		tbPlowing_loading.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 3);
			}
		});

		tbPlowing_drive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 4);
			}
		});

		tbPlowing_ramp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 5);
			}
		});

		tbPlowing_delivery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingCheck(isChecked, 6);
			}
		});

		ToggleButton tbDeice_entrance = (ToggleButton) layout1.findViewById(R.id.deice_entrance);
		ToggleButton tbDeice_rode = (ToggleButton) layout1.findViewById(R.id.deice_rode);
		ToggleButton tbDeice_parking = (ToggleButton) layout1.findViewById(R.id.deice_parking);
		ToggleButton tbDeice_loading = (ToggleButton) layout1.findViewById(R.id.deice_loading);
		ToggleButton tbDeice_drive = (ToggleButton) layout1.findViewById(R.id.deice_drive);
		ToggleButton tbDeice_ramp = (ToggleButton) layout1.findViewById(R.id.deice_ramp);
		ToggleButton tbDeice_delivery = (ToggleButton) layout1.findViewById(R.id.deice_delivery);

		tbDeice_entrance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 0);
			}
		});

		tbDeice_rode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 1);
			}
		});

		tbDeice_parking.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 2);
			}
		});

		tbDeice_loading.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 3);
			}
		});

		tbDeice_drive.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 4);
			}
		});

		tbDeice_ramp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 5);
			}
		});

		tbDeice_delivery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftDeiceCheck(isChecked, 6);
			}
		});

		ToggleButton tbPlowingEntranceDoor = (ToggleButton) layout1.findViewById(R.id.plowing_entrance_door);
		ToggleButton tbPlowingService = (ToggleButton) layout1.findViewById(R.id.plowing_service);
		ToggleButton tbPlowingRampWalk = (ToggleButton) layout1.findViewById(R.id.plowing_ramp_walk);
		ToggleButton tbPlowingStreet = (ToggleButton) layout1.findViewById(R.id.plowing_street);

		tbPlowingEntranceDoor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingWalkCheck(isChecked, 0);
			}
		});

		tbPlowingService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingWalkCheck(isChecked, 1);
			}
		});

		tbPlowingRampWalk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingWalkCheck(isChecked, 2);
			}
		});

		tbPlowingStreet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftPlowingWalkCheck(isChecked, 3);
			}
		});

		ToggleButton tbDeiceEntranceDoor = (ToggleButton) layout1.findViewById(R.id.deice_entrance_door);
		ToggleButton tbDeiceService = (ToggleButton) layout1.findViewById(R.id.deice_service);
		ToggleButton tbDeiceWalkRamp = (ToggleButton) layout1.findViewById(R.id.deice_walk_ramp);
		ToggleButton tbDeiceStreet = (ToggleButton) layout1.findViewById(R.id.deice_street);

		tbDeiceEntranceDoor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftWalkDeiceCheck(isChecked, 0);
			}
		});

		tbDeiceService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftWalkDeiceCheck(isChecked, 1);
			}
		});

		tbDeiceWalkRamp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftWalkDeiceCheck(isChecked, 2);
			}
		});

		tbDeiceStreet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				shiftWalkDeiceCheck(isChecked, 3);
			}
		});

		((Button) layout1.findViewById(R.id.submit_close)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (mPoi.getStatus()) {

				case SERVICE_ACTIVITY_ACCEPTED:
				case SERVICE_ACTIVITY_IN_DIRECTION:
				case SERVICE_ACTIVITY_RECEIVED:
					saDF.setService_activity_id(mPoi.getId());
					break;
				}

				int accumulation = 0;
				double time = 0;
				try {
					accumulation = Integer.parseInt(spAccumulationInch.getSelectedItem().toString());
				} catch (NumberFormatException nfe) {
					Log.w("SA Details", "Failed to parse accumulation for " + spAccumulationInch.getSelectedItem().toString());
				}

				try {
					String value = spTotalTimeOnSite.getSelectedItem().toString();
					int index = value.indexOf(" ");
					if (index > 0) {
						value = value.substring(0, index);
					}
					time = Double.parseDouble(value);
				} catch (NumberFormatException nfe) {
					Log.w("SA Details", "Failed to parse time on site for " + spTotalTimeOnSite.getSelectedItem().toString());
				}

				if (farenheit) {
					saDF.setOutdoor_temp(tvTempF.getText().toString());
				} else {
					saDF.setOutdoor_temp(tvTempC.getText().toString());
				}
				saDF.setCompany_id(Session.getCompanyId());				
				saDF.setAccumulation_depth(accumulation);
				saDF.setAccumulation_type(spAccumulationType.getSelectedItem().toString());
				saDF.setPrecipitation(spPrecipitationType.getSelectedItem().toString());
				saDF.setConditions(spConditionType.getSelectedItem().toString());
				// saDF.setPlowing_roads(String.format("%7s", Integer.toBinaryString(plowingRoad)).replace(' ', '0'));
				saDF.setPlowing_roads(plowingRoad + "");
				saDF.setDeice_roads(deiceRoad + "");
				saDF.setPlowing_walkways(plowingWalk + "");
				saDF.setDeice_walkways(deiceWalk + "");
				saDF.setInspection_only(tbIOnly.isChecked() ? 1 : 0);
				saDF.setTime_on_site(time);
				saDF.setNotes(((EditText) layout1.findViewById(R.id.notes)).getText().toString());

				dialogDetails.dismiss();
			}
		});

		builder.setView(layout1);
		dialogDetails = builder.create();
		dialogDetails.setCancelable(true);
		dialogDetails.show();
		dialogDetails.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	protected void shiftPlowingCheck(boolean isChecked, int pos) {
		if (isChecked) {
			plowingRoad = plowingRoad | (int) Math.pow(2, pos);
		} else {
			plowingRoad = plowingRoad & (255 - (int) Math.pow(2, pos));
		}
	}

	protected void shiftDeiceCheck(boolean isChecked, int pos) {
		if (isChecked) {
			deiceRoad = deiceRoad | (int) Math.pow(2, pos);
		} else {
			deiceRoad = deiceRoad & (255 - (int) Math.pow(2, pos));
		}
	}

	protected void shiftPlowingWalkCheck(boolean isChecked, int pos) {
		if (isChecked) {
			plowingWalk = plowingWalk | (int) Math.pow(2, pos);
		} else {
			plowingWalk = plowingWalk & (15 - (int) Math.pow(2, pos));
		}
	}

	protected void shiftWalkDeiceCheck(boolean isChecked, int pos) {
		if (isChecked) {
			deiceWalk = deiceWalk | (int) Math.pow(2, pos);
		} else {
			deiceWalk = deiceWalk & (15 - (int) Math.pow(2, pos));
		}
	}
}