package com.operasoft.snowboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.util.ContractListCustomDialog;
import com.operasoft.snowboard.util.HelpHandler;
import com.operasoft.snowboard.util.Session;

public class HelpListAdapter extends BaseAdapter {
	static public final int NO_ICON = 0;
	static public final int SETTINGS_VIEW_VEHICLE = -1;
	static public final int SETTINGS_VIEW_ALL_SA = -2;
	static public final int FOREMAN_DAILY_LOGS = -3;
	static public final int VIEW_DFAULT_SEASON = -4;
	static public final int DISABLE_AUTO_POPUP = -5;
	static public final int STAFF_LIST = -6;

	private final LayoutInflater mInflater;
	private final List<String> list;
	ArrayList<Integer> mIcon;
	private static Context mContext;
	private final ViewVehiclesHandler vHandler = new ViewVehiclesHandler();
	private final ViewAllSAHandler saHandler = new ViewAllSAHandler();
	private final ForemanLogHandler foremanLogHandler = new ForemanLogHandler();
	private final SeasonHandler seasonHandler = new SeasonHandler();
	private final DisableOnRouteAutoPopup disableOnRouteAutoPopupHandler = new DisableOnRouteAutoPopup();
	private final StaffListHandler staffListHandler = new StaffListHandler();

	public HelpListAdapter(Context context, List<String> list, ArrayList<Integer> icon) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
		HelpListAdapter.mContext = context;
		this.mIcon = icon;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		convertView = mInflater.inflate(R.layout.help_item_row, null);

		holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.iMg_arrow_dialog);
		holder.text_name = (TextView) convertView.findViewById(R.id.text1);
		holder.button = (ToggleButton) convertView.findViewById(R.id.toggleButton1);
		convertView.setTag(holder);
		convertView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.dialog_route_brown_background));
		convertView.setVisibility(View.VISIBLE);

		String cmd = list.get(position);
		holder.text_name.setText(cmd);
		int iconId = mIcon.get(position);
		switch (iconId) {
		case NO_ICON:
			holder.text_name.setTextSize(20);
			holder.text_name.setTextColor(Color.LTGRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.GONE);
			break;
		case SETTINGS_VIEW_VEHICLE:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setOnCheckedChangeListener(vHandler);
			holder.button.setChecked(Session.viewVehicles);
			break;
		case SETTINGS_VIEW_ALL_SA:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setOnCheckedChangeListener(saHandler);
			holder.button.setChecked(Session.viewAllSAs);
			break;
		case FOREMAN_DAILY_LOGS:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setChecked(false);
			holder.button.setOnCheckedChangeListener(foremanLogHandler);
			break;
		case DISABLE_AUTO_POPUP:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setChecked(Session.getDisableOnRouteAutoPopup());
			holder.button.setOnCheckedChangeListener(this.disableOnRouteAutoPopupHandler);
			break;
		case VIEW_DFAULT_SEASON:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setOnCheckedChangeListener(seasonHandler);
			holder.button.setChecked(Session.isViewDefautSeason());
			break;
		case STAFF_LIST:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.GONE);
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setChecked(Session.isStaffListEnabled());
			holder.button.setOnCheckedChangeListener(staffListHandler);
			break;
		default:
			holder.text_name.setTextSize(15);
			holder.text_name.setTextColor(Color.GRAY);
			holder.icon.setVisibility(View.VISIBLE);
			holder.icon.setImageDrawable(mContext.getResources().getDrawable(mIcon.get(position)));
			holder.icon.setBackgroundResource(R.drawable.login_presses_button);
			holder.button.setVisibility(View.GONE);
		}

		return convertView;
	}

	private static class ViewHolder {
		ImageView icon;
		TextView text_name;
		ToggleButton button;
	}

	private static class ViewVehiclesHandler implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Session.viewVehicles = isChecked;
		}
	}

	private static class ViewAllSAHandler implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (Session.viewAllSAs != isChecked) {
				Session.viewAllSAs = isChecked;
				if (Session.viewAllSAs) {
					PointOfInterestManager.getInstance().attachAllOthersServiceActivities();
				} else {
					PointOfInterestManager.getInstance().detachAllOthersServiceActivities();
				}
			}
		}
	}

	private static class ForemanLogHandler implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			HelpHandler.dismissHelpHandler();
			ContractListCustomDialog contractlisthandler = new ContractListCustomDialog(mContext);
			contractlisthandler.createDialog();
		}
	}

	private static class DisableOnRouteAutoPopup implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Session.changeDisableOnRouteAutoPopup();
		}
	}

	private static class SeasonHandler implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (!Session.isInit()) {
				Session.init(mContext, PreferenceManager.getDefaultSharedPreferences(mContext));
			}
			if (buttonView.isPressed())
				Session.setViewDefautSeason(isChecked);

		}
	}

	private static class StaffListHandler implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Session.changeDisableStaffList();
		}
	}

}