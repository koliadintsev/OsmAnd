package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.List;

import net.osmand.plus.activities.MapActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.operasoft.snowboard.HelpListAdapter;
import com.operasoft.snowboard.R;

public class HelpHandler {

	private final Context mContext;
	private final View mView;
	private static Dialog dialog;
	List<String> list;

	public HelpHandler(Context context, View view, MapActivity mapActivity) {
		this.mContext = context;
		this.mView = view;
	}

	private void createDialog() {

		if (dialog != null)
			if (dialog.isShowing())
				dialog.dismiss();

		dialog = new Dialog(mContext);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_help_header, (ViewGroup) mView.findViewById(R.id.root_help));

		ImageView iMg_cancel_dialog = (ImageView) layout.findViewById(R.id.iMg_cancel_dialog);
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

		ListView modeList = new ListView(mContext);

		list = new ArrayList<String>();
		ArrayList<Integer> icon = new ArrayList<Integer>();

		list.add(mContext.getResources().getString(R.string.stafflist));
		icon.add(HelpListAdapter.STAFF_LIST);

		if (Session.getDriver().isForeman()) {
			list.add("ForeMan Daily Logs");
			icon.add(HelpListAdapter.FOREMAN_DAILY_LOGS);
		}

		list.add(mContext.getResources().getString(R.string.disable_autopup_when_onroute));
		icon.add(HelpListAdapter.DISABLE_AUTO_POPUP);

		list.add("View Default Season");
		icon.add(HelpListAdapter.VIEW_DFAULT_SEASON);

		if (Session.isSuperDriver()) {
			list.add("Settings");
			icon.add(HelpListAdapter.NO_ICON);

			list.add("View Vehicles");
			icon.add(HelpListAdapter.SETTINGS_VIEW_VEHICLE);

			list.add("View All SAs");
			icon.add(HelpListAdapter.SETTINGS_VIEW_ALL_SA);
		}
		String[] aa = new String[] { "Top Menu Icons", mContext.getResources().getString(R.string.route), mContext.getResources().getString(R.string.punch_clock),
				mContext.getResources().getString(R.string.refuel_log), mContext.getResources().getString(R.string.return_to_login), "Route Icons on map", "Location to serve on a route",
				"Location served on a route", "Location Go Back Reminder", "Marker Install Required", "Service Call Icons on map", "Not yet accepted", "Accepted & Pending", "En Route",
				"Mission Enabled", "Mission Active", "Complete", "End Route" };

		icon.add(0);
		icon.add(R.drawable.map_action_routes);
		icon.add(R.drawable.map_action_clock);
		icon.add(R.drawable.map_action_refuel);
		icon.add(R.drawable.map_action_logout);
		icon.add(0);
		icon.add(R.drawable.sl_active);
		icon.add(R.drawable.sl_completed);
		icon.add(R.drawable.sl_go_back);
		icon.add(R.drawable.marker);
		icon.add(0);
		icon.add(R.drawable.sa_received);
		icon.add(R.drawable.sa_assigned);
		icon.add(R.drawable.sa_in_direction);
		icon.add(R.drawable.mission_enabled);
		icon.add(R.drawable.mission_active);
		icon.add(R.drawable.service_location_completed_now);
		icon.add(R.drawable.ic_stopsign);

		for (int i = 0; i < aa.length; i++) {
			list.add(aa[i].toString());
		}

		HelpListAdapter adapter = new HelpListAdapter(mContext, list, icon);
		modeList.setAdapter(adapter);
		builder.setView(modeList);
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();

	}

	public OnClickListener helpClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_help:
				createDialog();
				break;
			default:
				break;
			}
		}
	};

	public static void dismissHelpHandler() {
		dialog.dismiss();
	}
}