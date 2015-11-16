package com.operasoft.snowboard.util;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.util.Log;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.database.Punch;
import com.operasoft.snowboard.database.PunchDao;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class UserPunchInController {

	/**
	 * Get last Punch dto of user from SM d
	 * 
	 * @param userId
	 * @param context
	 * @return punch dto
	 * @throws Exception
	 */
	public Punch canPunch(String userId, Context context) {

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		Utils cU = new Utils(context);

		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_IMEI, cU.getIMEI()));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_PIN, Session.getUserPin()));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_MODEL, "Punch"));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "index"));

		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[conditions][Punch.user_id]", userId));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[" + NetworkUtilities.PARAM_LIMIT + "]", "1"));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[order]", "Punch.created DESC"));

		String value = "";
		try {

			value = NetworkUtilities.getCurlResponse(NetworkUtilities.AUTH_URI, params);

			JSONArray jsArray = new JSONArray(value);
			if (jsArray.length() > 0) {
				PunchDao punchDao = new PunchDao();
				Punch punch = punchDao.buildDto(jsArray.getJSONObject(0).getJSONObject("Punch"));
				if (!punch.getOperation().equals("In"))
					return punch;

				throw new IllegalArgumentException("Already punched");
			}

		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Request failed: " + value, e);
		}

		throw new IllegalArgumentException("Already punched");
	}
}
