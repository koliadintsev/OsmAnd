package com.operasoft.snowboard.view.worksheet.row;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.WorksheetTravelTime;

/**
 * @author dounaka
 *
 */
public class TravelTimeRowView extends RowPageView<WorksheetTravelTime> {

	private DtoSpinner<User> mSpinnerEmployee;
	private ImageView imgdelete;
	private EditText mEditHours;
	private TextView mTxtDate;

	public TravelTimeRowView(Context context) {
		super(context);
	}

	@Override
	public int getViewResourceId() {
		return R.layout.worksheet_traveltime_row;
	}

	public void initUsers(List<User> employees) {
		mSpinnerEmployee.setDtoAdapter(new UserSpinnerDto(getContext(), employees));
	}

	@Override
	public void bindRowControls(Context ctx) {
		mSpinnerEmployee = (DtoSpinner<User>) findViewById(R.id.spinneremployee);
		mTxtDate = (TextView) findViewById(R.id.editdate);
		mTxtDate.setOnClickListener(this.dateInputClickListener);
		mEditHours = (EditText) findViewById(R.id.edithours);
		imgdelete = (ImageView) findViewById(R.id.imgdelete);
		mEditHours.setOnFocusChangeListener(selectTextListener);
		mEditHours.setOnTouchListener(selectTextListener);

	}

	@Override
	protected void display(WorksheetTravelTime travel) {
		mSpinnerEmployee.setValueById(travel.getUserId());
		setDate(mTxtDate, travel.getTravelDate());
		mEditHours.setText("" + travel.getHours());
		mEditHours.requestFocus(FOCUS_UP);
	}

	@Override
	protected WorksheetTravelTime update(WorksheetTravelTime travel) {
		travel.setTravelDate(mTxtDate.getText().toString());
		travel.setUserId(mSpinnerEmployee.getValueId());
		String nbHours = mEditHours.getText().toString();
		travel.setHours(Integer.parseInt(nbHours));
		return travel;
	}

	@Override
	protected View getDeleteRowView() {
		return imgdelete;
	}

}
