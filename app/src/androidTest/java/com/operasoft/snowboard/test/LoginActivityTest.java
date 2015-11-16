package com.operasoft.snowboard.test;

import com.operasoft.snowboard.R;
import com.operasoft.snowboard.Sw_LoginScreenActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<Sw_LoginScreenActivity> {

	private Sw_LoginScreenActivity activity;
	
	private ListView truckList;
	
	public LoginActivityTest() {
		super(Sw_LoginScreenActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// turns off touch mode in the device or emulator. 
		// If any of your test methods send key events to the application, you must turn off touch mode before you start any activities; otherwise, the call is ignored. 
		setActivityInitialTouchMode(false);

		activity = getActivity();
		truckList = (ListView) activity.findViewById(R.id.lv_sml_truck_names);

	}

	/**
	 * Makes sure the activity is initialized properly
	 */
	public void testPreConditions() {
		assertTrue(truckList.getCount() == 0);
	}

}
