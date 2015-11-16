package com.operasoft.snowboard.dbsync.periodic;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.util.Log;

import com.operasoft.snowboard.database.CompanyDao;
import com.operasoft.snowboard.database.Dao;
import com.operasoft.snowboard.database.User;
import com.operasoft.snowboard.dbsync.NetworkUtilities;
import com.operasoft.snowboard.dbsync.Utils;
import com.operasoft.snowboard.engine.PointOfInterestManager;
import com.operasoft.snowboard.util.Session;

public abstract class AbstractPeriodicSync {
	protected PointOfInterestManager poiMgr;
	protected BasicNameValuePair imeiParam;
	protected BasicNameValuePair pinParam;
	protected BasicNameValuePair actionParam;
	protected BasicNameValuePair modelParam;
	protected BasicNameValuePair companyParam;
	protected BasicNameValuePair limitParam;
	protected String pinUsed = null;
	protected Dao dao;

	/**
	 * The URL to invoke to perform this synchronisation
	 */
	protected String url = NetworkUtilities.AUTH_URI;
	protected String model;
	protected String snowmanModel;
	protected boolean companySpecific = true;
	protected String companyId;
	protected boolean superUserOnly = false;
	protected boolean syncChildren = false;

	protected AbstractPeriodicSync(String model, Dao dao, boolean companySpecific) {
		this.model = model;
		this.dao = dao;
		if (model.equalsIgnoreCase("RouteSelection") || model.equalsIgnoreCase("WorkOrder")) {
			this.snowmanModel = model;
		} else {
			this.snowmanModel = "Sb" + model;
		}
		this.companySpecific = companySpecific;
		CompanyDao cDao = new CompanyDao();
		companyId = cDao.getCompanyId();

		poiMgr = PointOfInterestManager.getInstance();
		imeiParam = new BasicNameValuePair(NetworkUtilities.PARAM_IMEI, Utils.selectIMEINum());
		String pin = Session.getUserPin();
		if ((pin != null) && (!pin.equals(""))) {
			pinParam = new BasicNameValuePair(NetworkUtilities.PARAM_PIN, pin);
		}
		actionParam = new BasicNameValuePair(NetworkUtilities.PARAM_ACTION, "index");
		modelParam = new BasicNameValuePair(NetworkUtilities.PARAM_MODEL, snowmanModel);
		limitParam = new BasicNameValuePair(NetworkUtilities.PARAM_LIMIT, String.valueOf(NetworkUtilities.SYNC_LIMIT));

		if (companySpecific) {
			if (snowmanModel.equals("SbCompany"))
				companyParam = new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[conditions][" + snowmanModel + ".id" + "]", companyId);
			else
				companyParam = new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[conditions][" + snowmanModel + ".company_id" + "]", companyId);
		}
	}

	public String getModel() {
		return model;
	}

	public boolean isSuperUserOnly() {
		return superUserOnly;
	}

	public void setSuperUserOnly(boolean superUserOnly) {
		this.superUserOnly = superUserOnly;
	}

	public boolean isSyncChildren() {
		return syncChildren;
	}

	public void setSyncChildren(boolean syncChildren) {
		this.syncChildren = syncChildren;
	}

	/**
	 * This method implements the common algorithm to use for synchronizing data from the master database at a periodic interval The basic
	 * idea is quite simple: - Retrieve all the data that belongs to the current company that has been updated since the last periodic
	 * synchronization. - Override the local database with the data received. - Prune any entity that is no longer active.
	 * 
	 * In order to be efficient, we will retrieve entries in blocks of 100.
	 * @param context 
	 */
	public boolean fetchData(Context context) {
		try {
			if (superUserOnly) {
				// This synchronization is only required if the driver has admin rights
				User user = Session.getDriver();
				if ( (user == null) || (!user.isForeman() && !user.isSuperDriver()) ) {
					return true;
				}
			}
			
			String lastUpdate = dao.getLastModified();
			Log.i("PeriodicSync - " + model, "Last update = " + lastUpdate);

			// Make sure we always use the current driver's PIN...
			String userPin = Session.getUserPin();
			if ((pinUsed == null) || ((userPin != null) && (!pinUsed.equals(userPin)))) {
				if ((userPin == null) || (userPin.equals(""))) {
					userPin = Session.getLastUserPin();
				}
				if (userPin != null) {
					pinParam = new BasicNameValuePair(NetworkUtilities.PARAM_PIN, userPin);
					pinUsed = userPin;
				}
			}

			if (companySpecific && (pinParam == null)) {
				Log.e("Periodic Sync - " + model, "Failed to retrieve user PIN");
				return false;
			}

			// Ask the concrete class to provide the list of parameters needed
			// for this query
			ArrayList<NameValuePair> params = buildRequestParams();

			int offset = 0;
			BasicNameValuePair offsetParam = new BasicNameValuePair(NetworkUtilities.PARAM_OFFSET, String.valueOf(offset));

			// Retrieve only fields that have been modified since our previous update.
			if ((lastUpdate != null) && !lastUpdate.equals("")) {
				if (url == NetworkUtilities.AUTH_URI_SYNC) {
					params.add(new BasicNameValuePair("conditions[" + snowmanModel + ".modified >" + "]", lastUpdate));
				} else {
					params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[conditions][" + snowmanModel + ".modified >" + "]", lastUpdate));
				}
			}
			
			if (syncChildren) {
				// We also need to retrieve the associated entities
				params.add(new BasicNameValuePair(NetworkUtilities.PARAM_OPTION + "[recursive]", "1"));
			}

			// NOTE: There is no need to specify the list of fields we are interested in since we have created
			// "sb_" database views restricted with the fields that must be sent to Snowboard.

			boolean done = false;
			while (!done) {

				// Query Snowman server to retrieve the data
				String servResponse = NetworkUtilities.getCurlResponse(url, params);
				Log.d("PeriodicSync - " + model, "Response received: " + servResponse);

				if (!servResponse.equals("null") && !servResponse.equals("[]")) {
					// Parse the JSON data received
					JSONArray jsArray = new JSONArray(servResponse);

					// Hand it over to our subclass for processing...
					processServerResponse(jsArray);

					if (jsArray.length() >= NetworkUtilities.SYNC_LIMIT) {
						// Their is more data to retrieve on Snowman for this update, let's move the offset for the next request
						params.remove(offsetParam);
						offset += NetworkUtilities.SYNC_LIMIT;
						offsetParam = new BasicNameValuePair(NetworkUtilities.PARAM_OFFSET, String.valueOf(offset));
						params.add(offsetParam);
					} else {
						// Nothins else to process
						done = true;
					}
				} else {
					// Nothing else to process
					done = true;
				}
			}

			// We are done with this periodic update, let's remember the time at which it was done...
			//Utils.lastupdate(mLastSync, model);

		} catch (Exception e) {
			Log.e("PeriodicSync - " + model, "fetchData failure - Exception caught: " + e.getMessage(), e);
			return false;
		}

		return true;
	}

	/**
	 * This method is intended to be overriden by subclasses to return the list of parameters to pass to the server in order to achieve the
	 * periodic update.
	 */
	abstract protected ArrayList<NameValuePair> buildRequestParams();

	/**
	 * This method is intended to be overriden by subclasses to process specific changes related to the data received.
	 * 
	 * As an example, if a service activity has been removed or assigned to someone else, this is where the subclass needs to detect it and
	 * handle it properly (e.g. detach it from the POI list.
	 * 
	 * @param jsArray
	 *            The list of JSON objects retrieved from Snowman
	 */
	abstract protected void processServerResponse(JSONArray jsArray) throws Exception;
}
