package com.operasoft.snowboard.dbsync.periodic;

import net.osmand.plus.activities.MapActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.util.Config;
import com.operasoft.snowboard.util.Session;

public class UserPeriodicSync extends DefaultPeriodicSync {

	private UsersDao userDao;

	public UserPeriodicSync() {
		super("User", new UsersDao());
		userDao = (UsersDao) dao;
	}

	@Override
	protected void processServerResponse(JSONArray jsArray) throws Exception {
		for (int i = 0; i < jsArray.length(); i++) {
			JSONObject jsonObject = jsArray.getJSONObject(i);
			JSONArray recordKey = jsonObject.names();
			for (int j = 0; j < recordKey.length(); j++) {
				String jsonModel = recordKey.getString(j);
				JSONObject json = jsonObject.getJSONObject(jsonModel);

				User user = userDao.buildDto(json);
				userDao.insertOrReplace(user);

				if (Session.getDriver().getId().equals(user.getId())) {
					if (user.getWorkStatus().equals(User.STATUS_INACTIVE))
						if (MapActivity.mapView != null) {
							SharedPreferences mSP = PreferenceManager.getDefaultSharedPreferences(MapActivity.mapView.getContext());
							String userPin = mSP.getString(Config.USER_PIN_KEY, "");
							if (!userPin.equals("")) {
								Session.logoutFromServer = true;
								Session.MapAct.doLogout();
							}

						}
				}
			}
		}
	}

}
