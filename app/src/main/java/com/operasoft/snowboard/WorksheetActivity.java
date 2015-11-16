package com.operasoft.snowboard;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.operasoft.snowboard.fragment.WorksheetFragment;

/**
 * @author dounaka
 * Service Location Worksheet for a service location
 */
public class WorksheetActivity extends FragmentActivity {
	private WorksheetFragment mWorksheetFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_worksheet);
		// save in DB
	}

	@Override
	protected void onResume() {
		super.onResume();

		mWorksheetFragment = (WorksheetFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentworksheet);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mWorksheetFragment.onBackPressed();
	}

	public void closeworksheet(View v) {
		this.finish();

	}

}
