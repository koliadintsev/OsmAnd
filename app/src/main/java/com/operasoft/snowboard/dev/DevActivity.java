package com.operasoft.snowboard.dev;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.operasoft.snowboard.database.Callout;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.engine.PointOfInterest;
import com.operasoft.snowboard.engine.PointOfInterestActionListener;
import com.operasoft.snowboard.util.DamageCustomDialogHandler;
import com.operasoft.snowboard.util.SelectCalloutTypeCustomDialog;

/**
 * @author dounaka
 *
 */
public class DevActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView txt = new TextView(this);
		txt.setText("*** Development Activity ***");
		setContentView(txt);

	}

	@Override
	protected void onResume() {
		super.onResume();
		testServiceLocations();
	}

	private void testServiceLocations() {
		ServiceLocationDao sDao = new ServiceLocationDao();

		Log.d("sldao", "findall-----------------------------------------");
		List<ServiceLocation> serviceLocations = sDao.findByRoute("5033f09b-e93c-40d8-a5bf-7d59ae8ed672");
		Log.d("sldao", "last7-----------------------------------------");
		List<ServiceLocation> serviceLocation2s = sDao.findByRoute("5033f09b-e93c-40d8-a5bf-7d59ae8ed672", 7);

	}

	PointOfInterestActionListener poiActionListener = new PointOfInterestActionListener() {

		@Override
		public void serviceLocationToserviceActivityEnroute(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void serviceLocationCompleted(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void serviceActivityRefused(PointOfInterest poi, ServiceActivity sa) {
			// TODO Auto-generated method stub

		}

		@Override
		public void serviceActivityInDirection(PointOfInterest poi, ServiceActivity sa) {
			// TODO Auto-generated method stub

		}

		@Override
		public void serviceActivityCreated(PointOfInterest poi, ServiceActivity sa) {
			// TODO Auto-generated method stub

		}

		@Override
		public void serviceActivityCompleted(PointOfInterest poi, ServiceActivity sa) {
			// TODO Auto-generated method stub

		}

		@Override
		public void serviceActivityAccepted(PointOfInterest poi, ServiceActivity sa) {
			// TODO Auto-generated method stub

		}

		@Override
		public void markerInstalled(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void incidentCreated(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void goToTriggered(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void goBackTriggered(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void goBackCancelTriggered(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}

		@Override
		public void calloutCreated(PointOfInterest poi, Callout callout) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ForemanDaily(PointOfInterest poi) {
			// TODO Auto-generated method stub

		}
	};

	public void showCalloutDialog() {
		SelectCalloutTypeCustomDialog selectCallOut = new SelectCalloutTypeCustomDialog(this, getDevPoi(), poiActionListener);
		selectCallOut.createDialog();
	}

	private PointOfInterest getDevPoi() {
		PointOfInterest poi = new PointOfInterest("poi-developement");
		return poi;
	}

	public void showDamageDialog() {
		DamageCustomDialogHandler damageHandler = new DamageCustomDialogHandler(this, getDevPoi());
		damageHandler.createDialog();
	}

}
