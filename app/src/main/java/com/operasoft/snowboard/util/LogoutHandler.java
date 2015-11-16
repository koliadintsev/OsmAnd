package com.operasoft.snowboard.util;

import net.osmand.plus.OsmandSettings;
import net.osmand.plus.activities.MapActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.operasoft.android.gps.services.GPSService;
import com.operasoft.snowboard.Sw_LoginScreenActivity;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.events.PunchOutEvent;

/**
 * @deprecated
 * @author Enabke
 *
 */
public class LogoutHandler {

	private Context mContext;
	private MapActivity mapActivity;

	public LogoutHandler(Context context, Activity mapActivity) {
		this.mContext = context;
		this.mapActivity = (MapActivity) mapActivity;
	}

	public OnClickListener logout = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Reset the current driver/vehicle information in the preferences
			SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(mContext);
			SharedPreferences.Editor prefEditor = mSP.edit();
			prefEditor.putString(Config.USER_PIN_KEY, "");
			prefEditor.putString(Config.VEHICLE_ID_KEY, "");
			prefEditor.commit();

			// Punch out.
			String userId = "";
			if (Session.getDriver() != null) {
				userId = Session.getDriver().getId();
			}
			PunchOutEvent event = new PunchOutEvent(mContext, userId);
			event.doPunchOut(true);
			PointOfInterestManager.getInstance().clear();

			OsmandSettings.setLogoutMap(mContext, "logout_user");
			mContext.stopService(new Intent(mContext, GPSService.class));

			Intent intent = new Intent(mContext, Sw_LoginScreenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(intent);

			mapActivity.finish();

			// Cleanup the session
			Session.close();
			
		}
	};
}