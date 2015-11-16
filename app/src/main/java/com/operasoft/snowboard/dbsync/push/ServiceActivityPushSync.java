package com.operasoft.snowboard.dbsync.push;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.snowboard.database.Activity;
import com.operasoft.snowboard.database.ActivityDao;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.ServiceActivity;
import com.operasoft.snowboard.database.ServiceActivityDao;
import com.operasoft.snowboard.database.ServiceActivityDetailsDao;
import com.operasoft.snowboard.database.ServiceActivityLog;
import com.operasoft.snowboard.database.ServiceActivityLogDao;
import com.operasoft.snowboard.database.ServiceLocation;
import com.operasoft.snowboard.database.ServiceLocationDao;
import com.operasoft.snowboard.dbsync.CommonUtils;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.util.Session;

public class ServiceActivityPushSync extends AbstractPushSync<ServiceActivity> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private ServiceActivityDao saDao = null;
	private ServiceActivityLogDao logDao = null;
	private ActivityDao activityDao = null;
	private ServiceActivityDetailsDao saDFDao = null;
	private ServiceLocationDao slDao = null;

	// Singleton pattern
	static private ServiceActivityPushSync instance_s = new ServiceActivityPushSync();

	static public ServiceActivityPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new ServiceActivityPushSync();
		}

		return instance_s;
	}

	private ServiceActivityPushSync() {
		super("ServiceActivity");
		getDao();
	}

	@Override
	protected Dao<ServiceActivity> getDao() {
		if (saDao == null) {
			saDao = new ServiceActivityDao();
			logDao = new ServiceActivityLogDao();
			activityDao = new ActivityDao();
			saDFDao = new ServiceActivityDetailsDao();
			slDao = new ServiceLocationDao();
		}
		return saDao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, ServiceActivity dto) {
		if (dto.isNew()) {

			if (dto.getStatus().equals(ServiceActivity.SA_COMPLETED)) {
				params.remove(actionParam);
				params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "completedSA"));
			}

			// String contactId = new ServiceLocationDao().getContactId(dto.getServiceLocationId());
			String contactId = slDao.findServiceLocationByContractId(dto.getContractId()).getContactId();

			params.add(new BasicNameValuePair(model + "[contact_id]", contactId));
			params.add(new BasicNameValuePair(model + "[contract_id]", dto.getContractId()));
			params.add(new BasicNameValuePair(model + "[status_code_id]", dto.getStatus()));
			params.add(new BasicNameValuePair(model + "[date_time]", dto.getDateTime()));
			params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
			params.add(new BasicNameValuePair(model + "[job_notes]", dto.getJobNotes()));
			params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
			params.add(new BasicNameValuePair(model + "[enroute_time]", dto.getEnrouteTime()));
			params.add(new BasicNameValuePair(model + "[enroute_latitude]", dto.getEnrouteLatitude()));
			params.add(new BasicNameValuePair(model + "[enroute_longitude]", dto.getEnrouteLongitude()));
			params.add(new BasicNameValuePair(model + "[arrived_time]", dto.getArrivedTime()));
			params.add(new BasicNameValuePair(model + "[time_on_site]", dto.getTimeOnSite()));
			params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
			params.add(new BasicNameValuePair(model + "[sequence_number]", "-1"));

			// Specify the data to put in the Activity table
			List<Activity> activities = dto.listRequestedServices();

			int i = 0;

			for (Activity activity : activities) {
				params.add(new BasicNameValuePair("Activity[" + i + "][contract_service_id]", activity.getContractServiceId()));
				params.add(new BasicNameValuePair("Activity[" + i + "][quantity]", String.valueOf(activity.getQuantity())));
				params.add(new BasicNameValuePair("Activity[" + i + "][time]", activity.getTime()));
				i++;
			}

			// Pass on the data to put in the ServiceActivityLog table List<ServiceActivityLog> logs
			// = dto.listLogs();
			ServiceActivityLog log = null;
			if ((dto.listLogs() != null) && (dto.listLogs().isEmpty())) {
				log = dto.listLogs().get(0);
			}
			i = 0;
			List<String> saCode = new ArrayList<String>();
			saCode.add(ServiceActivity.SA_CREATED);
			saCode.add(ServiceActivity.SA_ASSIGNED);
			saCode.add(ServiceActivity.SA_ACCEPTED);
			saCode.add(ServiceActivity.SA_IN_DIRECTION);

			if (dto.getStatus() != ServiceActivity.SA_IN_DIRECTION)
			saCode.add(ServiceActivity.SA_COMPLETED);
			
			for (String code : saCode) {
				params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][contract_id]", dto.getContractId()));
				params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][status_code_id]", code));
				params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][company_id]", dto.getCompanyId()));
				if (log != null) {
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][date_time]", log.getDateTime()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][gps_coordinates]", log.getLatLong()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][vehicle_id]", log.getVehicleId()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][user_id]", log.getUserId()));
				} else {
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][date_time]", CommonUtils.UtcDateNow()));
					if (Session.clocation != null) {
						String latLong = Session.clocation.getLatitude() + " " + Session.clocation.getLongitude();
						params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][gps_coordinates]", latLong));
					}
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][vehicle_id]", dto.getVehicleId()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][user_id]", dto.getUserId()));
				}
				i++;
			}

		} else {
			params.remove(actionParam);
			if (dto.getStatus().equals(ServiceActivity.SA_COMPLETED)) {
				params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "completedSA"));
			} else {
				params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "edit"));
			}

			// We are updating the status of an existing SA
			params.add(new BasicNameValuePair(model + "[id]", dto.getId()));
			ServiceLocation sl = slDao.findServiceLocationByContractId(dto.getContractId()); 
			if (sl != null) {
				params.add(new BasicNameValuePair(model + "[contact_id]", sl.getContactId()));
			} else {
				Log.w("SA Push", "No SL found for SA " + dto.getId() + " - Contract ID: " + dto.getContractId());
			}
			params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
			params.add(new BasicNameValuePair(model + "[contract_id]", dto.getContractId()));
			params.add(new BasicNameValuePair(model + "[status_code_id]", dto.getStatus()));
			params.add(new BasicNameValuePair(model + "[date_time]", dto.getDateTime()));
			params.add(new BasicNameValuePair(model + "[client_notes]", dto.getClientNotes()));
			params.add(new BasicNameValuePair(model + "[job_notes]", dto.getJobNotes()));
			params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
			params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
			params.add(new BasicNameValuePair(model + "[season_id]", dto.getSeasonId()));
			params.add(new BasicNameValuePair(model + "[sequence_number]", String.valueOf(dto.getSequenceNumber())));

			// Specify the data to put in the Activity table
			List<Activity> activities = dto.listRequestedServices();

			// here i is used as counter to create array of activities and SaLogs.
			int i = 0;
			for (Activity activity : activities) {

				if (activity.getQuantity() != 0.0f) {

					if (activity.getId() != "" && activity.getId() != null)
						params.add(new BasicNameValuePair("Activity[" + i + "][id]", activity.getId()));

					params.add(new BasicNameValuePair("Activity[" + i + "][contract_service_id]", activity.getContractServiceId()));
					params.add(new BasicNameValuePair("Activity[" + i + "][quantity]", String.valueOf(activity.getQuantity())));
					i++;
				}
			}

			// Pass on the data to put in the ServiceActivityLog table
			List<ServiceActivityLog> logs = dto.listLogs();

			// Reseting i to 0.
			i = 0;
			for (ServiceActivityLog saLog : logs) {
				if ((saLog.getVehicleId() != null) && (dto.getStatus().equals(saLog.getStatus()))) {
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][contract_id]", dto.getContractId()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][status_code_id]", saLog.getStatus()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][company_id]", dto.getCompanyId()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][vehicle_id]", saLog.getVehicleId()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][user_id]", saLog.getUserId()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][date_time]", saLog.getDateTime()));
					params.add(new BasicNameValuePair("ServiceActivityLog[" + i + "][gps_coordinates]", saLog.getLatLong()));
					i++;
				}
			}
		}
	}

	@Override
	protected boolean processServerResponse(String value, ServiceActivity dto) throws JSONException {
		if (isEmptyJsonResponse(value)) {
			Log.w("SA Push", "No details received from server: " + value);
			if (dto.isNew()) {
				// Fake a created date...
				// dto.setCreated(dateFormat.format(new Date()));
			}

			return true;
		}

		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		JSONArray jsArray = new JSONArray(value);
		if (jsArray.length() > 0) {
			JSONObject jsonObject = jsArray.getJSONObject(0);
			ServiceActivity snowmanDto = dtoParser.parseServiceActivity(jsonObject, "ServiceActivity");
			if (dto.isNew() && dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
				dto.setCreated(snowmanDto.getCreated());
				activityDao.replaceServiceActivityId(dto.getId(), dto.getNewId());
				logDao.replaceServiceActivityId(dto.getId(), dto.getNewId());
				saDFDao.replaceServiceActivityId(dto.getId(), dto.getNewId());
				saDao.replaceId(dto);
			} else if (dto.isNew()) {
				// The DTO has not been inserted in our DB yet
				dto.setId(snowmanDto.getId());
				dto.setCreated(snowmanDto.getCreated());
			}

			return true;
		}

		Log.e("SA Push", "No object found in response: " + value);
		return false;
	}

	@Override
	protected void saveDirtyDto(ServiceActivity dto) {
		saDao.markAsDirty(dto);
		List<Activity> activities = dto.listRequestedServices();
		for (Activity activity : activities) {
			activity.setServiceActivityId(dto.getId());
			activityDao.markAsDirty(activity);
		}
		List<ServiceActivityLog> logs = dto.listLogs();
		for (ServiceActivityLog saLog : logs) {
			if (saLog.isNew()) {
				saLog.setServiceActivityId(dto.getId());
				logDao.markAsDirty(saLog);
			}
		}
	}

	@Override
	protected void clearDirtyDto(ServiceActivity dto) {
		saDao.clearDirtyDto(dto);
		List<Activity> activities = dto.listRequestedServices();
		for (Activity activity : activities) {
			activityDao.clearDirtyDto(activity);
		}
		List<ServiceActivityLog> logs = dto.listLogs();
		for (ServiceActivityLog saLog : logs) {
			if (saLog.isNew()) {
				saLog.setServiceActivityId(dto.getId());
				logDao.clearDirtyDto(saLog);
			}
		}
	}

	@Override
	protected void saveClearDto(ServiceActivity dto) {
		saDao.insertOrReplace(dto);
		List<Activity> activities = dto.listRequestedServices();
		for (Activity activity : activities) {
			activity.setServiceActivityId(dto.getId());
			activityDao.insertOrReplace(activity);
		}
		List<ServiceActivityLog> logs = dto.listLogs();
		for (ServiceActivityLog saLog : logs) {
			if (saLog.isNew()) {
				// Set the created date to make sure they are not sent again
				saLog.setCreated(CommonUtils.UtcDateNow());
				saLog.setServiceActivityId(dto.getId());
				logDao.insertOrReplace(saLog);
			}
		}
	}
}
