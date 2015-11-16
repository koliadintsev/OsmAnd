package com.operasoft.snowboard.util;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.Callout;
import com.operasoft.snowboard.database.CalloutType;
import com.operasoft.snowboard.database.CalloutTypeDao;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;

/**
 * @author dounaka
 *
 */
public class SelectCalloutTypeCustomDialog implements OnClickListener {

	private final Context mContext;
	private final PointOfInterest mPoi;
	private Dialog dialog;
	private String titleText;
	private ArrayList<CalloutType> calloutTypes = new ArrayList<CalloutType>();
	private final PointOfInterestActionListener actionListener;
	private final ListView callOutTypeLIstView;

	public SelectCalloutTypeCustomDialog(Context context, PointOfInterest poi, PointOfInterestActionListener actionListener) {
		mContext = context;
		mPoi = poi;
		this.actionListener = actionListener;
		this.callOutTypeLIstView = new ListView(mContext);
	}

	public void createDialog() {
		final CalloutTypeDao calloutTypeDao = new CalloutTypeDao();
		calloutTypes.clear();
		calloutTypes = calloutTypeDao.listCalloutTypes();
		if (calloutTypes.size() == 0) {
			Toast.makeText(mContext, "No callout associated with your company.", Toast.LENGTH_LONG).show();
			return;
		}
		titleText = "Select Callout Type";
		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialoag_listview_punch_clock, (ViewGroup) dialog.findViewById(R.id.root_punch));

		// Dialog close button and Text on click listener.
		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
		ImageView iMg_title_dialog = (ImageView) layout.findViewById(R.id.iMg_dialog_title);
		iMg_title_dialog.setImageResource(R.drawable.add);
		TextView closeText = (TextView) layout.findViewById(R.id.textView2);
		TextView tvTitleText = (TextView) layout.findViewById(R.id.textView1);
		tvTitleText.setText(titleText);
		iMg_cancel_dialog.setOnClickListener(this);
		closeText.setOnClickListener(this);
		builder.setCustomTitle(layout);
		ArrayList<String> callOutName = new ArrayList<String>();
		for (CalloutType callOutType : calloutTypes) {
			callOutName.add(callOutType.getName());
		}
		//layoutCalloutInflator = inflater.inflate(R.layout.callout_type_list, null);
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext, R.layout.callouttype_list_item, callOutName);
		callOutTypeLIstView.setAdapter(listAdapter);
		builder.setView(callOutTypeLIstView);
		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.show();
		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		callOutTypeLIstView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				dialog.dismiss();
				String userId = Session.getDriver().getId();
				Callout callout = new Callout(mPoi.getId(), userId);
				callout.setCallOutTypeId(calloutTypes.get(pos).getId());
				actionListener.calloutCreated(mPoi, callout);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iMg_cancel_dialog:
		case R.id.textView2:
			dialog.dismiss();
			break;

		default:
			break;
		}
	}
}
