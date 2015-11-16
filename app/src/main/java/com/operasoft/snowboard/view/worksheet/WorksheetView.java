package com.operasoft.snowboard.view.worksheet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author dounaka
 * -1 -3 -4 -6 
 * @param <T>
 */
public abstract class WorksheetView<T> extends FrameLayout {

	protected T dto;

	public abstract int getViewResourceId();

	public abstract void bindControls(Context ctx);

	protected abstract void display(T dto);

	protected abstract T update(T dto);

	public WorksheetView(Context ctx) {
		super(ctx);
		initView(ctx);
	}

	public WorksheetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public WorksheetView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
		initView(context);
	}

	private void initView(final Context ctx) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(getViewResourceId(), this, true);
		bindControls(ctx);
	}

	public final void show(T dto) {
		this.dto = dto;
		display(dto);
	}

	public T getDto() {
		if (dto != null)
			update(dto);
		return dto;
	}

	class DateInputClickListener implements OnClickListener {
		int year, month, day;
		TextView textView;
		DatePickerDialog dialog = null;
		final Calendar c = Calendar.getInstance();

		DateInputClickListener() {
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		}

		@Override
		public void onClick(final View v) {
			if (dialog != null)
				return;
			textView = (TextView) v;
			DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					c.set(Calendar.YEAR, year);
					c.set(Calendar.MONTH, monthOfYear);
					c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					textView.setText(dateFormat.format(c.getTime()));
					textView.setTag(c.getTime());
					dialog = null;
				}
			};
			dialog = new DatePickerDialog(getContext(), mDateSetListener, year, month, day);
			dialog.show();
		}
	};

	protected DateInputClickListener dateInputClickListener = new DateInputClickListener();

	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected Date today = new Date();

	protected void setDate(TextView txtDate, String dat) {
		if (dat == null || dat.trim().length() == 0)
			txtDate.setText(dateFormat.format(today));
		else
			txtDate.setText(dat);
	}

	protected void setMinutes(Spinner spinnerMinutes, float hoursmn) {
		float mn = hoursmn - ((int) hoursmn);
		spinnerMinutes.setSelection(0);
		if (mn > 0)
			if (mn < 0.26f)
				spinnerMinutes.setSelection(1);
			else if (mn < 0.51f)
				spinnerMinutes.setSelection(2);
			else if (mn < 0.76f)
				spinnerMinutes.setSelection(3);
	}

	protected float getMinutes(Spinner spinnerMinutes) {
		final int i = spinnerMinutes.getSelectedItemPosition();
		if (i == 1)
			return 0.25f;
		if (i == 2)
			return 0.50f;
		if (i == 3)
			return 0.75f;
		else
			return 0f;
	}

	class SelectTextListener implements OnFocusChangeListener, OnTouchListener {
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if (hasFocus) {
				EditText et = (EditText) view;
				et.selectAll();
			}
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			EditText et = (EditText) view;
			et.selectAll();
			return false;
		}
	}
	
	protected SelectTextListener selectTextListener = new SelectTextListener();
}