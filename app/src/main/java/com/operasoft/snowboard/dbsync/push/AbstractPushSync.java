package com.operasoft.snowboard.dbsync.push;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.dbsync.DbSyncManager;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.util.Session;

/**
 * This class defines the common logic used for pushing updates from our
 * local database up to the Snowman database
 * @author Christian
 *
 */
public abstract class AbstractPushSync<T> {
	protected BasicNameValuePair imeiParam;
	protected BasicNameValuePair pinParam;
	protected BasicNameValuePair actionParam;
	protected BasicNameValuePair modelParam;
	protected String pinUsed = null;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	/**
	 * The URL to invoke to perform this synchronisation
	 */
	protected String url = NetworkUtilities.AUTH_URI;
	protected String model;
	private   Dao<T> dao;

	protected AbstractPushSync(String model) {
		this.model = model;
		this.dao = getDao();		
		imeiParam = new BasicNameValuePair(NetworkUtilities.PARAM_IMEI, Utils.selectIMEINum());
		String pin = Session.getUserPin();
		if ( (pin != null) && (!pin.equals("")) ) {
			pinParam = new BasicNameValuePair(NetworkUtilities.PARAM_PIN, pin);
		}
		actionParam = new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "add");
		modelParam = new BasicNameValuePair(NetworkUtilities.PARAM_MODEL, model);
	}
	
	public String getModel() {
		return model;
	}

	/**
	 * This method attemps to push a DTO modifications to the server immediately. If it fails
	 * or if we are not online, the DTO is marked as dirty in the database so that it will be
	 * picked up on the next periodic sync interval.
	 * 
	 * @param dto
	 * @return true if the server has been notified, false if we will retry later
	 */
	public boolean pushData(Context c, T dto) {
		if (Utils.isOnline(c)) {
			// We are online, let's try to push the update to the server now
			if (pushToServer(dto)) {
				saveClearDto(dto);
				return true;
			} else {
				// We could not push it to the server, mark it as dirty for later processing.
				saveDirtyDto(dto);
			}
		} else {
			// We are offline, let's mark this DTO as dirty to try later
			saveDirtyDto(dto);
		}
		
		return false;
	}

	
	/**
	 * This method implements the common algorithm to use for synchronizing data from the local database at a periodic interval.
	 * The basic idea is quite simple: 
	 * - Find all the rowViews that have been marked as "dirty" and send them over to the server
	 * - Retrieve all the data that belongs to the current company that has been updated since the last periodic
	 * synchronization. - Override the local database with the data received. - Prune any entity that is no longer active.
	 * 
	 * In order to be efficient, we will retrieve entries in blocks of 100.
	 */
	public boolean syncData() {
		try {
			// Ask the concrete class to provide the list of parameters needed
			// for this query
			
			// Ask the concrete class to list all DTOs to send to server
			if (dao == null) {
				Log.e("PushSync - " + model, "DAO is NULL");
				return false;
			}
			
			// Make sure we always use the current driver's PIN...
			String userPin = Session.getUserPin();
			if ( (pinUsed == null) || ( (userPin != null) && (!pinUsed.equals(userPin)) ) ) {
				if ( (userPin == null) || (userPin.equals("")) ) {
					userPin = Session.getLastUserPin();
				}
				if (userPin != null) {
					pinParam = new BasicNameValuePair(NetworkUtilities.PARAM_PIN, userPin);
					pinUsed = userPin;
				}
			}
			
			if (pinParam == null) {
				Log.e("PushSync - " + model, "Failed to retrieve user PIN");
				return false;
			}
			
			List<T> dtoList = dao.listDirtyDtos();
			Log.d("PushSync - " + model, dtoList.size() + " DTOs to push");
			for (T dto: dtoList) {
				if (pushToServer(dto)) {
					clearDirtyDto(dto);
				} else {
					updateDirtyDto(dto);
				}
			}
			return true;
		} catch (Exception e) {
			Log.e("PushSync - " + model, "syncData failure - Exception caught: " + e.getMessage(), e);
		}
		
		return false;
	}

	/**
	 * This method attempts to invoke the API on snowman to update a DTO
	 * @param dto The DTO to update
	 * @return true if the API was properly called, false otherwise
	 */
	protected boolean pushToServer(T dto) {
		// For each DTO, build the appropriate request parameters
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(); 
		// Default queries require the following parameters:
		//	- imei:   To identify the tablet
		//  - pin:    To identify the user
		//  - model:  The data we want to query
		//  - action: The action to invoke (always "add" for push updates)
		params.add(imeiParam);
		params.add(pinParam);
		params.add(modelParam);
		params.add(actionParam);
		
		addUpdateDtoParams(params, dto);

		String value = "";
		try {
			
			if (model.equals("Worksheet")
					&& (params.get(params.size() - 1).getName().equals("Worksheet[daily_photos_url]"))){
				value = NetworkUtilities.getCurlResponseForImage(url, params);
			}else{
				value = NetworkUtilities.getCurlResponse(url, params);
			}
			Log.d(this.getClass().getName(), "Response received: " + value);

			if (processServerResponse(value, dto)) {
				return true;
			}
			else {
				// The request failed.
				Log.e(this.getClass().getName(), "Request failed: " + value);
			}
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Request failed: " + value, e);
		}
		
		return false;
	}

	/**
	 * This method saves a DTO that was successfully pushed to the server on its first attempt. 
	 * If a subclass needs to perform something fancy, it may override this method to do it.
	 */
	protected void saveClearDto(T dto) {
		dao.insertOrReplace(dto);
	}

	/**
	 * This method saves a DTO that cannot be pushed to the server at this point in time. 
	 * If a subclass needs to perform something fancy, it may override this method to do it.
	 */
	protected void saveDirtyDto(T dto) {
		dao.markAsDirty(dto);
	}

	/**
	 * This method clears a dirty DTO field in our database after it has been processed by the server. 
	 * If a subclass needs to perform something fancy, it may override this method to do it.
	 */
	protected void clearDirtyDto(T dto) {
		dao.clearDirtyDto(dto);
	}

	private void updateDirtyDto(T dto) {
		dao.updateDirtyDto(dto);		
	}
	
	protected boolean isEmptyJsonResponse(String value) {
		if (value.equals("[]") || value.equals("null") || value.equals("") || value.equals("Success") || value.equals("\"Success\"")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Alert the user that something went wrong with the sync process
	 */
	protected void alertUser(String message) {
		DbSyncManager.getInstance().recordPushSyncFailure(model, message, true);
	}
	
	/**
	 * Subclasses must return the DAO we need to use to handle their DTOs
	 */
	abstract protected Dao<T> getDao();
	
	/**
	 * This method is intended to be overriden by subclasses to add the list of DTO-specific 
	 * parameters before sending the request.
	 */
	abstract protected void addUpdateDtoParams(List<NameValuePair> params, T dto);

	/**
	 * This method is intended to be overriden by subclasses to perform custom processing on the
	 * DTO (e.g. set its ID) based on the response received from the server
	 * 
	 * @return true if the call to the server was successful, false otherwise
	 * @throws JSONException 
	 */
	abstract protected boolean processServerResponse(String value, T dto) throws JSONException;
}
