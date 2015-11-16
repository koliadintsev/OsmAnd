package com.operasoft.snowboard.view.worksheet.row;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.operasoft.snowboard.view.SpinnerTextView;

public class MinutesAdapter extends BaseAdapter {

	private final Context mContext;

	private static final String[] minutesRange = new String[] {

	"0", "15", "30", "45"

	};

	public MinutesAdapter(Context ctx) {
		super();
		mContext = ctx;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object getItem(int position) {
		return minutesRange[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SpinnerTextView txt = null;
		if (convertView == null)
			txt = new SpinnerTextView(mContext);
		else
			txt = (SpinnerTextView) convertView;
		txt.setText(minutesRange[position]);
		return txt;
	}
}
