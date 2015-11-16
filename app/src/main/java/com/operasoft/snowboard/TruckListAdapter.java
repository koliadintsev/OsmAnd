package com.operasoft.snowboard;

import java.util.List;

import com.operasoft.snowboard.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TruckListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> list;
	private int mSelPos;

	public TruckListAdapter(Context context, List<String> list, int selPos) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.mSelPos = selPos;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
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

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sw_main_login_truck_list, null);

			holder = new ViewHolder();
			holder.truck_name = (TextView) convertView.findViewById(R.id.tv_truck_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position != mSelPos) {
			holder.truck_name.setTextColor(Color.parseColor("#e6a715"));
			holder.truck_name.setBackgroundColor(Color.BLACK);
		} else {
			holder.truck_name.setTextColor(Color.BLACK);
			holder.truck_name.setBackgroundColor(Color.parseColor("#e6a715"));
		}
		String cmd = list.get(position);
		holder.truck_name.setText(cmd);

		return convertView;
	}

	private static class ViewHolder {
		TextView truck_name;
	}
}