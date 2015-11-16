package com.operasoft.snowboard.dbsync.push;

import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.operasoft.android.db.sync.DbSyncFailureException;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.database.UserWorkStatusLogs;
import com.operasoft.snowboard.database.UserWorkStatusLogsDao;
import com.operasoft.snowboard.database.UsersDao;
import com.operasoft.snowboard.dbsync.JsonDtoParser;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.util.Session;

public class PunchPushSync extends AbstractPushSync<UserWorkStatusLogs> {

	private JsonDtoParser dtoParser = new JsonDtoParser();
	private UserWorkStatusLogsDao dao = null;

	// Singleton pattern
	static private PunchPushSync instance_s = new PunchPushSync();

	static public PunchPushSync getInstance() {
		if (instance_s == null) {
			instance_s = new PunchPushSync();
		}

		return instance_s;
	}

	private PunchPushSync() {
		super("UserWorkStatusLog");
	}

	@Override
	protected Dao<UserWorkStatusLogs> getDao() {
		if (dao == null) {
			dao = new UserWorkStatusLogsDao();
		}
		return dao;
	}

	@Override
	protected void addUpdateDtoParams(List<NameValuePair> params, UserWorkStatusLogs dto) {

		if (dto.getWorkStatus().equals(User.STATUS_ON_SITE)) {
			params.remove(actionParam);
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, UserWorkStatusLogs.DROP_OFF));

		} else if (dto.getWorkStatus().equals(User.STATUS_INACTIVE)) {
			params.remove(actionParam);
			params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, UserWorkStatusLogs.PUNCH_OUT));

		} else if (dto.getWorkStatus().equals(User.STATUS_IN_VEHICLE)) {

			params.remove(actionParam);
			UsersDao userDao = new UsersDao();
			User user = userDao.getById(dto.getUserId());
			if ((user != null) && user.getWorkStatus().equals(User.STATUS_ON_SITE))
				params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, UserWorkStatusLogs.PICK_UP));
			else
				params.add(new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, UserWorkStatusLogs.PUNCH_IN));
		}

		params.add(new BasicNameValuePair(model + "[company_id]", dto.getCompanyId()));
		params.add(new BasicNameValuePair(model + "[date_time]", dto.getDateTime()));
		params.add(new BasicNameValuePair(model + "[user_id]", dto.getUserId()));
		params.add(new BasicNameValuePair(model + "[vehicle_id]", dto.getVehicleId()));
		params.add(new BasicNameValuePair(model + "[service_location_id]", dto.getServiceLocationId()));
		params.add(new BasicNameValuePair(model + "[latitude]", String.valueOf(dto.getLatitude())));
		params.add(new BasicNameValuePair(model + "[longitude]", String.valueOf(dto.getLongitude())));
		params.add(new BasicNameValuePair(model + "[operation]", dto.getOperation()));
		params.add(new BasicNameValuePair(model + "[work_status]", dto.getWorkStatus()));
		params.add(new BasicNameValuePair(model + "[imei]", dto.getImei()));

		if (Session.getDriver() != null)
			params.add(new BasicNameValuePair(model + "[creator]", Session.getDriver().getId()));

	}

	@Override
	protected boolean processServerResponse(String response, UserWorkStatusLogs dto) throws JSONException {

		if (isEmptyJsonResponse(response)) {
			return processEmptyResponse(dto);
		}

		try {
			// Check the response status received from the server
			JSONObject jsonObject = new JSONObject(response);
			if (!jsonObject.isNull("type") && !jsonObject.isNull("message")) {
				int status = jsonObject.getInt("type");
				String message = jsonObject.getString("message");
				switch (status) {
				case 200: // OK
					if (!jsonObject.isNull("data")) {
						JSONArray jsArray = jsonObject.getJSONArray("data");
						return processDataReceived(dto, jsArray);
					}
					// No data to process
					return processEmptyResponse(dto);

				case 401:
					return processEmptyResponse(dto);

				case 400: // Bad Request
				case 403: // Forbidden
				case 500: // Internal Server Error
				case 501: // Not Implemented
					processServerError(dto, status, message);
					return false;

				default:
					Log.e("PushSync - " + model, "Unknown status in processServerResponse: " + status + ": " + message);
					return processSyncError(dto, response, "Exception in  processServerResponse ", null);

				}
			} else {
				Log.e("PushSync - " + model, "Incomplete response received from server: " + response);
				return processSyncError(dto, response, "Incomplete response received from server", null);
			}

		} catch (JSONException jse) {
			// Try to parse the old response format (version 0)
			Log.i("PushSync - " + model, "V0 response received: " + response);
			try {
				JSONArray jsArray = new JSONArray(response);
				return processDataReceived(dto, jsArray);
			} catch (Exception e) {
				Log.e("PushSync - " + model, "Exception in  processServerResponse " + e);
				return processSyncError(dto, response, "Exception in  processServerResponse ", e);
			}
		} catch (DbSyncFailureException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected boolean processEmptyResponse(UserWorkStatusLogs dto) {
		if (dto.isNew()) {
			// Fake a created date...
			dto.setCreated(dateFormat.format(new Date()));
		} else if (dto.isDirty()) {
			// The DTO is already in our DB, we need to replace its ID
			dto.setNewId(dto.getId());
		}
		updateUser(dto);
		return true;

	}

	protected boolean processDataReceived(UserWorkStatusLogs dto, JSONArray jsArray) throws DbSyncFailureException, JSONException {

		if (jsArray.length() < 1) {
			Log.w("UserWorkStatusLogsPushSync Push", "No details received from server: " + jsArray.toString());
			if (dto.isNew()) {
				// Fake a created date...
				dto.setCreated(dateFormat.format(new Date()));
			}
			updateUser(dto);
			return true;
		}

		// Let's make sure the DTO have been properly created on the server...
		// Parse the JSON data received
		if (jsArray.length() > 0) {
			JSONObject jsonObject = jsArray.getJSONObject(0);
			UserWorkStatusLogs snowmanDto = dtoParser.parseSnowmanUserWorkStatusLog(jsonObject);
			if (dto.isDirty()) {
				// The DTO is already in our DB, we need to replace its ID
				dto.setNewId(snowmanDto.getId());
			} else {
				// The DTO has not been inserted in our DB
				dto.setId(snowmanDto.getId());
			}
			updateUser(dto);
			return true;
		}

		Log.e("UserWorkStatusLogsPushSync", "No UserWorkStatusLogs object found in response: " + jsArray.toString());
		return false;

	}

	/**
	 * Manually update the user value in database
	 * 
	 * @param dto
	 */
	private void updateUser(UserWorkStatusLogs dto) {
		UsersDao usersDao = new UsersDao();
		User user = usersDao.getById(dto.getUserId());
		if (user == null)
			return;

		user.setWorkStatus(dto.getWorkStatus());
		user.setWorkStatusDate(dto.getDateTime());
		user.setCurrentVehicleId(dto.getVehicleId());
		user.setCurrentServiceLocationId(dto.getServiceLocationId());

		usersDao.replace(user);
	}

	protected boolean processSyncError(UserWorkStatusLogs dto, String response, String reason, Exception e) {
		// TODO Auto-generated method stub
		return false;
	}

	protected boolean processServerError(UserWorkStatusLogs dto, int status, String message) throws DbSyncFailureException {
		// TODO Auto-generated method stub
		return false;
	}

	protected void handleNewId(String oldId, String newId) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveDirtyDto(UserWorkStatusLogs dto) {
		super.saveDirtyDto(dto);
		// This method will be called in case user doing punch/pick/drop operation in off line mode
		updateUser(dto);
	}

}
