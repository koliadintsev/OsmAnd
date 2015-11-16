package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.util.Log;

import com.operasoft.snowboard.database.Activity;
import com.operasoft.snowboard.database.ActivityDao;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;

public class ActivityPushSync extends AbstractPushSync<Activity> {

	private ActivityDao dao = null;

	// Singleton pattern
	static private ActivityPushSync instance_s = new ActivityPushSync();

	static public ActivityPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new ActivityPushSync();
		}

		return instance_s;
	}

	private ActivityPushSync() {
		super("Activity");
	}

	@Override
	protected Dao<Activity> getDao() {
		if (dao == null) {
			dao = new ActivityDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, Activity dto) {

		// ServiceActivityDao activityDao = new ServiceActivityDao();
		// ServiceActivity serviceActivity = activityDao.getById(dto.getServiceActivityId());
		//
		// List<Activity> activities = serviceActivity.listRequestedServices();
		//
		// int counter = 0;
		// for (Activity activity : activities) {
		// params.add(new BasicNameValuePair(model + "[" + counter + "][id]", activity.getId()));
		// params.add(new BasicNameValuePair(model + "[" + counter + "][quantity]",
		// String.valueOf(activity.getQuantity())));
		// params.add(new BasicNameValuePair(model + "[" + counter + "][service_activity_id]",
		// activity.getServiceActivityId()));
		// counter++;
		// }

		if (dto.getId() != "" && dto.getId() != null) {
			params.add(new BasicNameValuePair(model + "[id]", dto.getId()));

		} else {

			ServiceActivityDao saDao = new ServiceActivityDao();
			ServiceActivity serviceActivity = saDao.getById(dto.getServiceActivityId());
			String routeGroupId = saDao.getRouteGroupId(serviceActivity.getServiceLocationId());

			params.add(new BasicNameValuePair(model + "[route_group_id]", routeGroupId));
			params.add(new BasicNameValuePair(model + "[contract_service_id]", dto.getContractServiceId()));

		}

		params.add(new BasicNameValuePair(model + "[quantity]", String.valueOf(dto.getQuantity())));
		params.add(new BasicNameValuePair(model + "[service_activity_id]", dto.getServiceActivityId()));
	}

	@Override
	protected boolean processServerResponse(String value, Activity dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("ActivityPushSync Push", "No details received from server: " + value);
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}
			return true;
		}
		return false;
	}

}
