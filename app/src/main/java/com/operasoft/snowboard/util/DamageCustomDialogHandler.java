package com.operasoft.snowboard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Damage;
import com.operasoft.snowboard.database.DamageType;
import com.operasoft.snowboard.database.DamageTypeDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.push.DamagePushSync;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestManager;

public class DamageCustomDialogHandler {

	private Context mContext;
	private PointOfInterest mPoi;
	private Dialog dialog;
	private Spinner spinner;
	private HashMap<String, String> spinnerItemsListMap;
	private ArrayList<String> spinnerItemsList;
	private EditText damageEdit;
	private EditText addressEdit;
	private LinearLayout imageLayout;
	private HorizontalScrollView hsv;
	

	public DamageCustomDialogHandler(Context context, PointOfInterest poi) {
		mContext = context;
		mPoi = poi;
	}

	public void createDialog() {

		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_header_menu, (ViewGroup) dialog.findViewById(R.id.root_punch));

		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iv_dhm_cancel);
		TextView closeText = (TextView) layout.findViewById(R.id.tv_dhm_cancel);
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

		((ImageView) layout.findViewById(R.id.iv_dhm_icon)).setBackgroundResource(R.drawable.mission);
		((TextView) layout.findViewById(R.id.tv_dhm_title)).setText("INCIDENT");
		builder.setCustomTitle(layout);
		View layout1 = inflater.inflate(R.layout.dialog_damages, null);

		((Button) layout1.findViewById(R.id.dd_btn_submit)).setOnClickListener(damageListener);
		
		spinnerItemsListMap = new HashMap<String, String>();
		spinnerItemsList = new ArrayList<String>();
		DamageTypeDao damagetypes = new DamageTypeDao();

		List<DamageType> spinnerItems = damagetypes.listAll();
		for (DamageType damage : spinnerItems) {
			spinnerItemsList.add(damage.getName());
			spinnerItemsListMap.put(damage.getName(), damage.getId());
		}

		spinner = (Spinner) layout1.findViewById(R.id.dd_spn_damages_type);
		damageEdit = (EditText) layout1.findViewById(R.id.dd_et_damage);
		// damageEdit.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int count) {
		// if (s.length() > 0) {
		// spinner.setEnabled(false);
		// } else {
		// spinner.setEnabled(true);
		// }
		// }
		//
		// @Override
		// public void afterTextChanged(Editable arg0) {
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// }
		// });

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerItemsList);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		builder.setView(layout1);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();

	}

	public OnClickListener damageListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_incident:
				mPoi = PointOfInterestManager.getInstance().getInsidePolygonPoi();
				createDialog();
				break;
			case R.id.dd_btn_submit:
				sendDamageDetails();
				dialog.dismiss();
			}
		}
	};
	


	public void sendDamageDetails() {
		Damage dto = new Damage();

		dto.setStatus(Damage.DAMAGE_PENDING);
		dto.setContractId(mPoi == null ? "null" : mPoi.getContract().getId());
		dto.setDamageTypeId(spinnerItemsListMap.get(spinner.getSelectedItem()));
		dto.setDescription(damageEdit.getText().toString());
		dto.setCompanyId(Session.getCompanyId());
		dto.setUserId(Session.getDriver().getId());
		dto.setVehicleId(Session.getVehicle().getId());
		dto.setDate(CommonUtils.UtcDateNow());
		if (Session.clocation != null) {
			dto.setGpsCoordinates(Session.clocation.getLatitude() + " " + Session.clocation.getLongitude());
		}

		DamagePushSync.getInstance().pushData(mContext, dto);
	}
}