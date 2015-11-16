package com.operasoft.snowboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.database.User;

public class UserView extends AppView {

	private TextView mTxtName, mTxtStatus, mTxtDateTime, mTxtVehicle, mTxtLocation;

	public UserView(Context context) {
		super(context);
	}

	public UserView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserView(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
	}

	@Override
	protected int getViewResId() {
		return R.layout.staff_list_row_view;
	}

	@Override
	protected void bindControls(Context ctx) {
		mTxtName = (TextView) findViewById(R.id.txtname);
		mTxtStatus = (TextView) findViewById(R.id.txtstatus);
		mTxtDateTime = (TextView) findViewById(R.id.txtdatetime);
		mTxtVehicle = (TextView) findViewById(R.id.txtvehicle);
		mTxtLocation = (TextView) findViewById(R.id.txtlocation);
	}

	public void show(final User user) {
		mTxtName.setText(user.getFirstName() + " " + user.getLastName());
		mTxtStatus.setText(user.workStatusLabel);
		mTxtDateTime.setText(user.getWorkStatusDate());

		if (user.isInVehicle() && (user.vehicle != null)) { 
			mTxtVehicle.setText(user.vehicle.getName());
		} else {
			mTxtVehicle.setText(null);
		}

		if (user.isOnSite() && (user.serviceLocation != null)) {
			mTxtLocation.setText(user.serviceLocation.getName());
		} else {
			mTxtLocation.setText(null);
		}
	}
}
