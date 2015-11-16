package com.operasoft.snowboard.view.worksheet.row;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.operasoft.snowboard.database.Dto;
import com.operasoft.snowboard.view.SpinnerTextView;

/**
 * @author dounaka
 *
 * @param <T>
 */
public abstract class DtoAdapter<T extends Dto> extends BaseAdapter {
	final ArrayList<T> dtos = new ArrayList<T>();

	private final Context mContext;

	public abstract String getTitle(T dto);

	public DtoAdapter(Context ctx, List<T> dtoList) {
		super();
		dtos.addAll(dtoList);
		mContext = ctx;
	}

	@Override
	public int getCount() {
		return dtos.size();
	}

	@Override
	public Object getItem(int position) {
		return dtos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateDisplay(SpinnerTextView view, T dto) {

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SpinnerTextView txt = null;
		if (convertView != null)
			txt = (SpinnerTextView) convertView;
		else
			txt = new SpinnerTextView(mContext);
		T dto = dtos.get(position);
		txt.setText(getTitle(dto));
		updateDisplay(txt, dto);
		txt.setTag(dto);
		return txt;
	}
}
