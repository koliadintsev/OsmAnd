package com.operasoft.snowboard.util;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.operasoft.android.util.Utils;
import com.operasoft.snowboard.database.LoginSession;
import com.operasoft.snowboard.database.LoginSessionDao;
import com.operasoft.snowboard.dbsync.NetworkUtilities;

public class VehicleLoginController {

	public LoginSession listVehicles(Context context, String vehicleId, String pin) {

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		Utils cU = new Utils(context);

		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_IMEI, cU.getIMEI()));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_PIN, pin));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_MODEL, "LoginSession"));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "index"));

		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[contain]", "['Vehicle','User']"));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[" + NetworkUtilities.PARAM_LIMIT + "]", "1"));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[conditions][LoginSession.vehicle_id]", vehicleId));
		params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[order]", "LoginSession.start_datetime DESC, LoginSession.end_datetime DESC"));


		try {
			String value = NetworkUtilities.getCurlResponse(NetworkUtilities.AUTH_URI, params);

			JSONArray jsArray = new JSONArray(value);
			LoginSessionDao dao = new LoginSessionDao();

			try {
				JSONObject jsonObject = jsArray.getJSONObject(0).getJSONObject("LoginSession");
				LoginSession dto = dao.buildDto(jsonObject);
				if (dto != null) {
					return dto;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
